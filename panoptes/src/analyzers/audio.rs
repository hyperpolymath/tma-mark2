// SPDX-FileCopyrightText: 2024 Panoptes Contributors
// SPDX-License-Identifier: MIT

//! Audio file analyzer

use async_trait::async_trait;
use std::path::Path;
use std::time::Instant;
use symphonia::core::formats::FormatOptions;
use symphonia::core::io::MediaSourceStream;
use symphonia::core::meta::MetadataOptions;
use symphonia::core::probe::Hint;

use super::{AnalysisResult, FileAnalyzer};
use crate::config::Config;
use crate::error::{Error, Result};

/// Analyzer for audio files
pub struct AudioAnalyzer {
    extensions: Vec<&'static str>,
}

impl Default for AudioAnalyzer {
    fn default() -> Self {
        Self::new()
    }
}

impl AudioAnalyzer {
    /// Create a new audio analyzer
    pub fn new() -> Self {
        Self {
            extensions: vec![
                "mp3", "wav", "flac", "ogg", "m4a", "aac", "wma", "aiff", "opus", "webm",
            ],
        }
    }

    /// Extract audio metadata using symphonia
    fn extract_metadata(&self, path: &Path) -> Result<AudioMetadata> {
        let file = std::fs::File::open(path)?;
        let mss = MediaSourceStream::new(Box::new(file), Default::default());

        let mut hint = Hint::new();
        if let Some(ext) = path.extension().and_then(|e| e.to_str()) {
            hint.with_extension(ext);
        }

        let format_opts = FormatOptions::default();
        let metadata_opts = MetadataOptions::default();

        let probed = symphonia::default::get_probe()
            .format(&hint, mss, &format_opts, &metadata_opts)
            .map_err(|e| Error::analyzer(self.name(), path, e.to_string()))?;

        let mut metadata = AudioMetadata::default();

        // Get format info
        let format = probed.format;

        // Get track info
        if let Some(track) = format.tracks().first() {
            if let Some(sample_rate) = track.codec_params.sample_rate {
                metadata.sample_rate = Some(sample_rate);
            }
            if let Some(channels) = track.codec_params.channels {
                metadata.channels = Some(channels.count() as u32);
            }
            if let Some(bits) = track.codec_params.bits_per_sample {
                metadata.bits_per_sample = Some(bits);
            }
        }

        // Try to get duration
        if let Some(track) = format.tracks().first() {
            if let Some(n_frames) = track.codec_params.n_frames {
                if let Some(sample_rate) = track.codec_params.sample_rate {
                    metadata.duration_secs = Some(n_frames as f64 / sample_rate as f64);
                }
            }
        }

        // Extract tags from metadata
        if let Some(meta) = format.metadata().current() {
            for tag in meta.tags() {
                match tag.std_key {
                    Some(symphonia::core::meta::StandardTagKey::TrackTitle) => {
                        metadata.title = Some(tag.value.to_string());
                    }
                    Some(symphonia::core::meta::StandardTagKey::Artist) => {
                        metadata.artist = Some(tag.value.to_string());
                    }
                    Some(symphonia::core::meta::StandardTagKey::Album) => {
                        metadata.album = Some(tag.value.to_string());
                    }
                    Some(symphonia::core::meta::StandardTagKey::Genre) => {
                        metadata.genre = Some(tag.value.to_string());
                    }
                    Some(symphonia::core::meta::StandardTagKey::TrackNumber) => {
                        metadata.track_number = tag.value.to_string().parse().ok();
                    }
                    Some(symphonia::core::meta::StandardTagKey::Date) => {
                        metadata.year = tag.value.to_string().chars().take(4).collect::<String>().parse().ok();
                    }
                    _ => {}
                }
            }
        }

        Ok(metadata)
    }

