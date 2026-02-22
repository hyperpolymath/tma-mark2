<!-- SPDX-License-Identifier: PMPL-1.0-or-later -->
<!-- TOPOLOGY.md — Project architecture map and completion dashboard -->
<!-- Last updated: 2026-02-19 -->

# eTMA Handler (tma-mark2) — Project Topology

## System Architecture

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
                        │ - Docx Generator      │  │ - WASM Plugin Host             │
                        └──────────┬────────────┘  └──────────┬─────────────────────┘
                                   │                          │
                                   └────────────┬─────────────┘
                                                ▼
                        ┌─────────────────────────────────────────┐
                        │             FILESYSTEM                  │
                        │  ┌───────────┐  ┌───────────┐  ┌───────┐│
                        │  │ Downloads/│  │ .docx     │  │ SQLite││
                        │  │ Ingest    │  │ Output    │  │ (Auth)││
                        │  └───────────┘  └───────────┘  └───────┘│
                        └─────────────────────────────────────────┘

                        ┌─────────────────────────────────────────┐
                        │          REPO INFRASTRUCTURE            │
                        │  Justfile Automation  .machine_readable/  │
                        │  Nix / flake.nix      0-AI-MANIFEST.a2ml  │
                        └─────────────────────────────────────────┘
```

## Completion Dashboard

```
COMPONENT                          STATUS              NOTES
─────────────────────────────────  ──────────────────  ─────────────────────────────────
CORE HANDLER (ELIXIR)
  Phoenix LiveView UI               ██████████ 100%    Real-time marking stable
  FHI/Docx Parser                   ██████████ 100%    XML ingestion verified
  File Watcher (Watcher)            ██████████ 100%    Auto-ingest verified
  Marking Logic                     ████████░░  80%    Grading grid refining

STORAGE & PACKAGING
  CubDB Integration                 ██████████ 100%    Pure Elixir storage verified
  Burrito Packaging                 ██████████ 100%    Single binary dist stable
  WASM Plugin Support               ██████░░░░  60%    Sandboxed extensions active

REPO INFRASTRUCTURE
  Justfile Automation               ██████████ 100%    Standard build/release tasks
  .machine_readable/                ██████████ 100%    STATE tracking active
  Nix Development Shell             ██████████ 100%    Reproducible env stable

─────────────────────────────────────────────────────────────────────────────
OVERALL:                            █████████░  ~90%   Production ready (v2.0.0)
```

## Key Dependencies

```
.fhi Ingest ──────► XML Parser ───────► Marking Logic ──────► .docx Export
     │                 │                   │                    │
     ▼                 ▼                   ▼                    ▼
Watcher ────────► CubDB Store ────────► LiveView UI ────────► Burrito Bin
```

## Update Protocol

This file is maintained by both humans and AI agents. When updating:

1. **After completing a component**: Change its bar and percentage
2. **After adding a component**: Add a new row in the appropriate section
3. **After architectural changes**: Update the ASCII diagram
4. **Date**: Update the `Last updated` comment at the top of this file

Progress bars use: `█` (filled) and `░` (empty), 10 characters wide.
Percentages: 0%, 10%, 20%, ... 100% (in 10% increments).
