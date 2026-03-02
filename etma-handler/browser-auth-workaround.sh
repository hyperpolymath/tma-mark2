#!/bin/bash
# Workaround: Open browser to authenticate first, then try the app
echo "Opening browser to authenticate with OU..."
echo "1. Log in to https://css3.open.ac.uk/etma/tutor in your browser"
echo "2. Complete the SAML authentication"
echo "3. Then try running the eTMA handler"
echo ""
echo "Press Enter after you've logged in via browser..."
xdg-open "https://css3.open.ac.uk/etma/tutor" 2>/dev/null &
read
echo "Starting eTMA handler..."
cd "$(dirname "$0")"
./file-handler.sh
