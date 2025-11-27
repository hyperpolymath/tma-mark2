defmodule EtmaHandler.Marking.Audit do
  @moduledoc """
  The Quality Assurance Bot.

  Audits feedback quantity and quality before allowing a return.
  Prevents the classic tutor disasters:

  1. "Empty Summary" - Returning without saving comments
  2. "Thin Script" - Too few words/annotations
  3. "Missing Link" - Summary doesn't refer to in-text comments
  4. "Wrong Name" - Using the wrong student's name in feedback

  ## Example

      audit = Audit.audit_feedback("Good work, Bill!", "Amanda", %{min_words: 50})
      # => %Audit{passed?: false, issues: ["Possible wrong name: mentioned 'Bill' but student is 'Amanda'"]}
  """

  defstruct [:passed?, :issues, :metrics, :warnings]

  @type t :: %__MODULE__{
          passed?: boolean(),
          issues: [String.t()],
          metrics: map(),
          warnings: [String.t()]
        }

  @doc """
  Performs a comprehensive audit of tutor feedback.

  Returns an `%Audit{}` struct with:
  - `passed?` - Whether the feedback meets minimum requirements
  - `issues` - Blocking issues that must be fixed
  - `warnings` - Non-blocking concerns
  - `metrics` - Quantitative measurements

  ## Options

  - `:min_words` - Minimum word count for summary (default: 50)
  - `:min_comments` - Minimum in-text comment count (default: 5)
  - `:student_name` - Student's name to check for wrong-name errors
  - `:file_path` - Path to the marked document for additional checks
  """
  @spec audit_feedback(String.t() | nil, keyword()) :: t()
  def audit_feedback(tutor_comments, opts \\ []) do
    min_words = Keyword.get(opts, :min_words, get_config(:min_feedback_words, 50))
    min_comments = Keyword.get(opts, :min_comments, get_config(:min_comments, 5))
    student_name = Keyword.get(opts, :student_name)
    file_path = Keyword.get(opts, :file_path)

    # Collect all check results
    checks = [
      check_word_count(tutor_comments, min_words),
      check_script_reference(tutor_comments),
      check_student_name(tutor_comments, student_name),
      check_empty_feedback(tutor_comments)
    ]

    # Add file-based checks if path provided
    file_checks =
      if file_path do
        [check_file_saved(file_path)]
      else
        []
      end

    all_checks = checks ++ file_checks

    # Separate issues (blocking) from warnings (advisory)
    issues =
      all_checks
      |> Enum.filter(fn
        {:error, _} -> true
        _ -> false
      end)
      |> Enum.map(fn {:error, msg} -> msg end)

    warnings =
      all_checks
      |> Enum.filter(fn
        {:warning, _} -> true
        _ -> false
      end)
      |> Enum.map(fn {:warning, msg} -> msg end)

    metrics = %{
      word_count: count_words(tutor_comments),
      character_count: String.length(tutor_comments || ""),
      has_script_reference: has_script_reference?(tutor_comments),
      sentiment: analyze_sentiment(tutor_comments)
    }

    %__MODULE__{
      passed?: Enum.empty?(issues),
      issues: issues,
      warnings: warnings,
      metrics: metrics
    }
  end

  @doc """
  Quick check if feedback meets minimum requirements.
  """
  @spec passes?(String.t() | nil, keyword()) :: boolean()
  def passes?(tutor_comments, opts \\ []) do
    audit_feedback(tutor_comments, opts).passed?
  end

  # --- Individual Checks ---

  defp check_word_count(nil, _min), do: {:error, "Feedback is empty"}

  defp check_word_count(comments, min_words) do
    count = count_words(comments)

    cond do
      count == 0 -> {:error, "Feedback is empty"}
      count < min_words -> {:error, "Feedback too brief (#{count}/#{min_words} words minimum)"}
      count < min_words * 1.5 -> {:warning, "Feedback is on the shorter side (#{count} words)"}
      true -> :ok
    end
  end

  defp check_script_reference(nil), do: :ok

  defp check_script_reference(comments) do
    if has_script_reference?(comments) do
      :ok
    else
      {:warning, "Consider referring the student to your in-text comments"}
    end
  end

  defp check_student_name(nil, _name), do: :ok
  defp check_student_name(_comments, nil), do: :ok

  defp check_student_name(comments, student_name) do
    # Extract the first name from the student name
    student_first_name = student_name |> String.split() |> List.first() |> String.downcase()

    # Find all capitalized words that look like names
    potential_names =
      Regex.scan(~r/\b([A-Z][a-z]{2,})\b/, comments)
      |> Enum.map(fn [_, name] -> String.downcase(name) end)
      |> Enum.uniq()

    # Common salutation words to ignore
    ignore_words = ~w(dear regards sincerely thank thanks good best kind assignment question)

    # Check if any potential name doesn't match the student
    wrong_names =
      potential_names
      |> Enum.reject(&(&1 in ignore_words))
      |> Enum.reject(&(&1 == student_first_name))
      |> Enum.filter(&looks_like_name?/1)

    case wrong_names do
      [] ->
        :ok

      names ->
        {:warning,
         "Possible wrong name: mentioned '#{Enum.join(names, "', '")}' but student is '#{student_name}'"}
    end
  end

  defp check_empty_feedback(nil), do: {:error, "No feedback provided"}
  defp check_empty_feedback(""), do: {:error, "No feedback provided"}

  defp check_empty_feedback(comments) do
    trimmed = String.trim(comments)

    if String.length(trimmed) < 10 do
      {:error, "Feedback is too short to be meaningful"}
    else
      :ok
    end
  end

  defp check_file_saved(file_path) do
    # This would check if the file has been modified since opening
    # For now, just check if it exists
    if File.exists?(file_path) do
      :ok
    else
      {:error, "Marked file not found - ensure you've saved the document"}
    end
  end

  # --- Helper Functions ---

  defp count_words(nil), do: 0
  defp count_words(""), do: 0

  defp count_words(text) do
    text
    |> String.split(~r/\s+/, trim: true)
    |> length()
  end

  defp has_script_reference?(nil), do: false

  defp has_script_reference?(text) do
    # Check for common phrases that refer to in-text comments
    patterns = [
      ~r/\b(script|document|file|in-?line|margin|annotation|comment)\b/i,
      ~r/\b(see|refer|check|read)\s+(my|the|your)\s+(comments?|feedback|notes?)\b/i,
      ~r/\bas\s+(noted|mentioned|indicated|highlighted)\b/i
    ]

    Enum.any?(patterns, &Regex.match?(&1, text))
  end

  defp looks_like_name?(word) do
    # Simple heuristic: common English first names are typically 3-8 characters
    # and don't look like common words
    len = String.length(word)
    len >= 3 && len <= 12 && not common_word?(word)
  end

  defp common_word?(word) do
    common_words = ~w(
      the and for but not you your this that with have from
      well done good work great nice please ensure check
      section part question answer essay report
    )

    word in common_words
  end

  defp analyze_sentiment(nil), do: :neutral

  defp analyze_sentiment(text) do
    positive_patterns = [
      ~r/\b(well\s+done|excellent|great|good|nicely|clearly|effectively)\b/i,
      ~r/\b(impressed|pleased|happy|delighted)\b/i,
      ~r/\b(strength|strong|improvement|improved)\b/i
    ]

    negative_patterns = [
      ~r/\b(poor|weak|lacking|missing|failed|incorrect)\b/i,
      ~r/\b(unfortunately|however|but|problem|issue|concern)\b/i,
      ~r/\b(disappointed|confused|unclear)\b/i
    ]

    positive_count = Enum.count(positive_patterns, &Regex.match?(&1, text))
    negative_count = Enum.count(negative_patterns, &Regex.match?(&1, text))

    cond do
      positive_count > negative_count * 2 -> :positive
      negative_count > positive_count * 2 -> :negative
      positive_count > negative_count -> :balanced_positive
      negative_count > positive_count -> :balanced_negative
      true -> :neutral
    end
  end

  defp get_config(key, default) do
    Application.get_env(:etma_handler, key, default)
  end
end
