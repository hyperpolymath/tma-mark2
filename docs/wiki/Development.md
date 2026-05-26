# Development

## Read first

* [`CONTRIBUTING.md`](https://github.com/hyperpolymath/tma-mark2/blob/main/CONTRIBUTING.md) — contribution workflow, perimeter rules, what gets reviewed.
* [`CODE_OF_CONDUCT.adoc`](https://github.com/hyperpolymath/tma-mark2/blob/main/CODE_OF_CONDUCT.adoc) — Contributor Covenant.
* [`.claude/CLAUDE.md`](https://github.com/hyperpolymath/tma-mark2/blob/main/.claude/CLAUDE.md) — language policy (which languages are allowed and which are banned).

## Language policy in one paragraph

Elixir is the application language. Rust is for the NIFs in `native/`. ReScript will be the frontend language (planned, not yet wired). Nickel holds structural contracts under `must/`. Bash is allowed for scripts. **TypeScript, Node, npm, Python, Go, Kotlin, Swift, Flutter are banned.** Mobile, if ever revived, goes through Tauri 2.0 or Dioxus.

## Repository layout (top level)

```
tma-mark2/
├── lib/                    # Elixir source
├── test/                   # ExUnit tests (incl. test/fuzz/)
├── native/                 # Rust NIFs
├── config/                 # Elixir build/runtime configuration
├── must/                   # Nickel structural contracts
├── docs/                   # All long-form documentation
│   ├── architecture/  ai/  examples/  operations/
│   ├── research/      sessions/  specs/  status/
├── experiments/            # Post-v1 sketches (mobile, AffineScript, …)
├── .machine_readable/      # Project state, ecosystem manifests, agent instructions
├── .well-known/            # RFC metadata (security.txt, ai.txt, humans.txt)
├── Justfile                # Task runner
├── guix.scm                # Guix manifest
├── mix.exs                 # Elixir project definition
└── Containerfile{,.hardened}
```

## Daily loops

```bash
just dev                       # phx.server
just test                      # ExUnit
just test path/to/file.exs     # targeted
just quality                   # format + lint + test
just ci                        # full CI pipeline locally
```

## Status reports to update

When you change a surface, also bump:

* [`TOPOLOGY.md`](https://github.com/hyperpolymath/tma-mark2/blob/main/TOPOLOGY.md) — completion dashboard.
* [`CHANGELOG.adoc`](https://github.com/hyperpolymath/tma-mark2/blob/main/CHANGELOG.adoc) — under `[Unreleased]`.
* The relevant file under [`docs/status/`](https://github.com/hyperpolymath/tma-mark2/tree/main/docs/status) if you fix a known gap.

## Testing philosophy

ExUnit is the primary harness. Property tests, fuzz tests, and golden tests are tracked in [`docs/status/test-needs.md`](https://github.com/hyperpolymath/tma-mark2/blob/main/docs/status/test-needs.md) — many slots are not yet filled. New code that talks to the data plane should ship with at least:

1. Happy path unit test
2. One adversarial input
3. Round-trip if the code serialises

## SPDX headers

Every source file carries `SPDX-License-Identifier: MPL-2.0`. CI enforces this.
