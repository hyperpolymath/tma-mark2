# SPDX-License-Identifier: AGPL-3.0-or-later
defmodule EtmaHandler.Marking.FeedbackGenerator do
  @moduledoc """
  Automated Feedback Generation & Validation.

  Generates structured feedback documents with:

  1. **Personalized greeting** - Uses correct student name from metadata
  2. **Question-structured feedback** - Organizes by TMA questions
  3. **Rubric integration** - Auto-generates band-based feedback
  4. **Mark consistency checks** - Warns if marks deviate from rubric
  5. **Pre-send validation** - Ensures comments present, document identity verified

  ## Usage

      # Generate feedback structure
      {:ok, feedback} = FeedbackGenerator.generate(metadata, rubric_scores, opts)

      # Validate before sending
      {:ok, :ready} = FeedbackGenerator.validate_for_return(marked_path, original_path)

      # Check mark consistency
      {:ok, :consistent} = FeedbackGenerator.check_consistency(rubric, scores, overall_mark)
  """

  require Logger

  alias EtmaHandler.Marking.{Rubric, DocumentAnalyzer}

  # Greeting templates by context
  @greetings %{
    good: [
      "Well done on this assignment, {name}!",
      "Good work on this TMA, {name}.",
      "Nice effort on this assignment, {name}."
    ],
    improvement: [
      "Thank you for your submission, {name}.",
      "{name}, thank you for submitting this TMA.",
      "I have reviewed your work, {name}."
    ],
    concern: [
      "{name}, I have some feedback on your submission.",
      "Thank you for your submission, {name}. Please read my comments carefully.",
      "{name}, please review the feedback below carefully."
    ]
  }

  @type feedback_section :: %{
          question: String.t(),
          score: integer() | nil,
          band: String.t() | nil,
          comments: String.t(),
          auto_feedback: String.t() | nil
        }

  @type feedback_document :: %{
          greeting: String.t(),
          summary: String.t(),
          sections: [feedback_section()],
          overall_mark: integer(),
          overall_grade: String.t(),
          closing: String.t(),
          tutor_signature: String.t()
        }

  # ============================================================
  # PUBLIC API - GENERATION
  # ============================================================

  @doc """
  Generate a structured feedback document.

  ## Parameters

  - `metadata` - Submission metadata (must include student_oucu or student_name)
  - `rubric_scores` - Map of criterion_id => score
  - `options`:
    - `:rubric` - Rubric struct to use
    - `:overall_mark` - Override calculated mark
    - `:tutor_name` - Tutor's name for signature
    - `:custom_greeting` - Override auto-greeting
    - `:custom_closing` - Override auto-closing
    - `:question_comments` - Map of question => comments
  """
  @spec generate(map(), map(), keyword()) :: {:ok, feedback_document()} | {:error, term()}
  def generate(metadata, rubric_scores, opts \\ []) do
    # Get student name
    student_name = get_student_name(metadata)

    # Get rubric and calculate if provided
    rubric = Keyword.get(opts, :rubric)

    {overall_mark, overall_grade, sections} =
      if rubric do
        {:ok, result} = Rubric.calculate(rubric, rubric_scores)

        sections =
          Enum.map(result.criterion_results, fn r ->
            %{
              question: r.criterion_name,
              score: r.score,
              band: r.band,
              comments: Map.get(opts[:question_comments] || %{}, r.criterion_id, ""),
              auto_feedback: r.feedback
            }
          end)

        mark = Keyword.get(opts, :overall_mark, result.total)
        {mark, result.grade, sections}
      else
        mark = Keyword.get(opts, :overall_mark, 0)
        {mark, determine_grade(mark), []}
      end

    # Generate greeting based on performance
    greeting =
      Keyword.get(opts, :custom_greeting) ||
        generate_greeting(student_name, overall_mark)

    # Generate summary
    summary = generate_summary(metadata, overall_mark, overall_grade)

    # Generate closing
    closing =
      Keyword.get(opts, :custom_closing) ||
        generate_closing(overall_mark)

    # Tutor signature
    tutor_name = Keyword.get(opts, :tutor_name, "Your Tutor")

    {:ok,
     %{
       greeting: greeting,
       summary: summary,
       sections: sections,
       overall_mark: overall_mark,
       overall_grade: overall_grade,
       closing: closing,
       tutor_signature: tutor_name,
       generated_at: DateTime.utc_now()
     }}
  end

  @doc """
  Format feedback as plain text.
  """
  @spec format_text(feedback_document()) :: String.t()
  def format_text(feedback) do
    sections_text =
      feedback.sections
      |> Enum.map(fn s ->
        """
        #{s.question}
        Score: #{s.score || "N/A"}% (#{s.band || "N/A"})

        #{s.comments}

        #{if s.auto_feedback, do: "[Standard feedback: #{s.auto_feedback}]", else: ""}
        """
      end)
      |> Enum.join("\n---\n")

    """
    #{feedback.greeting}

    #{feedback.summary}

    ---
    DETAILED FEEDBACK
    ---

    #{sections_text}

    ---

    Overall Mark: #{feedback.overall_mark}%
    Grade: #{feedback.overall_grade}

    #{feedback.closing}

    #{feedback.tutor_signature}
    """
  end

  # ============================================================
  # PUBLIC API - VALIDATION
  # ============================================================

  @doc """
  Validate a marked document is ready for return.

  Checks:
  - Document has comments (tutor feedback)
  - No unresolved track changes
  - Document identity matches original submission
  """
  @spec validate_for_return(Path.t(), Path.t()) :: {:ok, :ready} | {:error, [map()]}
  def validate_for_return(marked_path, original_path) do
    issues = []

    # Check marked document has comments
    issues =
      case DocumentAnalyzer.check_marked_document(marked_path) do
        {:ok, :ready} -> issues
        {:error, doc_issues} -> issues ++ doc_issues
      end

    # Verify document identity
    issues =
      case verify_document_identity(marked_path, original_path) do
        {:ok, :matched} ->
          issues

        {:error, :mismatch} ->
          [
            %{
              type: :document_mismatch,
              severity: :error,
              message: "The marked document does not appear to be based on the original submission. Please verify you are returning the correct assignment."
            }
            | issues
          ]

        {:error, reason} ->
          [
            %{
              type: :verification_failed,
              severity: :warning,
              message: "Could not verify document identity: #{inspect(reason)}"
            }
            | issues
          ]
      end

    # Check for any errors (not just warnings)
    if Enum.any?(issues, &(&1.severity == :error)) do
      {:error, issues}
    else
      {:ok, :ready}
    end
  end

  @doc """
  Check if the tutor's mark is consistent with rubric scores.

  Returns warnings if there's a significant discrepancy.
  """
  @spec check_consistency(Rubric.t(), map(), integer()) ::
          {:ok, :consistent} | {:warning, map()}
  def check_consistency(rubric, scores, overall_mark) do
    {:ok, result} = Rubric.calculate(rubric, scores)
    calculated = result.total

    difference = abs(overall_mark - calculated)

    cond do
      difference == 0 ->
        {:ok, :consistent}

      difference <= 5 ->
        {:ok, :consistent}

      difference <= 10 ->
        {:warning,
         %{
           type: :minor_discrepancy,
           calculated: calculated,
           given: overall_mark,
           difference: difference,
           message: "The overall mark (#{overall_mark}) differs from the rubric calculation (#{calculated}) by #{difference}%. This may be intentional."
         }}

      true ->
        {:warning,
         %{
           type: :major_discrepancy,
           calculated: calculated,
           given: overall_mark,
           difference: difference,
           message: "WARNING: The overall mark (#{overall_mark}) differs significantly from the rubric calculation (#{calculated}) by #{difference}%. Please verify this is correct."
         }}
    end
  end

  @doc """
  Suggest a mark based on rubric scores.
  """
  @spec suggest_mark(Rubric.t(), map()) :: {:ok, integer()}
  def suggest_mark(rubric, scores) do
    {:ok, result} = Rubric.calculate(rubric, scores)
    {:ok, result.total}
  end

  @doc """
  Validate scores are within expected ranges for each criterion.
  """
  @spec validate_scores(Rubric.t(), map()) :: {:ok, :valid} | {:warning, [map()]}
  def validate_scores(rubric, scores) do
    warnings =
      Enum.flat_map(rubric.criteria, fn criterion ->
        score = Map.get(scores, criterion.id, 0)

        cond do
          score == 0 ->
            [
              %{
                criterion: criterion.name,
                score: score,
                message: "Score of 0% for #{criterion.name} - is this intentional?"
              }
            ]

          score == 100 ->
            [
              %{
                criterion: criterion.name,
                score: score,
                message: "Perfect score of 100% for #{criterion.name} - please verify"
              }
            ]

          true ->
            []
        end
      end)

    if warnings == [] do
      {:ok, :valid}
    else
      {:warning, warnings}
    end
  end

  # ============================================================
  # PUBLIC API - DOCUMENT IDENTITY
  # ============================================================

  @doc """
  Verify that a marked document is based on the original submission.

  Compares core content (ignoring comments and track changes) to ensure
  the correct document is being returned.
  """
  @spec verify_document_identity(Path.t(), Path.t()) ::
          {:ok, :matched} | {:error, :mismatch | term()}
  def verify_document_identity(marked_path, original_path) do
    with {:ok, marked_content} <- extract_core_content(marked_path),
         {:ok, original_content} <- extract_core_content(original_path) do
      # Calculate similarity
      similarity = calculate_similarity(marked_content, original_content)

      if similarity >= 0.85 do
        {:ok, :matched}
      else
        {:error, :mismatch}
      end
    end
  end

  @doc """
  Get a content hash for document identity tracking.
  """
  @spec content_hash(Path.t()) :: {:ok, String.t()} | {:error, term()}
  def content_hash(file_path) do
    case extract_core_content(file_path) do
      {:ok, content} ->
        hash = :crypto.hash(:sha256, content) |> Base.encode16(case: :lower)
        {:ok, hash}

      error ->
        error
    end
  end

  # ============================================================
  # PRIVATE - GREETING GENERATION
  # ============================================================

  defp get_student_name(metadata) do
    cond do
      Map.has_key?(metadata, :student_name) and metadata.student_name ->
        metadata.student_name

      Map.has_key?(metadata, "student_name") and metadata["student_name"] ->
        metadata["student_name"]

      Map.has_key?(metadata, :student_oucu) ->
        # Extract first name from OUCU if possible, otherwise use OUCU
        oucu = metadata.student_oucu
        # OUCUs are typically initials + numbers, so just use it as-is
        oucu

      Map.has_key?(metadata, "student_oucu") ->
        metadata["student_oucu"]

      true ->
        "Student"
    end
  end

  defp generate_greeting(name, mark) do
    templates =
      cond do
        mark >= 70 -> @greetings.good
        mark >= 40 -> @greetings.improvement
        true -> @greetings.concern
      end

    template = Enum.random(templates)
    String.replace(template, "{name}", name)
  end

  defp generate_summary(metadata, mark, grade) do
    assignment_ref =
      if Map.has_key?(metadata, :course_code) do
        "#{metadata.course_code} #{metadata.tma_number}"
      else
        "this assignment"
      end

    "This is my feedback on #{assignment_ref}. Your mark is #{mark}% (#{grade})."
  end

  defp generate_closing(mark) do
    cond do
      mark >= 85 ->
        "Excellent work! Keep up this standard."

      mark >= 70 ->
        "Good work overall. Review my comments to see where you can improve further."

      mark >= 55 ->
        "A solid effort. Please review my feedback carefully to help improve your future submissions."

      mark >= 40 ->
        "Please read my comments carefully. If you have questions, don't hesitate to get in touch."

      true ->
        "Please read my feedback carefully and contact me if you need to discuss this further."
    end
  end

  defp determine_grade(mark) do
    cond do
      mark >= 85 -> "Distinction"
      mark >= 70 -> "Pass 1"
      mark >= 55 -> "Pass 2"
      mark >= 40 -> "Pass 3"
      mark >= 30 -> "Pass 4"
      true -> "Fail"
    end
  end

  # ============================================================
  # PRIVATE - CONTENT EXTRACTION & COMPARISON
  # ============================================================

  defp extract_core_content(file_path) do
    ext = file_path |> Path.extname() |> String.downcase()

    case ext do
      ".docx" -> extract_docx_core(file_path)
      ".odt" -> extract_odt_core(file_path)
      ".rtf" -> extract_rtf_core(file_path)
      _ -> {:error, {:unsupported_format, ext}}
    end
  end

  defp extract_docx_core(path) do
    case :zip.unzip(String.to_charlist(path), [:memory]) do
      {:ok, files} ->
        files_map = Map.new(files, fn {name, content} -> {List.to_string(name), content} end)
        doc_xml = files_map["word/document.xml"] || ""

        # Remove comments, track changes, and metadata - keep only core text
        content =
          doc_xml
          |> String.replace(~r/<w:commentRangeStart[^>]*\/>/, "")
          |> String.replace(~r/<w:commentRangeEnd[^>]*\/>/, "")
          |> String.replace(~r/<w:commentReference[^>]*\/>/, "")
          |> String.replace(~r/<w:ins\s[^>]*>.*?<\/w:ins>/s, "")
          |> String.replace(~r/<w:del\s[^>]*>.*?<\/w:del>/s, "")
          |> String.replace(~r/<[^>]+>/, " ")
          |> String.replace(~r/\s+/, " ")
          |> String.trim()
          |> String.downcase()

        {:ok, content}

      {:error, reason} ->
        {:error, {:zip_error, reason}}
    end
  end

  defp extract_odt_core(path) do
    case :zip.unzip(String.to_charlist(path), [:memory]) do
      {:ok, files} ->
        files_map = Map.new(files, fn {name, content} -> {List.to_string(name), content} end)
        content_xml = files_map["content.xml"] || ""

        content =
          content_xml
          |> String.replace(~r/<office:annotation[^>]*>.*?<\/office:annotation>/s, "")
          |> String.replace(~r/<[^>]+>/, " ")
          |> String.replace(~r/\s+/, " ")
          |> String.trim()
          |> String.downcase()

        {:ok, content}

      {:error, reason} ->
        {:error, {:zip_error, reason}}
    end
  end

  defp extract_rtf_core(path) do
    case File.read(path) do
      {:ok, content} ->
        text =
          content
          |> String.replace(~r/\\[a-z]+\d*\s?/i, " ")
          |> String.replace(~r/\{|\}/, "")
          |> String.replace(~r/\s+/, " ")
          |> String.trim()
          |> String.downcase()

        {:ok, text}

      error ->
        error
    end
  end

  defp calculate_similarity(text1, text2) do
    # Simple word-based Jaccard similarity
    words1 = text1 |> String.split() |> MapSet.new()
    words2 = text2 |> String.split() |> MapSet.new()

    intersection = MapSet.intersection(words1, words2) |> MapSet.size()
    union = MapSet.union(words1, words2) |> MapSet.size()

    if union == 0 do
      0.0
    else
      intersection / union
    end
  end
end
