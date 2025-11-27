# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Plugin architecture for custom analyzers
- Web dashboard with real-time updates
- Full-text search across tags and content

### Changed
- Improved error messages with suggestions

### Fixed
- Memory leak in file watcher

## [3.0.0] - 2024-12-01

### Added
- **Plugin Architecture**: Extensible analyzer system with trait-based design
- **Web Dashboard**: Real-time monitoring interface with Axum
- **Multi-Format Support**: Audio, video, code, document, and archive analyzers
- **Tagging System**: SQLite-backed tags and categories with search
- **Database**: Persistent storage for file metadata and tags
- **Deduplication**: Blake3 hashing to identify duplicate files
- **History**: JSONL-based operation log for undo/redo
- **Multi-Model Support**: Different AI models for vision, text, and code
- **Watch Stability**: Debouncing and stability checks for file watching
- **Shell Completions**: Generate completions for bash, zsh, fish, PowerShell

### Changed
- Complete architecture rewrite for modularity
- Configuration format updated with new options
- CLI restructured with subcommands
- Improved error handling with `thiserror`

### Deprecated
- Legacy v1 configuration format (migration available)

### Removed
- Blocking I/O operations (replaced with async)

### Security
- Added `#![forbid(unsafe_code)]`
- Dependency audit process established

## [2.0.0] - 2024-06-01

### Added
- File watching with notify crate
- Graceful shutdown with signal handling
- Ollama health checks
- Undo capability with history log
- JSON configuration support

### Changed
- Migrated to async/await with Tokio
- Improved Ollama client with retries

### Fixed
- Config loading for Nickel format
- Thread blocking in async context

## [1.0.0] - 2024-03-01

### Added
- Initial release
- Image analysis with Moondream
- PDF text extraction
- Basic file renaming
- Ollama integration
- CLI interface

---

[Unreleased]: https://github.com/panoptes/panoptes/compare/v3.0.0...HEAD
[3.0.0]: https://github.com/panoptes/panoptes/compare/v2.0.0...v3.0.0
[2.0.0]: https://github.com/panoptes/panoptes/compare/v1.0.0...v2.0.0
[1.0.0]: https://github.com/panoptes/panoptes/releases/tag/v1.0.0
