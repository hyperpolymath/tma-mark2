#!/bin/bash
# eTMA Handler - Linux Desktop Integration Installer
# Registers the .fhi file extension and creates desktop shortcuts
#
# Usage:
#   ./install.sh                 # Install for current user
#   sudo ./install.sh --system   # Install system-wide
#
# Requirements:
#   - xdg-utils (xdg-mime, xdg-desktop-menu)
#   - The etma_handler binary in the same directory or PATH
#
# This script:
#   1. Registers the .fhi MIME type with Linux
#   2. Creates a desktop entry (launcher)
#   3. Associates .fhi files with eTMA Handler

set -euo pipefail

# ===========================================
# Configuration
# ===========================================

APP_NAME="eTMA Handler"
APP_ID="uk.ac.open.etma-handler"
APP_VERSION="2.0.0"
MIME_TYPE="application/x-etma-fhi"

# Detect installation paths
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
INSTALL_DIR="${HOME}/.local/share/etma_handler"
BIN_DIR="${HOME}/.local/bin"
DATA_DIR="${HOME}/.local/share/etma_handler/data"

# System-wide installation paths
if [[ "${1:-}" == "--system" ]]; then
    INSTALL_DIR="/opt/etma_handler"
    BIN_DIR="/usr/local/bin"
    DATA_DIR="/var/lib/etma_handler"
    SYSTEM_INSTALL=true
else
    SYSTEM_INSTALL=false
fi

# ===========================================
# Helper Functions
# ===========================================

log_info() {
    echo -e "\033[0;32m[INFO]\033[0m $1"
}

log_warn() {
    echo -e "\033[0;33m[WARN]\033[0m $1"
}

log_error() {
    echo -e "\033[0;31m[ERROR]\033[0m $1"
}

check_command() {
    if ! command -v "$1" &> /dev/null; then
        log_error "Required command '$1' not found. Please install it first."
        exit 1
    fi
}

# ===========================================
# Pre-flight Checks
# ===========================================

log_info "eTMA Handler Installer v${APP_VERSION}"
log_info "=========================================="

# Check for required tools
check_command "xdg-mime"
check_command "xdg-desktop-menu"

# Check for the binary
if [[ -f "${SCRIPT_DIR}/etma_handler" ]]; then
    APP_EXEC="${SCRIPT_DIR}/etma_handler"
elif command -v etma_handler &> /dev/null; then
    APP_EXEC="$(command -v etma_handler)"
else
    log_warn "etma_handler binary not found."
    log_warn "Please ensure the binary is in the same directory as this script"
    log_warn "or available in your PATH."
    APP_EXEC="etma_handler"  # Assume it will be installed later
fi

# ===========================================
# Installation
# ===========================================

log_info "Installing to: ${INSTALL_DIR}"

# Create directories
mkdir -p "${INSTALL_DIR}"
mkdir -p "${BIN_DIR}"
mkdir -p "${DATA_DIR}"

# Copy binary if found
if [[ -f "${SCRIPT_DIR}/etma_handler" ]]; then
    log_info "Installing binary..."
    cp "${SCRIPT_DIR}/etma_handler" "${INSTALL_DIR}/"
    chmod +x "${INSTALL_DIR}/etma_handler"

    # Create symlink in bin directory
    ln -sf "${INSTALL_DIR}/etma_handler" "${BIN_DIR}/etma_handler"
fi

# Copy icon if present
if [[ -f "${SCRIPT_DIR}/icon.png" ]]; then
    log_info "Installing icon..."
    mkdir -p "${HOME}/.local/share/icons/hicolor/256x256/apps"
    cp "${SCRIPT_DIR}/icon.png" "${HOME}/.local/share/icons/hicolor/256x256/apps/etma-handler.png"
fi

# ===========================================
# MIME Type Registration
# ===========================================

log_info "Registering MIME type..."

# Create MIME type XML definition
MIME_XML=$(mktemp)
cat > "${MIME_XML}" << EOF
<?xml version="1.0" encoding="UTF-8"?>
<mime-info xmlns="http://www.freedesktop.org/standards/shared-mime-info">
  <mime-type type="${MIME_TYPE}">
    <comment>eTMA Marking File</comment>
    <comment xml:lang="en">Open University eTMA Marking File</comment>
    <glob pattern="*.fhi"/>
    <glob pattern="*.FHI"/>
    <icon name="text-xml"/>
    <magic priority="50">
      <match type="string" offset="0" value="&lt;?xml"/>
    </magic>
  </mime-type>
</mime-info>
EOF

# Install MIME type
xdg-mime install "${MIME_XML}"
rm "${MIME_XML}"

# Update MIME database
if [[ "${SYSTEM_INSTALL}" == true ]]; then
    update-mime-database /usr/share/mime 2>/dev/null || true
else
    update-mime-database "${HOME}/.local/share/mime" 2>/dev/null || true
fi

# ===========================================
# Desktop Entry
# ===========================================

log_info "Creating desktop entry..."

DESKTOP_FILE=$(mktemp)
cat > "${DESKTOP_FILE}" << EOF
[Desktop Entry]
Version=1.1
Type=Application
Name=${APP_NAME}
GenericName=Marking Tool
Comment=Open University TMA Marking Handler
Exec=${INSTALL_DIR}/etma_handler %f
Icon=etma-handler
Terminal=false
Categories=Office;Education;
Keywords=marking;tutor;assessment;university;
MimeType=${MIME_TYPE};
StartupNotify=true
StartupWMClass=etma-handler
Actions=new-window;

[Desktop Action new-window]
Name=New Window
Exec=${INSTALL_DIR}/etma_handler --new-window
EOF

# Install desktop entry
xdg-desktop-menu install "${DESKTOP_FILE}"
rm "${DESKTOP_FILE}"

# Set as default handler for .fhi files
xdg-mime default etma-handler.desktop "${MIME_TYPE}"

# ===========================================
# Completion
# ===========================================

log_info "=========================================="
log_info "Installation complete!"
log_info ""
log_info "You can now:"
log_info "  - Launch from your application menu"
log_info "  - Double-click .fhi files to open them"
log_info "  - Run 'etma_handler' from the terminal"
log_info ""

if [[ "${SYSTEM_INSTALL}" == false ]]; then
    log_info "Note: You may need to log out and back in"
    log_info "for the changes to take effect."
fi

# Verify installation
if command -v etma_handler &> /dev/null; then
    log_info "Binary installed: $(which etma_handler)"
else
    log_warn "Binary not in PATH. Add ${BIN_DIR} to your PATH."
    log_info "Example: echo 'export PATH=\"\$PATH:${BIN_DIR}\"' >> ~/.bashrc"
fi
