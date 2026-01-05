#!/bin/bash
# SPDX-License-Identifier: MPL-2.0
# =============================================================================
#                       INSTALL.SH - DESKTOP SHORTCUT INSTALLER
# =============================================================================
#
# WHAT THIS SCRIPT DOES:
#   Creates a desktop shortcut so you can launch eTMA File Handler from your
#   applications menu (like any other installed program). Also optionally
#   creates a shortcut on your Desktop.
#
# SAFETY:
#   - This script does NOT require administrator/root access
#   - It only creates files in YOUR home directory
#   - It does NOT modify system files
#   - It does NOT connect to the internet
#
# WHAT IT CREATES:
#   - ~/.local/share/applications/etma-file-handler.desktop
#   - ~/Desktop/etma-file-handler.desktop (if Desktop folder exists)
#
# ORIGINAL AUTHOR: Mike Hay (2007)
# MODERNISATION: hyperpolymath (2026)
#
# -----------------------------------------------------------------------------

# =============================================================================
# PATH SETUP
# =============================================================================

# Find where this script is located (the scripts/ folder)
SCRIPTDIR="$(cd "$(dirname "$0")" && pwd)"

# Go up one level to get the main "etma handler" folder
BASEDIR="$(dirname "$SCRIPTDIR")"

# Where to create the desktop entry file
# This is the standard location for user-installed applications on Linux
DESKTOP_FILE="$HOME/.local/share/applications/etma-file-handler.desktop"

# =============================================================================
# CREATE APPLICATIONS DIRECTORY
# =============================================================================

# Make sure the applications directory exists
# The -p flag means "create parent directories if needed" and
# "don't error if the directory already exists"
mkdir -p "$HOME/.local/share/applications"

# =============================================================================
# CREATE DESKTOP ENTRY
# =============================================================================
# A .desktop file is how Linux knows about applications.
# It tells the system the application name, icon, how to run it, etc.

# Create the desktop entry file
# The "cat > file << EOF" syntax writes everything between << EOF and EOF
# into the file
cat > "$DESKTOP_FILE" << EOF
[Desktop Entry]
Version=1.0
Type=Application
Name=eTMA File Handler
Comment=Open University eTMA handler for tutors
Exec=${SCRIPTDIR}/file-handler.sh %f
Icon=${BASEDIR}/.icon/icon.png
Terminal=false
Categories=Office;Education;
MimeType=application/x-etma;
StartupNotify=true
StartupWMClass=File Handler
EOF

# Make the desktop file executable
chmod +x "$DESKTOP_FILE"

# =============================================================================
# CREATE DESKTOP SHORTCUT (OPTIONAL)
# =============================================================================

# If the user has a Desktop folder, also create a shortcut there
# Not all Linux setups have a Desktop folder, so we check first
if [[ -d "$HOME/Desktop" ]]; then
    # Copy the desktop entry to the Desktop
    cp "$DESKTOP_FILE" "$HOME/Desktop/etma-file-handler.desktop"

    # Make it executable
    chmod +x "$HOME/Desktop/etma-file-handler.desktop"

    # Mark it as trusted (GNOME/KDE security feature)
    # The "|| true" means "don't fail if this command doesn't work"
    # (not all systems have gio or support this feature)
    gio set "$HOME/Desktop/etma-file-handler.desktop" metadata::trusted true 2>/dev/null || true
fi

# =============================================================================
# UPDATE DESKTOP DATABASE
# =============================================================================

# Tell the system to refresh its list of applications
# This makes the new shortcut appear in menus immediately
# The "|| true" means "don't fail if this command doesn't work"
update-desktop-database "$HOME/.local/share/applications" 2>/dev/null || true

# =============================================================================
# SUCCESS MESSAGE
# =============================================================================

echo "Installed! You can now:"
echo "  1. Find 'eTMA File Handler' in your application menu"
echo "  2. Use the desktop shortcut (if Desktop folder exists)"
echo ""
echo "To run directly: ${SCRIPTDIR}/file-handler.sh"
