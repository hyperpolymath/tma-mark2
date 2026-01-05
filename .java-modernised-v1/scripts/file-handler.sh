#!/bin/bash
# SPDX-License-Identifier: MPL-2.0
# =============================================================================
#                       FILE-HANDLER.SH - APPLICATION LAUNCHER
# =============================================================================
#
# WHAT THIS SCRIPT DOES:
#   This script launches the actual eTMA File Handler Java application.
#   It sets up the correct Java environment and runs the application.
#
# PLATFORM SUPPORT:
#   - Linux (primary, fully tested)
#   - macOS (should work with Homebrew Java)
#   - Windows (use WSL or Git Bash)
#
# SAFETY:
#   - This script does NOT connect to the internet directly
#   - The Java application connects only to: css3.open.ac.uk (OU eTMA site)
#   - It does NOT modify system files
#   - It does NOT require administrator/root access
#
# WHAT IT ACCESSES:
#   - The Java runtime (GraalVM or Temurin)
#   - The JAR files in dev/modernised/
#   - Your etmas folder (where student files are stored)
#
# ORIGINAL AUTHOR: Mike Hay (2007)
# MODERNISATION: hyperpolymath (2026)
#
# -----------------------------------------------------------------------------

# =============================================================================
# PLATFORM DETECTION
# =============================================================================
# Detect the operating system for platform-specific behaviour

detect_os() {
    case "$(uname -s)" in
        Linux*)     OS="linux";;
        Darwin*)    OS="macos";;
        CYGWIN*|MINGW*|MSYS*) OS="windows";;
        *)          OS="unknown";;
    esac
}
detect_os

# Application window title (used by wmctrl on Linux to detect if already running)
handler="File Handler"

# =============================================================================
# PATH SETUP
# =============================================================================

# Find where this script is located
# readlink -f gets the full absolute path, even if called via a symlink
SCRIPTDIR="$(dirname "$(readlink -f "$0")")"

# Go up one level to get the main "etma handler" folder
BASEDIR="$(dirname "$SCRIPTDIR")"

# Configuration file location
CONFIG_FILE="${BASEDIR}/config/accessibility.toml"

# =============================================================================
# FIRST-RUN SETUP
# =============================================================================
# On first run, offer accessibility configuration wizard

if [[ -f "${SCRIPTDIR}/first-run-setup.sh" ]]; then
    source "${SCRIPTDIR}/first-run-setup.sh"
fi

# =============================================================================
# ACCESSIBILITY CONFIGURATION
# =============================================================================
# Read accessibility settings from TOML config and set Java system properties.
# This allows the Java application to use the configured accessibility settings.

JAVA_ACCESSIBILITY_OPTS=""

read_accessibility_config() {
    [[ ! -f "$CONFIG_FILE" ]] && return

    # Helper to read a TOML value (simple parser for flat values)
    get_toml_value() {
        local key="$1"
        /usr/bin/grep "^$key = " "$CONFIG_FILE" 2>/dev/null | sed 's/.*= //' | tr -d '"'
    }

    # Read display settings
    local font_scale=$(get_toml_value "font_scale")
    local font_family=$(get_toml_value "font_family")
    local high_contrast=$(get_toml_value "high_contrast")

    # Read dyslexia settings
    local dyslexia_enabled=$(get_toml_value "use_dyslexic_font")

    # Read motion settings
    local reduced_motion=$(get_toml_value "reduced_motion")

    # Build Java system properties
    if [[ -n "$font_scale" && "$font_scale" != "1.0" ]]; then
        JAVA_ACCESSIBILITY_OPTS="$JAVA_ACCESSIBILITY_OPTS -Detma.accessibility.fontScale=$font_scale"
    fi

    if [[ -n "$font_family" && "$font_family" != "system" ]]; then
        JAVA_ACCESSIBILITY_OPTS="$JAVA_ACCESSIBILITY_OPTS -Detma.accessibility.fontFamily=$font_family"
    fi

    if [[ -n "$high_contrast" && "$high_contrast" != "off" ]]; then
        JAVA_ACCESSIBILITY_OPTS="$JAVA_ACCESSIBILITY_OPTS -Detma.accessibility.highContrast=$high_contrast"
    fi

    if [[ "$dyslexia_enabled" == "true" ]]; then
        JAVA_ACCESSIBILITY_OPTS="$JAVA_ACCESSIBILITY_OPTS -Detma.accessibility.dyslexicFont=true"
    fi

    if [[ "$reduced_motion" == "true" ]]; then
        JAVA_ACCESSIBILITY_OPTS="$JAVA_ACCESSIBILITY_OPTS -Detma.accessibility.reducedMotion=true"
    fi

    # Enable Java accessibility bridge if screen reader mode is on
    local screen_reader=$(get_toml_value "enabled")  # from [screen_reader] section
    if [[ "$screen_reader" == "true" ]]; then
        # Enable Java Access Bridge for screen readers
        JAVA_ACCESSIBILITY_OPTS="$JAVA_ACCESSIBILITY_OPTS -Djavax.accessibility.assistive_technologies=com.sun.java.accessibility.AccessBridge"
    fi
}

