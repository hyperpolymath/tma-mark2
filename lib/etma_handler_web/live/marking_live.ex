# SPDX-License-Identifier: AGPL-3.0-or-later
defmodule EtmaHandlerWeb.MarkingLive do
  @moduledoc """
  The "Cockpit" - Main Marking Interface.

  A three-pane layout that provides:
  - Left: Student list ("Flight Manifest")
  - Center: Document view + Marking form ("Worksurface")
  - Right: Comment bank + Audit status ("Co-Pilot")

  Features:
  - Auto-save with "Time Machine" snapshots
  - Smart calculator inputs (type "10+5" → shows "= 15")
  - Real-time quality auditing
  - What-If grade calculator
  """

  use EtmaHandlerWeb, :live_view

  alias EtmaHandler.Repo
  alias EtmaHandler.Marking.Audit
  alias EtmaHandler.Logic.Calculator

  @impl true
  def mount(_params, _session, socket) do
    # Subscribe to bouncer events for new file notifications
    if connected?(socket) do
      Phoenix.PubSub.subscribe(EtmaHandler.PubSub, "bouncer:events")
    end

    socket =
      socket
      |> assign(:page_title, "Marking")
      |> assign(:course_code, "M150-25J")
      |> assign(:submissions, load_mock_submissions())
      |> assign(:selected_submission, nil)
      |> assign(:form, nil)
      |> assign(:audit, nil)
      |> assign(:save_status, :saved)
      |> assign(:history_position, 100)
      |> assign(:what_if_target, nil)
      |> assign(:what_if_result, nil)
      |> assign(:active_tab, :refinery)
      |> assign(:comment_bank, load_comment_bank())

    {:ok, socket}
  end

  @impl true
  def handle_params(%{"submission_id" => id}, _uri, socket) do
    case find_submission(socket.assigns.submissions, id) do
      nil ->
        {:noreply, put_flash(socket, :error, "Submission not found")}

      submission ->
        socket =
          socket
          |> assign(:selected_submission, submission)
          |> assign(:form, to_form(submission_to_params(submission)))
          |> assign(:page_title, "Marking - #{submission.student_name}")

        {:noreply, socket}
    end
  end

  def handle_params(_params, _uri, socket) do
    {:noreply, socket}
  end

  @impl true
  def handle_event("select_submission", %{"id" => id}, socket) do
    {:noreply, push_patch(socket, to: ~p"/mark/#{id}")}
  end

  @impl true
  def handle_event("validate", %{"submission" => params}, socket) do
    # Run audit on changes
    audit = Audit.audit_feedback(params["tutor_comments"], student_name: get_student_name(socket))

    socket =
      socket
      |> assign(:form, to_form(params))
      |> assign(:audit, audit)
      |> assign(:save_status, :unsaved)

    {:noreply, socket}
  end

  @impl true
  def handle_event("save", %{"submission" => params}, socket) do
    submission = socket.assigns.selected_submission

    if submission do
      # Save snapshot to CubDB (Time Machine)
      submission_id = "#{submission.course_code}-#{submission.student_oucu}-#{submission.tma}"

      case Repo.save_snapshot(submission_id, params) do
        {:ok, _timestamp} ->
          socket =
            socket
            |> assign(:save_status, :saved)
            |> put_flash(:info, "Saved successfully")

          {:noreply, socket}

        {:error, reason} ->
          {:noreply, put_flash(socket, :error, "Save failed: #{inspect(reason)}")}
      end
    else
      {:noreply, put_flash(socket, :error, "No submission selected")}
    end
  end

  @impl true
  def handle_event("finalise", _params, socket) do
    audit = socket.assigns.audit

    if audit && audit.passed? do
      # TODO: Package and return the submission
      {:noreply, put_flash(socket, :info, "Submission finalised!")}
    else
      issues = if audit, do: Enum.join(audit.issues, ", "), else: "Please fill in feedback first"
      {:noreply, put_flash(socket, :error, "Cannot finalise: #{issues}")}
    end
  end

  @impl true
  def handle_event("what_if", %{"target" => target_str}, socket) do
    case Integer.parse(target_str) do
      {target, _} ->
        # Get current marks from form
        form_params = socket.assigns.form.params || %{}

        current_marks =
          ["q1", "q2", "q3"]
          |> Enum.map(&Map.get(form_params, &1, "0"))
          |> Enum.map(fn expr ->
            case Calculator.evaluate(expr) do
              {:ok, val} -> val
              _ -> 0
            end
          end)

        result = Calculator.what_if_grade(current_marks, target)

        socket =
          socket
          |> assign(:what_if_target, target)
          |> assign(:what_if_result, result)

        {:noreply, socket}

      :error ->
        {:noreply, socket}
    end
  end

  @impl true
  def handle_event("restore_snapshot", %{"position" => position_str}, socket) do
    # TODO: Implement time machine restore
    position = String.to_integer(position_str)
    {:noreply, assign(socket, :history_position, position)}
  end

  @impl true
  def handle_event("insert_comment", %{"id" => comment_id}, socket) do
    comment = Enum.find(socket.assigns.comment_bank, &(&1.id == comment_id))

    if comment do
      form_params = socket.assigns.form.params || %{}
      current_comments = Map.get(form_params, "tutor_comments", "")
      new_comments = current_comments <> "\n\n" <> comment.text

      updated_params = Map.put(form_params, "tutor_comments", new_comments)

      socket =
        socket
        |> assign(:form, to_form(updated_params))
        |> assign(:save_status, :unsaved)

      {:noreply, socket}
    else
      {:noreply, socket}
    end
  end

  @impl true
  def handle_event("switch_tab", %{"tab" => tab}, socket) do
    {:noreply, assign(socket, :active_tab, String.to_existing_atom(tab))}
  end

  @impl true
  def handle_info({:new_file, path, metadata}, socket) do
    {:noreply,
     socket
     |> put_flash(:info, "New file detected: #{Path.basename(path)}")
     |> assign(:submissions, [metadata_to_submission(metadata) | socket.assigns.submissions])}
  end

  @impl true
  def handle_info({:rejected_file, path, reasons}, socket) do
    {:noreply, put_flash(socket, :error, "File rejected: #{Path.basename(path)} - #{Enum.join(reasons, ", ")}")}
  end

  # --- Render ---

  @impl true
  def render(assigns) do
    ~H"""
    <div class="h-screen w-screen flex flex-row overflow-hidden bg-slate-50 text-slate-900 font-sans">
      <!-- Left Pane: Flight Manifest -->
      <aside class="w-72 flex-shrink-0 border-r border-slate-300 bg-white flex flex-col z-20 shadow-sm">
        <div class="p-4 border-b border-slate-200 bg-slate-100">
          <div class="flex items-center justify-between">
            <h2 class="font-bold text-lg text-slate-800"><%= @course_code %></h2>
            <span class="text-xs font-mono bg-slate-200 px-2 py-1 rounded">TMA 01</span>
          </div>
          <div class="mt-3 relative">
            <input
              type="text"
              placeholder="Filter by name or OUCU..."
              class="w-full text-sm border-slate-300 rounded focus:border-blue-600 focus:ring-blue-600 pl-9"
            />
            <svg class="w-4 h-4 absolute left-3 top-2.5 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
          </div>
        </div>

        <div class="overflow-y-auto flex-1">
          <%= for sub <- @submissions do %>
            <div
              phx-click="select_submission"
              phx-value-id={sub.id}
              class={[
                "p-4 border-b border-slate-100 cursor-pointer transition-colors",
                @selected_submission && @selected_submission.id == sub.id &&
                  "bg-blue-50 border-l-4 border-l-blue-600",
                !(@selected_submission && @selected_submission.id == sub.id) &&
                  "hover:bg-slate-50"
              ]}
            >
              <div class="font-bold text-slate-900"><%= sub.student_name %></div>
              <div class="flex justify-between text-xs mt-1 text-slate-600">
                <span class="font-mono"><%= sub.student_oucu %></span>
                <span class={status_badge_class(sub.status)}>
                  <%= String.capitalize(sub.status) %>
                </span>
              </div>
            </div>
          <% end %>
        </div>
      </aside>

      <!-- Center: Worksurface -->
      <main class="flex-1 flex flex-col min-w-0 bg-white relative z-10">
        <!-- Header -->
        <header class="h-16 border-b border-slate-200 flex items-center justify-between px-6 bg-slate-50 shadow-sm">
          <div class="flex items-center gap-6">
            <%= if @selected_submission do %>
              <h1 class="text-xl font-bold text-slate-800 truncate">
                <%= @selected_submission.student_name %>
                <span class="text-slate-400 font-normal">| PT3</span>
              </h1>

              <!-- Time Machine Slider -->
              <div class="flex items-center gap-3 px-4 py-1.5 bg-white border border-slate-200 rounded-full shadow-sm">
                <svg class="w-4 h-4 text-slate-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                <input
                  type="range"
                  min="0"
                  max="100"
                  value={@history_position}
                  phx-change="restore_snapshot"
                  phx-value-position={@history_position}
                  class="w-32 h-1 bg-slate-200 rounded-lg appearance-none cursor-pointer accent-blue-600"
                  title="Restore previous snapshot"
                />
                <span class="text-xs font-mono text-slate-500">
                  <%= if @history_position == 100, do: "Now", else: "#{@history_position}%" %>
                </span>
              </div>
            <% else %>
              <h1 class="text-xl font-bold text-slate-800">Select a student to begin marking</h1>
            <% end %>
          </div>

          <!-- Save Status -->
          <div class="flex items-center gap-2">
            <%= case @save_status do %>
              <% :saved -> %>
                <div class="flex h-2.5 w-2.5 relative">
                  <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-green-400 opacity-75"></span>
                  <span class="relative inline-flex rounded-full h-2.5 w-2.5 bg-green-500"></span>
                </div>
                <span class="text-xs font-bold text-slate-500 uppercase tracking-wide">Saved</span>
              <% :unsaved -> %>
                <div class="flex h-2.5 w-2.5 relative">
                  <span class="relative inline-flex rounded-full h-2.5 w-2.5 bg-yellow-500"></span>
                </div>
                <span class="text-xs font-bold text-yellow-600 uppercase tracking-wide">Unsaved</span>
              <% :saving -> %>
                <div class="flex h-2.5 w-2.5 relative">
                  <span class="animate-pulse relative inline-flex rounded-full h-2.5 w-2.5 bg-blue-500"></span>
                </div>
                <span class="text-xs font-bold text-blue-600 uppercase tracking-wide">Saving...</span>
            <% end %>
          </div>
        </header>

        <%= if @selected_submission do %>
          <div class="flex-1 flex flex-row overflow-hidden">
            <!-- Document Preview -->
            <div class="flex-1 bg-slate-200 p-8 overflow-y-auto border-r border-slate-300 shadow-inner">
              <div class="bg-white shadow-lg min-h-[800px] max-w-[210mm] mx-auto p-8 text-sm font-serif">
                <h1 class="text-2xl font-bold mb-4">Assignment: <%= @selected_submission.tma %></h1>
                <p class="mb-4 text-slate-600">Student: <%= @selected_submission.student_name %> (<%= @selected_submission.student_oucu %>)</p>
                <hr class="my-6 border-slate-200" />
                <p class="text-slate-400 italic">
                  [Document preview would render here using PDF.js or similar]
                </p>
                <p class="mt-4 text-slate-400 italic">
                  The actual student submission would be displayed in this area,
                  with inline annotation capabilities.
                </p>
              </div>
            </div>

            <!-- Marking Form -->
            <div class="w-[500px] overflow-y-auto bg-white flex flex-col">
              <!-- What-If Calculator -->
              <div class="p-6 border-b border-slate-100 bg-blue-50">
                <label class="text-xs font-bold uppercase text-blue-800 tracking-wider">What-If Calculator</label>
                <div class="flex items-center gap-3 mt-2">
                  <div class="relative">
                    <span class="absolute left-3 top-2 text-blue-400 font-bold text-sm">Target:</span>
                    <input
                      type="number"
                      value={@what_if_target || ""}
                      phx-change="what_if"
                      phx-debounce="500"
                      name="target"
                      class="w-28 pl-16 pr-2 py-1 text-right font-bold text-blue-900 border-blue-200 rounded focus:border-blue-500 focus:ring-blue-500"
                      placeholder="85"
                    />
                  </div>
                  <div class="text-sm text-blue-700">
                    <%= if @what_if_result do %>
                      <% {:ok, needed, msg} = @what_if_result %>
                      <%= msg %>
                    <% else %>
                      Enter a target grade
                    <% end %>
                  </div>
                </div>
              </div>

              <!-- Marking Fields -->
              <div class="p-6 space-y-8 flex-1">
                <.form for={@form} phx-change="validate" phx-submit="save">
                  <!-- Question 1 -->
                  <div class="relative pl-4 border-l-4 border-slate-200 focus-within:border-blue-600 transition-colors mb-8">
                    <div class="flex justify-between items-baseline mb-2">
                      <label class="font-bold text-lg text-slate-800">Question 1</label>
                      <span class="text-xs font-bold text-slate-400 uppercase bg-slate-100 px-2 py-1 rounded">Max: 20</span>
                    </div>
                    <.input field={@form[:q1]} type="calculator" max_score={20} />
                    <.input field={@form[:q1_feedback]} type="textarea" placeholder="Feedback for Q1..." class="mt-2" />
                  </div>

                  <!-- Question 2 -->
                  <div class="relative pl-4 border-l-4 border-slate-200 focus-within:border-blue-600 transition-colors mb-8">
                    <div class="flex justify-between items-baseline mb-2">
                      <label class="font-bold text-lg text-slate-800">Question 2</label>
                      <span class="text-xs font-bold text-slate-400 uppercase bg-slate-100 px-2 py-1 rounded">Max: 30</span>
                    </div>
                    <.input field={@form[:q2]} type="calculator" max_score={30} />
                    <.input field={@form[:q2_feedback]} type="textarea" placeholder="Feedback for Q2..." class="mt-2" />
                  </div>

                  <!-- Question 3 -->
                  <div class="relative pl-4 border-l-4 border-slate-200 focus-within:border-blue-600 transition-colors mb-8">
                    <div class="flex justify-between items-baseline mb-2">
                      <label class="font-bold text-lg text-slate-800">Question 3</label>
                      <span class="text-xs font-bold text-slate-400 uppercase bg-slate-100 px-2 py-1 rounded">Max: 50</span>
                    </div>
                    <.input field={@form[:q3]} type="calculator" max_score={50} />
                    <.input field={@form[:q3_feedback]} type="textarea" placeholder="Feedback for Q3..." class="mt-2" />
                  </div>

                  <!-- Overall Comments -->
                  <div class="mb-8">
                    <.input field={@form[:tutor_comments]} type="textarea" label="Summary Feedback" placeholder="Overall feedback and feed-forward..." rows="6" />
                  </div>

                  <.button type="submit" class="w-full">Save Progress</.button>
                </.form>
              </div>
            </div>
          </div>
        <% else %>
          <!-- Empty State -->
          <div class="flex-1 flex items-center justify-center bg-slate-100">
            <div class="text-center">
              <svg class="w-16 h-16 mx-auto text-slate-300 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
              <h2 class="text-xl font-bold text-slate-500 mb-2">No Submission Selected</h2>
              <p class="text-slate-400">Select a student from the list to begin marking</p>
            </div>
          </div>
        <% end %>
      </main>

      <!-- Right Pane: Co-Pilot -->
      <aside class="w-80 border-l border-slate-300 bg-slate-50 flex flex-col z-20 shadow-sm">
        <!-- Tabs -->
        <div class="flex border-b border-slate-200 bg-white">
          <button
            phx-click="switch_tab"
            phx-value-tab="refinery"
            class={[
              "flex-1 py-3 text-sm font-bold transition-colors",
              @active_tab == :refinery && "text-blue-600 border-b-2 border-blue-600",
              @active_tab != :refinery && "text-slate-500 hover:text-slate-700 hover:bg-slate-50"
            ]}
          >
            Refinery
          </button>
          <button
            phx-click="switch_tab"
            phx-value-tab="audit"
            class={[
              "flex-1 py-3 text-sm font-bold transition-colors",
              @active_tab == :audit && "text-blue-600 border-b-2 border-blue-600",
              @active_tab != :audit && "text-slate-500 hover:text-slate-700 hover:bg-slate-50"
            ]}
          >
            Audit
            <%= if @audit && !@audit.passed? do %>
              <span class="ml-1 px-1.5 py-0.5 text-xs bg-red-100 text-red-700 rounded-full">
                <%= length(@audit.issues) %>
              </span>
            <% end %>
          </button>
        </div>

        <!-- Tab Content -->
        <div class="flex-1 overflow-y-auto p-4">
          <%= if @active_tab == :refinery do %>
            <!-- Comment Bank Search -->
            <div class="relative mb-4">
              <input
                type="text"
                placeholder="Search comments..."
                class="w-full pl-9 text-sm border-slate-300 rounded-full focus:border-blue-600 focus:ring-blue-600"
              />
              <svg class="w-4 h-4 absolute left-3 top-2.5 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
            </div>

            <h3 class="text-xs font-bold text-slate-400 uppercase tracking-wider mb-2">Common Comments</h3>
            <div class="space-y-2">
              <%= for comment <- @comment_bank do %>
                <div
                  phx-click="insert_comment"
                  phx-value-id={comment.id}
                  class="bg-white p-3 rounded border border-slate-200 shadow-sm cursor-pointer hover:border-blue-400 group transition-all"
                >
                  <div class="flex justify-between items-start mb-1">
                    <span class={["text-xs font-bold px-1.5 py-0.5 rounded", tag_color(comment.tag)]}>
                      #<%= comment.tag %>
                    </span>
                  </div>
                  <p class="text-xs text-slate-600 group-hover:text-slate-900 line-clamp-2">
                    <%= comment.text %>
                  </p>
                </div>
              <% end %>
            </div>
          <% else %>
            <!-- Audit Tab -->
            <div class="space-y-4">
              <%= if @audit do %>
                <!-- Status -->
                <div class={[
                  "p-4 rounded-lg border-2",
                  @audit.passed? && "bg-green-50 border-green-200",
                  !@audit.passed? && "bg-red-50 border-red-200"
                ]}>
                  <div class="flex items-center gap-2 mb-2">
                    <%= if @audit.passed? do %>
                      <svg class="w-5 h-5 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                      </svg>
                      <span class="font-bold text-green-800">Ready to Return</span>
                    <% else %>
                      <svg class="w-5 h-5 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                      </svg>
                      <span class="font-bold text-red-800">Issues Found</span>
                    <% end %>
                  </div>

                  <%= if @audit.issues != [] do %>
                    <ul class="text-sm text-red-700 space-y-1">
                      <%= for issue <- @audit.issues do %>
                        <li class="flex items-start gap-2">
                          <span class="text-red-400">-</span>
                          <%= issue %>
                        </li>
                      <% end %>
                    </ul>
                  <% end %>

                  <%= if @audit.warnings != [] do %>
                    <ul class="text-sm text-yellow-700 space-y-1 mt-2">
                      <%= for warning <- @audit.warnings do %>
                        <li class="flex items-start gap-2">
                          <span class="text-yellow-400">!</span>
                          <%= warning %>
                        </li>
                      <% end %>
                    </ul>
                  <% end %>
                </div>

                <!-- Metrics -->
                <div class="bg-white p-4 rounded-lg border border-slate-200">
                  <h4 class="text-xs font-bold text-slate-400 uppercase tracking-wider mb-3">Metrics</h4>
                  <div class="grid grid-cols-2 gap-4 text-sm">
                    <div>
                      <span class="text-slate-500">Words:</span>
                      <span class="font-bold ml-1"><%= @audit.metrics.word_count %></span>
                    </div>
                    <div>
                      <span class="text-slate-500">Tone:</span>
                      <span class="font-bold ml-1 capitalize"><%= @audit.metrics.sentiment %></span>
                    </div>
                  </div>
                </div>
              <% else %>
                <div class="text-center text-slate-400 py-8">
                  <p>Start entering feedback to see audit results</p>
                </div>
              <% end %>
            </div>
          <% end %>
        </div>

        <!-- Finalise Button -->
        <div class="p-4 border-t border-slate-300 bg-white">
          <button
            phx-click="finalise"
            disabled={@audit == nil || !@audit.passed?}
            class={[
              "w-full py-3.5 font-bold rounded shadow-lg text-sm uppercase tracking-wider",
              "flex justify-center items-center gap-2 transition-all",
              (@audit && @audit.passed?) && "bg-blue-700 hover:bg-blue-800 text-white active:scale-95",
              !(@audit && @audit.passed?) && "bg-slate-200 text-slate-400 cursor-not-allowed"
            ]}
          >
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            Finalise & Return
          </button>
        </div>
      </aside>
    </div>

    <.flash_group flash={@flash} />
    """
  end

  # --- Private Helpers ---

  defp status_badge_class(status) do
    base = "px-2 py-0.5 rounded-full text-xs font-bold border"

    case status do
      "draft" -> base <> " bg-yellow-100 text-yellow-800 border-yellow-200"
      "sent" -> base <> " bg-green-100 text-green-800 border-green-200"
      "unmarked" -> base <> " bg-slate-100 text-slate-600 border-slate-200"
      _ -> base <> " bg-slate-100 text-slate-600 border-slate-200"
    end
  end

  defp tag_color(tag) do
    case tag do
      "Structure" -> "text-blue-700 bg-blue-50"
      "Referencing" -> "text-red-700 bg-red-50"
      "Grammar" -> "text-purple-700 bg-purple-50"
      "Positive" -> "text-green-700 bg-green-50"
      _ -> "text-slate-700 bg-slate-50"
    end
  end

  defp get_student_name(socket) do
    case socket.assigns.selected_submission do
      nil -> nil
      sub -> sub.student_name
    end
  end

  defp find_submission(submissions, id) do
    Enum.find(submissions, &(&1.id == id))
  end

  defp submission_to_params(submission) do
    %{
      "q1" => submission[:q1] || "",
      "q1_feedback" => submission[:q1_feedback] || "",
      "q2" => submission[:q2] || "",
      "q2_feedback" => submission[:q2_feedback] || "",
      "q3" => submission[:q3] || "",
      "q3_feedback" => submission[:q3_feedback] || "",
      "tutor_comments" => submission[:tutor_comments] || ""
    }
  end

  defp metadata_to_submission(metadata) do
    %{
      id: "#{metadata.course_code}-#{metadata.student_oucu}-#{metadata.tma_number}",
      student_name: metadata.student_oucu,
      student_oucu: metadata.student_oucu,
      course_code: metadata.course_code,
      tma: "TMA #{metadata.tma_number}",
      status: "unmarked"
    }
  end

  # --- Mock Data ---

  defp load_mock_submissions do
    [
      %{
        id: "M150-rg8274-TMA01",
        student_name: "Gil-Hopgood, Rosa",
        student_oucu: "rg8274",
        course_code: "M150",
        tma: "TMA01",
        status: "draft"
      },
      %{
        id: "M150-js1234-TMA01",
        student_name: "Smith, John",
        student_oucu: "js1234",
        course_code: "M150",
        tma: "TMA01",
        status: "sent"
      },
      %{
        id: "M150-ad5678-TMA01",
        student_name: "Doe, Amanda",
        student_oucu: "ad5678",
        course_code: "M150",
        tma: "TMA01",
        status: "unmarked"
      },
      %{
        id: "M150-bp9012-TMA01",
        student_name: "Parker, Bill",
        student_oucu: "bp9012",
        course_code: "M150",
        tma: "TMA01",
        status: "unmarked"
      }
    ]
  end

  defp load_comment_bank do
    [
      %{
        id: "struct1",
        tag: "Structure",
        text: "Please ensure you have a clear introduction that outlines the structure of your assignment."
      },
      %{
        id: "ref1",
        tag: "Referencing",
        text: "Please use the Cite Them Right Harvard style for all references. See the OU Library guide for details."
      },
      %{
        id: "gram1",
        tag: "Grammar",
        text: "Please proofread your work carefully. Consider using spelling and grammar checking tools."
      },
      %{
        id: "pos1",
        tag: "Positive",
        text: "Well done! You have demonstrated a good understanding of the key concepts."
      },
      %{
        id: "struct2",
        tag: "Structure",
        text: "Your conclusion should summarise the main points and provide a clear answer to the question."
      }
    ]
  end
end
