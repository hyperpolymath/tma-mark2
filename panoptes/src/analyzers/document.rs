// SPDX-FileCopyrightText: 2024 Panoptes Contributors
// SPDX-License-Identifier: MIT

//! Document file analyzer (Office documents, text files, etc.)

use async_trait::async_trait;
use std::path::Path;
use std::time::Instant;

use super::{AnalysisResult, FileAnalyzer};
use crate::config::Config;
use crate::error::Result;

/// Analyzer for document files
pub struct DocumentAnalyzer {
    extensions: Vec<&'static str>,
}

impl Default for DocumentAnalyzer {
    fn default() -> Self {
        Self::new()
    }
}

impl DocumentAnalyzer {
    /// Create a new document analyzer
    pub fn new() -> Self {
        Self {
            extensions: vec![
                // Plain text
                "txt", "text", "log",
                // Rich text
                "rtf",
                // Office documents
                "doc", "docx", "odt",
                // Spreadsheets
                "xls", "xlsx", "ods", "csv", "tsv",
                // Presentations
                "ppt", "pptx", "odp",
                // eBooks
                "epub", "mobi", "azw", "azw3",
                // Other
                "pages", "numbers", "key",
            ],
        }
    }

    /// Detect document type from extension
    fn detect_type(&self, path: &Path) -> DocumentType {
        let ext = path
            .extension()
            .and_then(|e| e.to_str())
            .map(|e| e.to_lowercase())
            .unwrap_or_default();

        match ext.as_str() {
            "txt" | "text" | "log" | "rtf" => DocumentType::Text,
            "doc" | "docx" | "odt" | "pages" => DocumentType::WordProcessor,
            "xls" | "xlsx" | "ods" | "csv" | "tsv" | "numbers" => DocumentType::Spreadsheet,
            "ppt" | "pptx" | "odp" | "key" => DocumentType::Presentation,
            "epub" | "mobi" | "azw" | "azw3" => DocumentType::Ebook,
            _ => DocumentType::Unknown,
        }
    }

    /// Extract text content for analysis (txt files only for now)
    fn extract_text_preview(&self, path: &Path) -> Result<Option<String>> {
        let ext = path
            .extension()
            .and_then(|e| e.to_str())
            .map(|e| e.to_lowercase());

        match ext.as_deref() {
            Some("txt") | Some("text") | Some("log") | Some("csv") | Some("tsv") => {
                let content = std::fs::read_to_string(path)?;
                Ok(Some(content.chars().take(2000).collect()))
            }
            _ => Ok(None),
        }
    }

    /// Analyze document and generate suggestions
    fn analyze_document(&self, path: &Path) -> Result<DocumentMetrics> {
        let meta = std::fs::metadata(path)?;
        let doc_type = self.detect_type(path);
        let preview = self.extract_text_preview(path)?;

        let word_count = preview
            .as_ref()
            .map(|t| t.split_whitespace().count())
            .unwrap_or(0);

        let line_count = preview.as_ref().map(|t| t.lines().count()).unwrap_or(0);

        Ok(DocumentMetrics {
            doc_type,
            size_bytes: meta.len(),
            word_count,
            line_count,
            has_preview: preview.is_some(),
        })
    }

    /// Generate filename suggestion
    fn generate_suggestion(
        &self,
        path: &Path,
        metrics: &DocumentMetrics,
    ) -> (String, Vec<String>, String, f32) {
        let stem = path
            .file_stem()
            .and_then(|s| s.to_str())
            .unwrap_or("document");

        let mut tags = vec!["document".to_string()];

        // Add type-specific tags
        match metrics.doc_type {
            DocumentType::Text => tags.push("text".to_string()),
            DocumentType::WordProcessor => tags.push("word-doc".to_string()),
            DocumentType::Spreadsheet => tags.push("spreadsheet".to_string()),
            DocumentType::Presentation => tags.push("presentation".to_string()),
            DocumentType::Ebook => tags.push("ebook".to_string()),
            DocumentType::Unknown => {}
        }

        // Size tag
        let size_tag = match metrics.size_bytes {
            0..=10_000 => "small",
            10_001..=100_000 => "medium",
            100_001..=1_000_000 => "large",
            _ => "very-large",
        };
        tags.push(format!("size:{}", size_tag));

        // Generate description
        let description = format!(
            "{} document ({} bytes{})",
            metrics.doc_type.as_str(),
            metrics.size_bytes,
            if metrics.word_count > 0 {
                format!(", ~{} words", metrics.word_count)
            } else {
                String::new()
            }
        );

        // Clean the filename
        let cleaned_name = clean_document_name(stem);

        // Confidence is moderate
        let confidence = if metrics.has_preview { 0.6 } else { 0.4 };

        (cleaned_name, tags, description, confidence)
    }
}