    /// Generate filename from metadata
    fn generate_filename(&self, metadata: &AudioMetadata) -> (String, Vec<String>, f32) {
        let mut parts = Vec::new();
        let mut tags = Vec::new();
        let mut confidence = 0.3; // Base confidence

        // Build filename from metadata
        if let Some(ref artist) = metadata.artist {
            parts.push(sanitize_part(artist));
            tags.push(artist.to_lowercase());
            confidence += 0.2;
        }

        if let Some(ref title) = metadata.title {
            parts.push(sanitize_part(title));
            confidence += 0.2;
        }

        if let Some(ref album) = metadata.album {
            tags.push(format!("album:{}", sanitize_part(album)));
            confidence += 0.1;
        }

        if let Some(ref genre) = metadata.genre {
            tags.push(genre.to_lowercase());
            confidence += 0.1;
        }

        // Add audio type tag
        tags.push("audio".to_string());

        let filename = if parts.is_empty() {
            "untitled-audio".to_string()
        } else {
            parts.join("-")
        };

        (filename.chars().take(50).collect(), tags, confidence.min(1.0))
    }
}

#[derive(Default, Debug)]
struct AudioMetadata {
    title: Option<String>,
    artist: Option<String>,
    album: Option<String>,
    genre: Option<String>,
    track_number: Option<u32>,
    year: Option<u32>,
    duration_secs: Option<f64>,
    sample_rate: Option<u32>,
    channels: Option<u32>,
    bits_per_sample: Option<u32>,
}

impl AudioMetadata {
    fn to_json(&self) -> serde_json::Value {
        serde_json::json!({
            "title": self.title,
            "artist": self.artist,
            "album": self.album,
            "genre": self.genre,
            "track_number": self.track_number,
            "year": self.year,
            "duration_secs": self.duration_secs,
            "sample_rate": self.sample_rate,
            "channels": self.channels,
            "bits_per_sample": self.bits_per_sample,
        })
    }
}

fn sanitize_part(s: &str) -> String {
    s.chars()
        .filter(|c| c.is_alphanumeric() || *c == ' ')
        .collect::<String>()
        .split_whitespace()
        .collect::<Vec<_>>()
        .join("-")
        .to_lowercase()
}

#[async_trait]
impl FileAnalyzer for AudioAnalyzer {
    fn name(&self) -> &'static str {
        "audio"
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

        // Generate filename and tags
        let (suggested_name, tags, confidence) = self.generate_filename(&metadata);

        let elapsed = start.elapsed().as_millis() as u64;

        let mut result = AnalysisResult::new(self.name())
            .with_name(suggested_name)
            .with_tags(tags)
            .with_confidence(confidence)
            .with_metadata("audio", metadata.to_json())
            .with_processing_time(elapsed);

        // Add description from metadata
        let desc_parts: Vec<String> = [
            metadata.title.as_deref().map(|t| format!("\"{}\"", t)),
            metadata.artist.as_deref().map(|a| format!("by {}", a)),
            metadata.album.as_deref().map(|a| format!("from album \"{}\"", a)),
        ]
        .into_iter()
        .flatten()
        .collect();

        if !desc_parts.is_empty() {
            result = result.with_description(desc_parts.join(" "));
        }

        // Set category based on genre or default
        if let Some(ref genre) = metadata.genre {
            result = result.with_category(format!("music/{}", genre.to_lowercase()));
        } else {
            result = result.with_category("audio");
        }

        Ok(result)
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_audio_analyzer_extensions() {
        let analyzer = AudioAnalyzer::new();
        assert!(analyzer.can_analyze(Path::new("song.mp3")));
        assert!(analyzer.can_analyze(Path::new("audio.FLAC")));
        assert!(!analyzer.can_analyze(Path::new("video.mp4")));
    }

    #[test]
    fn test_sanitize_part() {
        assert_eq!(sanitize_part("The Beatles"), "the-beatles");
        assert_eq!(sanitize_part("Song #1!"), "song-1");
    }
}
