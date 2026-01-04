# SPDX-License-Identifier: AGPL-3.0-or-later
defmodule EtmaHandler.Marking do
  @moduledoc """
  # eTMA Marking Analysis System

  ## CRITICAL LIMITATIONS - READ BEFORE USE

  This system provides **automated analysis** to assist human markers.
  It does NOT and CANNOT:

  - **Grade work** - Only a human can assess whether arguments are valid
  - **Detect bullshit** - Well-written nonsense scores high on form metrics
  - **Evaluate understanding** - Surface markers ≠ deep comprehension
  - **Replace judgment** - All outputs are flags for human review

  ## What This System Measures

  ### Form Metrics (High Confidence)

  These measure *how* something is written, not *what* is said:

  | Metric | Detects | Cannot Detect |
  |--------|---------|---------------|
  | Syntactic Complexity | Sentence structure | Meaningful complexity |
  | Vocabulary Richness | Word variety | Appropriate word choice |
  | Cohesion | Textual flow | Logical coherence |
  | Academic Register | Formal style | Genuine understanding |
  | Citation Patterns | Reference format | Reference relevance |

  ### Content Proxies (Lower Confidence)

  These are *indicators* that require human verification:

  | Proxy | Suggests | Fails When |
  |-------|----------|------------|
  | Domain Terms | Topic engagement | Terms used without understanding |
  | Evidence Density | Research done | Evidence irrelevant to claims |
  | Argumentation Markers | Structured thinking | Markers without actual argument |
  | Bloom's Taxonomy Verbs | Cognitive level | Verbs misused or decorative |
  | Hedging/Overclaiming | Epistemic awareness | Hedging hides weak claims |

  ## The "Debating Champion" Problem

  A student trained in rhetoric can produce text that:
  - Uses all the right discourse markers
  - Has perfect academic register
  - Includes appropriate hedging
  - Cites sources correctly
  - Says absolutely nothing of substance

  **This system will give high form scores to such work.**

  The QualityDivergence module attempts to flag this by detecting when:
  - Form metrics are high
  - Content proxies are low (no domain terms, shallow Bloom's verbs)
  - But this is a heuristic, not a solution

  ## The ESL Writer Problem

  A student with limited English proficiency may produce text that:
  - Has grammatical errors
  - Uses simpler sentence structures
  - Has lower vocabulary metrics
  - Makes excellent points with strong reasoning

  **This system will give low form scores to such work.**

  The QualityDivergence module flags this for human attention:
  - Form metrics are low
  - Content proxies are high (domain terms present, evidence cited)
  - Human should focus on content, not language

  ## Recommended Workflow

  1. Run automated analysis
  2. Check QualityDivergence flags first
  3. For `:form_over_content` - read critically for substance
  4. For `:content_over_form` - focus feedback on ideas, note language support
  5. Use form metrics to guide feedback on writing skills
  6. Use content proxies to identify areas needing verification
  7. Apply human judgment for actual grading

  ## Module Index

  ### Core Analysis
  - `ArgumentationAnalyzer` - Hedging, overclaiming, discourse markers, Bloom's verbs
  - `TextStatistics` - Word frequencies, vocabulary, cohesion, evidence density
  - `SyntacticComplexity` - T-units, clause analysis, embedding depth
  - `QualityDivergence` - Form vs content divergence detection

  ### Reference & Source Analysis
  - `ReferenceAnalyzer` - Citation parsing, reference list validation
  - `WebSourceEvaluator` - PROMPT framework for web sources

  ### Feedback Generation
  - `Rubric` - Structured marking criteria
  - `FeedbackLibrary` - Reusable comments and materials
  - `FeedbackGenerator` - Automated feedback from rubric bands

  ### Security & Validation
  - `Bouncer` - File validation
  - `Scanner` - Malware detection
  - `Container` - Safe file packaging
  """

  @doc """
  Run comprehensive analysis on a submission.

  Returns a structured analysis with explicit confidence levels
  and flags for human review.

  ## Options

  - `:domain_terms` - Expected domain terminology for this assignment
  - `:assignment_level` - Academic level (:level_1, :level_2, :level_3, :postgrad)
  - `:word_limit` - Expected word count
  - `:rubric` - Rubric struct for this assignment

  ## Returns

  ```elixir
  %{
    form_analysis: %{...},      # High confidence, mechanical checks
    content_proxies: %{...},    # Lower confidence, require verification
    divergence: %{...},         # Form vs content comparison
    flags: [...],               # Issues requiring human attention
    confidence: :high | :medium | :low,
    human_focus_areas: [...]    # Where marker should focus attention
  }
  ```
  """
  @spec analyze(String.t(), keyword()) :: {:ok, map()} | {:error, term()}
  def analyze(text, opts \\ []) do
    alias EtmaHandler.Marking.{
      SyntacticComplexity,
      TextStatistics,
      ArgumentationAnalyzer,
      QualityDivergence
    }

    with {:ok, syntax} <- SyntacticComplexity.analyze(text),
         {:ok, stats} <- TextStatistics.analyze(text),
         {:ok, args} <- ArgumentationAnalyzer.analyze(text),
         {:ok, divergence} <- QualityDivergence.analyze(text, opts) do
      {:ok,
       %{
         # Mechanical, high-confidence metrics
         form_analysis: %{
           syntax: syntax,
           vocabulary: stats.vocabulary,
           cohesion: stats.cohesion,
           structure: stats.structure,
           confidence: :high,
           note: "These measure HOW text is written, not WHAT it says"
         },

         # Proxies that require human verification
         content_proxies: %{
           argumentation: args,
           evidence: stats.evidence,
           domain_coverage: divergence.detail.domain_term_coverage,
           confidence: :low,
           note:
             "These are INDICATORS only. A skilled rhetorician can fake all of these."
         },

         # The critical divergence analysis
         divergence: divergence,

         # Aggregated flags for attention
         flags: aggregate_flags(syntax, stats, args, divergence, opts),

         # Where the human should focus
         human_focus_areas: determine_focus_areas(divergence, stats, args),

         # Overall analysis confidence
         confidence: divergence.confidence,

         # Explicit limitations reminder
         limitations: %{
           cannot_detect: [
             "Whether arguments are actually valid",
             "Whether evidence supports claims",
             "Whether the student understands the material",
             "Whether sophisticated language masks empty content"
           ],
           requires_human: [
             "Grading",
             "Assessing argument validity",
             "Evaluating understanding",
             "Final quality judgment"
           ]
         }
       }}
    end
  end

  # ==========================================================================
  # Private Helpers
  # ==========================================================================

  defp aggregate_flags(_syntax, stats, args, divergence, opts) do
    flags = divergence.flags

    # Add word count flag if provided
    word_limit = Keyword.get(opts, :word_limit)

    flags =
      if word_limit && stats.basic.word_count > word_limit * 1.1 do
        [
          %{
            type: :over_word_limit,
            severity: :warning,
            message:
              "Word count (#{stats.basic.word_count}) exceeds limit (#{word_limit}) by " <>
                "#{round((stats.basic.word_count / word_limit - 1) * 100)}%"
          }
          | flags
        ]
      else
        flags
      end

    # Add under word limit flag
    flags =
      if word_limit && stats.basic.word_count < word_limit * 0.8 do
        [
          %{
            type: :under_word_limit,
            severity: :warning,
            message:
              "Word count (#{stats.basic.word_count}) is significantly under limit (#{word_limit})"
          }
          | flags
        ]
      else
        flags
      end

    # Add overclaiming flag
    flags =
      if args.overclaiming.count > 3 do
        [
          %{
            type: :overclaiming,
            severity: :warning,
            message:
              "Multiple instances of potentially overclaiming language. " <>
                "Examples: #{Enum.take(args.overclaiming.examples, 2) |> Enum.join(", ")}"
          }
          | flags
        ]
      else
        flags
      end

    # Add potential fallacy flag
    fallacy_count =
      args.potential_fallacies
      |> Map.values()
      |> List.flatten()
      |> length()

    flags =
      if fallacy_count > 0 do
        [
          %{
            type: :potential_fallacies,
            severity: :info,
            message:
              "#{fallacy_count} potential informal fallacy pattern(s) detected. " <>
                "Verify if these are actual fallacies in context."
          }
          | flags
        ]
      else
        flags
      end

    flags
  end

  defp determine_focus_areas(divergence, stats, args) do
    areas = []

    areas =
      case divergence.divergence_type do
        :form_over_content ->
          ["Verify substance behind polished prose", "Check evidence supports claims" | areas]

        :content_over_form ->
          ["Focus on ideas, not grammar", "Acknowledge strong content" | areas]

        _ ->
          areas
      end

    # Check for weak evidence
    areas =
      if stats.evidence.citation_density < 0.01 do
        ["Check for evidence and sources" | areas]
      else
        areas
      end

    # Check for missing discourse markers
    discourse_total = args.discourse_markers |> Map.values() |> Enum.sum()

    areas =
      if discourse_total < 3 do
        ["Check for structured argumentation" | areas]
      else
        areas
      end

    areas
  end
end
