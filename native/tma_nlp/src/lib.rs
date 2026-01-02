// SPDX-License-Identifier: AGPL-3.0-or-later
//! tma_nlp - Natural Language Processing NIF for tma-mark2
//!
//! Provides text processing primitives:
//! - Approximate string matching (agrep-like)
//! - Similarity detection for plagiarism checking
//! - N-gram generation for text analysis
//! - Text normalization (Unicode-aware)
//! - Word extraction and tokenization

use rayon::prelude::*;
use rustler::{Encoder, Env, NifResult, Term};
use strsim::{
    damerau_levenshtein, jaro_winkler, levenshtein, normalized_damerau_levenshtein,
    normalized_levenshtein,
};
use unicode_normalization::UnicodeNormalization;

mod atoms {
    rustler::atoms! {
        ok,
        error,
        invalid_input,
    }
}

// ============================================================
// STRING SIMILARITY METRICS
// ============================================================

/// Calculate Jaro-Winkler similarity (0.0 to 1.0)
/// Good for short strings like names
#[rustler::nif]
fn jaro_winkler_similarity(s1: String, s2: String) -> f64 {
    jaro_winkler(&s1, &s2)
}

/// Calculate Levenshtein edit distance
/// Returns the minimum number of single-character edits
#[rustler::nif]
fn levenshtein_distance(s1: String, s2: String) -> usize {
    levenshtein(&s1, &s2)
}

/// Calculate normalized Levenshtein similarity (0.0 to 1.0)
#[rustler::nif]
fn levenshtein_similarity(s1: String, s2: String) -> f64 {
    normalized_levenshtein(&s1, &s2)
}

/// Calculate Damerau-Levenshtein distance
/// Includes transpositions as a single edit
#[rustler::nif]
fn damerau_levenshtein_distance(s1: String, s2: String) -> usize {
    damerau_levenshtein(&s1, &s2)
}

/// Calculate normalized Damerau-Levenshtein similarity (0.0 to 1.0)
#[rustler::nif]
fn damerau_levenshtein_similarity(s1: String, s2: String) -> f64 {
    normalized_damerau_levenshtein(&s1, &s2)
}

// ============================================================
// APPROXIMATE GREP (AGREP)
// ============================================================

/// Find approximate matches in a list of strings (agrep-like)
/// Returns list of {candidate, similarity_score} tuples
#[rustler::nif]
fn agrep<'a>(
    env: Env<'a>,
    pattern: String,
    candidates: Vec<String>,
    threshold: f64,
) -> Term<'a> {
    let pattern_lower = pattern.to_lowercase();

    let matches: Vec<(String, f64)> = candidates
        .par_iter()
        .filter_map(|candidate| {
            let candidate_lower = candidate.to_lowercase();
            let similarity = jaro_winkler(&pattern_lower, &candidate_lower);
            if similarity >= threshold {
                Some((candidate.clone(), similarity))
            } else {
                None
            }
        })
        .collect();

    // Sort by similarity descending
    let mut sorted = matches;
    sorted.sort_by(|a, b| b.1.partial_cmp(&a.1).unwrap_or(std::cmp::Ordering::Equal));

    (atoms::ok(), sorted).encode(env)
}

/// Find approximate matches using Levenshtein distance
/// Returns matches within max_distance edits
#[rustler::nif]
fn agrep_levenshtein<'a>(
    env: Env<'a>,
    pattern: String,
    candidates: Vec<String>,
    max_distance: usize,
) -> Term<'a> {
    let pattern_lower = pattern.to_lowercase();

    let matches: Vec<(String, usize)> = candidates
        .par_iter()
        .filter_map(|candidate| {
            let candidate_lower = candidate.to_lowercase();
            let distance = levenshtein(&pattern_lower, &candidate_lower);
            if distance <= max_distance {
                Some((candidate.clone(), distance))
            } else {
                None
            }
        })
        .collect();

    // Sort by distance ascending
    let mut sorted = matches;
    sorted.sort_by_key(|x| x.1);

    (atoms::ok(), sorted).encode(env)
}

// ============================================================
// TEXT NORMALIZATION
// ============================================================

/// Normalize text: NFC normalization, lowercase, collapse whitespace
#[rustler::nif]
fn normalize_text(text: String) -> String {
    text.nfc()
        .collect::<String>()
        .to_lowercase()
        .split_whitespace()
        .collect::<Vec<&str>>()
        .join(" ")
}

/// Normalize text preserving case
#[rustler::nif]
fn normalize_text_preserve_case(text: String) -> String {
    text.nfc()
        .collect::<String>()
        .split_whitespace()
        .collect::<Vec<&str>>()
        .join(" ")
}

