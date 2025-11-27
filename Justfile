# eTMA Handler - Master Justfile
# The "Trillion Recipe" Cookbook for marking tool development
#
# Usage:
#   just              # List all recipes
#   just setup        # Initial project setup
#   just start        # Start development server
#   just build        # Build for production
#
# Requirements:
#   - Elixir 1.14+
#   - Erlang/OTP 25+
#   - Node.js (for assets)
#   - Podman (for containers)

set shell := ["bash", "-c"]

# Default recipe: show available commands
default:
    @just --list --unsorted

# ===========================================
# DEVELOPMENT RECIPES
# ===========================================

# Initial project setup (run once after clone)
setup:
    @echo "Setting up eTMA Handler..."
    mix local.hex --force
    mix local.rebar --force
    mix deps.get
    mix assets.setup
    mix assets.build
    @echo "Setup complete! Run 'just start' to begin."

# Start development server with live reload
start:
    @echo "Starting eTMA Handler on http://localhost:4000"
    mix phx.server

# Start in interactive mode (iex)
iex:
    iex -S mix phx.server

# Run tests
test:
    mix test

# Run tests with coverage
test-coverage:
    mix test --cover

# Format code
format:
    mix format

# Check formatting without changing files
format-check:
    mix format --check-formatted

# Run static analysis
lint:
    mix credo --strict

# Run all quality checks
quality: format-check lint test
    @echo "All quality checks passed!"

# ===========================================
# BUILD RECIPES
# ===========================================

# Build production release
build:
    @echo "Building production release..."
    MIX_ENV=prod mix assets.deploy
    MIX_ENV=prod mix release
    @echo "Release built in _build/prod/rel/etma_handler"

# Build cross-platform binaries using Burrito
build-binaries:
    @echo "Building cross-platform binaries (Burrito)..."
    MIX_ENV=prod mix release
    @echo "Binaries ready in burrito_out/"

# Build for specific target
build-linux:
    @echo "Building for Linux x86_64..."
    MIX_ENV=prod mix release --target linux
    @echo "Binary: burrito_out/etma_handler_linux"

build-windows:
    @echo "Building for Windows x86_64..."
    MIX_ENV=prod mix release --target windows
    @echo "Binary: burrito_out/etma_handler_windows.exe"

build-macos:
    @echo "Building for macOS (ARM64)..."
    MIX_ENV=prod mix release --target macos
    @echo "Binary: burrito_out/etma_handler_macos"

build-riscv:
    @echo "Building for RISC-V (experimental)..."
    MIX_ENV=prod mix release --target riscv
    @echo "Binary: burrito_out/etma_handler_riscv"

# ===========================================
# CONTAINER RECIPES
# ===========================================

# Build Wolfi-based OCI container
build-container:
    @echo "Building Wolfi container..."
    podman build -t etma-handler:latest .
    @echo "Container built: etma-handler:latest"

# Run container locally
run-container:
    @echo "Running container on http://localhost:4000"
    podman run -p 4000:4000 --rm -it etma-handler:latest

# Push container to registry
push-container registry="ghcr.io/yourusername":
    podman tag etma-handler:latest {{registry}}/etma-handler:latest
    podman push {{registry}}/etma-handler:latest

# ===========================================
# DATABASE RECIPES
# ===========================================

# Reset the local CubDB database
db-reset:
    @echo "Resetting CubDB database..."
    rm -rf priv/data/etma.cub
    @echo "Database reset. Data will be recreated on next start."

# Backup database
db-backup:
    @echo "Backing up database..."
    mkdir -p backups
    cp -r priv/data backups/data_$(date +%Y%m%d_%H%M%S)
    @echo "Backup complete."

# ===========================================
# SECURITY RECIPES
# ===========================================

# Audit dependencies for vulnerabilities
audit:
    mix hex.audit
    @echo "Security audit complete."

# Generate new secret key
gen-secret:
    mix phx.gen.secret

# Rotate signing keys (for production)
rotate-keys:
    @echo "Key rotation not implemented yet."
    @echo "This would regenerate signing keys and update config."

# ===========================================
# INSTALLER RECIPES
# ===========================================

# Generate Linux installer script
gen-installer:
    @echo "Generating Linux installer..."
    cp scripts/install.sh dist/install.sh
    chmod +x dist/install.sh
    @echo "Installer: dist/install.sh"

# Generate .deb package
package-deb:
    @echo "Generating Debian package..."
    @echo "Not implemented yet. Would use fpm or nfpm."

# Generate .rpm package
package-rpm:
    @echo "Generating RPM package..."
    @echo "Not implemented yet. Would use fpm or nfpm."

# ===========================================
# DOCUMENTATION RECIPES
# ===========================================

# Generate documentation
docs:
    mix docs

# Open documentation in browser
docs-open:
    mix docs && open doc/index.html

# ===========================================
# CLEANUP RECIPES
# ===========================================

# Clean build artifacts
clean:
    mix clean
    rm -rf _build deps node_modules

# Deep clean (including database)
clean-all: clean db-reset
    rm -rf priv/static/assets

# ===========================================
# DEVELOPMENT HELPERS
# ===========================================

# Open Elixir shell with project loaded
shell:
    iex -S mix

# Watch for changes and run tests
test-watch:
    mix test.watch

# Check for outdated dependencies
deps-outdated:
    mix hex.outdated

# Update all dependencies
deps-update:
    mix deps.update --all

# Generate a new LiveView
gen-live context name:
    mix phx.gen.live {{context}} {{name}}

# ===========================================
# IMPORT MODULAR RECIPES
# ===========================================

# Include additional recipe files if they exist
# import? "recipes/security.just"
# import? "recipes/deploy.just"
