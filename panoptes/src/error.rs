// SPDX-FileCopyrightText: 2024 Panoptes Contributors
// SPDX-License-Identifier: MIT

//! Error types for Panoptes

use std::path::PathBuf;
use thiserror::Error;

/// Result type alias using Panoptes error
pub type Result<T> = std::result::Result<T, Error>;

/// Main error type for Panoptes operations
#[derive(Error, Debug)]
pub enum Error {
    /// Configuration error
    #[error("Configuration error: {message}")]
    Config {
        /// Error message
        message: String,
        /// Source of the error
        #[source]
        source: Option<Box<dyn std::error::Error + Send + Sync>>,
    },

    /// File not found
    #[error("File not found: {path}")]
    FileNotFound {
        /// Path that was not found
        path: PathBuf,
    },

    /// Permission denied
    #[error("Permission denied: {path}")]
    PermissionDenied {
        /// Path that was denied
        path: PathBuf,
    },

    /// Invalid file type
    #[error("Invalid file type for {path}: expected {expected}, got {actual}")]
    InvalidFileType {
        /// File path
        path: PathBuf,
        /// Expected type
        expected: String,
        /// Actual type
        actual: String,
    },

    /// Analyzer error
    #[error("Analyzer '{name}' failed for {path}: {message}")]
    Analyzer {
        /// Analyzer name
        name: String,
        /// File path
        path: PathBuf,
        /// Error message
        message: String,
    },

    /// Ollama API error
    #[error("Ollama API error: {message}")]
    Ollama {
        /// Error message
        message: String,
        /// HTTP status code if available
        status_code: Option<u16>,
    },

    /// Ollama not available
    #[error("Ollama is not available at {url}")]
    OllamaUnavailable {
        /// Ollama URL
        url: String,
    },

    /// Model not found
    #[error("Model '{model}' not found. Available models: {available:?}")]
    ModelNotFound {
        /// Requested model
        model: String,
        /// Available models
        available: Vec<String>,
    },

    /// Database error
    #[error("Database error: {message}")]
    Database {
        /// Error message
        message: String,
        /// Source error
        #[source]
        source: Option<rusqlite::Error>,
    },

    /// History error
    #[error("History error: {message}")]
    History {
        /// Error message
        message: String,
    },

    /// Watcher error
    #[error("File watcher error: {message}")]
    Watcher {
        /// Error message
        message: String,
    },

    /// Rename error
    #[error("Failed to rename {from} to {to}: {reason}")]
    Rename {
        /// Original path
        from: PathBuf,
        /// Target path
        to: PathBuf,
        /// Reason for failure
        reason: String,
    },

    /// Duplicate file
    #[error("Duplicate file detected: {path} (matches {original})")]
    Duplicate {
        /// Duplicate file path
        path: PathBuf,
        /// Original file path
        original: PathBuf,
    },

    /// Timeout error
    #[error("Operation timed out after {seconds} seconds")]
    Timeout {
        /// Timeout in seconds
        seconds: u64,
    },

    /// IO error
    #[error("IO error: {0}")]
    Io(#[from] std::io::Error),

    /// JSON serialization error
    #[error("JSON error: {0}")]
    Json(#[from] serde_json::Error),

    /// HTTP request error
    #[error("HTTP error: {0}")]
    Http(#[from] reqwest::Error),

    /// Other error
    #[error("{0}")]
    Other(String),
}

impl Error {
    /// Create a configuration error
    pub fn config(message: impl Into<String>) -> Self {
        Self::Config {
            message: message.into(),
            source: None,
        }
    }

    /// Create a configuration error with source
    pub fn config_with_source<E: std::error::Error + Send + Sync + 'static>(
        message: impl Into<String>,
        source: E,
    ) -> Self {
        Self::Config {
            message: message.into(),
            source: Some(Box::new(source)),
        }
    }

    /// Create an analyzer error
    pub fn analyzer(name: &str, path: &std::path::Path, message: impl Into<String>) -> Self {
        Self::Analyzer {
            name: name.to_string(),
            path: path.to_path_buf(),
            message: message.into(),
        }
    }

    /// Create an Ollama error
    pub fn ollama(message: impl Into<String>) -> Self {
        Self::Ollama {
            message: message.into(),
            status_code: None,
        }
    }

    /// Create an Ollama error with status code
    pub fn ollama_with_status(message: impl Into<String>, status: u16) -> Self {
        Self::Ollama {
            message: message.into(),
            status_code: Some(status),
        }
    }

    /// Create a database error
    pub fn database(message: impl Into<String>) -> Self {
        Self::Database {
            message: message.into(),
            source: None,
        }
    }

    /// Create a database error from rusqlite error
    pub fn database_from(message: impl Into<String>, source: rusqlite::Error) -> Self {
        Self::Database {
            message: message.into(),
            source: Some(source),
        }
    }

    /// Check if error is retryable
    pub fn is_retryable(&self) -> bool {
        matches!(
            self,
            Self::Ollama { .. } | Self::Timeout { .. } | Self::Http(_)
        )
    }

    /// Get error code for CLI exit status
    pub fn exit_code(&self) -> i32 {
        match self {
            Self::Config { .. } => 64,       // EX_USAGE
            Self::FileNotFound { .. } => 66, // EX_NOINPUT
            Self::PermissionDenied { .. } => 77, // EX_NOPERM
            Self::Ollama { .. } | Self::OllamaUnavailable { .. } => 69, // EX_UNAVAILABLE
            Self::Database { .. } => 74,     // EX_IOERR
            Self::Io(_) => 74,               // EX_IOERR
            _ => 1,                          // General error
        }
    }
}

impl From<rusqlite::Error> for Error {
    fn from(err: rusqlite::Error) -> Self {
        Self::Database {
            message: err.to_string(),
            source: Some(err),
        }
    }
}

impl From<notify::Error> for Error {
    fn from(err: notify::Error) -> Self {
        Self::Watcher {
            message: err.to_string(),
        }
    }
}
