# AI Agent Briefing

This project is designed for human + AI collaboration. The following files exist specifically to brief LLM agents before they touch the code.

## Canonical entry points

| File | When to read | Notes |
|------|--------------|-------|
| [`0-AI-MANIFEST.a2ml`](https://github.com/hyperpolymath/tma-mark2/blob/main/0-AI-MANIFEST.a2ml) | Always, first | Canonical locations, critical invariants, context tiers. |
| [`.claude/CLAUDE.md`](https://github.com/hyperpolymath/tma-mark2/blob/main/.claude/CLAUDE.md) | Always | Language policy, banned tools, prohibited dependencies. |
| [`docs/ai/warmup-dev.md`](https://github.com/hyperpolymath/tma-mark2/blob/main/docs/ai/warmup-dev.md) | If you're modifying code | Developer-oriented briefing. |
| [`docs/ai/warmup-user.md`](https://github.com/hyperpolymath/tma-mark2/blob/main/docs/ai/warmup-user.md) | If you're helping an end user | User-oriented briefing. |
| [`TOPOLOGY.md`](https://github.com/hyperpolymath/tma-mark2/blob/main/TOPOLOGY.md) | Before claiming a component works | Live completion dashboard. |

## Machine-readable manifests

The [`.machine_readable/`](https://github.com/hyperpolymath/tma-mark2/tree/main/.machine_readable) tree holds structured manifests that agents can parse without LLM inference:

* [`.machine_readable/6a2/STATE.a2ml`](https://github.com/hyperpolymath/tma-mark2/blob/main/.machine_readable/6a2/STATE.a2ml) — current state & v1 critical-path step status.
* [`.machine_readable/6a2/META.a2ml`](https://github.com/hyperpolymath/tma-mark2/blob/main/.machine_readable/6a2/META.a2ml) — architecture decisions and development practices.
* [`.machine_readable/6a2/ECOSYSTEM.a2ml`](https://github.com/hyperpolymath/tma-mark2/blob/main/.machine_readable/6a2/ECOSYSTEM.a2ml) — ecosystem position.
* [`.machine_readable/6a2/AGENTIC.a2ml`](https://github.com/hyperpolymath/tma-mark2/blob/main/.machine_readable/6a2/AGENTIC.a2ml) — AI agent interaction patterns.
* [`.machine_readable/6a2/PLAYBOOK.a2ml`](https://github.com/hyperpolymath/tma-mark2/blob/main/.machine_readable/6a2/PLAYBOOK.a2ml) — operational runbook.

## House rules for agents

1. **Do not raise completion percentages** in `TOPOLOGY.md` above what is demonstrable. Docstrings and stub bodies are not progress.
2. **Do not introduce a banned language**. If you reach for TypeScript, Node, npm, Python, Go, Kotlin, or Swift, stop and ask.
3. **SPDX header on every new file**: `SPDX-License-Identifier: MPL-2.0`.
4. **CHANGELOG.adoc** gets an `[Unreleased]` entry for anything user-visible.
5. **One conceptual change per commit**. Commits should be reviewable in isolation.
6. Tests come with the code, not as a follow-up commit.

## Boundary cases worth flagging early

* Any change to encryption-at-rest, the FHI parser, or the supervision tree → ask before implementing.
* Anything that adds a new top-level directory → ask.
* Anything that removes or relocates files in `LICENSES/`, `.well-known/`, or `.machine_readable/6a2/` → ask.
* Anything that calls out to a third-party hosted service → ask.
