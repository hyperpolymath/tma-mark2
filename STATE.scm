;; SPDX-License-Identifier: MIT
;; STATE.scm - AI conversation checkpoint for eTMA Handler v2
;; Format: Guile Scheme S-expressions (human-readable, minikanren queryable)

(define-module (state etma-handler)
  #:export (state))

;; =============================================================================
;; METADATA
;; =============================================================================

(define metadata
  '((format-version . "1.0.0")
    (project-name . "eTMA Handler")
    (project-version . "2.0.0-alpha")
    (repository . "hyperpolymath/tma-mark2")
    (branch . "claude/create-state-scm-01TZriL3CycG3ynp9dAkZxMA")
    (created . "2025-12-08T00:00:00Z")
    (last-modified . "2025-12-08T00:00:00Z")
    (author . "Claude (Opus 4)")))

;; =============================================================================
;; USER CONTEXT
;; =============================================================================

(define user-context
  '((name . "hyperpolymath")
    (roles . (maintainer architect open-university-tutor))
    (preferences
      (languages . (elixir scheme nix))
      (frameworks . (phoenix liveview otp))
      (tools . (just nix podman burrito))
      (documentation . asciidoc)
      (standards . (rsr-gold spdx tpcf)))
    (values
      (crash-proof . critical)
      (offline-first . critical)
      (security . high)
      (privacy . high)
      (no-telemetry . mandatory))))

;; =============================================================================
;; CURRENT POSITION
;; =============================================================================

(define current-position
  '((summary . "Core marking infrastructure complete, needs integration & testing")

    (phase . alpha)
    (completion-percentage . 65)

    (what-exists
      (fully-implemented
        (marking-ui
          (description . "Three-pane LiveView marking cockpit")
          (file . "lib/etma_handler_web/live/marking_live.ex")
          (lines . 703)
          (status . working-with-mock-data))

        (file-watcher
          (description . "Bouncer - automatic file ingestion from Downloads")
          (file . "lib/etma_handler/bouncer.ex")
          (lines . 243)
          (status . functional))

        (storage
          (description . "CubDB crash-proof append-only B-tree")
          (file . "lib/etma_handler/repo.ex")
          (lines . 260)
          (status . functional))

        (calculator
          (description . "Safe arithmetic evaluation with what-if")
          (file . "lib/etma_handler/logic/calculator.ex")
          (status . functional))

        (audit
          (description . "Quality bot - feedback validation")
          (file . "lib/etma_handler/marking/audit.ex")
          (status . functional))

        (security
          (description . "6-layer defense: network, headers, validation, isolation, crypto, integrity")
          (modules . (security_headers rate_limiter force_ssl crypto))
          (status . production-ready))

        (deployment
          (description . "Burrito binaries + OCI containers")
          (targets . (linux-x86_64 linux-arm64 linux-riscv64 macos-intel macos-arm windows))
          (status . functional))

        (documentation
          (files . (README.adoc CLAUDE.adoc GOVERNANCE.adoc CONTRIBUTING.adoc))
          (status . excellent)))

      (partially-implemented
        (parser
          (description . ".fhi XML and .docx parsing")
          (status . scaffolded-needs-logic))

        (refinery
          (description . "Comment bank editor")
          (file . "lib/etma_handler_web/live/refinery_live.ex")
          (lines . 35)
          (status . basic-scaffold))

        (settings
          (description . "Configuration interface")
          (file . "lib/etma_handler_web/live/settings_live.ex")
          (lines . 44)
          (status . basic-scaffold))

        (export
          (description . "Generate marked .docx files")
          (status . framework-exists-not-integrated))

        (course-management
          (description . "Course CRUD operations")
          (status . minimal-functionality)))

      (not-implemented
        (test-suite . "0% coverage, no test directory")
        (wasm-plugins . "Architecture designed, dependency commented out")
        (multi-tutor . "Raft consensus planned")
        (dark-mode . "v2.1 roadmap")
        (bulk-operations . "v2.1 roadmap")
        (webdav-sync . "v2.2 roadmap")
        (ai-feedback . "v3.0 roadmap")))

    (architecture-health
      (strengths
        (separation-of-concerns . "Clean OTP supervision tree")
        (fault-tolerance . "GenServer boundaries, crash-proof storage")
        (security . "Defense in depth, post-quantum ready")
        (documentation . "Comprehensive CLAUDE.adoc with diagrams"))
      (weaknesses
        (test-coverage . "Zero tests")
        (mock-data . "MarkingLive uses hardcoded mock submissions")
        (incomplete-integration . "File watcher broadcasts but storage not connected")))))