/// Remove diacritics/accents from text
#[rustler::nif]
fn remove_diacritics(text: String) -> String {
    use unicode_normalization::char::decompose_canonical;

    text.chars()
        .flat_map(|c| {
            let mut base = None;
            decompose_canonical(c, |d| {
                if base.is_none() && !d.is_ascii() {
                    // Check if it's a combining character
                    if !(d >= '\u{0300}' && d <= '\u{036f}') {
                        base = Some(d);
                    }
                } else if base.is_none() {
                    base = Some(d);
                }
            });
            base
        })
        .collect()
}

// ============================================================
// TOKENIZATION
// ============================================================

/// Extract words from text (Unicode-aware word segmentation)
#[rustler::nif]
fn extract_words(text: String) -> Vec<String> {
    use unicode_segmentation::UnicodeSegmentation;

    text.unicode_words().map(|w| w.to_string()).collect()
}

/// Extract words and convert to lowercase
#[rustler::nif]
fn extract_words_lowercase(text: String) -> Vec<String> {
    use unicode_segmentation::UnicodeSegmentation;

    text.unicode_words().map(|w| w.to_lowercase()).collect()
}

/// Count words in text
#[rustler::nif]
fn word_count(text: String) -> usize {
    use unicode_segmentation::UnicodeSegmentation;
    text.unicode_words().count()
}

/// Extract sentences from text
#[rustler::nif]
fn extract_sentences(text: String) -> Vec<String> {
    use unicode_segmentation::UnicodeSegmentation;
    text.unicode_sentences().map(|s| s.trim().to_string()).collect()
}

// ============================================================
// N-GRAM GENERATION
// ============================================================

/// Generate character n-grams
#[rustler::nif]
fn char_ngrams(text: String, n: usize) -> Vec<String> {
    if n == 0 || text.is_empty() {
        return vec![];
    }

    let chars: Vec<char> = text.chars().collect();
    if chars.len() < n {
        return vec![];
    }

    chars
        .windows(n)
        .map(|w| w.iter().collect())
        .collect()
}

/// Generate word n-grams
#[rustler::nif]
fn word_ngrams(words: Vec<String>, n: usize) -> Vec<String> {
    if n == 0 || words.is_empty() || words.len() < n {
        return vec![];
    }

    words.windows(n).map(|w| w.join(" ")).collect()
}

/// Generate word n-grams from text
#[rustler::nif]
fn text_word_ngrams(text: String, n: usize) -> Vec<String> {
    use unicode_segmentation::UnicodeSegmentation;

    if n == 0 {
        return vec![];
    }

    let words: Vec<&str> = text.unicode_words().collect();
    if words.len() < n {
        return vec![];
    }

    words.windows(n).map(|w| w.join(" ")).collect()
}

// ============================================================
// PLAGIARISM DETECTION
// ============================================================

/// Compare two texts and return similarity metrics
/// Returns {ok, %{jaccard: f64, overlap: f64, common_ngrams: usize, total_ngrams: usize}}
#[rustler::nif]
fn text_similarity<'a>(env: Env<'a>, text1: String, text2: String, ngram_size: usize) -> Term<'a> {
    use std::collections::HashSet;

    if ngram_size == 0 {
        return (atoms::error(), atoms::invalid_input()).encode(env);
    }

    // Normalize texts
    let norm1 = normalize_text_internal(&text1);
    let norm2 = normalize_text_internal(&text2);

    // Generate word n-grams
    let words1: Vec<&str> = norm1.split_whitespace().collect();
    let words2: Vec<&str> = norm2.split_whitespace().collect();

    if words1.len() < ngram_size || words2.len() < ngram_size {
        return (
            atoms::ok(),
            vec![
                ("jaccard", 0.0),
                ("overlap", 0.0),
                ("common_ngrams", 0.0),
                ("total_ngrams", 0.0),
            ],
        )
            .encode(env);
    }

    let ngrams1: HashSet<String> = words1.windows(ngram_size).map(|w| w.join(" ")).collect();

    let ngrams2: HashSet<String> = words2.windows(ngram_size).map(|w| w.join(" ")).collect();

    // Calculate metrics
    let intersection = ngrams1.intersection(&ngrams2).count() as f64;
    let union = ngrams1.union(&ngrams2).count() as f64;
    let min_size = ngrams1.len().min(ngrams2.len()) as f64;

    let jaccard = if union > 0.0 { intersection / union } else { 0.0 };
    let overlap = if min_size > 0.0 {
        intersection / min_size
    } else {
        0.0
    };

    (
        atoms::ok(),
        vec![
            ("jaccard", jaccard),
            ("overlap", overlap),
            ("common_ngrams", intersection),
            ("total_ngrams", union),
        ],
    )
        .encode(env)
}

