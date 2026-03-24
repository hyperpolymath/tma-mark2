# SPDX-License-Identifier: PMPL-1.0-or-later
# LLM Warmup: tma-mark2 (User Guide)

## What This Project Does

tma-mark2 (eTMA Handler) is a marking tool for Open University tutors. It
processes electronic Tutor Marked Assignment (eTMA) submissions and lets tutors
grade student work through a browser-based interface.

**Input:** `.fhi` files (student assignment submissions)
**Output:** Graded `.docx` files with feedback annotations

## Who Uses This

Open University associate lecturers (tutors) who mark student assignments.
The tool runs locally on the tutor's machine -- it works offline and never
sends student data to external servers.

## How to Get Started

```bash
# Recommended: use Nix
nix develop && mix deps.get && mix assets.setup && mix phx.server

# Or: plain Elixir
mix deps.get && mix assets.setup && mix assets.build && mix phx.server

# Or: container
just build-container && just container-dev
```

Then open http://localhost:4000.

## Core Workflow

1. Drop `.fhi` files into your Downloads folder (auto-watched)
2. Files appear in the web UI automatically
3. Use the marking grid to assign grades and write feedback per question
4. Export graded `.docx` files for return to the student

## Key Commands

| Command | What It Does |
|---------|-------------|
| `just dev` | Start dev server with live reload |
| `just test` | Run all tests (Elixir + Rust NIFs) |
| `just lint` | Check formatting and run Credo |
| `just doctor` | Diagnose environment problems |
| `just heal` | Attempt automatic repair |
| `just tour` | Guided walkthrough of the codebase |
| `just help-me` | Interactive help menu (pick what you need) |
| `just build` | Build release (Guix/Nix/Mix cascade) |
| `just build-container` | Build Chainguard container image |
| `just security-audit` | Audit deps + run Trivy scan |

## Architecture at a Glance

- **Elixir/Phoenix LiveView** -- real-time web UI, OTP supervision
- **CubDB** -- embedded crash-proof database (no PostgreSQL needed)
- **Cachex** -- in-process caching layer
- **Rust NIFs** -- cryptographic operations (Argon2, HMAC)
- **Burrito** -- single-binary distribution for all platforms
- **Bebop** -- binary serialization protocol for sync

## Important Constraints

- This is an **offline-first** application. No cloud dependency.
- Student data is **never** transmitted externally.
- The app uses **CubDB** (embedded) -- no database server required.
- Production builds target 6 platforms via Burrito (Linux x86/ARM/RISC-V,
  Windows, macOS Intel/ARM).

## File Layout

```
lib/etma_handler/       Core business logic
  fhi.ex                FHI file parser
  marking/              Grading engine
  crypto/               Security (Argon2, hashing)
  scanner.ex            Downloads folder watcher
  repo.ex               CubDB data layer
lib/etma_handler_web/   Phoenix LiveView UI
native/tma_crypto/      Rust NIF (cryptography)
config/                 Elixir app config
test/                   ExUnit tests
```

## Troubleshooting

| Symptom | Fix |
|---------|-----|
| `mix deps.get` fails on Rustler | Ensure Rust is installed: `rustup show` |
| Port 4000 in use | `PORT=4001 mix phx.server` |
| Assets not loading | `mix assets.setup && mix assets.build` |
| CubDB lock file error | Run `just heal` or remove `data/db/*.lock` |
| Everything feels broken | Run `just doctor` then `just heal` |

## What You Can Ask an LLM to Help With

- "How do I add a new marking criterion to the grid?"
- "How does the FHI parser handle malformed files?"
- "How do I configure the watch directory?"
- "What happens if CubDB crashes mid-write?"
- "How do I build a single binary for Windows?"
- "How do I run this in a container on my VPS?"

## What to Avoid Changing

- Do not modify `native/tma_crypto/` without understanding Rustler NIFs.
- Do not switch from CubDB to PostgreSQL -- the embedded design is intentional.
- Do not add cloud dependencies -- offline-first is a hard requirement.
- Do not remove SPDX headers from source files.

## License

PMPL-1.0-or-later (Palimpsest License). See LICENSE file.
