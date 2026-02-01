;; SPDX-License-Identifier: PMPL-1.0-or-later
;; Session Learning Capture - 2026-01-06
;; eTMA Marking with Corrected Comment Style

(session
  (date "2026-01-06")
  (context "Marking E225 and DD210 eTMAs with corrected comment approach")

  (marking-style-requirements
    (rule "british-english"
      (description "Use British spelling: analyse, behaviour, etc."))

    (rule "no-labels"
      (description "No POSITIVE:/IMPROVE: labels - natural prose only")
      (reason "Seen as patronising"))

    (rule "surgical-targeting"
      (description "One comment = one specific piece of text = one point")
      (bad-example "Whole paragraph with battery of issues in one comment")
      (good-example "Target exact text where change makes difference"))

    (rule "whole-unit-comments-when-appropriate"
      (description "Whole-paragraph comments OK for structural units")
      (examples "Introduction structure, conclusion logic, Marr three-layer approach"))

    (rule "direct-pedagogy"
      (description "What → Why → What to do (not questions)")
      (bad-example "What methods did these studies use?")
      (good-example "The citation format is inconsistent - use parentheses throughout"))

    (rule "three-levels"
      (level-1 "Simple fixes" "Barr not Bars")
      (level-2 "Explain the problem" "Non-unique citations need a/b/c suffixes")
      (level-3 "Sophisticated" "Secondary citations need page numbers"))

    (rule "practical-focus"
      (description "Focus on writing efficiency over philosophical depth")
      (reason "Time is short, essay is behind them, build transferable skills")))

  (problems-and-solutions
    (problem "POSITIVE:/IMPROVE: format in AI-generated comments"
      (symptoms "Comments started with labels, batched multiple issues")
      (solution "Rewrote comments.xml with clean surgical comments")
      (files-affected
        "E225-25J/01/ah32757/1/AH_AatifaHussain...docx"
        "E225-25J/01/zt971869/1/RM_E225_TMA01...docx"))

    (problem "Unescaped ampersands in FHI files"
      (symptoms "XML parser error: xmlParseEntityRef: no name")
      (cause "Text like 'Barr & Hayne' not escaped as 'Barr &amp; Hayne'")
      (solution "Edit FHI to escape & as &amp;")
      (files-affected "E225-25J/01/hd3393/1/E225-25J-01-1-hd3393.fhi"))

    (problem "Image-based PDF submission (gg4764)"
      (symptoms "pdftotext returned empty, pdftohtml showed only PNG images")
      (cause "Student submitted scanned/image-converted PDF, not Word")
      (attempted-solutions
        "LibreOffice PDF import - failed (Draw document error)"
        "pandoc - not installed"
        "pdf2docx - not installed"
        "tesseract OCR - not installed")
      (final-solution "Added submission format note to FHI file")
      (learning "Some PDFs are image-based and require OCR for text extraction"))

    (problem "Empty/orphan comment markers in document.xml"
      (symptoms "Comment IDs in document.xml don't match comments.xml")
      (solution "Remove orphan markers with sed or strip all and rebuild"))

    (problem "Repetitive comments in Rachael's file"
      (symptoms "Same 1-2-3 introduction guidance repeated twice")
      (solution "Condensed to single clean comment")))

  (validation-protocol
    (name "7/44")
    (tool "mark2-integrity validate")
    (checks
      "ZIP integrity"
      "Macro check"
      "XML validation for all component files"
      "File size sanity")
    (rule "Always validate AFTER making changes, before moving to next file"))

  (files-processed
    (module "E225-25J"
      (tma "01")
      (students
        (student "ah32757" "Aatifa Hussain"
          (docx "revised-comments" "valid")
          (fhi "valid"))
        (student "hd3393" "Holly Stoyan"
          (docx "already-correct" "valid")
          (fhi "fixed-ampersand" "valid"))
        (student "zt971869" "Rachael Mortimer"
          (docx "cleaned-comments" "valid")
          (fhi "valid"))))

    (module "DD210-25J"
      (tma "01")
      (students
        (student "aadw2" (docx "valid") (fhi "valid"))
        (student "ab24633" (docx "valid") (fhi "valid"))
        (student "gg4764" (pdf "image-based-cannot-convert") (fhi "added-format-note" "valid"))
        (student "rr3936" (docx "valid") (fhi "valid"))
        (student "zs168643" (docx "valid") (fhi "valid"))
        (student "zt141266" (docx "valid") (fhi "valid"))
        (student "zu34438" (docx "valid") (fhi "valid"))
        (student "zv314328" (docx "valid") (fhi "valid"))
        (student "zv422328" (docx "valid") (fhi "valid"))
        (student "zv790452" (docx "valid") (fhi "valid"))
        (student "zx378951" (docx "valid") (fhi "valid"))
        (student "zx962148" (docx "valid") (fhi "valid")))
      (tma "02")
      (students
        (student "zu34438" (docx "pristine-unmarked" "valid") (fhi "valid"))
        (student "zu539395" (docx "pristine-unmarked" "valid") (fhi "valid")))))

  (docx-comment-injection
    (required-modifications
      "[Content_Types].xml - register comments content type"
      "word/_rels/document.xml.rels - add relationship"
      "word/comments.xml - create with comment content"
      "word/document.xml - insert commentRangeStart/End markers")

    (xml-escaping
      (ampersand "&" "&amp;")
      (less-than "<" "&lt;")
      (greater-than ">" "&gt;")
      (quote "\"" "&quot;")
      (apostrophe "'" "&apos;"))

    (forensic-signatures
      (description "Modifications are detectable via:")
      (signatures
        "File size increase"
        "ZIP timestamp bifurcation (original vs modified files)"
        "Metadata inconsistency (core.xml modified date unchanged)"
        "Missing Word signatures (rsid attributes)"
        "Sequential relationship IDs")))

  (tools-used
    (mark2-integrity "Chain of custody and validation")
    (xmllint "XML validation")
    (unzip "DOCX extraction")
    (zip "DOCX repackaging")
    (sed "XML text manipulation")
    (pdftotext "PDF text extraction (failed on image PDF)")
    (pdftohtml "PDF structure analysis")
    (libreoffice-flatpak "PDF conversion attempts")))
