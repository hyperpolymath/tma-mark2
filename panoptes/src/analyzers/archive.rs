// SPDX-FileCopyrightText: 2024 Panoptes Contributors
// SPDX-License-Identifier: MIT

//! Archive file analyzer (ZIP, TAR, etc.)

use async_trait::async_trait;
use std::io::{Read, Seek};
use std::path::Path;
use std::time::Instant;

use super::{AnalysisResult, FileAnalyzer};
use crate::config::Config;
use crate::error::{Error, Result};

/// Analyzer for archive files
pub struct ArchiveAnalyzer {
    extensions: Vec<&'static str>,
}

impl Default for ArchiveAnalyzer {
    fn default() -> Self {
        Self::new()
    }
}

impl ArchiveAnalyzer {
    /// Create a new archive analyzer
    pub fn new() -> Self {
        Self {
            extensions: vec![
                "zip", "tar", "gz", "tgz", "bz2", "tbz2", "xz", "txz", "7z", "rar", "iso", "dmg",
                "jar", "war", "ear", "apk", "ipa", "deb", "rpm", "pkg",
            ],
        }
    }

    /// Detect archive type
    fn detect_type(&self, path: &Path) -> ArchiveType {
        let name = path
            .file_name()
            .and_then(|n| n.to_str())
            .unwrap_or("")
            .to_lowercase();

        if name.ends_with(".tar.gz") || name.ends_with(".tgz") {
            ArchiveType::TarGz
        } else if name.ends_with(".tar.bz2") || name.ends_with(".tbz2") {
            ArchiveType::TarBz2
        } else if name.ends_with(".tar.xz") || name.ends_with(".txz") {
            ArchiveType::TarXz
        } else {
            match path.extension().and_then(|e| e.to_str()) {
                Some("zip") | Some("jar") | Some("war") | Some("ear") | Some("apk") => {
                    ArchiveType::Zip
                }
                Some("tar") => ArchiveType::Tar,
                Some("gz") => ArchiveType::Gzip,
                Some("bz2") => ArchiveType::Bzip2,
                Some("xz") => ArchiveType::Xz,
                Some("7z") => ArchiveType::SevenZip,
                Some("rar") => ArchiveType::Rar,
                Some("iso") => ArchiveType::Iso,
                Some("dmg") => ArchiveType::Dmg,
                Some("deb") => ArchiveType::Deb,
                Some("rpm") => ArchiveType::Rpm,
                _ => ArchiveType::Unknown,
            }
        }
    }

    /// Analyze ZIP archive
    fn analyze_zip(&self, path: &Path) -> Result<ArchiveMetrics> {
        let file = std::fs::File::open(path)?;
        let mut archive =
            zip::ZipArchive::new(file).map_err(|e| Error::analyzer(self.name(), path, e.to_string()))?;

        let mut metrics = ArchiveMetrics::default();
        metrics.archive_type = ArchiveType::Zip;
        metrics.file_count = archive.len();

        let mut extensions = std::collections::HashMap::new();
        let mut total_uncompressed = 0u64;

        for i in 0..archive.len() {
            if let Ok(file) = archive.by_index(i) {
                total_uncompressed += file.size();

                if let Some(ext) = Path::new(file.name())
                    .extension()
                    .and_then(|e| e.to_str())
                {
                    *extensions.entry(ext.to_lowercase()).or_insert(0) += 1;
                }

                // Track top-level entries
                let parts: Vec<&str> = file.name().split('/').collect();
                if parts.len() == 1 || (parts.len() == 2 && parts[1].is_empty()) {
                    metrics.top_entries.push(parts[0].to_string());
                }
            }
        }

        metrics.uncompressed_size = total_uncompressed;
        metrics.compressed_size = std::fs::metadata(path)?.len();
        metrics.compression_ratio = if total_uncompressed > 0 {
            metrics.compressed_size as f64 / total_uncompressed as f64
        } else {
            1.0
        };

        // Get top extensions
        let mut ext_vec: Vec<_> = extensions.into_iter().collect();
        ext_vec.sort_by(|a, b| b.1.cmp(&a.1));
        metrics.top_extensions = ext_vec.into_iter().take(5).map(|(e, _)| e).collect();

        Ok(metrics)
    }

