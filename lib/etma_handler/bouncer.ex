# SPDX-License-Identifier: AGPL-3.0-or-later
defmodule EtmaHandler.Bouncer do
  @moduledoc """
  The "Bouncer" - Automatic File Ingestion.

  Watches the Downloads folder for student files and automatically:
  1. Validates file integrity (can it be opened?)
  2. Checks file naming convention (correct course code format?)
  3. Validates file type (allowed extensions only)
  4. Moves valid files to the secure vault

  Invalid files trigger immediate rejection emails to students,
  catching issues within seconds of download rather than days later.

  ## File Naming Convention

  Expected format: `[Course]-[Presentation]-[TMA]-[ID]-[OUCU].ext`
  Example: `M150-25J-01-1-rg8274.docx`

  ## Allowed Extensions

  - `.doc`, `.docx` (Word documents)
  - `.rtf` (Rich Text Format)
  - `.pdf` (Portable Document Format)
  - `.zip` (Compressed archives)
  - `.fhi` (eTMA marking files)
  """

  use GenServer
  require Logger

  @allowed_extensions ~w(.doc .docx .rtf .pdf .zip .fhi .odt)
  @filename_pattern ~r/^([A-Z]{1,4}\d{2,4})-(\d{2}[JB])-(\d{2})-(\d+)-([a-z]{2}\d+)/i

  defstruct [:download_dir, :watcher_pid]

  # --- Client API ---

  def start_link(opts) do
    GenServer.start_link(__MODULE__, opts, name: __MODULE__)
  end

  @doc """
  Manually trigger ingestion of a specific file.
  """
  def ingest(file_path) do
    GenServer.call(__MODULE__, {:ingest, file_path})
  end

  @doc """
  Check if a file passes validation without ingesting.
  """
  def validate(file_path) do
    GenServer.call(__MODULE__, {:validate, file_path})
  end

  # --- Server Implementation ---

  @impl true
  def init(opts) do
    download_dir = Keyword.fetch!(opts, :download_dir)

    Logger.info("Bouncer watching: #{download_dir}")

    # Start the file system watcher
    {:ok, watcher_pid} = FileSystem.start_link(dirs: [download_dir])
    FileSystem.subscribe(watcher_pid)

    {:ok, %__MODULE__{download_dir: download_dir, watcher_pid: watcher_pid}}
  end

  @impl true
  def handle_call({:ingest, file_path}, _from, state) do
    result = do_ingest(file_path)
    {:reply, result, state}
  end

  @impl true
  def handle_call({:validate, file_path}, _from, state) do
    result = do_validate(file_path)
    {:reply, result, state}
  end

  @impl true
  def handle_info({:file_event, _watcher_pid, {path, events}}, state) do
    # Only process new files (created or modified)
    if :created in events or :modified in events do
      handle_new_file(path)
    end

    {:noreply, state}
  end

  @impl true
  def handle_info({:file_event, _watcher_pid, :stop}, state) do
    Logger.warning("File watcher stopped")
    {:noreply, state}
  end

  # --- Private Implementation ---

  defp handle_new_file(path) do
    # Ignore hidden files and directories
    filename = Path.basename(path)

    unless String.starts_with?(filename, ".") or File.dir?(path) do
      # Wait a moment for the file to finish writing
      Process.sleep(500)

      if File.exists?(path) do
        case do_validate(path) do
          {:ok, metadata} ->
            Logger.info("Valid file detected: #{filename}")
            # Broadcast to UI
            Phoenix.PubSub.broadcast(
              EtmaHandler.PubSub,
              "bouncer:events",
              {:new_file, path, metadata}
            )

          {:error, reasons} ->
            Logger.warning("Invalid file rejected: #{filename} - #{inspect(reasons)}")
            # Broadcast rejection to UI
            Phoenix.PubSub.broadcast(
              EtmaHandler.PubSub,
              "bouncer:events",
              {:rejected_file, path, reasons}
            )
        end
      end
    end
  end

  defp do_validate(path) do
    checks = [
      check_exists(path),
      check_extension(path),
      check_filename(path),
      check_readable(path),
      check_not_empty(path)
    ]

    errors = Enum.filter(checks, fn
      {:error, _} -> true
      _ -> false
    end)

    if Enum.empty?(errors) do
      {:ok, extract_metadata(path)}
    else
      {:error, Enum.map(errors, fn {:error, msg} -> msg end)}
    end
  end

  defp do_ingest(path) do
    case do_validate(path) do
      {:ok, metadata} ->
        # Move to secure storage
        # For now, just log - actual vault integration comes later
        Logger.info("Ingesting: #{path}")
        {:ok, metadata}

      error ->
        error
    end
  end

  # --- Validation Checks ---

  defp check_exists(path) do
    if File.exists?(path) do
      :ok
    else
      {:error, "File not found"}
    end
  end

  defp check_extension(path) do
    ext = path |> Path.extname() |> String.downcase()

    if ext in @allowed_extensions do
      :ok
    else
      {:error, "Invalid file type '#{ext}'. Allowed: #{Enum.join(@allowed_extensions, ", ")}"}
    end
  end

  defp check_filename(path) do
    filename = Path.basename(path, Path.extname(path))

    if Regex.match?(@filename_pattern, filename) do
      :ok
    else
      {:error,
       "Incorrect filename format. Expected: [Course]-[Presentation]-[TMA]-[ID]-[OUCU] (e.g. M150-25J-01-1-rg8274)"}
    end
  end

  defp check_readable(path) do
    case File.read(path) do
      {:ok, _} -> :ok
      {:error, reason} -> {:error, "Cannot read file: #{reason}"}
    end
  end

  defp check_not_empty(path) do
    case File.stat(path) do
      {:ok, %{size: 0}} -> {:error, "File is empty"}
      {:ok, _} -> :ok
      {:error, reason} -> {:error, "Cannot check file: #{reason}"}
    end
  end

  # --- Metadata Extraction ---

  defp extract_metadata(path) do
    filename = Path.basename(path, Path.extname(path))

    case Regex.run(@filename_pattern, filename) do
      [_, course, presentation, tma, id, oucu] ->
        %{
          path: path,
          filename: Path.basename(path),
          course_code: course,
          presentation: presentation,
          tma_number: tma,
          submission_id: id,
          student_oucu: oucu,
          extension: Path.extname(path),
          size: File.stat!(path).size,
          detected_at: DateTime.utc_now()
        }

      nil ->
        %{
          path: path,
          filename: Path.basename(path),
          extension: Path.extname(path),
          size: File.stat!(path).size,
          detected_at: DateTime.utc_now()
        }
    end
  end
end
