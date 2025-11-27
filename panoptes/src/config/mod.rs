// SPDX-FileCopyrightText: 2024 Panoptes Contributors
// SPDX-License-Identifier: MIT

//! Configuration management for Panoptes
//!
//! Configuration is loaded from multiple sources in order of precedence:
//! 1. Built-in defaults
//! 2. System config (`/etc/panoptes/config.json`)
//! 3. User config (`~/.config/panoptes/config.json`)
//! 4. Project config (`./config.json`)
//! 5. Environment variables (`PANOPTES_*`)
//! 6. CLI arguments

use serde::{Deserialize, Serialize};
use std::path::{Path, PathBuf};

use crate::error::{Error, Result};

/// Main application configuration
#[derive(Debug, Clone, Serialize, Deserialize)]
#[serde(default)]
pub struct Config {
    /// Ollama API URL
    pub ollama_url: String,

    /// Model to use for analysis
    pub model: String,

    /// Database path
    pub database_path: Option<PathBuf>,

    /// History file path
    pub history_path: Option<PathBuf>,

    /// Maximum concurrent jobs
    pub max_jobs: usize,

    /// Request timeout in seconds
    pub timeout_secs: u64,

    /// Retry configuration
    pub retry: RetryConfig,

    /// Naming configuration
    pub naming: NamingConfig,

    /// Tag configuration
    pub tags: TagConfig,

    /// Watch mode configuration
    pub watch: WatchConfig,

    /// Web dashboard configuration
    pub web: WebConfig,

    /// Logging configuration
    pub logging: LoggingConfig,

    /// File filtering configuration
    pub filter: FilterConfig,
}

impl Default for Config {
    fn default() -> Self {
        Self {
            ollama_url: "http://localhost:11434".to_string(),
            model: "moondream".to_string(),
            database_path: None,
            history_path: None,
            max_jobs: num_cpus::get(),
            timeout_secs: 120,
            retry: RetryConfig::default(),
            naming: NamingConfig::default(),
            tags: TagConfig::default(),
            watch: WatchConfig::default(),
            web: WebConfig::default(),
            logging: LoggingConfig::default(),
            filter: FilterConfig::default(),
        }
    }
}

/// Retry configuration
#[derive(Debug, Clone, Serialize, Deserialize)]
#[serde(default)]
pub struct RetryConfig {
    /// Maximum number of retries
    pub max_retries: u32,
    /// Initial backoff in milliseconds
    pub initial_backoff_ms: u64,
    /// Maximum backoff in milliseconds
    pub max_backoff_ms: u64,
    /// Backoff multiplier
    pub multiplier: f64,
}

impl Default for RetryConfig {
    fn default() -> Self {
        Self {
            max_retries: 3,
            initial_backoff_ms: 1000,
            max_backoff_ms: 30000,
            multiplier: 2.0,
        }
    }
}

/// Naming convention configuration
#[derive(Debug, Clone, Serialize, Deserialize)]
#[serde(default)]
pub struct NamingConfig {
    /// Naming style
    pub style: NamingStyle,
    /// Maximum filename length
    pub max_length: usize,
    /// Prefix for renamed files
    pub prefix: Option<String>,
    /// Suffix for renamed files
    pub suffix: Option<String>,
    /// Include date in filename
    pub include_date: bool,
    /// Date format string
    pub date_format: String,
    /// Preserve original name as tag
    pub preserve_original_as_tag: bool,
}

impl Default for NamingConfig {
    fn default() -> Self {
        Self {
            style: NamingStyle::KebabCase,
            max_length: 50,
            prefix: None,
            suffix: None,
            include_date: false,
            date_format: "%Y-%m-%d".to_string(),
            preserve_original_as_tag: true,
        }
    }
}

/// Naming style options
#[derive(Debug, Clone, Copy, Serialize, Deserialize, Default)]
#[serde(rename_all = "kebab-case")]
pub enum NamingStyle {
    /// lowercase-with-hyphens
    #[default]
    KebabCase,
    /// lowercase_with_underscores
    SnakeCase,
    /// CapitalizedWords
    PascalCase,
    /// capitalizedWordsLowerFirst
    CamelCase,
}