    /// Analyze TAR archive (uncompressed)
    fn analyze_tar<R: Read>(&self, reader: R, path: &Path) -> Result<ArchiveMetrics> {
        let mut archive = tar::Archive::new(reader);

        let mut metrics = ArchiveMetrics::default();
        metrics.archive_type = ArchiveType::Tar;

        let mut extensions = std::collections::HashMap::new();
        let mut total_size = 0u64;
        let mut count = 0;

        for entry in archive
            .entries()
            .map_err(|e| Error::analyzer(self.name(), path, e.to_string()))?
        {
            if let Ok(entry) = entry {
                count += 1;
                total_size += entry.size();

                if let Ok(entry_path) = entry.path() {
                    if let Some(ext) = entry_path.extension().and_then(|e| e.to_str()) {
                        *extensions.entry(ext.to_lowercase()).or_insert(0) += 1;
                    }

                    // Track top-level entries
                    if let Some(first) = entry_path.components().next() {
                        let name = first.as_os_str().to_string_lossy().to_string();
                        if !metrics.top_entries.contains(&name) && metrics.top_entries.len() < 10 {
                            metrics.top_entries.push(name);
                        }
                    }
                }
            }
        }

        metrics.file_count = count;
        metrics.uncompressed_size = total_size;
        metrics.compressed_size = std::fs::metadata(path)?.len();

        let mut ext_vec: Vec<_> = extensions.into_iter().collect();
        ext_vec.sort_by(|a, b| b.1.cmp(&a.1));
        metrics.top_extensions = ext_vec.into_iter().take(5).map(|(e, _)| e).collect();

        Ok(metrics)
    }

    /// Analyze archive based on type
    fn analyze_archive(&self, path: &Path) -> Result<ArchiveMetrics> {
        let archive_type = self.detect_type(path);

        match archive_type {
            ArchiveType::Zip => self.analyze_zip(path),
            ArchiveType::Tar => {
                let file = std::fs::File::open(path)?;
                self.analyze_tar(file, path)
            }
            ArchiveType::TarGz | ArchiveType::Gzip => {
                let file = std::fs::File::open(path)?;
                let decoder = flate2::read::GzDecoder::new(file);
                let mut metrics = self.analyze_tar(decoder, path)?;
                metrics.archive_type = ArchiveType::TarGz;
                Ok(metrics)
            }
            _ => {
                // For unsupported types, return basic metrics
                let meta = std::fs::metadata(path)?;
                Ok(ArchiveMetrics {
                    archive_type,
                    compressed_size: meta.len(),
                    ..Default::default()
                })
            }
        }
    }

    /// Generate filename suggestion
    fn generate_suggestion(
        &self,
        path: &Path,
        metrics: &ArchiveMetrics,
    ) -> (String, Vec<String>, String, f32) {
        let stem = path
            .file_stem()
            .and_then(|s| s.to_str())
            .unwrap_or("archive");

        // Remove .tar from stem if present
        let stem = stem.strip_suffix(".tar").unwrap_or(stem);

        let mut tags = vec!["archive".to_string()];
        tags.push(metrics.archive_type.as_str().to_lowercase());

        // Add content-based tags
        if metrics.top_extensions.contains(&"rs".to_string()) {
            tags.push("rust-project".to_string());
        } else if metrics.top_extensions.contains(&"py".to_string()) {
            tags.push("python-project".to_string());
        } else if metrics.top_extensions.contains(&"js".to_string()) {
            tags.push("javascript-project".to_string());
        }

        // Generate description
        let description = format!(
            "{} archive with {} files ({} compressed)",
            metrics.archive_type.as_str(),
            metrics.file_count,
            format_size(metrics.compressed_size)
        );

        let confidence = if metrics.file_count > 0 { 0.6 } else { 0.3 };

        (stem.to_lowercase(), tags, description, confidence)
    }
}

