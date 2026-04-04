# TEST-NEEDS.md — tma-mark2

## CRG Grade: C — ACHIEVED 2026-04-04

## Current Test State

| Category | Count | Notes |
|----------|-------|-------|
| Zig FFI tests | 2 | Root level + etma-handler |
| ExUnit tests (Elixir) | 5 | `test/etma_handler/{bouncer,calculator,crypto,nlp}_test.exs` |
| Test config | Present | `config/test.exs` |
| Additional tests dir | Present | `tests/` (structure TBD) |

## What's Covered

- [x] Zig FFI integration tests
- [x] ETMA handler unit tests
- [x] Bouncer logic tests
- [x] Cryptographic function tests
- [x] NLP processing tests
- [x] Calculator operation tests

## Still Missing (for CRG B+)

- [ ] Property-based crypto testing
- [ ] Fuzzing for NLP input
- [ ] End-to-end ETMA flow tests
- [ ] Performance benchmarks
- [ ] Load testing for handlers

## Run Tests

```bash
cd /var/mnt/eclipse/repos/tma-mark2 && mix test
```
