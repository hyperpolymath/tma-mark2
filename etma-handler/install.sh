#!/bin/bash
# Install eTMA File Handler desktop shortcut

BASEDIR="$(cd "$(dirname "$0")" && pwd)"
DESKTOP_FILE="$HOME/.local/share/applications/etma-file-handler.desktop"

# Create applications directory if needed
mkdir -p "$HOME/.local/share/applications"

# Create desktop entry with correct absolute paths
cat > "$DESKTOP_FILE" << EOF
[Desktop Entry]
Version=1.0
Type=Application
Name=eTMA File Handler
Comment=Open University eTMA handler for tutors
Exec=${BASEDIR}/file-handler.sh %f
Icon=${BASEDIR}/Installer/etmahandlerj.png
Terminal=false
Categories=Office;Education;
MimeType=application/x-etma;
StartupNotify=true
StartupWMClass=File Handler
EOF

chmod +x "$DESKTOP_FILE"

# Also create on Desktop if it exists
if [[ -d "$HOME/Desktop" ]]; then
    cp "$DESKTOP_FILE" "$HOME/Desktop/etma-file-handler.desktop"
    chmod +x "$HOME/Desktop/etma-file-handler.desktop"
    # Mark as trusted (for GNOME/KDE)
    gio set "$HOME/Desktop/etma-file-handler.desktop" metadata::trusted true 2>/dev/null || true
fi

# Update desktop database
update-desktop-database "$HOME/.local/share/applications" 2>/dev/null || true

echo "Installed! You can now:"
echo "  1. Find 'eTMA File Handler' in your application menu"
echo "  2. Use the desktop shortcut (if Desktop folder exists)"
echo ""
echo "To run directly: ${BASEDIR}/file-handler.sh"
