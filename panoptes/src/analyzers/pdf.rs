// SPDX-FileCopyrightText: 2024 Panoptes Contributors
// SPDX-License-Identifier: MIT

//! PDF document analyzer

use async_trait::async_trait;
use std::path::Path;
use std::time::Instant;

use super::{AnalysisResult, FileAnalyzer};
use crate::config::Config;
use crate::error::{Error, Result};
use crate::ollama::OllamaClient;

/// Analyzer for PDF files
pub struct PdfAnalyzer {
    extensions: Vec<&'static str>,
}

impl Default for PdfAnalyzer {
    fn default() -> Self {
        Self::new()
    }
}

impl PdfAnalyzer {
    /// Create a new PDF analyzer
    pub fn new() -> Self {
        Self {
            extensions: vec!["pdf"],
        }
    }

    /// Extract text from PDF
    fn extract_text(&self, path: &Path) -> Result<String> {
        let bytes = std::fs::read(path)?;
        pdf_extract::extract_text_from_mem(&bytes)
            .map_err(|e| Error::analyzer(self.name(), path, e.to_string()))
    }

    /// Extract PDF metadata
    fn extract_metadata(&self, path: &Path) -> Result<serde_json::Value> {
        let bytes = std::fs::read(path)?;

        // Basic metadata from file
        let file_meta = std::fs::metadata(path)?;
        let size = file_meta.len();

        // Try to get page count (simplified)
        let text = self.extract_text(path).unwrap_or_default();
        let word_count = text.split_whitespace().count();

        Ok(serde_json::json!({
            "size_bytes": size,
            "word_count": word_count,
            "char_count": text.len(),
        }))
    }

    /// Generate filename suggestion from PDF content
    async fn generate_suggestion(
        &self,
        path: &Path,
        config: &Config,
    ) -> Result<(String, String, Vec<String>, f32)> {
        let client = OllamaClient::new(&config.ollama_url);

        // Extract text preview
        let text = self.extract_text(path)?;
        let preview: String = text.chars().take(2000).collect();

        if preview.trim().is_empty() {
            return Ok((
                "scanned-document".to_string(),
                "Scanned PDF with no extractable text".to_string(),
                vec!["scanned".to_string(), "pdf".to_string()],
                0.3,
            ));
        }

        let prompt = format!(
            r#"Analyze this PDF text excerpt and provide:
1. A concise description (1-2 sentences)
2. A suggested filename (lowercase, hyphens, no extension, max 50 chars)
3. 3-5 relevant tags
4. Document category (invoice, report, letter, form, article, manual, contract, other)

Text excerpt:
---
{}
---

Format response as:
DESCRIPTION: <description>
FILENAME: <filename>
TAGS: <tag1>, <tag2>, <tag3>
CATEGORY: <category>"#,
            preview
        );

        let response = client.generate(&config.model, &prompt).await?;

        // Parse response
        let mut description = String::new();
        let mut filename = String::new();
        let mut tags = Vec::new();
        let mut category = String::new();

        for line in response.lines() {
            let line = line.trim();
            if let Some(desc) = line.strip_prefix("DESCRIPTION:") {
                description = desc.trim().to_string();
            } else if let Some(name) = line.strip_prefix("FILENAME:") {
                filename = sanitize_filename(name.trim());
            } else if let Some(tag_str) = line.strip_prefix("TAGS:") {
                tags = tag_str
                    .split(',')
                    .map(|t| t.trim().to_lowercase())
                    .filter(|t| !t.is_empty())
                    .collect();
            } else if let Some(cat) = line.strip_prefix("CATEGORY:") {
                category = cat.trim().to_lowercase();
            }
        }

        // Calculate confidence
        let confidence = calculate_confidence(&description, &filename, &tags, &category);

        Ok((
            if filename.is_empty() {
                generate_fallback_name(&description)
            } else {
                filename
            },
            description,
            tags,
            confidence,
        ))
    }
}

#[async_trait]
impl FileAnalyzer for PdfAnalyzer {
    fn name(&self) -> &'static str {
        "pdf"
    }

    fn supported_extensions(&self) -> &[&str] {
        &self.extensions
    }

    fn priority(&self) -> u8 {
        75
    }

    async fn analyze(&self, path: &Path, config: &Config) -> Result<AnalysisResult> {
        let start = Instant::now();

        // Extract metadata
        let metadata = self.extract_metadata(path)?;

        // Generate AI suggestion
        let (suggested_name, description, tags, confidence) =
            self.generate_suggestion(path, config).await?;

        let elapsed = start.elapsed().as_millis() as u64;

        let mut result = AnalysisResult::new(self.name())
            .with_name(suggested_name)
            .with_description(description)
            .with_tags(tags)
            .with_confidence(confidence)
            .with_metadata("pdf", metadata)
            .with_processing_time(elapsed);

        // Add document category if detected
        if let Some(cat) = result.category.as_ref() {
            if !cat.is_empty() {
                result = result.with_category(cat.clone());
            }
        }

        Ok(result)
    }
}

/// Sanitize a filename suggestion
fn sanitize_filename(name: &str) -> String {
    name.chars()
        .filter(|c| c.is_alphanumeric() || *c == '-' || *c == '_')
        .take(50)
        .collect::<String>()
        .to_lowercase()
        .trim_matches(|c| c == '-' || c == '_')
        .to_string()
}

/// Generate a fallback filename from description
fn generate_fallback_name(description: &str) -> String {
    let words: Vec<&str> = description
        .split_whitespace()
        .filter(|w| w.len() > 2)
        .take(4)
        .collect();

    if words.is_empty() {
        "document".to_string()
    } else {
        words.join("-").to_lowercase()
    }
}

/// Calculate confidence score
fn calculate_confidence(description: &str, filename: &str, tags: &[String], category: &str) -> f32 {
    let mut score = 0.0;

    if !description.is_empty() {
        score += 0.25;
    }
    if !filename.is_empty() && filename.len() > 5 {
        score += 0.25;
    }
    if tags.len() >= 3 {
        score += 0.25;
    }
    if !category.is_empty() && category != "other" {
        score += 0.25;
    }

    score
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_pdf_analyzer_extensions() {
        let analyzer = PdfAnalyzer::new();
        assert!(analyzer.can_analyze(Path::new("document.pdf")));
        assert!(analyzer.can_analyze(Path::new("DOCUMENT.PDF")));
        assert!(!analyzer.can_analyze(Path::new("image.png")));
    }

    #[test]
    fn test_sanitize_filename() {
        assert_eq!(sanitize_filename("Invoice #123"), "invoice123");
        assert_eq!(sanitize_filename("my-document"), "my-document");
    }
}
