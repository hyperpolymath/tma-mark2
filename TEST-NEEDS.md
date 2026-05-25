# TEST-NEEDS.md — tma-mark2

## Status: alpha — minimal coverage

The previous version of this file claimed "CRG Grade C ACHIEVED 2026-04-04". That claim was unsupported. This file is now an honest accounting.

## Current ExUnit Suite (4 files)

| File | What it actually tests | Real? |
|------|------------------------|-------|
| `test/etma_handler/calculator_test.exs` | `Logic.Calculator` evaluate / what-if / percentage | **Yes** |
| `test/etma_handler/crypto_test.exs` | BLAKE3 hash, encrypt/decrypt round-trip via NIF | **Yes** |
| `test/etma_handler/bouncer_test.exs` | Regex literal pattern matching only — does **not** exercise the GenServer (which is broken) | Cosmetic |
| `test/etma_handler/nlp_test.exs` | NLP NIF wrappers | Partial |

`test/fuzz/` exists but has no payload yet.

## Rust NIF Tests

Both `native/tma_crypto` and `native/tma_nlp` have their own Cargo test suites — these are real and pass under `cargo test`.

## Not Tested (the long list)

### Data plane — blocked on implementation
- [ ] `EtmaHandler.Repo` — CRUD, snapshots, restore, durability under kill
- [ ] `EtmaHandler.FHI.parse/1` — needs ≥5 real `.fhi` fixtures
- [ ] `EtmaHandler.FHI.generate/1` — golden, byte-identical round-trip
- [ ] `EtmaHandler.Bouncer` — file watcher → Repo write integration
- [ ] `.docx` generator — output validity in LibreOffice / Word

### Web layer
- [ ] `MarkingLive` — LiveView mount, select, save, history
- [ ] `ApiController.health` — currently calls undefined `Repo.get/1`
- [ ] Router pipelines (auth, CSRF, secure headers)

### Crypto integration
- [ ] `Crypto.Hybrid` end-to-end KEM round-trip
- [ ] `Crypto.EncryptedStorage` against Repo
- [ ] `Crypto.WebAuthn` registration / authentication flow

### Marking domain
- All 15 modules under `lib/etma_handler/marking/` are stubs; no tests possible until implemented.

### Property / fuzz
- [ ] Property tests on Calculator (associativity, max-score bounds)
- [ ] Property tests on FHI round-trip
- [ ] Fuzz `EtmaHandler.NLP` for malformed UTF-8
- [ ] Fuzz `EtmaHandler.FHI.parse/1` for malformed XML

### End-to-end
- [ ] Ingest → parse → mark → export, with a real `.fhi`
- [ ] Crash recovery: kill mid-write, restart, verify no corruption

## Test target for v1

| Area | Target |
|------|--------|
| Line coverage (`mix test --cover`) | ≥70% |
| Repo durability tests | Pass under kill-and-restart |
| FHI golden tests | Byte-identical round-trip on ≥5 real fixtures |
| End-to-end test | Ingest → mark → export → reopen output |

## Run Tests

```bash
mix deps.get
mix test
```

For NIF crates:

```bash
cargo test --manifest-path native/tma_crypto/Cargo.toml
cargo test --manifest-path native/tma_nlp/Cargo.toml
```
