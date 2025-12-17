;; tma-mark2 - Guix Package Definition
;; Run: guix shell -D -f guix.scm

(use-modules (guix packages)
             (guix gexp)
             (guix git-download)
             (guix build-system mix)
             ((guix licenses) #:prefix license:)
             (gnu packages base))

(define-public tma_mark2
  (package
    (name "tma-mark2")
    (version "0.1.0")
    (source (local-file "." "tma-mark2-checkout"
                        #:recursive? #t
                        #:select? (git-predicate ".")))
    (build-system mix-build-system)
    (synopsis "Elixir application")
    (description "Elixir application - part of the RSR ecosystem.")
    (home-page "https://github.com/hyperpolymath/tma-mark2")
    (license (list license:expat license:agpl3+))))  ;; MIT OR AGPL-3.0-or-later

;; Return package for guix shell
tma_mark2