read_accessibility_config

# =============================================================================
# ARCHITECTURE DETECTION
# =============================================================================
# Detect CPU architecture to choose appropriate JVM.
# GraalVM is 64-bit only; Temurin supports both 32-bit and 64-bit.

detect_arch() {
    case "$(uname -m)" in
        x86_64|amd64|aarch64|arm64)
            ARCH="64bit"
            ;;
        i386|i486|i586|i686|armv7l|armhf)
            ARCH="32bit"
            ;;
        *)
            # Unknown architecture - assume 64-bit and let JVM sort it out
            ARCH="64bit"
            ;;
    esac
}
detect_arch

# =============================================================================
# JAVA RUNTIME DETECTION
# =============================================================================
# We try multiple Java installations in order of preference, checking
# platform-specific locations and CPU architecture.
#
# Search order for 64-bit systems:
#   1. asdf-managed GraalVM 25 (best performance)
#   2. asdf-managed Temurin 25
#   3. Homebrew Java (macOS)
#   4. SDKMAN Java
#   5. System Java
#
# Search order for 32-bit systems:
#   1. asdf-managed Temurin 25 (GraalVM is 64-bit only)
#   2. SDKMAN Java
#   3. System Java
#
# NOTE: GraalVM Community Edition only supports 64-bit architectures.
#       For 32-bit systems, we use Temurin (Eclipse Adoptium) which
#       provides 32-bit builds for Linux and Windows.

JAVA_BIN=""

# Linux and macOS: Check asdf-managed Java first
if [[ "$OS" == "linux" || "$OS" == "macos" ]]; then

    # 64-bit: Try GraalVM first (best performance)
    if [[ "$ARCH" == "64bit" ]]; then
        if [[ -x "$HOME/.asdf/installs/java/graalvm-community-25.0.1/bin/java" ]]; then
            JAVA_HOME="$HOME/.asdf/installs/java/graalvm-community-25.0.1"
            JAVA_BIN="${JAVA_HOME}/bin/java"
        fi
    fi

    # Both architectures: Try Temurin (has 32-bit and 64-bit builds)
    if [[ -z "$JAVA_BIN" ]]; then
        # Try Temurin 25 first
        if [[ -x "$HOME/.asdf/installs/java/temurin-25.0.1+8.0.LTS/bin/java" ]]; then
            JAVA_HOME="$HOME/.asdf/installs/java/temurin-25.0.1+8.0.LTS"
            JAVA_BIN="${JAVA_HOME}/bin/java"
        # Try any Temurin version (glob for flexibility)
        elif ls "$HOME/.asdf/installs/java/temurin-"*/bin/java &>/dev/null 2>&1; then
            JAVA_HOME=$(dirname "$(dirname "$(ls "$HOME/.asdf/installs/java/temurin-"*/bin/java 2>/dev/null | head -1)")")
            JAVA_BIN="${JAVA_HOME}/bin/java"
        fi
    fi
fi

# macOS: Check Homebrew Java
if [[ -z "$JAVA_BIN" && "$OS" == "macos" ]]; then
    # Homebrew on Apple Silicon
    if [[ -x "/opt/homebrew/opt/openjdk/bin/java" ]]; then
        JAVA_HOME="/opt/homebrew/opt/openjdk"
        JAVA_BIN="${JAVA_HOME}/bin/java"
    # Homebrew on Intel Mac
    elif [[ -x "/usr/local/opt/openjdk/bin/java" ]]; then
        JAVA_HOME="/usr/local/opt/openjdk"
        JAVA_BIN="${JAVA_HOME}/bin/java"
    fi
