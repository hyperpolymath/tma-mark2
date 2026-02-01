;; SPDX-License-Identifier: PMPL-1.0-or-later
;; Comment Bank: Computing Fundamentals (TM129)
;;
;; This is an example of the structured comment bank format.
;; Source: Scheme (.scm) - machine-readable, versionable
;; Output: Can generate AsciiDoc (.adoc) for human-readable reference
;;
;; Usage in editor:
;;   /ref-harvard  → inserts Harvard referencing comment
;;   /code-style   → inserts code style feedback
;;   ;;ref         → shortcut expansion

(comment-bank
  (metadata
    (name "Computing Fundamentals")
    (description "Standard feedback comments for TM129 and related computing modules")
    (module "TM129")
    (level 1)
    (author "OU Computing Tutors")
    (version "1.0.0")
    (license "CC-BY-SA-4.0")
    (created "2026-01-05")
    (updated "2026-01-05"))

  ;; ============================================================
  ;; REFERENCING
  ;; ============================================================
  (category "referencing"
    (description "Comments about citation and reference formatting")
    (icon "📚")

    (comment
      (id "ref-001")
      (shortcut "ref-missing")
      (tags (academic integrity sources))
      (severity high)
      (variants
        (neutral
          "References should be included for all external sources used.")
        (encouraging
          "You've made good points - now strengthen them by adding references to support your claims.")
        (concern
          "Several statements lack supporting references. In academic work, claims must be backed by credible sources.")))

    (comment
      (id "ref-002")
      (shortcut "ref-harvard")
      (tags (formatting harvard style))
      (severity medium)
      (text "Please use Harvard referencing format consistently. In-text citations should be (Author, Year) and the reference list should be alphabetical by author surname."))

    (comment
      (id "ref-003")
      (shortcut "ref-url-only")
      (tags (web sources urls))
      (severity medium)
      (text "URLs alone are not sufficient references. Please include: author/organisation, title, date published, date accessed, and full URL."))

    (comment
      (id "ref-004")
      (shortcut "ref-wikipedia")
      (tags (wikipedia sources academic))
      (severity medium)
      (variants
        (neutral
          "Wikipedia can be useful for initial research, but academic work should cite primary sources. Consider using Wikipedia's own references as a starting point.")
        (encouraging
          "Good that you've researched the topic. Now try to find the original sources that Wikipedia itself references."))))

  ;; ============================================================
  ;; CODE QUALITY
  ;; ============================================================
  (category "code-quality"
    (description "Comments about programming and code submissions")
    (icon "💻")

    (comment
      (id "code-001")
      (shortcut "code-comments")
      (tags (documentation comments readability))
      (severity medium)
      (variants
        (neutral
          "Code should include comments explaining the purpose of functions and complex logic.")
        (encouraging
          "Your code works well. Adding comments would make it even easier to understand and maintain.")
        (concern
          "The code lacks comments, making it difficult to follow the logic. Please add explanatory comments.")))

    (comment
      (id "code-002")
      (shortcut "code-naming")
      (tags (naming conventions readability))
      (severity low)
      (text "Variable and function names should be descriptive. Instead of 'x' or 'temp', use names that explain what the variable represents, like 'userCount' or 'averageScore'."))

    (comment
      (id "code-003")
      (shortcut "code-tested")
      (tags (testing verification))
      (severity high)
      (variants
        (positive
          "Excellent - you've clearly tested your code with different inputs.")
        (neutral
          "Remember to test your code with various inputs, including edge cases.")
        (concern
          "The code doesn't appear to have been tested. Several obvious errors would have been caught with basic testing.")))

    (comment
      (id "code-004")
      (shortcut "code-dry")
      (tags (dry refactoring functions))
      (severity low)
      (text "I notice some repeated code. Consider creating a function to avoid repetition (DRY principle - Don't Repeat Yourself). This makes code easier to maintain.")))

  ;; ============================================================
  ;; STRUCTURE & PRESENTATION
  ;; ============================================================
  (category "structure"
    (description "Comments about document structure and presentation")
    (icon "📝")

    (comment
      (id "struct-001")
      (shortcut "struct-intro")
      (tags (introduction structure))
      (severity medium)
      (variants
        (positive
          "Good introduction that clearly sets out what the submission will cover.")
        (neutral
          "The introduction could more clearly outline the structure of your answer.")
        (concern
          "The submission lacks a clear introduction. Start by briefly explaining what you will discuss.")))

    (comment
      (id "struct-002")
      (shortcut "struct-conclusion")
      (tags (conclusion summary))
      (severity medium)
      (text "A brief conclusion summarising your main points would strengthen the submission. This helps demonstrate that you've addressed all aspects of the question."))

    (comment
      (id "struct-003")
      (shortcut "struct-headings")
      (tags (headings organisation))
      (severity low)
      (text "Using headings to organise your answer makes it easier to read and ensures you address all parts of the question. Consider structuring around the question's sub-parts."))

    (comment
      (id "struct-004")
      (shortcut "struct-word-count")
      (tags (length word-count))
      (severity medium)
      (variants
        (under
          "Your submission is under the word limit. This suggests some areas could be explored in more depth.")
        (over
          "Your submission exceeds the word limit. Try to be more concise - focus on the most important points.")
        (appropriate
          "Good use of the available word count."))))

  ;; ============================================================
  ;; UNDERSTANDING & ANALYSIS
  ;; ============================================================
  (category "understanding"
    (description "Comments about conceptual understanding and analysis")
    (icon "🎯")

    (comment
      (id "und-001")
      (shortcut "und-accurate")
      (tags (accuracy concepts))
      (severity high)
      (variants
        (positive
          "You demonstrate accurate understanding of the key concepts.")
        (concern
          "There are some inaccuracies in your explanation. Please review the module materials on this topic.")))

    (comment
      (id "und-002")
      (shortcut "und-deeper")
      (tags (analysis depth))
      (severity medium)
      (text "Your answer describes the topic but could go deeper. Try to explain *why* things work this way, not just *what* they are."))

    (comment
      (id "und-003")
      (shortcut "und-example")
      (tags (examples practical))
      (severity low)
      (variants
        (positive
          "Excellent use of examples to illustrate your points.")
        (neutral
          "Adding a practical example would help demonstrate your understanding.")
        (encouraging
          "You clearly understand the theory - now try to show how it applies in practice.")))

    (comment
      (id "und-004")
      (shortcut "und-critical")
      (tags (critical-thinking evaluation))
      (severity medium)
      (text "At this level, we're looking for critical evaluation, not just description. Consider: What are the limitations? What alternatives exist? Why might someone disagree?")))

  ;; ============================================================
  ;; POSITIVE FEEDBACK
  ;; ============================================================
  (category "positive"
    (description "Encouraging comments for good work")
    (icon "⭐")

    (comment
      (id "pos-001")
      (shortcut "pos-excellent")
      (tags (praise overall))
      (severity none)
      (text "Excellent work overall. You've clearly engaged with the material and demonstrated strong understanding."))

    (comment
      (id "pos-002")
      (shortcut "pos-improved")
      (tags (progress improvement))
      (severity none)
      (text "I can see real improvement from your previous submission. Well done for taking the feedback on board."))

    (comment
      (id "pos-003")
      (shortcut "pos-effort")
      (tags (effort engagement))
      (severity none)
      (text "It's clear you've put considerable effort into this submission. Keep up the good work."))

    (comment
      (id "pos-004")
      (shortcut "pos-beyond")
      (tags (initiative extra))
      (severity none)
      (text "You've gone beyond the basic requirements here, showing genuine interest in the subject. Excellent initiative.")))

  ;; ============================================================
  ;; SIGNATURES & CLOSINGS
  ;; ============================================================
  (category "closing"
    (description "Standard sign-offs and closing remarks")
    (icon "✍️")

    (comment
      (id "close-001")
      (shortcut "sig-standard")
      (tags (signature closing))
      (severity none)
      (template "Best regards,\n{tutor-name}\n{module} Tutor"))

    (comment
      (id "close-002")
      (shortcut "sig-questions")
      (tags (support help))
      (severity none)
      (text "If you have any questions about this feedback, please don't hesitate to get in touch via the module forum or email."))

    (comment
      (id "close-003")
      (shortcut "sig-next")
      (tags (encouragement next-tma))
      (severity none)
      (template "Good luck with {next-tma}! Remember the tutorials are there to help."))))

;; ============================================================
;; EXPORT CONFIGURATION
;; ============================================================
;; These settings control how the comment bank is exported

(export-config
  (formats
    (adoc
      (output-file "computing-fundamentals-reference.adoc")
      (include-shortcuts #t)
      (include-tags #t)
      (group-by category))
    (pdf
      (output-file "computing-fundamentals-quickref.pdf")
      (format "reference-card")
      (columns 2))
    (json
      (output-file "computing-fundamentals.json")
      (for-import #t))))
