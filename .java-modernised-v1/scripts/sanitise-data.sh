#!/bin/bash
# SPDX-License-Identifier: MPL-2.0
# =============================================================================
#                  SANITISE-DATA.SH - STUDENT DATA REMOVAL TOOL
# =============================================================================
#
# WHAT THIS SCRIPT DOES:
#   Securely removes student data from your computer to comply with
#   Open University data protection requirements.
#
# OU DATA PROTECTION POLICY:
#   You should not retain student work or personal data for more than
#   ONE YEAR after the student leaves your tuition. This includes:
#   - TMA submissions
#   - PT3 forms (marked feedback)
#   - Student names and PI numbers
#   - Email addresses
#   - Any other personally identifiable information
#
# SAFETY:
#   - This script does NOT require administrator/root access
#   - It ONLY operates on files in your etmas folder
#   - It asks for confirmation multiple times before deleting
#   - It can show you what will be deleted before doing anything
#   - Deletions are PERMANENT - ensure you have uploaded marked work first
#
# WHAT IT CAN REMOVE:
#   - Course folders (DD210-*, E225-*, etc.)
#   - TMA files (.fhi, .pt3, .doc, .docx, .odt, .pdf, etc.)
#   - The returns folder (zipped marked work)
#
# ORIGINAL AUTHOR: Mike Hay (2007)
# MODERNISATION: hyperpolymath (2026)
#
# -----------------------------------------------------------------------------

# =============================================================================
# CONFIGURATION
# =============================================================================

# Default location for student files
# This can be changed in eTMA Handler preferences
DEFAULT_ETMAS_DIR="$HOME/etmas"

# Terminal colours
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# =============================================================================
# BANNER
# =============================================================================

echo -e "${RED}"
echo "╔═══════════════════════════════════════════════════════════╗"
echo "║          eTMA Data Sanitisation Tool                      ║"
echo "║          PERMANENT DATA DELETION                          ║"
echo "╚═══════════════════════════════════════════════════════════╝"
echo -e "${NC}"

# =============================================================================
# DATA PROTECTION REMINDER
# =============================================================================

echo ""
echo -e "${YELLOW}╔═══════════════════════════════════════════════════════════╗"
echo "║                 OU DATA PROTECTION POLICY                 ║"
echo "╠═══════════════════════════════════════════════════════════╣"
echo "║                                                           ║"
echo "║  You should NOT retain student work or personal data for  ║"
echo "║  more than ONE YEAR after the student leaves your         ║"
echo "║  tuition.                                                 ║"
echo "║                                                           ║"
echo "║  This includes:                                           ║"
echo "║  • TMA submissions and marked copies                      ║"
echo "║  • PT3 feedback forms                                     ║"
echo "║  • Student names, PI numbers, email addresses             ║"
echo "║  • Any personally identifiable information                ║"
echo "║                                                           ║"
echo "╚═══════════════════════════════════════════════════════════╝${NC}"
echo ""

# =============================================================================
# LOCATE DATA FOLDER
# =============================================================================

echo -e "${BLUE}Locating student data folder...${NC}"
echo ""

# Check default location
ETMAS_DIR="$DEFAULT_ETMAS_DIR"

if [[ ! -d "$ETMAS_DIR" ]]; then
    echo -e "${YELLOW}Default folder not found: $ETMAS_DIR${NC}"
    echo ""
    read -p "Enter the path to your etmas folder: " ETMAS_DIR

    if [[ ! -d "$ETMAS_DIR" ]]; then
        echo -e "${RED}Folder not found: $ETMAS_DIR${NC}"
        exit 1
    fi
fi

echo "Using folder: $ETMAS_DIR"
echo ""

# =============================================================================
# SHOW CURRENT DATA
# =============================================================================

echo -e "${BLUE}Scanning for student data...${NC}"
echo ""

# Count files and folders
course_folders=$(find "$ETMAS_DIR" -maxdepth 1 -type d -name "*-*" 2>/dev/null | wc -l)
fhi_files=$(find "$ETMAS_DIR" -name "*.fhi" 2>/dev/null | wc -l)
pt3_files=$(find "$ETMAS_DIR" -name "*.pt3" 2>/dev/null | wc -l)
doc_files=$(find "$ETMAS_DIR" \( -name "*.doc" -o -name "*.docx" -o -name "*.odt" -o -name "*.pdf" \) 2>/dev/null | wc -l)
zip_files=$(find "$ETMAS_DIR" -name "*.zip" 2>/dev/null | wc -l)

echo "Found:"
echo "  Course folders:  $course_folders"
echo "  FHI files:       $fhi_files"
echo "  PT3 files:       $pt3_files"
echo "  Documents:       $doc_files"
echo "  ZIP archives:    $zip_files"
echo ""

# List course folders
if [[ $course_folders -gt 0 ]]; then
    echo "Course folders present:"
    find "$ETMAS_DIR" -maxdepth 1 -type d -name "*-*" -printf "  %f\n" 2>/dev/null
    echo ""
fi

# =============================================================================
# MENU
# =============================================================================

echo -e "${YELLOW}What would you like to do?${NC}"
echo ""
echo "  1) Preview what would be deleted (safe - no changes)"
echo "  2) Delete specific course folder"
echo "  3) Delete all student data (PERMANENT)"
echo "  4) Delete only returns folder (already-submitted work)"
echo "  q) Quit without changes"
echo ""
read -p "Enter choice [1-4, q]: " -n 1 -r
echo ""
echo ""

