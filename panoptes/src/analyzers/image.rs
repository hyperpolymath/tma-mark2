// SPDX-FileCopyrightText: 2024 Panoptes Contributors
// SPDX-License-Identifier: MIT

//! Image file analyzer using Moondream vision model

use async_trait::async_trait;
use std::path::Path;
use std::time::Instant;

use super::{AnalysisResult, FileAnalyzer};
use crate::config::Config;
use crate::error::{Error, Result};
use crate::ollama::OllamaClient;

/// Analyzer for image files
pub struct ImageAnalyzer {
    extensions: Vec<&'static str>,
}

impl Default for ImageAnalyzer {
    fn default() -> Self {
        Self::new()
    }
}

impl ImageAnalyzer {
    /// Create a new image analyzer
    pub fn new() -> Self {
        Self {
            extensions: vec![
                "jpg", "jpeg", "png", "gif", "webp", "bmp", "tiff", "tif", "ico", "svg", "heic",
                "heif", "avif", "raw", "cr2", "nef", "arw", "dng",
            ],
        }
    }

    /// Extract basic image metadata
    fn extract_metadata(&self, path: &Path) -> Result<serde_json::Value> {
        let img = image::open(path).map_err(|e| Error::analyzer(self.name(), path, e.to_string()))?;

        let (width, height) = img.dimensions();
        let color_type = format!("{:?}", img.color());

        Ok(serde_json::json!({
            "width": width,
            "height": height,
            "color_type": color_type,
            "aspect_ratio": format!("{:.2}", width as f64 / height as f64),
        }))
    }

    /// Generate AI description of the image
    async fn generate_description(
        &self,
        path: &Path,
        config: &Config,
    ) -> Result<(String, Vec<String>, f32)> {
        let client = OllamaClient::new(&config.ollama_url);

        // Read image and encode to base64
        let image_data = tokio::fs::read(path).await?;
        let base64_image = base64::Engine::encode(&base64::engine::general_purpose::STANDARD, &image_data);

        // Ask Moondream to describe the image
        let prompt = r#"Describe this image concisely. Then suggest:
1. A filename (lowercase, hyphens, no extension, max 50 chars)
2. 3-5 relevant tags (comma-separated)

Format your response exactly as:
DESCRIPTION: <description>
FILENAME: <suggested-filename>
TAGS: <tag1>, <tag2>, <tag3>"#;

        let response = client
            .generate_with_image(&config.model, prompt, &base64_image)
            .await?;

        // Parse response
        let mut description = String::new();
        let mut filename = String::new();
        let mut tags = Vec::new();

        for line in response.lines() {
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
            }
        }

        // Calculate confidence based on response completeness
        let confidence = calculate_confidence(&description, &filename, &tags);

        Ok((
            if filename.is_empty() {
                generate_fallback_name(&description)
            } else {
                filename
            },
            tags,
            confidence,
        ))
    }
}

#[async_trait]
impl FileAnalyzer for ImageAnalyzer {
    fn name(&self) -> &'static str {
        "image"
    }

    fn supported_extensions(&self) -> &[&str] {
        &self.extensions
    }

    fn priority(&self) -> u8 {
        80
    }

    async fn analyze(&self, path: &Path, config: &Config) -> Result<AnalysisResult> {
        let start = Instant::now();

        // Extract basic metadata
        let metadata = self.extract_metadata(path)?;

        // Generate AI description
        let (suggested_name, tags, confidence) = self.generate_description(path, config).await?;

        let elapsed = start.elapsed().as_millis() as u64;

        Ok(AnalysisResult::new(self.name())
            .with_name(suggested_name)
            .with_tags(tags)
            .with_confidence(confidence)
            .with_metadata("image", metadata)
            .with_processing_time(elapsed))
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
        "unnamed-image".to_string()
    } else {
        words.join("-").to_lowercase()
    }
}

/// Calculate confidence score based on response quality
fn calculate_confidence(description: &str, filename: &str, tags: &[String]) -> f32 {
    let mut score = 0.0;

    // Description quality (0-0.4)
    if !description.is_empty() {
        score += 0.2;
        if description.len() > 20 {
            score += 0.1;
        }
        if description.len() > 50 {
            score += 0.1;
        }
    }

    // Filename quality (0-0.3)
    if !filename.is_empty() {
        score += 0.15;
        if filename.len() > 5 {
            score += 0.15;
        }
    }

    // Tags quality (0-0.3)
    if !tags.is_empty() {
        score += 0.1;
        if tags.len() >= 3 {
            score += 0.1;
        }
        if tags.len() >= 5 {
            score += 0.1;
        }
    }

    score.min(1.0)
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_sanitize_filename() {
        assert_eq!(sanitize_filename("Hello World!"), "helloworld");
        assert_eq!(sanitize_filename("my-file-name"), "my-file-name");
        assert_eq!(sanitize_filename("---test---"), "test");
        assert_eq!(sanitize_filename("UPPERCASE"), "uppercase");
    }

    #[test]
    fn test_fallback_name() {
        assert_eq!(
            generate_fallback_name("A beautiful sunset over mountains"),
            "beautiful-sunset-over-mountains"
        );
        assert_eq!(generate_fallback_name(""), "unnamed-image");
        assert_eq!(generate_fallback_name("a b c"), "unnamed-image");
    }

    #[test]
    fn test_confidence_calculation() {
        // Empty response
        assert!(calculate_confidence("", "", &[]) < 0.1);

        // Full response
        let tags = vec!["tag1".to_string(), "tag2".to_string(), "tag3".to_string()];
        let confidence = calculate_confidence(
            "A detailed description of the image with plenty of information",
            "my-image-name",
            &tags,
        );
        assert!(confidence > 0.8);
    }

    #[test]
    fn test_image_analyzer_extensions() {
        let analyzer = ImageAnalyzer::new();
        assert!(analyzer.can_analyze(Path::new("test.jpg")));
        assert!(analyzer.can_analyze(Path::new("test.PNG")));
        assert!(!analyzer.can_analyze(Path::new("test.pdf")));
    }
}
