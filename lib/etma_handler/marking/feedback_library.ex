# SPDX-License-Identifier: AGPL-3.0-or-later
defmodule EtmaHandler.Marking.FeedbackLibrary do
  @moduledoc """
  Reusable Feedback Comments and Supporting Materials Library.

  Provides:

  1. **Comment Templates** - Reusable feedback snippets organized by category
  2. **Supporting Materials** - Diagrams, handouts, model answers
  3. **Quick Insert** - Fast access to common feedback phrases
  4. **Personalization** - Templates with variable substitution

  ## Comment Categories

  - `:praise` - Positive feedback for good work
  - `:improvement` - Suggestions for improvement
  - `:correction` - Specific errors to address
  - `:guidance` - Directions to resources/further reading
  - `:structure` - Comments on organization and presentation
  - `:referencing` - Citation and reference feedback
  - `:critical_thinking` - Analysis and argumentation feedback

  ## Usage

      # Get all comments for a category
      {:ok, comments} = FeedbackLibrary.list_comments(:praise)

      # Search comments
      {:ok, results} = FeedbackLibrary.search_comments("Harvard")

      # Add a new comment template
      {:ok, comment} = FeedbackLibrary.add_comment(:improvement, %{
        title: "Expand Analysis",
        text: "This point could be developed further. Consider...",
        tags: ["analysis", "depth"]
      })

      # Attach supporting material
      {:ok, material} = FeedbackLibrary.add_material(%{
        name: "Harvard Guide",
        type: :handout,
        file_path: "/path/to/harvard-guide.pdf",
        description: "Quick reference for Harvard citations"
      })
  """

  require Logger

  alias EtmaHandler.Settings

  @comment_categories [
    :praise,
    :improvement,
    :correction,
    :guidance,
    :structure,
    :referencing,
    :critical_thinking,
    :general
  ]

  @material_types [:handout, :diagram, :model_answer, :rubric, :guide, :template, :other]

  @type comment :: %{
          id: String.t(),
          category: atom(),
          title: String.t(),
          text: String.t(),
          tags: [String.t()],
          variables: [String.t()],
          usage_count: integer(),
          created_at: DateTime.t(),
          updated_at: DateTime.t()
        }

  @type material :: %{
          id: String.t(),
          name: String.t(),
          type: atom(),
          file_path: String.t() | nil,
          content: String.t() | nil,
          description: String.t(),
          tags: [String.t()],
          course_codes: [String.t()],
          usage_count: integer(),
          created_at: DateTime.t(),
          updated_at: DateTime.t()
        }

  # ============================================================
  # PUBLIC API - COMMENTS
  # ============================================================

  @doc """
  List all comment categories.
  """
  @spec categories() :: [atom()]
  def categories, do: @comment_categories

  @doc """
  List all comments, optionally filtered by category.
  """
  @spec list_comments(atom() | nil) :: {:ok, [comment()]}
  def list_comments(category \\ nil) do
    comments = Settings.get(:feedback_comments, default_comments())

    filtered =
      if category do
        Enum.filter(comments, &(&1.category == category))
      else
        comments
      end

    {:ok, Enum.sort_by(filtered, & &1.usage_count, :desc)}
  end

  @doc """
  Search comments by text content or tags.
  """
  @spec search_comments(String.t(), keyword()) :: {:ok, [comment()]}
  def search_comments(query, opts \\ []) do
    {:ok, comments} = list_comments(Keyword.get(opts, :category))

    query_lower = String.downcase(query)

    results =
      Enum.filter(comments, fn c ->
        String.contains?(String.downcase(c.title), query_lower) ||
          String.contains?(String.downcase(c.text), query_lower) ||
          Enum.any?(c.tags, &String.contains?(String.downcase(&1), query_lower))
      end)

    {:ok, results}
  end

  @doc """
  Get a single comment by ID.
  """
  @spec get_comment(String.t()) :: {:ok, comment()} | {:error, :not_found}
  def get_comment(id) do
    comments = Settings.get(:feedback_comments, default_comments())

    case Enum.find(comments, &(&1.id == id)) do
      nil -> {:error, :not_found}
      comment -> {:ok, comment}
    end
  end

  @doc """
  Add a new comment template.
  """
  @spec add_comment(atom(), map()) :: {:ok, comment()} | {:error, term()}
  def add_comment(category, attrs) when category in @comment_categories do
    comments = Settings.get(:feedback_comments, default_comments())

    comment = %{
      id: generate_id(),
      category: category,
      title: Map.get(attrs, :title, "Untitled"),
      text: Map.get(attrs, :text, ""),
      tags: Map.get(attrs, :tags, []),
      variables: extract_variables(Map.get(attrs, :text, "")),
      usage_count: 0,
      created_at: DateTime.utc_now(),
      updated_at: DateTime.utc_now()
    }

    Settings.put(:feedback_comments, [comment | comments])
    {:ok, comment}
  end

  def add_comment(category, _attrs) do
    {:error, {:invalid_category, category, @comment_categories}}
  end

  @doc """
  Update an existing comment.
  """
  @spec update_comment(String.t(), map()) :: {:ok, comment()} | {:error, term()}
  def update_comment(id, attrs) do
    comments = Settings.get(:feedback_comments, default_comments())

    case Enum.find_index(comments, &(&1.id == id)) do
      nil ->
        {:error, :not_found}

      index ->
        old_comment = Enum.at(comments, index)

        updated =
          old_comment
          |> Map.merge(Map.take(attrs, [:title, :text, :tags, :category]))
          |> Map.put(:variables, extract_variables(Map.get(attrs, :text, old_comment.text)))
          |> Map.put(:updated_at, DateTime.utc_now())

        new_comments = List.replace_at(comments, index, updated)
        Settings.put(:feedback_comments, new_comments)
        {:ok, updated}
    end
  end

  @doc """
  Delete a comment by ID.
  """
  @spec delete_comment(String.t()) :: :ok | {:error, :not_found}
  def delete_comment(id) do
    comments = Settings.get(:feedback_comments, default_comments())
    new_comments = Enum.reject(comments, &(&1.id == id))

    if length(new_comments) == length(comments) do
      {:error, :not_found}
    else
      Settings.put(:feedback_comments, new_comments)
      :ok
    end
  end

  @doc """
  Record usage of a comment (increments counter for sorting by popularity).
  """
  @spec record_usage(String.t()) :: :ok
  def record_usage(id) do
    comments = Settings.get(:feedback_comments, default_comments())

    case Enum.find_index(comments, &(&1.id == id)) do
      nil ->
        :ok

      index ->
        comment = Enum.at(comments, index)
        updated = %{comment | usage_count: comment.usage_count + 1}
        new_comments = List.replace_at(comments, index, updated)
        Settings.put(:feedback_comments, new_comments)
        :ok
    end
  end

  @doc """
  Apply variable substitutions to a comment text.

  ## Example

      text = "Good work, {student_name}! Your analysis of {topic} was excellent."
      vars = %{"student_name" => "Alice", "topic" => "climate change"}
      {:ok, "Good work, Alice! Your analysis of climate change was excellent."}
  """
  @spec apply_variables(String.t(), map()) :: {:ok, String.t()}
  def apply_variables(text, variables) do
    result =
      Enum.reduce(variables, text, fn {key, value}, acc ->
        String.replace(acc, "{#{key}}", to_string(value))
      end)

    {:ok, result}
  end

  # ============================================================
  # PUBLIC API - SUPPORTING MATERIALS
  # ============================================================

  @doc """
  List all material types.
  """
  @spec material_types() :: [atom()]
  def material_types, do: @material_types

  @doc """
  List all supporting materials, optionally filtered by type or course.
  """
  @spec list_materials(keyword()) :: {:ok, [material()]}
  def list_materials(opts \\ []) do
    materials = Settings.get(:supporting_materials, [])

    filtered =
      materials
      |> maybe_filter_by_type(Keyword.get(opts, :type))
      |> maybe_filter_by_course(Keyword.get(opts, :course_code))
      |> maybe_filter_by_tags(Keyword.get(opts, :tags))

    {:ok, Enum.sort_by(filtered, & &1.usage_count, :desc)}
  end

  @doc """
  Search materials by name or description.
  """
  @spec search_materials(String.t()) :: {:ok, [material()]}
  def search_materials(query) do
    {:ok, materials} = list_materials()
    query_lower = String.downcase(query)

    results =
      Enum.filter(materials, fn m ->
        String.contains?(String.downcase(m.name), query_lower) ||
          String.contains?(String.downcase(m.description || ""), query_lower) ||
          Enum.any?(m.tags, &String.contains?(String.downcase(&1), query_lower))
      end)

    {:ok, results}
  end

  @doc """
  Get a single material by ID.
  """
  @spec get_material(String.t()) :: {:ok, material()} | {:error, :not_found}
  def get_material(id) do
    materials = Settings.get(:supporting_materials, [])

    case Enum.find(materials, &(&1.id == id)) do
      nil -> {:error, :not_found}
      material -> {:ok, material}
    end
  end

  @doc """
  Add a new supporting material.

  Can provide either:
  - `:file_path` - Path to an existing file
  - `:content` - Text content to store directly
  """
  @spec add_material(map()) :: {:ok, material()} | {:error, term()}
  def add_material(attrs) do
    type = Map.get(attrs, :type, :other)

    if type not in @material_types do
      {:error, {:invalid_type, type, @material_types}}
    else
      materials = Settings.get(:supporting_materials, [])

      # If file_path provided, verify it exists
      file_path = Map.get(attrs, :file_path)

      if file_path && !File.exists?(file_path) do
        {:error, {:file_not_found, file_path}}
      else
        material = %{
          id: generate_id(),
          name: Map.get(attrs, :name, "Untitled"),
          type: type,
          file_path: file_path,
          content: Map.get(attrs, :content),
          description: Map.get(attrs, :description, ""),
          tags: Map.get(attrs, :tags, []),
          course_codes: Map.get(attrs, :course_codes, []),
          usage_count: 0,
          created_at: DateTime.utc_now(),
          updated_at: DateTime.utc_now()
        }

        Settings.put(:supporting_materials, [material | materials])
        {:ok, material}
      end
    end
  end

  @doc """
  Update an existing material.
  """
  @spec update_material(String.t(), map()) :: {:ok, material()} | {:error, term()}
  def update_material(id, attrs) do
    materials = Settings.get(:supporting_materials, [])

    case Enum.find_index(materials, &(&1.id == id)) do
      nil ->
        {:error, :not_found}

      index ->
        old_material = Enum.at(materials, index)

        updated =
          old_material
          |> Map.merge(
            Map.take(attrs, [:name, :type, :file_path, :content, :description, :tags, :course_codes])
          )
          |> Map.put(:updated_at, DateTime.utc_now())

        new_materials = List.replace_at(materials, index, updated)
        Settings.put(:supporting_materials, new_materials)
        {:ok, updated}
    end
  end

  @doc """
  Delete a material by ID.
  """
  @spec delete_material(String.t()) :: :ok | {:error, :not_found}
  def delete_material(id) do
    materials = Settings.get(:supporting_materials, [])
    new_materials = Enum.reject(materials, &(&1.id == id))

    if length(new_materials) == length(materials) do
      {:error, :not_found}
    else
      Settings.put(:supporting_materials, new_materials)
      :ok
    end
  end

  @doc """
  Record usage of a material.
  """
  @spec record_material_usage(String.t()) :: :ok
  def record_material_usage(id) do
    materials = Settings.get(:supporting_materials, [])

    case Enum.find_index(materials, &(&1.id == id)) do
      nil ->
        :ok

      index ->
        material = Enum.at(materials, index)
        updated = %{material | usage_count: material.usage_count + 1}
        new_materials = List.replace_at(materials, index, updated)
        Settings.put(:supporting_materials, new_materials)
        :ok
    end
  end

  @doc """
  Get the content of a material (reads file if file_path, returns content if stored).
  """
  @spec get_material_content(String.t()) :: {:ok, binary()} | {:error, term()}
  def get_material_content(id) do
    case get_material(id) do
      {:ok, material} ->
        cond do
          material.content ->
            {:ok, material.content}

          material.file_path && File.exists?(material.file_path) ->
            File.read(material.file_path)

          material.file_path ->
            {:error, {:file_not_found, material.file_path}}

          true ->
            {:error, :no_content}
        end

      error ->
        error
    end
  end

  # ============================================================
  # PUBLIC API - QUICK INSERT
  # ============================================================

  @doc """
  Get frequently used comments for quick insertion (top N by usage).
  """
  @spec quick_comments(integer()) :: {:ok, [comment()]}
  def quick_comments(limit \\ 10) do
    {:ok, comments} = list_comments()
    {:ok, Enum.take(comments, limit)}
  end

  @doc """
  Get comments for a specific context (e.g., "low mark", "high mark", "referencing issues").
  """
  @spec contextual_comments(atom(), keyword()) :: {:ok, [comment()]}
  def contextual_comments(context, opts \\ [])

  def contextual_comments(:low_mark, _opts) do
    # Get improvement and guidance comments
    {:ok, improvement} = list_comments(:improvement)
    {:ok, guidance} = list_comments(:guidance)
    {:ok, Enum.take(improvement ++ guidance, 10)}
  end

  def contextual_comments(:high_mark, _opts) do
    # Get praise comments
    list_comments(:praise)
  end

  def contextual_comments(:referencing_issues, _opts) do
    list_comments(:referencing)
  end

  def contextual_comments(:structure_issues, _opts) do
    list_comments(:structure)
  end

  def contextual_comments(:analysis_weak, _opts) do
    list_comments(:critical_thinking)
  end

  def contextual_comments(_, _opts) do
    list_comments(:general)
  end

  # ============================================================
  # PUBLIC API - IMPORT/EXPORT
  # ============================================================

  @doc """
  Export all comments and materials to a portable format.
  """
  @spec export() :: {:ok, map()}
  def export do
    {:ok,
     %{
       version: 1,
       exported_at: DateTime.utc_now(),
       comments: Settings.get(:feedback_comments, default_comments()),
       materials:
         Settings.get(:supporting_materials, [])
         |> Enum.map(&Map.drop(&1, [:file_path]))
     }}
  end

  @doc """
  Import comments and materials from exported data.

  Options:
  - `:merge` - Merge with existing (default: true)
  - `:overwrite` - Replace existing with same ID
  """
  @spec import(map(), keyword()) :: {:ok, map()} | {:error, term()}
  def import(data, opts \\ []) do
    merge = Keyword.get(opts, :merge, true)
    overwrite = Keyword.get(opts, :overwrite, false)

    imported_comments = Map.get(data, "comments", Map.get(data, :comments, []))
    imported_materials = Map.get(data, "materials", Map.get(data, :materials, []))

    # Convert string keys to atoms for comments
    imported_comments =
      Enum.map(imported_comments, fn c ->
        c
        |> atomize_keys()
        |> Map.update(:category, :general, &to_category/1)
        |> Map.put_new(:id, generate_id())
      end)

    imported_materials =
      Enum.map(imported_materials, fn m ->
        m
        |> atomize_keys()
        |> Map.update(:type, :other, &to_material_type/1)
        |> Map.put_new(:id, generate_id())
      end)

    # Merge or replace
    existing_comments = Settings.get(:feedback_comments, default_comments())
    existing_materials = Settings.get(:supporting_materials, [])

    new_comments =
      if merge do
        merge_by_id(existing_comments, imported_comments, overwrite)
      else
        imported_comments
      end

    new_materials =
      if merge do
        merge_by_id(existing_materials, imported_materials, overwrite)
      else
        imported_materials
      end

    Settings.put(:feedback_comments, new_comments)
    Settings.put(:supporting_materials, new_materials)

    {:ok,
     %{
       comments_imported: length(imported_comments),
       materials_imported: length(imported_materials),
       total_comments: length(new_comments),
       total_materials: length(new_materials)
     }}
  end

  # ============================================================
  # PRIVATE - DEFAULT COMMENTS
  # ============================================================

  defp default_comments do
    [
      # Praise
      %{
        id: "default-praise-1",
        category: :praise,
        title: "Excellent Understanding",
        text: "You demonstrate excellent understanding of {topic}. Your explanation is clear and accurate.",
        tags: ["understanding", "clarity"],
        variables: ["topic"],
        usage_count: 0,
        created_at: ~U[2025-01-01 00:00:00Z],
        updated_at: ~U[2025-01-01 00:00:00Z]
      },
      %{
        id: "default-praise-2",
        category: :praise,
        title: "Well-Structured Response",
        text: "Your answer is well-structured with a clear introduction, development, and conclusion.",
        tags: ["structure", "organization"],
        variables: [],
        usage_count: 0,
        created_at: ~U[2025-01-01 00:00:00Z],
        updated_at: ~U[2025-01-01 00:00:00Z]
      },
      %{
        id: "default-praise-3",
        category: :praise,
        title: "Good Use of Evidence",
        text: "You make good use of evidence from the module materials to support your points.",
        tags: ["evidence", "referencing"],
        variables: [],
        usage_count: 0,
        created_at: ~U[2025-01-01 00:00:00Z],
        updated_at: ~U[2025-01-01 00:00:00Z]
      },

      # Improvement
      %{
        id: "default-improve-1",
        category: :improvement,
        title: "Develop Analysis",
        text:
          "This point could be developed further. Consider explaining why {concept} is significant and what implications it has.",
        tags: ["analysis", "depth"],
        variables: ["concept"],
        usage_count: 0,
        created_at: ~U[2025-01-01 00:00:00Z],
        updated_at: ~U[2025-01-01 00:00:00Z]
      },
      %{
        id: "default-improve-2",
        category: :improvement,
        title: "Add Evidence",
        text:
          "This claim needs supporting evidence. Consider citing specific sources from the module materials.",
        tags: ["evidence", "citations"],
        variables: [],
        usage_count: 0,
        created_at: ~U[2025-01-01 00:00:00Z],
        updated_at: ~U[2025-01-01 00:00:00Z]
      },
      %{
        id: "default-improve-3",
        category: :improvement,
        title: "Explain Connection",
        text: "The connection between {point_a} and {point_b} could be made more explicit.",
        tags: ["connections", "clarity"],
        variables: ["point_a", "point_b"],
        usage_count: 0,
        created_at: ~U[2025-01-01 00:00:00Z],
        updated_at: ~U[2025-01-01 00:00:00Z]
      },

      # Correction
      %{
        id: "default-correct-1",
        category: :correction,
        title: "Factual Error",
        text:
          "This is not quite accurate. {correct_information}. See {resource} for the correct explanation.",
        tags: ["accuracy", "factual"],
        variables: ["correct_information", "resource"],
        usage_count: 0,
        created_at: ~U[2025-01-01 00:00:00Z],
        updated_at: ~U[2025-01-01 00:00:00Z]
      },
      %{
        id: "default-correct-2",
        category: :correction,
        title: "Misunderstanding",
        text:
          "There seems to be a misunderstanding here. {concept} actually refers to {explanation}.",
        tags: ["understanding", "clarification"],
        variables: ["concept", "explanation"],
        usage_count: 0,
        created_at: ~U[2025-01-01 00:00:00Z],
        updated_at: ~U[2025-01-01 00:00:00Z]
      },

      # Referencing
      %{
        id: "default-ref-1",
        category: :referencing,
        title: "Missing In-Text Citation",
        text:
          "This statement needs an in-text citation. Remember to cite sources when presenting ideas or evidence from module materials.",
        tags: ["citations", "Harvard"],
        variables: [],
        usage_count: 0,
        created_at: ~U[2025-01-01 00:00:00Z],
        updated_at: ~U[2025-01-01 00:00:00Z]
      },
      %{
        id: "default-ref-2",
        category: :referencing,
        title: "Harvard Format",
        text:
          "Please check your Harvard referencing format. In-text citations should be (Author, Year) and references should follow the standard format.",
        tags: ["Harvard", "format"],
        variables: [],
        usage_count: 0,
        created_at: ~U[2025-01-01 00:00:00Z],
        updated_at: ~U[2025-01-01 00:00:00Z]
      },
      %{
        id: "default-ref-3",
        category: :referencing,
        title: "Page Numbers",
        text:
          "When using direct quotes, include page numbers in your citation, e.g., (Author, Year, p. 42).",
        tags: ["quotes", "page numbers"],
        variables: [],
        usage_count: 0,
        created_at: ~U[2025-01-01 00:00:00Z],
        updated_at: ~U[2025-01-01 00:00:00Z]
      },

      # Structure
      %{
        id: "default-struct-1",
        category: :structure,
        title: "Add Introduction",
        text:
          "Consider adding a brief introduction that outlines what you will cover and signals your approach.",
        tags: ["introduction", "signposting"],
        variables: [],
        usage_count: 0,
        created_at: ~U[2025-01-01 00:00:00Z],
        updated_at: ~U[2025-01-01 00:00:00Z]
      },
      %{
        id: "default-struct-2",
        category: :structure,
        title: "Paragraph Structure",
        text:
          "Each paragraph should focus on one main point. Consider using the PEEL structure: Point, Evidence, Explanation, Link.",
        tags: ["paragraphs", "PEEL"],
        variables: [],
        usage_count: 0,
        created_at: ~U[2025-01-01 00:00:00Z],
        updated_at: ~U[2025-01-01 00:00:00Z]
      },

      # Critical Thinking
      %{
        id: "default-crit-1",
        category: :critical_thinking,
        title: "Consider Counterarguments",
        text:
          "A stronger answer would consider alternative perspectives or potential counterarguments to your position.",
        tags: ["analysis", "counterarguments"],
        variables: [],
        usage_count: 0,
        created_at: ~U[2025-01-01 00:00:00Z],
        updated_at: ~U[2025-01-01 00:00:00Z]
      },
      %{
        id: "default-crit-2",
        category: :critical_thinking,
        title: "Evaluate Sources",
        text:
          "Try to critically evaluate your sources rather than simply presenting them. What are their strengths and limitations?",
        tags: ["evaluation", "sources"],
        variables: [],
        usage_count: 0,
        created_at: ~U[2025-01-01 00:00:00Z],
        updated_at: ~U[2025-01-01 00:00:00Z]
      },

      # Guidance
      %{
        id: "default-guide-1",
        category: :guidance,
        title: "Further Reading",
        text: "For more on this topic, see {resource}.",
        tags: ["resources", "reading"],
        variables: ["resource"],
        usage_count: 0,
        created_at: ~U[2025-01-01 00:00:00Z],
        updated_at: ~U[2025-01-01 00:00:00Z]
      },
      %{
        id: "default-guide-2",
        category: :guidance,
        title: "Tutorial Discussion",
        text: "We will discuss {topic} in more detail during the tutorial on {date}.",
        tags: ["tutorial", "discussion"],
        variables: ["topic", "date"],
        usage_count: 0,
        created_at: ~U[2025-01-01 00:00:00Z],
        updated_at: ~U[2025-01-01 00:00:00Z]
      }
    ]
  end

  # ============================================================
  # PRIVATE - HELPERS
  # ============================================================

  defp generate_id do
    :crypto.strong_rand_bytes(8) |> Base.encode16(case: :lower)
  end

  defp extract_variables(text) do
    ~r/\{([a-z_]+)\}/
    |> Regex.scan(text)
    |> Enum.map(&List.last/1)
    |> Enum.uniq()
  end

  defp maybe_filter_by_type(materials, nil), do: materials
  defp maybe_filter_by_type(materials, type), do: Enum.filter(materials, &(&1.type == type))

  defp maybe_filter_by_course(materials, nil), do: materials

  defp maybe_filter_by_course(materials, code) do
    Enum.filter(materials, fn m ->
      m.course_codes == [] || code in m.course_codes
    end)
  end

  defp maybe_filter_by_tags(materials, nil), do: materials
  defp maybe_filter_by_tags(materials, []), do: materials

  defp maybe_filter_by_tags(materials, tags) do
    Enum.filter(materials, fn m ->
      Enum.any?(tags, &(&1 in m.tags))
    end)
  end

  defp atomize_keys(map) when is_map(map) do
    Map.new(map, fn
      {k, v} when is_binary(k) -> {String.to_existing_atom(k), v}
      {k, v} when is_atom(k) -> {k, v}
    end)
  rescue
    ArgumentError -> map
  end

  defp to_category(cat) when is_atom(cat) and cat in @comment_categories, do: cat
  defp to_category(cat) when is_binary(cat), do: String.to_existing_atom(cat)
  defp to_category(_), do: :general

  defp to_material_type(type) when is_atom(type) and type in @material_types, do: type
  defp to_material_type(type) when is_binary(type), do: String.to_existing_atom(type)
  defp to_material_type(_), do: :other

  defp merge_by_id(existing, new, overwrite) do
    existing_ids = MapSet.new(existing, & &1.id)

    if overwrite do
      # Replace existing items with same ID
      new_ids = MapSet.new(new, & &1.id)
      kept = Enum.reject(existing, &(&1.id in new_ids))
      kept ++ new
    else
      # Only add items with new IDs
      additions = Enum.reject(new, &(&1.id in existing_ids))
      existing ++ additions
    end
  end
end
