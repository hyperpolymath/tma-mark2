# SPDX-License-Identifier: AGPL-3.0-or-later
defmodule EtmaHandler.Marking.DocumentAnalyzer do
  @moduledoc """
  Document Structure & Formatting Analyzer.

  Performs comprehensive analysis of submitted documents:

  1. **Word Count** - Accurate count with margin tolerance
  2. **Readability** - Flesch-Kincaid, Gunning Fog, average sentence length
  3. **Structure** - Paragraphs, headings, questions, sections
  4. **Formatting** - Font consistency, spacing, margins, heading styles
  5. **Track Changes** - Detect if student left track changes on
  6. **Comments** - Verify tutor comments present before return

  ## Usage

      # Full analysis
      {:ok, analysis} = DocumentAnalyzer.analyze("/path/to/essay.docx", word_limit: 2000)

      # Quick checks
      {:ok, word_count} = DocumentAnalyzer.word_count("/path/to/essay.docx")
      {:ok, readability} = DocumentAnalyzer.readability("/path/to/essay.docx")

      # Pre-return checks for marked documents
      {:ok, :ready} = DocumentAnalyzer.check_marked_document("/path/to/marked.docx")

  ## University Style Guidelines

  Default OU style requirements (configurable):
  - Font: Arial 11pt or Times New Roman 12pt
  - Line spacing: 1.5 or Double
  - Margins: 2.54cm (1 inch) all sides
  - Paragraph spacing: 12pt after
  - Headings: Consistent hierarchy (H1, H2, H3)
  """

  require Logger

  require SweetXml
  import SweetXml, only: [sigil_x: 2, xpath: 2, xpath: 3]

  # Reading level calculations
  @syllable_patterns [
    {~r/(?:[^laeiouy]es|ed|[^laeiouy]e)$/, -1},
    {~r/^y/, 0},
    {~r/[aeiouy]{1,2}/, 1}
  ]

  # Default university style
  @default_style %{
    fonts: ["Arial", "Times New Roman", "Calibri"],
    font_size_body: [11, 12],
    font_size_heading: [12, 14, 16, 18],
    line_spacing: [1.5, 2.0],
    margin_cm: 2.54,
    paragraph_spacing_pt: 12,
    word_tolerance_percent: 10
  }

  @type analysis_result :: %{
          word_count: map(),
          readability: map(),
          structure: map(),
          formatting: map(),
          issues: [map()],
          compliance: map()
        }

  # ============================================================
  # PUBLIC API
  # ============================================================

  @doc """
  Perform full document analysis.

  ## Options

  - `:word_limit` - Expected word count limit
  - `:style` - Custom style requirements map
  - `:strict` - Enable strict formatting checks
  """
  @spec analyze(Path.t(), keyword()) :: {:ok, analysis_result()} | {:error, term()}
  def analyze(file_path, opts \\ []) do
    with {:ok, doc} <- parse_document(file_path),
         {:ok, text} <- extract_text(doc),
         {:ok, structure} <- analyze_structure(doc),
         {:ok, formatting} <- analyze_formatting(doc),
         {:ok, word_data} <- analyze_word_count(text, opts),
         {:ok, readability} <- analyze_readability(text) do
      issues = find_issues(word_data, readability, structure, formatting, opts)
      compliance = calculate_compliance(formatting, opts)

      {:ok,
       %{
         word_count: word_data,
         readability: readability,
         structure: structure,
         formatting: formatting,
         issues: issues,
         compliance: compliance
       }}
    end
  end

  @doc """
  Get accurate word count.
  """
  @spec word_count(Path.t()) :: {:ok, map()} | {:error, term()}
  def word_count(file_path) do
    with {:ok, doc} <- parse_document(file_path),
         {:ok, text} <- extract_text(doc) do
      analyze_word_count(text, [])
    end
  end

  @doc """
  Calculate readability metrics.
  """
  @spec readability(Path.t()) :: {:ok, map()} | {:error, term()}
  def readability(file_path) do
    with {:ok, doc} <- parse_document(file_path),
         {:ok, text} <- extract_text(doc) do
      analyze_readability(text)
    end
  end

  @doc """
  Check if a marked document is ready to return to student.

  Verifies:
  - Comments are present (tutor feedback)
  - Track changes are resolved or turned off
  - Document is not corrupted
  """
  @spec check_marked_document(Path.t()) :: {:ok, :ready} | {:error, [map()]}
  def check_marked_document(file_path) do
    with {:ok, doc} <- parse_document(file_path) do
      issues = []

      # Check for comments
      issues =
        case count_comments(doc) do
          0 ->
            [
              %{
                type: :no_comments,
                severity: :warning,
                message: "Document has no comments. Did you forget to add feedback?"
              }
              | issues
            ]

          _ ->
            issues
        end

      # Check for unresolved track changes
      issues =
        case check_track_changes(doc) do
          {:ok, :none} ->
            issues

          {:ok, :present} ->
            [
              %{
                type: :track_changes_present,
                severity: :info,
                message: "Track changes are present in the document"
              }
              | issues
            ]

          {:ok, :unresolved} ->
            [
              %{
                type: :unresolved_changes,
                severity: :warning,
                message: "Document has unresolved track changes"
              }
              | issues
            ]
        end

      if Enum.any?(issues, &(&1.severity == :error)) do
        {:error, issues}
      else
        {:ok, :ready}
      end
    end
  end

  @doc """
  Check if a student submission has track changes that should be removed.
  """
  @spec check_student_submission(Path.t()) :: {:ok, :clean} | {:error, [map()]}
  def check_student_submission(file_path) do
    with {:ok, doc} <- parse_document(file_path) do
      issues = []

      # Check for track changes (students shouldn't submit with these)
      issues =
        case check_track_changes(doc) do
          {:ok, :none} ->
            issues

          {:ok, _} ->
            [
              %{
                type: :has_track_changes,
                severity: :warning,
                message: "Document contains track changes. Please accept/reject all changes before submitting."
              }
              | issues
            ]
        end

      # Check for comments (students might leave personal notes)
      issues =
        case count_comments(doc) do
          0 ->
            issues

          n when n > 3 ->
            [
              %{
                type: :has_comments,
                severity: :info,
                message: "Document contains #{n} comments. Ensure these are intentional."
              }
              | issues
            ]

          _ ->
            issues
        end

      if issues == [] do
        {:ok, :clean}
      else
        {:error, issues}
      end
    end
  end

  @doc """
  Check heading consistency and hierarchy.
  """
  @spec check_headings(Path.t()) :: {:ok, map()} | {:error, term()}
  def check_headings(file_path) do
    with {:ok, doc} <- parse_document(file_path) do
      analyze_headings(doc)
    end
  end

  @doc """
  Check for question labels (TMA format: Question 1, Q1, etc.)
  """
  @spec check_question_labels(Path.t()) :: {:ok, [map()]} | {:error, term()}
  def check_question_labels(file_path) do
    with {:ok, doc} <- parse_document(file_path),
         {:ok, text} <- extract_text(doc) do
      find_question_labels(text)
    end
  end

  # ============================================================
  # PRIVATE - DOCUMENT PARSING
  # ============================================================

  defp parse_document(file_path) do
    ext = file_path |> Path.extname() |> String.downcase()

    case ext do
      ".docx" -> parse_docx(file_path)
      ".odt" -> parse_odt(file_path)
      ".rtf" -> parse_rtf(file_path)
      _ -> {:error, {:unsupported_format, ext}}
    end
  end

  defp parse_docx(path) do
    case :zip.unzip(String.to_charlist(path), [:memory]) do
      {:ok, files} ->
        files_map = Map.new(files, fn {name, content} -> {List.to_string(name), content} end)

        {:ok,
         %{
           type: :docx,
           path: path,
           files: files_map,
           document: files_map["word/document.xml"],
           styles: files_map["word/styles.xml"],
           comments: files_map["word/comments.xml"],
           settings: files_map["word/settings.xml"]
         }}

      {:error, reason} ->
        {:error, {:zip_error, reason}}
    end
  end

  defp parse_odt(path) do
    case :zip.unzip(String.to_charlist(path), [:memory]) do
      {:ok, files} ->
        files_map = Map.new(files, fn {name, content} -> {List.to_string(name), content} end)

        {:ok,
         %{
           type: :odt,
           path: path,
           files: files_map,
           content: files_map["content.xml"],
           styles: files_map["styles.xml"],
           meta: files_map["meta.xml"]
         }}

      {:error, reason} ->
        {:error, {:zip_error, reason}}
    end
  end

  defp parse_rtf(path) do
    case File.read(path) do
      {:ok, content} ->
        {:ok,
         %{
           type: :rtf,
           path: path,
           content: content
         }}

      error ->
        error
    end
  end

  # ============================================================
  # PRIVATE - TEXT EXTRACTION
  # ============================================================

  defp extract_text(%{type: :docx, document: xml}) when is_binary(xml) do
    text =
      xml
      |> String.replace(~r/<w:p[^>]*>/, "\n\n")
      |> String.replace(~r/<w:br[^>]*\/>/, "\n")
      |> String.replace(~r/<[^>]+>/, "")
      |> String.replace(~r/&amp;/, "&")
      |> String.replace(~r/&lt;/, "<")
      |> String.replace(~r/&gt;/, ">")
      |> String.replace(~r/\n{3,}/, "\n\n")
      |> String.trim()

    {:ok, text}
  end

  defp extract_text(%{type: :odt, content: xml}) when is_binary(xml) do
    text =
      xml
      |> String.replace(~r/<text:p[^>]*>/, "\n\n")
      |> String.replace(~r/<text:line-break\/>/, "\n")
      |> String.replace(~r/<[^>]+>/, "")
      |> String.replace(~r/\n{3,}/, "\n\n")
      |> String.trim()

    {:ok, text}
  end

  defp extract_text(%{type: :rtf, content: content}) do
    text =
      content
      |> String.replace(~r/\\par\s*/i, "\n\n")
      |> String.replace(~r/\\[a-z]+\d*\s?/i, "")
      |> String.replace(~r/\{|\}/, "")
      |> String.replace(~r/\\'[0-9a-f]{2}/i, "")
      |> String.replace(~r/\n{3,}/, "\n\n")
      |> String.trim()

    {:ok, text}
  end

  defp extract_text(_), do: {:error, :cannot_extract_text}

  # ============================================================
  # PRIVATE - WORD COUNT
  # ============================================================

  defp analyze_word_count(text, opts) do
    word_limit = Keyword.get(opts, :word_limit)

    # Split on whitespace and count
    words =
      text
      |> String.split(~r/\s+/)
      |> Enum.filter(&(String.length(&1) > 0))

    count = length(words)

    result = %{
      total: count,
      unique: words |> Enum.map(&String.downcase/1) |> Enum.uniq() |> length(),
      average_word_length: average_word_length(words)
    }

    result =
      if word_limit do
        tolerance = @default_style.word_tolerance_percent
        min_allowed = round(word_limit * (1 - tolerance / 100))
        max_allowed = round(word_limit * (1 + tolerance / 100))

        over_under =
          cond do
            count < min_allowed -> {:under, word_limit - count}
            count > max_allowed -> {:over, count - word_limit}
            true -> :within
          end

        Map.merge(result, %{
          limit: word_limit,
          difference: count - word_limit,
          percentage: round(count / word_limit * 100),
          status: over_under
        })
      else
        result
      end

    {:ok, result}
  end

  defp average_word_length(words) when words == [], do: 0

  defp average_word_length(words) do
    total_chars = words |> Enum.map(&String.length/1) |> Enum.sum()
    Float.round(total_chars / length(words), 1)
  end

  # ============================================================
  # PRIVATE - READABILITY
  # ============================================================

  defp analyze_readability(text) do
    sentences = split_sentences(text)
    words = split_words(text)
    syllables = count_syllables(words)

    sentence_count = max(length(sentences), 1)
    word_count = max(length(words), 1)
    syllable_count = max(syllables, 1)

    # Flesch Reading Ease (0-100, higher = easier)
    flesch_ease =
      206.835 - 1.015 * (word_count / sentence_count) - 84.6 * (syllable_count / word_count)

    flesch_ease = Float.round(max(0, min(100, flesch_ease)), 1)

    # Flesch-Kincaid Grade Level
    fk_grade =
      0.39 * (word_count / sentence_count) + 11.8 * (syllable_count / word_count) - 15.59

    fk_grade = Float.round(max(0, fk_grade), 1)

    # Gunning Fog Index
    complex_words = Enum.count(words, &(count_word_syllables(&1) >= 3))
    fog = 0.4 * ((word_count / sentence_count) + 100 * (complex_words / word_count))
    fog = Float.round(max(0, fog), 1)

    # Average sentence length
    avg_sentence_length = Float.round(word_count / sentence_count, 1)

    {:ok,
     %{
       flesch_reading_ease: flesch_ease,
       flesch_kincaid_grade: fk_grade,
       gunning_fog_index: fog,
       average_sentence_length: avg_sentence_length,
       sentence_count: sentence_count,
       word_count: word_count,
       syllable_count: syllable_count,
       complex_word_count: complex_words,
       reading_level: reading_level_description(fk_grade)
     }}
  end

  defp split_sentences(text) do
    text
    |> String.split(~r/[.!?]+\s*/)
    |> Enum.filter(&(String.length(String.trim(&1)) > 0))
  end

  defp split_words(text) do
    text
    |> String.downcase()
    |> String.replace(~r/[^a-z\s]/, "")
    |> String.split(~r/\s+/)
    |> Enum.filter(&(String.length(&1) > 0))
  end

  defp count_syllables(words) do
    Enum.sum(Enum.map(words, &count_word_syllables/1))
  end

  defp count_word_syllables(word) do
    word = String.downcase(word)

    if String.length(word) <= 3 do
      1
    else
      count =
        Enum.reduce(@syllable_patterns, 0, fn {pattern, adjustment}, acc ->
          matches = length(Regex.scan(pattern, word))
          acc + matches * max(1, 1 + adjustment)
        end)

      max(1, count)
    end
  end

  defp reading_level_description(grade) do
    cond do
      grade <= 6 -> "Elementary (Age 11-12)"
      grade <= 8 -> "Middle School (Age 12-14)"
      grade <= 10 -> "High School (Age 14-16)"
      grade <= 12 -> "Senior (Age 16-18)"
      grade <= 14 -> "Undergraduate"
      grade <= 16 -> "Postgraduate"
      true -> "Professional/Academic"
    end
  end

  # ============================================================
  # PRIVATE - STRUCTURE ANALYSIS
  # ============================================================

  defp analyze_structure(%{type: :docx, document: xml}) when is_binary(xml) do
    paragraphs = Regex.scan(~r/<w:p[^>]*>.*?<\/w:p>/s, xml)
    headings = find_docx_headings(xml)

    {:ok,
     %{
       paragraph_count: length(paragraphs),
       heading_count: length(headings),
       headings: headings,
       sections: detect_sections(headings),
       has_intro: has_introduction?(headings),
       has_conclusion: has_conclusion?(headings)
     }}
  end

  defp analyze_structure(%{type: :odt, content: xml}) when is_binary(xml) do
    paragraphs = Regex.scan(~r/<text:p[^>]*>.*?<\/text:p>/s, xml)
    headings = find_odt_headings(xml)

    {:ok,
     %{
       paragraph_count: length(paragraphs),
       heading_count: length(headings),
       headings: headings,
       sections: detect_sections(headings),
       has_intro: has_introduction?(headings),
       has_conclusion: has_conclusion?(headings)
     }}
  end

  defp analyze_structure(%{type: :rtf, content: content}) do
    paragraphs = String.split(content, ~r/\\par/i)

    {:ok,
     %{
       paragraph_count: length(paragraphs),
       heading_count: 0,
       headings: [],
       sections: [],
       has_intro: false,
       has_conclusion: false
     }}
  end

  defp analyze_structure(_), do: {:error, :cannot_analyze_structure}

  defp find_docx_headings(xml) do
    # Find paragraphs with heading styles
    heading_pattern = ~r/<w:p[^>]*>.*?<w:pStyle\s+w:val="Heading(\d)".*?<w:t>([^<]+)<\/w:t>.*?<\/w:p>/s

    Regex.scan(heading_pattern, xml)
    |> Enum.map(fn [_, level, text] ->
      %{level: String.to_integer(level), text: String.trim(text)}
    end)
  end

  defp find_odt_headings(xml) do
    heading_pattern = ~r/<text:h[^>]*text:outline-level="(\d)"[^>]*>([^<]+)<\/text:h>/

    Regex.scan(heading_pattern, xml)
    |> Enum.map(fn [_, level, text] ->
      %{level: String.to_integer(level), text: String.trim(text)}
    end)
  end

  defp detect_sections(headings) do
    headings
    |> Enum.filter(&(&1.level == 1))
    |> Enum.map(& &1.text)
  end

  defp has_introduction?(headings) do
    Enum.any?(headings, fn h ->
      Regex.match?(~r/^introduction|^intro\b/i, h.text)
    end)
  end

  defp has_conclusion?(headings) do
    Enum.any?(headings, fn h ->
      Regex.match?(~r/^conclusion|^summary|^closing/i, h.text)
    end)
  end

  defp analyze_headings(%{type: :docx, document: xml}) when is_binary(xml) do
    headings = find_docx_headings(xml)

    # Check hierarchy
    levels = Enum.map(headings, & &1.level)

    hierarchy_issues =
      levels
      |> Enum.chunk_every(2, 1, :discard)
      |> Enum.with_index()
      |> Enum.filter(fn {[prev, curr], _} -> curr > prev + 1 end)
      |> Enum.map(fn {[prev, curr], idx} ->
        %{
          position: idx + 1,
          issue: "Skipped heading level from H#{prev} to H#{curr}"
        }
      end)

    {:ok,
     %{
       headings: headings,
       count: length(headings),
       levels_used: Enum.uniq(levels),
       hierarchy_valid: hierarchy_issues == [],
       hierarchy_issues: hierarchy_issues
     }}
  end

  defp analyze_headings(_), do: {:error, :cannot_analyze_headings}

  # ============================================================
  # PRIVATE - FORMATTING ANALYSIS
  # ============================================================

  defp analyze_formatting(%{type: :docx, files: files}) do
    styles_xml = files["word/styles.xml"] || ""
    settings_xml = files["word/settings.xml"] || ""
    document_xml = files["word/document.xml"] || ""

    fonts = extract_fonts(document_xml)
    font_sizes = extract_font_sizes(document_xml)
    spacing = extract_spacing(styles_xml)

    {:ok,
     %{
       fonts_used: fonts,
       font_sizes: font_sizes,
       line_spacing: spacing[:line_spacing],
       paragraph_spacing: spacing[:paragraph_spacing],
       margins: extract_margins(settings_xml),
       font_consistency: check_font_consistency(fonts),
       size_consistency: check_size_consistency(font_sizes)
     }}
  end

  defp analyze_formatting(%{type: :odt, files: files}) do
    styles_xml = files["styles.xml"] || ""
    content_xml = files["content.xml"] || ""

    {:ok,
     %{
       fonts_used: extract_odt_fonts(content_xml),
       font_sizes: [],
       line_spacing: nil,
       paragraph_spacing: nil,
       margins: extract_odt_margins(styles_xml),
       font_consistency: :unknown,
       size_consistency: :unknown
     }}
  end

  defp analyze_formatting(%{type: :rtf}) do
    {:ok,
     %{
       fonts_used: [],
       font_sizes: [],
       line_spacing: nil,
       paragraph_spacing: nil,
       margins: nil,
       font_consistency: :unknown,
       size_consistency: :unknown
     }}
  end

  defp analyze_formatting(_), do: {:error, :cannot_analyze_formatting}

  defp extract_fonts(xml) do
    ~r/<w:rFonts[^>]*w:ascii="([^"]+)"/
    |> Regex.scan(xml)
    |> Enum.map(fn [_, font] -> font end)
    |> Enum.uniq()
  end

  defp extract_odt_fonts(xml) do
    ~r/fo:font-family="([^"]+)"/
    |> Regex.scan(xml)
    |> Enum.map(fn [_, font] -> font end)
    |> Enum.uniq()
  end

  defp extract_font_sizes(xml) do
    ~r/<w:sz\s+w:val="(\d+)"/
    |> Regex.scan(xml)
    |> Enum.map(fn [_, size] -> div(String.to_integer(size), 2) end)
    |> Enum.uniq()
    |> Enum.sort()
  end

  defp extract_spacing(_styles_xml) do
    # Would need proper XML parsing for accurate extraction
    %{line_spacing: nil, paragraph_spacing: nil}
  end

  defp extract_margins(_settings_xml) do
    # Would need proper XML parsing for accurate extraction
    nil
  end

  defp extract_odt_margins(_styles_xml) do
    nil
  end

  defp check_font_consistency(fonts) when length(fonts) <= 2, do: :consistent
  defp check_font_consistency(fonts) when length(fonts) <= 4, do: :mostly_consistent
  defp check_font_consistency(_), do: :inconsistent

  defp check_size_consistency(sizes) when length(sizes) <= 3, do: :consistent
  defp check_size_consistency(sizes) when length(sizes) <= 5, do: :mostly_consistent
  defp check_size_consistency(_), do: :inconsistent

  # ============================================================
  # PRIVATE - TRACK CHANGES & COMMENTS
  # ============================================================

  defp check_track_changes(%{type: :docx, document: xml}) when is_binary(xml) do
    has_insertions = Regex.match?(~r/<w:ins\s/, xml)
    has_deletions = Regex.match?(~r/<w:del\s/, xml)

    cond do
      has_insertions or has_deletions -> {:ok, :present}
      true -> {:ok, :none}
    end
  end

  defp check_track_changes(_), do: {:ok, :unknown}

  defp count_comments(%{type: :docx, comments: nil}), do: 0

  defp count_comments(%{type: :docx, comments: xml}) when is_binary(xml) do
    Regex.scan(~r/<w:comment\s/, xml) |> length()
  end

  defp count_comments(_), do: 0

  # ============================================================
  # PRIVATE - QUESTION LABELS
  # ============================================================

  defp find_question_labels(text) do
    patterns = [
      {~r/Question\s+(\d+)/i, :full},
      {~r/Q\.?\s*(\d+)/i, :abbreviated},
      {~r/Part\s+([A-Za-z]|\d+)/i, :part},
      {~r/Task\s+(\d+)/i, :task}
    ]

    labels =
      Enum.flat_map(patterns, fn {pattern, type} ->
        Regex.scan(pattern, text, return: :index)
        |> Enum.zip(Regex.scan(pattern, text))
        |> Enum.map(fn {[{pos, len} | _], [_full, number]} ->
          %{
            text: String.slice(text, pos, len),
            type: type,
            number: number,
            position: pos
          }
        end)
      end)
      |> Enum.sort_by(& &1.position)

    {:ok, labels}
  end

  # ============================================================
  # PRIVATE - ISSUE DETECTION
  # ============================================================

  defp find_issues(word_data, readability, structure, formatting, _opts) do
    issues = []

    # Word count issues
    issues =
      case word_data[:status] do
        {:over, amount} when amount > 200 ->
          [
            %{
              type: :word_count,
              severity: :warning,
              message: "#{amount} words over the limit (#{word_data.total}/#{word_data.limit})"
            }
            | issues
          ]

        {:under, amount} when amount > 200 ->
          [
            %{
              type: :word_count,
              severity: :warning,
              message: "#{amount} words under the limit (#{word_data.total}/#{word_data.limit})"
            }
            | issues
          ]

        _ ->
          issues
      end

    # Readability issues
    issues =
      cond do
        readability.flesch_kincaid_grade < 10 ->
          [
            %{
              type: :readability,
              severity: :info,
              message: "Writing level may be below expected academic standard (Grade #{readability.flesch_kincaid_grade})"
            }
            | issues
          ]

        readability.average_sentence_length > 30 ->
          [
            %{
              type: :sentence_length,
              severity: :info,
              message: "Average sentence length is high (#{readability.average_sentence_length} words)"
            }
            | issues
          ]

        true ->
          issues
      end

    # Structure issues
    issues =
      if structure.paragraph_count < 3 do
        [
          %{
            type: :structure,
            severity: :warning,
            message: "Very few paragraphs detected (#{structure.paragraph_count})"
          }
          | issues
        ]
      else
        issues
      end

    # Formatting issues
    issues =
      case formatting.font_consistency do
        :inconsistent ->
          [
            %{
              type: :formatting,
              severity: :info,
              message: "Multiple fonts detected: #{Enum.join(formatting.fonts_used, ", ")}"
            }
            | issues
          ]

        _ ->
          issues
      end

    issues
  end

  defp calculate_compliance(formatting, _opts) do
    style = @default_style

    font_ok = Enum.any?(formatting.fonts_used, &(&1 in style.fonts))
    size_ok = Enum.any?(formatting.font_sizes, &(&1 in style.font_size_body))

    %{
      font_compliant: font_ok,
      size_compliant: size_ok,
      overall: font_ok and size_ok
    }
  end
end
