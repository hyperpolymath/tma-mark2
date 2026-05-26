# RSR Compliance

The authoritative compliance state lives in [`RSR_COMPLIANCE.adoc`](https://github.com/hyperpolymath/tma-mark2/blob/main/RSR_COMPLIANCE.adoc) at the repo root. This wiki page is a summary.

## Summary

| Tier | Status |
|------|--------|
| RSR required files | ✓ pass |
| Language policy | ✓ pass (Elixir tier 1; ReScript planned) |
| Build system | ✓ pass (Guix primary, Mix fallback) |
| Container | ✓ pass (`Containerfile` + `Containerfile.hardened`) |
| `.well-known/` metadata | ✓ pass |
| License + SPDX | ✓ pass (MPL-2.0 across the tree) |
| Documentation taxonomy | ✓ pass (`docs/{architecture,specs,…}`) |
| State tracking | partial — uses `.machine_readable/6a2/STATE.a2ml` rather than a root-level `STATE.scm` |

## Intentional deviations from the canonical RSR template

* Source dir is `lib/`, not `src/` — Elixir convention.
* Test dir is `test/`, not `tests/` — Elixir convention.
* State lives at `.machine_readable/6a2/STATE.a2ml` rather than a root `STATE.scm`. This keeps STATE next to its peers (META, ECOSYSTEM, NEUROSYM, PLAYBOOK, AGENTIC).

## Reference

* Template definition: [`docs/research/rsr-template.adoc`](https://github.com/hyperpolymath/tma-mark2/blob/main/docs/research/rsr-template.adoc)
* Upstream standard: <https://rhodium.sh>
