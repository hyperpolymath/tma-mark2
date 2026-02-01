;; guix.scm
;; SPDX-License-Identifier: PMPL-1.0-or-later
;;
;; GNU Guix package definition for tma-mark2
;; Primary build system - Nix is fallback
;;
;; Usage:
;;   guix build -f guix.scm
;;   guix shell -m guix.scm
;;   guix pack -f docker -S /bin=bin tma-mark2

(use-modules (guix packages)
             (guix download)
             (guix git-download)
             (guix build-system gnu)
             (guix build-system cargo)
             (guix gexp)
             ((guix licenses) #:prefix license:)
             (gnu packages)
             (gnu packages base)
             (gnu packages compression)
             (gnu packages certs)
             (gnu packages erlang)
             (gnu packages rust)
             (gnu packages rust-apps)
             (gnu packages tls)
             (gnu packages ncurses)
             (gnu packages image)
             (gnu packages pdf)
             (gnu packages fonts)
             (gnu packages security-token)
             (gnu packages linux)
             (gnu packages vpn))

(define-public tma-mark2
  (package
    (name "tma-mark2")
    (version "2.0.0")
    (source (local-file "." "tma-mark2-checkout"
              #:recursive? #t
              #:select? (git-predicate ".")))

    (build-system gnu-build-system)

    (arguments
     (list
      #:phases
      #~(modify-phases %standard-phases
          ;; No configure script
          (delete 'configure)

          ;; Set up Elixir/Erlang environment
          (add-before 'build 'setup-elixir-env
            (lambda* (#:key inputs #:allow-other-keys)
              (setenv "HOME" (getcwd))
              (setenv "MIX_ENV" "prod")
              (setenv "MIX_HOME" (string-append (getcwd) "/.mix"))
              (setenv "HEX_HOME" (string-append (getcwd) "/.hex"))
              ;; Install Hex and Rebar locally
              (invoke "mix" "local.hex" "--force")
              (invoke "mix" "local.rebar" "--force")))

          ;; Build Rust NIFs first
          (add-before 'build 'build-rust-nifs
            (lambda* (#:key inputs #:allow-other-keys)
              (when (file-exists? "native/tma_crypto/Cargo.toml")
                (invoke "cargo" "build" "--release"
                        "--manifest-path" "native/tma_crypto/Cargo.toml"))
              (when (file-exists? "native/tma_nlp/Cargo.toml")
                (invoke "cargo" "build" "--release"
                        "--manifest-path" "native/tma_nlp/Cargo.toml"))))

          ;; Build Elixir application
          (replace 'build
            (lambda _
              (invoke "mix" "deps.get" "--only" "prod")
              (invoke "mix" "deps.compile")
              (invoke "mix" "compile")
              ;; Build assets if they exist
              (when (file-exists? "assets")
                (invoke "mix" "assets.deploy"))
              (invoke "mix" "release")))

          ;; Install the release
          (replace 'install
            (lambda* (#:key outputs #:allow-other-keys)
              (let ((out (assoc-ref outputs "out")))
                (copy-recursively "_build/prod/rel/etma_handler"
                                  out))))

          ;; Skip check for now (tests need network)
          (delete 'check))))

    (native-inputs
     (list erlang
           elixir
           rust
           `(,rust "cargo")
           git))

    (inputs
     (list ;; Runtime dependencies
           nss-certs           ; TLS certificates
           openssl             ; Crypto
           ncurses             ; Terminal
           zlib                ; Compression

           ;; Security tools
           clamav              ; Virus scanning
           wireguard-tools     ; VPN
           nftables            ; Firewall

           ;; Document processing
           tesseract-ocr       ; OCR
           poppler             ; PDF handling

           ;; Fonts
           font-liberation
           font-dejavu))

    (synopsis "eTMA Handler - Open University Marking Tool")
    (description
     "A secure, offline-first tool for marking electronic Tutor Marked
Assignments (eTMAs) for Open University tutors.  Features include virus
scanning, plagiarism detection, feedback management, and post-quantum
cryptography support.")
    (home-page "https://github.com/hyperpolymath/tma-mark2")
    (license (list license:agpl3+))))

;; Development environment
(define-public tma-mark2-dev
  (package
    (inherit tma-mark2)
    (name "tma-mark2-dev")
    (arguments
     (substitute-keyword-arguments (package-arguments tma-mark2)
       ((#:phases phases)
        #~(modify-phases #$phases
            ;; Don't delete check phase in dev
            (delete 'delete-check)))))
    (native-inputs
     (modify-inputs (package-native-inputs tma-mark2)
       (prepend ;; Development tools
                erlang-ls          ; Language server
                rebar3             ; Build tool
                rust-analyzer)))))

;; Export the package
tma-mark2