;; =============================================================================
;; ROUTE TO MVP v1
;; =============================================================================

(define mvp-v1-route
  '((goal . "Functional marking workflow: ingest -> mark -> export")

    (definition-of-done
      (can-load-fhi-files . "Parse .fhi XML from OU, extract metadata and questions")
      (can-mark-submissions . "Enter marks, feedback, per-question scores")
      (can-save-progress . "Persist to CubDB, recover after crash")
      (can-export-docx . "Generate marked .docx for student")
      (has-test-suite . "Core paths covered, >60% coverage")
      (can-build-binary . "Burrito produces working executable"))

    (phases
      (phase-1-foundation
        (name . "Storage Integration")
        (priority . critical)
        (tasks
          (wire-bouncer-to-repo
            (description . "Connect file watcher events to CubDB storage")
            (estimate . "Connect Bouncer GenServer to Repo module")
            (files . ("lib/etma_handler/bouncer.ex" "lib/etma_handler/repo.ex")))
          (replace-mock-data
            (description . "Load real submissions from CubDB in MarkingLive")
            (estimate . "Replace hardcoded mock with Repo.list_submissions/0")
            (files . ("lib/etma_handler_web/live/marking_live.ex")))
          (persist-marking-state
            (description . "Save marks/feedback to CubDB on autosave")
            (estimate . "Wire handle_event save to Repo.put_submission/2")
            (files . ("lib/etma_handler_web/live/marking_live.ex")))))

      (phase-2-parsing
        (name . "File Format Parsing")
        (priority . critical)
        (tasks
          (implement-fhi-parser
            (description . "Parse OU .fhi XML format")
            (estimate . "Extract student info, questions, max marks from XML")
            (files . ("lib/etma_handler/parser/fhi.ex"))
            (dependencies . (sweetxml)))
          (implement-docx-reader
            (description . "Read submitted .docx content")
            (estimate . "Extract text/structure from Office Open XML")
            (files . ("lib/etma_handler/parser/docx.ex")))
          (validate-submission-format
            (description . "Verify filename matches OU pattern")
            (estimate . "Already done in Bouncer, needs Repo integration"))))

      (phase-3-export
        (name . "Output Generation")
        (priority . high)
        (tasks
          (implement-docx-generator
            (description . "Create marked .docx from template")
            (estimate . "Use template with placeholders for marks/feedback")
            (files . ("lib/etma_handler/export/docx.ex")))
          (add-export-button
            (description . "UI button to generate and download marked file")
            (estimate . "LiveView component + download handler")
            (files . ("lib/etma_handler_web/live/marking_live.ex")))))

      (phase-4-testing
        (name . "Quality Assurance")
        (priority . high)
        (tasks
          (create-test-directory
            (description . "Set up test infrastructure")
            (estimate . "mkdir test, add support files")
            (files . ("test/test_helper.exs")))
          (write-unit-tests
            (description . "Test Calculator, Audit, Bouncer, Repo")
            (estimate . "Cover happy paths and edge cases")
            (files . ("test/etma_handler/*_test.exs")))
          (write-integration-tests
            (description . "Test full marking workflow")
            (estimate . "Ingest -> mark -> save -> export")
            (files . ("test/integration/*_test.exs")))
          (add-property-tests
            (description . "StreamData for Calculator, Bouncer")
            (estimate . "Fuzz validation logic")
            (dependencies . (stream_data)))))

      (phase-5-polish
        (name . "MVP Polish")
        (priority . medium)
        (tasks
          (implement-refinery
            (description . "Comment bank with drag-and-drop")
            (estimate . "CRUD for reusable comments")
            (files . ("lib/etma_handler_web/live/refinery_live.ex")))
          (implement-settings
            (description . "User preferences UI")
            (estimate . "Watch directory, min feedback words, themes")
            (files . ("lib/etma_handler_web/live/settings_live.ex")))
          (error-handling
            (description . "User-friendly error messages")
            (estimate . "Flash messages, recovery suggestions"))
          (keyboard-shortcuts
            (description . "Power user navigation")
            (estimate . "Already scaffolded, needs expansion")))))))

