# SPDX-License-Identifier: PMPL-1.0-or-later
# Justfile - Task Orchestration for tma-mark2
#
# All local tasks orchestrated via Just
# All deployment transitions managed via Must
#
# Usage: just --list

set shell := ["bash", "-euo", "pipefail", "-c"]
set dotenv-load := true

# Project metadata
import? "contractile.just"

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
    @echo "License: PMPL-1.0-or-later + Palimpsest"
    @echo ""
    @echo "Build System: Guix (primary), Nix (fallback)"
    @echo "Container: Wolfi (Chainguard)"
    @echo "Security: Post-quantum crypto, SDP, VPN support"

# ============================================================
# DIAGNOSTICS & ONBOARDING
# ============================================================

# Diagnose environment problems — checks every tool this project needs
doctor:
    #!/usr/bin/env bash
    set -uo pipefail
    echo "=== tma-mark2 doctor ==="
    echo ""
    ok=0; warn=0; fail=0
    check() {
        if command -v "$1" &>/dev/null; then
            printf "  [OK]   %-18s %s\n" "$1" "$($1 --version 2>&1 | head -1)"
            ((ok++))
        elif [ "${2:-required}" = "optional" ]; then
            printf "  [SKIP] %-18s not installed (optional)\n" "$1"
            ((warn++))
        else
            printf "  [FAIL] %-18s not installed\n" "$1"
            ((fail++))
        fi
    }
    echo "-- Core toolchain --"
    check elixir
    check erl
    check mix
    check rustc
    check cargo
    echo ""
    echo "-- Build / task --"
    check just
    check git
    check nix optional
    check guix optional
    check nickel optional
    echo ""
    echo "-- Container --"
    check podman optional
    check nerdctl optional
    echo ""
    echo "-- Security --"
    check panic-attack optional
    check trivy optional
    check cosign optional
    echo ""
    echo "-- Quality --"
    check mix  # credo/format via mix
    echo ""
    echo "-- Files --"
    for f in mix.exs Justfile 0-AI-MANIFEST.a2ml LICENSE SECURITY.md; do
        if [ -f "$f" ]; then
            printf "  [OK]   %s exists\n" "$f"
            ((ok++))
        else
            printf "  [FAIL] %s missing\n" "$f"
            ((fail++))
        fi
    done
    echo ""
    echo "-- Ports --"
    if ss -tlnp 2>/dev/null | grep -q ':4000 '; then
        printf "  [WARN] Port 4000 already in use\n"
        ((warn++))
    else
        printf "  [OK]   Port 4000 available\n"
        ((ok++))
    fi
    echo ""
    echo "=== Results: $ok OK, $warn warnings, $fail failures ==="
    if [ "$fail" -gt 0 ]; then
        echo "Run 'just heal' to attempt automatic fixes."
        exit 1
    fi

