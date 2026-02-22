# SPDX-License-Identifier: AGPL-3.0-or-later

defmodule EtmaHandler.Marking.AssignmentRules do
  @moduledoc """
  Assignment Rules Engine — Semantic Validation Policies.

  This module defines the formal requirements for student submissions 
  on a per-assignment basis. It ensures that uploaded files match the 
  expected pedagogical and technical criteria.

  ## Rule Dimensions:
  1. **File Type**: Whitelist of allowed extensions (e.g. `.docx` for essays).
  2. **Security**: Blacklist of dangerous extensions (e.g. `.exe`, `.bat`).
  3. **Volume**: Max file size and word count constraints.
  4. **Pedagogy**: Requirements for references, citations, and specific 
     module materials.
  """

  alias EtmaHandler.Settings

  @type rules :: %{
          optional(:allowed_extensions) => [String.t()],
          optional(:max_file_size_mb) => pos_integer(),
          optional(:require_references) => boolean(),
          optional(:auto_notify) => boolean()
        }

  @doc """
  VALIDATION: Checks if a file extension is permitted for the given assignment.
  
  RETURNS:
  - `{:ok, true}`: Submission is compliant.
  - `{:error, {:rejected, reason}}`: Submission violates a specific rule.
  """
  @spec allowed?(map(), String.t()) :: {:ok, boolean()} | {:error, {:rejected, String.t()}}
  def allowed?(metadata, extension) do
    # ... [Implementation checking against @default_rules and stored overrides]
  end

  @doc """
  TEMPLATES: Returns pre-configured rule sets for common assignment types.
  Supported: `:essay`, `:computing`, `:report`, `:presentation`.
  """
  @spec template(atom()) :: rules()
  def template(:essay) do
    %{
      allowed_extensions: ~w(.docx .rtf .odt),
      max_file_size_mb: 10,
      require_references: true,
      auto_notify: true
    }
  end
end
