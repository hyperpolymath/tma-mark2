<!-- SPDX-License-Identifier: MPL-2.0 -->
<!-- TOPOLOGY.md — Project architecture map and completion dashboard -->
<!-- Last updated: 2026-05-24 -->

# eTMA Handler (tma-mark2) — Project Topology

## Status: alpha scaffold (~25–30%)

The previous version of this file claimed ~90% completion. That was wrong. This is now an honest dashboard. See [ROADMAP.adoc](ROADMAP.adoc) for the path to v1.

## System Architecture (intended)

```
                        ┌─────────────────────────────────────────┐
                        │              ASSOCIATE LECTURER         │
                        │        (LiveView UI / Browser)          │
                        └───────────────────┬─────────────────────┘
                                            │ HTTP / WebSocket
                                            ▼
                        ┌─────────────────────────────────────────┐
                        │           ETMA HANDLER CORE (ELIXIR)    │
                        │    (OTP Supervision, Phoenix Web)       │
                        └──────────┬───────────────────┬──────────┘
                                   │                   │
                                   ▼                   ▼
                        ┌───────────────────────┐  ┌────────────────────────────────┐
                        │ PROCESSING PIPELINE   │  │ STORAGE & SYSTEM               │
                        │ - File Watcher (.fhi) │  │ - CubDB (Embedded)             │
                        │ - XML Parser          │  │ - Burrito Packaging            │
                        │ - Docx Generator      │  │ - OS Keystore (master key)     │
                        └──────────┬────────────┘  └──────────┬─────────────────────┘
                                   │                          │
                                   └────────────┬─────────────┘
                                                ▼
                        ┌─────────────────────────────────────────┐
                        │             FILESYSTEM                  │
                        │  ┌───────────┐  ┌───────────┐  ┌───────┐│
                        │  │ Downloads/│  │ .docx     │  │ Vault ││
                        │  │ Ingest    │  │ Output    │  │ (enc) ││
                        │  └───────────┘  └───────────┘  └───────┘│
                        └─────────────────────────────────────────┘
```

## Honest Completion Dashboard

```
COMPONENT                          STATUS                NOTES
─────────────────────────────────  ────────────────────  ──────────────────────────────────────
RUST NIFs (lib + tests)
  tma_crypto (BLAKE3/XChaCha/PQ)    █████████░  ~90%     560 LOC, wired via rustler
  tma_nlp (similarity, tokens)      █████████░  ~90%     491 LOC, wired via rustler

ELIXIR CRYPTO SURFACE
  Crypto.Hybrid (KEM)               ████████░░  ~80%     Real impl, needs integration tests
  Crypto.Backend/Signatures         ███████░░░  ~70%     Real, used by Hybrid
  Crypto.WebAuthn                   █████░░░░░  ~50%     Module exists, NOT wired to router
  Crypto.EncryptedStorage           █████░░░░░  ~50%     Helpers exist, Repo not encrypted yet
  Crypto.encrypt/3 (top-level)      ░░░░░░░░░░   0%      STUB — placeholder body

DATA PLANE (BLOCKING for v1)
  Repo (CubDB)                      ░░░░░░░░░░   0%      STUB — 21-line module, no functions
  FHI parser/generator              ░░░░░░░░░░   0%      STUB — parse/1 + generate/1 unimplemented
  Bouncer (file watcher)            █░░░░░░░░░  ~10%     init/1 references undefined symbols
  Scanner / Container               ░░░░░░░░░░   0%      STUB
  .docx generator                   ░░░░░░░░░░   0%      No module, no dependency selected

MARKING DOMAIN
  Logic.Calculator                  █████████░  ~90%     Real impl, real ExUnit tests
  Marking.* (15 modules)            ░░░░░░░░░░   0%      ALL stubs (Audit, Rubric, Analyzer, …)

WEB LAYER
  Router / Endpoint / Plugs         ████████░░  ~80%     Standard Phoenix scaffold
  core_components.ex                ████████░░  ~80%     Phoenix-generated components
  MarkingLive (cockpit)             ████░░░░░░  ~40%     UI works against mock data; not wired to Repo
  SettingsLive                      ████░░░░░░  ~40%     Shell present, persistence stubbed
  RefineryLive, CourseLive.*        █░░░░░░░░░  ~10%     "Coming soon" placeholders
  ApiController /health             ███░░░░░░░  ~30%     Calls Repo.get/1 which does not exist

EXTERNAL / DEFERRED (not on v1 path)
  Scheduling.Calendar               ████░░░░░░  ~40%     689 LOC, untested, deferred to post-v1
  External.Zotero                   ████░░░░░░  ~40%     695 LOC, untested, deferred to post-v1
  Proven.SafeStateMachine           ███░░░░░░░  ~30%     Experimental, deferred
  Mobile (Tauri/Dioxus skeleton)    ██░░░░░░░░  ~15%     ~1000 LOC sketch (api/state/crypto + 8 components), unbuilt
  AffineScript frontend             █░░░░░░░░░  ~10%     25-line stub returning [0]
  Bebop / Zig FFI                   █░░░░░░░░░  ~10%     Schemas + zig main exist; not integrated

SECURITY / GOVERNANCE
  AuthN / AuthZ                     ░░░░░░░░░░   0%      No login pipeline; WebAuthn not wired
  Encryption at rest                ░░░░░░░░░░   0%      Repo writes are plaintext (Repo is stub)
  DPIA / threat model               ░░░░░░░░░░   0%      SECURITY.md is generic boilerplate
  Audit log                         ░░░░░░░░░░   0%      Not implemented

TESTING
  ExUnit suite (4 files)            ██░░░░░░░░  ~20%     calculator + crypto real; bouncer tests regex literals only
  No e2e, no property, no fuzz      ░░░░░░░░░░   0%

REPO INFRASTRUCTURE
  Justfile / guix.scm               █████████░  ~90%     Solid; not exercised in CI
  CI workflows                      ██████░░░░  ~60%     14 workflows; most are governance, not value-adding
  Containerfiles                    ██████░░░░  ~60%     Exist; never demonstrated to run the app
  Burrito release                   █░░░░░░░░░  ~10%     Configured; never actually built/run

─────────────────────────────────────────────────────────────────────────────
OVERALL (to v1 scope):              ███░░░░░░░  ~25-30%   Scaffold + partial crypto + working calculator
```

## v1 Critical Path (see ROADMAP.adoc)

```
1. Repo (CubDB) ──┐
2. FHI parser ────┼──► 3. Bouncer ──► 4. .docx gen ──► 5. MarkingLive wired ──► 6. Auth + at-rest ──► 7. Burrito release
                  │
                  └── golden tests
```

Nothing past step 1 can be honestly claimed complete until step 1 lands.

## Update Protocol

This file is maintained by both humans and AI agents. **Do not** raise a component above its actual demonstrable state — i.e. it has functions that work and are tested. Docstrings and `# ... [Implementation]` placeholders do not count as progress.

1. **After implementing a component**: change its bar and percentage
2. **After adding a component**: add a row in the appropriate section
3. **After architectural changes**: update the ASCII diagram
4. **Date**: update the `Last updated` comment at the top

Progress bars: `█` filled, `░` empty, 10 chars wide. Percentages in 10% increments.
