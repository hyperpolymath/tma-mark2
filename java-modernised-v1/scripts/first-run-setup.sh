#!/bin/bash
# SPDX-License-Identifier: MPL-2.0
# =============================================================================
#                    FIRST-RUN-SETUP.SH - ACCESSIBILITY SETUP WIZARD
# =============================================================================
#
# WHAT THIS SCRIPT DOES:
#   Runs on first launch to help users configure accessibility settings.
#   Creates or updates the config/accessibility.toml file with user preferences.
#
# SAFETY:
#   - Only modifies files in the config/ folder
#   - Does NOT require administrator/root access
#   - Does NOT send any data anywhere
#
# ORIGINAL AUTHOR: hyperpolymath (2026)
#
# -----------------------------------------------------------------------------

# =============================================================================
# CONFIGURATION
# =============================================================================

SCRIPTDIR="$(dirname "$(readlink -f "$0")")"
BASEDIR="$(dirname "$SCRIPTDIR")"
CONFIG_FILE="${BASEDIR}/config/accessibility.toml"

# Terminal colours
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

# =============================================================================
# FUNCTIONS
# =============================================================================

# Check if this is first run
is_first_run() {
    if [[ ! -f "$CONFIG_FILE" ]]; then
        return 0  # True - first run
    fi

    # Check if first_run flag is true in config
    if grep -q "first_run = true" "$CONFIG_FILE" 2>/dev/null; then
        return 0  # True - first run flag still set
    fi

    return 1  # False - not first run
}

# Display banner
show_banner() {
    echo -e "${CYAN}"
    echo "╔═══════════════════════════════════════════════════════════╗"
    echo "║          eTMA File Handler - First Time Setup             ║"
    echo "║          Accessibility Configuration Wizard               ║"
    echo "╚═══════════════════════════════════════════════════════════╝"
    echo -e "${NC}"
}

# Ask yes/no question
ask_yes_no() {
    local question="$1"
    local default="${2:-n}"

    if [[ "$default" == "y" ]]; then
        prompt="[Y/n]"
    else
        prompt="[y/N]"
    fi

    read -p "$question $prompt " -n 1 -r
    echo

    if [[ -z "$REPLY" ]]; then
        REPLY="$default"
    fi

    [[ "$REPLY" =~ ^[Yy]$ ]]
}

# Update TOML value
update_toml() {
    local key="$1"
    local value="$2"
    local file="$CONFIG_FILE"

    # Handle boolean values
    if [[ "$value" == "true" || "$value" == "false" ]]; then
        sed -i "s/^$key = .*/$key = $value/" "$file"
    # Handle string values
    else
        sed -i "s/^$key = .*/$key = \"$value\"/" "$file"
    fi
}

# =============================================================================
# MAIN WIZARD
# =============================================================================