/// Tag configuration
#[derive(Debug, Clone, Serialize, Deserialize)]
#[serde(default)]
pub struct TagConfig {
    /// Auto-generate tags from content
    pub auto_generate: bool,
    /// Maximum number of auto-generated tags
    pub max_auto_tags: usize,
    /// Default tags to apply to all files
    pub default_tags: Vec<String>,
    /// Tag separator in filenames (if embedding)
    pub separator: String,
}

impl Default for TagConfig {
    fn default() -> Self {
        Self {
            auto_generate: true,
            max_auto_tags: 5,
            default_tags: Vec::new(),
            separator: "_".to_string(),
        }
    }
}

/// Watch mode configuration
#[derive(Debug, Clone, Serialize, Deserialize)]
#[serde(default)]
pub struct WatchConfig {
    /// Debounce delay in milliseconds
    pub debounce_ms: u64,
    /// Watch subdirectories recursively
    pub recursive: bool,
    /// Process existing files on startup
    pub process_existing: bool,
    /// Ignore patterns (glob)
    pub ignore_patterns: Vec<String>,
}

impl Default for WatchConfig {
    fn default() -> Self {
        Self {
            debounce_ms: 500,
            recursive: true,
            process_existing: false,
            ignore_patterns: vec![
                ".*".to_string(),
                "*~".to_string(),
                "*.tmp".to_string(),
                "*.swp".to_string(),
            ],
        }
    }
}

/// Web dashboard configuration
#[derive(Debug, Clone, Serialize, Deserialize)]
#[serde(default)]
pub struct WebConfig {
    /// Web server address
    pub address: String,
    /// Web server port
    pub port: u16,
    /// Enable API
    pub enable_api: bool,
    /// API key (optional)
    pub api_key: Option<String>,
}

impl Default for WebConfig {
    fn default() -> Self {
        Self {
            address: "127.0.0.1".to_string(),
            port: 8080,
            enable_api: true,
            api_key: None,
        }
    }
}

/// Logging configuration
#[derive(Debug, Clone, Serialize, Deserialize)]
#[serde(default)]
pub struct LoggingConfig {
    /// Log level
    pub level: String,
    /// Log to file
    pub file: Option<PathBuf>,
    /// JSON output
    pub json: bool,
}

impl Default for LoggingConfig {
    fn default() -> Self {
        Self {
            level: "info".to_string(),
            file: None,
            json: false,
        }
    }
}

/// File filtering configuration
#[derive(Debug, Clone, Serialize, Deserialize)]
#[serde(default)]
pub struct FilterConfig {
    /// Minimum file size (bytes)
    pub min_size: Option<u64>,
    /// Maximum file size (bytes)
    pub max_size: Option<u64>,
    /// Include only these extensions
    pub include_extensions: Vec<String>,
    /// Exclude these extensions
    pub exclude_extensions: Vec<String>,
    /// Exclude hidden files
    pub exclude_hidden: bool,
    /// Exclude patterns (glob)
    pub exclude_patterns: Vec<String>,
}

impl Default for FilterConfig {
    fn default() -> Self {
        Self {
            min_size: None,
            max_size: None,
            include_extensions: Vec::new(),
            exclude_extensions: Vec::new(),
            exclude_hidden: true,
            exclude_patterns: Vec::new(),
        }
    }
}

impl Config {
    /// Load configuration from default locations
    pub fn load() -> Result<Self> {
        let mut config = Self::default();

        // Load from system config
        if let Some(path) = Self::system_config_path() {
            if path.exists() {
                config = config.merge_from_file(&path)?;
            }
        }

        // Load from user config
        if let Some(path) = Self::user_config_path() {
            if path.exists() {
                config = config.merge_from_file(&path)?;
            }
        }

        // Load from project config
        let project_config = PathBuf::from("config.json");
        if project_config.exists() {
            config = config.merge_from_file(&project_config)?;
        }

        // Override with environment variables
        config = config.merge_from_env();

        Ok(config)
    }

