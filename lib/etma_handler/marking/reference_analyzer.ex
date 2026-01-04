# SPDX-License-Identifier: AGPL-3.0-or-later
defmodule EtmaHandler.Marking.ReferenceAnalyzer do
  @moduledoc """
  Academic Reference & Citation Analyzer.

  Parses submitted documents to analyze:

  1. **In-text citations** - Detects Harvard-style (Author, Year) patterns
  2. **Reference list** - Extracts and parses bibliography entries
  3. **Style compliance** - Checks against Cite Them Right / Harvard
  4. **Module materials** - Identifies usage of required readings
  5. **Primary vs Secondary** - Classifies sources by type
  6. **Verification** - Optional lookup via CrossRef/Semantic Scholar

  ## Usage

      # Analyze a document
      {:ok, analysis} = ReferenceAnalyzer.analyze("/path/to/essay.docx")

      # Check against module materials
      {:ok, comparison} = ReferenceAnalyzer.compare_module_materials(
        analysis,
        ["Author2020", "Smith2019"]
      )

  ## Analysis Result Structure

      %{
        citations: [%{text: "(Smith, 2020)", author: "Smith", year: 2020, ...}],
        references: [%{raw: "Smith, J. (2020) Title...", parsed: %{...}}],
        statistics: %{
          total_citations: 16,
          primary_sources: 12,
          secondary_sources: 4,
          module_materials: 6,
          unique_sources: 14
        },
        issues: [
          %{type: :alphabetical, message: "References not in alphabetical order"},
          %{type: :missing_reference, citation: "(Jones, 2019)", message: "Citation not in reference list"}
        ],
        score: %{
          formatting: 85,
          coverage: 90,
          variety: 75
        }
      }
  """

  require Logger

  alias EtmaHandler.Marking.DocumentExtractor

  # Citation patterns for different styles
  @harvard_citation ~r/\(([A-Z][a-zA-Z'-]+(?:\s+(?:and|&)\s+[A-Z][a-zA-Z'-]+)?(?:\s+et\s+al\.?)?),?\s*(\d{4}[a-z]?)(?:,?\s*p\.?\s*(\d+(?:-\d+)?))?\)/
  @harvard_citation_author_date ~r/([A-Z][a-zA-Z'-]+(?:\s+(?:and|&)\s+[A-Z][a-zA-Z'-]+)?(?:\s+et\s+al\.?)?)\s*\((\d{4}[a-z]?)\)/
  @numeric_citation ~r/\[(\d+(?:[-,]\d+)*)\]/
  @footnote_marker ~r/\[\^(\d+)\]|<sup>(\d+)<\/sup>/

  # Reference list patterns
  @reference_start ~r/^(?:references?|bibliography|works?\s+cited|sources?)$/im
  @harvard_reference ~r/^([A-Z][a-zA-Z'-]+(?:,\s*[A-Z]\.?)+)(?:\s+(?:and|&)\s+[A-Z][a-zA-Z'-]+(?:,\s*[A-Z]\.?)+)*\s*\((\d{4}[a-z]?)\)/m

  # Source type indicators
  @journal_indicators ~r/journal|quarterly|review|bulletin|proceedings|transactions/i
  @book_indicators ~r/(?:^[^(]+\(\d{4}\)\s*[^.]+\.\s*(?:\d+(?:st|nd|rd|th)\s+ed\.\s*)?[A-Z][a-z]+:)/
  @website_indicators ~r/available\s+at|retrieved\s+from|accessed|https?:\/\/|www\./i
  @module_indicators ~r/\[online\]|\[ebook\]|open\s+university|ou\s+press/i

  @type citation :: %{
          text: String.t(),
          author: String.t(),
          year: integer() | String.t(),
          page: String.t() | nil,
          position: non_neg_integer(),
          style: :harvard | :numeric | :footnote
        }

  @type bib_reference :: %{
          raw: String.t(),
          author: String.t() | nil,
          year: integer() | String.t() | nil,
          title: String.t() | nil,
          source_type: :journal | :book | :website | :module | :unknown,
          is_primary: boolean()
        }

  @type analysis_result :: %{
          citations: [citation()],
          references: [bib_reference()],
          statistics: map(),
          issues: [map()],
          score: map()
        }

  # ============================================================
  # PUBLIC API
  # ============================================================

  @doc """
  Analyze a document for citations and references.

  ## Options

  - `:style` - Expected citation style (`:harvard`, `:numeric`, `:footnote`)
  - `:module_materials` - List of expected sources from module
  - `:strict` - Enable strict style checking (default: false)
  """
  @spec analyze(Path.t(), keyword()) :: {:ok, analysis_result()} | {:error, term()}
  def analyze(file_path, opts \\ []) do
    with {:ok, text} <- extract_text(file_path),
         {:ok, citations} <- find_citations(text, opts),
         {:ok, references} <- find_references(text, opts) do
      # Build the analysis
      statistics = calculate_statistics(citations, references, opts)
      issues = find_issues(citations, references, opts)
      score = calculate_score(citations, references, issues, opts)

      {:ok,
       %{
         citations: citations,
         references: references,
         statistics: statistics,
         issues: issues,
         score: score
       }}
    end
  end

  @doc """
  Quick check - just counts citations and references.
  """
  @spec quick_count(Path.t()) :: {:ok, map()} | {:error, term()}
  def quick_count(file_path) do
    with {:ok, text} <- extract_text(file_path) do
      citation_count = count_citations(text)
      reference_count = count_references(text)

      {:ok,
       %{
         citations: citation_count,
         references: reference_count,
         balanced: abs(citation_count - reference_count) <= 2
       }}
    end
  end

  @doc """
  Compare analysis against module materials list.
  """
  @spec compare_module_materials(analysis_result(), [String.t()]) :: {:ok, map()}
  def compare_module_materials(analysis, module_materials) do
    # Normalize module materials for matching
    normalized_materials =
      module_materials
      |> Enum.map(&normalize_author_year/1)
      |> MapSet.new()

    # Check which citations/references match module materials
    matched_citations =
      analysis.citations
      |> Enum.filter(fn c ->
        normalized = normalize_author_year("#{c.author}#{c.year}")
        MapSet.member?(normalized_materials, normalized)
      end)

    matched_references =
      analysis.references
      |> Enum.filter(fn r ->
        normalized = normalize_author_year("#{r.author}#{r.year}")
        MapSet.member?(normalized_materials, normalized)
      end)

    # Find materials not used
    used_keys =
      (matched_citations ++ matched_references)
      |> Enum.map(fn item ->
        normalize_author_year("#{item.author || item[:author]}#{item.year || item[:year]}")
      end)
      |> MapSet.new()

    unused_materials =
      normalized_materials
      |> MapSet.difference(used_keys)
      |> MapSet.to_list()

    {:ok,
     %{
       module_materials_used: length(matched_citations),
       module_materials_total: length(module_materials),
       coverage_percentage: coverage_percent(matched_citations, module_materials),
       unused_materials: unused_materials,
       matched_citations: matched_citations,
       matched_references: matched_references
     }}
  end

  @doc """
  Check if references are in alphabetical order.
  """
  @spec check_alphabetical(analysis_result()) :: {:ok, boolean()} | {:error, [map()]}
  def check_alphabetical(analysis) do
    authors =
      analysis.references
      |> Enum.map(& &1.author)
      |> Enum.filter(&(&1 != nil))

    sorted = Enum.sort(authors, &(String.downcase(&1) <= String.downcase(&2)))

    if authors == sorted do
      {:ok, true}
    else
      # Find which ones are out of order
      out_of_order =
        Enum.zip(authors, sorted)
        |> Enum.with_index()
        |> Enum.filter(fn {{a, b}, _} -> a != b end)
        |> Enum.map(fn {{actual, expected}, idx} ->
          %{
            position: idx + 1,
            found: actual,
            expected: expected,
            message: "Position #{idx + 1}: Found '#{actual}' but expected '#{expected}'"
          }
        end)

      {:error, out_of_order}
    end
  end

  @doc """
  Find citations that don't have matching references.
  """
  @spec find_orphan_citations(analysis_result()) :: [citation()]
  def find_orphan_citations(analysis) do
    reference_keys =
      analysis.references
      |> Enum.map(fn r -> normalize_author_year("#{r.author}#{r.year}") end)
      |> MapSet.new()

    analysis.citations
    |> Enum.reject(fn c ->
      key = normalize_author_year("#{c.author}#{c.year}")
      MapSet.member?(reference_keys, key)
    end)
  end

  @doc """
  Find references that aren't cited in the text.
  """
  @spec find_uncited_references(analysis_result()) :: [reference()]
  def find_uncited_references(analysis) do
    citation_keys =
      analysis.citations
      |> Enum.map(fn c -> normalize_author_year("#{c.author}#{c.year}") end)
      |> MapSet.new()

    analysis.references
    |> Enum.reject(fn r ->
      key = normalize_author_year("#{r.author}#{r.year}")
      MapSet.member?(citation_keys, key)
    end)
  end

  # ============================================================
  # PRIVATE - TEXT EXTRACTION
  # ============================================================

  defp extract_text(file_path) do
    ext = file_path |> Path.extname() |> String.downcase()

    case ext do
      ".txt" ->
        File.read(file_path)

      ".docx" ->
        extract_docx_text(file_path)

      ".rtf" ->
        extract_rtf_text(file_path)

      ".odt" ->
        extract_odt_text(file_path)

      ".pdf" ->
        extract_pdf_text(file_path)

      _ ->
        {:error, {:unsupported_format, ext}}
    end
  end

  defp extract_docx_text(path) do
    # DOCX is a ZIP containing XML
    case :zip.unzip(String.to_charlist(path), [:memory]) do
      {:ok, files} ->
        # Find document.xml
        case Enum.find(files, fn {name, _} ->
               List.to_string(name) == "word/document.xml"
             end) do
          {_, content} ->
            # Strip XML tags to get text
            text =
              content
              |> to_string()
              |> String.replace(~r/<w:p[^>]*>/, "\n")
              |> String.replace(~r/<[^>]+>/, "")
              |> String.replace(~r/&[a-z]+;/, " ")
              |> String.trim()

            {:ok, text}

          nil ->
            {:error, :no_document_xml}
        end

      {:error, reason} ->
        {:error, {:zip_error, reason}}
    end
  end

  defp extract_rtf_text(path) do
    case File.read(path) do
      {:ok, content} ->
        # Basic RTF to text conversion
        text =
          content
          |> String.replace(~r/\\[a-z]+\d*\s?/, " ")
          |> String.replace(~r/\{|\}/, "")
          |> String.replace(~r/\\'[0-9a-f]{2}/i, "")
          |> String.trim()

        {:ok, text}

      error ->
        error
    end
  end

  defp extract_odt_text(path) do
    # ODT is also a ZIP containing XML
    case :zip.unzip(String.to_charlist(path), [:memory]) do
      {:ok, files} ->
        case Enum.find(files, fn {name, _} ->
               List.to_string(name) == "content.xml"
             end) do
          {_, content} ->
            text =
              content
              |> to_string()
              |> String.replace(~r/<text:p[^>]*>/, "\n")
              |> String.replace(~r/<[^>]+>/, "")
              |> String.trim()

            {:ok, text}

          nil ->
            {:error, :no_content_xml}
        end

      {:error, reason} ->
        {:error, {:zip_error, reason}}
    end
  end

  defp extract_pdf_text(_path) do
    # PDF text extraction is complex - would need external tool
    # For now, return an error suggesting OCR or pdftotext
    {:error, :pdf_extraction_not_implemented}
  end

  # ============================================================
  # PRIVATE - CITATION DETECTION
  # ============================================================

  defp find_citations(text, opts) do
    style = Keyword.get(opts, :style, :harvard)

    citations =
      case style do
        :harvard -> find_harvard_citations(text)
        :numeric -> find_numeric_citations(text)
        :footnote -> find_footnote_citations(text)
        _ -> find_harvard_citations(text)
      end

    {:ok, citations}
  end

  defp find_harvard_citations(text) do
    # Find (Author, Year) and Author (Year) patterns
    parenthetical = find_parenthetical_citations(text)
    narrative = find_narrative_citations(text)

    (parenthetical ++ narrative)
    |> Enum.uniq_by(fn c -> {c.author, c.year, c.position} end)
    |> Enum.sort_by(& &1.position)
  end

  defp find_parenthetical_citations(text) do
    Regex.scan(@harvard_citation, text, return: :index)
    |> Enum.zip(Regex.scan(@harvard_citation, text))
    |> Enum.map(fn {[{pos, len} | _], [_full, author, year | rest]} ->
      page = if length(rest) > 0, do: hd(rest), else: nil

      %{
        text: String.slice(text, pos, len),
        author: String.trim(author),
        year: parse_year(year),
        page: page,
        position: pos,
        style: :harvard,
        citation_type: :parenthetical
      }
    end)
  end

  defp find_narrative_citations(text) do
    Regex.scan(@harvard_citation_author_date, text, return: :index)
    |> Enum.zip(Regex.scan(@harvard_citation_author_date, text))
    |> Enum.map(fn {[{pos, len} | _], [_full, author, year]} ->
      %{
        text: String.slice(text, pos, len),
        author: String.trim(author),
        year: parse_year(year),
        page: nil,
        position: pos,
        style: :harvard,
        citation_type: :narrative
      }
    end)
  end

  defp find_numeric_citations(text) do
    Regex.scan(@numeric_citation, text, return: :index)
    |> Enum.zip(Regex.scan(@numeric_citation, text))
    |> Enum.map(fn {[{pos, len} | _], [_full, numbers]} ->
      %{
        text: String.slice(text, pos, len),
        numbers: parse_citation_numbers(numbers),
        position: pos,
        style: :numeric
      }
    end)
  end

  defp find_footnote_citations(text) do
    Regex.scan(@footnote_marker, text, return: :index)
    |> Enum.zip(Regex.scan(@footnote_marker, text))
    |> Enum.map(fn {[{pos, len} | _], matches} ->
      number = Enum.find(matches, &(&1 != nil && &1 != ""))

      %{
        text: String.slice(text, pos, len),
        number: String.to_integer(number || "0"),
        position: pos,
        style: :footnote
      }
    end)
  end

  defp count_citations(text) do
    harvard = length(Regex.scan(@harvard_citation, text))
    numeric = length(Regex.scan(@numeric_citation, text))
    harvard + numeric
  end

  # ============================================================
  # PRIVATE - REFERENCE LIST PARSING
  # ============================================================

  defp find_references(text, _opts) do
    # Find the references section
    case find_reference_section(text) do
      {:ok, section} ->
        references = parse_reference_entries(section)
        {:ok, references}

      :not_found ->
        {:ok, []}
    end
  end

  defp find_reference_section(text) do
    # Split by common reference section headers
    case Regex.split(@reference_start, text, parts: 2) do
      [_, section] -> {:ok, section}
      _ -> :not_found
    end
  end

  defp parse_reference_entries(section) do
    # Split by newlines and parse each entry
    section
    |> String.split(~r/\n{2,}|\r\n{2,}/)
    |> Enum.filter(&(String.length(String.trim(&1)) > 20))
    |> Enum.map(&parse_single_reference/1)
    |> Enum.filter(&(&1 != nil))
  end

  defp parse_single_reference(entry) do
    entry = String.trim(entry)

    # Try Harvard format: Author, A. (Year) Title...
    case Regex.run(@harvard_reference, entry) do
      [_, author, year] ->
        %{
          raw: entry,
          author: clean_author(author),
          year: parse_year(year),
          title: extract_title(entry),
          source_type: detect_source_type(entry),
          is_primary: is_primary_source?(entry)
        }

      nil ->
        # Try to extract what we can
        %{
          raw: entry,
          author: nil,
          year: extract_year_anywhere(entry),
          title: nil,
          source_type: detect_source_type(entry),
          is_primary: is_primary_source?(entry)
        }
    end
  end

  defp count_references(text) do
    case find_reference_section(text) do
      {:ok, section} ->
        section
        |> String.split(~r/\n{2,}/)
        |> Enum.count(&(String.length(String.trim(&1)) > 20))

      :not_found ->
        0
    end
  end

  # ============================================================
  # PRIVATE - ANALYSIS HELPERS
  # ============================================================

  defp calculate_statistics(citations, references, opts) do
    module_materials = Keyword.get(opts, :module_materials, [])

    primary = Enum.count(references, & &1.is_primary)
    secondary = length(references) - primary

    module_used =
      if module_materials != [] do
        references
        |> Enum.count(fn r ->
          key = normalize_author_year("#{r.author}#{r.year}")
          Enum.any?(module_materials, &(normalize_author_year(&1) == key))
        end)
      else
        0
      end

    unique_authors =
      citations
      |> Enum.map(& &1.author)
      |> Enum.uniq()
      |> length()

    %{
      total_citations: length(citations),
      total_references: length(references),
      primary_sources: primary,
      secondary_sources: secondary,
      module_materials_used: module_used,
      unique_sources: unique_authors,
      citation_density: citation_density(citations),
      source_variety: source_variety(references)
    }
  end

  defp find_issues(citations, references, opts) do
    issues = []

    # Check for orphan citations
    orphans = find_orphan_citations(%{citations: citations, references: references})

    issues =
      if orphans != [] do
        orphan_issues =
          Enum.map(orphans, fn c ->
            %{
              type: :missing_reference,
              citation: c.text,
              message: "Citation #{c.text} has no matching reference"
            }
          end)

        issues ++ orphan_issues
      else
        issues
      end

    # Check alphabetical order
    issues =
      case check_alphabetical(%{references: references}) do
        {:ok, true} ->
          issues

        {:error, order_issues} ->
          issues ++
            [
              %{
                type: :alphabetical_order,
                details: order_issues,
                message: "References are not in alphabetical order"
              }
            ]
      end

    # Check for module material usage
    module_materials = Keyword.get(opts, :module_materials, [])

    issues =
      if module_materials != [] do
        {:ok, comparison} =
          compare_module_materials(%{citations: citations, references: references}, module_materials)

        if comparison.unused_materials != [] do
          issues ++
            [
              %{
                type: :unused_module_materials,
                materials: comparison.unused_materials,
                message:
                  "#{length(comparison.unused_materials)} module materials not referenced: #{Enum.join(comparison.unused_materials, ", ")}"
              }
            ]
        else
          issues
        end
      else
        issues
      end

    issues
  end

  defp calculate_score(citations, references, issues, _opts) do
    # Formatting score (deduct for issues)
    formatting_base = 100
    formatting_deductions = length(issues) * 5
    formatting = max(0, formatting_base - formatting_deductions)

    # Coverage score (based on citation-reference match)
    orphans = find_orphan_citations(%{citations: citations, references: references})
    uncited = find_uncited_references(%{citations: citations, references: references})
    coverage = if length(citations) > 0 do
      matched = length(citations) - length(orphans)
      round(matched / length(citations) * 100)
    else
      0
    end

    # Variety score (based on unique sources)
    unique = citations |> Enum.map(& &1.author) |> Enum.uniq() |> length()
    variety = if length(citations) > 0 do
      round(unique / length(citations) * 100)
    else
      0
    end

    %{
      formatting: formatting,
      coverage: coverage,
      variety: min(variety, 100),
      overall: round((formatting + coverage + variety) / 3),
      uncited_references: length(uncited)
    }
  end

  # ============================================================
  # PRIVATE - UTILITY FUNCTIONS
  # ============================================================

  defp parse_year(year_str) when is_binary(year_str) do
    case Integer.parse(year_str) do
      {year, _} -> year
      :error -> year_str
    end
  end

  defp parse_year(year), do: year

  defp parse_citation_numbers(numbers) do
    numbers
    |> String.split(~r/[,\s]+/)
    |> Enum.flat_map(fn part ->
      case String.split(part, "-") do
        [start, finish] ->
          s = String.to_integer(start)
          f = String.to_integer(finish)
          Enum.to_list(s..f)

        [single] ->
          [String.to_integer(single)]
      end
    end)
  end

  defp clean_author(author) do
    author
    |> String.replace(~r/,\s*[A-Z]\.?\s*$/, "")
    |> String.trim()
  end

  defp extract_title(entry) do
    # Try to find title after year in parentheses
    case Regex.run(~r/\(\d{4}[a-z]?\)\s*['"]?([^.]+)/, entry) do
      [_, title] -> String.trim(title)
      nil -> nil
    end
  end

  defp extract_year_anywhere(text) do
    case Regex.run(~r/\((\d{4}[a-z]?)\)/, text) do
      [_, year] -> parse_year(year)
      nil -> nil
    end
  end

  defp detect_source_type(entry) do
    cond do
      Regex.match?(@journal_indicators, entry) -> :journal
      Regex.match?(@book_indicators, entry) -> :book
      Regex.match?(@website_indicators, entry) -> :website
      Regex.match?(@module_indicators, entry) -> :module
      true -> :unknown
    end
  end

  defp is_primary_source?(entry) do
    # Primary sources are usually journal articles, original research
    # Secondary sources are textbooks, reviews, websites
    source_type = detect_source_type(entry)
    source_type == :journal or Regex.match?(~r/original|empirical|study|experiment/i, entry)
  end

  defp normalize_author_year(text) do
    text
    |> String.downcase()
    |> String.replace(~r/[^a-z0-9]/, "")
  end

  defp coverage_percent(matched, total) when is_list(matched) and is_list(total) do
    if length(total) > 0 do
      round(length(matched) / length(total) * 100)
    else
      0
    end
  end

  defp citation_density(citations) do
    # Citations per 1000 characters would need text length
    # For now, just return count
    length(citations)
  end

  defp source_variety(references) do
    types =
      references
      |> Enum.map(& &1.source_type)
      |> Enum.uniq()

    length(types)
  end
end
