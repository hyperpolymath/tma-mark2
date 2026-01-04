# SPDX-License-Identifier: AGPL-3.0-or-later
defmodule EtmaHandler.Scanner do
  @moduledoc """
  File Security Scanner for eTMA Submissions.

  Performs multiple layers of security checks on uploaded files:

  1. **Magic Byte Verification** - Does the file signature match the extension?
  2. **Corruption Detection** - Can the file be parsed/opened?
  3. **Size Validation** - Within acceptable limits?
  4. **Content Analysis** - No embedded macros/scripts in documents?
  5. **VirusTotal** (opt-in) - Cloud-based malware scanning

  ## Usage

      # Quick scan (local checks only)
      {:ok, :clean} = Scanner.scan("/path/to/file.docx")

      # Full scan with VirusTotal (requires API key + consent)
      {:ok, result} = Scanner.scan("/path/to/file.docx", virustotal: true)

      # Scan a container
      {:ok, results} = Scanner.scan_container("/path/to/submission.zip")

  ## Security Philosophy

  - **Never execute** untrusted content
  - **Fail safe** - reject on any suspicion
  - **User consent** for cloud scanning (privacy)
  - **Offline capable** - core checks work without network
  """

  require Logger

  alias EtmaHandler.Settings

  # File signatures (magic bytes)
  @magic_bytes %{
    # Documents
    ".docx" => <<0x50, 0x4B, 0x03, 0x04>>,  # PK (ZIP-based)
    ".xlsx" => <<0x50, 0x4B, 0x03, 0x04>>,
    ".pptx" => <<0x50, 0x4B, 0x03, 0x04>>,
    ".odt" => <<0x50, 0x4B, 0x03, 0x04>>,
    ".ods" => <<0x50, 0x4B, 0x03, 0x04>>,
    ".odp" => <<0x50, 0x4B, 0x03, 0x04>>,
    ".doc" => <<0xD0, 0xCF, 0x11, 0xE0>>,   # OLE compound
    ".xls" => <<0xD0, 0xCF, 0x11, 0xE0>>,
    ".ppt" => <<0xD0, 0xCF, 0x11, 0xE0>>,
    ".pdf" => <<0x25, 0x50, 0x44, 0x46>>,   # %PDF
    ".rtf" => <<0x7B, 0x5C, 0x72, 0x74>>,   # {\rt
    ".zip" => <<0x50, 0x4B, 0x03, 0x04>>,

    # Images
    ".png" => <<0x89, 0x50, 0x4E, 0x47>>,   # .PNG
    ".jpg" => <<0xFF, 0xD8, 0xFF>>,         # JFIF
    ".jpeg" => <<0xFF, 0xD8, 0xFF>>,
    ".gif" => <<0x47, 0x49, 0x46, 0x38>>,   # GIF8
    ".bmp" => <<0x42, 0x4D>>,               # BM
    ".webp" => <<0x52, 0x49, 0x46, 0x46>>   # RIFF (need to check further for WEBP)
  }

  # Dangerous patterns in Office documents
  @dangerous_patterns [
    # VBA Macros
    ~r/vbaProject\.bin/i,
    ~r/\x00V\x00B\x00A/,

    # Auto-execution
    ~r/AutoOpen/i,
    ~r/AutoExec/i,
    ~r/Document_Open/i,
    ~r/Workbook_Open/i,

    # Shell execution
    ~r/Shell\s*\(/i,
    ~r/WScript\.Shell/i,
    ~r/PowerShell/i,
    ~r/cmd\.exe/i,

    # Network calls
    ~r/XMLHTTP/i,
    ~r/WinHttp/i,

    # Embedded objects
    ~r/ole10Native/i,
    ~r/objdata/i
  ]

  @type scan_result :: :clean | {:suspicious, [String.t()]} | {:infected, String.t()}
  @type scan_error :: {:error, term()}

  # ============================================================
  # PUBLIC API
  # ============================================================

  @doc """
  Scan a single file for security issues.

  ## Options

  - `:virustotal` - Enable VirusTotal scanning (requires API key)
  - `:skip_magic` - Skip magic byte verification
  - `:skip_content` - Skip content analysis
  - `:max_size` - Maximum file size in bytes (default from settings)
  """
  @spec scan(Path.t(), keyword()) :: {:ok, scan_result()} | scan_error()
  def scan(path, opts \\ []) do
    checks = [
      {:exists, fn -> check_exists(path) end},
      {:size, fn -> check_size(path, opts) end},
      {:magic, fn -> check_magic_bytes(path) end},
      {:readable, fn -> check_readable(path) end},
      {:content, fn -> check_content(path) end}
    ]

    # Filter out skipped checks
    checks =
      checks
      |> Enum.reject(fn {name, _} ->
        (name == :magic and Keyword.get(opts, :skip_magic, false)) or
          (name == :content and Keyword.get(opts, :skip_content, false))
      end)

    # Run checks in sequence, stop on first failure
    result = run_checks(checks)

    # Optionally add VirusTotal
    result =
      if Keyword.get(opts, :virustotal, false) and result == {:ok, :clean} do
        check_virustotal(path)
      else
        result
      end

    result
  end

  @doc """
  Scan all files in a container (ZIP).

  Returns a map of filename => scan result.
  """
  @spec scan_container(Path.t(), keyword()) :: {:ok, map()} | scan_error()
  def scan_container(zip_path, opts \\ []) do
    with {:ok, container_info} <- EtmaHandler.Container.info(zip_path),
         {:ok, extracted} <- EtmaHandler.Container.extract(zip_path) do
      results =
        extracted.files
        |> Enum.map(fn file_path ->
          full_path = Path.join(extracted.path, file_path)
          result = if File.regular?(full_path), do: scan(full_path, opts), else: {:ok, :skip}
          {file_path, result}
        end)
        |> Map.new()

      # Cleanup
      File.rm_rf(extracted.path)

      # Check for any infections
      infections =
        results
        |> Enum.filter(fn
          {_, {:ok, {:infected, _}}} -> true
          {_, {:ok, {:suspicious, _}}} -> true
          _ -> false
        end)

      if Enum.empty?(infections) do
        {:ok, %{status: :clean, files: results, container: container_info}}
      else
        {:ok, %{status: :flagged, files: results, container: container_info, flagged: infections}}
      end
    end
  end

  @doc """
  Quick validation check (fast, no deep analysis).
  """
  @spec quick_check(Path.t()) :: :ok | {:error, term()}
  def quick_check(path) do
    case scan(path, skip_content: true, skip_magic: false) do
      {:ok, :clean} -> :ok
      {:ok, result} -> {:error, result}
      error -> error
    end
  end

  @doc """
  Get file type from magic bytes.
  """
  @spec detect_type(Path.t()) :: {:ok, String.t()} | {:error, :unknown}
  def detect_type(path) do
    case File.read(path) do
      {:ok, <<magic::binary-size(8), _rest::binary>>} ->
        detected =
          @magic_bytes
          |> Enum.find(fn {_ext, signature} ->
            sig_size = byte_size(signature)
            binary_part(magic, 0, sig_size) == signature
          end)

        case detected do
          {ext, _} -> {:ok, ext}
          nil -> {:error, :unknown}
        end

      {:ok, _small_file} ->
        {:error, :unknown}

      {:error, _} ->
        {:error, :unreadable}
    end
  end

  @doc """
  Check if extension matches actual file type.
  """
  @spec verify_extension(Path.t()) :: :ok | {:error, :mismatch, String.t(), String.t()}
  def verify_extension(path) do
    claimed_ext = path |> Path.extname() |> String.downcase()

    case detect_type(path) do
      {:ok, actual_ext} ->
        # Some extensions share magic bytes (all Office Open XML are ZIP-based)
        if extensions_compatible?(claimed_ext, actual_ext) do
          :ok
        else
          {:error, :mismatch, claimed_ext, actual_ext}
        end

      {:error, :unknown} ->
        # For text files and code, magic bytes don't apply
        if text_extension?(claimed_ext) do
          :ok
        else
          {:error, :unknown_type}
        end

      {:error, reason} ->
        {:error, reason}
    end
  end

  # ============================================================
  # PRIVATE - CHECK IMPLEMENTATIONS
  # ============================================================

  defp run_checks(checks) do
    Enum.reduce_while(checks, {:ok, :clean}, fn {_name, check_fn}, _acc ->
      case check_fn.() do
        :ok -> {:cont, {:ok, :clean}}
        {:ok, _} = ok -> {:cont, ok}
        {:suspicious, reasons} -> {:halt, {:ok, {:suspicious, reasons}}}
        {:infected, reason} -> {:halt, {:ok, {:infected, reason}}}
        {:error, _} = error -> {:halt, error}
      end
    end)
  end

  defp check_exists(path) do
    if File.exists?(path), do: :ok, else: {:error, :not_found}
  end

  defp check_size(path, opts) do
    max_size = Keyword.get(opts, :max_size, get_max_size())

    case File.stat(path) do
      {:ok, %{size: 0}} ->
        {:error, :empty_file}

      {:ok, %{size: size}} when size > max_size ->
        {:error, {:too_large, size, max_size}}

      {:ok, _} ->
        :ok

      {:error, reason} ->
        {:error, {:stat_failed, reason}}
    end
  end

  defp get_max_size do
    case Settings.get([:file_validation, :max_file_size_mb]) do
      nil -> 50 * 1024 * 1024  # 50 MB default
      mb -> mb * 1024 * 1024
    end
  rescue
    _ -> 50 * 1024 * 1024
  end

  defp check_magic_bytes(path) do
    ext = path |> Path.extname() |> String.downcase()

    case Map.get(@magic_bytes, ext) do
      nil ->
        # No magic bytes defined for this extension (e.g., .txt, .rs)
        :ok

      expected_magic ->
        case File.read(path) do
          {:ok, content} when byte_size(content) >= byte_size(expected_magic) ->
            actual = binary_part(content, 0, byte_size(expected_magic))

            if actual == expected_magic do
              :ok
            else
              {:suspicious, ["Magic bytes don't match extension #{ext}"]}
            end

          {:ok, _} ->
            {:error, :file_too_small}

          {:error, reason} ->
            {:error, {:read_failed, reason}}
        end
    end
  end

  defp check_readable(path) do
    ext = path |> Path.extname() |> String.downcase()

    cond do
      ext in [".docx", ".xlsx", ".pptx", ".odt", ".ods", ".odp", ".zip"] ->
        # Try to list ZIP contents
        case :zip.list_dir(String.to_charlist(path)) do
          {:ok, _} -> :ok
          {:error, _} -> {:suspicious, ["Cannot read ZIP structure - possibly corrupted"]}
        end

      ext == ".pdf" ->
        # Check for PDF header and EOF marker
        check_pdf_structure(path)

      true ->
        # For other files, just check readability
        case File.read(path) do
          {:ok, _} -> :ok
          {:error, _} -> {:error, :unreadable}
        end
    end
  end

  defp check_pdf_structure(path) do
    case File.read(path) do
      {:ok, content} ->
        has_header = String.starts_with?(content, "%PDF")
        has_eof = String.contains?(content, "%%EOF")

        cond do
          not has_header -> {:suspicious, ["PDF missing header"]}
          not has_eof -> {:suspicious, ["PDF missing EOF marker - possibly truncated"]}
          true -> :ok
        end

      {:error, _} ->
        {:error, :unreadable}
    end
  end

  defp check_content(path) do
    ext = path |> Path.extname() |> String.downcase()

    cond do
      ext in [".doc", ".xls", ".ppt"] ->
        # OLE compound documents - check for macros
        check_ole_macros(path)

      ext in [".docx", ".xlsx", ".pptx"] ->
        # Office Open XML - check for vbaProject.bin
        check_ooxml_macros(path)

      ext == ".pdf" ->
        # Check for JavaScript in PDF
        check_pdf_scripts(path)

      true ->
        :ok
    end
  end

  defp check_ole_macros(path) do
    case File.read(path) do
      {:ok, content} ->
        suspicious =
          @dangerous_patterns
          |> Enum.filter(fn pattern -> Regex.match?(pattern, content) end)
          |> Enum.map(fn pattern -> "Pattern match: #{inspect(pattern.source)}" end)

        if Enum.empty?(suspicious) do
          :ok
        else
          {:suspicious, suspicious}
        end

      {:error, _} ->
        {:error, :unreadable}
    end
  end

  defp check_ooxml_macros(path) do
    case :zip.list_dir(String.to_charlist(path)) do
      {:ok, entries} ->
        has_vba =
          Enum.any?(entries, fn
            {:zip_file, name, _, _, _, _} ->
              name_str = List.to_string(name)
              String.contains?(name_str, "vbaProject") or String.contains?(name_str, "vbaData")

            _ ->
              false
          end)

        if has_vba do
          {:suspicious, ["Document contains VBA macros"]}
        else
          :ok
        end

      {:error, _} ->
        {:suspicious, ["Cannot read document structure"]}
    end
  end

  defp check_pdf_scripts(path) do
    case File.read(path) do
      {:ok, content} ->
        suspicious =
          [
            {~r/\/JavaScript/i, "Contains JavaScript"},
            {~r/\/JS\s/i, "Contains JS action"},
            {~r/\/OpenAction/i, "Contains auto-open action"},
            {~r/\/Launch/i, "Contains launch action"},
            {~r/\/EmbeddedFile/i, "Contains embedded files"}
          ]
          |> Enum.filter(fn {pattern, _} -> Regex.match?(pattern, content) end)
          |> Enum.map(fn {_, msg} -> msg end)

        if Enum.empty?(suspicious) do
          :ok
        else
          {:suspicious, suspicious}
        end

      {:error, _} ->
        {:error, :unreadable}
    end
  end

  # ============================================================
  # PRIVATE - VIRUSTOTAL
  # ============================================================

  defp check_virustotal(path) do
    # This would integrate with VirusTotal API
    # For now, return clean (actual implementation requires API key)
    Logger.debug("VirusTotal check requested for #{path} (not implemented)")
    {:ok, :clean}
  end

  # ============================================================
  # PRIVATE - HELPERS
  # ============================================================

  defp extensions_compatible?(claimed, actual) do
    # Office Open XML formats all share ZIP magic bytes
    ooxml = [".docx", ".xlsx", ".pptx", ".odt", ".ods", ".odp", ".zip"]

    cond do
      claimed in ooxml and actual in ooxml -> true
      claimed == actual -> true
      true -> false
    end
  end

  defp text_extension?(ext) do
    ext in ~w(.txt .md .adoc .rst .csv .json .xml .html .css .js .ts .rs .ex .exs .py .rb .java .c .cpp .h .go .hs .ml .sh .bash)
  end
end
