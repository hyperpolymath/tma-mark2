# Getting Started

> The application is in early-stage scaffold. The end-to-end marking flow is not yet wired up — see [Roadmap and Status](Roadmap-and-Status). What follows lets you compile, run the test suite, and bring up the Phoenix shell.

## Audiences

| You are a… | Start with |
|------------|------------|
| End user (tutor) | [`QUICKSTART-USER.adoc`](https://github.com/hyperpolymath/tma-mark2/blob/main/QUICKSTART-USER.adoc) |
| Developer | [`QUICKSTART-DEV.adoc`](https://github.com/hyperpolymath/tma-mark2/blob/main/QUICKSTART-DEV.adoc) |
| Maintainer cutting a release | [`QUICKSTART-MAINTAINER.adoc`](https://github.com/hyperpolymath/tma-mark2/blob/main/QUICKSTART-MAINTAINER.adoc) |

## Prerequisites

* Elixir 1.18+ with OTP 25 (pinned in `.tool-versions`)
* Rust + Cargo (for the native NIFs in `native/`)
* Node.js 18+ (for Phoenix assets)
* Optionally: GNU Guix for reproducible builds (`guix.scm`)

The simplest install uses [asdf](https://asdf-vm.com/):

```bash
asdf install
mix deps.get
mix assets.setup
mix assets.build
mix phx.server
```

Then open <http://localhost:4000>.

## Just recipes

The project uses [`just`](https://github.com/casey/just) as a thin task runner over `mix`. Common recipes:

```bash
just dev         # development server
just test        # full ExUnit suite
just build       # Guix → Mix cascade
just info        # project info
just            # list all recipes (about 70)
```

## What to expect

The Phoenix endpoint comes up and serves a shell UI. The data plane (FHI parser, file watcher, .docx generator) is not wired through yet — you can compile, test, and load LiveView pages, but you cannot mark a real assignment end-to-end.

For a component-by-component status, see [Roadmap and Status](Roadmap-and-Status).
