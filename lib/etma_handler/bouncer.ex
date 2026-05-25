# SPDX-License-Identifier: MPL-2.0

defmodule EtmaHandler.Bouncer do
  @moduledoc """
  The Bouncer — Automated Ingestion and Security Gateway.

  This module is the planned reactive filesystem watcher that monitors the
  system Downloads folder for student submissions. The full pipeline is
  scheduled for Phase 1.3 (after the FHI parser in Phase 1.2); the module
  currently exposes only the filename / extension constants used by other
  components and fails loudly if added to the supervision tree.

  ## Planned ingestion pipeline:
  1. **DETECTION**: Filters for filenames matching the eTMA pattern
     (`[Course]-[Presentation]-[TMA]-[ID]-[OUCU]`).
  2. **SCANNING**: Dispatches the file to the `Scanner` for virus
     and macro analysis.
  3. **QUARANTINE**: Isolates "dirty" or malformed files for manual review.
  4. **PACKAGING**: Encapsulates verified files into an immutable
     ZIP container within the secure vault.
  """

  use GenServer
  require Logger

  @document_extensions ~w(.doc .docx .rtf .odt .pdf .txt)
  @code_extensions ~w(.ex .rs .js .py .java .c .cpp)
  @filename_pattern ~r/^([A-Z]{1,4}\d{2,4})-(\d{2}[JBDK])-(\d{2})-(\d+)-([a-z]{2}\d+)/i

  @doc "Compiled regex for valid eTMA filenames."
  @spec filename_pattern() :: Regex.t()
  def filename_pattern, do: @filename_pattern

  @doc "Document extensions accepted by the ingest pipeline."
  @spec document_extensions() :: [String.t()]
  def document_extensions, do: @document_extensions

  @doc "Source-code extensions accepted by the ingest pipeline."
  @spec code_extensions() :: [String.t()]
  def code_extensions, do: @code_extensions

  def start_link(opts \\ []) do
    GenServer.start_link(__MODULE__, opts, name: __MODULE__)
  end

  @impl true
  def init(_opts) do
    Logger.error(
      "EtmaHandler.Bouncer is not implemented (scheduled for Phase 1.3). " <>
        "Set :etma_handler, :auto_ingest to false (default) to remove it from " <>
        "the supervision tree."
    )

    {:stop, :not_implemented}
  end
end
