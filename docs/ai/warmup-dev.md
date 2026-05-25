# SPDX-License-Identifier: MPL-2.0
# LLM Warmup: tma-mark2 (Developer Guide)

## Project Identity

**Name:** tma-mark2 (eTMA Handler Mark 2)
**Type:** Desktop web application (Phoenix LiveView)
**Primary language:** Elixir 1.17+ / OTP 27+
**Secondary languages:** Rust (NIFs), ReScript (frontend components), Nickel (config)
**License:** MPL-2.0
**Author:** Jonathan D.A. Jewell <j.d.a.jewell@open.ac.uk>

## What This Does

A marking tool for Open University tutors that:
1. Watches a directory for `.fhi` files (student assignment submissions)
2. Parses FHI XML into structured marking data
3. Presents a LiveView marking interface (grades, feedback, rubrics)
4. Exports graded `.docx` files for return to students
5. Runs entirely offline with embedded CubDB storage

## Architecture

### Supervision Tree

```
EtmaHandler.Application
  +-- EtmaHandler.Repo (CubDB)
  +-- EtmaHandler.Scanner (FileSystem watcher)
  +-- EtmaHandler.Bouncer (rate limiting / auth)
  +-- Cachex (in-process caching)
  +-- Phoenix.PubSub
  +-- EtmaHandlerWeb.Endpoint (Bandit HTTP)
  +-- Phoenix.LiveDashboard
```

### Data Flow

```
.fhi file -> Scanner -> FHI Parser -> CubDB -> LiveView UI -> .docx export
```

### Key Modules

| Module | Purpose |
|--------|---------|
| `EtmaHandler.Fhi` | Parses `.fhi` XML files using SweetXml |
| `EtmaHandler.Marking.*` | Grading logic, rubrics, score calculations |
| `EtmaHandler.Repo` | CubDB wrapper -- append-only B-tree storage |
| `EtmaHandler.Scanner` | FileSystem watcher for auto-ingestion |
| `EtmaHandler.Crypto` | Argon2id hashing, HMAC via Rust NIF |
| `EtmaHandler.Security` | Auth, session management, SDP |
| `EtmaHandler.Bouncer` | Rate limiting and access control |
| `EtmaHandler.Logic.*` | Guesswork-based learning rules engine |
| `EtmaHandler.Scheduling` | Assignment deadline tracking |
| `EtmaHandlerWeb.*` | Phoenix controllers, LiveView components |

### Rust NIFs

Located in `native/tma_crypto/`:
- Argon2id password hashing (memory-hard, side-channel resistant)
- HMAC-SHA256 for data integrity
- Built via Rustler, loaded at BEAM startup

### ReScript

`src/App.res` and `src/Phoenix.res` provide type-safe frontend components
that compile to JavaScript for use in LiveView hooks.

### Bebop Protocol

`bebop/schemas/*.bop` define binary serialization schemas used for sync
operations. Generated code lands in `lib/etma_handler/bebop/generated/`.

## Build System

### Primary: Guix

```bash
guix build -f guix.scm
guix shell -m guix.scm   # dev shell
```

### Fallback: Mix

```bash
mix deps.get && mix compile && mix release
```

### Containers

Uses Chainguard Wolfi base images via `Containerfile.hardened`:
- Multi-stage build (build stage + minimal runtime)
- `--cap-drop ALL`, `--read-only`, `--no-new-privileges`
- Nonroot user (UID 65532)
- Multi-arch: linux/amd64, linux/arm64, linux/riscv64

### Cross-Platform Binaries

Burrito wraps the BEAM release into a single executable for:
- Linux x86_64, aarch64, riscv64
- Windows x86_64
- macOS x86_64, aarch64

## Testing

```bash
just test              # ExUnit + Rust NIF tests
just test-coverage     # ExUnit with --cover
just test-integration  # only integration-tagged tests
```

Test files live in `test/` and follow ExUnit conventions. The `test/support/`
directory provides shared fixtures (sample `.fhi` files, mock data).

## Configuration

Nickel type-safe config in `must/must.ncl` defines:
- Required files that must exist
- Banned files that must not exist
- Structural invariants

Elixir config in `config/`:
- `config.exs` -- base config
- `dev.exs` -- dev server settings
- `test.exs` -- test isolation
- `runtime.exs` -- production runtime config

## Security Model

1. **No cloud dependency** -- all data stays local
2. **Argon2id** for any stored credentials (memory-hard)
3. **CubDB** -- append-only, crash-proof, no SQL injection surface
4. **FileSystem watcher** -- only watches configured directories
5. **Deno permissions** -- ReScript frontend runs in Deno sandbox during dev
6. **Container hardening** -- read-only fs, dropped caps, nonroot

