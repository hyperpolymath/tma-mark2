# SPDX-License-Identifier: AGPL-3.0-or-later
# Justfile - Task Orchestration for tma-mark2
#
# All local tasks orchestrated via Just
# All deployment transitions managed via Must
#
# Usage: just --list

set shell := ["bash", "-euo", "pipefail", "-c"]
set dotenv-load := true

# Project metadata
project := "tma-mark2"
version := "2.0.0"

# Default: show available tasks
default:
    @just --list --unsorted

# ============================================================
# BUILD COMMANDS
# ============================================================

# Build with Guix (primary) or Nix (fallback)
build:
    @echo ">>> Building {{project}} v{{version}}"
    @if command -v guix &> /dev/null && [ -f guix.scm ]; then \
        echo ">>> Using Guix (primary)"; \
        guix build -f guix.scm; \
    elif command -v nix &> /dev/null && [ -f flake.nix ]; then \
        echo ">>> Using Nix (fallback)"; \
        nix build .#default; \
    else \
        echo ">>> Using Mix (emergency)"; \
        MIX_ENV=prod mix deps.get && mix compile && mix release; \
    fi

# Build with Guix explicitly
build-guix:
    @echo ">>> Building with Guix"
    guix build -f guix.scm

# Build with Nix explicitly
build-nix:
    @echo ">>> Building with Nix"
    nix build .#default

# Build for specific platform
build-platform platform="linux-x86_64":
    @echo ">>> Building for {{platform}}"
    MIX_ENV=prod mix release --overwrite

# Build container image
build-container arch="linux/amd64":
    @echo ">>> Building container for {{arch}}"
    nerdctl build --platform {{arch}} \
        -t {{project}}:{{version}} \
        -t {{project}}:latest \
        -f Containerfile.hardened .

# Build multi-arch container
build-container-all:
    @echo ">>> Building multi-arch container"
    nerdctl build \
        --platform linux/amd64,linux/arm64,linux/riscv64 \
        -t ghcr.io/hyperpolymath/{{project}}:{{version}} \
        -t ghcr.io/hyperpolymath/{{project}}:latest \
        -f Containerfile.hardened .

# Build Rust NIFs
build-nifs:
    @echo ">>> Building Rust NIFs"
    @if [ -d native/tma_crypto ]; then \
        cargo build --release --manifest-path native/tma_crypto/Cargo.toml; \
    fi
    @if [ -d native/tma_nlp ]; then \
        cargo build --release --manifest-path native/tma_nlp/Cargo.toml; \
    fi

# Build Dioxus mobile app
build-mobile platform="android":
    @echo ">>> Building Dioxus mobile for {{platform}}"
    cd mobile && dx build --release --platform {{platform}}

# ============================================================
# DEVELOPMENT
# ============================================================

# Start development server
dev:
    iex -S mix phx.server

# Start with file watching
dev-watch:
    watchexec -e ex,exs,res,ncl,scm -- just dev

# Open development shell (Guix or Nix)
shell:
    @if command -v guix &> /dev/null; then \
        guix shell -m guix.scm; \
    else \
        nix develop; \
    fi

# ReScript build
rescript:
    deno task rescript:build

# ReScript watch mode
rescript-watch:
    deno task rescript:watch

# ============================================================
# TESTING
# ============================================================

# Run all tests
test:
    mix test
    @if [ -d native/tma_crypto ]; then \
        cargo test --manifest-path native/tma_crypto/Cargo.toml; \
    fi

# Run tests with coverage
test-coverage:
    mix test --cover

# Run integration tests
test-integration:
    mix test --only integration

# ============================================================
# LINTING & FORMATTING
# ============================================================

# Lint all code
lint:
    mix format --check-formatted
    mix credo --strict
    @if [ -d native/tma_crypto ]; then \
        cargo clippy --manifest-path native/tma_crypto/Cargo.toml -- -D warnings; \
    fi
    nickel typecheck must/must.ncl || true

# Format all code
format:
    mix format
    @if [ -d native/tma_crypto ]; then \
        cargo fmt --manifest-path native/tma_crypto/Cargo.toml; \
    fi

# ============================================================
# MUSTFILE VALIDATION
# ============================================================

# Validate physical state contracts
must-validate:
    @echo ">>> Validating Mustfile contracts"
    nickel export must/must.ncl --format json | jq .

# Strict validation (pre-release)
must-validate-strict:
    @echo ">>> Strict Mustfile validation"
    nickel export must/must.ncl --format json | jq -e '.must.files_present | length > 0'
    nickel export must/must.ncl --format json | jq -e '.must.files_absent | length > 0'

# Check all required files exist
must-check-files:
    @echo ">>> Checking required files"
    @nickel export must/must.ncl --format json | \
        jq -r '.must.files_present[]' | \
        while read f; do \
            if [ -f "$f" ]; then \
                echo "  ✓ $f"; \
            else \
                echo "  ✗ $f (MISSING)" && exit 1; \
            fi; \
        done

