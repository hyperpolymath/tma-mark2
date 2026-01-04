# SPDX-License-Identifier: AGPL-3.0-or-later
defmodule EtmaHandler.FHI do
  @moduledoc """
  Parser and generator for FHI (File Handler Info) XML files.

  FHI files are XML metadata files attached to eTMA submissions containing:
  - Student details (name, ID, contact info)
  - Tutor details (staff ID, name, region)
  - Submission details (course, TMA number, dates, scores)
  - Question details (max scores, student scores)

  ## Example Usage

      # Parse an FHI file
      {:ok, submission} = FHI.parse_file("/path/to/submission.fhi")

      # Parse FHI XML string
      {:ok, submission} = FHI.parse(xml_string)

      # Generate FHI XML from a submission struct
      xml = FHI.generate(submission)

      # Update scores in an FHI file
      {:ok, updated} = FHI.update_scores(submission, %{
        overall: 85,
        questions: [%{number: 1, score: 52}, %{number: 2, score: 33}]
      })
  """

  # Use require instead of import to avoid function name conflicts
  require SweetXml
  import SweetXml, only: [sigil_x: 2, xpath: 2]

  # ============================================================
  # TYPES
  # ============================================================

  @type student :: %{
          oucu: String.t(),
          personal_id: String.t(),
          title: String.t(),
          initials: String.t(),
          forenames: String.t(),
          surname: String.t(),
          email: String.t(),
          address: list(String.t()),
          postcode: String.t()
        }

  @type tutor :: %{
          staff_id: String.t(),
          title: String.t(),
          initials: String.t(),
          forenames: String.t(),
          surname: String.t(),
          region_code: String.t()
        }

  @type question :: %{
          number: integer(),
          max_score: integer(),
          student_score: integer() | nil,
          parts_count: integer()
        }

  @type submission :: %{
          student: student(),
          tutor: tutor(),
          course_code: String.t(),
          course_version: integer(),
          presentation: String.t(),
          tma_number: String.t(),
          submission_number: integer(),
          submission_date: DateTime.t() | nil,
          walton_received_date: DateTime.t() | nil,
          marked_date: DateTime.t() | nil,
          status: String.t(),
          late_status: String.t() | nil,
          after_cutoff: boolean(),
          zip_date: DateTime.t() | nil,
          zip_file: String.t() | nil,
          score_update_allowed: boolean(),
          overall_score: integer() | nil,
          max_score: integer(),
          tutor_comments: String.t() | nil,
          total_questions: integer(),
          permitted_questions: integer(),
          questions: list(question())
        }

  # ============================================================
  # PARSING
  # ============================================================

  @doc """
  Parse an FHI XML file.
  """
  @spec parse_file(Path.t()) :: {:ok, submission()} | {:error, term()}
  def parse_file(path) do
    case File.read(path) do
      {:ok, content} -> parse(content)
      {:error, reason} -> {:error, {:file_error, reason}}
    end
  end

  @doc """
  Parse FHI XML content.
  """
  @spec parse(String.t()) :: {:ok, submission()} | {:error, term()}
  def parse(xml) when is_binary(xml) do
    try do
      doc = SweetXml.parse(xml, quiet: true)

      submission = %{
        student: parse_student(doc),
        tutor: parse_tutor(doc),
        course_code: xpath(doc, ~x"//submission_details/course_code/text()"s),
        course_version: xpath(doc, ~x"//submission_details/course_version_num/text()"i),
        presentation: xpath(doc, ~x"//submission_details/pres_code/text()"s),
        tma_number: xpath(doc, ~x"//submission_details/assgnmt_suffix/text()"s),
        submission_number: xpath(doc, ~x"//submission_details/e_tma_submission_num/text()"i),
        submission_date: parse_date(xpath(doc, ~x"//submission_details/e_tma_submission_date/text()"s)),
        walton_received_date: parse_date(xpath(doc, ~x"//submission_details/walton_received_date/text()"s)),
        marked_date: parse_date(xpath(doc, ~x"//submission_details/marked_date/text()"s)),
        status: xpath(doc, ~x"//submission_details/submission_status/text()"s),
        late_status: xpath(doc, ~x"//submission_details/late_submission_status/text()"s) |> nilify(),
        after_cutoff: xpath(doc, ~x"//submission_details/tma_submitted_after_absolute_cutoff/text()"s) == "Y",
        zip_date: parse_date(xpath(doc, ~x"//submission_details/zip_date/text()"s)),
        zip_file: xpath(doc, ~x"//submission_details/zip_file/text()"s) |> nilify(),
        score_update_allowed: xpath(doc, ~x"//submission_details/score_update_allowed/text()"s) == "Y",
        overall_score: parse_int(xpath(doc, ~x"//submission_details/overall_grade_score/text()"s)),
        max_score: xpath(doc, ~x"//submission_details/max_assgnmt_score/text()"i),
        tutor_comments: xpath(doc, ~x"//submission_details/tutor_comments/text()"s) |> nilify(),
        total_questions: xpath(doc, ~x"//submission_details/total_question_count/text()"i),
        permitted_questions: xpath(doc, ~x"//submission_details/permitted_question_count/text()"i),
        questions: parse_questions(doc)
      }

      {:ok, submission}
    rescue
      e -> {:error, {:parse_error, Exception.message(e)}}
    end
  end

  defp parse_student(doc) do
    %{
      oucu: xpath(doc, ~x"//student_details/ou_computer_user_name/text()"s),
      personal_id: xpath(doc, ~x"//student_details/personal_id/text()"s),
      title: xpath(doc, ~x"//student_details/title/text()"s),
      initials: xpath(doc, ~x"//student_details/initials/text()"s),
      forenames: xpath(doc, ~x"//student_details/forenames/text()"s),
      surname: xpath(doc, ~x"//student_details/surname/text()"s),
      email: xpath(doc, ~x"//student_details/email_address/text()"s),
      address: [
        xpath(doc, ~x"//student_details/address_line1/text()"s),
        xpath(doc, ~x"//student_details/address_line2/text()"s),
        xpath(doc, ~x"//student_details/address_line3/text()"s),
        xpath(doc, ~x"//student_details/address_line4/text()"s),
        xpath(doc, ~x"//student_details/address_line5/text()"s)
      ] |> Enum.reject(&(&1 == "")),
      postcode: xpath(doc, ~x"//student_details/postcode/text()"s)
    }
  end

  defp parse_tutor(doc) do
    %{
      staff_id: xpath(doc, ~x"//tutor_details/staff_id/text()"s),
      title: xpath(doc, ~x"//tutor_details/staff_title/text()"s),
      initials: xpath(doc, ~x"//tutor_details/staff_initials/text()"s),
      forenames: xpath(doc, ~x"//tutor_details/staff_forenames/text()"s),
      surname: xpath(doc, ~x"//tutor_details/staff_surname/text()"s),
      region_code: xpath(doc, ~x"//tutor_details/region_code/text()"s)
    }
  end

  defp parse_questions(doc) do
    xpath(doc, ~x"//question_details/question"l)
    |> Enum.map(fn q ->
      %{
        number: xpath(q, ~x"./@question_number"s) |> String.to_integer(),
        max_score: xpath(q, ~x"./maximum_question_score/text()"i),
        student_score: parse_int(xpath(q, ~x"./student_question_score/text()"s)),
        parts_count: xpath(q, ~x"./question_parts_count/text()"i)
      }
    end)
  end

  defp parse_date(""), do: nil
  defp parse_date(nil), do: nil

  defp parse_date(date_str) do
    # Format: "23-Nov-2025 23:52:29"
    case Timex.parse(date_str, "{0D}-{Mshort}-{YYYY} {h24}:{m}:{s}") do
      {:ok, datetime} -> datetime
      _ -> nil
    end
  rescue
    _ -> nil
  end

  defp parse_int(""), do: nil
  defp parse_int(nil), do: nil

  defp parse_int(str) do
    case Integer.parse(str) do
      {int, _} -> int
      :error -> nil
    end
  end

  defp nilify(""), do: nil
  defp nilify(str), do: str

  # ============================================================
  # GENERATION
  # ============================================================

  @doc """
  Generate FHI XML from a submission struct.
  """
  @spec generate(submission()) :: String.t()
  def generate(submission) do
    """
    <?xml version="1.0" encoding="ISO-8859-1"?>
    <student_submission>
    #{generate_student_details(submission.student)}
    #{generate_tutor_details(submission.tutor)}
    #{generate_submission_details(submission)}
    #{generate_question_details(submission.questions)}
    </student_submission>
    """
    |> String.trim()
  end

  defp generate_student_details(student) do
    """
    <student_details>
    <ou_computer_user_name>#{escape(student.oucu)}</ou_computer_user_name>
    <personal_id>#{escape(student.personal_id)}</personal_id>
    <title>#{escape(student.title)}</title>
    <initials>#{escape(student.initials)}</initials>
    <forenames>#{escape(student.forenames)}</forenames>
    <surname>#{escape(student.surname)}</surname>
    <email_address>#{escape(student.email)}</email_address>
    #{generate_address_lines(student.address)}
    <postcode>#{escape(student.postcode)}</postcode>
    </student_details>
    """
  end

  defp generate_address_lines(address) do
    address
    |> Enum.with_index(1)
    |> Enum.map(fn {line, idx} ->
      "<address_line#{idx}>#{escape(line)}</address_line#{idx}>"
    end)
    |> Enum.join("\n")
    |> then(fn lines ->
      # Pad to 5 lines
      existing = length(address)
      padding = for i <- (existing + 1)..5, do: "<address_line#{i}></address_line#{i}>"
      lines <> "\n" <> Enum.join(padding, "\n")
    end)
  end

  defp generate_tutor_details(tutor) do
    """
    <tutor_details>
    <staff_id>#{escape(tutor.staff_id)}</staff_id>
    <staff_title>#{escape(tutor.title)}</staff_title>
    <staff_initials>#{escape(tutor.initials)}</staff_initials>
    <staff_forenames>#{escape(tutor.forenames)}</staff_forenames>
    <staff_surname>#{escape(tutor.surname)}</staff_surname>
    <region_code>#{escape(tutor.region_code)}</region_code>
    </tutor_details>
    """
  end

  defp generate_submission_details(sub) do
    """
    <submission_details>
    <course_code>#{escape(sub.course_code)}</course_code>
    <course_version_num>#{sub.course_version}</course_version_num>
    <pres_code>#{escape(sub.presentation)}</pres_code>
    <assgnmt_suffix>#{escape(sub.tma_number)}</assgnmt_suffix>
    <e_tma_submission_num>#{sub.submission_number}</e_tma_submission_num>
    <e_tma_submission_date>#{format_date(sub.submission_date)}</e_tma_submission_date>
    <walton_received_date>#{format_date(sub.walton_received_date)}</walton_received_date>
    <marked_date>#{format_date(sub.marked_date)}</marked_date>
    <submission_status>#{escape(sub.status)}</submission_status>
    <late_submission_status>#{escape(sub.late_status || "")}</late_submission_status>
    <tma_submitted_after_absolute_cutoff>#{if sub.after_cutoff, do: "Y", else: ""}</tma_submitted_after_absolute_cutoff>
    <zip_date>#{format_date(sub.zip_date)}</zip_date>
    <zip_file>#{escape(sub.zip_file || "")}</zip_file>
    <score_update_allowed>#{if sub.score_update_allowed, do: "Y", else: "N"}</score_update_allowed>
    <overall_grade_score>#{format_score(sub.overall_score)}</overall_grade_score>
    <tutor_comments>#{escape(sub.tutor_comments || "")}</tutor_comments>
    <max_assgnmt_score>#{String.pad_leading(to_string(sub.max_score), 4, "0")}</max_assgnmt_score>
    <total_question_count>#{String.pad_leading(to_string(sub.total_questions), 2, "0")}</total_question_count>
    <permitted_question_count>#{String.pad_leading(to_string(sub.permitted_questions), 2, "0")}</permitted_question_count>
    </submission_details>
    """
  end

  defp generate_question_details(questions) do
    question_xml =
      questions
      |> Enum.map(fn q ->
        """
        <question question_number="#{String.pad_leading(to_string(q.number), 2, "0")}">
        <maximum_question_score>#{q.max_score}</maximum_question_score>
        <student_question_score>#{format_score(q.student_score)}</student_question_score>
        <question_parts_count>#{q.parts_count}</question_parts_count>
        </question>
        """
      end)
      |> Enum.join("")

    "<question_details>\n#{question_xml}</question_details>"
  end

  defp format_date(nil), do: ""

  defp format_date(datetime) do
    Timex.format!(datetime, "{0D}-{Mshort}-{YYYY} {h24}:{m}:{s}")
  rescue
    _ -> ""
  end

  defp format_score(nil), do: ""
  defp format_score(score), do: to_string(score)

  defp escape(nil), do: ""

  defp escape(str) when is_binary(str) do
    str
    |> String.replace("&", "&amp;")
    |> String.replace("<", "&lt;")
    |> String.replace(">", "&gt;")
    |> String.replace("\"", "&quot;")
    |> String.replace("'", "&apos;")
  end

  # ============================================================
  # MODIFICATION
  # ============================================================

  @doc """
  Update scores in a submission.
  """
  @spec update_scores(submission(), map()) :: {:ok, submission()}
  def update_scores(submission, %{overall: overall, questions: question_scores}) do
    updated_questions =
      submission.questions
      |> Enum.map(fn q ->
        case Enum.find(question_scores, fn qs -> qs.number == q.number end) do
          nil -> q
          %{score: score} -> %{q | student_score: score}
        end
      end)

    {:ok,
     %{
       submission
       | overall_score: overall,
         questions: updated_questions,
         status: "Marked",
         marked_date: DateTime.utc_now()
     }}
  end

  @doc """
  Mark a submission as zipped/returned.
  """
  @spec mark_zipped(submission(), String.t()) :: submission()
  def mark_zipped(submission, zip_filename) do
    %{
      submission
      | zip_file: zip_filename,
        zip_date: DateTime.utc_now()
    }
  end

  @doc """
  Get the student's full name.
  """
  @spec student_full_name(submission()) :: String.t()
  def student_full_name(%{student: student}) do
    "#{student.forenames} #{student.surname}"
  end

  @doc """
  Get a display-friendly submission identifier.
  """
  @spec submission_id(submission()) :: String.t()
  def submission_id(sub) do
    "#{sub.course_code}-#{sub.presentation}-TMA#{sub.tma_number}-#{sub.student.oucu}"
  end

  @doc """
  Calculate the percentage score.
  """
  @spec percentage(submission()) :: float() | nil
  def percentage(%{overall_score: nil}), do: nil
  def percentage(%{max_score: 0}), do: nil
  def percentage(%{overall_score: score, max_score: max}), do: score / max * 100

  @doc """
  Check if the submission is complete (all questions marked).
  """
  @spec complete?(submission()) :: boolean()
  def complete?(%{questions: questions}) do
    Enum.all?(questions, fn q -> q.student_score != nil end)
  end

  @doc """
  Get file information summary for validation display.
  """
  @spec file_info(Path.t()) :: {:ok, map()} | {:error, term()}
  def file_info(path) do
    with {:ok, stat} <- File.stat(path),
         {:ok, content} <- File.read(path),
         {:ok, hash} <- compute_hash(content) do
      {:ok,
       %{
         path: path,
         filename: Path.basename(path),
         size: stat.size,
         modified: stat.mtime,
         hash: hash,
         extension: Path.extname(path),
         valid_fhi: String.contains?(content, "<student_submission>")
       }}
    end
  end

  defp compute_hash(content) do
    hash =
      :crypto.hash(:sha256, content)
      |> Base.encode16(case: :lower)

    {:ok, hash}
  rescue
    _ ->
      # Fall back if BLAKE3 NIF isn't available
      {:ok, "unavailable"}
  end
end
