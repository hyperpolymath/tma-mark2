// SPDX-FileCopyrightText: 2024 Panoptes Contributors
// SPDX-License-Identifier: MIT

//! Source code file analyzer

use async_trait::async_trait;
use std::collections::HashMap;
use std::path::Path;
use std::time::Instant;

use super::{AnalysisResult, FileAnalyzer};
use crate::config::Config;
use crate::error::Result;

/// Analyzer for source code files
pub struct CodeAnalyzer {
    extensions: Vec<&'static str>,
    language_map: HashMap<&'static str, &'static str>,
}

impl Default for CodeAnalyzer {
    fn default() -> Self {
        Self::new()
    }
}

impl CodeAnalyzer {
    /// Create a new code analyzer
    pub fn new() -> Self {
        let extensions = vec![
            // Rust
            "rs",
            // Python
            "py", "pyi", "pyw",
            // JavaScript/TypeScript
            "js", "jsx", "ts", "tsx", "mjs", "cjs",
            // Web
            "html", "htm", "css", "scss", "sass", "less",
            // Systems
            "c", "h", "cpp", "hpp", "cc", "cxx", "hxx",
            // JVM
            "java", "kt", "kts", "scala", "clj", "cljs",
            // .NET
            "cs", "fs", "vb",
            // Go
            "go",
            // Ruby
            "rb", "erb",
            // PHP
            "php",
            // Shell
            "sh", "bash", "zsh", "fish",
            // Config
            "json", "yaml", "yml", "toml", "xml", "ini", "conf",
            // Data
            "sql", "graphql", "gql",
            // Markup
            "md", "mdx", "rst", "adoc", "tex",
            // Other
            "lua", "r", "jl", "ex", "exs", "erl", "hrl", "hs", "ml", "mli",
            "nim", "zig", "v", "d", "swift", "m", "mm",
        ];

        let mut language_map = HashMap::new();
        language_map.insert("rs", "rust");
        language_map.insert("py", "python");
        language_map.insert("pyi", "python");
        language_map.insert("js", "javascript");
        language_map.insert("jsx", "javascript");
        language_map.insert("ts", "typescript");
        language_map.insert("tsx", "typescript");
        language_map.insert("c", "c");
        language_map.insert("h", "c");
        language_map.insert("cpp", "cpp");
        language_map.insert("hpp", "cpp");
        language_map.insert("java", "java");
        language_map.insert("kt", "kotlin");
        language_map.insert("go", "go");
        language_map.insert("rb", "ruby");
        language_map.insert("php", "php");
        language_map.insert("cs", "csharp");
        language_map.insert("fs", "fsharp");
        language_map.insert("swift", "swift");
        language_map.insert("ex", "elixir");
        language_map.insert("exs", "elixir");
        language_map.insert("hs", "haskell");
        language_map.insert("ml", "ocaml");
        language_map.insert("lua", "lua");
        language_map.insert("r", "r");
        language_map.insert("jl", "julia");

        Self {
            extensions,
            language_map,
        }
    }

    /// Detect the programming language
    fn detect_language(&self, path: &Path) -> Option<&'static str> {
        path.extension()
            .and_then(|e| e.to_str())
            .and_then(|ext| self.language_map.get(ext.to_lowercase().as_str()))
            .copied()
    }

    /// Analyze code content
    fn analyze_content(&self, path: &Path) -> Result<CodeMetrics> {
        let content = std::fs::read_to_string(path)?;
        let lines: Vec<&str> = content.lines().collect();

        let total_lines = lines.len();
        let blank_lines = lines.iter().filter(|l| l.trim().is_empty()).count();
        let code_lines = total_lines - blank_lines;

        // Count comment lines (simple heuristic)
        let comment_lines = lines
            .iter()
            .filter(|l| {
                let trimmed = l.trim();
                trimmed.starts_with("//")
                    || trimmed.starts_with('#')
                    || trimmed.starts_with("/*")
                    || trimmed.starts_with('*')
                    || trimmed.starts_with("--")
                    || trimmed.starts_with(';')
            })
            .count();

        // Detect common patterns
        let has_tests = content.contains("#[test]")
            || content.contains("def test_")
            || content.contains("it('")
            || content.contains("describe('")
            || content.contains("@Test");

        let has_main = content.contains("fn main(")
            || content.contains("def main(")
            || content.contains("public static void main")
            || content.contains("func main(")
            || content.contains("if __name__");

        // Extract identifiers for naming
        let identifiers = extract_identifiers(&content);

        Ok(CodeMetrics {
            total_lines,
            code_lines,
            blank_lines,
            comment_lines,
            has_tests,
            has_main,
            identifiers,
            size_bytes: content.len(),
        })
    }

    /// Generate filename suggestion
    fn generate_suggestion(
        &self,
        path: &Path,
        language: Option<&str>,
        metrics: &CodeMetrics,
    ) -> (String, Vec<String>, String, f32) {
        let stem = path.file_stem().and_then(|s| s.to_str()).unwrap_or("code");

        let mut tags = Vec::new();

        // Add language tag
        if let Some(lang) = language {
            tags.push(lang.to_string());
        }

        // Add type tags
        if metrics.has_tests {
            tags.push("tests".to_string());
        }
        if metrics.has_main {
            tags.push("executable".to_string());
        }

        // Add size category
        let size_tag = match metrics.code_lines {
            0..=50 => "small",
            51..=200 => "medium",
            201..=500 => "large",
            _ => "very-large",
        };
        tags.push(format!("size:{}", size_tag));

        // Generate description
        let description = format!(
            "{} code file with {} lines ({} code, {} comments)",
            language.unwrap_or("Unknown"),
            metrics.total_lines,
            metrics.code_lines,
            metrics.comment_lines
        );

        // Confidence is moderate since we're keeping the original name
        let confidence = 0.5;

        (stem.to_string(), tags, description, confidence)
    }
}

