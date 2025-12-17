;;; STATE.scm — tma-mark2
;; SPDX-License-Identifier: AGPL-3.0-or-later
;; SPDX-FileCopyrightText: 2025 Jonathan D.A. Jewell

(define metadata
  '((version . "2.0.0") (updated . "2025-12-17") (project . "tma-mark2")))

(define current-position
  '((phase . "v2.0 - Security Hardening")
    (overall-completion . 40)
    (components
     ((rsr-compliance ((status . "complete") (completion . 100)))
      (scm-files ((status . "complete") (completion . 100)))
      (security-workflows ((status . "complete") (completion . 100)))
      (license-compliance ((status . "complete") (completion . 100)))
      (rescript-migration ((status . "pending") (completion . 0)))))))

(define blockers-and-issues '((critical ()) (high-priority ())))

(define critical-next-actions
  '((immediate
     (("Generate flake.lock" . medium)
      ("Publish Guix channel" . low)))
    (this-week
     (("Begin ReScript migration" . medium)
      ("Full SHA-pin audit" . low)))))

(define session-history
  '((snapshots
     ((date . "2025-12-15") (session . "initial") (notes . "SCM files added"))
     ((date . "2025-12-17") (session . "security-review") (notes . "Fixed license inconsistencies, SHA-pinned actions, CodeQL branch fix, updated roadmap")))))

(define state-summary
  '((project . "tma-mark2") (completion . 40) (blockers . 0) (updated . "2025-12-17")))