run_wizard() {
    show_banner

    echo -e "${BLUE}Welcome to eTMA File Handler!${NC}"
    echo ""
    echo "This wizard will help you configure accessibility settings."
    echo "You can change these settings later in the config file or"
    echo "through the application's Preferences menu."
    echo ""
    echo -e "${YELLOW}Press Enter to continue, or Ctrl+C to skip setup...${NC}"
    read

    # Ensure config directory exists
    mkdir -p "$(dirname "$CONFIG_FILE")"

    # Copy default config if it doesn't exist
    if [[ ! -f "$CONFIG_FILE" ]]; then
        if [[ -f "${BASEDIR}/config/accessibility.toml.default" ]]; then
            cp "${BASEDIR}/config/accessibility.toml.default" "$CONFIG_FILE"
        else
            # Create minimal config
            cat > "$CONFIG_FILE" << 'EOF'
[general]
first_run = true
language = "en"

[display]
font_scale = 1.0
font_family = "system"
high_contrast = "off"

[dyslexia]
enabled = false
use_dyslexic_font = false

[low_vision]
enabled = false

[screen_reader]
enabled = false

[motion]
reduced_motion = false
EOF
        fi
    fi

    echo ""
    echo -e "${BLUE}═══════════════════════════════════════════════════════════${NC}"
    echo -e "${BLUE}SECTION 1: Display Preferences${NC}"
    echo -e "${BLUE}═══════════════════════════════════════════════════════════${NC}"
    echo ""

    # Large text
    if ask_yes_no "Would you like larger text throughout the application?"; then
        echo ""
        echo "Choose a size:"
        echo "  1) Slightly larger (125%)"
        echo "  2) Large (150%)"
        echo "  3) Very large (200%)"
        read -p "Enter choice [1-3]: " -n 1 -r
        echo ""
        case $REPLY in
            1) update_toml "font_scale" "1.25" ;;
            2) update_toml "font_scale" "1.5" ;;
            3) update_toml "font_scale" "2.0" ;;
            *) update_toml "font_scale" "1.25" ;;
        esac
        echo -e "${GREEN}✓ Large text enabled${NC}"
    fi

    # High contrast
    echo ""
    if ask_yes_no "Would you like high contrast mode?"; then
        echo ""
        echo "Choose a contrast style:"
        echo "  1) High contrast (black and white)"
        echo "  2) Yellow on black"
        echo "  3) Black on yellow"
        read -p "Enter choice [1-3]: " -n 1 -r
        echo ""
        case $REPLY in
            1) update_toml "high_contrast" "high" ;;
            2) update_toml "high_contrast" "yellow-black" ;;
            3) update_toml "high_contrast" "black-yellow" ;;
            *) update_toml "high_contrast" "high" ;;
        esac
        echo -e "${GREEN}✓ High contrast enabled${NC}"
    fi

    echo ""
    echo -e "${BLUE}═══════════════════════════════════════════════════════════${NC}"
    echo -e "${BLUE}SECTION 2: Reading Support${NC}"
    echo -e "${BLUE}═══════════════════════════════════════════════════════════${NC}"
    echo ""

    # Dyslexia support
    if ask_yes_no "Would you like dyslexia-friendly settings?"; then
        update_toml "enabled" "true"  # dyslexia.enabled

        echo ""
        if ask_yes_no "  Use a dyslexia-friendly font (OpenDyslexic)?" "y"; then
            update_toml "use_dyslexic_font" "true"
            update_toml "font_family" "opendyslexic"
        fi

        echo ""
        if ask_yes_no "  Use a coloured background overlay?" "n"; then
            echo ""
            echo "  Choose a colour:"
            echo "    1) Cream/off-white"
            echo "    2) Light blue"
            echo "    3) Light green"
            echo "    4) Light pink"
            read -p "  Enter choice [1-4]: " -n 1 -r
            echo ""
            case $REPLY in
                1) update_toml "overlay_colour" "cream" ;;
                2) update_toml "overlay_colour" "light-blue" ;;
                3) update_toml "overlay_colour" "light-green" ;;
                4) update_toml "overlay_colour" "light-pink" ;;
                *) update_toml "overlay_colour" "cream" ;;
            esac
        fi

        echo -e "${GREEN}✓ Dyslexia support enabled${NC}"
    fi

    echo ""
    echo -e "${BLUE}═══════════════════════════════════════════════════════════${NC}"
    echo -e "${BLUE}SECTION 3: Screen Reader and Motion${NC}"
    echo -e "${BLUE}═══════════════════════════════════════════════════════════${NC}"
    echo ""

    # Screen reader
    if ask_yes_no "Do you use a screen reader?"; then
        # Find the screen_reader section and update enabled
        sed -i '/^\[screen_reader\]/,/^\[/{s/^enabled = .*/enabled = true/}' "$CONFIG_FILE"
        sed -i '/^\[screen_reader\]/,/^\[/{s/^enhanced_labels = .*/enhanced_labels = true/}' "$CONFIG_FILE"
        echo -e "${GREEN}✓ Screen reader optimisations enabled${NC}"
    fi

    # Reduced motion
    echo ""
    if ask_yes_no "Would you prefer reduced motion (fewer animations)?"; then
        sed -i '/^\[motion\]/,/^\[/{s/^reduced_motion = .*/reduced_motion = true/}' "$CONFIG_FILE"
        echo -e "${GREEN}✓ Reduced motion enabled${NC}"
    fi

    echo ""
    echo -e "${BLUE}═══════════════════════════════════════════════════════════${NC}"
    echo -e "${BLUE}Setup Complete${NC}"
    echo -e "${BLUE}═══════════════════════════════════════════════════════════${NC}"
    echo ""

    # Mark first run as complete
    update_toml "first_run" "false"

    echo -e "${GREEN}Your accessibility settings have been saved to:${NC}"
    echo "  $CONFIG_FILE"
    echo ""
    echo "You can change these settings at any time by:"
    echo "  1. Editing the config file directly, or"
    echo "  2. Using the application's Preferences menu"
    echo ""
    echo -e "${YELLOW}Press Enter to start eTMA File Handler...${NC}"
    read
}

# =============================================================================
# MAIN
# =============================================================================

if is_first_run; then
    run_wizard
fi
