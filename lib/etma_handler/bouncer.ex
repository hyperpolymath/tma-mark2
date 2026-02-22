# SPDX-License-Identifier: AGPL-3.0-or-later

defmodule EtmaHandler.Bouncer do
  @moduledoc """
  The Bouncer — Automated Ingestion and Security Gateway.

  This module implements a reactive filesystem watcher that monitors 
  the system Downloads folder for student submissions. It provides a 
  secure, multi-stage pipeline to transform raw files into verified 
  ZIP artifacts.

  ## Ingestion Pipeline:
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

  # SUPPORTED TYPES: High-assurance whitelist for student artifacts.
  @document_extensions ~w(.doc .docx .rtf .odt .pdf .txt)
  @code_extensions ~w(.ex .rs .js .py .java .c .cpp)
  @filename_pattern ~r/^([A-Z]{1,4}\d{2,4})-(\d{2}[JBDK])-(\d{2})-(\d+)-([a-z]{2}\d+)/i

  @impl true
  def init(opts) do
    # BOOTSTRAP: Establishes the link to the OS filesystem watcher.
    # Ensures that vault and quarantine directories exist.
    {:ok, watcher_pid} = FileSystem.start_link(dirs: [download_dir])
    FileSystem.subscribe(watcher_pid)
    {:ok, %__MODULE__{...}}
  end

  @impl true
  def handle_call({:ingest, file_path}, _from, state) do
    # MANUAL OVERRIDE: Allows the UI to force ingestion of a specific path.
    result = do_ingest(file_path, state)
    {:reply, result, state}
  end
end
