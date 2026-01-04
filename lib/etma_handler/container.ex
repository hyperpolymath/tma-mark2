# SPDX-License-Identifier: AGPL-3.0-or-later
defmodule EtmaHandler.Container do
  @moduledoc """
  ZIP Container Management for eTMA Submissions.

  All submissions are packaged into standardized ZIP containers, regardless
  of whether they contain one file or many. This provides:

  - **Consistency**: Always deal with one format
  - **Integrity**: ZIP CRC32 + BLAKE3 manifest for verification
  - **Security**: Scan before extraction, never touch filesystem until verified
  - **Metadata**: FHI + our manifest travel with the files
  - **Atomicity**: Single file to move/backup/upload

  ## Container Structure

      submission-{oucu}-{course}-{tma}.zip
      ├── manifest.json          # Our metadata (JSON)
      ├── original.fhi           # OU metadata (XML, if present)
      ├── submission/            # Student's submitted files
      │   ├── essay.docx         # Main document
      │   ├── code/              # Code files (computing modules)
      │   │   └── *.rs, *.ex, *.js
      │   └── media/             # Images, screenshots
      │       └── *.png, *.jpg
      ├── marked/                # Added during marking (empty initially)
      └── hashes.blake3          # File integrity hashes

  ## Usage

      # Package a single file
      {:ok, zip_path} = Container.package("/path/to/essay.docx", metadata)

      # Package multiple files
      {:ok, zip_path} = Container.package(["/path/to/essay.docx", "/path/to/code.rs"], metadata)

      # Package with existing FHI
      {:ok, zip_path} = Container.package_with_fhi("/path/to/essay.docx", "/path/to/file.fhi")

      # Extract and verify
      {:ok, contents} = Container.extract(zip_path)

      # Add marked document to existing container
      {:ok, zip_path} = Container.add_marked(zip_path, "/path/to/essay_MARKED.docx")
  """

  require Logger

  alias EtmaHandler.Crypto

  @manifest_version "1.0"
  @hash_algorithm :blake3

  # File type categorization
  @document_extensions ~w(.doc .docx .rtf .odt .pdf .txt)
  @code_extensions ~w(.ex .exs .rs .js .ts .py .rb .java .c .cpp .h .go .hs .ml)
  @media_extensions ~w(.png .jpg .jpeg .gif .svg .bmp .webp)
  @spreadsheet_extensions ~w(.xlsx .xls .ods .csv)
  @presentation_extensions ~w(.pptx .ppt .odp)
  @archive_extensions ~w(.zip .tar .gz .7z)

  @type file_category :: :document | :code | :media | :spreadsheet | :presentation | :archive | :other
  @type metadata :: map()

  # ============================================================
  # PUBLIC API
  # ============================================================

  @doc """
  Package one or more files into a standardized ZIP container.

  ## Options

  - `:output_dir` - Where to save the ZIP (default: temp directory)
  - `:student_oucu` - Student's OUCU (for filename)
  - `:course_code` - Course code (for filename)
  - `:tma_number` - TMA number (for filename)
  - `:fhi_path` - Path to existing FHI file to include
  """
  @spec package(Path.t() | [Path.t()], keyword()) :: {:ok, Path.t()} | {:error, term()}
  def package(files, opts \\ [])

  def package(file, opts) when is_binary(file) do
    package([file], opts)
  end

  def package(files, opts) when is_list(files) do
    with :ok <- validate_files_exist(files),
         {:ok, categorized} <- categorize_files(files),
         {:ok, manifest} <- build_manifest(categorized, opts),
         {:ok, zip_path} <- create_zip(categorized, manifest, opts) do
      Logger.info("Created container: #{Path.basename(zip_path)} with #{length(files)} file(s)")
      {:ok, zip_path}
    end
  end

  @doc """
  Package files with an existing FHI metadata file.

  The FHI file provides student/tutor/submission details which are
  merged into our manifest.
  """
  @spec package_with_fhi(Path.t() | [Path.t()], Path.t(), keyword()) ::
          {:ok, Path.t()} | {:error, term()}
  def package_with_fhi(files, fhi_path, opts \\ []) do
    opts = Keyword.put(opts, :fhi_path, fhi_path)
    package(files, opts)
  end

  @doc """
  Extract and verify a container.

  Returns the extracted contents with integrity verification.
  Files are extracted to a temporary directory.
  """
  @spec extract(Path.t(), keyword()) :: {:ok, map()} | {:error, term()}
  def extract(zip_path, opts \\ []) do
    output_dir = Keyword.get(opts, :output_dir, System.tmp_dir!())
    extract_dir = Path.join(output_dir, "etma_extract_#{:erlang.unique_integer([:positive])}")

    with :ok <- File.mkdir_p(extract_dir),
         {:ok, files} <- :zip.unzip(String.to_charlist(zip_path), cwd: String.to_charlist(extract_dir)),
         {:ok, manifest} <- read_manifest(extract_dir),
         :ok <- verify_integrity(extract_dir, manifest) do
      {:ok,
       %{
         path: extract_dir,
         manifest: manifest,
         files: Enum.map(files, &List.to_string/1)
       }}
    else
      {:error, reason} ->
        # Cleanup on failure
        File.rm_rf(extract_dir)
        {:error, reason}
    end
  end

  @doc """
  Add a marked document to an existing container.

  The marked document is placed in the `marked/` directory within the ZIP.
  """
  @spec add_marked(Path.t(), Path.t()) :: {:ok, Path.t()} | {:error, term()}
  def add_marked(zip_path, marked_file) do
    with {:ok, extracted} <- extract(zip_path),
         {:ok, hash} <- compute_hash(marked_file),
         :ok <- copy_to_marked(extracted.path, marked_file),
         {:ok, updated_manifest} <- update_manifest_with_marked(extracted.manifest, marked_file, hash),
         {:ok, new_zip} <- repackage(extracted.path, updated_manifest, zip_path) do
      # Cleanup extraction directory
      File.rm_rf(extracted.path)
      {:ok, new_zip}
    end
  end

  @doc """
  Verify a container's integrity without extracting.
  """
  @spec verify(Path.t()) :: :ok | {:error, term()}
  def verify(zip_path) do
    # Quick check using :zip.list_dir
    case :zip.list_dir(String.to_charlist(zip_path)) do
      {:ok, entries} ->
        has_manifest =
          Enum.any?(entries, fn
            {:zip_file, name, _, _, _, _} -> List.to_string(name) == "manifest.json"
            _ -> false
          end)

        if has_manifest, do: :ok, else: {:error, :missing_manifest}

      {:error, reason} ->
        {:error, {:zip_error, reason}}
    end
  end

  @doc """
  Get container info without full extraction.
  """
  @spec info(Path.t()) :: {:ok, map()} | {:error, term()}
  def info(zip_path) do
    with {:ok, entries} <- :zip.list_dir(String.to_charlist(zip_path)) do
      files =
        entries
        |> Enum.filter(fn
          {:zip_file, _, _, _, _, _} -> true
          _ -> false
        end)
        |> Enum.map(fn {:zip_file, name, info, _, _, _} ->
          %{
            name: List.to_string(name),
            size: elem(info, 0),
            compressed_size: elem(info, 1)
          }
        end)

      {:ok,
       %{
         path: zip_path,
         file_count: length(files),
         files: files,
         total_size: Enum.sum(Enum.map(files, & &1.size))
       }}
    end
  end

  @doc """
  Create a return package for uploading back to eTMA site.
  """
  @spec package_return(Path.t(), keyword()) :: {:ok, Path.t()} | {:error, term()}
  def package_return(container_path, opts \\ []) do
    with {:ok, extracted} <- extract(container_path),
         {:ok, return_zip} <- create_return_zip(extracted, opts) do
      File.rm_rf(extracted.path)
      {:ok, return_zip}
    end
  end

  # ============================================================
  # PRIVATE - FILE CATEGORIZATION
  # ============================================================

  defp validate_files_exist(files) do
    missing = Enum.reject(files, &File.exists?/1)

    case missing do
      [] -> :ok
      _ -> {:error, {:files_not_found, missing}}
    end
  end

  defp categorize_files(files) do
    categorized =
      files
      |> Enum.map(fn path ->
        ext = path |> Path.extname() |> String.downcase()
        {path, categorize_extension(ext)}
      end)
      |> Enum.group_by(fn {_path, category} -> category end, fn {path, _} -> path end)

    {:ok, categorized}
  end

  @spec categorize_extension(String.t()) :: file_category()
  defp categorize_extension(ext) do
    cond do
      ext in @document_extensions -> :document
      ext in @code_extensions -> :code
      ext in @media_extensions -> :media
      ext in @spreadsheet_extensions -> :spreadsheet
      ext in @presentation_extensions -> :presentation
      ext in @archive_extensions -> :archive
      true -> :other
    end
  end

  # ============================================================
  # PRIVATE - MANIFEST
  # ============================================================

  defp build_manifest(categorized, opts) do
    fhi_data =
      case Keyword.get(opts, :fhi_path) do
        nil -> %{}
        path -> parse_fhi_for_manifest(path)
      end

    file_entries =
      categorized
      |> Enum.flat_map(fn {category, files} ->
        Enum.map(files, fn path ->
          {:ok, hash} = compute_hash(path)
          stat = File.stat!(path)

          %{
            original_path: path,
            filename: Path.basename(path),
            category: category,
            container_path: container_path_for(path, category),
            size: stat.size,
            hash: hash,
            hash_algorithm: @hash_algorithm
          }
        end)
      end)

    manifest = %{
      version: @manifest_version,
      created_at: DateTime.utc_now() |> DateTime.to_iso8601(),
      student: Map.get(fhi_data, :student, %{}),
      tutor: Map.get(fhi_data, :tutor, %{}),
      submission: %{
        course_code: Keyword.get(opts, :course_code, Map.get(fhi_data, :course_code)),
        presentation: Keyword.get(opts, :presentation, Map.get(fhi_data, :presentation)),
        tma_number: Keyword.get(opts, :tma_number, Map.get(fhi_data, :tma_number)),
        student_oucu: Keyword.get(opts, :student_oucu, get_in(fhi_data, [:student, :oucu]))
      },
      files: file_entries,
      marked: [],
      status: "unmarked"
    }

    {:ok, manifest}
  end

  defp parse_fhi_for_manifest(fhi_path) do
    case EtmaHandler.FHI.parse_file(fhi_path) do
      {:ok, submission} ->
        %{
          student: submission.student,
          tutor: submission.tutor,
          course_code: submission.course_code,
          presentation: submission.presentation,
          tma_number: submission.tma_number
        }

      {:error, _} ->
        %{}
    end
  end

  defp container_path_for(path, category) do
    filename = Path.basename(path)

    case category do
      :code -> "submission/code/#{filename}"
      :media -> "submission/media/#{filename}"
      _ -> "submission/#{filename}"
    end
  end

  defp read_manifest(extract_dir) do
    manifest_path = Path.join(extract_dir, "manifest.json")

    case File.read(manifest_path) do
      {:ok, content} ->
        case Jason.decode(content, keys: :atoms) do
          {:ok, manifest} -> {:ok, manifest}
          {:error, _} -> {:error, :invalid_manifest}
        end

      {:error, _} ->
        {:error, :missing_manifest}
    end
  end

  defp update_manifest_with_marked(manifest, marked_file, hash) do
    marked_entry = %{
      filename: Path.basename(marked_file),
      container_path: "marked/#{Path.basename(marked_file)}",
      size: File.stat!(marked_file).size,
      hash: hash,
      hash_algorithm: @hash_algorithm,
      added_at: DateTime.utc_now() |> DateTime.to_iso8601()
    }

    updated =
      manifest
      |> Map.update(:marked, [marked_entry], &[marked_entry | &1])
      |> Map.put(:status, "marked")
      |> Map.put(:marked_at, DateTime.utc_now() |> DateTime.to_iso8601())

    {:ok, updated}
  end

  # ============================================================
  # PRIVATE - ZIP CREATION
  # ============================================================

  defp create_zip(categorized, manifest, opts) do
    output_dir = Keyword.get(opts, :output_dir, System.tmp_dir!())
    zip_name = generate_zip_name(manifest)
    zip_path = Path.join(output_dir, zip_name)

    # Build list of files to include
    files_to_zip =
      categorized
      |> Enum.flat_map(fn {_category, files} ->
        Enum.map(files, fn path ->
          entry = Enum.find(manifest.files, fn f -> f.original_path == path end)
          {String.to_charlist(entry.container_path), File.read!(path)}
        end)
      end)

    # Add manifest
    manifest_json = Jason.encode!(manifest, pretty: true)
    files_to_zip = [{'manifest.json', manifest_json} | files_to_zip]

    # Add FHI if present
    files_to_zip =
      case Keyword.get(opts, :fhi_path) do
        nil -> files_to_zip
        fhi_path -> [{'original.fhi', File.read!(fhi_path)} | files_to_zip]
      end

    # Add hash file
    hash_content = generate_hash_file(manifest)
    files_to_zip = [{'hashes.blake3', hash_content} | files_to_zip]

    # Create the ZIP
    case :zip.create(String.to_charlist(zip_path), files_to_zip) do
      {:ok, _} -> {:ok, zip_path}
      {:error, reason} -> {:error, {:zip_create_failed, reason}}
    end
  end

  defp generate_zip_name(manifest) do
    parts =
      [
        manifest.submission[:student_oucu],
        manifest.submission[:course_code],
        manifest.submission[:tma_number]
      ]
      |> Enum.reject(&is_nil/1)

    if Enum.empty?(parts) do
      "submission-#{:erlang.unique_integer([:positive])}.zip"
    else
      "submission-#{Enum.join(parts, "-")}.zip"
    end
  end

  defp generate_hash_file(manifest) do
    manifest.files
    |> Enum.map(fn file ->
      "#{file.hash}  #{file.container_path}"
    end)
    |> Enum.join("\n")
  end

  defp repackage(extract_dir, manifest, original_path) do
    # Write updated manifest
    manifest_path = Path.join(extract_dir, "manifest.json")
    File.write!(manifest_path, Jason.encode!(manifest, pretty: true))

    # Update hash file
    hash_path = Path.join(extract_dir, "hashes.blake3")
    File.write!(hash_path, generate_hash_file(manifest))

    # Recreate ZIP
    files =
      extract_dir
      |> File.ls!()
      |> Enum.flat_map(fn name ->
        full_path = Path.join(extract_dir, name)
        collect_files_for_zip(full_path, name)
      end)

    case :zip.create(String.to_charlist(original_path), files) do
      {:ok, _} -> {:ok, original_path}
      {:error, reason} -> {:error, {:repackage_failed, reason}}
    end
  end

  defp collect_files_for_zip(path, relative_path) do
    if File.dir?(path) do
      path
      |> File.ls!()
      |> Enum.flat_map(fn name ->
        collect_files_for_zip(Path.join(path, name), Path.join(relative_path, name))
      end)
    else
      [{String.to_charlist(relative_path), File.read!(path)}]
    end
  end

  defp copy_to_marked(extract_dir, marked_file) do
    marked_dir = Path.join(extract_dir, "marked")
    File.mkdir_p!(marked_dir)
    dest = Path.join(marked_dir, Path.basename(marked_file))
    File.cp!(marked_file, dest)
    :ok
  end

  defp create_return_zip(extracted, opts) do
    output_dir = Keyword.get(opts, :output_dir, System.tmp_dir!())
    manifest = extracted.manifest

    # Return ZIP should contain marked files + FHI
    marked_dir = Path.join(extracted.path, "marked")
    fhi_path = Path.join(extracted.path, "original.fhi")

    files =
      if File.dir?(marked_dir) do
        marked_dir
        |> File.ls!()
        |> Enum.map(fn name ->
          {String.to_charlist(name), File.read!(Path.join(marked_dir, name))}
        end)
      else
        []
      end

    files =
      if File.exists?(fhi_path) do
        [{String.to_charlist(Path.basename(fhi_path)), File.read!(fhi_path)} | files]
      else
        files
      end

    zip_name = "return-#{manifest.submission[:student_oucu] || "unknown"}.zip"
    zip_path = Path.join(output_dir, zip_name)

    case :zip.create(String.to_charlist(zip_path), files) do
      {:ok, _} -> {:ok, zip_path}
      {:error, reason} -> {:error, {:return_zip_failed, reason}}
    end
  end

  # ============================================================
  # PRIVATE - INTEGRITY
  # ============================================================

  defp compute_hash(path) do
    case File.read(path) do
      {:ok, content} ->
        case Crypto.hash(content) do
          {:ok, hash} -> {:ok, hash}
          _ -> fallback_hash(content)
        end

      {:error, reason} ->
        {:error, {:file_read_error, reason}}
    end
  end

  defp fallback_hash(content) do
    hash =
      :crypto.hash(:sha256, content)
      |> Base.encode16(case: :lower)

    {:ok, hash}
  end

  defp verify_integrity(extract_dir, manifest) do
    errors =
      manifest.files
      |> Enum.map(fn file ->
        full_path = Path.join(extract_dir, file.container_path)

        case compute_hash(full_path) do
          {:ok, computed_hash} ->
            if computed_hash == file.hash do
              :ok
            else
              {:error, {:hash_mismatch, file.container_path}}
            end

          {:error, reason} ->
            {:error, {:hash_compute_failed, file.container_path, reason}}
        end
      end)
      |> Enum.filter(fn
        :ok -> false
        _ -> true
      end)

    case errors do
      [] -> :ok
      _ -> {:error, {:integrity_failures, errors}}
    end
  end
end
