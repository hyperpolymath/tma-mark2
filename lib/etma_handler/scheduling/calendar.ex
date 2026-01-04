# SPDX-License-Identifier: AGPL-3.0-or-later
defmodule EtmaHandler.Scheduling.Calendar do
  @moduledoc """
  Calendar & Deadline Management.

  Manages marking deadlines, tutorials, and student support timing:

  1. **Course calendar** - Tutorials, deadlines, key dates
  2. **Marking deadlines** - 10 working day rule with bank holiday handling
  3. **Student extensions** - Per-student deadline adjustments
  4. **Time tracking** - How long marking tasks take
  5. **iCal export** - Sync with external calendars
  6. **Notifications** - Deadline warnings and reminders

  ## Usage

      # Set up a course calendar
      Calendar.set_course_dates("M150", "25J", dates)

      # Calculate marking deadline
      {:ok, deadline} = Calendar.marking_deadline(submission_date, opts)

      # Get upcoming events
      {:ok, events} = Calendar.upcoming_events(days: 7)

      # Export to iCal
      {:ok, ical} = Calendar.export_ical("M150-25J")

      # Track marking time
      Calendar.start_timer(submission_id)
      Calendar.stop_timer(submission_id)
  """

  require Logger

  use Timex

  alias EtmaHandler.Settings

  # UK Bank Holidays (configurable, defaults for common ones)
  @default_bank_holidays [
    # 2025
    ~D[2025-01-01],
    ~D[2025-04-18],
    ~D[2025-04-21],
    ~D[2025-05-05],
    ~D[2025-05-26],
    ~D[2025-08-25],
    ~D[2025-12-25],
    ~D[2025-12-26],
    # 2026
    ~D[2026-01-01],
    ~D[2026-04-03],
    ~D[2026-04-06],
    ~D[2026-05-04],
    ~D[2026-05-25],
    ~D[2026-08-31],
    ~D[2026-12-25],
    ~D[2026-12-28]
  ]

  @type event_type :: :assignment_deadline | :tutorial | :marking_deadline | :extension | :other
  @type event :: %{
          id: String.t(),
          type: event_type(),
          title: String.t(),
          date: Date.t(),
          time: Time.t() | nil,
          course: String.t(),
          details: map()
        }

  @type timer_record :: %{
          submission_id: String.t(),
          started_at: DateTime.t(),
          stopped_at: DateTime.t() | nil,
          duration_seconds: integer() | nil
        }

  # ============================================================
  # PUBLIC API - COURSE DATES
  # ============================================================

  @doc """
  Set up course dates for a presentation.

  ## Date Structure

      %{
        start_date: ~D[2025-02-01],
        end_date: ~D[2025-10-31],
        assignments: [
          %{tma: "01", open: ~D[2025-02-01], due: ~D[2025-03-15]},
          %{tma: "02", open: ~D[2025-03-16], due: ~D[2025-04-30]},
          ...
        ],
        tutorials: [
          %{title: "TMA01 Prep", date: ~D[2025-03-08], time: ~T[10:00:00]},
          ...
        ],
        exam: %{date: ~D[2025-06-15], type: :ema}
      }
  """
  @spec set_course_dates(String.t(), String.t(), map()) :: :ok
  def set_course_dates(course_code, presentation, dates) do
    key = "#{course_code}-#{presentation}"
    Settings.put([:calendar, :courses, key], dates)
  end

  @doc """
  Get course dates for a presentation.
  """
  @spec get_course_dates(String.t(), String.t()) :: {:ok, map()} | {:error, :not_found}
  def get_course_dates(course_code, presentation) do
    key = "#{course_code}-#{presentation}"

    case Settings.get([:calendar, :courses, key]) do
      nil -> {:error, :not_found}
      dates -> {:ok, dates}
    end
  end

  @doc """
  Get all configured courses.
  """
  @spec list_courses() :: [String.t()]
  def list_courses do
    case Settings.get([:calendar, :courses]) do
      nil -> []
      courses when is_map(courses) -> Map.keys(courses)
      _ -> []
    end
  end

  # ============================================================
  # PUBLIC API - DEADLINE CALCULATION
  # ============================================================

  @doc """
  Calculate marking deadline based on submission date.

  Uses 10 working days from submission, excluding weekends and bank holidays.

  ## Options

  - `:working_days` - Number of working days (default: 10)
  - `:bank_holidays` - Custom list of bank holiday dates
  - `:extension_days` - Additional days for student extension
  """
  @spec marking_deadline(Date.t() | DateTime.t(), keyword()) :: {:ok, Date.t()}
  def marking_deadline(submission_date, opts \\ []) do
    date =
      case submission_date do
        %DateTime{} = dt -> DateTime.to_date(dt)
        %Date{} = d -> d
        str when is_binary(str) -> Date.from_iso8601!(str)
      end

    working_days = Keyword.get(opts, :working_days, 10)
    extension_days = Keyword.get(opts, :extension_days, 0)
    total_days = working_days + extension_days

    bank_holidays = Keyword.get(opts, :bank_holidays, get_bank_holidays())

    deadline = add_working_days(date, total_days, bank_holidays)
    {:ok, deadline}
  end

  @doc """
  Check if a deadline has been missed.
  """
  @spec deadline_status(Date.t()) :: :upcoming | :due_today | :overdue | {:overdue, integer()}
  def deadline_status(deadline) do
    today = Date.utc_today()
    diff = Date.diff(deadline, today)

    cond do
      diff > 0 -> :upcoming
      diff == 0 -> :due_today
      diff < 0 -> {:overdue, abs(diff)}
    end
  end

  @doc """
  Get days remaining until a deadline.
  """
  @spec days_remaining(Date.t()) :: integer()
  def days_remaining(deadline) do
    Date.diff(deadline, Date.utc_today())
  end

  @doc """
  Get working days remaining until a deadline.
  """
  @spec working_days_remaining(Date.t()) :: integer()
  def working_days_remaining(deadline) do
    today = Date.utc_today()
    bank_holidays = get_bank_holidays()
    count_working_days(today, deadline, bank_holidays)
  end

  # ============================================================
  # PUBLIC API - STUDENT EXTENSIONS
  # ============================================================

  @doc """
  Record a student extension.
  """
  @spec record_extension(String.t(), String.t(), Date.t(), integer(), String.t()) :: :ok
  def record_extension(student_oucu, assignment, original_deadline, extension_days, reason \\ "") do
    new_deadline = Date.add(original_deadline, extension_days)

    extension = %{
      student_oucu: student_oucu,
      assignment: assignment,
      original_deadline: original_deadline,
      extension_days: extension_days,
      new_deadline: new_deadline,
      reason: reason,
      recorded_at: DateTime.utc_now()
    }

    extensions = get_all_extensions()
    key = "#{student_oucu}-#{assignment}"
    updated = Map.put(extensions, key, extension)
    Settings.put([:calendar, :extensions], updated)
  end

  @doc """
  Get extension for a specific student and assignment.
  """
  @spec get_extension(String.t(), String.t()) :: {:ok, map()} | {:error, :not_found}
  def get_extension(student_oucu, assignment) do
    extensions = get_all_extensions()
    key = "#{student_oucu}-#{assignment}"

    case Map.get(extensions, key) do
      nil -> {:error, :not_found}
      ext -> {:ok, ext}
    end
  end

  @doc """
  Get effective deadline for a student (accounting for extensions).
  """
  @spec effective_deadline(String.t(), String.t(), Date.t()) :: Date.t()
  def effective_deadline(student_oucu, assignment, default_deadline) do
    case get_extension(student_oucu, assignment) do
      {:ok, ext} -> ext.new_deadline
      {:error, :not_found} -> default_deadline
    end
  end

  # ============================================================
  # PUBLIC API - EVENTS & REMINDERS
  # ============================================================

  @doc """
  Get upcoming events across all courses.

  ## Options

  - `:days` - Look ahead days (default: 7)
  - `:courses` - Filter by course codes
  - `:types` - Filter by event types
  """
  @spec upcoming_events(keyword()) :: {:ok, [event()]}
  def upcoming_events(opts \\ []) do
    days = Keyword.get(opts, :days, 7)
    course_filter = Keyword.get(opts, :courses)
    type_filter = Keyword.get(opts, :types)

    today = Date.utc_today()
    end_date = Date.add(today, days)

    events =
      list_courses()
      |> Enum.filter(fn course ->
        course_filter == nil or course in course_filter
      end)
      |> Enum.flat_map(&get_course_events/1)
      |> Enum.filter(fn event ->
        Date.compare(event.date, today) != :lt and
          Date.compare(event.date, end_date) != :gt
      end)
      |> Enum.filter(fn event ->
        type_filter == nil or event.type in type_filter
      end)
      |> Enum.sort_by(& &1.date)

    {:ok, events}
  end

  @doc """
  Get events for a specific course.
  """
  @spec course_events(String.t(), String.t()) :: {:ok, [event()]}
  def course_events(course_code, presentation) do
    key = "#{course_code}-#{presentation}"
    events = get_course_events(key)
    {:ok, events}
  end

  @doc """
  Add a custom event.
  """
  @spec add_event(event()) :: :ok
  def add_event(event) do
    events = Settings.get([:calendar, :custom_events]) || []
    updated = [event | events]
    Settings.put([:calendar, :custom_events], updated)
  end

  @doc """
  Get reminders for today.
  """
  @spec todays_reminders() :: {:ok, [map()]}
  def todays_reminders do
    # Get submissions with approaching deadlines
    pending_marks = get_pending_marks()

    reminders =
      pending_marks
      |> Enum.map(fn sub ->
        deadline = sub[:marking_deadline]
        days = days_remaining(deadline)

        cond do
          days < 0 ->
            %{
              type: :overdue,
              priority: :high,
              message: "OVERDUE: #{sub[:assignment]} - #{abs(days)} days past deadline",
              submission: sub
            }

          days == 0 ->
            %{
              type: :due_today,
              priority: :high,
              message: "DUE TODAY: #{sub[:assignment]} must be returned today",
              submission: sub
            }

          days <= 2 ->
            %{
              type: :urgent,
              priority: :medium,
              message: "#{sub[:assignment]} - #{days} days remaining",
              submission: sub
            }

          days <= 5 ->
            %{
              type: :upcoming,
              priority: :low,
              message: "#{sub[:assignment]} - #{days} days remaining",
              submission: sub
            }

          true ->
            nil
        end
      end)
      |> Enum.filter(&(&1 != nil))
      |> Enum.sort_by(fn r ->
        case r.priority do
          :high -> 0
          :medium -> 1
          :low -> 2
        end
      end)

    {:ok, reminders}
  end

  # ============================================================
  # PUBLIC API - TIME TRACKING
  # ============================================================

  @doc """
  Start timing a marking session.
  """
  @spec start_timer(String.t()) :: :ok
  def start_timer(submission_id) do
    timers = Settings.get([:calendar, :timers]) || %{}

    timer = %{
      submission_id: submission_id,
      started_at: DateTime.utc_now(),
      stopped_at: nil,
      duration_seconds: nil
    }

    updated = Map.put(timers, submission_id, timer)
    Settings.put([:calendar, :timers], updated)
  end

  @doc """
  Stop timing a marking session.
  """
  @spec stop_timer(String.t()) :: {:ok, integer()} | {:error, :not_found}
  def stop_timer(submission_id) do
    timers = Settings.get([:calendar, :timers]) || %{}

    case Map.get(timers, submission_id) do
      nil ->
        {:error, :not_found}

      %{started_at: started} = timer ->
        now = DateTime.utc_now()
        duration = DateTime.diff(now, started)

        updated_timer = %{timer | stopped_at: now, duration_seconds: duration}
        updated = Map.put(timers, submission_id, updated_timer)
        Settings.put([:calendar, :timers], updated)

        # Also record to history
        record_marking_time(submission_id, duration)

        {:ok, duration}
    end
  end

  @doc """
  Get current timer status.
  """
  @spec timer_status(String.t()) :: {:ok, map()} | {:error, :not_found}
  def timer_status(submission_id) do
    timers = Settings.get([:calendar, :timers]) || %{}

    case Map.get(timers, submission_id) do
      nil ->
        {:error, :not_found}

      %{stopped_at: nil, started_at: started} = timer ->
        elapsed = DateTime.diff(DateTime.utc_now(), started)
        {:ok, Map.put(timer, :elapsed_seconds, elapsed)}

      timer ->
        {:ok, timer}
    end
  end

  @doc """
  Get average marking time for an assignment type.
  """
  @spec average_marking_time(String.t()) :: {:ok, integer()} | {:error, :no_data}
  def average_marking_time(assignment) do
    history = Settings.get([:calendar, :marking_times]) || []

    times =
      history
      |> Enum.filter(&(&1[:assignment] == assignment))
      |> Enum.map(& &1[:duration_seconds])
      |> Enum.filter(&(&1 != nil))

    if times == [] do
      {:error, :no_data}
    else
      avg = round(Enum.sum(times) / length(times))
      {:ok, avg}
    end
  end

  @doc """
  Format duration for display.
  """
  @spec format_duration(integer()) :: String.t()
  def format_duration(seconds) when is_integer(seconds) do
    hours = div(seconds, 3600)
    minutes = div(rem(seconds, 3600), 60)
    secs = rem(seconds, 60)

    cond do
      hours > 0 -> "#{hours}h #{minutes}m"
      minutes > 0 -> "#{minutes}m #{secs}s"
      true -> "#{secs}s"
    end
  end

  # ============================================================
  # PUBLIC API - ICAL EXPORT
  # ============================================================

  @doc """
  Export events to iCal format.

  ## Options

  - `:course` - Filter by course (e.g., "M150-25J")
  - `:days` - Days to include (default: 90)
  - `:include_marking` - Include marking deadlines (default: true)
  """
  @spec export_ical(keyword()) :: {:ok, String.t()}
  def export_ical(opts \\ []) do
    course = Keyword.get(opts, :course)
    days = Keyword.get(opts, :days, 90)
    include_marking = Keyword.get(opts, :include_marking, true)

    events =
      if course do
        [course_code, presentation] = String.split(course, "-")
        {:ok, evts} = course_events(course_code, presentation)
        evts
      else
        {:ok, evts} = upcoming_events(days: days)
        evts
      end

    events =
      if include_marking do
        marking_events = get_marking_deadline_events()
        events ++ marking_events
      else
        events
      end

    ical = build_ical(events)
    {:ok, ical}
  end

  @doc """
  Save iCal to file.
  """
  @spec save_ical(String.t(), String.t()) :: :ok | {:error, term()}
  def save_ical(ical_content, file_path) do
    File.write(file_path, ical_content)
  end

  # ============================================================
  # PRIVATE - HELPERS
  # ============================================================

  defp add_working_days(date, days, _bank_holidays) when days <= 0, do: date

  defp add_working_days(date, days, bank_holidays) do
    next = Date.add(date, 1)

    if is_working_day?(next, bank_holidays) do
      add_working_days(next, days - 1, bank_holidays)
    else
      add_working_days(next, days, bank_holidays)
    end
  end

  defp count_working_days(from, to, _) when from >= to, do: 0

  defp count_working_days(from, to, bank_holidays) do
    next = Date.add(from, 1)
    count = if is_working_day?(next, bank_holidays), do: 1, else: 0
    count + count_working_days(next, to, bank_holidays)
  end

  defp is_working_day?(date, bank_holidays) do
    day_of_week = Date.day_of_week(date)
    not (day_of_week in [6, 7]) and date not in bank_holidays
  end

  defp get_bank_holidays do
    case Settings.get([:calendar, :bank_holidays]) do
      nil -> @default_bank_holidays
      holidays -> holidays
    end
  end

  defp get_all_extensions do
    case Settings.get([:calendar, :extensions]) do
      nil -> %{}
      exts when is_map(exts) -> exts
      _ -> %{}
    end
  end

  defp get_course_events(course_key) do
    case Settings.get([:calendar, :courses, course_key]) do
      nil ->
        []

      dates ->
        events = []

        # Assignment deadlines
        assignment_events =
          (dates[:assignments] || [])
          |> Enum.flat_map(fn a ->
            [
              %{
                id: "#{course_key}-#{a[:tma]}-due",
                type: :assignment_deadline,
                title: "TMA#{a[:tma]} Due",
                date: a[:due],
                time: nil,
                course: course_key,
                details: %{tma: a[:tma], open: a[:open]}
              }
            ]
          end)

        # Tutorials
        tutorial_events =
          (dates[:tutorials] || [])
          |> Enum.map(fn t ->
            %{
              id: "#{course_key}-tutorial-#{t[:date]}",
              type: :tutorial,
              title: t[:title] || "Tutorial",
              date: t[:date],
              time: t[:time],
              course: course_key,
              details: t
            }
          end)

        events ++ assignment_events ++ tutorial_events
    end
  end

  defp get_pending_marks do
    # Would integrate with actual submission tracking
    []
  end

  defp get_marking_deadline_events do
    # Would integrate with actual submission tracking
    []
  end

  defp record_marking_time(submission_id, duration) do
    history = Settings.get([:calendar, :marking_times]) || []

    record = %{
      submission_id: submission_id,
      duration_seconds: duration,
      recorded_at: DateTime.utc_now()
    }

    updated = [record | history] |> Enum.take(1000)
    Settings.put([:calendar, :marking_times], updated)
  end

  # ============================================================
  # PRIVATE - ICAL GENERATION
  # ============================================================

  defp build_ical(events) do
    header = """
    BEGIN:VCALENDAR
    VERSION:2.0
    PRODID:-//eTMA Handler//EN
    CALSCALE:GREGORIAN
    METHOD:PUBLISH
    X-WR-CALNAME:eTMA Marking Calendar
    """

    footer = "END:VCALENDAR\n"

    event_strings =
      events
      |> Enum.map(&build_vevent/1)
      |> Enum.join("\n")

    header <> event_strings <> footer
  end

  defp build_vevent(event) do
    uid = "#{event.id}@etma-handler"

    dtstart =
      if event.time do
        datetime = DateTime.new!(event.date, event.time)
        DateTime.to_iso8601(datetime, :basic)
      else
        Date.to_iso8601(event.date) |> String.replace("-", "")
      end

    summary = String.replace(event.title, ~r/[,;\\]/, "")

    """
    BEGIN:VEVENT
    UID:#{uid}
    DTSTART:#{dtstart}
    SUMMARY:#{summary}
    DESCRIPTION:#{event.course}
    CATEGORIES:#{event.type |> Atom.to_string() |> String.upcase()}
    END:VEVENT
    """
  end
end
