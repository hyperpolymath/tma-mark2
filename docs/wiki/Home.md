# tma-mark2 — eTMA Handler

The BEAM edition of the Open University's electronic Tutor-Marked Assignment (eTMA) marking tool. Elixir / Phoenix on the backend, Rust NIFs for cryptography and NLP, a Phoenix LiveView UI on the way.

> ⚠️ **Status: alpha scaffold (~30–35%)** — see [Roadmap and Status](Roadmap-and-Status) for an honest dashboard. This is not a runnable product yet.

## Quick links

| Goal | Page |
|------|------|
| Install / run the app | [Getting Started](Getting-Started) |
| See where the project stands | [Roadmap and Status](Roadmap-and-Status) |
| Understand the architecture | [Architecture](Architecture) |
| Contribute code | [Development](Development) |
| Brief an AI agent | [AI Agent Briefing](AI-Agent-Briefing) |
| Audit compliance | [RSR Compliance](RSR-Compliance) |
| Read the file-format spec | [FHI Format](FHI-Format) |
| Find a document by intent | [Documentation Index](Documentation-Index) |

## What is the eTMA Handler?

When complete, the application will:

1. Watch the user's Downloads folder for `.fhi` student submissions.
2. Verify, decompress, and store them in a local CubDB vault.
3. Present a Phoenix LiveView marking cockpit for tutors.
4. Generate graded `.docx` files for return to students.
5. Run entirely offline; ship as a single Burrito binary.

The original Java implementation predates this repo; this is a clean re-architecture rather than a port.

## Repository

* Source: <https://github.com/hyperpolymath/tma-mark2>
* License: Palimpsest License (MPL-2.0) — see [License](License)
* Governance: see [Governance](Governance)
* Author: Jonathan D.A. Jewell ([@hyperpolymath](https://github.com/hyperpolymath))

## How this wiki relates to the repository

This wiki is a **navigation layer**, not a source of truth. Every authoritative document lives in the repository under `docs/` and is linked from here. If a wiki page disagrees with the repository, the repository wins.