#[derive(Debug)]
struct CodeMetrics {
    total_lines: usize,
    code_lines: usize,
    blank_lines: usize,
    comment_lines: usize,
    has_tests: bool,
    has_main: bool,
    identifiers: Vec<String>,
    size_bytes: usize,
}

impl CodeMetrics {
    fn to_json(&self) -> serde_json::Value {
        serde_json::json!({
            "total_lines": self.total_lines,
            "code_lines": self.code_lines,
            "blank_lines": self.blank_lines,
            "comment_lines": self.comment_lines,
            "has_tests": self.has_tests,
            "has_main": self.has_main,
            "size_bytes": self.size_bytes,
            "top_identifiers": self.identifiers.iter().take(10).collect::<Vec<_>>(),
        })
    }
}

/// Extract common identifiers from code
fn extract_identifiers(content: &str) -> Vec<String> {
    let mut identifiers = HashMap::new();

    // Simple word extraction (alphanumeric + underscore)
    for word in content.split(|c: char| !c.is_alphanumeric() && c != '_') {
        if word.len() >= 3 && word.chars().next().map(|c| c.is_alphabetic()).unwrap_or(false) {
            *identifiers.entry(word.to_string()).or_insert(0) += 1;
        }
    }

    // Sort by frequency and return top identifiers
    let mut sorted: Vec<_> = identifiers.into_iter().collect();
    sorted.sort_by(|a, b| b.1.cmp(&a.1));
    sorted.into_iter().take(20).map(|(k, _)| k).collect()
}

#[async_trait]
impl FileAnalyzer for CodeAnalyzer {
    fn name(&self) -> &'static str {
        "code"
    }

    fn supported_extensions(&self) -> &[&str] {
        &self.extensions
    }

    fn priority(&self) -> u8 {
        60
    }

    async fn analyze(&self, path: &Path, _config: &Config) -> Result<AnalysisResult> {
        let start = Instant::now();

        // Detect language
        let language = self.detect_language(path);

        // Analyze content
        let metrics = self.analyze_content(path)?;

        // Generate suggestion
        let (suggested_name, tags, description, confidence) =
            self.generate_suggestion(path, language, &metrics);

        let elapsed = start.elapsed().as_millis() as u64;

        let category = language
            .map(|l| format!("code/{}", l))
            .unwrap_or_else(|| "code".to_string());

        Ok(AnalysisResult::new(self.name())
            .with_name(suggested_name)
            .with_description(description)
            .with_tags(tags)
            .with_category(category)
            .with_confidence(confidence)
            .with_metadata("code", metrics.to_json())
            .with_processing_time(elapsed))
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_code_analyzer_extensions() {
        let analyzer = CodeAnalyzer::new();
        assert!(analyzer.can_analyze(Path::new("main.rs")));
        assert!(analyzer.can_analyze(Path::new("script.py")));
        assert!(analyzer.can_analyze(Path::new("app.tsx")));
        assert!(!analyzer.can_analyze(Path::new("image.png")));
    }

    #[test]
    fn test_language_detection() {
        let analyzer = CodeAnalyzer::new();
        assert_eq!(analyzer.detect_language(Path::new("main.rs")), Some("rust"));
        assert_eq!(
            analyzer.detect_language(Path::new("script.py")),
            Some("python")
        );
        assert_eq!(
            analyzer.detect_language(Path::new("app.tsx")),
            Some("typescript")
        );
    }

    #[test]
    fn test_extract_identifiers() {
        let content = "fn main() { let foo = bar; let baz = foo + bar; }";
        let ids = extract_identifiers(content);
        assert!(ids.contains(&"foo".to_string()));
        assert!(ids.contains(&"bar".to_string()));
    }
}
