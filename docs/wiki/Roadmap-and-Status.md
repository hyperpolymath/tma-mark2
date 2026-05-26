# Roadmap and Status

The single source of truth for project state is the repository:

* [`TOPOLOGY.md`](https://github.com/hyperpolymath/tma-mark2/blob/main/TOPOLOGY.md) — live component-by-component completion dashboard.
* [`ROADMAP.adoc`](https://github.com/hyperpolymath/tma-mark2/blob/main/ROADMAP.adoc) — the v1 critical path and milestones.
* [`docs/status/`](https://github.com/hyperpolymath/tma-mark2/tree/main/docs/status) — test-needs, proof-needs, SEAMS review.

This wiki page is a navigational summary. If it disagrees with `TOPOLOGY.md`, trust the repository.

## Overall status

**Alpha scaffold (~30–35% of v1 scope).** A Phoenix shell, the Rust crypto/NLP NIFs, the CubDB Repo, and the marking calculator are real. The rest of the data plane and most of the UI are unimplemented or mock-wired.

## v1 critical path

```
1. Repo (CubDB)         ✓  Phase 1.1 — landed on main
2. FHI parser           ░  Phase 1.2 — blocked on real .fhi fixtures
3. Bouncer file watcher ░  Phase 1.3 — stub only
4. .docx generator      ░  No dep selected
5. MarkingLive wired    ░  Currently on mock data
6. Auth + at-rest crypt ░  WebAuthn module exists, not wired
7. Burrito release      ░  Configured but never demonstrated
```

Nothing past step 1 can be honestly claimed complete until step 1 lands on `main` (it has). Step 2 needs ≥ 5 anonymised `.fhi` samples in `test/fixtures/fhi/`.

## What's solid

| Surface | Status | Notes |
|---------|--------|-------|
| Rust NIFs (`tma_crypto`, `tma_nlp`) | ~90% | 1051 LOC, wired through Rustler |
| `Crypto.Hybrid` (KEM) | ~80% | Needs integration tests |
| `Logic.Calculator` | ~90% | Real ExUnit tests, used by UI |
| `Repo` (CubDB) | ~70% | 280 LOC + 237 LOC tests; encryption-at-rest still TODO |
| Phoenix scaffold | ~80% | Router, endpoint, components |
| Repo infra (`Justfile`, `guix.scm`) | ~90% | Solid; not exercised in CI |

## What's a stub

| Surface | Status | Notes |
|---------|--------|-------|
| FHI parser / generator | 0% | Phase 1.2 |
| Bouncer ingestion pipeline | 20% | `init/1` compiles cleanly with a loud-fail stub; pipeline unimplemented |
| Scanner / Container | 0% | |
| `.docx` generator | 0% | No dependency selected |
| 15 `Marking.*` modules | 0% | All docstring stubs |
| AuthN / AuthZ | 0% | No login pipeline |
| Encryption at rest | 0% | Repo writes plaintext |
| DPIA / threat model | 0% | `SECURITY.md` is generic boilerplate |
| Audit log | 0% | |

## Deferred (post-v1)

`Scheduling.Calendar`, `External.Zotero`, `Proven.SafeStateMachine`, mobile (Tauri/Dioxus skeleton), AffineScript frontend, Bebop/Zig FFI. See [`experiments/`](https://github.com/hyperpolymath/tma-mark2/tree/main/experiments).
