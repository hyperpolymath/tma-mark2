# SPDX-License-Identifier: AGPL-3.0-or-later
defmodule EtmaHandler.NlpTest do
  use ExUnit.Case, async: true

  alias EtmaHandler.Nlp

  describe "similarity/2" do
    test "identical strings have similarity 1.0" do
      assert Nlp.similarity("hello", "hello") == 1.0
    end

    test "completely different strings have low similarity" do
      sim = Nlp.similarity("abc", "xyz")
      assert sim < 0.5
    end

    test "similar strings have high similarity" do
      sim = Nlp.similarity("hello", "hallo")
      assert sim > 0.8
    end
  end

  describe "distance/2" do
    test "identical strings have distance 0" do
      assert Nlp.distance("hello", "hello") == 0
    end

    test "single character difference has distance 1" do
      assert Nlp.distance("hello", "hallo") == 1
    end

    test "completely different strings have high distance" do
      assert Nlp.distance("abc", "xyz") == 3
    end
  end

  describe "similar?/3" do
    test "returns true for similar strings above threshold" do
      assert Nlp.similar?("hello", "hallo", 0.8)
    end

    test "returns false for dissimilar strings below threshold" do
      refute Nlp.similar?("hello", "world", 0.8)
    end
  end

  describe "fuzzy_search/3" do
    test "finds exact match" do
      candidates = ["hello", "world", "help"]
      {:ok, matches} = Nlp.fuzzy_search("hello", candidates, 0.9)

      assert length(matches) >= 1
      assert Enum.any?(matches, fn {word, _} -> word == "hello" end)
    end

    test "finds similar matches" do
      candidates = ["hello", "hallo", "world", "help"]
      {:ok, matches} = Nlp.fuzzy_search("hello", candidates, 0.7)

      words = Enum.map(matches, fn {word, _} -> word end)
      assert "hello" in words
      assert "hallo" in words
    end

    test "results are sorted by similarity descending" do
      candidates = ["help", "hello", "helicopter"]
      {:ok, matches} = Nlp.fuzzy_search("hello", candidates, 0.5)

      scores = Enum.map(matches, fn {_, score} -> score end)
      assert scores == Enum.sort(scores, :desc)
    end
  end

  describe "normalize/1" do
    test "lowercases text" do
      assert Nlp.normalize("HELLO WORLD") == "hello world"
    end

    test "collapses whitespace" do
      assert Nlp.normalize("hello    world") == "hello world"
    end

    test "trims leading/trailing whitespace" do
      assert Nlp.normalize("  hello  ") == "hello"
    end
  end

  describe "normalize_preserve_case/1" do
    test "preserves case" do
      assert Nlp.normalize_preserve_case("Hello WORLD") == "Hello WORLD"
    end

    test "collapses whitespace" do
      assert Nlp.normalize_preserve_case("Hello   World") == "Hello World"
    end
  end

  describe "strip_diacritics/1" do
    test "removes accents" do
      assert Nlp.strip_diacritics("café") == "cafe"
      assert Nlp.strip_diacritics("résumé") == "resume"
    end

    test "handles mixed text" do
      assert Nlp.strip_diacritics("naïve façade") == "naive facade"
    end
  end

  describe "words/1" do
    test "extracts words from text" do
      words = Nlp.words("Hello, world! How are you?")
      assert words == ["Hello", "world", "How", "are", "you"]
    end

    test "handles empty string" do
      assert Nlp.words("") == []
    end
  end

  describe "words_lowercase/1" do
    test "extracts lowercase words" do
      words = Nlp.words_lowercase("Hello World")
      assert words == ["hello", "world"]
    end
  end

  describe "word_count/1" do
    test "counts words correctly" do
      assert Nlp.word_count("one two three four five") == 5
    end

    test "empty string has zero words" do
      assert Nlp.word_count("") == 0
    end
  end

  describe "sentences/1" do
    test "extracts sentences" do
      sentences = Nlp.sentences("Hello world. How are you? I'm fine!")
      assert length(sentences) == 3
    end
  end

  describe "char_ngrams/2" do
    test "generates character bigrams" do
      ngrams = Nlp.char_ngrams("hello", 2)
      assert ngrams == ["he", "el", "ll", "lo"]
    end

    test "generates character trigrams" do
      ngrams = Nlp.char_ngrams("hello", 3)
      assert ngrams == ["hel", "ell", "llo"]
    end

    test "returns empty for n > length" do
      assert Nlp.char_ngrams("hi", 3) == []
    end
  end

  describe "word_ngrams/2" do
    test "generates word bigrams" do
      words = ["the", "quick", "brown", "fox"]
      ngrams = Nlp.word_ngrams(words, 2)
      assert ngrams == ["the quick", "quick brown", "brown fox"]
    end
  end

  describe "text_word_ngrams/2" do
    test "generates word ngrams from text" do
      ngrams = Nlp.text_word_ngrams("the quick brown fox", 2)
      assert ngrams == ["the quick", "quick brown", "brown fox"]
    end
  end

  describe "compare_texts/3" do
    test "identical texts have high similarity" do
      text = "The quick brown fox jumps over the lazy dog"
      {:ok, metrics} = Nlp.compare_texts(text, text, 3)

      assert metrics["jaccard"] == 1.0
    end

    test "different texts have lower similarity" do
      text1 = "The quick brown fox"
      text2 = "The lazy brown dog"
      {:ok, metrics} = Nlp.compare_texts(text1, text2, 2)

      assert metrics["jaccard"] < 1.0
      assert metrics["jaccard"] > 0.0
    end
  end

  describe "find_common_phrases/3" do
    test "finds shared phrases" do
      text1 = "The quick brown fox jumps"
      text2 = "The quick brown cat jumps"
      {:ok, common} = Nlp.find_common_phrases(text1, text2, 2)

      assert "the quick" in common or "quick brown" in common
    end
  end

  describe "suggest_spelling/3" do
    test "suggests correct spelling" do
      dictionary = ["hello", "world", "help", "hero"]
      {:ok, suggestions} = Nlp.suggest_spelling("helo", dictionary, 3)

      words = Enum.map(suggestions, fn {word, _} -> word end)
      assert "hello" in words
    end

    test "returns top N suggestions" do
      dictionary = ["a", "b", "c", "d", "e", "f"]
      {:ok, suggestions} = Nlp.suggest_spelling("x", dictionary, 3)

      assert length(suggestions) <= 3
    end
  end

  describe "stats/1" do
    test "returns text statistics" do
      {:ok, stats} = Nlp.stats("Hello world. This is a test.")

      assert stats["word_count"] == 6
      assert stats["sentence_count"] == 2
      assert is_float(stats["avg_word_length"])
      assert stats["unique_words"] > 0
    end
  end

  describe "possibly_plagiarized?/4" do
    test "detects potential plagiarism" do
      original = "The quick brown fox jumps over the lazy dog"
      copy = "The quick brown fox leaps over the lazy dog"

      assert Nlp.possibly_plagiarized?(original, copy, 0.5, 3)
    end

    test "returns false for different texts" do
      text1 = "Completely original content here"
      text2 = "Entirely different unrelated text"

      refute Nlp.possibly_plagiarized?(text1, text2, 0.5, 3)
    end
  end

  describe "readability/1" do
    test "calculates readability metrics" do
      {:ok, metrics} = Nlp.readability("Simple sentences. Easy to read. Very clear.")

      assert is_float(metrics.avg_sentence_length)
      assert is_float(metrics.avg_word_length)
      assert is_float(metrics.complexity_score)
    end
  end
end
