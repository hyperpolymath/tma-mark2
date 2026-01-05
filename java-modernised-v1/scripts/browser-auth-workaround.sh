#!/bin/bash
# SPDX-License-Identifier: MPL-2.0
# =============================================================================
#                  BROWSER-AUTH-WORKAROUND.SH - LOGIN HELPER
# =============================================================================
#
# WHAT THIS SCRIPT DOES:
#   Helps when the eTMA Handler has trouble logging in to the OU system.
#   The OU uses SAML authentication, which sometimes works better when you
#   first log in via your web browser.
#
# WHY IS THIS NEEDED?
#   The Open University's eTMA system uses "SAML" for login. SAML requires
#   cookies and session data that the Java application sometimes has trouble
#   with. By logging in via your browser first, these session tokens get
#   stored and the Java app can then use them.
#
# SAFETY:
#   - This script opens your default web browser
#   - It only connects to: css3.open.ac.uk (the official OU eTMA site)
#   - It does NOT send any data anywhere else
#   - It does NOT store or transmit your password
#   - After you log in via the browser, it starts the eTMA Handler
#
# WHAT IT ACCESSES:
#   - Your default web browser (via xdg-open)
#   - The OU eTMA website (https://css3.open.ac.uk/etma/tutor)
#   - The file-handler.sh script in this directory
#
# ORIGINAL AUTHOR: Mike Hay (2007)
# MODERNISATION: hyperpolymath (2026)
#
# -----------------------------------------------------------------------------

# =============================================================================
# INSTRUCTIONS FOR USER
# =============================================================================

echo "Opening browser to authenticate with OU..."
echo ""
echo "Please follow these steps:"
echo "  1. Log in to the OU eTMA site in your browser"
echo "  2. Complete the SAML authentication (you may see multiple pages)"
echo "  3. Once you see the eTMA tutor dashboard, come back here"
echo ""
echo "Press Enter after you've logged in via browser..."

# =============================================================================
# OPEN BROWSER
# =============================================================================

# Open the OU eTMA site in the default web browser
# xdg-open is the standard way to open URLs on Linux
# The & runs it in the background so the script continues
# The 2>/dev/null hides any error messages (e.g., if xdg-open is noisy)
xdg-open "https://css3.open.ac.uk/etma/tutor" 2>/dev/null &

# =============================================================================
# WAIT FOR USER
# =============================================================================

# Pause until the user presses Enter
# The "read" command waits for input
read

# =============================================================================
# START THE APPLICATION
# =============================================================================

echo "Starting eTMA handler..."

# Change to the scripts directory (where this script is located)
cd "$(dirname "$0")"

# Run the main file handler script
./file-handler.sh