;; =============================================================================
;; ISSUES / BLOCKERS
;; =============================================================================

(define issues
  '((blocking
      (no-test-suite
        (severity . high)
        (description . "Zero test coverage makes refactoring dangerous")
        (impact . "Cannot confidently ship to production")
        (resolution . "Create test directory and write ExUnit tests"))

      (mock-data-only
        (severity . high)
        (description . "MarkingLive displays hardcoded mock submissions")
        (impact . "Cannot actually mark real files")
        (resolution . "Wire Bouncer -> Repo -> MarkingLive data flow"))

      (parser-not-implemented
        (severity . high)
        (description . ".fhi and .docx parsing is scaffolded but empty")
        (impact . "Cannot read OU submission files")
        (resolution . "Implement XML/docx parsing with SweetXml")))

    (non-blocking
      (wasmex-commented-out
        (severity . low)
        (description . "WASM plugin dependency disabled in mix.exs")
        (impact . "Plugin system unavailable")
        (resolution . "Post-MVP: uncomment when Wasmex stabilizes"))

      (no-dark-mode
        (severity . low)
        (description . "UI is light-only")
        (impact . "Accessibility for light-sensitive users")
        (resolution . "v2.1 roadmap item"))

      (no-ci-pipeline
        (severity . medium)
        (description . "Only CodeQL and static workflows exist")
        (impact . "No automated testing on PR")
        (resolution . "Add GitHub Actions workflow for mix test")))))

;; =============================================================================
;; QUESTIONS FOR USER
;; =============================================================================

(define questions
  '((architecture
      (q1
        (question . "Should RefinaryLive share comments globally or per-course?")
        (context . "Comment bank design affects schema and UI")
        (options . (global per-course hierarchical))
        (recommendation . "per-course with optional global favorites"))

      (q2
        (question . "Is the current 3-pane layout optimal or should it be configurable?")
        (context . "Some tutors may prefer different arrangements")
        (options . (fixed-layout configurable-layout))
        (recommendation . "fixed initially, configurable in v2.1")))

    (parsing
      (q3
        (question . "Do you have sample .fhi files I can use for parser development?")
        (context . "Need real OU format to implement correctly")
        (impact . "Parser accuracy depends on real samples"))

      (q4
        (question . "What .docx template should exported files use?")
        (context . "OU may have required formatting/branding")
        (impact . "Export compliance with OU standards")))

    (priorities
      (q5
        (question . "What is the minimum viable feature set for your first marking session?")
        (context . "MVP scoping - some features can be deferred")
        (example . "Do you need RefinaryLive before first use?"))

      (q6
        (question . "Are there specific OU deadlines driving the timeline?")
        (context . "Helps prioritize blocking work")
        (impact . "May need to cut scope for deadline")))

    (deployment
      (q7
        (question . "Target platform for first release: container, binary, or both?")
        (context . "Affects testing focus")
        (options . (container-only binary-only both))
        (recommendation . "binary-first for desktop use"))

      (q8
        (question . "Do you need Windows support in MVP or can it wait?")
        (context . "Windows adds testing complexity")
        (recommendation . "Linux/macOS first, Windows in v2.1")))))

;; =============================================================================
;; LONG-TERM ROADMAP
;; =============================================================================