#[derive(Debug, Clone, Copy)]
enum DocumentType {
    Text,
    WordProcessor,
    Spreadsheet,
    Presentation,
    Ebook,
    Unknown,
}

impl DocumentType {
    fn as_str(&self) -> &'static str {
        match self {
            Self::Text => "Text",
            Self::WordProcessor => "Word processor",
            Self::Spreadsheet => "Spreadsheet",
            Self::Presentation => "Presentation",
            Self::Ebook => "E-book",
            Self::Unknown => "Unknown",
        }
    }
}

#[derive(Debug)]
struct DocumentMetrics {
    doc_type: DocumentType,
    size_bytes: u64,
    word_count: usize,
    line_count: usize,
    has_preview: bool,
}

impl DocumentMetrics {
    fn to_json(&self) -> serde_json::Value {
        serde_json::json!({
            "type": self.doc_type.as_str(),
            "size_bytes": self.size_bytes,
            "word_count": self.word_count,
            "line_count": self.line_count,
        })
    }
}

/// Clean a document filename
fn clean_document_name(name: &str) -> String {
    // Remove common suffixes
    let patterns = ["_final", "_v1", "_v2", "_draft", "_copy", " (1)", " (2)"];

    let mut result = name.to_lowercase();
    for pattern in patterns {
        result = result.replace(pattern, "");
    }

    // Convert to kebab-case
    result
        .chars()
        .map(|c| if c.is_alphanumeric() { c } else { '-' })
        .collect::<String>()
        .split('-')
        .filter(|s| !s.is_empty())
        .collect::<Vec<_>>()
        .join("-")
        .chars()
        .take(50)
        .collect()
}

#[async_trait]
impl FileAnalyzer for DocumentAnalyzer {
    fn name(&self) -> &'static str {
        "document"
    }

    fn supported_extensions(&self) -> &[&str] {
        &self.extensions
    }

    fn priority(&self) -> u8 {
        55
    }

    async fn analyze(&self, path: &Path, _config: &Config) -> Result<AnalysisResult> {
        let start = Instant::now();

        // Analyze document
        let metrics = self.analyze_document(path)?;

        // Generate suggestion
        let (suggested_name, tags, description, confidence) =
            self.generate_suggestion(path, &metrics);

        let elapsed = start.elapsed().as_millis() as u64;

        let category = match metrics.doc_type {
            DocumentType::Text => "documents/text",
            DocumentType::WordProcessor => "documents/word",
            DocumentType::Spreadsheet => "documents/spreadsheet",
            DocumentType::Presentation => "documents/presentation",
            DocumentType::Ebook => "documents/ebook",
            DocumentType::Unknown => "documents",
        };

        Ok(AnalysisResult::new(self.name())
            .with_name(suggested_name)
            .with_description(description)
            .with_tags(tags)
            .with_category(category)
            .with_confidence(confidence)
            .with_metadata("document", metrics.to_json())
            .with_processing_time(elapsed))
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_document_analyzer_extensions() {
        let analyzer = DocumentAnalyzer::new();
        assert!(analyzer.can_analyze(Path::new("report.docx")));
        assert!(analyzer.can_analyze(Path::new("data.xlsx")));
        assert!(analyzer.can_analyze(Path::new("notes.txt")));
        assert!(!analyzer.can_analyze(Path::new("image.png")));
    }

    #[test]
    fn test_detect_type() {
        let analyzer = DocumentAnalyzer::new();
        assert!(matches!(
            analyzer.detect_type(Path::new("doc.txt")),
            DocumentType::Text
        ));
        assert!(matches!(
            analyzer.detect_type(Path::new("doc.xlsx")),
            DocumentType::Spreadsheet
        ));
    }

    #[test]
    fn test_clean_document_name() {
        assert_eq!(clean_document_name("Report_Final"), "report");
        assert_eq!(clean_document_name("My Document (1)"), "my-document");
        assert_eq!(clean_document_name("data_v2"), "data");
    }
}
