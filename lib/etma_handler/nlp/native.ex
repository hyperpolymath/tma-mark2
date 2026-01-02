# SPDX-License-Identifier: AGPL-3.0-or-later
defmodule EtmaHandler.Nlp.Native do
  @moduledoc """
  Native Rust NIF bindings for NLP operations.

  Provides high-performance text processing:
  - String similarity metrics (Jaro-Winkler, Levenshtein)
  - Approximate grep (agrep)
  - Text normalization (Unicode-aware)
  - Tokenization and word extraction
  - N-gram generation
  - Plagiarism detection via text similarity
  - Spelling suggestions
  - Text statistics
  """

  use Rustler,
    otp_app: :etma_handler,
    crate: :tma_nlp

  # Similarity metrics
  @doc "Calculate Jaro-Winkler similarity (0.0 to 1.0)"
  def jaro_winkler_similarity(_s1, _s2), do: :erlang.nif_error(:nif_not_loaded)

  @doc "Calculate Levenshtein edit distance"
  def levenshtein_distance(_s1, _s2), do: :erlang.nif_error(:nif_not_loaded)

  @doc "Calculate normalized Levenshtein similarity (0.0 to 1.0)"
  def levenshtein_similarity(_s1, _s2), do: :erlang.nif_error(:nif_not_loaded)

  @doc "Calculate Damerau-Levenshtein distance (includes transpositions)"
  def damerau_levenshtein_distance(_s1, _s2), do: :erlang.nif_error(:nif_not_loaded)

  @doc "Calculate normalized Damerau-Levenshtein similarity (0.0 to 1.0)"
  def damerau_levenshtein_similarity(_s1, _s2), do: :erlang.nif_error(:nif_not_loaded)

  # Agrep
  @doc "Find approximate matches using Jaro-Winkler similarity"
  def agrep(_pattern, _candidates, _threshold), do: :erlang.nif_error(:nif_not_loaded)

  @doc "Find approximate matches using Levenshtein distance"
  def agrep_levenshtein(_pattern, _candidates, _max_distance),
    do: :erlang.nif_error(:nif_not_loaded)

  # Normalization
  @doc "Normalize text: NFC, lowercase, collapse whitespace"
  def normalize_text(_text), do: :erlang.nif_error(:nif_not_loaded)

  @doc "Normalize text preserving case"
  def normalize_text_preserve_case(_text), do: :erlang.nif_error(:nif_not_loaded)

  @doc "Remove diacritics/accents from text"
  def remove_diacritics(_text), do: :erlang.nif_error(:nif_not_loaded)

  # Tokenization
  @doc "Extract words from text (Unicode-aware)"
  def extract_words(_text), do: :erlang.nif_error(:nif_not_loaded)

  @doc "Extract words and convert to lowercase"
  def extract_words_lowercase(_text), do: :erlang.nif_error(:nif_not_loaded)

  @doc "Count words in text"
  def word_count(_text), do: :erlang.nif_error(:nif_not_loaded)

  @doc "Extract sentences from text"
  def extract_sentences(_text), do: :erlang.nif_error(:nif_not_loaded)

  # N-grams
  @doc "Generate character n-grams"
  def char_ngrams(_text, _n), do: :erlang.nif_error(:nif_not_loaded)

  @doc "Generate word n-grams from word list"
  def word_ngrams(_words, _n), do: :erlang.nif_error(:nif_not_loaded)

  @doc "Generate word n-grams from text"
  def text_word_ngrams(_text, _n), do: :erlang.nif_error(:nif_not_loaded)

  # Plagiarism detection
  @doc "Compare texts and return similarity metrics"
  def text_similarity(_text1, _text2, _ngram_size), do: :erlang.nif_error(:nif_not_loaded)

  @doc "Find common n-grams between two texts"
  def find_common_ngrams(_text1, _text2, _ngram_size), do: :erlang.nif_error(:nif_not_loaded)

  # Spelling
  @doc "Find spelling suggestions from dictionary"
  def spelling_suggestions(_word, _dictionary, _max_suggestions),
    do: :erlang.nif_error(:nif_not_loaded)

  # Statistics
  @doc "Calculate text statistics"
  def text_stats(_text), do: :erlang.nif_error(:nif_not_loaded)
end
