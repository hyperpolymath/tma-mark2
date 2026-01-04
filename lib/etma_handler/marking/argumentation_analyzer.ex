# SPDX-License-Identifier: AGPL-3.0-or-later
defmodule EtmaHandler.Marking.ArgumentationAnalyzer do
  @moduledoc """
  Rule-Based Argumentation Quality Analysis.

  Provides mechanical analysis of academic writing quality using linguistic
  patterns, without relying on neural networks. Results are INDICATIVE only
  and require human verification.

  ## Analysis Categories

  Based on Damer's principles and academic writing conventions:

  1. **Structure** - Paragraph organization, discourse markers, logical flow
  2. **Hedging/Certainty** - Use of uncertainty language vs overclaiming
  3. **Academic Register** - First person, emotional language, formality
  4. **Bloom's Taxonomy** - Cognitive level indicators in verb usage
  5. **Domain Relevance** - Term coverage against module materials
  6. **Potential Fallacy Indicators** - Patterns suggesting common fallacies

  ## Limitations

  This analyzer uses pattern matching and heuristics. It cannot:
  - Determine actual argument validity
  - Assess factual accuracy
  - Evaluate relevance of evidence to claims
  - Detect subtle logical errors

  All findings should be treated as flags for human review, not definitive judgments.

  ## Usage

      {:ok, analysis} = ArgumentationAnalyzer.analyze(text, module_terms: ["concept1", "concept2"])

      # Check specific aspects
      {:ok, hedging} = ArgumentationAnalyzer.check_hedging(text)
      {:ok, register} = ArgumentationAnalyzer.check_academic_register(text)
      {:ok, blooms} = ArgumentationAnalyzer.check_blooms_verbs(text)
  """

  require Logger

  # ============================================================
  # LINGUISTIC PATTERNS
  # ============================================================

  # Hedging/uncertainty markers (appropriate in academic writing)
  @hedging_markers [
    # Epistemic verbs
    {"may", :verb},
    {"might", :verb},
    {"could", :verb},
    {"appears to", :verb},
    {"seems to", :verb},
    {"suggests", :verb},
    {"indicates", :verb},
    # Hedging adverbs
    {"possibly", :adverb},
    {"probably", :adverb},
    {"perhaps", :adverb},
    {"potentially", :adverb},
    {"apparently", :adverb},
    {"arguably", :adverb},
    {"presumably", :adverb},
    # Hedging phrases
    {"it is possible that", :phrase},
    {"it could be argued", :phrase},
    {"there is evidence to suggest", :phrase},
    {"this may indicate", :phrase},
    {"to some extent", :phrase},
    {"in some cases", :phrase}
  ]

  # Overclaiming/absolutist language (often inappropriate)
  @overclaiming_markers [
    # Absolutist terms
    {"always", :adverb},
    {"never", :adverb},
    {"definitely", :adverb},
    {"certainly", :adverb},
    {"obviously", :adverb},
    {"clearly", :adverb},
    {"undoubtedly", :adverb},
    {"unquestionably", :adverb},
    {"undeniably", :adverb},
    # Absolutist phrases
    {"it is certain that", :phrase},
    {"there is no doubt", :phrase},
    {"everyone knows", :phrase},
    {"it is obvious", :phrase},
    {"the fact is", :phrase},
    {"proves that", :phrase},
    {"proves beyond doubt", :phrase}
  ]

  # First person markers (often discouraged in academic writing)
  @first_person_markers [
    {~r/\bI\s+(?:think|believe|feel|argue|contend|suggest|propose)/i, :opinion, :high},
    {~r/\bIn\s+my\s+(?:view|opinion|experience)/i, :opinion, :high},
    {~r/\bI\s+(?:will|shall|am\s+going\s+to)/i, :structural, :low},
    {~r/\bWe\s+(?:will|shall|can\s+see)/i, :structural, :low},
    {~r/\bmy\s+(?:research|analysis|argument|findings)/i, :ownership, :medium},
    {~r/\bI\b/i, :general, :low},
    {~r/\bme\b/i, :general, :low},
    {~r/\bmy\b/i, :general, :low}
  ]

  # Emotional/informal language markers
  @emotional_markers [
    # Strong emotional terms
    {"amazing", :positive},
    {"terrible", :negative},
    {"horrible", :negative},
    {"wonderful", :positive},
    {"shocking", :negative},
    {"outrageous", :negative},
    {"fantastic", :positive},
    {"disgraceful", :negative},
    {"ridiculous", :negative},
    {"brilliant", :positive},
    # Intensifiers
    {"very", :intensifier},
    {"extremely", :intensifier},
    {"incredibly", :intensifier},
    {"absolutely", :intensifier},
    {"totally", :intensifier},
    {"really", :intensifier}
  ]

  # Discourse markers by function
  @discourse_markers %{
    addition: [
      "furthermore",
      "moreover",
      "in addition",
      "additionally",
      "also",
      "besides",
      "likewise"
    ],
    contrast: [
      "however",
      "nevertheless",
      "nonetheless",
      "on the other hand",
      "conversely",
      "in contrast",
      "although",
      "while",
      "whereas"
    ],
    cause_effect: [
      "therefore",
      "consequently",
      "as a result",
      "hence",
      "thus",
      "accordingly",
      "because",
      "since"
    ],
    example: ["for example", "for instance", "such as", "namely", "specifically", "in particular"],
    summary: [
      "in conclusion",
      "to summarize",
      "in summary",
      "overall",
      "to conclude",
      "in brief"
    ],
    sequence: [
      "firstly",
      "secondly",
      "thirdly",
      "finally",
      "first",
      "next",
      "then",
      "subsequently"
    ]
  }

  # Bloom's Taxonomy verbs by level (Anderson & Krathwohl revision)
  @blooms_taxonomy %{
    remember: [
      "define",
      "describe",
      "identify",
      "list",
      "name",
      "recall",
      "recognize",
      "state",
      "match",
      "label"
    ],
    understand: [
      "classify",
      "compare",
      "contrast",
      "explain",
      "interpret",
      "summarize",
      "paraphrase",
      "discuss",
      "illustrate"
    ],
    apply: [
      "apply",
      "demonstrate",
      "implement",
      "solve",
      "use",
      "execute",
      "calculate",
      "show",
      "illustrate"
    ],
    analyze: [
      "analyze",
      "differentiate",
      "examine",
      "investigate",
      "categorize",
      "distinguish",
      "deconstruct",
      "compare",
      "critique"
    ],
    evaluate: [
      "assess",
      "evaluate",
      "critique",
      "judge",
      "justify",
      "argue",
      "defend",
      "support",
      "appraise",
      "conclude"
    ],
    create: [
      "create",
      "design",
      "develop",
      "formulate",
      "construct",
      "produce",
      "propose",
      "synthesize",
      "generate"
    ]
  }

  # Potential fallacy indicators (heuristic patterns only)
  @fallacy_indicators %{
    ad_hominem: [
      ~r/\b(?:stupid|ignorant|foolish|naive|biased)\s+(?:author|writer|critic)/i,
      ~r/\bthey\s+(?:obviously|clearly)\s+don't\s+understand/i,
      ~r/\bwhat\s+would\s+(?:he|she|they)\s+know\s+about/i
    ],
    appeal_to_authority: [
      ~r/\bexperts\s+(?:say|agree|claim)\b/i,
      ~r/\bscientists\s+(?:say|prove|have\s+shown)\b/i,
      ~r/\beveryone\s+(?:knows|agrees)\b/i,
      ~r/\bstudies\s+show\b/i
    ],
    false_dichotomy: [
      ~r/\b(?:either|only\s+two)\s+(?:options|choices|possibilities)/i,
      ~r/\bif\s+not\s+.+\s+then\s+must\s+be/i,
      ~r/\byou're\s+either\s+.+\s+or\s+.+/i
    ],
    hasty_generalization: [
      ~r/\ball\s+(?:\w+\s+)?(?:always|never)/i,
      ~r/\beveryone\s+(?:is|does|thinks)/i,
      ~r/\bnobody\s+(?:ever|can|does)/i,
      ~r/\bno\s+\w+\s+ever\b/i
    ],
    slippery_slope: [
      ~r/\bwill\s+(?:inevitably|necessarily)\s+lead\s+to/i,
      ~r/\bthe\s+next\s+step\s+(?:is|will\s+be)/i,
      ~r/\bonce\s+.+\s+there's\s+no\s+stopping/i
    ],
    straw_man: [
      ~r/\b(?:so\s+you're\s+saying|what\s+you\s+really\s+mean)/i,
      ~r/\bin\s+other\s+words,?\s+(?:you|they)\s+(?:think|believe)/i
    ]
  }

  # Rebuttal/counter-argument indicators
  @rebuttal_indicators [
    "however",
    "nevertheless",
    "on the other hand",
    "critics argue",
    "opponents suggest",
    "it could be argued",
    "an alternative view",
    "counter to this",
    "in response to",
    "this objection",
    "some might argue",
    "a potential criticism"
  ]

  @type analysis_result :: %{
          hedging: map(),
          overclaiming: map(),
          academic_register: map(),
          blooms_profile: map(),
          discourse_structure: map(),
          potential_fallacies: [map()],
          rebuttal_indicators: [String.t()],
          domain_coverage: map() | nil,
          overall_assessment: map()
        }

  # ============================================================
  # PUBLIC API
  # ============================================================

  @doc """
  Perform comprehensive argumentation analysis.

  Options:
  - `:module_terms` - List of domain-specific terms for relevance checking
  - `:assignment_scope` - Expected word count for sufficiency assessment
  - `:strict_register` - Whether to flag all first-person usage (default: false)
  """
  @spec analyze(String.t(), keyword()) :: {:ok, analysis_result()}
  def analyze(text, opts \\ []) do
    module_terms = Keyword.get(opts, :module_terms, [])
    assignment_scope = Keyword.get(opts, :assignment_scope)
    strict_register = Keyword.get(opts, :strict_register, false)

    {:ok, hedging} = check_hedging(text)
    {:ok, overclaiming} = check_overclaiming(text)
    {:ok, register} = check_academic_register(text, strict: strict_register)
    {:ok, blooms} = check_blooms_verbs(text)
    {:ok, discourse} = check_discourse_structure(text)
    {:ok, fallacies} = check_potential_fallacies(text)
    {:ok, rebuttals} = check_rebuttals(text)

    domain_coverage =
      if module_terms != [] do
        {:ok, coverage} = check_domain_coverage(text, module_terms)
        coverage
      else
        nil
      end

    overall = generate_overall_assessment(hedging, overclaiming, register, blooms, discourse, fallacies, rebuttals, domain_coverage, assignment_scope, text)

    {:ok,
     %{
       hedging: hedging,
       overclaiming: overclaiming,
       academic_register: register,
       blooms_profile: blooms,
       discourse_structure: discourse,
       potential_fallacies: fallacies,
       rebuttal_indicators: rebuttals,
       domain_coverage: domain_coverage,
       overall_assessment: overall
     }}
  end

  @doc """
  Check for appropriate use of hedging language.
  """
  @spec check_hedging(String.t()) :: {:ok, map()}
  def check_hedging(text) do
    text_lower = String.downcase(text)
    word_count = text |> String.split() |> length()

    matches =
      @hedging_markers
      |> Enum.map(fn {marker, type} ->
        count = count_occurrences(text_lower, marker)
        if count > 0, do: {marker, type, count}, else: nil
      end)
      |> Enum.reject(&is_nil/1)

    total_hedging = Enum.reduce(matches, 0, fn {_, _, count}, acc -> acc + count end)
    hedging_density = if word_count > 0, do: total_hedging / word_count * 100, else: 0

    assessment =
      cond do
        hedging_density < 0.5 -> :low_hedging
        hedging_density > 3.0 -> :excessive_hedging
        true -> :appropriate
      end

    {:ok,
     %{
       markers_found: matches,
       total_count: total_hedging,
       density_percent: Float.round(hedging_density, 2),
       assessment: assessment,
       note:
         case assessment do
           :low_hedging -> "Consider using more tentative language for claims that aren't definitively proven"
           :excessive_hedging -> "Some claims may benefit from more confident language where evidence is strong"
           :appropriate -> "Hedging appears appropriately balanced"
         end
     }}
  end

  @doc """
  Check for overclaiming/absolutist language.
  """
  @spec check_overclaiming(String.t()) :: {:ok, map()}
  def check_overclaiming(text) do
    text_lower = String.downcase(text)
    word_count = text |> String.split() |> length()

    matches =
      @overclaiming_markers
      |> Enum.map(fn {marker, type} ->
        count = count_occurrences(text_lower, marker)
        if count > 0, do: {marker, type, count}, else: nil
      end)
      |> Enum.reject(&is_nil/1)

    total = Enum.reduce(matches, 0, fn {_, _, count}, acc -> acc + count end)
    density = if word_count > 0, do: total / word_count * 100, else: 0

    assessment =
      cond do
        total == 0 -> :none_detected
        density > 1.0 -> :concerning
        density > 0.5 -> :moderate
        true -> :minor
      end

    {:ok,
     %{
       markers_found: matches,
       total_count: total,
       density_percent: Float.round(density, 2),
       assessment: assessment,
       note:
         case assessment do
           :none_detected -> "No absolutist language detected"
           :concerning -> "Several instances of overclaiming detected - consider qualifying claims"
           :moderate -> "Some absolutist language present - verify claims are fully supported"
           :minor -> "Minor instances of absolutist language"
         end
     }}
  end

  @doc """
  Check academic register (first person, emotional language, formality).
  """
  @spec check_academic_register(String.t(), keyword()) :: {:ok, map()}
  def check_academic_register(text, opts \\ []) do
    strict = Keyword.get(opts, :strict, false)
    text_lower = String.downcase(text)
    word_count = text |> String.split() |> length()

    # First person analysis
    first_person_matches =
      @first_person_markers
      |> Enum.map(fn {pattern, type, severity} ->
        count = pattern |> Regex.scan(text) |> length()
        if count > 0, do: {pattern, type, severity, count}, else: nil
      end)
      |> Enum.reject(&is_nil/1)

    high_severity_fp =
      first_person_matches
      |> Enum.filter(fn {_, _, severity, _} -> severity == :high end)
      |> Enum.reduce(0, fn {_, _, _, count}, acc -> acc + count end)

    total_fp = Enum.reduce(first_person_matches, 0, fn {_, _, _, count}, acc -> acc + count end)

    # Emotional language analysis
    emotional_matches =
      @emotional_markers
      |> Enum.map(fn {marker, type} ->
        count = count_occurrences(text_lower, marker)
        if count > 0, do: {marker, type, count}, else: nil
      end)
      |> Enum.reject(&is_nil/1)

    total_emotional = Enum.reduce(emotional_matches, 0, fn {_, _, count}, acc -> acc + count end)

    # Assessment
    first_person_assessment =
      cond do
        strict && total_fp > 0 -> :flagged
        high_severity_fp > 3 -> :excessive
        high_severity_fp > 0 -> :present
        true -> :minimal
      end

    emotional_assessment =
      cond do
        total_emotional > 5 -> :excessive
        total_emotional > 2 -> :moderate
        total_emotional > 0 -> :minimal
        true -> :none
      end

    {:ok,
     %{
       first_person: %{
         matches: first_person_matches,
         total: total_fp,
         high_severity: high_severity_fp,
         assessment: first_person_assessment
       },
       emotional_language: %{
         matches: emotional_matches,
         total: total_emotional,
         assessment: emotional_assessment
       },
       overall_register:
         if(first_person_assessment in [:excessive, :flagged] || emotional_assessment == :excessive,
           do: :informal,
           else: :academic
         )
     }}
  end

  @doc """
  Analyze verb usage against Bloom's Taxonomy levels.
  """
  @spec check_blooms_verbs(String.t()) :: {:ok, map()}
  def check_blooms_verbs(text) do
    text_lower = String.downcase(text)

    level_counts =
      @blooms_taxonomy
      |> Enum.map(fn {level, verbs} ->
        count =
          verbs
          |> Enum.reduce(0, fn verb, acc ->
            acc + count_word_occurrences(text_lower, verb)
          end)

        {level, count}
      end)
      |> Map.new()

    total = level_counts |> Map.values() |> Enum.sum()

    level_percentages =
      if total > 0 do
        level_counts
        |> Enum.map(fn {level, count} ->
          {level, Float.round(count / total * 100, 1)}
        end)
        |> Map.new()
      else
        level_counts |> Enum.map(fn {level, _} -> {level, 0.0} end) |> Map.new()
      end

    # Determine dominant level
    dominant_level =
      level_counts
      |> Enum.max_by(fn {_, count} -> count end, fn -> {:none, 0} end)
      |> elem(0)

    # Higher-order thinking ratio (analyze, evaluate, create vs remember, understand, apply)
    hot_count =
      Map.get(level_counts, :analyze, 0) + Map.get(level_counts, :evaluate, 0) +
        Map.get(level_counts, :create, 0)

    lot_count =
      Map.get(level_counts, :remember, 0) + Map.get(level_counts, :understand, 0) +
        Map.get(level_counts, :apply, 0)

    hot_ratio = if (hot_count + lot_count) > 0, do: hot_count / (hot_count + lot_count), else: 0

    cognitive_assessment =
      cond do
        hot_ratio > 0.6 -> :high_order_dominant
        hot_ratio > 0.4 -> :balanced
        hot_ratio > 0.2 -> :lower_order_dominant
        total == 0 -> :insufficient_data
        true -> :predominantly_descriptive
      end

    {:ok,
     %{
       level_counts: level_counts,
       level_percentages: level_percentages,
       total_taxonomy_verbs: total,
       dominant_level: dominant_level,
       higher_order_ratio: Float.round(hot_ratio, 2),
       assessment: cognitive_assessment,
       note:
         case cognitive_assessment do
           :high_order_dominant -> "Writing shows strong higher-order thinking (analysis, evaluation, synthesis)"
           :balanced -> "Good balance between descriptive and analytical writing"
           :lower_order_dominant -> "Consider incorporating more analytical and evaluative language"
           :predominantly_descriptive -> "Writing is largely descriptive - more critical engagement may be needed"
           :insufficient_data -> "Insufficient cognitive verbs detected for assessment"
         end
     }}
  end

  @doc """
  Analyze discourse structure (logical connectors, signposting).
  """
  @spec check_discourse_structure(String.t()) :: {:ok, map()}
  def check_discourse_structure(text) do
    text_lower = String.downcase(text)
    word_count = text |> String.split() |> length()

    function_counts =
      @discourse_markers
      |> Enum.map(fn {function, markers} ->
        count =
          markers
          |> Enum.reduce(0, fn marker, acc ->
            acc + count_occurrences(text_lower, marker)
          end)

        {function, count}
      end)
      |> Map.new()

    total_markers = function_counts |> Map.values() |> Enum.sum()
    marker_density = if word_count > 0, do: total_markers / word_count * 100, else: 0

    # Check for balanced use
    functions_present =
      function_counts
      |> Enum.filter(fn {_, count} -> count > 0 end)
      |> length()

    structure_assessment =
      cond do
        marker_density < 0.5 -> :minimal_signposting
        marker_density > 5.0 -> :over_signposted
        functions_present < 3 -> :limited_variety
        true -> :well_structured
      end

    {:ok,
     %{
       function_counts: function_counts,
       total_markers: total_markers,
       density_percent: Float.round(marker_density, 2),
       functions_represented: functions_present,
       assessment: structure_assessment,
       note:
         case structure_assessment do
           :minimal_signposting -> "Consider using more discourse markers to guide the reader"
           :over_signposted -> "High density of discourse markers - some may be redundant"
           :limited_variety -> "Use a wider variety of connectors (addition, contrast, causation, etc.)"
           :well_structured -> "Good use of discourse markers for logical flow"
         end
     }}
  end

  @doc """
  Check for patterns that may indicate logical fallacies.

  IMPORTANT: These are heuristic indicators only. Many matches may be
  perfectly valid arguments. All findings require human review.
  """
  @spec check_potential_fallacies(String.t()) :: {:ok, [map()]}
  def check_potential_fallacies(text) do
    findings =
      @fallacy_indicators
      |> Enum.flat_map(fn {fallacy_type, patterns} ->
        patterns
        |> Enum.flat_map(fn pattern ->
          Regex.scan(pattern, text)
          |> Enum.map(fn [match | _] ->
            %{
              type: fallacy_type,
              matched_text: match,
              severity: :potential,
              note: "This pattern MAY indicate #{format_fallacy_name(fallacy_type)} but requires human verification"
            }
          end)
        end)
      end)

    {:ok, findings}
  end

  @doc """
  Check for rebuttal/counter-argument indicators.
  """
  @spec check_rebuttals(String.t()) :: {:ok, [String.t()]}
  def check_rebuttals(text) do
    text_lower = String.downcase(text)

    found =
      @rebuttal_indicators
      |> Enum.filter(&String.contains?(text_lower, &1))

    {:ok, found}
  end

  @doc """
  Check coverage of domain-specific terminology.
  """
  @spec check_domain_coverage(String.t(), [String.t()]) :: {:ok, map()}
  def check_domain_coverage(text, module_terms) do
    text_lower = String.downcase(text)

    term_counts =
      module_terms
      |> Enum.map(fn term ->
        count = count_occurrences(text_lower, String.downcase(term))
        {term, count}
      end)
      |> Map.new()

    terms_used =
      term_counts
      |> Enum.filter(fn {_, count} -> count > 0 end)
      |> length()

    coverage_percent =
      if length(module_terms) > 0 do
        Float.round(terms_used / length(module_terms) * 100, 1)
      else
        0.0
      end

    assessment =
      cond do
        coverage_percent >= 70 -> :excellent
        coverage_percent >= 50 -> :good
        coverage_percent >= 30 -> :moderate
        coverage_percent > 0 -> :limited
        true -> :none
      end

    {:ok,
     %{
       module_terms: length(module_terms),
       terms_used: terms_used,
       coverage_percent: coverage_percent,
       term_counts: term_counts,
       assessment: assessment,
       note:
         case assessment do
           :excellent -> "Excellent coverage of module terminology"
           :good -> "Good engagement with module concepts"
           :moderate -> "Moderate use of module terminology - consider engaging more with key concepts"
           :limited -> "Limited use of module-specific terms"
           :none -> "No module terms detected - ensure answer addresses the subject matter"
         end
     }}
  end

  # ============================================================
  # PRIVATE - HELPERS
  # ============================================================

  defp count_occurrences(text, pattern) when is_binary(pattern) do
    text
    |> String.split(pattern)
    |> length()
    |> Kernel.-(1)
    |> max(0)
  end

  defp count_word_occurrences(text, word) do
    ~r/\b#{Regex.escape(word)}(?:s|ed|ing|es)?\b/i
    |> Regex.scan(text)
    |> length()
  end

  defp format_fallacy_name(atom) do
    atom
    |> Atom.to_string()
    |> String.replace("_", " ")
    |> String.capitalize()
  end

  defp generate_overall_assessment(hedging, overclaiming, register, blooms, discourse, fallacies, rebuttals, domain_coverage, assignment_scope, text) do
    word_count = text |> String.split() |> length()

    # Calculate component scores (0-1 scale)
    hedging_score =
      case hedging.assessment do
        :appropriate -> 1.0
        :low_hedging -> 0.6
        :excessive_hedging -> 0.6
      end

    overclaiming_score =
      case overclaiming.assessment do
        :none_detected -> 1.0
        :minor -> 0.8
        :moderate -> 0.5
        :concerning -> 0.2
      end

    register_score =
      case register.overall_register do
        :academic -> 1.0
        :informal -> 0.4
      end

    blooms_score =
      case blooms.assessment do
        :high_order_dominant -> 1.0
        :balanced -> 0.8
        :lower_order_dominant -> 0.5
        :predominantly_descriptive -> 0.3
        :insufficient_data -> 0.5
      end

    discourse_score =
      case discourse.assessment do
        :well_structured -> 1.0
        :over_signposted -> 0.7
        :limited_variety -> 0.5
        :minimal_signposting -> 0.3
      end

    fallacy_penalty = min(length(fallacies) * 0.1, 0.3)
    rebuttal_bonus = if length(rebuttals) > 0, do: 0.1, else: 0.0

    domain_score =
      case domain_coverage do
        nil -> 0.5
        coverage ->
          case coverage.assessment do
            :excellent -> 1.0
            :good -> 0.8
            :moderate -> 0.6
            :limited -> 0.3
            :none -> 0.1
          end
      end

    # Weighted average
    base_score =
      hedging_score * 0.1 +
        overclaiming_score * 0.15 +
        register_score * 0.15 +
        blooms_score * 0.2 +
        discourse_score * 0.15 +
        domain_score * 0.25

    final_score = max(0.0, min(1.0, base_score - fallacy_penalty + rebuttal_bonus))

    # Sufficiency check
    sufficiency =
      case assignment_scope do
        nil ->
          nil

        expected_words ->
          ratio = word_count / expected_words

          cond do
            ratio < 0.8 -> {:under, Float.round(ratio * 100, 1)}
            ratio > 1.1 -> {:over, Float.round(ratio * 100, 1)}
            true -> {:appropriate, Float.round(ratio * 100, 1)}
          end
      end

    quality_band =
      cond do
        final_score >= 0.8 -> :strong
        final_score >= 0.6 -> :satisfactory
        final_score >= 0.4 -> :developing
        true -> :needs_work
      end

    %{
      overall_score: Float.round(final_score * 100, 1),
      quality_band: quality_band,
      word_count: word_count,
      sufficiency: sufficiency,
      strengths: identify_strengths(hedging, overclaiming, register, blooms, discourse, rebuttals, domain_coverage),
      areas_for_improvement: identify_improvements(hedging, overclaiming, register, blooms, discourse, fallacies, domain_coverage),
      disclaimer: "This is a mechanical analysis based on linguistic patterns. It provides indicators for human review, not definitive assessments of argument quality."
    }
  end

  defp identify_strengths(hedging, overclaiming, register, blooms, discourse, rebuttals, domain_coverage) do
    strengths = []

    strengths =
      if hedging.assessment == :appropriate,
        do: ["Appropriate use of hedging language" | strengths],
        else: strengths

    strengths =
      if overclaiming.assessment == :none_detected,
        do: ["Avoids overclaiming" | strengths],
        else: strengths

    strengths =
      if register.overall_register == :academic,
        do: ["Maintains academic register" | strengths],
        else: strengths

    strengths =
      if blooms.assessment in [:high_order_dominant, :balanced],
        do: ["Good cognitive level engagement" | strengths],
        else: strengths

    strengths =
      if discourse.assessment == :well_structured,
        do: ["Well-structured argument with clear signposting" | strengths],
        else: strengths

    strengths =
      if length(rebuttals) > 0,
        do: ["Considers counter-arguments" | strengths],
        else: strengths

    strengths =
      if domain_coverage && domain_coverage.assessment in [:excellent, :good],
        do: ["Good engagement with module content" | strengths],
        else: strengths

    strengths
  end

  defp identify_improvements(hedging, overclaiming, register, blooms, discourse, fallacies, domain_coverage) do
    improvements = []

    improvements =
      if hedging.assessment == :low_hedging,
        do: ["Consider using more tentative language for uncertain claims" | improvements],
        else: improvements

    improvements =
      if overclaiming.assessment in [:moderate, :concerning],
        do: ["Reduce absolutist claims - qualify statements where appropriate" | improvements],
        else: improvements

    improvements =
      if register.overall_register == :informal,
        do: ["Improve academic register - reduce first person and emotional language" | improvements],
        else: improvements

    improvements =
      if blooms.assessment in [:lower_order_dominant, :predominantly_descriptive],
        do: ["Increase analytical and evaluative engagement" | improvements],
        else: improvements

    improvements =
      if discourse.assessment == :minimal_signposting,
        do: ["Add more discourse markers to guide reader through argument" | improvements],
        else: improvements

    improvements =
      if length(fallacies) > 0,
        do: ["Review flagged passages for potential logical issues" | improvements],
        else: improvements

    improvements =
      if domain_coverage && domain_coverage.assessment in [:limited, :none],
        do: ["Engage more with module terminology and concepts" | improvements],
        else: improvements

    improvements
  end
end