    /// Load configuration from a specific file
    pub fn load_from_file(path: &Path) -> Result<Self> {
        let content = std::fs::read_to_string(path)?;
        serde_json::from_str(&content).map_err(|e| Error::config_with_source("Failed to parse config", e))
    }

    /// Merge configuration from a file
    fn merge_from_file(self, path: &Path) -> Result<Self> {
        let file_config = Self::load_from_file(path)?;
        Ok(self.merge(file_config))
    }

    /// Merge with another configuration (other takes precedence)
    fn merge(self, other: Self) -> Self {
        // For simplicity, just use the other config
        // A real implementation would merge field by field
        other
    }

    /// Override from environment variables
    fn merge_from_env(mut self) -> Self {
        if let Ok(url) = std::env::var("PANOPTES_OLLAMA_URL") {
            self.ollama_url = url;
        }
        if let Ok(model) = std::env::var("PANOPTES_MODEL") {
            self.model = model;
        }
        if let Ok(db) = std::env::var("PANOPTES_DATABASE") {
            self.database_path = Some(PathBuf::from(db));
        }
        if let Ok(jobs) = std::env::var("PANOPTES_MAX_JOBS") {
            if let Ok(n) = jobs.parse() {
                self.max_jobs = n;
            }
        }
        if let Ok(timeout) = std::env::var("PANOPTES_TIMEOUT") {
            if let Ok(t) = timeout.parse() {
                self.timeout_secs = t;
            }
        }
        if let Ok(level) = std::env::var("PANOPTES_LOG_LEVEL") {
            self.logging.level = level;
        }
        self
    }

    /// Get system config path
    fn system_config_path() -> Option<PathBuf> {
        if cfg!(unix) {
            Some(PathBuf::from("/etc/panoptes/config.json"))
        } else {
            None
        }
    }

    /// Get user config path
    fn user_config_path() -> Option<PathBuf> {
        dirs::config_dir().map(|p| p.join("panoptes").join("config.json"))
    }

    /// Get default database path
    pub fn default_database_path() -> PathBuf {
        dirs::data_local_dir()
            .unwrap_or_else(|| PathBuf::from("."))
            .join("panoptes")
            .join("panoptes.db")
    }

    /// Get default history path
    pub fn default_history_path() -> PathBuf {
        dirs::data_local_dir()
            .unwrap_or_else(|| PathBuf::from("."))
            .join("panoptes")
            .join("history.jsonl")
    }

    /// Get the database path, using default if not set
    pub fn get_database_path(&self) -> PathBuf {
        self.database_path
            .clone()
            .unwrap_or_else(Self::default_database_path)
    }

    /// Get the history path, using default if not set
    pub fn get_history_path(&self) -> PathBuf {
        self.history_path
            .clone()
            .unwrap_or_else(Self::default_history_path)
    }

    /// Validate configuration
    pub fn validate(&self) -> Result<()> {
        if self.max_jobs == 0 {
            return Err(Error::config("max_jobs must be at least 1"));
        }
        if self.timeout_secs == 0 {
            return Err(Error::config("timeout must be at least 1 second"));
        }
        if self.naming.max_length == 0 {
            return Err(Error::config("max_length must be at least 1"));
        }
        Ok(())
    }

    /// Save configuration to a file
    pub fn save(&self, path: &Path) -> Result<()> {
        let content = serde_json::to_string_pretty(self)?;
        std::fs::write(path, content)?;
        Ok(())
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_default_config() {
        let config = Config::default();
        assert_eq!(config.ollama_url, "http://localhost:11434");
        assert_eq!(config.model, "moondream");
        assert!(config.max_jobs > 0);
    }

    #[test]
    fn test_config_validation() {
        let mut config = Config::default();
        assert!(config.validate().is_ok());

        config.max_jobs = 0;
        assert!(config.validate().is_err());
    }

    #[test]
    fn test_naming_style_serialization() {
        let style = NamingStyle::KebabCase;
        let json = serde_json::to_string(&style).unwrap();
        assert_eq!(json, "\"kebab-case\"");

        let parsed: NamingStyle = serde_json::from_str(&json).unwrap();
        assert!(matches!(parsed, NamingStyle::KebabCase));
    }
}