fi

# All platforms: Check SDKMAN
if [[ -z "$JAVA_BIN" && -d "$HOME/.sdkman/candidates/java/current" ]]; then
    JAVA_HOME="$HOME/.sdkman/candidates/java/current"
    JAVA_BIN="${JAVA_HOME}/bin/java"
fi

# Windows (Git Bash/WSL): Check common locations
# Note: 64-bit Windows uses "Program Files", 32-bit uses "Program Files (x86)"
if [[ -z "$JAVA_BIN" && "$OS" == "windows" ]]; then
    # 64-bit Windows locations
    if [[ "$ARCH" == "64bit" ]]; then
        for java_dir in "/c/Program Files/Eclipse Adoptium"/*/bin/java.exe \
                        "/c/Program Files/GraalVM"/*/bin/java.exe \
                        "/c/Program Files/Java"/*/bin/java.exe; do
            if [[ -x "$java_dir" ]]; then
                JAVA_BIN="$java_dir"
                break
            fi
        done
    fi
    # 32-bit Windows or 32-bit app on 64-bit Windows
    if [[ -z "$JAVA_BIN" ]]; then
        for java_dir in "/c/Program Files (x86)/Eclipse Adoptium"/*/bin/java.exe \
                        "/c/Program Files (x86)/Java"/*/bin/java.exe; do
            if [[ -x "$java_dir" ]]; then
                JAVA_BIN="$java_dir"
                break
            fi
        done
    fi
fi

# Final fallback: System Java (whatever is in PATH)
if [[ -z "$JAVA_BIN" ]] || [[ ! -x "$JAVA_BIN" ]]; then
    if command -v java &>/dev/null; then
        JAVA_BIN="java"
    else
        echo "ERROR: Java not found. Please install Java 21 or later."
        echo ""
        echo "Detected: $OS ($ARCH)"
        echo ""
        if [[ "$ARCH" == "64bit" ]]; then
            echo "Recommended options for 64-bit systems:"
            echo "  - Linux:   asdf install java graalvm-community-25.0.1"
            echo "  - macOS:   brew install openjdk"
            echo "  - Windows: Download from https://adoptium.net/"
        else
            echo "Recommended options for 32-bit systems:"
            echo "  - Linux:   asdf install java temurin-21.0.4+7.0.LTS"
            echo "  - Windows: Download Temurin x86 from https://adoptium.net/"
            echo ""
            echo "Note: GraalVM is 64-bit only. Use Temurin for 32-bit systems."
        fi
        exit 1
    fi
fi

# =============================================================================
# CLASSPATH SETUP
# =============================================================================
# The classpath tells Java where to find the application and its libraries.
# Each JAR file is a package of Java code.
#
# PLATFORM NOTE: Unix uses : as separator, Windows uses ;
# We detect this automatically based on OS.

# Set classpath separator based on OS
if [[ "$OS" == "windows" ]]; then
    SEP=";"
else
    SEP=":"
fi

# Location of the library JAR files
LIBDIR="${BASEDIR}/dev/modernised/lib"

# Start with the main application JAR
# This contains the eTMA Handler code (Mike Hay's work)
CP="${BASEDIR}/dev/modernised/etmaHandlerJ-modernised.jar"

# Add email libraries (Jakarta Mail - for sending marked work)
# angus-mail is the reference implementation of Jakarta Mail
CP="${CP}${SEP}${LIBDIR}/angus-mail-2.0.4.jar"
CP="${CP}${SEP}${LIBDIR}/angus-activation-2.0.2.jar"
CP="${CP}${SEP}${LIBDIR}/jakarta.mail-api-2.1.5.jar"
CP="${CP}${SEP}${LIBDIR}/jakarta.activation-api-2.1.3.jar"

# Add utility libraries (Apache Commons)
# commons-codec: encoding/decoding (Base64, etc.)
# commons-lang3: string manipulation and utilities
CP="${CP}${SEP}${LIBDIR}/commons-codec-1.19.0.jar"
CP="${CP}${SEP}${LIBDIR}/commons-lang3-3.20.0.jar"

# Add spell checker libraries (Jazzy)
# These enable spell checking in the feedback editor
CP="${CP}${SEP}${LIBDIR}/jazzy-core.jar"
CP="${CP}${SEP}${LIBDIR}/jazzy-swing.jar"

# =============================================================================
# SINGLE INSTANCE CHECK
# =============================================================================
# If the application is already running, bring it to the front instead of
# starting a second copy. This prevents confusion and saves resources.
#
# PLATFORM-SPECIFIC METHODS:
#   - Linux: wmctrl (if installed) for window management
#   - macOS: AppleScript to activate existing window
#   - Windows: PowerShell to check for running process
#   - Fallback: pgrep/ps to check for Java process with our class name

check_already_running() {
    case "$OS" in
        linux)
            # Method 1: wmctrl (best - can actually bring window to front)
            if command -v wmctrl &>/dev/null; then
                if wmctrl -l | /usr/bin/grep -q "$handler"; then
                    wmctrl -a "$handler"
                    return 0  # Already running, activated
                fi
            fi
            # Method 2: Check for running Java process
            if pgrep -f "etmaHandler.EtmaHandlerJ" &>/dev/null; then
                echo "eTMA File Handler is already running."
                echo "Check your taskbar or use Alt+Tab to find it."
                return 0
            fi
            ;;
        macos)
            # Method 1: AppleScript to check and activate window
            if osascript -e 'tell application "System Events" to get name of every process' 2>/dev/null | /usr/bin/grep -q "java"; then
                # Check if it's our specific app
                if pgrep -f "etmaHandler.EtmaHandlerJ" &>/dev/null; then
                    # Try to bring it to front
                    osascript -e 'tell application "System Events" to set frontmost of (first process whose name contains "java") to true' 2>/dev/null
                    echo "eTMA File Handler is already running."
                    return 0
                fi
            fi
            ;;
        windows)
            # Check via PowerShell for running Java process
            if command -v powershell.exe &>/dev/null; then
                if powershell.exe -Command "Get-Process java -ErrorAction SilentlyContinue | Where-Object { \$_.CommandLine -match 'EtmaHandlerJ' }" 2>/dev/null | /usr/bin/grep -q "."; then
                    echo "eTMA File Handler is already running."
                    echo "Check your taskbar to find it."
                    return 0
                fi
            fi
            ;;
    esac
    return 1  # Not running
}

# Check if already running
if check_already_running; then
    exit 0
fi

# =============================================================================
# LAUNCH APPLICATION
# =============================================================================

# Change to the base directory (some relative paths may depend on this)
cd "${BASEDIR}"

# Start the Java application
# The "exec" command replaces this shell script with the Java process
# This means when Java exits, we're fully done (no leftover shell)
#
# Java options explained:
#   --add-opens: Allow access to internal Java classes (needed for older code)
#   $JAVA_ACCESSIBILITY_OPTS: Accessibility settings from config/accessibility.toml
#   -cp: Set the classpath (where to find JAR files)
#   etmaHandler.EtmaHandlerJ: The main class to run
#   "$@": Pass any command-line arguments through to the application
#
# shellcheck disable=SC2086
exec "$JAVA_BIN" \
    --add-opens=java.base/java.lang=ALL-UNNAMED \
    --add-opens=java.base/java.util=ALL-UNNAMED \
    --add-opens=java.base/java.io=ALL-UNNAMED \
    --add-opens=java.desktop/java.awt=ALL-UNNAMED \
    --add-opens=java.desktop/javax.swing=ALL-UNNAMED \
    --add-opens=java.desktop/javax.swing.plaf.basic=ALL-UNNAMED \
    --add-opens=java.desktop/javax.swing.text=ALL-UNNAMED \
    --add-opens=java.desktop/javax.swing.table=ALL-UNNAMED \
    --add-opens=java.desktop/sun.awt=ALL-UNNAMED \
    --add-opens=java.prefs/java.util.prefs=ALL-UNNAMED \
    $JAVA_ACCESSIBILITY_OPTS \
    -cp "$CP" \
    etmaHandler.EtmaHandlerJ "$@"