#[derive(Debug, Clone, Copy, Default)]
enum ArchiveType {
    Zip,
    Tar,
    TarGz,
    TarBz2,
    TarXz,
    Gzip,
    Bzip2,
    Xz,
    SevenZip,
    Rar,
    Iso,
    Dmg,
    Deb,
    Rpm,
    #[default]
    Unknown,
}

impl ArchiveType {
    fn as_str(&self) -> &'static str {
        match self {
            Self::Zip => "ZIP",
            Self::Tar => "TAR",
            Self::TarGz => "TAR.GZ",
            Self::TarBz2 => "TAR.BZ2",
            Self::TarXz => "TAR.XZ",
            Self::Gzip => "GZIP",
            Self::Bzip2 => "BZIP2",
            Self::Xz => "XZ",
            Self::SevenZip => "7Z",
            Self::Rar => "RAR",
            Self::Iso => "ISO",
            Self::Dmg => "DMG",
            Self::Deb => "DEB",
            Self::Rpm => "RPM",
            Self::Unknown => "Unknown",
        }
    }
}

#[derive(Debug, Default)]
struct ArchiveMetrics {
    archive_type: ArchiveType,
    file_count: usize,
    compressed_size: u64,
    uncompressed_size: u64,
    compression_ratio: f64,
    top_extensions: Vec<String>,
    top_entries: Vec<String>,
}

impl ArchiveMetrics {
    fn to_json(&self) -> serde_json::Value {
        serde_json::json!({
            "type": self.archive_type.as_str(),
            "file_count": self.file_count,
            "compressed_size": self.compressed_size,
            "uncompressed_size": self.uncompressed_size,
            "compression_ratio": self.compression_ratio,
            "top_extensions": self.top_extensions,
            "top_entries": self.top_entries,
        })
    }
}

/// Format file size for display
fn format_size(bytes: u64) -> String {
    const KB: u64 = 1024;
    const MB: u64 = KB * 1024;
    const GB: u64 = MB * 1024;

    if bytes >= GB {
        format!("{:.1} GB", bytes as f64 / GB as f64)
    } else if bytes >= MB {
        format!("{:.1} MB", bytes as f64 / MB as f64)
    } else if bytes >= KB {
        format!("{:.1} KB", bytes as f64 / KB as f64)
    } else {
        format!("{} B", bytes)
    }
}

#[async_trait]
impl FileAnalyzer for ArchiveAnalyzer {
    fn name(&self) -> &'static str {
        "archive"
    }

    fn supported_extensions(&self) -> &[&str] {
        &self.extensions
    }

    fn priority(&self) -> u8 {
        50
    }

    async fn analyze(&self, path: &Path, _config: &Config) -> Result<AnalysisResult> {
        let start = Instant::now();

        // Analyze archive
        let metrics = self.analyze_archive(path)?;

        // Generate suggestion
        let (suggested_name, tags, description, confidence) =
            self.generate_suggestion(path, &metrics);

        let elapsed = start.elapsed().as_millis() as u64;

        Ok(AnalysisResult::new(self.name())
            .with_name(suggested_name)
            .with_description(description)
            .with_tags(tags)
            .with_category("archives")
            .with_confidence(confidence)
            .with_metadata("archive", metrics.to_json())
            .with_processing_time(elapsed))
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_archive_analyzer_extensions() {
        let analyzer = ArchiveAnalyzer::new();
        assert!(analyzer.can_analyze(Path::new("file.zip")));
        assert!(analyzer.can_analyze(Path::new("file.tar.gz")));
        assert!(analyzer.can_analyze(Path::new("app.jar")));
        assert!(!analyzer.can_analyze(Path::new("doc.pdf")));
    }

    #[test]
    fn test_detect_type() {
        let analyzer = ArchiveAnalyzer::new();
        assert!(matches!(
            analyzer.detect_type(Path::new("file.zip")),
            ArchiveType::Zip
        ));
        assert!(matches!(
            analyzer.detect_type(Path::new("file.tar.gz")),
            ArchiveType::TarGz
        ));
    }

    #[test]
    fn test_format_size() {
        assert_eq!(format_size(500), "500 B");
        assert_eq!(format_size(1500), "1.5 KB");
        assert_eq!(format_size(1_500_000), "1.4 MB");
    }
}
