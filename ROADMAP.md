<!-- SPDX-FileCopyrightText: 2024-2025 eTMA Handler Contributors -->
<!-- SPDX-License-Identifier: MIT OR AGPL-3.0-or-later -->

# Roadmap

This document outlines the planned development direction for eTMA Handler.

## Current Version: 2.0.0

The BEAM edition is now feature-complete for basic marking workflows.

## Immediate (v2.0.x - Maintenance)

### Security & Compliance
- [x] SHA-pinned GitHub Actions (security)
- [x] CodeQL scanning on main branch
- [x] Dependabot for dependency updates
- [x] OSSF Scorecard integration
- [x] Dual-license compliance (MIT OR AGPL-3.0-or-later)
- [ ] Full SHA pinning for all actions (ongoing via Dependabot)

### SCM & Build
- [x] guix.scm package definition
- [x] flake.nix Nix derivation
- [x] Containerfile (Wolfi-based)
- [ ] flake.lock generation for reproducibility
- [ ] Guix channel publishing

## Short Term (v2.1 - v2.3)

### v2.1 - Enhanced UI
- [ ] Dark mode support
- [ ] Keyboard shortcuts for common actions
- [ ] Customizable marking grid
- [ ] Bulk operations (select multiple, batch grade)

### v2.2 - Integration
- [ ] WebDAV sync for cloud storage
- [ ] Export to CSV/Excel
- [ ] Grade statistics dashboard
- [ ] Assignment templates

### v2.3 - Collaboration
- [ ] Multi-tutor support (shared database)
- [ ] Audit trail for changes
- [ ] Comments and annotations

## Medium Term (v3.0)

### Major Features
- [ ] Plugin system (WASM-based)
- [ ] Custom marking schemes
- [ ] Plagiarism detection hooks
- [ ] AI-assisted feedback suggestions (local models)

### Technical
- [ ] Raft consensus for multi-node
- [ ] CRDTs for offline sync
- [ ] Event sourcing

### RSR Compliance
- [ ] Complete ReScript migration (TypeScript removal)
- [ ] Full SHA-pinned actions audit
- [ ] Security hardening review

## Long Term (v4.0+)

### Vision
- Full offline-first distributed marking
- Integration with LMS platforms
- Accessibility compliance (WCAG 2.1 AA)

### Considerations
- RISC-V native builds
- Post-quantum cryptography by default
- Federation with other marking tools

## End of Life Policy

- Major versions: Supported for 2 years
- Security patches: Provided for supported versions
- Migration guides: Provided for breaking changes

## Not Planned

Items we've decided against:

- Cloud-hosted version (privacy concerns)
- Electron wrapper (prefer native web)
- Mobile app (web responsive is sufficient)

## How to Influence

1. Open GitHub Discussions for feature ideas
2. Vote on existing proposals
3. Submit RFCs for significant changes
4. Contribute implementations

---

Last updated: 2025-12-17
