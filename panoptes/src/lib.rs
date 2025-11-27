// SPDX-FileCopyrightText: 2024 Panoptes Contributors
// SPDX-License-Identifier: MIT

//! Panoptes - AI-powered local file scanner library
//!
//! This library provides the core functionality for intelligent file organization
//! using vision models via Ollama.
//!
//! # Features
//!
//! - Plugin-based analyzer architecture
//! - Support for images, PDFs, audio, video, code, and documents
//! - SQLite-backed tagging and categorization
//! - File watching with automatic processing
//! - Full undo/redo history
//!
//! # Example
//!
//! ```rust,no_run
//! use panoptes::{config::AppConfig, analyzers::AnalyzerRegistry};
//!
//! #[tokio::main]
//! async fn main() -> anyhow::Result<()> {
//!     let config = AppConfig::default();
//!     let registry = AnalyzerRegistry::new(&config);
//!
//!     // Analyze a file
//!     let result = registry.analyze(std::path::Path::new("image.jpg")).await?;
//!     println!("Suggested name: {}", result.suggested_name);
//!
//!     Ok(())
//! }
//! ```

#![forbid(unsafe_code)]
#![warn(clippy::all, clippy::pedantic, clippy::nursery)]
#![warn(missing_docs)]

pub mod analyzers;
pub mod config;
pub mod db;
pub mod error;
pub mod history;
pub mod ollama;
pub mod watcher;

#[cfg(feature = "web")]
pub mod web;

pub use error::{Error, Result};

/// Library version
pub const VERSION: &str = env!("CARGO_PKG_VERSION");