## Development Workflow

```bash
# Daily cycle
just dev                  # start Phoenix server with live reload
just test                 # run tests
just lint                 # credo --strict + mix format --check-formatted
just format               # auto-format Elixir + Rust
just assail               # panic-attacker pre-commit scan

# Diagnostics
just doctor               # check all tools and files
just heal                 # auto-fix CubDB locks, missing deps, assets
just tour                 # guided codebase walkthrough
just help-me              # interactive help menu

# Before release
just must-validate-strict # verify all contractile invariants
just security-audit       # mix deps.audit + cargo audit + trivy
just sbom-generate        # produce SBOM
just release 2.1.0        # tag, test, build, sign
```

## Dependencies

### Elixir (mix.exs)

| Dep | Purpose |
|-----|---------|
| phoenix, phoenix_live_view | Web framework + real-time UI |
| bandit | HTTP server (Thousand Island) |
| cubdb | Embedded B-tree database |
| cachex | In-process cache |
| rustler | Rust NIF bridge |
| sweet_xml | FHI XML parsing |
| timex | Date parsing for FHI timestamps |
| argon2_elixir | Password hashing |
| req, mint, castore | HTTP client for OU API calls |
| file_system | Directory watching |
| guesswork | Logic programming rules |
| cbor | Binary serialization |
| burrito | Cross-platform single-binary packaging |
| ecto | Validation (schemaless changesets) |

### Rust (native/tma_crypto)

- Argon2 crate for password hashing
- Ring for HMAC operations
- Rustler for Erlang NIF bridge

## Contractile System

| File | Role |
|------|------|
| `contractiles/must/Mustfile` | Hard invariants (files that must/must-not exist) |
| `.machine_readable/MUST.contractile` | Structural invariants |
| `.machine_readable/TRUST.contractile` | AI permission boundaries |
| `.machine_readable/INTENT.contractile` | Project purpose and anti-purpose |
| `.machine_readable/ADJUST.contractile` | Accessibility + i18n requirements |

## Machine-Readable Metadata

All in `.machine_readable/6a2/`:
- `STATE.a2ml` -- current progress, blockers, next actions
- `META.a2ml` -- architecture decisions, ADRs
- `ECOSYSTEM.a2ml` -- position in hyperpolymath ecosystem
- `AGENTIC.a2ml` -- AI agent interaction patterns
- `NEUROSYM.a2ml` -- neurosymbolic integration config
- `PLAYBOOK.a2ml` -- operational runbook

## AI Manifest

Read `0-AI-MANIFEST.a2ml` first. It defines:
- **Context tiers** (tier-0: orientation, tier-1: development, tier-2: deep-dive)
- **Canonical file locations** (where things live)
- **Critical invariants** (what must never be violated)
- **Lifecycle hooks** (what to do on session enter/exit)

## Common Pitfalls

1. **Rustler compilation** -- needs Rust nightly + BEAM headers. If it fails,
   check `rustup show` and ensure `ERLANG_HOME` is set.
2. **CubDB lock** -- if the server crashes, a stale lock file may remain.
   Run `just heal` or delete `data/db/*.lock` to recover.
3. **Asset compilation** -- Phoenix requires esbuild and tailwind. Run
   `mix assets.setup` if styles are missing.
4. **Burrito builds** -- require Zig for cross-compilation. Not needed for
   local development.
5. **FHI format** -- proprietary OU format. Test files in `test/support/`.
   Real student data must never be committed.

## Ecosystem Position

- **Standalone repo** (not in a monorepo)
- **Related:** VeriSimDB (potential future backing store), proven (Idris2 formal
  verification for crypto primitives), panic-attacker (pre-commit security),
  polyglot-i18n (i18n framework -- see ADJUST.contractile for cross-reference)
- **Downstream:** Used by OU tutors for assignment marking

## What Not to Do

- Do not replace CubDB with PostgreSQL/SQLite -- embedded is the design
- Do not add network-dependent features -- offline-first is mandatory
- Do not commit real student `.fhi` files -- use test fixtures only
- Do not remove Burrito packaging -- cross-platform distribution is essential
- Do not weaken crypto (no MD5/SHA1, no `believe_me`, no `unsafe` without comment)
- Do not introduce TypeScript/Python/Go -- see language policy in CLAUDE.md

## License

MPL-2.0 (Palimpsest License). Copyright 2025-2026 Jonathan D.A. Jewell.
