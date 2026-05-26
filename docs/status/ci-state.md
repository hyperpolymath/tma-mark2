<!-- SPDX-License-Identifier: MPL-2.0 -->

# CI state on PR #41 (`claude/post-phase-1-1-cleanup`)

PR: <https://github.com/hyperpolymath/tma-mark2/pull/41>
Head SHA: `5b6a3c4`-ish (whatever is at HEAD when this file is read)
Captured: 2026-05-26

This file is a snapshot of the CI investigation done at the end of the
post-Phase-1.1 cleanup session, so that the open red checks have
context next time someone (human or agent) opens the PR.

## Check status

| Check | Conclusion | Disposition |
|-------|------------|-------------|
| CodeQL | ✓ success | |
| Hypatia | ✓ success | |
| governance / Security policy checks | ✓ success | |
| governance / Guix primary / Nix fallback policy | ✓ success | |
| governance / Code quality + docs | ✓ success | |
| governance / Well-Known (RFC 9116 + RSR) | ✓ success | |
| governance / Workflow security linter | ✓ success | |
| governance / Language / package anti-pattern policy | ✗ failure | Pre-existing — reusable workflow lives in `hyperpolymath/standards` and is out of scope for this PR. |
| trufflehog | ✓ success | |
| gitleaks | ✓ success | |
| rust-secrets | ✓ success | |
| analyze (CodeQL js-ts) | ✓ success | |
| Hypatia Neurosymbolic Analysis | ✓ success | |
| `test` (×2, elixir-ci + duplicate) | ✗ failure | Fails in 1–2 s — likely setup/compile step. Cannot reproduce locally: no Elixir toolchain in the remote-execution container. |
| `build` (×2, rescript-deno-ci + duplicate) | ✗ failure | Fails in 7–8 s — likely `deno fmt --check` on the new markdown files under `docs/wiki/`. No `deno.json` ignore section to bypass. |
| `check` (×2) | ✓ success | |
| `security` (×2) | ✓ success | |

The `(×2)` jobs run twice because `elixir-ci.yml` and
`rescript-deno-ci.yml` both use `on: [push, pull_request]`, so each
SHA triggers both event types.

## Bot findings

The Hypatia bot reported 98 findings on the PR. The breakdown:

* **3 critical** — all pre-existing in `main`:
  * `Code.eval_*` use in `lib/etma_handler/logic/calculator.ex`
  * "Secret found" pattern in `lib/etma_handler/crypto/encrypted_storage.ex:36`
  * "Secret found" pattern in `lib/etma_handler/crypto/suite.ex:266`
* **58 high** — mostly `expect()` in Rust hot paths (`native/tma_crypto/src/lib.rs`),
  `binary_to_term` without `:safe`, and the `setup.sh` curl-pipe pattern.
  All pre-existing in `main`.
* **37 medium** — one was actionable in this PR:
  * `docs/examples/nickel-config-example.ncl` missing SPDX header —
    introduced by moving the file out of `configs/`. **Fixed in this PR.**
  * The rest are pre-existing (CodeQL language-matrix issue, etc.).

## What's safe vs unsafe to assume

**Safe** to assume done from this PR:

* Repo tidy is complete — see `CHANGELOG.adoc [Unreleased]` for the
  full list. Eight commits, 95+ files touched.
* `docs/wiki/` is staged for the GitHub wiki — see
  `docs/wiki/README.md` for the sync recipe.
* SPDX-License-Identifier coverage is now complete on every file in
  the repository (the Nickel example was the last gap).
* Bouncer compiles cleanly with a loud-fail stub (was previously a
  non-compiling stub).
* PMPL → MPL-2.0 migration is finished (was incomplete on `main`).

**Unsafe** to assume:

* That CI is green. It isn't, and most of the red is pre-existing.
  The PR is mergeable from a content-correctness standpoint but
  not from a status-check standpoint.
* That `deno fmt` on the new markdown files would pass. It may
  reformat headings/lists; can't verify without Deno locally.
* That `mix compile --warnings-as-errors` passes on this branch.
  Likely fine (no functional Elixir changes in this PR beyond the
  Bouncer stub), but unverified — the `test` job fails too fast for
  the failure to be in test runs themselves.

## What's needed to take CI to green

Out of scope for this PR but documented for the next pass:

1. Find why `elixir-ci / test` fails inside the first ~2 s on every
   PR. Suspect: cache action against a stale `mix.lock` key, or the
   `erlef/setup-beam` Elixir/OTP combo (1.15/26) drifting from the
   project pin (1.18.4/25 in `.tool-versions`).
2. Either add `docs/wiki/` to a Deno fmt ignore list or stop running
   `rescript-deno-ci` on a repo that has no ReScript or Deno code.
   The latter looks correct — this workflow appears templated in,
   not used.
3. Open an issue against `hyperpolymath/standards` to see why
   `governance / Language / package anti-pattern policy` flags this
   repo. The reusable workflow's source is not visible from this
   repository.