/// Find common n-grams between two texts
#[rustler::nif]
fn find_common_ngrams<'a>(
    env: Env<'a>,
    text1: String,
    text2: String,
    ngram_size: usize,
) -> Term<'a> {
    use std::collections::HashSet;

    if ngram_size == 0 {
        return (atoms::error(), atoms::invalid_input()).encode(env);
    }

    let norm1 = normalize_text_internal(&text1);
    let norm2 = normalize_text_internal(&text2);

    let words1: Vec<&str> = norm1.split_whitespace().collect();
    let words2: Vec<&str> = norm2.split_whitespace().collect();

    if words1.len() < ngram_size || words2.len() < ngram_size {
        return (atoms::ok(), Vec::<String>::new()).encode(env);
    }

    let ngrams1: HashSet<String> = words1.windows(ngram_size).map(|w| w.join(" ")).collect();

    let ngrams2: HashSet<String> = words2.windows(ngram_size).map(|w| w.join(" ")).collect();

    let common: Vec<String> = ngrams1.intersection(&ngrams2).cloned().collect();

    (atoms::ok(), common).encode(env)
}

// ============================================================
// SPELLING SUGGESTIONS
// ============================================================

/// Find closest matches from a dictionary
/// Returns top N matches sorted by similarity
#[rustler::nif]
fn spelling_suggestions<'a>(
    env: Env<'a>,
    word: String,
    dictionary: Vec<String>,
    max_suggestions: usize,
) -> Term<'a> {
    if max_suggestions == 0 {
        return (atoms::ok(), Vec::<(String, f64)>::new()).encode(env);
    }

    let word_lower = word.to_lowercase();

    let mut scored: Vec<(String, f64)> = dictionary
        .par_iter()
        .map(|dict_word| {
            let dict_lower = dict_word.to_lowercase();
            let score = jaro_winkler(&word_lower, &dict_lower);
            (dict_word.clone(), score)
        })
        .collect();

    // Sort by score descending
    scored.sort_by(|a, b| b.1.partial_cmp(&a.1).unwrap_or(std::cmp::Ordering::Equal));

    // Take top N
    scored.truncate(max_suggestions);

    (atoms::ok(), scored).encode(env)
}

// ============================================================
// TEXT STATISTICS
// ============================================================

/// Calculate text statistics
/// Returns {word_count, sentence_count, avg_word_length, unique_words}
#[rustler::nif]
fn text_stats<'a>(env: Env<'a>, text: String) -> Term<'a> {
    use std::collections::HashSet;
    use unicode_segmentation::UnicodeSegmentation;

    let words: Vec<&str> = text.unicode_words().collect();
    let sentences: Vec<&str> = text.unicode_sentences().collect();

    let word_count = words.len();
    let sentence_count = sentences.len();

    let total_chars: usize = words.iter().map(|w| w.chars().count()).sum();
    let avg_word_length = if word_count > 0 {
        total_chars as f64 / word_count as f64
    } else {
        0.0
    };

    let unique_words: HashSet<String> = words.iter().map(|w| w.to_lowercase()).collect();
    let unique_count = unique_words.len();

    let vocabulary_richness = if word_count > 0 {
        unique_count as f64 / word_count as f64
    } else {
        0.0
    };

    (
        atoms::ok(),
        vec![
            ("word_count", word_count as f64),
            ("sentence_count", sentence_count as f64),
            ("avg_word_length", avg_word_length),
            ("unique_words", unique_count as f64),
            ("vocabulary_richness", vocabulary_richness),
        ],
    )
        .encode(env)
}

// ============================================================
// HELPER FUNCTIONS
// ============================================================

fn normalize_text_internal(text: &str) -> String {
    text.nfc()
        .collect::<String>()
        .to_lowercase()
        .split_whitespace()
        .collect::<Vec<&str>>()
        .join(" ")
}

// ============================================================
// NIF INITIALIZATION
// ============================================================

rustler::init!(
    "Elixir.EtmaHandler.Nlp.Native",
    [
        // Similarity metrics
        jaro_winkler_similarity,
        levenshtein_distance,
        levenshtein_similarity,
        damerau_levenshtein_distance,
        damerau_levenshtein_similarity,
        // Agrep
        agrep,
        agrep_levenshtein,
        // Normalization
        normalize_text,
        normalize_text_preserve_case,
        remove_diacritics,
        // Tokenization
        extract_words,
        extract_words_lowercase,
        word_count,
        extract_sentences,
        // N-grams
        char_ngrams,
        word_ngrams,
        text_word_ngrams,
        // Plagiarism
        text_similarity,
        find_common_ngrams,
        // Spelling
        spelling_suggestions,
        // Statistics
        text_stats,
    ]
);
