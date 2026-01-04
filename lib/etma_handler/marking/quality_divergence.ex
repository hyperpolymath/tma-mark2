# SPDX-License-Identifier: AGPL-3.0-or-later
defmodule EtmaHandler.Marking.QualityDivergence do
  @moduledoc """
  Detects divergence between form quality and content quality.

  This module identifies cases where:
  - Writing quality is high but content/argumentation is weak
  - Writing quality is low but content/argumentation is strong

  These divergences are critical flags for human review, as they indicate
  the automated metrics may be misleading if taken at face value.

  ## Philosophy

  Rule-based metrics measure proxies, not quality itself:
  - Syntactic complexity ≠ good thinking
  - Vocabulary richness ≠ understanding
  - Perfect grammar ≠ valid arguments
  - Broken grammar ≠ invalid arguments

  The system's job is to flag, not grade. Divergence detection ensures
  that students with non-native English or different linguistic styles
  are not penalized for form when their content is strong.
  """

  alias EtmaHandler.Marking.{
    SyntacticComplexity,
    TextStatistics,
    ArgumentationAnalyzer
  }

  @type divergence_result :: %{
          form_score: float(),
          content_score: float(),
          divergence: float(),
          divergence_type: :none | :form_over_content | :content_over_form | :balanced,
          flags: [flag()],
          recommendations: [String.t()],
          confidence: :high | :medium | :low
        }

  @type flag :: %{
          type: atom(),
          severity: :info | :warning | :critical,
          message: String.t()
        }

  # Thresholds for detecting significant divergence
  @high_form_threshold 0.7
  @low_form_threshold 0.4
  @high_content_threshold 0.7
  @low_content_threshold 0.4
  @significant_divergence 0.3

  @doc """
  Analyze text for form/content divergence.

  Returns a comprehensive divergence analysis that helps markers understand
  when to trust automated metrics and when to apply human judgment.

  ## Options

  - `:domain_terms` - List of expected domain terms for the assignment
  - `:assignment_level` - Expected academic level (:level_1 to :level_3, :postgrad)
  - `:non_native_sensitive` - If true, applies adjusted thresholds for ESL writers

  ## Examples

      iex> QualityDivergence.analyze(text, domain_terms: ["methodology", "hypothesis"])
      {:ok, %{divergence_type: :content_over_form, ...}}
  """
  @spec analyze(String.t(), keyword()) :: {:ok, divergence_result()} | {:error, term()}
  def analyze(text, opts \\ []) do
    domain_terms = Keyword.get(opts, :domain_terms, [])
    level = Keyword.get(opts, :assignment_level, :level_2)
    non_native_sensitive = Keyword.get(opts, :non_native_sensitive, true)

    with {:ok, syntax} <- SyntacticComplexity.analyze(text),
         {:ok, stats} <- TextStatistics.analyze(text),
         {:ok, args} <- ArgumentationAnalyzer.analyze(text) do
      form_score = calculate_form_score(syntax, stats)
      content_score = calculate_content_score(stats, args, domain_terms)

      divergence = abs(form_score - content_score)

      divergence_type =
        cond do
          divergence < @significant_divergence -> :balanced
          form_score > content_score -> :form_over_content
          true -> :content_over_form
        end

      flags = generate_divergence_flags(form_score, content_score, divergence_type, level)

      recommendations =
        generate_recommendations(
          divergence_type,
          form_score,
          content_score,
          non_native_sensitive
        )

      confidence = assess_confidence(divergence, stats.basic.word_count)

      {:ok,
       %{
         form_score: Float.round(form_score, 3),
         content_score: Float.round(content_score, 3),
         divergence: Float.round(divergence, 3),
         divergence_type: divergence_type,
         flags: flags,
         recommendations: recommendations,
         confidence: confidence,
         detail: %{
           syntax_summary: summarize_syntax(syntax),
           content_summary: summarize_content(stats, args),
           domain_term_coverage: domain_term_coverage(text, domain_terms)
         }
       }}
    end
  end

  @doc """
  Quick check for significant divergence without full analysis.
  """
  @spec significant_divergence?(String.t(), keyword()) :: boolean()
  def significant_divergence?(text, opts \\ []) do
    case analyze(text, opts) do
      {:ok, result} -> result.divergence >= @significant_divergence
      {:error, _} -> false
    end
  end

  # ==========================================================================
  # Form Score Calculation
  # ==========================================================================

  defp calculate_form_score(syntax, stats) do
    # Normalize each component to 0-1 range
    components = [
      normalize_syntax_complexity(syntax),
      normalize_vocabulary(stats.vocabulary),
      normalize_cohesion(stats.cohesion),
      normalize_structure(stats.structure)
    ]

    # Weighted average
    weights = [0.3, 0.25, 0.25, 0.2]

    components
    |> Enum.zip(weights)
    |> Enum.map(fn {score, weight} -> score * weight end)
    |> Enum.sum()
  end

  defp normalize_syntax_complexity(syntax) do
    # Based on academic writing norms
    # MLT (Mean Length of T-unit) of 15-25 is typical for academic writing
    mlt = syntax.t_units.mean_length
    mlt_score = normalize_to_range(mlt, 8, 25, 35)

    # Subordination ratio of 0.3-0.6 is typical
    sub_ratio = syntax.clauses.subordination_ratio
    sub_score = normalize_to_range(sub_ratio, 0.1, 0.45, 0.8)

    # Clause density of 1.5-2.5 is typical
    clause_density = syntax.t_units.clauses_per_t_unit
    clause_score = normalize_to_range(clause_density, 1.0, 2.0, 3.5)

    (mlt_score + sub_score + clause_score) / 3
  end

  defp normalize_vocabulary(vocab) do
    # Type-Token Ratio: higher isn't always better (typically 0.4-0.7 for essays)
    ttr_score = normalize_to_range(vocab.type_token_ratio, 0.3, 0.55, 0.85)

    # Lexical density: 0.4-0.6 is typical for academic writing
    lex_score = normalize_to_range(vocab.lexical_density, 0.25, 0.5, 0.75)

    # Sophistication (long word ratio): 0.15-0.3 is typical
    soph_score = normalize_to_range(vocab.sophistication, 0.08, 0.22, 0.4)

    (ttr_score + lex_score + soph_score) / 3
  end

  defp normalize_cohesion(cohesion) do
    # Adjacent overlap of 0.2-0.4 indicates good flow
    overlap_score = normalize_to_range(cohesion.adjacent_overlap, 0.1, 0.3, 0.5)

    # Connective density of 0.03-0.08 is typical
    conn_score = normalize_to_range(cohesion.connective_density, 0.01, 0.05, 0.1)

    (overlap_score + conn_score) / 2
  end

  defp normalize_structure(structure) do
    # Paragraph count relative to word count
    para_score =
      if structure.paragraph_count > 0 do
        avg_para_length = structure.total_sentences / structure.paragraph_count
        # 4-8 sentences per paragraph is typical
        normalize_to_range(avg_para_length, 2, 6, 12)
      else
        0.3
      end

    # Sentence length consistency (CV of sentence length)
    length_scores = structure.sentence_lengths
    cv = coefficient_of_variation(length_scores)
    # CV of 0.3-0.5 indicates good variety without chaos
    cv_score = normalize_to_range(cv, 0.15, 0.4, 0.8)

    (para_score + cv_score) / 2
  end

  # ==========================================================================
  # Content Score Calculation
  # ==========================================================================

  defp calculate_content_score(stats, args, domain_terms) do
    components = [
      score_evidence_usage(stats.evidence),
      score_argumentation(args),
      score_domain_terminology(stats, domain_terms),
      score_critical_thinking(args)
    ]

    weights = [0.25, 0.3, 0.25, 0.2]

    components
    |> Enum.zip(weights)
    |> Enum.map(fn {score, weight} -> score * weight end)
    |> Enum.sum()
  end

  defp score_evidence_usage(evidence) do
    # Citation density (per 100 words)
    cite_score = normalize_to_range(evidence.citation_density * 100, 0.5, 3, 8)

    # Quotation ratio (too much is bad, some is good)
    quote_ratio = evidence.quotation_ratio
    quote_score = if quote_ratio > 0.15, do: 0.3, else: normalize_to_range(quote_ratio, 0, 0.05, 0.12)

    # Data markers
    data_score = min(1.0, evidence.data_markers / 5)

    (cite_score * 0.5 + quote_score * 0.2 + data_score * 0.3)
  end

  defp score_argumentation(args) do
    # Check for balanced argumentation
    hedging = args.hedging
    overclaiming = args.overclaiming

    # Good academic writing has some hedging, minimal overclaiming
    hedge_score =
      cond do
        hedging.count == 0 -> 0.3
        hedging.density > 0.02 and hedging.density < 0.06 -> 1.0
        hedging.density <= 0.02 -> 0.5
        true -> 0.6
      end

    overclaim_score = max(0, 1 - overclaiming.count * 0.15)

    # Discourse markers indicate structured argument
    discourse = args.discourse_markers
    discourse_score = min(1.0, Map.values(discourse) |> Enum.sum() |> Kernel./(10))

    (hedge_score * 0.3 + overclaim_score * 0.3 + discourse_score * 0.4)
  end

  defp score_domain_terminology(_stats, domain_terms) when domain_terms == [], do: 0.5

  defp score_domain_terminology(stats, domain_terms) do
    text_lower = String.downcase(stats.basic.text || "")

    found =
      domain_terms
      |> Enum.count(fn term ->
        String.contains?(text_lower, String.downcase(term))
      end)

    coverage = found / length(domain_terms)

    # Also check if domain terms appear in the frequent words
    frequent_words = Map.keys(stats.vocabulary.word_frequencies) |> Enum.take(50)

    domain_in_frequent =
      domain_terms
      |> Enum.count(fn term ->
        String.downcase(term) in frequent_words
      end)

    prominence = if length(domain_terms) > 0, do: domain_in_frequent / length(domain_terms), else: 0

    coverage * 0.6 + prominence * 0.4
  end

  defp score_critical_thinking(args) do
    blooms = args.blooms_taxonomy

    # Weight higher-order thinking more heavily
    weights = %{
      remember: 0.1,
      understand: 0.15,
      apply: 0.2,
      analyze: 0.25,
      evaluate: 0.3,
      create: 0.35
    }

    # Calculate weighted score based on verb distribution
    total_verbs = Map.values(blooms) |> List.flatten() |> length()

    if total_verbs == 0 do
      0.3
    else
      weighted_sum =
        blooms
        |> Enum.map(fn {level, verbs} ->
          weight = Map.get(weights, level, 0.1)
          length(verbs) * weight
        end)
        |> Enum.sum()

      min(1.0, weighted_sum / 5)
    end
  end

  # ==========================================================================
  # Flag Generation
  # ==========================================================================

  defp generate_divergence_flags(form_score, content_score, divergence_type, level) do
    flags = []

    flags =
      case divergence_type do
        :form_over_content when form_score > @high_form_threshold and
                                 content_score < @low_content_threshold ->
          [
            %{
              type: :style_over_substance,
              severity: :warning,
              message:
                "Writing is technically proficient but content indicators are weak. " <>
                  "Check for meaningful engagement with the topic and evidence of understanding."
            }
            | flags
          ]

        :content_over_form when content_score > @high_content_threshold and
                                 form_score < @low_form_threshold ->
          [
            %{
              type: :substance_over_style,
              severity: :info,
              message:
                "Content indicators are strong but writing quality metrics are lower. " <>
                  "This may indicate an ESL writer or different linguistic background. " <>
                  "Focus marking on content quality and understanding."
            }
            | flags
          ]

        :content_over_form ->
          [
            %{
              type: :potential_esl,
              severity: :info,
              message:
                "Writing complexity is below typical but content engagement is evident. " <>
                  "Consider whether language proficiency is affecting expression of understanding."
            }
            | flags
          ]

        _ ->
          flags
      end

    # Level-specific flags
    flags =
      if form_score > 0.8 and content_score < 0.5 and level in [:level_1, :level_2] do
        [
          %{
            type: :surface_learning,
            severity: :warning,
            message:
              "Sophisticated writing style with limited evidence of deep engagement. " <>
                "May indicate surface learning or template-based writing."
          }
          | flags
        ]
      else
        flags
      end

    flags
  end

  defp generate_recommendations(divergence_type, form_score, content_score, non_native_sensitive) do
    recs = []

    recs =
      case divergence_type do
        :form_over_content ->
          [
            "Review for genuine understanding beyond polished prose",
            "Check if citations support actual argumentation or are decorative",
            "Look for original analysis vs sophisticated paraphrasing"
            | recs
          ]

        :content_over_form when non_native_sensitive ->
          [
            "Focus feedback on content quality; note language support available",
            "Consider content separately from language proficiency",
            "Recognize that broken grammar doesn't invalidate good arguments"
            | recs
          ]

        :content_over_form ->
          [
            "Acknowledge strong content while noting areas for writing improvement",
            "Separate feedback on ideas from feedback on expression"
            | recs
          ]

        :balanced when form_score > 0.7 and content_score > 0.7 ->
          ["Well-balanced submission with strong form and content indicators" | recs]

        :balanced when form_score < 0.4 and content_score < 0.4 ->
          ["Both form and content indicators suggest areas for development" | recs]

        _ ->
          recs
      end

    recs
  end

  # ==========================================================================
  # Helper Functions
  # ==========================================================================

  defp normalize_to_range(value, low, optimal, high) do
    cond do
      value <= low -> 0.0
      value >= high -> 0.5
      value <= optimal -> (value - low) / (optimal - low)
      true -> 1.0 - (value - optimal) / (high - optimal) * 0.5
    end
  end

  defp coefficient_of_variation(values) when length(values) < 2, do: 0.0

  defp coefficient_of_variation(values) do
    mean = Enum.sum(values) / length(values)

    if mean == 0 do
      0.0
    else
      variance = Enum.map(values, fn v -> (v - mean) * (v - mean) end) |> Enum.sum()
      std_dev = :math.sqrt(variance / length(values))
      std_dev / mean
    end
  end

  defp assess_confidence(divergence, word_count) do
    cond do
      word_count < 300 -> :low
      word_count < 800 -> :medium
      divergence > 0.5 -> :high
      true -> :medium
    end
  end

  defp summarize_syntax(syntax) do
    %{
      mean_sentence_length: syntax.t_units.mean_length,
      subordination_ratio: syntax.clauses.subordination_ratio,
      complexity_level: categorize_complexity(syntax)
    }
  end

  defp categorize_complexity(syntax) do
    mlt = syntax.t_units.mean_length
    sub = syntax.clauses.subordination_ratio

    cond do
      mlt > 25 and sub > 0.5 -> :very_complex
      mlt > 18 and sub > 0.35 -> :complex
      mlt > 12 and sub > 0.2 -> :moderate
      true -> :simple
    end
  end

  defp summarize_content(stats, args) do
    %{
      evidence_density: stats.evidence.citation_density,
      argumentation_present: args.discourse_markers |> Map.values() |> Enum.sum() > 3,
      critical_thinking_level: dominant_blooms_level(args.blooms_taxonomy)
    }
  end

  defp dominant_blooms_level(blooms) do
    blooms
    |> Enum.max_by(fn {_level, verbs} -> length(verbs) end, fn -> {:none, []} end)
    |> elem(0)
  end

  defp domain_term_coverage(_text, []), do: %{coverage: 0, found: [], missing: []}

  defp domain_term_coverage(text, domain_terms) do
    text_lower = String.downcase(text)

    {found, missing} =
      Enum.split_with(domain_terms, fn term ->
        String.contains?(text_lower, String.downcase(term))
      end)

    %{
      coverage: length(found) / length(domain_terms),
      found: found,
      missing: missing
    }
  end
end
