// SPDX-FileCopyrightText: 2024 Panoptes Contributors
// SPDX-License-Identifier: MIT

//! File analyzers for different content types
//!
//! This module provides a plugin architecture for analyzing various file types.
//! Each analyzer implements the [`FileAnalyzer`] trait and is registered with
//! the [`AnalyzerRegistry`].

pub mod archive;
pub mod audio;
pub mod code;
pub mod document;
pub mod image;
pub mod pdf;
pub mod video;

use async_trait::async_trait;
use serde::{Deserialize, Serialize};
use std::collections::HashMap;
use std::path::Path;

use crate::config::Config;
use crate::error::Result;

/// Result of analyzing a file
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct AnalysisResult {
    /// Suggested filename (without extension)
    pub suggested_name: Option<String>,
    /// File description from AI
    pub description: Option<String>,
    /// Suggested tags
    pub tags: Vec<String>,
    /// Suggested category
    pub category: Option<String>,
    /// Confidence score (0.0 - 1.0)
    pub confidence: f32,
    /// Additional metadata extracted
    pub metadata: HashMap<String, serde_json::Value>,
    /// Analyzer that produced this result
    pub analyzer: String,
    /// Processing time in milliseconds
    pub processing_time_ms: u64,
}

impl AnalysisResult {
    /// Create a new analysis result
    pub fn new(analyzer: &str) -> Self {
        Self {
            suggested_name: None,
            description: None,
            tags: Vec::new(),
            category: None,
            confidence: 0.0,
            metadata: HashMap::new(),
            analyzer: analyzer.to_string(),
            processing_time_ms: 0,
        }
    }

    /// Set suggested name
    pub fn with_name(mut self, name: impl Into<String>) -> Self {
        self.suggested_name = Some(name.into());
        self
    }

    /// Set description
    pub fn with_description(mut self, desc: impl Into<String>) -> Self {
        self.description = Some(desc.into());
        self
    }

    /// Add a tag
    pub fn with_tag(mut self, tag: impl Into<String>) -> Self {
        self.tags.push(tag.into());
        self
    }

    /// Set tags
    pub fn with_tags(mut self, tags: Vec<String>) -> Self {
        self.tags = tags;
        self
    }

    /// Set category
    pub fn with_category(mut self, category: impl Into<String>) -> Self {
        self.category = Some(category.into());
        self
    }

    /// Set confidence
    pub fn with_confidence(mut self, confidence: f32) -> Self {
        self.confidence = confidence.clamp(0.0, 1.0);
        self
    }

    /// Add metadata
    pub fn with_metadata(mut self, key: impl Into<String>, value: serde_json::Value) -> Self {
        self.metadata.insert(key.into(), value);
        self
    }

    /// Set processing time
    pub fn with_processing_time(mut self, ms: u64) -> Self {
        self.processing_time_ms = ms;
        self
    }
}

/// Trait for file analyzers
#[async_trait]
pub trait FileAnalyzer: Send + Sync {
    /// Name of this analyzer
    fn name(&self) -> &'static str;

    /// File extensions this analyzer supports
    fn supported_extensions(&self) -> &[&str];

    /// Priority (higher = preferred when multiple analyzers match)
    fn priority(&self) -> u8 {
        50
    }

    /// Check if this analyzer can handle the given path
    fn can_analyze(&self, path: &Path) -> bool {
        path.extension()
            .and_then(|e| e.to_str())
            .map(|e| {
                self.supported_extensions()
                    .iter()
                    .any(|ext| ext.eq_ignore_ascii_case(e))
            })
            .unwrap_or(false)
    }

    /// Analyze a file and return results
    async fn analyze(&self, path: &Path, config: &Config) -> Result<AnalysisResult>;
}

/// Registry of available file analyzers
pub struct AnalyzerRegistry {
    analyzers: Vec<Box<dyn FileAnalyzer>>,
}

impl Default for AnalyzerRegistry {
    fn default() -> Self {
        Self::new()
    }
}

impl AnalyzerRegistry {
    /// Create a new registry with all built-in analyzers
    pub fn new() -> Self {
        let mut registry = Self {
            analyzers: Vec::new(),
        };

        // Register built-in analyzers
        registry.register(Box::new(image::ImageAnalyzer::new()));
        registry.register(Box::new(pdf::PdfAnalyzer::new()));
        registry.register(Box::new(audio::AudioAnalyzer::new()));
        registry.register(Box::new(video::VideoAnalyzer::new()));
        registry.register(Box::new(code::CodeAnalyzer::new()));
        registry.register(Box::new(document::DocumentAnalyzer::new()));
        registry.register(Box::new(archive::ArchiveAnalyzer::new()));

        registry
    }

    /// Register a new analyzer
    pub fn register(&mut self, analyzer: Box<dyn FileAnalyzer>) {
        self.analyzers.push(analyzer);
        // Sort by priority (highest first)
        self.analyzers.sort_by(|a, b| b.priority().cmp(&a.priority()));
    }

    /// Find the best analyzer for a file
    pub fn find_analyzer(&self, path: &Path) -> Option<&dyn FileAnalyzer> {
        self.analyzers
            .iter()
            .find(|a| a.can_analyze(path))
            .map(|a| a.as_ref())
    }

    /// Get all analyzers that can handle a file
    pub fn find_all_analyzers(&self, path: &Path) -> Vec<&dyn FileAnalyzer> {
        self.analyzers
            .iter()
            .filter(|a| a.can_analyze(path))
            .map(|a| a.as_ref())
            .collect()
    }

    /// List all registered analyzers
    pub fn list(&self) -> Vec<(&'static str, Vec<&str>)> {
        self.analyzers
            .iter()
            .map(|a| (a.name(), a.supported_extensions().to_vec()))
            .collect()
    }

    /// Get supported extensions
    pub fn supported_extensions(&self) -> Vec<&str> {
        self.analyzers
            .iter()
            .flat_map(|a| a.supported_extensions().iter().copied())
            .collect()
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_analysis_result_builder() {
        let result = AnalysisResult::new("test")
            .with_name("my-file")
            .with_description("A test file")
            .with_tag("test")
            .with_tag("example")
            .with_category("documents")
            .with_confidence(0.85)
            .with_metadata("size", serde_json::json!(1024));

        assert_eq!(result.suggested_name, Some("my-file".to_string()));
        assert_eq!(result.description, Some("A test file".to_string()));
        assert_eq!(result.tags, vec!["test", "example"]);
        assert_eq!(result.category, Some("documents".to_string()));
        assert!((result.confidence - 0.85).abs() < f32::EPSILON);
        assert_eq!(result.metadata.get("size"), Some(&serde_json::json!(1024)));
    }

    #[test]
    fn test_confidence_clamping() {
        let result = AnalysisResult::new("test").with_confidence(1.5);
        assert!((result.confidence - 1.0).abs() < f32::EPSILON);

        let result = AnalysisResult::new("test").with_confidence(-0.5);
        assert!(result.confidence.abs() < f32::EPSILON);
    }

    #[test]
    fn test_registry_creation() {
        let registry = AnalyzerRegistry::new();
        let analyzers = registry.list();
        assert!(!analyzers.is_empty());
    }
}
