# SPDX-License-Identifier: AGPL-3.0-or-later
defmodule EtmaHandler.Marking.StudentAnalytics do
  @moduledoc """
  Student Performance Analytics & Tracking.

  Provides anonymized tracking of student progress with:

  1. **Pseudonymous IDs** - OUCU → stable anonymous identifier
  2. **Performance tracking** - Marks over time, by criterion
  3. **Group comparisons** - Cohort statistics, distributions
  4. **Feedback analysis** - What areas get feedback, trends
  5. **Progress indicators** - Improvement/regression detection

  ## Privacy & Anonymization

  Student data is pseudonymized using one-way hashing:
  - OUCU "rg8274" → "STU-A7B3" (stable, reversible only with key)
  - No PII stored in analytics
  - Can be fully anonymized for external reporting

  ## Usage

      # Track a submission
      StudentAnalytics.record_submission(metadata, rubric_result, feedback_data)

      # Get student progress
      {:ok, progress} = StudentAnalytics.student_progress("rg8274", "M150")

      # Get cohort statistics
      {:ok, stats} = StudentAnalytics.cohort_stats("M150", "25J")

      # Compare criteria performance
      {:ok, analysis} = StudentAnalytics.criteria_analysis("M150-25J-01")
  """

  require Logger

  alias EtmaHandler.{Settings, Vault}

  @type student_id :: String.t()
  @type pseudonym :: String.t()
  @type assignment_key :: String.t()

  @type submission_record :: %{
          student_id: pseudonym(),
          assignment: assignment_key(),
          submitted_at: DateTime.t(),
          marked_at: DateTime.t() | nil,
          overall_mark: integer() | nil,
          grade: String.t() | nil,
          criterion_scores: map(),
          feedback_categories: [String.t()],
          word_count: integer() | nil,
          readability_score: float() | nil
        }

  @type progress_report :: %{
          student_id: pseudonym(),
          course: String.t(),
          submissions: [submission_record()],
          average_mark: float(),
          trend: :improving | :stable | :declining,
          strengths: [String.t()],
          weaknesses: [String.t()]
        }

  # ============================================================
  # PUBLIC API - PSEUDONYMIZATION
  # ============================================================

  @doc """
  Generate a pseudonymous identifier for a student.

  Uses HMAC-SHA256 with a secret key to create a stable,
  non-reversible identifier that can still be used for tracking.
  """
  @spec pseudonymize(String.t()) :: pseudonym()
  def pseudonymize(oucu) when is_binary(oucu) do
    key = get_pseudonym_key()
    hash = :crypto.mac(:hmac, :sha256, key, String.downcase(oucu))

    # Take first 4 bytes, encode as base32-like string
    <<prefix::binary-size(4), _rest::binary>> = hash

    code =
      prefix
      |> Base.encode16(case: :upper)
      |> String.slice(0, 4)

    "STU-#{code}"
  end

  @doc """
  Check if a pseudonym matches an OUCU.
  """
  @spec verify_pseudonym(String.t(), pseudonym()) :: boolean()
  def verify_pseudonym(oucu, pseudonym) do
    pseudonymize(oucu) == pseudonym
  end

  @doc """
  Generate a display label that's human-friendly but anonymized.
  """
  @spec display_label(String.t(), keyword()) :: String.t()
  def display_label(oucu, opts \\ []) do
    include_course = Keyword.get(opts, :course)
    pseudo = pseudonymize(oucu)

    if include_course do
      "#{pseudo} (#{include_course})"
    else
      pseudo
    end
  end

  # ============================================================
  # PUBLIC API - RECORDING
  # ============================================================

  @doc """
  Record a submission for analytics tracking.

  Called when a submission is marked and ready to return.
  """
  @spec record_submission(map(), map(), map()) :: :ok | {:error, term()}
  def record_submission(metadata, rubric_result, opts \\ %{}) do
    student_id =
      pseudonymize(metadata[:student_oucu] || metadata["student_oucu"] || "unknown")

    assignment =
      "#{metadata[:course_code] || metadata["course_code"]}-" <>
        "#{metadata[:presentation] || metadata["presentation"]}-" <>
        "#{metadata[:tma_number] || metadata["tma_number"]}"

    record = %{
      student_id: student_id,
      assignment: assignment,
      course: metadata[:course_code] || metadata["course_code"],
      presentation: metadata[:presentation] || metadata["presentation"],
      tma_number: metadata[:tma_number] || metadata["tma_number"],
      submitted_at: metadata[:detected_at] || DateTime.utc_now(),
      marked_at: DateTime.utc_now(),
      overall_mark: rubric_result[:total] || opts[:overall_mark],
      grade: rubric_result[:grade] || opts[:grade],
      criterion_scores: extract_criterion_scores(rubric_result),
      feedback_categories: opts[:feedback_categories] || [],
      word_count: opts[:word_count],
      readability_score: opts[:readability_score]
    }

    save_submission_record(record)
  end

  @doc """
  Record feedback categories given to a student.
  """
  @spec record_feedback(pseudonym(), assignment_key(), [String.t()]) :: :ok
  def record_feedback(student_id, assignment, categories) do
    # Update existing record with feedback categories
    case get_submission_record(student_id, assignment) do
      {:ok, record} ->
        updated = Map.put(record, :feedback_categories, categories)
        save_submission_record(updated)

      {:error, :not_found} ->
        # Create minimal record
        save_submission_record(%{
          student_id: student_id,
          assignment: assignment,
          feedback_categories: categories,
          recorded_at: DateTime.utc_now()
        })
    end
  end

  # ============================================================
  # PUBLIC API - STUDENT PROGRESS
  # ============================================================

  @doc """
  Get a student's progress report for a course.
  """
  @spec student_progress(String.t(), String.t()) :: {:ok, progress_report()} | {:error, term()}
  def student_progress(oucu, course_code) do
    student_id = pseudonymize(oucu)
    submissions = get_student_submissions(student_id, course_code)

    if submissions == [] do
      {:error, :no_data}
    else
      report = build_progress_report(student_id, course_code, submissions)
      {:ok, report}
    end
  end

  @doc """
  Get a student's mark trajectory (marks over time).
  """
  @spec mark_trajectory(String.t(), String.t()) :: {:ok, [map()]}
  def mark_trajectory(oucu, course_code) do
    student_id = pseudonymize(oucu)
    submissions = get_student_submissions(student_id, course_code)

    trajectory =
      submissions
      |> Enum.filter(&(&1[:overall_mark] != nil))
      |> Enum.sort_by(& &1[:submitted_at])
      |> Enum.map(fn s ->
        %{
          assignment: s.tma_number,
          mark: s.overall_mark,
          grade: s.grade,
          date: s.marked_at || s.submitted_at
        }
      end)

    {:ok, trajectory}
  end

  @doc """
  Identify a student's strengths and weaknesses based on criterion scores.
  """
  @spec identify_patterns(String.t(), String.t()) :: {:ok, map()}
  def identify_patterns(oucu, course_code) do
    student_id = pseudonymize(oucu)
    submissions = get_student_submissions(student_id, course_code)

    # Aggregate scores by criterion
    criterion_totals =
      submissions
      |> Enum.flat_map(fn s ->
        Map.to_list(s[:criterion_scores] || %{})
      end)
      |> Enum.group_by(fn {k, _} -> k end, fn {_, v} -> v end)
      |> Enum.map(fn {criterion, scores} ->
        avg = Enum.sum(scores) / length(scores)
        {criterion, Float.round(avg, 1)}
      end)
      |> Map.new()

    # Identify strengths (avg >= 70) and weaknesses (avg < 55)
    strengths =
      criterion_totals
      |> Enum.filter(fn {_, avg} -> avg >= 70 end)
      |> Enum.sort_by(fn {_, avg} -> avg end, :desc)
      |> Enum.map(fn {c, _} -> c end)

    weaknesses =
      criterion_totals
      |> Enum.filter(fn {_, avg} -> avg < 55 end)
      |> Enum.sort_by(fn {_, avg} -> avg end, :asc)
      |> Enum.map(fn {c, _} -> c end)

    {:ok,
     %{
       criterion_averages: criterion_totals,
       strengths: strengths,
       weaknesses: weaknesses
     }}
  end

  # ============================================================
  # PUBLIC API - COHORT STATISTICS
  # ============================================================

  @doc """
  Get cohort statistics for an assignment.
  """
  @spec cohort_stats(String.t(), String.t(), String.t()) :: {:ok, map()}
  def cohort_stats(course_code, presentation, tma_number) do
    assignment = "#{course_code}-#{presentation}-#{tma_number}"
    submissions = get_assignment_submissions(assignment)

    marks =
      submissions
      |> Enum.map(& &1[:overall_mark])
      |> Enum.filter(&(&1 != nil))

    if marks == [] do
      {:ok, %{count: 0, message: "No marked submissions"}}
    else
      {:ok, calculate_stats(marks)}
    end
  end

  @doc """
  Get mark distribution for a cohort.
  """
  @spec mark_distribution(String.t(), String.t(), String.t()) :: {:ok, map()}
  def mark_distribution(course_code, presentation, tma_number) do
    assignment = "#{course_code}-#{presentation}-#{tma_number}"
    submissions = get_assignment_submissions(assignment)

    marks =
      submissions
      |> Enum.map(& &1[:overall_mark])
      |> Enum.filter(&(&1 != nil))

    distribution =
      marks
      |> Enum.group_by(fn mark ->
        cond do
          mark >= 85 -> "85-100 (Distinction)"
          mark >= 70 -> "70-84 (Pass 1)"
          mark >= 55 -> "55-69 (Pass 2)"
          mark >= 40 -> "40-54 (Pass 3)"
          mark >= 30 -> "30-39 (Pass 4)"
          true -> "0-29 (Fail)"
        end
      end)
      |> Enum.map(fn {band, students} -> {band, length(students)} end)
      |> Map.new()

    {:ok,
     %{
       total_students: length(marks),
       distribution: distribution
     }}
  end

  @doc """
  Compare criterion performance across the cohort.
  """
  @spec criteria_analysis(String.t()) :: {:ok, map()}
  def criteria_analysis(assignment) do
    submissions = get_assignment_submissions(assignment)

    # Aggregate by criterion
    by_criterion =
      submissions
      |> Enum.flat_map(fn s ->
        Map.to_list(s[:criterion_scores] || %{})
      end)
      |> Enum.group_by(fn {k, _} -> k end, fn {_, v} -> v end)
      |> Enum.map(fn {criterion, scores} ->
        stats = calculate_stats(scores)
        {criterion, stats}
      end)
      |> Map.new()

    {:ok,
     %{
       assignment: assignment,
       criteria: by_criterion,
       sample_size: length(submissions)
     }}
  end

  # ============================================================
  # PUBLIC API - FEEDBACK ANALYSIS
  # ============================================================

  @doc """
  Analyze what feedback categories are most common for a student.
  """
  @spec feedback_trends(String.t(), String.t()) :: {:ok, map()}
  def feedback_trends(oucu, course_code) do
    student_id = pseudonymize(oucu)
    submissions = get_student_submissions(student_id, course_code)

    # Count feedback categories
    category_counts =
      submissions
      |> Enum.flat_map(& &1[:feedback_categories] || [])
      |> Enum.frequencies()
      |> Enum.sort_by(fn {_, count} -> count end, :desc)

    {:ok,
     %{
       student_id: student_id,
       total_submissions: length(submissions),
       common_feedback: category_counts,
       most_common: if(category_counts != [], do: hd(category_counts), else: nil)
     }}
  end

  @doc """
  Track how a student's performance in specific areas changes over time.
  """
  @spec criterion_trajectory(String.t(), String.t(), String.t()) :: {:ok, [map()]}
  def criterion_trajectory(oucu, course_code, criterion_id) do
    student_id = pseudonymize(oucu)
    submissions = get_student_submissions(student_id, course_code)

    trajectory =
      submissions
      |> Enum.filter(fn s ->
        Map.has_key?(s[:criterion_scores] || %{}, criterion_id)
      end)
      |> Enum.sort_by(& &1[:submitted_at])
      |> Enum.map(fn s ->
        %{
          assignment: s.tma_number,
          score: get_in(s, [:criterion_scores, criterion_id]),
          date: s.marked_at || s.submitted_at
        }
      end)

    {:ok, trajectory}
  end

  # ============================================================
  # PRIVATE - HELPERS
  # ============================================================

  defp get_pseudonym_key do
    case Settings.get([:analytics, :pseudonym_key]) do
      nil ->
        # Generate and store a key
        key = :crypto.strong_rand_bytes(32)
        Settings.put([:analytics, :pseudonym_key], Base.encode64(key))
        key

      encoded ->
        Base.decode64!(encoded)
    end
  rescue
    _ -> :crypto.strong_rand_bytes(32)
  end

  defp extract_criterion_scores(%{criterion_results: results}) when is_list(results) do
    Map.new(results, fn r -> {r.criterion_id, r.score} end)
  end

  defp extract_criterion_scores(%{"criterion_results" => results}) when is_list(results) do
    Map.new(results, fn r -> {r["criterion_id"], r["score"]} end)
  end

  defp extract_criterion_scores(_), do: %{}

  defp build_progress_report(student_id, course_code, submissions) do
    marks =
      submissions
      |> Enum.map(& &1[:overall_mark])
      |> Enum.filter(&(&1 != nil))

    avg = if marks != [], do: Float.round(Enum.sum(marks) / length(marks), 1), else: 0.0

    # Determine trend
    trend =
      if length(marks) >= 2 do
        recent = Enum.take(marks, -3)
        earlier = Enum.take(marks, 3)
        recent_avg = Enum.sum(recent) / length(recent)
        earlier_avg = Enum.sum(earlier) / length(earlier)

        cond do
          recent_avg > earlier_avg + 5 -> :improving
          recent_avg < earlier_avg - 5 -> :declining
          true -> :stable
        end
      else
        :stable
      end

    # Identify strengths/weaknesses
    {:ok, patterns} = identify_patterns_from_submissions(submissions)

    %{
      student_id: student_id,
      course: course_code,
      submissions: submissions,
      submission_count: length(submissions),
      average_mark: avg,
      trend: trend,
      strengths: patterns.strengths,
      weaknesses: patterns.weaknesses
    }
  end

  defp identify_patterns_from_submissions(submissions) do
    criterion_totals =
      submissions
      |> Enum.flat_map(fn s ->
        Map.to_list(s[:criterion_scores] || %{})
      end)
      |> Enum.group_by(fn {k, _} -> k end, fn {_, v} -> v end)
      |> Enum.map(fn {criterion, scores} ->
        avg = Enum.sum(scores) / length(scores)
        {criterion, Float.round(avg, 1)}
      end)
      |> Map.new()

    strengths =
      criterion_totals
      |> Enum.filter(fn {_, avg} -> avg >= 70 end)
      |> Enum.map(fn {c, _} -> c end)

    weaknesses =
      criterion_totals
      |> Enum.filter(fn {_, avg} -> avg < 55 end)
      |> Enum.map(fn {c, _} -> c end)

    {:ok, %{strengths: strengths, weaknesses: weaknesses}}
  end

  defp calculate_stats(values) when is_list(values) and values != [] do
    sorted = Enum.sort(values)
    count = length(sorted)
    sum = Enum.sum(sorted)
    mean = sum / count

    variance =
      sorted
      |> Enum.map(fn x -> (x - mean) * (x - mean) end)
      |> Enum.sum()
      |> Kernel./(count)

    std_dev = :math.sqrt(variance)

    median =
      if rem(count, 2) == 0 do
        (Enum.at(sorted, div(count, 2) - 1) + Enum.at(sorted, div(count, 2))) / 2
      else
        Enum.at(sorted, div(count, 2))
      end

    %{
      count: count,
      min: hd(sorted),
      max: List.last(sorted),
      mean: Float.round(mean, 1),
      median: Float.round(median * 1.0, 1),
      std_dev: Float.round(std_dev, 1),
      sum: sum
    }
  end

  defp calculate_stats(_), do: %{count: 0}

  # ============================================================
  # PRIVATE - STORAGE
  # ============================================================

  defp get_student_submissions(student_id, course_code) do
    all = load_all_records()

    Enum.filter(all, fn r ->
      r[:student_id] == student_id and r[:course] == course_code
    end)
  end

  defp get_assignment_submissions(assignment) do
    all = load_all_records()
    Enum.filter(all, &(&1[:assignment] == assignment))
  end

  defp get_submission_record(student_id, assignment) do
    all = load_all_records()

    case Enum.find(all, &(&1[:student_id] == student_id and &1[:assignment] == assignment)) do
      nil -> {:error, :not_found}
      record -> {:ok, record}
    end
  end

  defp save_submission_record(record) do
    all = load_all_records()

    # Update or insert
    updated =
      case Enum.find_index(all, fn r ->
             r[:student_id] == record.student_id and r[:assignment] == record.assignment
           end) do
        nil -> [record | all]
        idx -> List.replace_at(all, idx, record)
      end

    Settings.put([:analytics, :submissions], updated)
  end

  defp load_all_records do
    case Settings.get([:analytics, :submissions]) do
      nil -> []
      records when is_list(records) -> records
      _ -> []
    end
  rescue
    _ -> []
  end
end
