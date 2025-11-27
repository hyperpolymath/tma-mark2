// SPDX-FileCopyrightText: 2024 Panoptes Contributors
// SPDX-License-Identifier: MIT

//! Video file analyzer

use async_trait::async_trait;
use std::path::Path;
use std::time::Instant;

use super::{AnalysisResult, FileAnalyzer};
use crate::config::Config;
use crate::error::Result;

/// Analyzer for video files
pub struct VideoAnalyzer {
    extensions: Vec<&'static str>,
}

impl Default for VideoAnalyzer {
    fn default() -> Self {
        Self::new()
    }
}

impl VideoAnalyzer {
    /// Create a new video analyzer
    pub fn new() -> Self {
        Self {
            extensions: vec![
                "mp4", "mkv", "avi", "mov", "wmv", "flv", "webm", "m4v", "mpeg", "mpg", "3gp",
                "ogv", "ts", "mts", "m2ts",
            ],
        }
    }

    /// Extract video metadata from file
    fn extract_metadata(&self, path: &Path) -> Result<serde_json::Value> {
        let file_meta = std::fs::metadata(path)?;

        // Basic metadata from file system
        Ok(serde_json::json!({
            "size_bytes": file_meta.len(),
            "extension": path.extension().and_then(|e| e.to_str()),
        }))
    }

    /// Generate filename suggestion from video file
    fn generate_suggestion(&self, path: &Path) -> (String, Vec<String>, f32) {
        let stem = path
            .file_stem()
            .and_then(|s| s.to_str())
            .unwrap_or("video");

        // Clean up the filename
        let cleaned = clean_video_name(stem);
        let tags = extract_video_tags(&cleaned);

        // Base confidence is low since we're just cleaning the name
        let confidence = 0.4;

        (cleaned, tags, confidence)
    }
}

/// Clean up a video filename
fn clean_video_name(name: &str) -> String {
    // Common patterns to remove
    let patterns = [
        // Resolution patterns
        r"1080p",
        r"720p",
        r"480p",
        r"2160p",
        r"4k",
        r"uhd",
        r"hd",
        // Codec patterns
        r"x264",
        r"x265",
        r"h264",
        r"h265",
        r"hevc",
        r"avc",
        r"xvid",
        r"divx",
        // Audio patterns
        r"aac",
        r"ac3",
        r"dts",
        r"mp3",
        // Source patterns
        r"bluray",
        r"blu-ray",
        r"bdrip",
        r"dvdrip",
        r"webrip",
        r"web-dl",
        r"hdtv",
        r"hdcam",
        // Release groups (common brackets)
        r"\[.*?\]",
        r"\(.*?\)",
        // Year patterns will be preserved
    ];

    let mut result = name.to_lowercase();

    // Replace dots and underscores with spaces
    result = result.replace(['.', '_'], " ");

    // Remove common patterns (case insensitive)
    for pattern in patterns {
        if let Ok(re) = regex::Regex::new(&format!(r"(?i){}", pattern)) {
            result = re.replace_all(&result, " ").to_string();
        }
    }

    // Clean up multiple spaces and trim
    let words: Vec<&str> = result.split_whitespace().collect();
    let cleaned = words.join("-");

    // Limit length
    cleaned.chars().take(50).collect()
}

/// Extract tags from video name
fn extract_video_tags(name: &str) -> Vec<String> {
    let mut tags = vec!["video".to_string()];

    // Check for year
    if let Ok(re) = regex::Regex::new(r"(19|20)\d{2}") {
        if let Some(m) = re.find(name) {
            tags.push(format!("year:{}", m.as_str()));
        }
    }

    // Check for series patterns (S01E01)
    if let Ok(re) = regex::Regex::new(r"(?i)s(\d{1,2})e(\d{1,2})") {
        if re.is_match(name) {
            tags.push("series".to_string());
            tags.push("tv-show".to_string());
        }
    }

    tags
}

#[async_trait]
impl FileAnalyzer for VideoAnalyzer {
    fn name(&self) -> &'static str {
        "video"
    }

    fn supported_extensions(&self) -> &[&str] {
        &self.extensions
    }

    fn priority(&self) -> u8 {
        70
    }

    async fn analyze(&self, path: &Path, _config: &Config) -> Result<AnalysisResult> {
        let start = Instant::now();

        // Extract metadata
        let metadata = self.extract_metadata(path)?;

        // Generate filename suggestion
        let (suggested_name, tags, confidence) = self.generate_suggestion(path);

        let elapsed = start.elapsed().as_millis() as u64;

        Ok(AnalysisResult::new(self.name())
            .with_name(suggested_name)
            .with_tags(tags)
            .with_confidence(confidence)
            .with_category("video")
            .with_metadata("video", metadata)
            .with_processing_time(elapsed))
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_video_analyzer_extensions() {
        let analyzer = VideoAnalyzer::new();
        assert!(analyzer.can_analyze(Path::new("movie.mp4")));
        assert!(analyzer.can_analyze(Path::new("video.MKV")));
        assert!(!analyzer.can_analyze(Path::new("audio.mp3")));
    }

    #[test]
    fn test_clean_video_name() {
        assert_eq!(
            clean_video_name("Movie.Name.2023.1080p.BluRay.x264"),
            "movie-name-2023"
        );
        assert_eq!(
            clean_video_name("Show_S01E05_Episode_Title"),
            "show-s01e05-episode-title"
        );
    }

    #[test]
    fn test_extract_video_tags() {
        let tags = extract_video_tags("movie-2023");
        assert!(tags.contains(&"video".to_string()));
        assert!(tags.contains(&"year:2023".to_string()));

        let tags = extract_video_tags("show-s01e05");
        assert!(tags.contains(&"series".to_string()));
    }
}
