# SPDX-License-Identifier: AGPL-3.0-or-later
defmodule EtmaHandler.Marking.AssignmentRules do
  @moduledoc """
  Assignment-Specific Rules Engine.

  Defines allowed file types, submission requirements, and validation
  rules on a per-assignment basis.

  ## Configuration Structure

      %{
        "M150-25J-01" => %{
          allowed_extensions: [".docx", ".rtf"],
          max_file_size_mb: 10,
          word_limit: 2000,
          require_references: true,
          reference_style: :cite_them_right,
          module_materials: ["Author2020", "Smith2019"],
          auto_reject_pdf: true,
          rejection_message: "TMA01 must be submitted in Word format (.docx or .rtf)"
        }
      }

  ## Usage

      # Check if file type is allowed
      AssignmentRules.allowed?(metadata, ".pdf")
      # => {:ok, true} or {:error, {:rejected, "reason"}}

      # Get rejection message for wrong file type
      AssignmentRules.rejection_message(metadata, ".pdf")
      # => "TMA01 must be submitted in Word format..."

  """

  require Logger

  alias EtmaHandler.Settings

  @type assignment_key :: String.t()
  @type rules :: %{
          optional(:allowed_extensions) => [String.t()],
          optional(:blocked_extensions) => [String.t()],
          optional(:max_file_size_mb) => pos_integer(),
          optional(:word_limit) => pos_integer(),
          optional(:require_references) => boolean(),
          optional(:reference_style) => atom(),
          optional(:min_references) => pos_integer(),
          optional(:module_materials) => [String.t()],
          optional(:rejection_message) => String.t(),
          optional(:auto_notify) => boolean()
        }

  # Default rules when no assignment-specific rules exist
  @default_rules %{
    allowed_extensions: ~w(.doc .docx .rtf .odt .pdf .txt),
    blocked_extensions: ~w(.exe .bat .cmd .scr .vbs .js),
    max_file_size_mb: 50,
    require_references: false,
    auto_notify: false
  }

  # ============================================================
  # PUBLIC API
  # ============================================================

  @doc """
  Check if a file extension is allowed for a given assignment.

  ## Examples

      iex> AssignmentRules.allowed?(%{course_code: "M150", presentation: "25J", tma_number: "01"}, ".docx")
      {:ok, true}

      iex> AssignmentRules.allowed?(%{course_code: "M150", presentation: "25J", tma_number: "01"}, ".pdf")
      {:error, {:rejected, "TMA01 must be submitted in Word format"}}
  """
  @spec allowed?(map(), String.t()) :: {:ok, boolean()} | {:error, {:rejected, String.t()}}
  def allowed?(metadata, extension) do
    rules = get_rules(metadata)
    ext = String.downcase(extension)

    cond do
      ext in (rules[:blocked_extensions] || []) ->
        {:error, {:rejected, "File type #{ext} is not allowed for security reasons"}}

      rules[:allowed_extensions] && ext not in rules[:allowed_extensions] ->
        message = rules[:rejection_message] || default_rejection_message(rules, ext)
        {:error, {:rejected, message}}

      true ->
        {:ok, true}
    end
  end

  @doc """
  Get the rejection message for a disallowed file type.
  """
  @spec rejection_message(map(), String.t()) :: String.t()
  def rejection_message(metadata, extension) do
    case allowed?(metadata, extension) do
      {:error, {:rejected, message}} -> message
      {:ok, _} -> ""
    end
  end

  @doc """
  Get all rules for an assignment.
  """
  @spec get_rules(map()) :: rules()
  def get_rules(metadata) do
    key = assignment_key(metadata)
    stored_rules = load_assignment_rules(key)
    Map.merge(@default_rules, stored_rules || %{})
  end

  @doc """
  Set rules for a specific assignment.
  """
  @spec set_rules(assignment_key(), rules()) :: :ok | {:error, term()}
  def set_rules(assignment_key, rules) when is_map(rules) do
    all_rules = load_all_rules()
    updated = Map.put(all_rules, assignment_key, rules)
    save_all_rules(updated)
  end

  @doc """
  Delete rules for a specific assignment (reverts to defaults).
  """
  @spec delete_rules(assignment_key()) :: :ok
  def delete_rules(assignment_key) do
    all_rules = load_all_rules()
    updated = Map.delete(all_rules, assignment_key)
    save_all_rules(updated)
  end

  @doc """
  List all assignments with custom rules.
  """
  @spec list_assignments :: [assignment_key()]
  def list_assignments do
    load_all_rules() |> Map.keys()
  end

  @doc """
  Check if auto-notification is enabled for an assignment.
  """
  @spec auto_notify?(map()) :: boolean()
  def auto_notify?(metadata) do
    rules = get_rules(metadata)
    Map.get(rules, :auto_notify, false)
  end

  @doc """
  Get reference requirements for an assignment.
  """
  @spec reference_requirements(map()) :: map() | nil
  def reference_requirements(metadata) do
    rules = get_rules(metadata)

    if rules[:require_references] do
      %{
        required: true,
        style: rules[:reference_style] || :harvard,
        min_count: rules[:min_references] || 0,
        module_materials: rules[:module_materials] || []
      }
    else
      nil
    end
  end

  @doc """
  Generate assignment key from metadata.
  Format: COURSE-PRESENTATION-TMA (e.g., "M150-25J-01")
  """
  @spec assignment_key(map()) :: assignment_key()
  def assignment_key(%{course_code: course, presentation: pres, tma_number: tma}) do
    "#{course}-#{pres}-#{tma}"
  end

  def assignment_key(%{"course_code" => course, "presentation" => pres, "tma_number" => tma}) do
    "#{course}-#{pres}-#{tma}"
  end

  def assignment_key(_), do: "default"

  # ============================================================
  # RULE TEMPLATES
  # ============================================================

  @doc """
  Pre-defined rule templates for common assignment types.
  """
  @spec template(atom()) :: rules()
  def template(:essay) do
    %{
      allowed_extensions: ~w(.docx .rtf .odt),
      max_file_size_mb: 10,
      require_references: true,
      reference_style: :cite_them_right,
      auto_notify: true,
      rejection_message: "Essays must be submitted in Word format (.docx, .rtf, or .odt). PDFs are not accepted."
    }
  end

  def template(:computing) do
    %{
      allowed_extensions: ~w(.docx .rtf .odt .pdf .zip .js .py .ex .rs .java .c .cpp .h),
      max_file_size_mb: 20,
      require_references: false,
      auto_notify: true,
      rejection_message: "Please submit your code files or a Word document containing your work."
    }
  end

  def template(:report) do
    %{
      allowed_extensions: ~w(.docx .rtf .odt .pdf),
      max_file_size_mb: 20,
      require_references: true,
      reference_style: :cite_them_right,
      min_references: 5,
      auto_notify: true
    }
  end

  def template(:presentation) do
    %{
      allowed_extensions: ~w(.pptx .ppt .odp .pdf),
      max_file_size_mb: 50,
      require_references: false,
      auto_notify: true
    }
  end

  def template(:spreadsheet) do
    %{
      allowed_extensions: ~w(.xlsx .xls .ods),
      max_file_size_mb: 20,
      require_references: false,
      auto_notify: true
    }
  end

  def template(_), do: @default_rules

  @doc """
  Apply a template to an assignment.
  """
  @spec apply_template(assignment_key(), atom(), map()) :: :ok | {:error, term()}
  def apply_template(assignment_key, template_name, overrides \\ %{}) do
    base_rules = template(template_name)
    merged_rules = Map.merge(base_rules, overrides)
    set_rules(assignment_key, merged_rules)
  end

  # ============================================================
  # PRIVATE - STORAGE
  # ============================================================

  defp load_all_rules do
    case Settings.get([:assignment_rules]) do
      nil -> %{}
      rules when is_map(rules) -> rules
      _ -> %{}
    end
  rescue
    _ -> %{}
  end

  defp load_assignment_rules(key) do
    load_all_rules() |> Map.get(key)
  end

  defp save_all_rules(rules) do
    Settings.put([:assignment_rules], rules)
  end

  defp default_rejection_message(rules, extension) do
    allowed = rules[:allowed_extensions] || []
    allowed_str = Enum.join(allowed, ", ")
    "File type #{extension} is not allowed. Accepted formats: #{allowed_str}"
  end
end
