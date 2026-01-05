#!/bin/bash
# SPDX-License-Identifier: MPL-2.0
# =============================================================================
#                       UNINSTALL.SH - COMPLETE REMOVAL TOOL
# =============================================================================
#
# WHAT THIS SCRIPT DOES:
#   Completely removes the eTMA File Handler from your system, including:
#   - Desktop shortcuts
#   - Application menu entries
#   - User preferences/settings
#   - Optionally: the application folder itself
#
# SAFETY:
#   - This script does NOT require administrator/root access
#   - It only removes files in YOUR home directory
#   - It asks for confirmation before each major deletion
#   - Student data (etmas folder) is NOT touched - use sanitise-data.sh for that
#
# WHAT IT REMOVES:
#   - ~/.local/share/applications/etma-file-handler.desktop
#   - ~/Desktop/etma-file-handler.desktop (if exists)
#   - ~/.java/.userPrefs/etmaHandler/ (application preferences)
#   - Optionally: the entire application folder
#
# ORIGINAL AUTHOR: Mike Hay (2007)
# MODERNISATION: hyperpolymath (2026)
#
# -----------------------------------------------------------------------------

# =============================================================================
# CONFIGURATION
# =============================================================================

SCRIPTDIR="$(cd "$(dirname "$0")" && pwd)"
BASEDIR="$(dirname "$SCRIPTDIR")"

# Terminal colours
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# =============================================================================
# BANNER
# =============================================================================

echo -e "${YELLOW}"
echo "╔═══════════════════════════════════════════════════════════╗"
echo "║          eTMA File Handler - Uninstaller                  ║"
echo "╚═══════════════════════════════════════════════════════════╝"
echo -e "${NC}"

echo ""
echo "This will remove the eTMA File Handler from your system."
echo ""
echo -e "${YELLOW}NOTE: This does NOT delete student data.${NC}"
echo "      Use sanitise-data.sh to securely delete student files."
echo ""

# =============================================================================
# CONFIRMATION
# =============================================================================

read -p "Are you sure you want to uninstall? [y/N] " -n 1 -r
echo ""
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "Uninstall cancelled."
    exit 0
fi

echo ""

# =============================================================================
# REMOVE DESKTOP SHORTCUTS
# =============================================================================

echo -e "${YELLOW}Removing desktop shortcuts...${NC}"

# Remove from applications menu
if [[ -f "$HOME/.local/share/applications/etma-file-handler.desktop" ]]; then
    rm -f "$HOME/.local/share/applications/etma-file-handler.desktop"
    echo "  ✓ Removed applications menu entry"
else
    echo "  - No applications menu entry found"
fi

# Remove from Desktop
if [[ -f "$HOME/Desktop/etma-file-handler.desktop" ]]; then
    rm -f "$HOME/Desktop/etma-file-handler.desktop"
    echo "  ✓ Removed Desktop shortcut"
else
    echo "  - No Desktop shortcut found"
fi

# Update desktop database
update-desktop-database "$HOME/.local/share/applications" 2>/dev/null || true
echo "  ✓ Updated applications database"

# =============================================================================
# REMOVE PREFERENCES
# =============================================================================

echo ""
echo -e "${YELLOW}Removing application preferences...${NC}"

# Java Preferences (where the application stores its settings)
if [[ -d "$HOME/.java/.userPrefs/etmaHandler" ]]; then
    rm -rf "$HOME/.java/.userPrefs/etmaHandler"
    echo "  ✓ Removed application preferences"
else
    echo "  - No preferences found"
fi

# =============================================================================
# REMOVE ASDF TOOL-VERSIONS (OPTIONAL)
# =============================================================================

echo ""
echo -e "${YELLOW}Checking for project-specific Java settings...${NC}"

if [[ -f "$BASEDIR/.tool-versions" ]]; then
    echo "  Found .tool-versions file (Java version pinning)"
    read -p "  Remove project Java settings? [y/N] " -n 1 -r
    echo ""
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        rm -f "$BASEDIR/.tool-versions"
        echo "  ✓ Removed .tool-versions"
    fi
fi

# =============================================================================
# REMOVE APPLICATION FOLDER (OPTIONAL)
# =============================================================================

echo ""
echo -e "${YELLOW}Remove the application folder itself?${NC}"
echo ""
echo "Location: $BASEDIR"
echo ""
echo -e "${RED}WARNING: This will delete the entire eTMA Handler folder!${NC}"
echo "         Make sure you have a backup if needed."
echo ""

read -p "Delete the application folder? [y/N] " -n 1 -r
echo ""
if [[ $REPLY =~ ^[Yy]$ ]]; then
    # Extra confirmation for safety
    read -p "Type 'DELETE' to confirm: " confirm
    if [[ "$confirm" == "DELETE" ]]; then
        echo ""
        echo "Removing application folder..."
        rm -rf "$BASEDIR"
        echo -e "${GREEN}✓ Application folder removed${NC}"
    else
        echo "Deletion cancelled."
    fi
else
    echo "Application folder kept."
fi

# =============================================================================
# COMPLETION
# =============================================================================

echo ""
echo -e "${GREEN}════════════════════════════════════════════════════════════${NC}"
echo -e "${GREEN}Uninstall complete!${NC}"
echo ""
echo "Removed:"
echo "  - Desktop shortcuts and menu entries"
echo "  - Application preferences"
echo ""
echo -e "${YELLOW}NOT removed (use sanitise-data.sh for these):${NC}"
echo "  - Student data in ~/etmas/"
echo "  - Any marked TMAs on your system"
echo ""
echo -e "${YELLOW}Remember: OU Data Protection Policy${NC}"
echo "  You should not retain student data for more than"
echo "  one year after the student leaves your tuition."
echo ""