(define roadmap
  '((v2.0 "MVP"
      (target . "First usable release")
      (features
        (core-marking . "Ingest, mark, save, export workflow")
        (file-watching . "Automatic ingestion from Downloads")
        (crash-proof-storage . "CubDB with Time Machine snapshots")
        (quality-audit . "Feedback validation, word count, name check")
        (calculator . "Safe arithmetic, what-if scenarios")
        (cross-platform . "Linux, macOS binaries via Burrito"))
      (technical
        (test-coverage . ">60%")
        (documentation . "Complete user guide")
        (security . "Production-hardened")))

    (v2.1 "Polish"
      (target . "Quality of life improvements")
      (features
        (dark-mode . "System preference detection + toggle")
        (bulk-operations . "Mark multiple submissions, batch export")
        (keyboard-navigation . "Full keyboard-driven workflow")
        (refinery-v2 . "Categorized comments, search, favorites")
        (settings-ui . "Full configuration interface")
        (windows-support . "Windows binary builds"))
      (technical
        (test-coverage . ">80%")
        (property-tests . "StreamData for all validators")
        (performance . "Benchmark and optimize hot paths")))

    (v2.2 "Integration"
      (target . "External system connectivity")
      (features
        (webdav-sync . "Sync with cloud storage")
        (csv-export . "Spreadsheet-compatible mark export")
        (excel-export . "Native .xlsx generation")
        (grade-statistics . "Dashboard with charts, trends")
        (backup-restore . "Explicit backup/restore UI"))
      (technical
        (api-versioning . "Stable REST API contract")
        (plugin-architecture . "WASM sandboxed extensions")))

    (v3.0 "Intelligence"
      (target . "AI-assisted marking")
      (features
        (local-llm . "On-device AI feedback suggestions")
        (plagiarism-hooks . "Turnitin/Urkund integration points")
        (smart-comments . "Context-aware comment suggestions")
        (marking-patterns . "Learn from tutor's marking style")
        (accessibility . "Screen reader optimization"))
      (technical
        (ml-pipeline . "ONNX runtime for local inference")
        (privacy-first . "All AI processing on-device")))

    (v4.0 "Collaboration"
      (target . "Multi-tutor workflows")
      (features
        (multi-tutor . "Shared marking pools")
        (raft-consensus . "Distributed consistency")
        (crdt-sync . "Offline-first collaboration")
        (audit-trail . "Complete marking history")
        (role-based-access . "Tutor, lead tutor, admin roles"))
      (technical
        (distributed-otp . "Clustered BEAM deployment")
        (post-quantum-crypto . "ML-KEM-1024, Dilithium")
        (risc-v-native . "Native RISC-V builds")))))

;; =============================================================================
;; CRITICAL NEXT ACTIONS
;; =============================================================================

(define next-actions
  '((immediate
      (action-1
        (priority . 1)
        (task . "Create test directory and write first tests for Calculator")
        (rationale . "Unblocks safe refactoring")
        (files . ("test/test_helper.exs" "test/etma_handler/logic/calculator_test.exs")))

      (action-2
        (priority . 2)
        (task . "Wire Bouncer file events to Repo storage")
        (rationale . "Enables real file ingestion")
        (files . ("lib/etma_handler/bouncer.ex" "lib/etma_handler/repo.ex")))

      (action-3
        (priority . 3)
        (task . "Replace MarkingLive mock data with Repo queries")
        (rationale . "Makes UI display real submissions")
        (files . ("lib/etma_handler_web/live/marking_live.ex"))))

    (this-week
      (action-4
        (priority . 4)
        (task . "Implement .fhi XML parser")
        (rationale . "Core requirement for OU workflow")
        (needs . "Sample .fhi files from user"))

      (action-5
        (priority . 5)
        (task . "Add CI workflow for automated testing")
        (rationale . "Prevent regressions")
        (files . (".github/workflows/test.yml"))))

    (next-week
      (action-6
        (priority . 6)
        (task . "Implement .docx export generator")
        (rationale . "Complete the marking workflow")
        (needs . "Template format decision"))

      (action-7
        (priority . 7)
        (task . "Expand RefinaryLive to full comment bank")
        (rationale . "Quality of life for frequent markers")
        (files . ("lib/etma_handler_web/live/refinery_live.ex"))))))

;; =============================================================================
;; SESSION CONTEXT (for continuity)
;; =============================================================================

(define session-context
  '((conversation-id . "create-state-scm-01TZriL3CycG3ynp9dAkZxMA")
    (messages-exchanged . 2)
    (token-status . fresh)
    (focus . "Creating STATE.scm checkpoint")
    (last-action . "Comprehensive codebase analysis complete")))

;; =============================================================================
;; EXPORT STATE
;; =============================================================================

(define state
  `((metadata . ,metadata)
    (user-context . ,user-context)
    (current-position . ,current-position)
    (mvp-route . ,mvp-v1-route)
    (issues . ,issues)
    (questions . ,questions)
    (roadmap . ,roadmap)
    (next-actions . ,next-actions)
    (session . ,session-context)))

;; EOF
