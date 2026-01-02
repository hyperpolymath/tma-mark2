# SPDX-License-Identifier: AGPL-3.0-or-later
defmodule EtmaHandler.Nlp do
  @moduledoc """
  High-level Natural Language Processing operations for tma-mark2.

  Provides a safe, ergonomic API over the native Rust NIFs:

  ## String Similarity
  - `similarity/2` - Jaro-Winkler similarity (0.0 to 1.0)
  - `distance/2` - Levenshtein edit distance
  - `similar?/3` - Check if strings are similar above threshold

  ## Approximate Search (agrep)
  - `fuzzy_search/3` - Find approximate matches in a list
  - `fuzzy_search_distance/3` - Find matches within edit distance

  ## Text Normalization
  - `normalize/1` - Unicode NFC, lowercase, collapse whitespace
  - `normalize_preserve_case/1` - Normalize without lowercasing
  - `strip_diacritics/1` - Remove accents/diacritics

  ## Tokenization
  - `words/1` - Extract words (Unicode-aware)
  - `sentences/1` - Extract sentences
  - `word_count/1` - Count words in text

  ## N-grams
  - `char_ngrams/2` - Generate character n-grams
  - `word_ngrams/2` - Generate word n-grams

  ## Text Analysis
  - `compare_texts/3` - Compare two texts for similarity
  - `find_common_phrases/3` - Find common n-grams between texts
  - `suggest_spelling/3` - Find spelling suggestions
  - `stats/1` - Calculate text statistics
  """

  alias EtmaHandler.Nlp.Native

  # ============================================================
  # STRING SIMILARITY
  # ============================================================

  @doc """
  Calculate Jaro-Winkler similarity between two strings.

  Returns a value from 0.0 (completely different) to 1.0 (identical).
  Best for short strings like names.

  ## Example

      iex> EtmaHandler.Nlp.similarity("hello", "hallo")
      0.88

      iex> EtmaHandler.Nlp.similarity("john", "jon")
      0.933...
  """
  @spec similarity(String.t(), String.t()) :: float()
  def similarity(s1, s2) when is_binary(s1) and is_binary(s2) do
    Native.jaro_winkler_similarity(s1, s2)
  end

  @doc """
  Calculate Levenshtein edit distance between two strings.

  Returns the minimum number of single-character edits (insertions,
  deletions, or substitutions) required to change one string into the other.

  ## Example

      iex> EtmaHandler.Nlp.distance("kitten", "sitting")
      3

      iex> EtmaHandler.Nlp.distance("hello", "hello")
      0
  """
  @spec distance(String.t(), String.t()) :: non_neg_integer()
  def distance(s1, s2) when is_binary(s1) and is_binary(s2) do
    Native.levenshtein_distance(s1, s2)
  end

  @doc """
  Calculate normalized Levenshtein similarity.

  Returns a value from 0.0 to 1.0, where 1.0 means identical.

  ## Example

      iex> EtmaHandler.Nlp.normalized_similarity("hello", "hallo")
      0.8
  """
  @spec normalized_similarity(String.t(), String.t()) :: float()
  def normalized_similarity(s1, s2) when is_binary(s1) and is_binary(s2) do
    Native.levenshtein_similarity(s1, s2)
  end

  @doc """
  Calculate Damerau-Levenshtein distance.

  Like Levenshtein but also counts transpositions (swapping adjacent
  characters) as a single edit.

  ## Example

      iex> EtmaHandler.Nlp.damerau_distance("ab", "ba")
      1  # One transposition

      iex> EtmaHandler.Nlp.distance("ab", "ba")
      2  # Two operations without transposition
  """
  @spec damerau_distance(String.t(), String.t()) :: non_neg_integer()
  def damerau_distance(s1, s2) when is_binary(s1) and is_binary(s2) do
    Native.damerau_levenshtein_distance(s1, s2)
  end

  @doc """
  Calculate normalized Damerau-Levenshtein similarity.

  ## Example

      iex> EtmaHandler.Nlp.damerau_similarity("hello", "hlelo")
      0.8
  """
  @spec damerau_similarity(String.t(), String.t()) :: float()
  def damerau_similarity(s1, s2) when is_binary(s1) and is_binary(s2) do
    Native.damerau_levenshtein_similarity(s1, s2)
  end

  @doc """
  Check if two strings are similar above a threshold.

  Uses Jaro-Winkler similarity by default.

  ## Example

      iex> EtmaHandler.Nlp.similar?("hello", "hallo", 0.8)
      true

      iex> EtmaHandler.Nlp.similar?("hello", "world", 0.8)
      false
  """
  @spec similar?(String.t(), String.t(), float()) :: boolean()
  def similar?(s1, s2, threshold \\ 0.8) when is_float(threshold) do
    similarity(s1, s2) >= threshold
  end

  # ============================================================
  # APPROXIMATE SEARCH (AGREP)
  # ============================================================

  @doc """
  Find approximate matches using Jaro-Winkler similarity.

  Returns a list of `{candidate, similarity_score}` tuples, sorted by
  similarity descending.

  ## Example

      iex> candidates = ["hello", "hallo", "world", "help", "helicopter"]
      iex> {:ok, matches} = EtmaHandler.Nlp.fuzzy_search("hello", candidates, 0.7)
      iex> Enum.map(matches, fn {word, _score} -> word end)
      ["hello", "hallo", "help"]
  """
  @spec fuzzy_search(String.t(), [String.t()], float()) ::
          {:ok, [{String.t(), float()}]} | {:error, atom()}
  def fuzzy_search(pattern, candidates, threshold \\ 0.8)
      when is_binary(pattern) and is_list(candidates) and is_float(threshold) do
    Native.agrep(pattern, candidates, threshold)
  end

  @doc """
  Find approximate matches within a maximum edit distance.

  Returns a list of `{candidate, distance}` tuples, sorted by distance
  ascending.

  ## Example

      iex> candidates = ["hello", "hallo", "world", "help"]
      iex> {:ok, matches} = EtmaHandler.Nlp.fuzzy_search_distance("hello", candidates, 2)
      iex> Enum.map(matches, fn {word, _dist} -> word end)
      ["hello", "hallo", "help"]
  """
  @spec fuzzy_search_distance(String.t(), [String.t()], non_neg_integer()) ::
          {:ok, [{String.t(), non_neg_integer()}]} | {:error, atom()}
  def fuzzy_search_distance(pattern, candidates, max_distance)
      when is_binary(pattern) and is_list(candidates) and is_integer(max_distance) do
    Native.agrep_levenshtein(pattern, candidates, max_distance)
  end

  # ============================================================
  # TEXT NORMALIZATION
  # ============================================================

  @doc """
  Normalize text: Unicode NFC normalization, lowercase, collapse whitespace.

  ## Example

      iex> EtmaHandler.Nlp.normalize("  Hello   WORLD  ")
      "hello world"

      iex> EtmaHandler.Nlp.normalize("café")
      "café"
  """
  @spec normalize(String.t()) :: String.t()
  def normalize(text) when is_binary(text) do
    Native.normalize_text(text)
  end

  @doc """
  Normalize text but preserve case.

  ## Example

      iex> EtmaHandler.Nlp.normalize_preserve_case("  Hello   WORLD  ")
      "Hello WORLD"
  """
  @spec normalize_preserve_case(String.t()) :: String.t()
  def normalize_preserve_case(text) when is_binary(text) do
    Native.normalize_text_preserve_case(text)
  end

  @doc """
  Remove diacritics/accents from text.

  ## Example

      iex> EtmaHandler.Nlp.strip_diacritics("café résumé naïve")
      "cafe resume naive"
  """
  @spec strip_diacritics(String.t()) :: String.t()
  def strip_diacritics(text) when is_binary(text) do
    Native.remove_diacritics(text)
  end

  # ============================================================
  # TOKENIZATION
  # ============================================================

  @doc """
  Extract words from text using Unicode-aware word segmentation.

  ## Example

      iex> EtmaHandler.Nlp.words("Hello, world! How are you?")
      ["Hello", "world", "How", "are", "you"]
  """
  @spec words(String.t()) :: [String.t()]
  def words(text) when is_binary(text) do
    Native.extract_words(text)
  end

  @doc """
  Extract words and convert to lowercase.

  ## Example

      iex> EtmaHandler.Nlp.words_lowercase("Hello World")
      ["hello", "world"]
  """
  @spec words_lowercase(String.t()) :: [String.t()]
  def words_lowercase(text) when is_binary(text) do
    Native.extract_words_lowercase(text)
  end

  @doc """
  Count words in text.

  ## Example

      iex> EtmaHandler.Nlp.word_count("Hello, world! How are you?")
      5
  """
  @spec word_count(String.t()) :: non_neg_integer()
  def word_count(text) when is_binary(text) do
    Native.word_count(text)
  end

  @doc """
  Extract sentences from text.

  ## Example

      iex> EtmaHandler.Nlp.sentences("Hello world. How are you? I'm fine!")
      ["Hello world.", "How are you?", "I'm fine!"]
  """
  @spec sentences(String.t()) :: [String.t()]
  def sentences(text) when is_binary(text) do
    Native.extract_sentences(text)
  end

  # ============================================================
  # N-GRAMS
  # ============================================================

  @doc """
  Generate character n-grams from text.

  ## Example

      iex> EtmaHandler.Nlp.char_ngrams("hello", 2)
      ["he", "el", "ll", "lo"]

      iex> EtmaHandler.Nlp.char_ngrams("hello", 3)
      ["hel", "ell", "llo"]
  """
  @spec char_ngrams(String.t(), pos_integer()) :: [String.t()]
  def char_ngrams(text, n) when is_binary(text) and n > 0 do
    Native.char_ngrams(text, n)
  end

  @doc """
  Generate word n-grams from a list of words.

  ## Example

      iex> words = ["the", "quick", "brown", "fox"]
      iex> EtmaHandler.Nlp.word_ngrams(words, 2)
      ["the quick", "quick brown", "brown fox"]
  """
  @spec word_ngrams([String.t()], pos_integer()) :: [String.t()]
  def word_ngrams(word_list, n) when is_list(word_list) and n > 0 do
    Native.word_ngrams(word_list, n)
  end

  @doc """
  Generate word n-grams directly from text.

  ## Example

      iex> EtmaHandler.Nlp.text_word_ngrams("the quick brown fox", 2)
      ["the quick", "quick brown", "brown fox"]
  """
  @spec text_word_ngrams(String.t(), pos_integer()) :: [String.t()]
  def text_word_ngrams(text, n) when is_binary(text) and n > 0 do
    Native.text_word_ngrams(text, n)
  end

  # ============================================================
  # TEXT ANALYSIS / PLAGIARISM DETECTION
  # ============================================================

  @doc """
  Compare two texts and return similarity metrics.

  Returns a map with:
  - `jaccard`: Jaccard similarity coefficient
  - `overlap`: Overlap coefficient
  - `common_ngrams`: Number of shared n-grams
  - `total_ngrams`: Total unique n-grams in union

  ## Options
  - `ngram_size`: Size of n-grams to compare (default: 3)

  ## Example

      iex> text1 = "The quick brown fox jumps over the lazy dog"
      iex> text2 = "The quick brown cat jumps over the lazy mouse"
      iex> {:ok, metrics} = EtmaHandler.Nlp.compare_texts(text1, text2, 3)
      iex> metrics["jaccard"]
      0.625  # 62.5% similar
  """
  @spec compare_texts(String.t(), String.t(), pos_integer()) ::
          {:ok, map()} | {:error, atom()}
  def compare_texts(text1, text2, ngram_size \\ 3)
      when is_binary(text1) and is_binary(text2) and ngram_size > 0 do
    case Native.text_similarity(text1, text2, ngram_size) do
      {:ok, metrics} -> {:ok, Map.new(metrics)}
      error -> error
    end
  end

  @doc """
  Find common n-grams between two texts.

  Useful for identifying shared phrases that might indicate plagiarism
  or common sources.

  ## Example

      iex> text1 = "The quick brown fox jumps over the lazy dog"
      iex> text2 = "The quick brown cat jumps over the lazy mouse"
      iex> {:ok, common} = EtmaHandler.Nlp.find_common_phrases(text1, text2, 3)
      iex> "the quick brown" in common
      true
  """
  @spec find_common_phrases(String.t(), String.t(), pos_integer()) ::
          {:ok, [String.t()]} | {:error, atom()}
  def find_common_phrases(text1, text2, ngram_size \\ 3)
      when is_binary(text1) and is_binary(text2) and ngram_size > 0 do
    Native.find_common_ngrams(text1, text2, ngram_size)
  end

  # ============================================================
  # SPELLING SUGGESTIONS
  # ============================================================

  @doc """
  Find spelling suggestions from a dictionary.

  Returns top N suggestions sorted by similarity.

  ## Example

      iex> dictionary = ["hello", "world", "help", "helicopter", "hero"]
      iex> {:ok, suggestions} = EtmaHandler.Nlp.suggest_spelling("helo", dictionary, 3)
      iex> Enum.map(suggestions, fn {word, _score} -> word end)
      ["hello", "hero", "help"]
  """
  @spec suggest_spelling(String.t(), [String.t()], pos_integer()) ::
          {:ok, [{String.t(), float()}]} | {:error, atom()}
  def suggest_spelling(word, dictionary, max_suggestions \\ 5)
      when is_binary(word) and is_list(dictionary) and max_suggestions > 0 do
    Native.spelling_suggestions(word, dictionary, max_suggestions)
  end

  # ============================================================
  # TEXT STATISTICS
  # ============================================================

  @doc """
  Calculate comprehensive text statistics.

  Returns a map with:
  - `word_count`: Total number of words
  - `sentence_count`: Total number of sentences
  - `avg_word_length`: Average word length in characters
  - `unique_words`: Number of unique words (case-insensitive)
  - `vocabulary_richness`: Ratio of unique words to total words

  ## Example

      iex> {:ok, stats} = EtmaHandler.Nlp.stats("Hello world. Hello universe!")
      iex> stats["word_count"]
      4
      iex> stats["unique_words"]
      3  # "hello", "world", "universe"
  """
  @spec stats(String.t()) :: {:ok, map()} | {:error, atom()}
  def stats(text) when is_binary(text) do
    case Native.text_stats(text) do
      {:ok, stats_list} -> {:ok, Map.new(stats_list)}
      error -> error
    end
  end

  # ============================================================
  # CONVENIENCE FUNCTIONS
  # ============================================================

  @doc """
  Check if text might be plagiarized based on similarity threshold.

  Returns `true` if the Jaccard similarity exceeds the threshold.

  ## Example

      iex> original = "The quick brown fox jumps over the lazy dog"
      iex> copy = "The quick brown fox leaps over the lazy dog"
      iex> EtmaHandler.Nlp.possibly_plagiarized?(original, copy, 0.5)
      true
  """
  @spec possibly_plagiarized?(String.t(), String.t(), float(), pos_integer()) :: boolean()
  def possibly_plagiarized?(text1, text2, threshold \\ 0.5, ngram_size \\ 3)
      when is_float(threshold) and threshold >= 0 and threshold <= 1 do
    case compare_texts(text1, text2, ngram_size) do
      {:ok, %{"jaccard" => jaccard}} -> jaccard >= threshold
      _ -> false
    end
  end

  @doc """
  Calculate reading difficulty metrics.

  Returns a map with basic readability indicators.

  ## Example

      iex> {:ok, metrics} = EtmaHandler.Nlp.readability("Simple text. Easy to read.")
      iex> metrics.avg_sentence_length
      2.0
  """
  @spec readability(String.t()) :: {:ok, map()} | {:error, atom()}
  def readability(text) when is_binary(text) do
    with {:ok, base_stats} <- stats(text) do
      word_count = Map.get(base_stats, "word_count", 0)
      sentence_count = max(Map.get(base_stats, "sentence_count", 1), 1)
      avg_word_length = Map.get(base_stats, "avg_word_length", 0)

      avg_sentence_length = word_count / sentence_count

      # Simplified readability score (lower = easier)
      # Based loosely on Flesch-Kincaid concepts
      complexity_score = avg_sentence_length * 0.4 + avg_word_length * 0.6

      {:ok,
       %{
         avg_sentence_length: avg_sentence_length,
         avg_word_length: avg_word_length,
         complexity_score: complexity_score,
         word_count: word_count,
         sentence_count: sentence_count
       }}
    end
  end
end
