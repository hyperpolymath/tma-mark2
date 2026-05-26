# Architecture

The architecture documents live in [`docs/architecture/`](https://github.com/hyperpolymath/tma-mark2/tree/main/docs/architecture). This page is a navigational map.

## System-level

* [`docs/architecture/overview.adoc`](https://github.com/hyperpolymath/tma-mark2/blob/main/docs/architecture/overview.adoc) — high-level overview.
* [`docs/architecture/decisions.adoc`](https://github.com/hyperpolymath/tma-mark2/blob/main/docs/architecture/decisions.adoc) — architectural decision records.
* [`TOPOLOGY.md`](https://github.com/hyperpolymath/tma-mark2/blob/main/TOPOLOGY.md) — current component completion dashboard.

## Governance frameworks

Three governance frameworks track different accountability dimensions:

* [`docs/architecture/maa.adoc`](https://github.com/hyperpolymath/tma-mark2/blob/main/docs/architecture/maa.adoc) — **MAA** (Mission, Authority, Accountability)
* [`docs/architecture/rmr.adoc`](https://github.com/hyperpolymath/tma-mark2/blob/main/docs/architecture/rmr.adoc) — **RMR** (Risk, Mitigation, Recovery)
* [`docs/architecture/rmo.adoc`](https://github.com/hyperpolymath/tma-mark2/blob/main/docs/architecture/rmo.adoc) — **RMO** (Roles, Mandates, Operations)

## Top-level shape

```
                   ┌──────────────────────────────────┐
                   │ Tutor (browser, LiveView UI)     │
                   └────────────────┬─────────────────┘
                                    │ HTTP / WebSocket
                                    ▼
                   ┌──────────────────────────────────┐
                   │  EtmaHandler (Elixir / OTP)      │
                   │  - Application supervision tree  │
                   │  - Phoenix endpoint              │
                   └────┬─────────────────────────────┘
                        │
        ┌───────────────┼────────────────────────────┐
        ▼               ▼                            ▼
  ┌──────────┐   ┌──────────────┐          ┌─────────────────┐
  │ Bouncer  │   │  Repo        │          │ Native (Rust)   │
  │ watcher  │   │  (CubDB)     │          │ tma_crypto      │
  └────┬─────┘   └──────┬───────┘          │ tma_nlp         │
       │                │                  └─────────────────┘
       ▼                ▼
  ┌─────────────────────────────┐
  │ Filesystem                  │
  │ ~/Downloads · vault · .docx │
  └─────────────────────────────┘
```

The Bouncer pipeline (DETECT → SCAN → QUARANTINE → PACKAGE) is the planned ingestion flow; only the constant set is implemented today.

## Specs that pin the architecture

* [`docs/specs/fhi-schema.adoc`](https://github.com/hyperpolymath/tma-mark2/blob/main/docs/specs/fhi-schema.adoc) — `.fhi` file format.
* [`docs/specs/integrity.adoc`](https://github.com/hyperpolymath/tma-mark2/blob/main/docs/specs/integrity.adoc) — submission integrity guarantees (hash chains, signatures).
