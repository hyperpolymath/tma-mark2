# SPDX-License-Identifier: AGPL-3.0-or-later
defmodule EtmaHandler.Marking.Rubric do
  @moduledoc """
  Marking Rubric Management.

  Provides structured assessment criteria alongside free-form feedback.
  Rubrics define marking bands, criteria, and weightings for consistent grading.

  ## Rubric Structure

      %Rubric{
        id: "M150-TMA01",
        name: "TMA01 Assessment Criteria",
        criteria: [
          %Criterion{
            id: "understanding",
            name: "Understanding of Concepts",
            weight: 30,
            bands: [
              %Band{min: 85, max: 100, label: "Excellent", descriptor: "..."},
              %Band{min: 70, max: 84, label: "Good", descriptor: "..."},
              ...
            ]
          },
          ...
        ],
        total_marks: 100
      }

  ## Usage

      # Create or load a rubric
      {:ok, rubric} = Rubric.get("M150-25J-01")

      # Score against rubric
      scores = %{
        "understanding" => 78,
        "application" => 82,
        "presentation" => 85
      }
      {:ok, result} = Rubric.calculate(rubric, scores)
      # => %{total: 81, grade: "Pass 2", feedback: [...]}

      # Get band feedback for a score
      {:ok, feedback} = Rubric.band_feedback(rubric, "understanding", 78)
      # => "Good understanding demonstrated..."
  """

  require Logger

  alias EtmaHandler.Settings

  # ============================================================
  # TYPES
  # ============================================================

  defmodule Band do
    @moduledoc "A marking band within a criterion."
    defstruct [:min, :max, :label, :descriptor, :feedback_template]

    @type t :: %__MODULE__{
            min: integer(),
            max: integer(),
            label: String.t(),
            descriptor: String.t(),
            feedback_template: String.t() | nil
          }
  end

  defmodule Criterion do
    @moduledoc "A single assessment criterion with marking bands."
    defstruct [:id, :name, :weight, :bands, :description]

    @type t :: %__MODULE__{
            id: String.t(),
            name: String.t(),
            weight: integer(),
            bands: [EtmaHandler.Marking.Rubric.Band.t()],
            description: String.t() | nil
          }
  end

  defstruct [:id, :name, :description, :criteria, :total_marks, :grade_boundaries, :created_at, :updated_at]

  @type t :: %__MODULE__{
          id: String.t(),
          name: String.t(),
          description: String.t() | nil,
          criteria: [Criterion.t()],
          total_marks: integer(),
          grade_boundaries: map() | nil,
          created_at: DateTime.t(),
          updated_at: DateTime.t()
        }

  # Default OU grade boundaries
  @default_grade_boundaries %{
    "Distinction" => 85,
    "Pass 1" => 70,
    "Pass 2" => 55,
    "Pass 3" => 40,
    "Pass 4" => 30,
    "Fail" => 0
  }

  # Standard marking bands - defined as function to avoid compilation order issues
  defp standard_bands do
    [
      %__MODULE__.Band{min: 85, max: 100, label: "Excellent", descriptor: "Outstanding work demonstrating exceptional understanding and application"},
      %__MODULE__.Band{min: 70, max: 84, label: "Good", descriptor: "Good work showing clear understanding with minor areas for improvement"},
      %__MODULE__.Band{min: 55, max: 69, label: "Satisfactory", descriptor: "Satisfactory work meeting basic requirements with scope for development"},
      %__MODULE__.Band{min: 40, max: 54, label: "Adequate", descriptor: "Adequate work that meets minimum requirements but needs significant improvement"},
      %__MODULE__.Band{min: 30, max: 39, label: "Marginal", descriptor: "Marginal work that falls short of requirements in several areas"},
      %__MODULE__.Band{min: 0, max: 29, label: "Unsatisfactory", descriptor: "Work that does not meet the requirements of the assignment"}
    ]
  end

  # ============================================================
  # PUBLIC API - CRUD
  # ============================================================

  @doc """
  Get a rubric by ID (assignment key).
  """
  @spec get(String.t()) :: {:ok, t()} | {:error, :not_found}
  def get(rubric_id) do
    case load_rubric(rubric_id) do
      nil -> {:error, :not_found}
      rubric -> {:ok, rubric}
    end
  end

  @doc """
  Get or create a rubric with defaults.
  """
  @spec get_or_create(String.t(), keyword()) :: {:ok, t()}
  def get_or_create(rubric_id, opts \\ []) do
    case get(rubric_id) do
      {:ok, rubric} -> {:ok, rubric}
      {:error, :not_found} -> create(rubric_id, opts)
    end
  end

  @doc """
  Create a new rubric.

  ## Options

  - `:name` - Display name
  - `:description` - Rubric description
  - `:template` - Use a predefined template (:essay, :report, :computing, :presentation)
  - `:criteria` - List of criterion maps
  - `:total_marks` - Total marks available (default: 100)
  """
  @spec create(String.t(), keyword()) :: {:ok, t()} | {:error, term()}
  def create(rubric_id, opts \\ []) do
    template = Keyword.get(opts, :template)
    criteria = Keyword.get(opts, :criteria) || template_criteria(template)

    rubric = %__MODULE__{
      id: rubric_id,
      name: Keyword.get(opts, :name, rubric_id),
      description: Keyword.get(opts, :description),
      criteria: Enum.map(criteria, &normalize_criterion/1),
      total_marks: Keyword.get(opts, :total_marks, 100),
      grade_boundaries: Keyword.get(opts, :grade_boundaries, @default_grade_boundaries),
      created_at: DateTime.utc_now(),
      updated_at: DateTime.utc_now()
    }

    case save_rubric(rubric) do
      :ok -> {:ok, rubric}
      error -> error
    end
  end

  @doc """
  Update an existing rubric.
  """
  @spec update(t(), map()) :: {:ok, t()} | {:error, term()}
  def update(rubric, changes) do
    updated =
      rubric
      |> Map.merge(changes)
      |> Map.put(:updated_at, DateTime.utc_now())

    case save_rubric(updated) do
      :ok -> {:ok, updated}
      error -> error
    end
  end

  @doc """
  Delete a rubric.
  """
  @spec delete(String.t()) :: :ok
  def delete(rubric_id) do
    delete_rubric(rubric_id)
  end

  @doc """
  List all rubrics.
  """
  @spec list() :: [t()]
  def list do
    load_all_rubrics()
  end

  @doc """
  List rubrics for a specific course.
  """
  @spec list_for_course(String.t()) :: [t()]
  def list_for_course(course_code) do
    list()
    |> Enum.filter(&String.starts_with?(&1.id, course_code))
  end

  # ============================================================
  # PUBLIC API - SCORING
  # ============================================================

  @doc """
  Calculate the total score and grade from criterion scores.

  ## Parameters

  - `rubric` - The rubric to score against
  - `scores` - Map of criterion_id => score (0-100)

  ## Returns

      {:ok, %{
        total: 78,
        weighted_total: 78.5,
        grade: "Pass 2",
        criterion_results: [...],
        feedback: [...]
      }}
  """
  @spec calculate(t(), map()) :: {:ok, map()} | {:error, term()}
  def calculate(rubric, scores) when is_map(scores) do
    # Validate all criteria have scores
    criterion_ids = Enum.map(rubric.criteria, & &1.id) |> MapSet.new()
    score_ids = Map.keys(scores) |> MapSet.new()

    missing = MapSet.difference(criterion_ids, score_ids) |> MapSet.to_list()

    if missing != [] do
      {:error, {:missing_scores, missing}}
    else
      do_calculate(rubric, scores)
    end
  end

  defp do_calculate(rubric, scores) do
    # Calculate weighted scores
    results =
      Enum.map(rubric.criteria, fn criterion ->
        score = Map.get(scores, criterion.id, 0)
        band = find_band(criterion.bands, score)
        weighted = score * (criterion.weight / 100)

        %{
          criterion_id: criterion.id,
          criterion_name: criterion.name,
          score: score,
          weight: criterion.weight,
          weighted_score: Float.round(weighted, 2),
          band: band.label,
          feedback: band.descriptor
        }
      end)

    weighted_total =
      results
      |> Enum.map(& &1.weighted_score)
      |> Enum.sum()
      |> Float.round(1)

    # Determine grade
    grade = determine_grade(weighted_total, rubric.grade_boundaries)

    # Generate feedback summary
    feedback =
      results
      |> Enum.map(fn r -> "#{r.criterion_name}: #{r.band} (#{r.score}%)" end)

    {:ok,
     %{
       total: round(weighted_total),
       weighted_total: weighted_total,
       grade: grade,
       criterion_results: results,
       feedback: feedback
     }}
  end

  @doc """
  Get the band feedback for a specific criterion and score.
  """
  @spec band_feedback(t(), String.t(), integer()) :: {:ok, map()} | {:error, :not_found}
  def band_feedback(rubric, criterion_id, score) do
    case Enum.find(rubric.criteria, &(&1.id == criterion_id)) do
      nil ->
        {:error, :not_found}

      criterion ->
        band = find_band(criterion.bands, score)

        {:ok,
         %{
           band: band.label,
           descriptor: band.descriptor,
           template: band.feedback_template,
           criterion: criterion.name
         }}
    end
  end

  @doc """
  Validate scores against a rubric (without calculating).
  """
  @spec validate_scores(t(), map()) :: :ok | {:error, [map()]}
  def validate_scores(rubric, scores) do
    issues =
      Enum.flat_map(rubric.criteria, fn criterion ->
        score = Map.get(scores, criterion.id)

        cond do
          score == nil ->
            [%{criterion: criterion.id, issue: "Missing score"}]

          not is_number(score) ->
            [%{criterion: criterion.id, issue: "Score must be a number"}]

          score < 0 or score > 100 ->
            [%{criterion: criterion.id, issue: "Score must be between 0 and 100"}]

          true ->
            []
        end
      end)

    if issues == [] do
      :ok
    else
      {:error, issues}
    end
  end

  # ============================================================
  # PUBLIC API - TEMPLATES
  # ============================================================

  @doc """
  Get available rubric templates.
  """
  @spec templates() :: [atom()]
  def templates do
    [:essay, :report, :computing, :presentation, :portfolio, :reflective]
  end

  @doc """
  Get criteria for a specific template.
  """
  @spec template_criteria(atom() | nil) :: [map()]
  def template_criteria(:essay) do
    [
      %{
        id: "understanding",
        name: "Understanding of Concepts",
        weight: 25,
        bands: standard_bands(),
        description: "Demonstrates understanding of key concepts and theories"
      },
      %{
        id: "argument",
        name: "Argument & Analysis",
        weight: 30,
        bands: standard_bands(),
        description: "Quality of argument construction and critical analysis"
      },
      %{
        id: "evidence",
        name: "Use of Evidence",
        weight: 20,
        bands: standard_bands(),
        description: "Appropriate use of sources and evidence to support arguments"
      },
      %{
        id: "structure",
        name: "Structure & Organisation",
        weight: 15,
        bands: standard_bands(),
        description: "Logical flow and clear organisation of ideas"
      },
      %{
        id: "presentation",
        name: "Academic Writing & Presentation",
        weight: 10,
        bands: standard_bands(),
        description: "Grammar, spelling, referencing, and academic style"
      }
    ]
  end

  def template_criteria(:report) do
    [
      %{
        id: "methodology",
        name: "Methodology & Approach",
        weight: 25,
        bands: standard_bands(),
        description: "Appropriateness and rigour of methodology"
      },
      %{
        id: "analysis",
        name: "Data Analysis",
        weight: 30,
        bands: standard_bands(),
        description: "Quality and depth of data analysis"
      },
      %{
        id: "findings",
        name: "Findings & Recommendations",
        weight: 25,
        bands: standard_bands(),
        description: "Clarity and validity of findings and recommendations"
      },
      %{
        id: "presentation",
        name: "Report Structure & Presentation",
        weight: 20,
        bands: standard_bands(),
        description: "Professional presentation and appropriate report format"
      }
    ]
  end

  def template_criteria(:computing) do
    [
      %{
        id: "functionality",
        name: "Functionality & Correctness",
        weight: 35,
        bands: standard_bands(),
        description: "Code works correctly and meets requirements"
      },
      %{
        id: "design",
        name: "Design & Architecture",
        weight: 25,
        bands: standard_bands(),
        description: "Appropriate design patterns and code structure"
      },
      %{
        id: "quality",
        name: "Code Quality",
        weight: 20,
        bands: standard_bands(),
        description: "Readability, documentation, and maintainability"
      },
      %{
        id: "testing",
        name: "Testing & Validation",
        weight: 10,
        bands: standard_bands(),
        description: "Evidence of testing and error handling"
      },
      %{
        id: "reflection",
        name: "Reflection & Explanation",
        weight: 10,
        bands: standard_bands(),
        description: "Quality of written explanation and reflection"
      }
    ]
  end

  def template_criteria(:presentation) do
    [
      %{
        id: "content",
        name: "Content & Knowledge",
        weight: 40,
        bands: standard_bands(),
        description: "Accuracy and depth of content"
      },
      %{
        id: "design",
        name: "Visual Design",
        weight: 25,
        bands: standard_bands(),
        description: "Clarity and professionalism of slides"
      },
      %{
        id: "structure",
        name: "Structure & Flow",
        weight: 20,
        bands: standard_bands(),
        description: "Logical progression and clear narrative"
      },
      %{
        id: "engagement",
        name: "Audience Engagement",
        weight: 15,
        bands: standard_bands(),
        description: "Use of examples, questions, and interactive elements"
      }
    ]
  end

  def template_criteria(:reflective) do
    [
      %{
        id: "description",
        name: "Description of Experience",
        weight: 15,
        bands: standard_bands(),
        description: "Clear and relevant description of the experience"
      },
      %{
        id: "reflection",
        name: "Depth of Reflection",
        weight: 35,
        bands: standard_bands(),
        description: "Quality of critical reflection and self-awareness"
      },
      %{
        id: "theory",
        name: "Link to Theory",
        weight: 25,
        bands: standard_bands(),
        description: "Connection between experience and theoretical concepts"
      },
      %{
        id: "action",
        name: "Action Planning",
        weight: 15,
        bands: standard_bands(),
        description: "Clear and realistic plans for future development"
      },
      %{
        id: "presentation",
        name: "Writing & Presentation",
        weight: 10,
        bands: standard_bands(),
        description: "Clarity of expression and appropriate format"
      }
    ]
  end

  def template_criteria(_), do: template_criteria(:essay)

  # ============================================================
  # PRIVATE - HELPERS
  # ============================================================

  defp find_band(bands, score) do
    Enum.find(bands, List.last(bands), fn band ->
      score >= band.min and score <= band.max
    end)
  end

  defp determine_grade(score, boundaries) do
    boundaries
    |> Enum.sort_by(fn {_, min} -> min end, :desc)
    |> Enum.find(fn {_, min} -> score >= min end)
    |> case do
      {grade, _} -> grade
      nil -> "Ungraded"
    end
  end

  defp normalize_criterion(criterion) when is_map(criterion) do
    bands =
      case Map.get(criterion, :bands) || Map.get(criterion, "bands") do
        nil -> standard_bands()
        bands -> Enum.map(bands, &normalize_band/1)
      end

    %Criterion{
      id: Map.get(criterion, :id) || Map.get(criterion, "id"),
      name: Map.get(criterion, :name) || Map.get(criterion, "name"),
      weight: Map.get(criterion, :weight) || Map.get(criterion, "weight") || 25,
      bands: bands,
      description: Map.get(criterion, :description) || Map.get(criterion, "description")
    }
  end

  defp normalize_band(band) when is_map(band) do
    %Band{
      min: Map.get(band, :min) || Map.get(band, "min") || 0,
      max: Map.get(band, :max) || Map.get(band, "max") || 100,
      label: Map.get(band, :label) || Map.get(band, "label") || "Ungraded",
      descriptor: Map.get(band, :descriptor) || Map.get(band, "descriptor") || "",
      feedback_template: Map.get(band, :feedback_template) || Map.get(band, "feedback_template")
    }
  end

  defp normalize_band(%Band{} = band), do: band

  # ============================================================
  # PRIVATE - STORAGE
  # ============================================================

  defp load_rubric(id) do
    case Settings.get([:rubrics, id]) do
      nil -> nil
      data -> deserialize_rubric(data)
    end
  rescue
    _ -> nil
  end

  defp load_all_rubrics do
    case Settings.get([:rubrics]) do
      nil -> []
      rubrics when is_map(rubrics) ->
        rubrics
        |> Map.values()
        |> Enum.map(&deserialize_rubric/1)
        |> Enum.filter(&(&1 != nil))
      _ -> []
    end
  rescue
    _ -> []
  end

  defp save_rubric(rubric) do
    data = serialize_rubric(rubric)
    Settings.put([:rubrics, rubric.id], data)
  end

  defp delete_rubric(id) do
    all_rubrics = Settings.get([:rubrics]) || %{}
    updated = Map.delete(all_rubrics, id)
    Settings.put([:rubrics], updated)
  end

  defp serialize_rubric(rubric) do
    %{
      id: rubric.id,
      name: rubric.name,
      description: rubric.description,
      criteria: Enum.map(rubric.criteria, &serialize_criterion/1),
      total_marks: rubric.total_marks,
      grade_boundaries: rubric.grade_boundaries,
      created_at: DateTime.to_iso8601(rubric.created_at),
      updated_at: DateTime.to_iso8601(rubric.updated_at)
    }
  end

  defp serialize_criterion(criterion) do
    %{
      id: criterion.id,
      name: criterion.name,
      weight: criterion.weight,
      description: criterion.description,
      bands: Enum.map(criterion.bands, &serialize_band/1)
    }
  end

  defp serialize_band(band) do
    %{
      min: band.min,
      max: band.max,
      label: band.label,
      descriptor: band.descriptor,
      feedback_template: band.feedback_template
    }
  end

  defp deserialize_rubric(data) when is_map(data) do
    %__MODULE__{
      id: data["id"] || data[:id],
      name: data["name"] || data[:name],
      description: data["description"] || data[:description],
      criteria: Enum.map(data["criteria"] || data[:criteria] || [], &normalize_criterion/1),
      total_marks: data["total_marks"] || data[:total_marks] || 100,
      grade_boundaries: data["grade_boundaries"] || data[:grade_boundaries] || @default_grade_boundaries,
      created_at: parse_datetime(data["created_at"] || data[:created_at]),
      updated_at: parse_datetime(data["updated_at"] || data[:updated_at])
    }
  end

  defp deserialize_rubric(_), do: nil

  defp parse_datetime(nil), do: DateTime.utc_now()
  defp parse_datetime(%DateTime{} = dt), do: dt

  defp parse_datetime(str) when is_binary(str) do
    case DateTime.from_iso8601(str) do
      {:ok, dt, _} -> dt
      _ -> DateTime.utc_now()
    end
  end
end
