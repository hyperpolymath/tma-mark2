<!-- SPDX-License-Identifier: MPL-2.0 -->

# experiments/

Code that is **not on the v1 critical path**. Kept in the repo so the work isn't lost, but moved out of the primary tree so it can't masquerade as production-bound.

See [../ROADMAP.adoc](../ROADMAP.adoc) — these are candidates for post-v1, only if a real pilot user asks for them.

## Contents

| Path | What it was | Why deferred |
|------|-------------|--------------|
| `mobile/` | Tauri/Dioxus skeleton (~1000 LOC: api/state/crypto + 8 components) | No demand; v1 is a desktop tool. Mobile is a separate product. |
| `affine-frontend/main.affine` | 25-line AffineScript stub returning `[0]` | Vapourware; LiveView is the v1 UI. |
| `bebop/schemas/` | Binary serialisation schemas | Not used by any working code path. v1 stays on the wire formats already in place (HTTP/JSON, FHI XML). |
| `ffi/zig/` | Zig FFI sketch | Rust NIFs cover crypto + NLP; Zig has no consumer. |

## Re-promotion criteria

Move something back into the primary tree only when **all** of these are true:

1. A real user has asked for the capability it enables.
2. There is a concrete scope doc that fits in one phase of the roadmap.
3. The v1 product is shipped and stable.