case $REPLY in
    1)
        # =============================================================
        # PREVIEW MODE
        # =============================================================
        echo -e "${BLUE}Preview of files that would be deleted:${NC}"
        echo ""
        echo "Course folders:"
        find "$ETMAS_DIR" -maxdepth 1 -type d -name "*-*" -printf "  %p\n" 2>/dev/null || echo "  (none)"
        echo ""
        echo "Student files:"
        find "$ETMAS_DIR" \( -name "*.fhi" -o -name "*.pt3" \) -printf "  %p\n" 2>/dev/null | head -20
        count=$(find "$ETMAS_DIR" \( -name "*.fhi" -o -name "*.pt3" \) 2>/dev/null | wc -l)
        if [[ $count -gt 20 ]]; then
            echo "  ... and $((count - 20)) more files"
        fi
        echo ""
        echo -e "${GREEN}No files were deleted. This was just a preview.${NC}"
        ;;

    2)
        # =============================================================
        # DELETE SPECIFIC COURSE
        # =============================================================
        echo "Available course folders:"
        find "$ETMAS_DIR" -maxdepth 1 -type d -name "*-*" -printf "  %f\n" 2>/dev/null
        echo ""
        read -p "Enter course folder name to delete (e.g., DD210-24J): " course

        target="$ETMAS_DIR/$course"
        if [[ -d "$target" ]]; then
            file_count=$(find "$target" -type f 2>/dev/null | wc -l)
            echo ""
            echo -e "${RED}WARNING: This will permanently delete:${NC}"
            echo "  Folder: $target"
            echo "  Files:  $file_count files"
            echo ""
            read -p "Are you sure? Type the course name to confirm: " confirm

            if [[ "$confirm" == "$course" ]]; then
                rm -rf "$target"
                echo -e "${GREEN}✓ Deleted $course${NC}"
            else
                echo "Deletion cancelled."
            fi
        else
            echo -e "${RED}Folder not found: $target${NC}"
        fi
        ;;

    3)
        # =============================================================
        # DELETE ALL DATA
        # =============================================================
        echo -e "${RED}╔═══════════════════════════════════════════════════════════╗"
        echo "║                      DANGER ZONE                          ║"
        echo "║                                                           ║"
        echo "║  This will PERMANENTLY DELETE all student data in:       ║"
        echo "║  $ETMAS_DIR"
        echo "║                                                           ║"
        echo "║  This action CANNOT be undone!                            ║"
        echo "╚═══════════════════════════════════════════════════════════╝${NC}"
        echo ""
        echo "Before continuing, confirm that you have:"
        echo "  [ ] Uploaded all marked work to the OU"
        echo "  [ ] Checked you don't need any files for appeals/queries"
        echo "  [ ] Verified the one-year retention period has passed"
        echo ""

        read -p "Have you confirmed all the above? [y/N] " -n 1 -r
        echo ""

        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            echo "Deletion cancelled."
            exit 0
        fi

        echo ""
        read -p "Type 'DELETE ALL DATA' to confirm permanent deletion: " confirm

        if [[ "$confirm" == "DELETE ALL DATA" ]]; then
            echo ""
            echo "Deleting all student data..."

            # Delete course folders
            find "$ETMAS_DIR" -maxdepth 1 -type d -name "*-*" -exec rm -rf {} \; 2>/dev/null

            # Delete returns folder
            rm -rf "$ETMAS_DIR/returns" 2>/dev/null

            # Delete any loose files
            find "$ETMAS_DIR" \( -name "*.fhi" -o -name "*.pt3" -o -name "*.doc" -o -name "*.docx" -o -name "*.odt" -o -name "*.pdf" -o -name "*.zip" \) -delete 2>/dev/null

            echo ""
            echo -e "${GREEN}✓ All student data has been deleted${NC}"
            echo ""
            echo "The folder $ETMAS_DIR still exists but is now empty."
            echo "You can safely reuse it for future courses."
        else
            echo "Deletion cancelled."
        fi
        ;;

    4)
        # =============================================================
        # DELETE RETURNS ONLY
        # =============================================================
        returns_dir="$ETMAS_DIR/returns"

        if [[ -d "$returns_dir" ]]; then
            file_count=$(find "$returns_dir" -type f 2>/dev/null | wc -l)
            echo "Returns folder: $returns_dir"
            echo "Contains: $file_count files"
            echo ""
            echo "These are zipped marked assignments ready for/already uploaded to OU."
            echo ""

            read -p "Delete returns folder? Type 'YES' to confirm: " confirm

            if [[ "$confirm" == "YES" ]]; then
                rm -rf "$returns_dir"
                echo -e "${GREEN}✓ Returns folder deleted${NC}"
            else
                echo "Deletion cancelled."
            fi
        else
            echo "No returns folder found at: $returns_dir"
        fi
        ;;

    q|Q)
        echo "Exiting without changes."
        exit 0
        ;;

    *)
        echo -e "${RED}Invalid option${NC}"
        exit 1
        ;;
esac

# =============================================================================
# FINAL REMINDER
# =============================================================================

echo ""
echo -e "${YELLOW}════════════════════════════════════════════════════════════${NC}"
echo -e "${YELLOW}REMINDER: OU Data Protection${NC}"
echo ""
echo "If you have student data older than one year, you should"
echo "run this tool periodically to stay compliant with OU policy."
echo ""
echo "For questions about data retention, contact the OU Data"
echo "Protection team or your Staff Tutor."
echo ""
