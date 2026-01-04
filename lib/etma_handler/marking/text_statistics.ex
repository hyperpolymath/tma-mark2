# SPDX-License-Identifier: AGPL-3.0-or-later
defmodule EtmaHandler.Marking.TextStatistics do
  @moduledoc """
  Statistical Text Analysis for Argument Quality Indicators.

  Non-neural proxies for assessing writing quality, argument development,
  and structural consistency. These measures don't require ML but provide
  meaningful signals about text quality.

  ## Word Frequency Analysis

  - **TF-IDF** - Term importance within document
  - **Frequency progression** - How key terms develop across sections
  - **Vocabulary richness** - Type-token ratio, hapax legomena
  - **Lexical density** - Content words vs function words

  ## Structural Proxies

  - **Paragraph consistency** - Length distribution, topic sentence patterns
  - **Section balance** - Intro/body/conclusion proportions
  - **Cohesion metrics** - Word overlap between adjacent units
  - **Subordination index** - Clause complexity

  ## Statistical Signals

  - **Zipf's law adherence** - Natural vs synthetic text detection
  - **Sentence variety** - Length distribution, opening patterns
  - **Quote/evidence ratio** - Supporting material density
  - **Question density** - Rhetorical engagement

  ## Usage

      {:ok, stats} = TextStatistics.analyze(text)
      {:ok, freq} = TextStatistics.frequency_analysis(text, sections: true)
      {:ok, cohesion} = TextStatistics.cohesion_metrics(text)
  """

  require Logger

  # Function words (stop words) for lexical density calculation
  @function_words ~w(
    the a an is are was were be been being have has had do does did
    will would shall should may might can could must
    and or but if then else when where which who whom whose what
    this that these those it its he she they we you I me him her us them
    in on at by for with from to of as into through during before after
    above below between under over about against among
    not no nor only also just even still yet already
    very much more most less least too so such
    how why when where what which who
  )

  # Evidence/quote indicators
  @quote_patterns [
    ~r/"[^"]{20,}"/,
    ~r/'[^']{20,}'/,
    ~r/\([A-Z][a-z]+,?\s*\d{4}.*?\)/,
    ~r/according to/i,
    ~r/\bstates?\s+that\b/i,
    ~r/\bargues?\s+that\b/i,
    ~r/\bsuggests?\s+that\b/i,
    ~r/\bnotes?\s+that\b/i
  ]

  @type frequency_map :: %{String.t() => integer()}

  @type text_stats :: %{
          word_count: integer(),
          sentence_count: integer(),
          paragraph_count: integer(),
          vocabulary_size: integer(),
          type_token_ratio: float(),
          lexical_density: float(),
          avg_sentence_length: float(),
          avg_paragraph_length: float(),
          hapax_ratio: float(),
          zipf_adherence: float()
        }

  # ============================================================
  # PUBLIC API - COMPREHENSIVE ANALYSIS
  # ============================================================

  @doc """
  Perform comprehensive statistical analysis of text.
  """
  @spec analyze(String.t(), keyword()) :: {:ok, map()}
  def analyze(text, opts \\ []) do
    include_sections = Keyword.get(opts, :sections, false)

    basic = basic_stats(text)
    vocabulary = vocabulary_analysis(text)
    structure = structural_analysis(text)
    {:ok, cohesion} = cohesion_metrics(text)
    {:ok, evidence} = evidence_density(text)

    section_analysis =
      if include_sections do
        {:ok, sections} = section_progression(text)
        sections
      else
        nil
      end

    {:ok,
     %{
       basic: basic,
       vocabulary: vocabulary,
       structure: structure,
       cohesion: cohesion,
       evidence: evidence,
       sections: section_analysis,
       quality_indicators: derive_quality_indicators(basic, vocabulary, structure, cohesion, evidence)
     }}
  end

  # ============================================================
  # PUBLIC API - FREQUENCY ANALYSIS
  # ============================================================

  @doc """
  Analyze word frequencies with optional section breakdown.
  """
  @spec frequency_analysis(String.t(), keyword()) :: {:ok, map()}
  def frequency_analysis(text, opts \\ []) do
    by_sections = Keyword.get(opts, :sections, false)
    top_n = Keyword.get(opts, :top_n, 20)

    words = extract_words(text)
    freq = word_frequencies(words)

    # Get top terms by frequency
    top_terms =
      freq
      |> Enum.sort_by(fn {_, count} -> count end, :desc)
      |> Enum.take(top_n)

    # Calculate TF-IDF scores (treating each paragraph as a "document")
    paragraphs = split_paragraphs(text)
    tfidf = calculate_tfidf(paragraphs)

    # Top terms by TF-IDF importance
    top_by_importance =
      tfidf
      |> Enum.sort_by(fn {_, score} -> score end, :desc)
      |> Enum.take(top_n)

    section_breakdown =
      if by_sections && length(paragraphs) > 3 do
        analyze_frequency_progression(paragraphs)
      else
        nil
      end

    {:ok,
     %{
       total_words: length(words),
       unique_words: map_size(freq),
       top_by_frequency: top_terms,
       top_by_importance: top_by_importance,
       section_progression: section_breakdown
     }}
  end

  @doc """
  Track how key terms develop across the document.

  Returns frequency of specified terms in each section (intro, body, conclusion).
  """
  @spec term_progression(String.t(), [String.t()]) :: {:ok, map()}
  def term_progression(text, terms) do
    paragraphs = split_paragraphs(text)
    total = length(paragraphs)

    # Split into thirds: intro, body, conclusion
    intro_end = max(1, div(total, 5))
    conclusion_start = total - max(1, div(total, 5))

    intro = paragraphs |> Enum.take(intro_end) |> Enum.join(" ")
    body = paragraphs |> Enum.slice(intro_end, conclusion_start - intro_end) |> Enum.join(" ")
    conclusion = paragraphs |> Enum.drop(conclusion_start) |> Enum.join(" ")

    term_tracking =
      terms
      |> Enum.map(fn term ->
        term_lower = String.downcase(term)

        {term,
         %{
           intro: count_term(intro, term_lower),
           body: count_term(body, term_lower),
           conclusion: count_term(conclusion, term_lower),
           total: count_term(text, term_lower),
           pattern: determine_pattern(
             count_term(intro, term_lower),
             count_term(body, term_lower),
             count_term(conclusion, term_lower)
           )
         }}
      end)
      |> Map.new()

    {:ok,
     %{
       terms: term_tracking,
       section_sizes: %{
         intro_paragraphs: intro_end,
         body_paragraphs: conclusion_start - intro_end,
         conclusion_paragraphs: total - conclusion_start
       }
     }}
  end

  # ============================================================
  # PUBLIC API - VOCABULARY ANALYSIS
  # ============================================================

  @doc """
  Analyze vocabulary richness and sophistication.
  """
  @spec vocabulary_analysis(String.t()) :: map()
  def vocabulary_analysis(text) do
    words = extract_words(text)
    total = length(words)
    freq = word_frequencies(words)
    unique = map_size(freq)

    # Type-Token Ratio (vocabulary richness)
    ttr = if total > 0, do: unique / total, else: 0.0

    # Hapax legomena (words appearing exactly once)
    hapax_count = freq |> Enum.count(fn {_, count} -> count == 1 end)
    hapax_ratio = if unique > 0, do: hapax_count / unique, else: 0.0

    # Lexical density (content words vs function words)
    content_words = Enum.reject(words, &(&1 in @function_words))
    lexical_density = if total > 0, do: length(content_words) / total, else: 0.0

    # Vocabulary sophistication (average word length as proxy)
    avg_word_length =
      if total > 0 do
        words |> Enum.map(&String.length/1) |> Enum.sum() |> Kernel./(total)
      else
        0.0
      end

    # Long word ratio (words > 6 chars, excluding common ones)
    long_words = Enum.count(words, &(String.length(&1) > 6 && &1 not in @function_words))
    long_word_ratio = if total > 0, do: long_words / total, else: 0.0

    # Zipf's law adherence
    zipf_score = calculate_zipf_adherence(freq)

    %{
      total_words: total,
      unique_words: unique,
      type_token_ratio: Float.round(ttr, 3),
      hapax_count: hapax_count,
      hapax_ratio: Float.round(hapax_ratio, 3),
      lexical_density: Float.round(lexical_density, 3),
      avg_word_length: Float.round(avg_word_length, 2),
      long_word_ratio: Float.round(long_word_ratio, 3),
      zipf_adherence: Float.round(zipf_score, 3),
      assessment: assess_vocabulary(ttr, lexical_density, hapax_ratio)
    }
  end

  # ============================================================
  # PUBLIC API - STRUCTURAL ANALYSIS
  # ============================================================

  @doc """
  Analyze document structure and consistency.
  """
  @spec structural_analysis(String.t()) :: map()
  def structural_analysis(text) do
    sentences = split_sentences(text)
    paragraphs = split_paragraphs(text)

    sentence_count = length(sentences)
    paragraph_count = length(paragraphs)

    # Sentence length statistics
    sentence_lengths = Enum.map(sentences, fn s -> s |> String.split() |> length() end)

    avg_sentence_length =
      if sentence_count > 0 do
        Enum.sum(sentence_lengths) / sentence_count
      else
        0.0
      end

    sentence_length_std_dev = std_dev(sentence_lengths)

    # Sentence variety (different lengths = more varied writing)
    sentence_variety =
      if sentence_count > 1 do
        unique_lengths = sentence_lengths |> Enum.uniq() |> length()
        unique_lengths / sentence_count
      else
        0.0
      end

    # Paragraph length statistics
    paragraph_lengths = Enum.map(paragraphs, fn p -> p |> String.split() |> length() end)

    avg_paragraph_length =
      if paragraph_count > 0 do
        Enum.sum(paragraph_lengths) / paragraph_count
      else
        0.0
      end

    paragraph_length_std_dev = std_dev(paragraph_lengths)

    # Paragraph consistency (low std dev = consistent lengths)
    paragraph_consistency =
      if avg_paragraph_length > 0 do
        1.0 - min(1.0, paragraph_length_std_dev / avg_paragraph_length)
      else
        0.0
      end

    # Sentence opening variety
    {:ok, opening_analysis} = sentence_opening_variety(sentences)

    # Question density
    question_count = sentences |> Enum.count(&String.ends_with?(&1, "?"))
    question_density = if sentence_count > 0, do: question_count / sentence_count, else: 0.0

    # Subordination index (proxy: sentences with commas/semicolons)
    complex_sentences =
      Enum.count(sentences, fn s ->
        String.contains?(s, ",") || String.contains?(s, ";") || String.contains?(s, "which") ||
          String.contains?(s, "although") || String.contains?(s, "because")
      end)

    subordination_index = if sentence_count > 0, do: complex_sentences / sentence_count, else: 0.0

    %{
      sentence_count: sentence_count,
      paragraph_count: paragraph_count,
      avg_sentence_length: Float.round(avg_sentence_length, 1),
      sentence_length_std_dev: Float.round(sentence_length_std_dev, 1),
      sentence_variety: Float.round(sentence_variety, 3),
      avg_paragraph_length: Float.round(avg_paragraph_length, 1),
      paragraph_consistency: Float.round(paragraph_consistency, 3),
      question_density: Float.round(question_density, 3),
      subordination_index: Float.round(subordination_index, 3),
      sentence_openings: opening_analysis,
      assessment: assess_structure(avg_sentence_length, sentence_variety, subordination_index)
    }
  end

  # ============================================================
  # PUBLIC API - COHESION METRICS
  # ============================================================

  @doc """
  Calculate cohesion metrics between adjacent text units.
  """
  @spec cohesion_metrics(String.t()) :: {:ok, map()}
  def cohesion_metrics(text) do
    paragraphs = split_paragraphs(text)
    sentences = split_sentences(text)

    # Paragraph-level cohesion (word overlap between adjacent paragraphs)
    paragraph_cohesion = calculate_adjacent_overlap(paragraphs)

    # Sentence-level cohesion
    sentence_cohesion = calculate_adjacent_overlap(sentences)

    # Lexical chains (repeated content words across text)
    content_word_chains = find_lexical_chains(text)

    # Topic continuity (key terms appearing in multiple paragraphs)
    topic_continuity = calculate_topic_continuity(paragraphs)

    overall_cohesion = (paragraph_cohesion + sentence_cohesion + topic_continuity) / 3

    {:ok,
     %{
       paragraph_cohesion: Float.round(paragraph_cohesion, 3),
       sentence_cohesion: Float.round(sentence_cohesion, 3),
       topic_continuity: Float.round(topic_continuity, 3),
       overall_cohesion: Float.round(overall_cohesion, 3),
       lexical_chain_count: length(content_word_chains),
       top_chains: Enum.take(content_word_chains, 10),
       assessment:
         cond do
           overall_cohesion > 0.3 -> :strong
           overall_cohesion > 0.15 -> :moderate
           overall_cohesion > 0.05 -> :weak
           true -> :fragmented
         end
     }}
  end

  # ============================================================
  # PUBLIC API - EVIDENCE DENSITY
  # ============================================================

  @doc """
  Analyze the density of evidence and quotations.
  """
  @spec evidence_density(String.t()) :: {:ok, map()}
  def evidence_density(text) do
    word_count = text |> String.split() |> length()
    sentence_count = text |> split_sentences() |> length()

    # Count quote patterns
    quote_matches =
      @quote_patterns
      |> Enum.map(fn pattern ->
        Regex.scan(pattern, text) |> length()
      end)
      |> Enum.sum()

    # Estimate quoted text proportion
    direct_quotes =
      Regex.scan(~r/"[^"]+"|'[^']+'/, text)
      |> Enum.map(fn [match] -> String.length(match) end)
      |> Enum.sum()

    quote_proportion = if String.length(text) > 0, do: direct_quotes / String.length(text), else: 0.0

    # Citation density
    citation_patterns = [
      ~r/\([A-Z][a-z]+,?\s*\d{4}[a-z]?(?:,\s*p\.?\s*\d+)?\)/,
      ~r/\[[0-9]+\]/,
      ~r/\([0-9]+\)/
    ]

    citation_count =
      citation_patterns
      |> Enum.map(fn p -> Regex.scan(p, text) |> length() end)
      |> Enum.sum()

    citations_per_100_words = if word_count > 0, do: citation_count / word_count * 100, else: 0.0

    # Data/figure references
    data_refs =
      Regex.scan(~r/\b(?:Figure|Table|Chart|Graph|Diagram|Appendix)\s+[0-9A-Z]+\b/i, text)
      |> length()

    assessment =
      cond do
        citations_per_100_words > 3 -> :heavily_cited
        citations_per_100_words > 1.5 -> :well_supported
        citations_per_100_words > 0.5 -> :adequately_supported
        citations_per_100_words > 0 -> :lightly_supported
        true -> :unsupported
      end

    {:ok,
     %{
       quote_pattern_matches: quote_matches,
       direct_quote_proportion: Float.round(quote_proportion * 100, 1),
       citation_count: citation_count,
       citations_per_100_words: Float.round(citations_per_100_words, 2),
       data_references: data_refs,
       assessment: assessment,
       note:
         case assessment do
           :heavily_cited -> "High citation density - ensure original analysis is also present"
           :well_supported -> "Good use of evidence to support arguments"
           :adequately_supported -> "Adequate citation support"
           :lightly_supported -> "Consider adding more citations to support claims"
           :unsupported -> "Very few or no citations detected - add supporting evidence"
         end
     }}
  end

  # ============================================================
  # PUBLIC API - SECTION PROGRESSION
  # ============================================================

  @doc """
  Analyze how the text develops across sections.
  """
  @spec section_progression(String.t()) :: {:ok, map()}
  def section_progression(text) do
    paragraphs = split_paragraphs(text)
    total = length(paragraphs)

    if total < 3 do
      {:ok, %{error: :insufficient_paragraphs}}
    else
      # Divide into sections
      intro_size = max(1, div(total, 5))
      conclusion_size = max(1, div(total, 5))
      body_size = total - intro_size - conclusion_size

      intro_paras = Enum.take(paragraphs, intro_size)
      body_paras = paragraphs |> Enum.drop(intro_size) |> Enum.take(body_size)
      conclusion_paras = Enum.drop(paragraphs, total - conclusion_size)

      intro_text = Enum.join(intro_paras, " ")
      body_text = Enum.join(body_paras, " ")
      conclusion_text = Enum.join(conclusion_paras, " ")

      # Analyze each section
      intro_vocab = vocabulary_analysis(intro_text)
      body_vocab = vocabulary_analysis(body_text)
      conclusion_vocab = vocabulary_analysis(conclusion_text)

      # Check for appropriate patterns
      # Introduction should set up terms used later
      intro_terms = intro_text |> extract_content_words() |> MapSet.new()
      conclusion_terms = conclusion_text |> extract_content_words() |> MapSet.new()

      term_callback =
        MapSet.intersection(intro_terms, conclusion_terms)
        |> MapSet.size()

      callback_ratio =
        if MapSet.size(intro_terms) > 0 do
          term_callback / MapSet.size(intro_terms)
        else
          0.0
        end

      # Body should be more complex than intro/conclusion
      complexity_pattern =
        cond do
          body_vocab.lexical_density > intro_vocab.lexical_density &&
              body_vocab.lexical_density > conclusion_vocab.lexical_density ->
            :appropriate

          true ->
            :flat
        end

      {:ok,
       %{
         section_sizes: %{
           intro_paragraphs: intro_size,
           body_paragraphs: body_size,
           conclusion_paragraphs: conclusion_size
         },
         section_stats: %{
           intro: Map.take(intro_vocab, [:lexical_density, :avg_word_length, :total_words]),
           body: Map.take(body_vocab, [:lexical_density, :avg_word_length, :total_words]),
           conclusion: Map.take(conclusion_vocab, [:lexical_density, :avg_word_length, :total_words])
         },
         term_callback_ratio: Float.round(callback_ratio, 2),
         complexity_pattern: complexity_pattern,
         balance_assessment: assess_balance(intro_size, body_size, conclusion_size, total)
       }}
    end
  end

  # ============================================================
  # PRIVATE - HELPERS
  # ============================================================

  defp basic_stats(text) do
    words = extract_words(text)
    sentences = split_sentences(text)
    paragraphs = split_paragraphs(text)

    %{
      character_count: String.length(text),
      word_count: length(words),
      sentence_count: length(sentences),
      paragraph_count: length(paragraphs),
      avg_sentence_length:
        if(length(sentences) > 0,
          do: Float.round(length(words) / length(sentences), 1),
          else: 0.0
        ),
      avg_paragraph_length:
        if(length(paragraphs) > 0,
          do: Float.round(length(words) / length(paragraphs), 1),
          else: 0.0
        )
    }
  end

  defp extract_words(text) do
    text
    |> String.downcase()
    |> String.replace(~r/[^\w\s'-]/, "")
    |> String.split(~r/\s+/, trim: true)
    |> Enum.filter(&(String.length(&1) > 1))
  end

  defp extract_content_words(text) do
    text
    |> extract_words()
    |> Enum.reject(&(&1 in @function_words))
  end

  defp word_frequencies(words) do
    Enum.reduce(words, %{}, fn word, acc ->
      Map.update(acc, word, 1, &(&1 + 1))
    end)
  end

  defp split_sentences(text) do
    text
    |> String.split(~r/(?<=[.!?])\s+(?=[A-Z])/, trim: true)
    |> Enum.filter(&(String.length(&1) > 5))
  end

  defp split_paragraphs(text) do
    text
    |> String.split(~r/\n\s*\n|\r\n\s*\r\n/, trim: true)
    |> Enum.filter(&(String.length(String.trim(&1)) > 20))
  end

  defp count_term(text, term) do
    text
    |> String.downcase()
    |> String.split(term)
    |> length()
    |> Kernel.-(1)
    |> max(0)
  end

  defp std_dev([]), do: 0.0
  defp std_dev([_]), do: 0.0

  defp std_dev(values) do
    n = length(values)
    mean = Enum.sum(values) / n
    variance = Enum.reduce(values, 0, fn x, acc -> acc + (x - mean) * (x - mean) end) / n
    :math.sqrt(variance)
  end

  defp calculate_tfidf(documents) do
    # Simple TF-IDF implementation
    doc_count = length(documents)

    # Calculate document frequencies
    df =
      documents
      |> Enum.flat_map(fn doc ->
        doc |> extract_words() |> Enum.uniq()
      end)
      |> Enum.reduce(%{}, fn word, acc ->
        Map.update(acc, word, 1, &(&1 + 1))
      end)

    # Calculate TF-IDF for each term across all documents
    all_words = documents |> Enum.join(" ") |> extract_words()
    tf = word_frequencies(all_words)

    tf
    |> Enum.map(fn {term, term_freq} ->
      doc_freq = Map.get(df, term, 1)
      idf = :math.log(doc_count / doc_freq)
      {term, term_freq * idf}
    end)
    |> Enum.reject(fn {term, _} -> term in @function_words end)
    |> Map.new()
  end

  defp analyze_frequency_progression(paragraphs) do
    third = max(1, div(length(paragraphs), 3))

    early = paragraphs |> Enum.take(third) |> Enum.join(" ")
    middle = paragraphs |> Enum.drop(third) |> Enum.take(third) |> Enum.join(" ")
    late = paragraphs |> Enum.drop(third * 2) |> Enum.join(" ")

    early_freq = early |> extract_content_words() |> word_frequencies()
    middle_freq = middle |> extract_content_words() |> word_frequencies()
    late_freq = late |> extract_content_words() |> word_frequencies()

    # Find terms that increase, decrease, or stay consistent
    all_terms =
      MapSet.union(
        MapSet.union(MapSet.new(Map.keys(early_freq)), MapSet.new(Map.keys(middle_freq))),
        MapSet.new(Map.keys(late_freq))
      )

    patterns =
      all_terms
      |> Enum.map(fn term ->
        e = Map.get(early_freq, term, 0)
        m = Map.get(middle_freq, term, 0)
        l = Map.get(late_freq, term, 0)

        pattern =
          cond do
            e < m && m < l -> :increasing
            e > m && m > l -> :decreasing
            e > 0 && m > 0 && l > 0 -> :consistent
            e > 0 && l > 0 && m == 0 -> :bookend
            true -> :sporadic
          end

        {term, %{early: e, middle: m, late: l, pattern: pattern}}
      end)
      |> Enum.filter(fn {_, %{early: e, middle: m, late: l}} ->
        e + m + l >= 3
      end)
      |> Map.new()

    %{
      term_patterns: patterns,
      increasing_terms: patterns |> Enum.filter(fn {_, v} -> v.pattern == :increasing end) |> Enum.map(&elem(&1, 0)),
      consistent_terms: patterns |> Enum.filter(fn {_, v} -> v.pattern == :consistent end) |> Enum.map(&elem(&1, 0))
    }
  end

  defp determine_pattern(intro, body, conclusion) do
    total = intro + body + conclusion

    cond do
      total == 0 -> :absent
      intro > 0 && conclusion > 0 && body > 0 -> :throughout
      intro > 0 && conclusion > 0 && body == 0 -> :bookend
      intro > body && body > conclusion -> :front_loaded
      conclusion > body && body > intro -> :back_loaded
      body > intro && body > conclusion -> :body_focused
      true -> :sporadic
    end
  end

  defp calculate_zipf_adherence(freq) when map_size(freq) < 10, do: 0.5

  defp calculate_zipf_adherence(freq) do
    # Zipf's law: frequency ∝ 1/rank
    # Natural text follows this closely; synthetic text often doesn't
    sorted =
      freq
      |> Enum.sort_by(fn {_, count} -> count end, :desc)
      |> Enum.take(50)
      |> Enum.with_index(1)

    if length(sorted) < 5 do
      0.5
    else
      # Calculate correlation with expected Zipf distribution
      top_freq = sorted |> List.first() |> elem(0) |> elem(1)

      deviations =
        sorted
        |> Enum.map(fn {{_, actual}, rank} ->
          expected = top_freq / rank
          abs(actual - expected) / max(expected, 1)
        end)

      avg_deviation = Enum.sum(deviations) / length(deviations)
      # Convert to 0-1 score (lower deviation = higher adherence)
      max(0.0, 1.0 - avg_deviation)
    end
  end

  defp calculate_adjacent_overlap(units) when length(units) < 2, do: 0.0

  defp calculate_adjacent_overlap(units) do
    pairs = Enum.zip(units, Enum.drop(units, 1))

    overlaps =
      pairs
      |> Enum.map(fn {a, b} ->
        words_a = a |> extract_content_words() |> MapSet.new()
        words_b = b |> extract_content_words() |> MapSet.new()

        intersection = MapSet.intersection(words_a, words_b) |> MapSet.size()
        union = MapSet.union(words_a, words_b) |> MapSet.size()

        if union > 0, do: intersection / union, else: 0.0
      end)

    Enum.sum(overlaps) / length(overlaps)
  end

  defp find_lexical_chains(text) do
    words = extract_content_words(text)
    freq = word_frequencies(words)

    freq
    |> Enum.filter(fn {word, count} ->
      count >= 3 && String.length(word) > 4
    end)
    |> Enum.sort_by(fn {_, count} -> count end, :desc)
    |> Enum.map(fn {word, count} -> %{word: word, occurrences: count} end)
  end

  defp calculate_topic_continuity(paragraphs) when length(paragraphs) < 2, do: 0.0

  defp calculate_topic_continuity(paragraphs) do
    # Find terms that appear in multiple paragraphs
    para_terms =
      paragraphs
      |> Enum.map(fn p -> p |> extract_content_words() |> MapSet.new() end)

    all_terms =
      para_terms
      |> Enum.reduce(MapSet.new(), &MapSet.union/2)

    term_spread =
      all_terms
      |> Enum.map(fn term ->
        paragraphs_containing = Enum.count(para_terms, &MapSet.member?(&1, term))
        paragraphs_containing / length(paragraphs)
      end)

    if MapSet.size(all_terms) > 0 do
      Enum.sum(term_spread) / MapSet.size(all_terms)
    else
      0.0
    end
  end

  defp sentence_opening_variety(sentences) when length(sentences) < 3 do
    {:ok, %{variety_score: 0.5, note: "Insufficient sentences for analysis"}}
  end

  defp sentence_opening_variety(sentences) do
    # Get first word of each sentence
    openings =
      sentences
      |> Enum.map(fn s ->
        s |> String.split() |> List.first() |> String.downcase()
      end)
      |> Enum.reject(&is_nil/1)

    freq = word_frequencies(openings)
    total = length(openings)
    unique = map_size(freq)

    # Check for repetitive patterns
    most_common = freq |> Enum.max_by(fn {_, count} -> count end, fn -> {"", 0} end)
    {most_common_word, most_common_count} = most_common

    repetition_ratio = if total > 0, do: most_common_count / total, else: 0.0
    variety_ratio = if total > 0, do: unique / total, else: 0.0

    {:ok,
     %{
       unique_openings: unique,
       total_sentences: total,
       variety_ratio: Float.round(variety_ratio, 2),
       most_common: most_common_word,
       repetition_ratio: Float.round(repetition_ratio, 2),
       assessment:
         cond do
           repetition_ratio > 0.3 -> :repetitive
           variety_ratio < 0.3 -> :limited
           variety_ratio > 0.6 -> :varied
           true -> :adequate
         end
     }}
  end

  defp assess_vocabulary(ttr, lexical_density, hapax_ratio) do
    cond do
      ttr > 0.6 && lexical_density > 0.5 -> :sophisticated
      ttr > 0.4 && lexical_density > 0.45 -> :good
      ttr > 0.3 && lexical_density > 0.4 -> :adequate
      true -> :basic
    end
  end

  defp assess_structure(avg_sentence_length, variety, subordination) do
    cond do
      avg_sentence_length > 25 && variety > 0.3 && subordination > 0.4 -> :complex
      avg_sentence_length > 15 && variety > 0.2 && subordination > 0.3 -> :mature
      avg_sentence_length > 10 && subordination > 0.2 -> :adequate
      true -> :simple
    end
  end

  defp assess_balance(intro, body, conclusion, total) do
    intro_ratio = intro / total
    conclusion_ratio = conclusion / total
    body_ratio = body / total

    cond do
      intro_ratio > 0.4 -> :intro_heavy
      conclusion_ratio > 0.4 -> :conclusion_heavy
      body_ratio < 0.5 -> :underdeveloped_body
      intro_ratio < 0.1 || conclusion_ratio < 0.1 -> :missing_sections
      true -> :balanced
    end
  end

  defp derive_quality_indicators(basic, vocab, structure, cohesion, evidence) do
    # Combine signals into quality indicators
    sophistication =
      (vocab.type_token_ratio + vocab.lexical_density + structure.subordination_index) / 3

    development = cohesion.overall_cohesion

    support =
      case evidence.assessment do
        :heavily_cited -> 0.9
        :well_supported -> 0.8
        :adequately_supported -> 0.6
        :lightly_supported -> 0.4
        :unsupported -> 0.2
      end

    consistency =
      (structure.paragraph_consistency +
         (1.0 - min(1.0, structure.sentence_length_std_dev / 20))) / 2

    %{
      sophistication_score: Float.round(sophistication, 2),
      development_score: Float.round(development, 2),
      support_score: Float.round(support, 2),
      consistency_score: Float.round(consistency, 2),
      overall_quality_estimate: Float.round((sophistication + development + support + consistency) / 4, 2)
    }
  end
end
