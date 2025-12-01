#!/usr/bin/env bash
# SPDX-FileCopyrightText: 2024 eTMA Handler Contributors
# SPDX-License-Identifier: MIT
#
# eTMA Handler - Setup Script
# ===========================
#
# One-liner install:
#   curl -fsSL https://raw.githubusercontent.com/Hyperpolymath/tma-mark2/main/setup.sh | bash
#
# Or with wget:
#   wget -qO- https://raw.githubusercontent.com/Hyperpolymath/tma-mark2/main/setup.sh | bash
#
# This script:
#   1. Detects your OS
#   2. Installs Podman (if needed)
#   3. Installs Just (if needed)
#   4. Pulls the eTMA Handler container
#   5. Shows you how to run it
#

set -euo pipefail

# Colors (if terminal supports them)
if [[ -t 1 ]]; then
    RED='\033[0;31m'
    GREEN='\033[0;32m'
    YELLOW='\033[0;33m'
    BLUE='\033[0;34m'
    BOLD='\033[1m'
    NC='\033[0m' # No Color
else
    RED=''
    GREEN=''
    YELLOW=''
    BLUE=''
    BOLD=''
    NC=''
fi

# Logging functions
info() { echo -e "${BLUE}ℹ${NC} $1"; }
success() { echo -e "${GREEN}✓${NC} $1"; }
warn() { echo -e "${YELLOW}⚠${NC} $1"; }
error() { echo -e "${RED}✗${NC} $1"; exit 1; }

# Header
echo ""
echo -e "${BOLD}╔═══════════════════════════════════════════════════════════╗${NC}"
echo -e "${BOLD}║              eTMA Handler - Setup Script                  ║${NC}"
echo -e "${BOLD}║            Open University Marking Tool                   ║${NC}"
echo -e "${BOLD}╚═══════════════════════════════════════════════════════════╝${NC}"
echo ""

# Detect OS
detect_os() {
    if [[ "$OSTYPE" == "linux-gnu"* ]]; then
        if [ -f /etc/fedora-release ]; then
            echo "fedora"
        elif [ -f /etc/debian_version ]; then
            echo "debian"
        elif [ -f /etc/arch-release ]; then
            echo "arch"
        elif [ -f /etc/alpine-release ]; then
            echo "alpine"
        elif [ -f /etc/redhat-release ]; then
            echo "rhel"
        else
            echo "linux"
        fi
    elif [[ "$OSTYPE" == "darwin"* ]]; then
        echo "macos"
    elif [[ "$OSTYPE" == "msys" ]] || [[ "$OSTYPE" == "cygwin" ]]; then
        echo "windows"
    else
        echo "unknown"
    fi
}

OS=$(detect_os)
info "Detected OS: $OS"

# Check if running as root (bad on macOS, sometimes needed on Linux)
if [[ "$EUID" -eq 0 ]] && [[ "$OS" == "macos" ]]; then
    error "Don't run this script as root on macOS"
fi

# Install Podman
install_podman() {
    if command -v podman &> /dev/null; then
        success "Podman already installed: $(podman --version)"
        return 0
    fi

    info "Installing Podman..."

    case $OS in
        fedora)
            sudo dnf install -y podman
            ;;
        debian)
            sudo apt-get update
            sudo apt-get install -y podman
            ;;
        arch)
            sudo pacman -Sy --noconfirm podman
            ;;
        alpine)
            sudo apk add podman
            ;;
        rhel)
            sudo dnf install -y podman
            ;;
        macos)
            if ! command -v brew &> /dev/null; then
                warn "Homebrew not found. Installing Homebrew first..."
                /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
            fi
            brew install podman
            info "Initializing Podman machine for macOS..."
            podman machine init 2>/dev/null || true
            podman machine start 2>/dev/null || true
            ;;
        *)
            error "Don't know how to install Podman on $OS. Please install manually."
            ;;
    esac

    success "Podman installed: $(podman --version)"
}

# Install Just
install_just() {
    if command -v just &> /dev/null; then
        success "Just already installed: $(just --version)"
        return 0
    fi

    info "Installing Just..."

    case $OS in
        fedora)
            sudo dnf install -y just
            ;;
        debian)
            # Just might not be in default repos, use cargo or prebuilt
            if command -v cargo &> /dev/null; then
                cargo install just
            else
                # Download prebuilt binary
                curl --proto '=https' --tlsv1.2 -sSf https://just.systems/install.sh | bash -s -- --to /usr/local/bin
            fi
            ;;
        arch)
            sudo pacman -Sy --noconfirm just
            ;;
        alpine)
            sudo apk add just
            ;;
        macos)
            brew install just
            ;;
        *)
            # Fallback: use the official installer
            info "Using Just installer script..."
            curl --proto '=https' --tlsv1.2 -sSf https://just.systems/install.sh | bash -s -- --to ~/.local/bin
            export PATH="$HOME/.local/bin:$PATH"
            ;;
    esac

    success "Just installed: $(just --version)"
}

# Pull the container
pull_container() {
    info "Pulling eTMA Handler container..."

    CONTAINER_IMAGE="ghcr.io/hyperpolymath/etma-handler:latest"

    if podman pull "$CONTAINER_IMAGE"; then
        success "Container pulled successfully"
    else
        warn "Could not pull container (might not be published yet)"
        info "You can build it locally after cloning the repository"
    fi
}

# Create data directory
create_data_dir() {
    DATA_DIR="$HOME/.local/share/etma_handler"
    mkdir -p "$DATA_DIR"
    success "Data directory: $DATA_DIR"
}

# Clone repo (optional)
clone_repo() {
    if [[ -d "tma-mark2" ]]; then
        info "Repository already exists"
        return 0
    fi

    read -p "Clone the repository? (y/N) " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        git clone https://github.com/Hyperpolymath/tma-mark2.git
        success "Repository cloned"
    fi
}

# Main
main() {
    install_podman
    install_just
    create_data_dir
    pull_container

    echo ""
    echo -e "${GREEN}${BOLD}═══════════════════════════════════════════════════════════${NC}"
    echo -e "${GREEN}${BOLD}                    Setup Complete!                        ${NC}"
    echo -e "${GREEN}${BOLD}═══════════════════════════════════════════════════════════${NC}"
    echo ""
    echo "  To run eTMA Handler:"
    echo ""
    echo "    Option 1 - Direct with Podman:"
    echo "      podman run -p 4000:4000 -v ~/.local/share/etma_handler:/data ghcr.io/hyperpolymath/etma-handler"
    echo ""
    echo "    Option 2 - Clone and use Just:"
    echo "      git clone https://github.com/Hyperpolymath/tma-mark2.git"
    echo "      cd tma-mark2"
    echo "      just do-it"
    echo ""
    echo "  Then open: http://localhost:4000"
    echo ""
}

main "$@"