# Check banned files don't exist
must-check-banned:
    @echo ">>> Checking banned files absent"
    @nickel export must/must.ncl --format json | \
        jq -r '.must.files_absent[]' | \
        while read f; do \
            if [ -f "$f" ]; then \
                echo "  ✗ $f (SHOULD NOT EXIST)" && exit 1; \
            else \
                echo "  ✓ $f (correctly absent)"; \
            fi; \
        done

# ============================================================
# SECURITY
# ============================================================

# Run security audit
security-audit:
    @echo ">>> Running security audit"
    mix deps.audit
    @if [ -d native/tma_crypto ]; then \
        cargo audit --file native/tma_crypto/Cargo.toml; \
    fi
    trivy fs . --severity HIGH,CRITICAL || true

# Generate SBOM
sbom-generate:
    @echo ">>> Generating SBOM"
    syft . -o spdx-json > sbom.spdx.json
    @echo ">>> SBOM saved to sbom.spdx.json"

# Sign container image
sign-container tag="latest":
    cosign sign ghcr.io/hyperpolymath/{{project}}:{{tag}}

# Verify container signature
verify-container tag="latest":
    cosign verify ghcr.io/hyperpolymath/{{project}}:{{tag}}

# Update ClamAV signatures
clamav-update:
    freshclam --datadir=./clamav-signatures

# ============================================================
# BEBOP PROTOCOL
# ============================================================

# Generate Bebop code from schemas
bebop-generate:
    @echo ">>> Generating Bebop code"
    @if [ -d bebop/schemas ] && ls bebop/schemas/*.bop 1>/dev/null 2>&1; then \
        bebop --lang elixir bebop/schemas/*.bop -o lib/etma_handler/bebop/generated/; \
        bebop --lang rust bebop/schemas/*.bop -o native/tma_sync/src/generated/; \
    else \
        echo ">>> No Bebop schemas found, skipping"; \
    fi

# ============================================================
# CONTAINER OPERATIONS
# ============================================================

# Run in container (development)
container-dev:
    nerdctl run -it --rm \
        -p 4000:4000 \
        -v $(pwd)/data:/data \
        {{project}}:latest

# Run in container (production mode with security)
container-prod:
    nerdctl run -d \
        --name {{project}} \
        --restart unless-stopped \
        -p 127.0.0.1:4000:4000 \
        -v ~/tma-data:/data:rw \
        --security-opt no-new-privileges \
        --read-only \
        --cap-drop ALL \
        --user 65532:65532 \
        ghcr.io/hyperpolymath/{{project}}:latest

# Stop production container
container-stop:
    nerdctl stop {{project}} && nerdctl rm {{project}}

# View container logs
container-logs:
    nerdctl logs -f {{project}}

# ============================================================
# RELEASE
# ============================================================

# Create release
release version:
    @echo ">>> Creating release v{{version}}"
    just must-validate-strict
    just test
    just security-audit
    just sbom-generate
    git tag -s v{{version}} -m "Release {{version}}"
    just build-container-all
    just sign-container {{version}}
    @echo ">>> Release v{{version}} complete"

# Push container to registry
push tag="latest":
    nerdctl push ghcr.io/hyperpolymath/{{project}}:{{tag}}

# ============================================================
# GUIX CHANNEL
# ============================================================

# Update Guix channel branch
guix-channel-update:
    git checkout guix-channel || git checkout -b guix-channel
    git merge main
    guix build -f guix.scm
    git add guix.scm
    git commit -m "Update Guix package definition" || true
    git push origin guix-channel
    git checkout main

# ============================================================
# CLEANUP
# ============================================================

# Clean build artifacts
clean:
    rm -rf _build deps .mix .hex
    rm -rf native/tma_crypto/target
    rm -rf native/tma_nlp/target
    rm -rf mobile/target

# Deep clean (including Nix/Guix stores - use with caution)
clean-deep: clean
    nix store gc || true
    guix gc || true

# ============================================================
# DOCUMENTATION
# ============================================================

# Generate documentation
docs:
    mix docs

# Serve documentation locally
docs-serve:
    mix docs && python3 -m http.server 8000 -d doc

# ============================================================
# DATABASE
# ============================================================

# Open CubDB shell
db-shell:
    iex -S mix run -e "CubDB.start_link(data_dir: \"data/db\")"

# Backup database
db-backup:
    @echo ">>> Backing up database"
    tar -czf backup-$(date +%Y%m%d-%H%M%S).tar.gz data/

# ============================================================
# HELP
# ============================================================

# Show project info
info:
    @echo "Project: {{project}}"
    @echo "Version: {{version}}"
    @echo "License: AGPL-3.0-or-later + Palimpsest"
    @echo ""
    @echo "Build System: Guix (primary), Nix (fallback)"
    @echo "Container: Wolfi (Chainguard)"
    @echo "Security: Post-quantum crypto, SDP, VPN support"
