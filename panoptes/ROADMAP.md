# Roadmap

This document outlines the planned development direction for Panoptes.

## Vision

Panoptes aims to be the definitive local AI-powered file organization tool, running entirely offline with no cloud dependencies.

## Current Release: v3.0

### Completed Features
- [x] Plugin architecture for analyzers
- [x] Web dashboard
- [x] Multi-format support (image, PDF, audio, video, code, document, archive)
- [x] SQLite database for tags and metadata
- [x] File watching with auto-processing
- [x] Undo/redo history
- [x] Deduplication via Blake3 hashing
- [x] Multi-model AI support

## Short Term (v3.1 - v3.2)

### v3.1: Enhanced Intelligence
- [ ] Batch processing optimization
- [ ] Smart suggestions based on folder patterns
- [ ] Face recognition in images
- [ ] OCR for images with text
- [ ] Improved PDF layout analysis

### v3.2: Integration & Workflow
- [ ] Browser extension for download handling
- [ ] Keyboard shortcuts in web UI
- [ ] Rule-based automation (if X then Y)
- [ ] Import from other organization tools
- [ ] Cloud sync adapters (optional)

## Medium Term (v4.0)

### Major Features
- [ ] Multi-user support with permissions
- [ ] Remote Ollama cluster support
- [ ] Mobile companion app (view only)
- [ ] Custom model fine-tuning helpers
- [ ] Plugin marketplace

### Performance
- [ ] Streaming analysis for large files
- [ ] Incremental database updates
- [ ] Memory-mapped file handling
- [ ] GPU acceleration support

## Long Term (v5.0+)

### Advanced Features
- [ ] Natural language queries ("find my vacation photos")
- [ ] Semantic file relationships
- [ ] Automated backup organization
- [ ] Cross-platform desktop app (Tauri)
- [ ] API for third-party integrations

### Ecosystem
- [ ] Official plugin SDK
- [ ] Model hub for specialized analyzers
- [ ] Community rule sharing
- [ ] Enterprise features

## Community Requested

Features frequently requested by the community:

| Feature | Votes | Status |
|---------|-------|--------|
| Network drive support | 42 | Planned |
| Thunderbird integration | 28 | Investigating |
| RAW photo support | 23 | v3.1 |
| Subtitle extraction | 19 | Planned |
| HEIC support | 15 | v3.1 |

## Non-Goals

Things we explicitly won't do:

- **Cloud processing**: Files never leave your machine
- **Telemetry**: No usage tracking or analytics
- **Subscription model**: Always free and open source
- **Proprietary formats**: Stick to open standards

## Contributing to the Roadmap

Have ideas? We'd love to hear them!

1. Check existing issues and roadmap items
2. Open a discussion for major features
3. Create an issue with the `enhancement` label
4. Vote on existing proposals with 👍

## Timeline Disclaimer

All dates and versions are estimates and subject to change based on:
- Community feedback
- Available resources
- Technical challenges
- Dependency updates

---

_Last updated: 2024-12-01_