# Attempt automatic repair of common problems
heal:
    #!/usr/bin/env bash
    set -uo pipefail
    echo "=== tma-mark2 heal ==="
    echo ""
    # Hex / Rebar
    if ! mix local.hex --check 2>/dev/null; then
        echo "[heal] Installing Hex..."
        mix local.hex --force
    fi
    if ! command -v rebar3 &>/dev/null; then
        echo "[heal] Installing Rebar..."
        mix local.rebar --force
    fi
    # Deps
    if [ ! -d deps ] || [ ! -d _build ]; then
        echo "[heal] Fetching dependencies..."
        mix deps.get
    fi
    # CubDB lock
    if ls data/db/*.lock 2>/dev/null; then
        echo "[heal] Removing stale CubDB lock files..."
        rm -f data/db/*.lock
    fi
    # Assets
    if [ -d assets ] && [ ! -d priv/static/assets ]; then
        echo "[heal] Building assets..."
        mix assets.setup 2>/dev/null || true
        mix assets.build 2>/dev/null || true
    fi
    # Rust NIFs
    if [ -d native/tma_crypto ] && [ ! -d native/tma_crypto/target ]; then
        echo "[heal] Building Rust NIFs..."
        cargo build --release --manifest-path native/tma_crypto/Cargo.toml 2>/dev/null || true
    fi
    echo ""
    echo "[heal] Done. Run 'just doctor' to verify."

# Guided tour of the codebase — read this if you are new
tour:
    #!/usr/bin/env bash
    set -uo pipefail
    echo "=== tma-mark2 Codebase Tour ==="
    echo ""
    echo "tma-mark2 (eTMA Handler) is a marking tool for Open University tutors."
    echo "It reads .fhi student submissions, provides a browser UI for grading,"
    echo "and exports .docx feedback files. Runs entirely offline."
    echo ""
    echo "--- Supervision Tree ---"
    echo "  EtmaHandler.Application"
    echo "    +-- EtmaHandler.Repo       (CubDB — embedded database)"
    echo "    +-- EtmaHandler.Scanner    (watches Downloads for .fhi files)"
    echo "    +-- EtmaHandler.Bouncer    (rate limiting)"
    echo "    +-- Cachex                 (in-process cache)"
    echo "    +-- Phoenix.PubSub"
    echo "    +-- EtmaHandlerWeb.Endpoint (Bandit HTTP on port 4000)"
    echo ""
    echo "--- Key Directories ---"
    echo "  lib/etma_handler/           Core business logic"
    echo "    fhi.ex                    FHI parser (student submissions)"
    echo "    marking/                  Grading engine, rubrics"
    echo "    crypto/                   Argon2id hashing via Rust NIF"
    echo "    scanner.ex                Downloads folder watcher"
    echo "    repo.ex                   CubDB data layer"
    echo "  lib/etma_handler_web/       Phoenix LiveView UI"
    echo "  native/tma_crypto/          Rust NIF (cryptography)"
    echo "  config/                     Environment-specific config"
    echo "  test/                       ExUnit tests"
    echo "  bebop/schemas/              Binary serialization schemas"
    echo "  must/                       Nickel invariant definitions"
    echo "  .machine_readable/          AI checkpoint metadata"
    echo ""
    echo "--- Data Flow ---"
    echo "  .fhi file -> Scanner -> FHI Parser -> CubDB -> LiveView UI -> .docx"
    echo ""
    echo "--- Quick Commands ---"
    echo "  just dev          Start Phoenix server (http://localhost:4000)"
    echo "  just test         Run ExUnit + Rust NIF tests"
    echo "  just doctor       Check environment health"
    echo "  just heal         Auto-fix common problems"
    echo "  just help-me      Interactive help menu"
    echo ""
    echo "Read QUICKSTART-USER.adoc for full setup instructions."

# Interactive help menu — pick what you need
help-me:
    #!/usr/bin/env bash
    set -uo pipefail
    echo "=== tma-mark2 Help ==="
    echo ""
    echo "  1) I want to SET UP the project for the first time"
    echo "  2) I want to START the development server"
    echo "  3) I want to RUN TESTS"
    echo "  4) I want to BUILD a release / container"
    echo "  5) I want to DIAGNOSE a problem"
    echo "  6) I want to understand the CODEBASE"
    echo "  7) I want to check SECURITY"
    echo ""
    read -rp "Pick a number (1-7): " choice
    case "$choice" in
        1) echo ""; echo "Run: nix develop  (or install Elixir 1.17+ and Rust manually)"
           echo "Then: mix deps.get && mix assets.setup && mix phx.server"
           echo "See: QUICKSTART-USER.adoc" ;;
        2) echo ""; echo "Run: just dev"
           echo "Opens http://localhost:4000 with live reload." ;;
        3) echo ""; echo "Run: just test           (all tests)"
           echo "      just test-coverage  (with coverage)"
           echo "      just test-integration (integration only)" ;;
        4) echo ""; echo "Run: just build              (Guix > Nix > Mix cascade)"
           echo "      just build-container    (Chainguard image)"
           echo "      just release 2.1.0      (full release cycle)" ;;
        5) echo ""; echo "Run: just doctor   (diagnose)"
           echo "      just heal     (auto-fix)"
           echo "Common issues: CubDB lock, missing Rust, port 4000 in use." ;;
        6) echo ""; echo "Run: just tour     (guided walkthrough)"
           echo "Read: README.adoc, EXPLAINME.adoc, llm-warmup-dev.md" ;;
        7) echo ""; echo "Run: just security-audit  (dep audit + Trivy)"
           echo "      just assail           (panic-attacker scan)"
           echo "      just sbom-generate    (SBOM)" ;;
        *) echo "Invalid choice. Run 'just --list' to see all commands." ;;
    esac

# Run panic-attacker pre-commit scan
assail:
    @command -v panic-attack >/dev/null 2>&1 && panic-attack assail . || echo "panic-attack not found — install from https://github.com/hyperpolymath/panic-attacker"


# Print the current CRG grade (reads from READINESS.md '**Current Grade:** X' line)
crg-grade:
    @grade=$$(grep -oP '(?<=\*\*Current Grade:\*\* )[A-FX]' READINESS.md 2>/dev/null | head -1); \
    [ -z "$$grade" ] && grade="X"; \
    echo "$$grade"

# Generate a shields.io badge markdown for the current CRG grade
# Looks for '**Current Grade:** X' in READINESS.md; falls back to X
crg-badge:
    @grade=$$(grep -oP '(?<=\*\*Current Grade:\*\* )[A-FX]' READINESS.md 2>/dev/null | head -1); \
    [ -z "$$grade" ] && grade="X"; \
    case "$$grade" in \
      A) color="brightgreen" ;; B) color="green" ;; C) color="yellow" ;; \
      D) color="orange" ;; E) color="red" ;; F) color="critical" ;; \
      *) color="lightgrey" ;; esac; \
    echo "[![CRG $$grade](https://img.shields.io/badge/CRG-$$grade-$$color?style=flat-square)](https://github.com/hyperpolymath/standards/tree/main/component-readiness-grades)"
