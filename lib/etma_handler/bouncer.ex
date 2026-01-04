# SPDX-License-Identifier: AGPL-3.0-or-later
defmodule EtmaHandler.Bouncer do
  @moduledoc """
  The "Bouncer" - Automatic File Ingestion & Security Gate.

  Watches the Downloads folder for student files and automatically:

  1. **Detects** new files matching eTMA patterns
  2. **Scans** for viruses, macros, and corruption
  3. **Packages** into standardized ZIP containers
  4. **Stores** in the secure vault

  ## Security Pipeline

      Downloads → Detect → Scan → Package → Store
                    ↓        ↓       ↓        ↓
                 PubSub   Quarantine  ZIP    Vault
                  event   if dirty   always

  ## File Naming Convention

  Expected format: `[Course]-[Presentation]-[TMA]-[ID]-[OUCU].ext`
  Example: `M150-25J-01-1-rg8274.docx`

  ## Supported File Types

  - **Documents**: .doc, .docx, .rtf, .odt, .pdf, .txt
  - **Code**: .ex, .exs, .rs, .js, .py, .java (computing modules)
  - **Media**: .png, .jpg, .gif (screenshots)
  - **Presentations**: .pptx, .ppt, .odp
  - **Spreadsheets**: .xlsx, .xls, .ods
  - **Archives**: .zip (already containerized)
  - **Metadata**: .fhi (eTMA marking files)
  """

  use GenServer
  require Logger

  alias EtmaHandler.{Container, Scanner, Settings}

  # Comprehensive file type support
  @document_extensions ~w(.doc .docx .rtf .odt .pdf .txt)
  @code_extensions ~w(.ex .exs .rs .js .ts .py .rb .java .c .cpp .h .go)
  @media_extensions ~w(.png .jpg .jpeg .gif .bmp .webp .svg)
  @spreadsheet_extensions ~w(.xlsx .xls .ods .csv)
  @presentation_extensions ~w(.pptx .ppt .odp)
  @archive_extensions ~w(.zip .tar.gz .7z)
  @metadata_extensions ~w(.fhi)

  @allowed_extensions @document_extensions ++
                        @code_extensions ++
                        @media_extensions ++
                        @spreadsheet_extensions ++
                        @presentation_extensions ++
                        @archive_extensions ++
                        @metadata_extensions

  @filename_pattern ~r/^([A-Z]{1,4}\d{2,4})-(\d{2}[JBDK])-(\d{2})-(\d+)-([a-z]{2}\d+)/i

  defstruct [:download_dir, :watcher_pid, :vault_dir, :quarantine_dir]

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

  @doc """
  Scan a file without ingesting.
  Returns the scan result without packaging or storing.
  """
  def scan(file_path) do
    Scanner.scan(file_path)
  end

  @doc """
  Ingest multiple files in batch.
  Returns a list of {path, result} tuples.
  """
  def ingest_batch(file_paths) when is_list(file_paths) do
    GenServer.call(__MODULE__, {:ingest_batch, file_paths}, :infinity)
  end

  @doc """
  List all containers in the vault.
  """
  def list_vault do
    GenServer.call(__MODULE__, :list_vault)
  end

  @doc """
  List all files in quarantine.
  """
  def list_quarantine do
    GenServer.call(__MODULE__, :list_quarantine)
  end

  @doc """
  Get the current directories being watched and used.
  """
  def get_paths do
    GenServer.call(__MODULE__, :get_paths)
  end

  @doc """
  Release a file from quarantine after manual review.
  The file is moved back to downloads for re-processing,
  or optionally force-ingested if `force: true` is passed.
  """
  def release_from_quarantine(quarantine_path, opts \\ []) do
    GenServer.call(__MODULE__, {:release_quarantine, quarantine_path, opts})
  end

  @doc """
  Permanently delete a file from quarantine.
  """
  def delete_from_quarantine(quarantine_path) do
    GenServer.call(__MODULE__, {:delete_quarantine, quarantine_path})
  end

  # --- Server Implementation ---

  @impl true
  def init(opts) do
    download_dir = Keyword.fetch!(opts, :download_dir)

    # Get vault and quarantine directories from settings or defaults
    data_dir = Settings.get([:paths, :data_dir]) ||
               Path.join(System.user_home!(), ".etma_handler")
    vault_dir = Keyword.get(opts, :vault_dir, Path.join(data_dir, "vault"))
    quarantine_dir = Keyword.get(opts, :quarantine_dir, Path.join(data_dir, "quarantine"))

    # Ensure directories exist
    File.mkdir_p!(vault_dir)
    File.mkdir_p!(quarantine_dir)

    Logger.info("Bouncer watching: #{download_dir}")
    Logger.info("  Vault: #{vault_dir}")
    Logger.info("  Quarantine: #{quarantine_dir}")

    # Start the file system watcher
    {:ok, watcher_pid} = FileSystem.start_link(dirs: [download_dir])
    FileSystem.subscribe(watcher_pid)

    {:ok, %__MODULE__{
      download_dir: download_dir,
      watcher_pid: watcher_pid,
      vault_dir: vault_dir,
      quarantine_dir: quarantine_dir
    }}
  end

  @impl true
  def handle_call({:ingest, file_path}, _from, state) do
    result = do_ingest(file_path, state)
    {:reply, result, state}
  end

  @impl true
  def handle_call({:validate, file_path}, _from, state) do
    result = do_validate(file_path)
    {:reply, result, state}
  end

  @impl true
  def handle_call({:ingest_batch, file_paths}, _from, state) do
    results =
      file_paths
      |> Enum.map(fn path ->
        {path, do_ingest(path, state)}
      end)

    {:reply, results, state}
  end

  @impl true
  def handle_call(:list_vault, _from, state) do
    containers =
      state.vault_dir
      |> File.ls!()
      |> Enum.filter(&String.ends_with?(&1, ".zip"))
      |> Enum.map(fn name ->
        path = Path.join(state.vault_dir, name)
        stat = File.stat!(path)
        %{
          name: name,
          path: path,
          size: stat.size,
          created: stat.ctime
        }
      end)
      |> Enum.sort_by(& &1.created, {:desc, NaiveDateTime})

    {:reply, {:ok, containers}, state}
  rescue
    e -> {:reply, {:error, Exception.message(e)}, state}
  end

  @impl true
  def handle_call(:list_quarantine, _from, state) do
    files =
      state.quarantine_dir
      |> File.ls!()
      |> Enum.reject(&String.ends_with?(&1, ".meta"))
      |> Enum.map(fn name ->
        path = Path.join(state.quarantine_dir, name)
        meta_path = "#{path}.meta"
        stat = File.stat!(path)

        meta =
          if File.exists?(meta_path) do
            File.read!(meta_path)
          else
            nil
          end

        %{
          name: name,
          path: path,
          size: stat.size,
          quarantined_at: stat.ctime,
          meta: meta
        }
      end)
      |> Enum.sort_by(& &1.quarantined_at, {:desc, NaiveDateTime})

    {:reply, {:ok, files}, state}
  rescue
    e -> {:reply, {:error, Exception.message(e)}, state}
  end

  @impl true
  def handle_call(:get_paths, _from, state) do
    paths = %{
      download_dir: state.download_dir,
      vault_dir: state.vault_dir,
      quarantine_dir: state.quarantine_dir
    }
    {:reply, {:ok, paths}, state}
  end

  @impl true
  def handle_call({:release_quarantine, quarantine_path, opts}, _from, state) do
    if File.exists?(quarantine_path) do
      if Keyword.get(opts, :force, false) do
        # Force ingest - skip scanning, directly package
        case do_validate(quarantine_path) do
          {:ok, metadata} ->
            case package_and_store(quarantine_path, metadata, state) do
              {:ok, container_path} ->
                # Remove from quarantine
                File.rm(quarantine_path)
                File.rm("#{quarantine_path}.meta")
                {:reply, {:ok, container_path}, state}

              error ->
                {:reply, error, state}
            end

          error ->
            {:reply, error, state}
        end
      else
        # Move back to downloads for re-scanning
        original_name = extract_original_name(quarantine_path)
        new_path = Path.join(state.download_dir, original_name)
        case File.rename(quarantine_path, new_path) do
          :ok ->
            File.rm("#{quarantine_path}.meta")
            {:reply, {:ok, new_path}, state}

          {:error, reason} ->
            {:reply, {:error, reason}, state}
        end
      end
    else
      {:reply, {:error, :not_found}, state}
    end
  end

  @impl true
  def handle_call({:delete_quarantine, quarantine_path}, _from, state) do
    if File.exists?(quarantine_path) do
      File.rm(quarantine_path)
      File.rm("#{quarantine_path}.meta")
      {:reply, :ok, state}
    else
      {:reply, {:error, :not_found}, state}
    end
  end

  @impl true
  def handle_info({:file_event, _watcher_pid, {path, events}}, state) do
    # Only process new files (created or modified)
    if :created in events or :modified in events do
      handle_new_file(path, state)
    end

    {:noreply, state}
  end

  @impl true
  def handle_info({:file_event, _watcher_pid, :stop}, state) do
    Logger.warning("File watcher stopped")
    {:noreply, state}
  end

  # --- Private Implementation ---

  defp handle_new_file(path, state) do
    # Ignore hidden files and directories
    filename = Path.basename(path)

    unless String.starts_with?(filename, ".") or File.dir?(path) do
      # Wait a moment for the file to finish writing
      Process.sleep(500)

      if File.exists?(path) do
        case do_validate(path) do
          {:ok, metadata} ->
            Logger.info("Valid file detected: #{filename}")

            # Run security scan
            case Scanner.scan(path) do
              {:ok, :clean} ->
                # File is clean - broadcast for user to approve ingestion
                Phoenix.PubSub.broadcast(
                  EtmaHandler.PubSub,
                  "bouncer:events",
                  {:new_file, path, metadata, :clean}
                )

              {:ok, {:suspicious, reasons}} ->
                # File is suspicious - quarantine and notify
                Logger.warning("Suspicious file detected: #{filename} - #{inspect(reasons)}")
                quarantine_file(path, reasons, state)
                Phoenix.PubSub.broadcast(
                  EtmaHandler.PubSub,
                  "bouncer:events",
                  {:suspicious_file, path, metadata, reasons}
                )

              {:ok, {:infected, reason}} ->
                # File is infected - quarantine and alert
                Logger.error("Infected file detected: #{filename} - #{reason}")
                quarantine_file(path, [reason], state)
                Phoenix.PubSub.broadcast(
                  EtmaHandler.PubSub,
                  "bouncer:events",
                  {:infected_file, path, metadata, reason}
                )

              {:error, scan_error} ->
                Logger.error("Scan failed for #{filename}: #{inspect(scan_error)}")
                Phoenix.PubSub.broadcast(
                  EtmaHandler.PubSub,
                  "bouncer:events",
                  {:scan_error, path, scan_error}
                )
            end

          {:error, reasons} ->
            Logger.warning("Invalid file rejected: #{filename} - #{inspect(reasons)}")
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

  defp do_ingest(path, state) do
    with {:ok, metadata} <- do_validate(path),
         {:ok, :clean} <- Scanner.scan(path),
         {:ok, container_path} <- package_and_store(path, metadata, state) do
      Logger.info("Ingested: #{Path.basename(path)} → #{container_path}")

      # Broadcast successful ingestion
      Phoenix.PubSub.broadcast(
        EtmaHandler.PubSub,
        "bouncer:events",
        {:ingested, path, container_path, metadata}
      )

      {:ok, %{metadata | container_path: container_path}}
    else
      {:ok, {:suspicious, reasons}} ->
        Logger.warning("Ingestion blocked - suspicious: #{inspect(reasons)}")
        quarantine_file(path, reasons, state)
        {:error, {:suspicious, reasons}}

      {:ok, {:infected, reason}} ->
        Logger.error("Ingestion blocked - infected: #{reason}")
        quarantine_file(path, [reason], state)
        {:error, {:infected, reason}}

      {:error, _} = error ->
        error
    end
  end

  defp package_and_store(path, metadata, state) do
    # Generate container filename from metadata
    container_name = generate_container_name(metadata)
    container_path = Path.join(state.vault_dir, container_name)

    # Package the file into a ZIP container
    case Container.package(path, container_path) do
      {:ok, _container_info} ->
        # Optionally delete original after successful packaging
        # For now, keep original - user can decide
        {:ok, container_path}

      {:error, reason} ->
        Logger.error("Failed to package #{path}: #{inspect(reason)}")
        {:error, {:packaging_failed, reason}}
    end
  end

  defp generate_container_name(metadata) do
    timestamp = DateTime.utc_now() |> DateTime.to_unix()

    base_name =
      if Map.has_key?(metadata, :course_code) do
        "#{metadata.course_code}-#{metadata.presentation}-#{metadata.tma_number}-#{metadata.student_oucu}"
      else
        Path.basename(metadata.filename, Path.extname(metadata.filename))
      end

    "#{base_name}-#{timestamp}.zip"
  end

  defp quarantine_file(path, reasons, state) do
    filename = Path.basename(path)
    timestamp = DateTime.utc_now() |> DateTime.to_unix()
    quarantine_name = "#{timestamp}-#{filename}"
    quarantine_path = Path.join(state.quarantine_dir, quarantine_name)

    # Move file to quarantine
    case File.rename(path, quarantine_path) do
      :ok ->
        # Write a metadata file explaining why it was quarantined
        meta_path = "#{quarantine_path}.meta"
        meta_content = """
        Quarantined: #{DateTime.utc_now() |> DateTime.to_iso8601()}
        Original: #{path}
        Reasons:
        #{Enum.map(reasons, &"  - #{&1}") |> Enum.join("\n")}
        """
        File.write(meta_path, meta_content)
        Logger.info("Quarantined: #{filename} → #{quarantine_path}")
        {:ok, quarantine_path}

      {:error, reason} ->
        Logger.error("Failed to quarantine #{filename}: #{reason}")
        {:error, reason}
    end
  end

  defp extract_original_name(quarantine_path) do
    # Quarantine names are: {timestamp}-{original_filename}
    filename = Path.basename(quarantine_path)

    case String.split(filename, "-", parts: 2) do
      [_timestamp, original] -> original
      _ -> filename
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
