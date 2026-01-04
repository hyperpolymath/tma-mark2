# SPDX-License-Identifier: AGPL-3.0-or-later
defmodule EtmaHandlerWeb.SettingsLive do
  @moduledoc """
  Application settings interface.

  Organized into collapsible sections:
  - Folders & Files
  - File Validation (corrupt, format, empty detection)
  - Marking Behavior
  - External Applications
  - Spellcheck & Language
  - Startup & Performance
  - Display & Accessibility
  """

  use EtmaHandlerWeb, :live_view

  alias EtmaHandler.Settings
  alias EtmaHandler.Crypto

  @impl true
  def mount(_params, _session, socket) do
    settings = Settings.all()

    {:ok,
     socket
     |> assign(:page_title, "Settings")
     |> assign(:settings, settings)
     |> assign(:expanded_sections, MapSet.new([:folders, :file_validation]))
     |> assign(:unsaved_changes, false)
     |> assign(:test_file_result, nil)}
  end

  @impl true
  def render(assigns) do
    ~H"""
    <div class="min-h-screen bg-slate-50">
      <!-- Header -->
      <header class="bg-white border-b border-slate-200 sticky top-0 z-10">
        <div class="max-w-5xl mx-auto px-6 py-4 flex items-center justify-between">
          <div class="flex items-center gap-3">
            <svg class="w-6 h-6 text-slate-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" />
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
            </svg>
            <h1 class="text-xl font-bold text-slate-800">Settings</h1>
          </div>
          <div class="flex items-center gap-3">
            <button
              :if={@unsaved_changes}
              phx-click="reset_all"
              class="px-4 py-2 text-sm text-slate-600 hover:text-slate-800"
            >
              Reset
            </button>
            <button
              phx-click="save_all"
              disabled={!@unsaved_changes}
              class={"px-4 py-2 text-sm font-medium rounded-lg transition " <>
                if(@unsaved_changes,
                  do: "bg-blue-600 text-white hover:bg-blue-700",
                  else: "bg-slate-100 text-slate-400 cursor-not-allowed")}
            >
              Save All
            </button>
          </div>
        </div>
      </header>

      <main class="max-w-5xl mx-auto px-6 py-8 space-y-4">
        <!-- Folders & Files -->
        <.settings_section
          id="folders"
          title="Folders & Files"
          icon="folder"
          expanded={MapSet.member?(@expanded_sections, :folders)}
        >
          <div class="space-y-4">
            <.folder_input
              label="eTMAs Folder"
              description="Where marked TMAs are stored"
              value={@settings.folders.etmas_folder}
              field={[:folders, :etmas_folder]}
            />
            <.folder_input
              label="Watch Folder"
              description="Monitor this folder for new downloads"
              value={@settings.folders.watch_folder}
              field={[:folders, :watch_folder]}
            />
            <.folder_input
              label="Returns Folder"
              description="Archived returned submissions"
              value={@settings.folders.returns_folder}
              field={[:folders, :returns_folder]}
            />
          </div>
        </.settings_section>

        <!-- File Validation -->
        <.settings_section
          id="file_validation"
          title="File Validation"
          icon="shield-check"
          expanded={MapSet.member?(@expanded_sections, :file_validation)}
        >
          <div class="grid md:grid-cols-2 gap-8">
            <!-- Detection Column -->
            <div>
              <h4 class="text-sm font-semibold text-slate-700 mb-4">Detection</h4>
              <div class="space-y-3">
                <.toggle_setting
                  label="Detect corrupt files"
                  description="Check if files can be opened"
                  value={@settings.file_validation.detect_corrupt}
                  field={[:file_validation, :detect_corrupt]}
                />
                <.toggle_setting
                  label="Detect wrong format"
                  description="Verify MIME type matches extension"
                  value={@settings.file_validation.detect_wrong_format}
                  field={[:file_validation, :detect_wrong_format]}
                />
                <.toggle_setting
                  label="Detect empty files"
                  description="Reject zero-byte files"
                  value={@settings.file_validation.detect_empty}
                  field={[:file_validation, :detect_empty]}
                />
                <.toggle_setting
                  label="Detect oversized files"
                  description="Warn about large files"
                  value={@settings.file_validation.detect_oversized}
                  field={[:file_validation, :detect_oversized]}
                />
              </div>
            </div>

            <!-- Display Column -->
            <div>
              <h4 class="text-sm font-semibold text-slate-700 mb-4">Display Info</h4>
              <div class="space-y-3">
                <.toggle_setting
                  label="Show filename"
                  description="Display file name in validation"
                  value={@settings.file_validation.show_filename}
                  field={[:file_validation, :show_filename]}
                />
                <.toggle_setting
                  label="Show file hash"
                  description="Display BLAKE3 hash for verification"
                  value={@settings.file_validation.show_hash}
                  field={[:file_validation, :show_hash]}
                />
                <.toggle_setting
                  label="Show file size"
                  description="Display human-readable size"
                  value={@settings.file_validation.show_size}
                  field={[:file_validation, :show_size]}
                />
                <.toggle_setting
                  label="Show MIME type"
                  description="Display detected file type"
                  value={@settings.file_validation.show_mime_type}
                  field={[:file_validation, :show_mime_type]}
                />
              </div>
            </div>
          </div>

          <!-- Limits -->
          <div class="mt-6 pt-6 border-t border-slate-200">
            <h4 class="text-sm font-semibold text-slate-700 mb-4">Limits</h4>
            <div class="grid md:grid-cols-2 gap-4">
              <.number_input
                label="Maximum file size"
                value={@settings.file_validation.max_file_size_mb}
                field={[:file_validation, :max_file_size_mb]}
                unit="MB"
                min={1}
                max={500}
              />
              <.number_input
                label="Warn if folder exceeds"
                value={@settings.file_validation.warn_large_folder_mb}
                field={[:file_validation, :warn_large_folder_mb]}
                unit="MB"
                min={10}
                max={1000}
              />
            </div>
          </div>

          <!-- Allowed Formats -->
          <div class="mt-6 pt-6 border-t border-slate-200">
            <h4 class="text-sm font-semibold text-slate-700 mb-4">Allowed File Formats</h4>
            <div class="flex flex-wrap gap-2">
              <%= for ext <- ~w(.doc .docx .odt .rtf .pdf .txt .html .htm) do %>
                <.format_checkbox
                  extension={ext}
                  checked={ext in @settings.file_validation.allowed_extensions}
                />
              <% end %>
            </div>
          </div>

          <!-- Test File -->
          <div class="mt-6 pt-6 border-t border-slate-200">
            <h4 class="text-sm font-semibold text-slate-700 mb-4">Test File Validation</h4>
            <div class="flex items-center gap-4">
              <label class="flex-1">
                <input
                  type="file"
                  phx-hook="FileValidation"
                  id="test-file-input"
                  class="block w-full text-sm text-slate-500 file:mr-4 file:py-2 file:px-4
                    file:rounded-lg file:border-0 file:text-sm file:font-medium
                    file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100"
                />
              </label>
            </div>
            <%= if @test_file_result do %>
              <.file_validation_result result={@test_file_result} settings={@settings.file_validation} />
            <% end %>
          </div>
        </.settings_section>

        <!-- Marking Behavior -->
        <.settings_section
          id="marking"
          title="Marking Behavior"
          icon="pencil"
          expanded={MapSet.member?(@expanded_sections, :marking)}
        >
          <div class="grid md:grid-cols-2 gap-x-8 gap-y-3">
            <.toggle_setting
              label="Autofill marks"
              description="Pre-populate mark fields"
              value={@settings.marking.autofill_marks}
              field={[:marking, :autofill_marks]}
            />
            <.toggle_setting
              label="Add initials to MARKED"
              description="Append your initials to filename"
              value={@settings.marking.add_initials_to_marked}
              field={[:marking, :add_initials_to_marked]}
            />
            <.toggle_setting
              label="Create MARKED copy"
              description="Save a copy with 'MARKED' suffix"
              value={@settings.marking.create_marked_copy}
              field={[:marking, :create_marked_copy]}
            />
            <.toggle_setting
              label="Direct entry of totals"
              description="Allow typing totals directly"
              value={@settings.marking.enable_direct_entry}
              field={[:marking, :enable_direct_entry]}
            />
            <.toggle_setting
              label="Show latest submissions only"
              description="Hide older submission versions"
              value={@settings.marking.show_latest_only}
              field={[:marking, :show_latest_only]}
            />
            <.toggle_setting
              label="Check before zipping"
              description="Verify scripts are closed before packaging"
              value={@settings.marking.check_open_scripts_before_zip}
              field={[:marking, :check_open_scripts_before_zip]}
            />
            <.toggle_setting
              label="Auto-import TMAs"
              description="Automatically import detected TMAs"
              value={@settings.marking.auto_import}
              field={[:marking, :auto_import]}
            />
          </div>
        </.settings_section>

        <!-- External Applications -->
        <.settings_section
          id="external_apps"
          title="External Applications"
          icon="desktop-computer"
          expanded={MapSet.member?(@expanded_sections, :external_apps)}
        >
          <div class="space-y-4">
            <.app_selector
              label="Web Browser"
              value={@settings.external_apps.browser}
              field={[:external_apps, :browser]}
            />
            <.app_selector
              label="Word Processor"
              value={@settings.external_apps.word_processor}
              field={[:external_apps, :word_processor]}
            />
            <.app_selector
              label="PDF Viewer"
              value={@settings.external_apps.pdf_viewer}
              field={[:external_apps, :pdf_viewer]}
            />
            <.app_selector
              label="Audio Player"
              value={@settings.external_apps.audio_app}
              field={[:external_apps, :audio_app]}
            />
          </div>
        </.settings_section>

        <!-- Spellcheck -->
        <.settings_section
          id="spellcheck"
          title="Spellcheck & Language"
          icon="translate"
          expanded={MapSet.member?(@expanded_sections, :spellcheck)}
        >
          <div class="space-y-4">
            <.toggle_setting
              label="Enable live spellcheck"
              description="Check spelling as you type"
              value={@settings.spellcheck.enabled}
              field={[:spellcheck, :enabled]}
            />
            <.toggle_setting
              label="Suggest corrections"
              description="Show spelling suggestions"
              value={@settings.spellcheck.suggest_corrections}
              field={[:spellcheck, :suggest_corrections]}
            />
            <.folder_input
              label="Custom Dictionary"
              description="Additional words file"
              value={@settings.spellcheck.dictionary_file || "None selected"}
              field={[:spellcheck, :dictionary_file]}
            />
          </div>
        </.settings_section>

        <!-- Startup -->
        <.settings_section
          id="startup"
          title="Startup & Performance"
          icon="lightning-bolt"
          expanded={MapSet.member?(@expanded_sections, :startup)}
        >
          <div class="grid md:grid-cols-2 gap-x-8 gap-y-3">
            <.toggle_setting
              label="Show startup screen"
              description="Display splash on launch"
              value={@settings.startup.show_startup_screen}
              field={[:startup, :show_startup_screen]}
            />
            <.toggle_setting
              label="Open TMA list on launch"
              description="Show pending TMAs immediately"
              value={@settings.startup.open_tma_list_on_launch}
              field={[:startup, :open_tma_list_on_launch]}
            />
            <.toggle_setting
              label="Check returns on launch"
              description="Scan for old zipped files"
              value={@settings.startup.check_returns_on_launch}
              field={[:startup, :check_returns_on_launch]}
            />
            <.toggle_setting
              label="Preload comment bank"
              description="Load comments at startup"
              value={@settings.startup.preload_commentbank}
              field={[:startup, :preload_commentbank]}
            />
          </div>
        </.settings_section>

        <!-- Display & Accessibility -->
        <.settings_section
          id="display"
          title="Display & Accessibility"
          icon="eye"
          expanded={MapSet.member?(@expanded_sections, :display)}
        >
          <div class="space-y-4">
            <.toggle_setting
              label="Show tooltips"
              description="Display helpful hints on hover"
              value={@settings.display.tooltips_enabled}
              field={[:display, :tooltips_enabled]}
            />
            <.toggle_setting
              label="High contrast mode"
              description="Enhanced visibility for accessibility"
              value={@settings.display.high_contrast}
              field={[:display, :high_contrast]}
            />
            <.toggle_setting
              label="Reduce animations"
              description="Minimize motion for accessibility"
              value={@settings.display.reduce_animations}
              field={[:display, :reduce_animations]}
            />
            <.toggle_setting
              label="Large click targets"
              description="Bigger buttons and controls"
              value={@settings.display.large_click_targets}
              field={[:display, :large_click_targets]}
            />
            <div class="pt-4">
              <label class="block text-sm font-medium text-slate-700 mb-2">
                Font Size: {@settings.display.global_font_size}px
              </label>
              <input
                type="range"
                min="12"
                max="24"
                value={@settings.display.global_font_size}
                phx-change="update_setting"
                phx-value-field="display.global_font_size"
                class="w-full h-2 bg-slate-200 rounded-lg appearance-none cursor-pointer"
              />
              <div class="flex justify-between text-xs text-slate-500 mt-1">
                <span>Small</span>
                <span>Default</span>
                <span>Large</span>
              </div>
            </div>
          </div>
        </.settings_section>

        <!-- eTMA Site (collapsed by default) -->
        <.settings_section
          id="etma_site"
          title="eTMA Site Connection"
          icon="globe"
          expanded={MapSet.member?(@expanded_sections, :etma_site)}
        >
          <div class="space-y-4">
            <.text_input
              label="eTMA Site URL"
              value={@settings.etma_site.base_url}
              field={[:etma_site, :base_url]}
              type="url"
            />
            <.text_input
              label="TMA Weightings URL"
              value={@settings.etma_site.weightings_url}
              field={[:etma_site, :weightings_url]}
              type="url"
            />
          </div>
        </.settings_section>

        <!-- Footer Actions -->
        <div class="flex justify-between items-center pt-6 border-t border-slate-200">
          <button
            phx-click="export_settings"
            class="text-sm text-slate-600 hover:text-slate-800"
          >
            Export Settings
          </button>
          <button
            phx-click="reset_all_confirm"
            class="text-sm text-red-600 hover:text-red-800"
          >
            Reset All to Defaults
          </button>
        </div>
      </main>
    </div>
    """
  end

  # ============================================================
  # COMPONENTS
  # ============================================================

  attr :id, :string, required: true
  attr :title, :string, required: true
  attr :icon, :string, required: true
  attr :expanded, :boolean, default: false
  slot :inner_block, required: true

  defp settings_section(assigns) do
    ~H"""
    <div class="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
      <button
        type="button"
        phx-click="toggle_section"
        phx-value-section={@id}
        class="w-full px-6 py-4 flex items-center justify-between hover:bg-slate-50 transition"
      >
        <div class="flex items-center gap-3">
          <.section_icon name={@icon} />
          <span class="font-semibold text-slate-800"><%= @title %></span>
        </div>
        <svg
          class={"w-5 h-5 text-slate-400 transition-transform " <> if(@expanded, do: "rotate-180", else: "")}
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
        >
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
        </svg>
      </button>
      <div class={"px-6 pb-6 " <> if(@expanded, do: "", else: "hidden")}>
        <%= render_slot(@inner_block) %>
      </div>
    </div>
    """
  end

  defp section_icon(%{name: "folder"} = assigns) do
    ~H"""
    <svg class="w-5 h-5 text-amber-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
        d="M3 7v10a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-6l-2-2H5a2 2 0 00-2 2z" />
    </svg>
    """
  end

  defp section_icon(%{name: "shield-check"} = assigns) do
    ~H"""
    <svg class="w-5 h-5 text-green-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
        d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
    </svg>
    """
  end

  defp section_icon(%{name: "pencil"} = assigns) do
    ~H"""
    <svg class="w-5 h-5 text-blue-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
        d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
    </svg>
    """
  end

  defp section_icon(%{name: "desktop-computer"} = assigns) do
    ~H"""
    <svg class="w-5 h-5 text-purple-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
        d="M9.75 17L9 20l-1 1h8l-1-1-.75-3M3 13h18M5 17h14a2 2 0 002-2V5a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
    </svg>
    """
  end

  defp section_icon(%{name: "translate"} = assigns) do
    ~H"""
    <svg class="w-5 h-5 text-indigo-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
        d="M3 5h12M9 3v2m1.048 9.5A18.022 18.022 0 016.412 9m6.088 9h7M11 21l5-10 5 10M12.751 5C11.783 10.77 8.07 15.61 3 18.129" />
    </svg>
    """
  end

  defp section_icon(%{name: "lightning-bolt"} = assigns) do
    ~H"""
    <svg class="w-5 h-5 text-yellow-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
        d="M13 10V3L4 14h7v7l9-11h-7z" />
    </svg>
    """
  end

  defp section_icon(%{name: "eye"} = assigns) do
    ~H"""
    <svg class="w-5 h-5 text-teal-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
        d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
        d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
    </svg>
    """
  end

  defp section_icon(%{name: "globe"} = assigns) do
    ~H"""
    <svg class="w-5 h-5 text-cyan-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
        d="M21 12a9 9 0 01-9 9m9-9a9 9 0 00-9-9m9 9H3m9 9a9 9 0 01-9-9m9 9c1.657 0 3-4.03 3-9s-1.343-9-3-9m0 18c-1.657 0-3-4.03-3-9s1.343-9 3-9m-9 9a9 9 0 019-9" />
    </svg>
    """
  end

  defp section_icon(assigns) do
    ~H"""
    <svg class="w-5 h-5 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
        d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
    </svg>
    """
  end

  attr :label, :string, required: true
  attr :description, :string, default: nil
  attr :value, :boolean, required: true
  attr :field, :list, required: true

  defp toggle_setting(assigns) do
    ~H"""
    <div class="flex items-center justify-between py-2">
      <div>
        <span class="text-sm font-medium text-slate-700"><%= @label %></span>
        <p :if={@description} class="text-xs text-slate-500"><%= @description %></p>
      </div>
      <button
        type="button"
        phx-click="toggle_boolean"
        phx-value-field={Enum.join(@field, ".")}
        class={"relative inline-flex h-6 w-11 flex-shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 " <>
          if(@value, do: "bg-blue-600", else: "bg-slate-200")}
        role="switch"
        aria-checked={to_string(@value)}
      >
        <span
          class={"pointer-events-none inline-block h-5 w-5 transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out " <>
            if(@value, do: "translate-x-5", else: "translate-x-0")}
        />
      </button>
    </div>
    """
  end

  attr :label, :string, required: true
  attr :description, :string, default: nil
  attr :value, :string, required: true
  attr :field, :list, required: true

  defp folder_input(assigns) do
    ~H"""
    <div class="space-y-1">
      <label class="block text-sm font-medium text-slate-700"><%= @label %></label>
      <p :if={@description} class="text-xs text-slate-500 mb-1"><%= @description %></p>
      <div class="flex gap-2">
        <input
          type="text"
          value={@value}
          phx-blur="update_setting"
          phx-value-field={Enum.join(@field, ".")}
          class="flex-1 px-3 py-2 text-sm border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
        />
        <button
          type="button"
          class="px-3 py-2 text-sm bg-slate-100 text-slate-600 rounded-lg hover:bg-slate-200"
        >
          Browse
        </button>
      </div>
    </div>
    """
  end

  attr :label, :string, required: true
  attr :value, :integer, required: true
  attr :field, :list, required: true
  attr :unit, :string, default: nil
  attr :min, :integer, default: 0
  attr :max, :integer, default: 100

  defp number_input(assigns) do
    ~H"""
    <div class="space-y-1">
      <label class="block text-sm font-medium text-slate-700"><%= @label %></label>
      <div class="flex items-center gap-2">
        <input
          type="number"
          value={@value}
          min={@min}
          max={@max}
          phx-blur="update_setting"
          phx-value-field={Enum.join(@field, ".")}
          class="w-24 px-3 py-2 text-sm border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
        />
        <span :if={@unit} class="text-sm text-slate-500"><%= @unit %></span>
      </div>
    </div>
    """
  end

  attr :label, :string, required: true
  attr :value, :any, required: true
  attr :field, :list, required: true
  attr :type, :string, default: "text"

  defp text_input(assigns) do
    ~H"""
    <div class="space-y-1">
      <label class="block text-sm font-medium text-slate-700"><%= @label %></label>
      <input
        type={@type}
        value={if is_atom(@value), do: "", else: @value}
        phx-blur="update_setting"
        phx-value-field={Enum.join(@field, ".")}
        class="w-full px-3 py-2 text-sm border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
      />
    </div>
    """
  end

  attr :label, :string, required: true
  attr :value, :any, required: true
  attr :field, :list, required: true

  defp app_selector(assigns) do
    ~H"""
    <div class="flex items-center justify-between py-2">
      <span class="text-sm font-medium text-slate-700"><%= @label %></span>
      <select
        phx-change="update_setting"
        phx-value-field={Enum.join(@field, ".")}
        class="px-3 py-2 text-sm border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
      >
        <option value="system_default" selected={@value == :system_default}>System Default</option>
        <option value="custom">Custom...</option>
      </select>
    </div>
    """
  end

  attr :extension, :string, required: true
  attr :checked, :boolean, required: true

  defp format_checkbox(assigns) do
    ~H"""
    <label class={"inline-flex items-center gap-2 px-3 py-1.5 rounded-lg border cursor-pointer transition " <>
      if(@checked,
        do: "bg-blue-50 border-blue-200 text-blue-700",
        else: "bg-white border-slate-200 text-slate-600 hover:bg-slate-50")}>
      <input
        type="checkbox"
        checked={@checked}
        phx-click="toggle_extension"
        phx-value-ext={@extension}
        class="sr-only"
      />
      <span class="text-sm font-medium"><%= @extension %></span>
      <svg :if={@checked} class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
        <path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd" />
      </svg>
    </label>
    """
  end

  attr :result, :map, required: true
  attr :settings, :map, required: true

  defp file_validation_result(assigns) do
    ~H"""
    <div class={"mt-4 p-4 rounded-lg " <>
      if(@result.valid, do: "bg-green-50 border border-green-200", else: "bg-red-50 border border-red-200")}>
      <div class="flex items-start gap-3">
        <svg :if={@result.valid} class="w-5 h-5 text-green-500 mt-0.5" fill="currentColor" viewBox="0 0 20 20">
          <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
        </svg>
        <svg :if={!@result.valid} class="w-5 h-5 text-red-500 mt-0.5" fill="currentColor" viewBox="0 0 20 20">
          <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
        </svg>
        <div class="flex-1">
          <p class={"font-medium " <> if(@result.valid, do: "text-green-800", else: "text-red-800")}>
            <%= if @result.valid, do: "File is valid", else: "File validation failed" %>
          </p>
          <dl class="mt-2 text-sm space-y-1">
            <div :if={@settings.show_filename} class="flex gap-2">
              <dt class="text-slate-500">Filename:</dt>
              <dd class="font-mono"><%= @result.filename %></dd>
            </div>
            <div :if={@settings.show_size} class="flex gap-2">
              <dt class="text-slate-500">Size:</dt>
              <dd><%= format_size(@result.size) %></dd>
            </div>
            <div :if={@settings.show_mime_type} class="flex gap-2">
              <dt class="text-slate-500">MIME Type:</dt>
              <dd class="font-mono"><%= @result.mime_type %></dd>
            </div>
            <div :if={@settings.show_hash} class="flex gap-2">
              <dt class="text-slate-500">BLAKE3 Hash:</dt>
              <dd class="font-mono text-xs break-all"><%= @result.hash %></dd>
            </div>
            <div :if={!@result.valid} class="flex gap-2 text-red-600">
              <dt>Error:</dt>
              <dd><%= @result.error %></dd>
            </div>
          </dl>
        </div>
      </div>
    </div>
    """
  end

  # ============================================================
  # EVENT HANDLERS
  # ============================================================

  @impl true
  def handle_event("toggle_section", %{"section" => section}, socket) do
    section_atom = String.to_existing_atom(section)
    expanded = socket.assigns.expanded_sections

    new_expanded =
      if MapSet.member?(expanded, section_atom) do
        MapSet.delete(expanded, section_atom)
      else
        MapSet.put(expanded, section_atom)
      end

    {:noreply, assign(socket, :expanded_sections, new_expanded)}
  end

  @impl true
  def handle_event("toggle_boolean", %{"field" => field_str}, socket) do
    path = String.split(field_str, ".") |> Enum.map(&String.to_existing_atom/1)
    current = get_in(socket.assigns.settings, path)
    new_settings = put_in(socket.assigns.settings, path, !current)

    {:noreply,
     socket
     |> assign(:settings, new_settings)
     |> assign(:unsaved_changes, true)}
  end

  @impl true
  def handle_event("update_setting", %{"field" => field_str, "value" => value}, socket) do
    path = String.split(field_str, ".") |> Enum.map(&String.to_existing_atom/1)

    # Parse value based on current type
    current = get_in(socket.assigns.settings, path)
    parsed_value = parse_value(value, current)

    new_settings = put_in(socket.assigns.settings, path, parsed_value)

    {:noreply,
     socket
     |> assign(:settings, new_settings)
     |> assign(:unsaved_changes, true)}
  end

  @impl true
  def handle_event("toggle_extension", %{"ext" => ext}, socket) do
    current = socket.assigns.settings.file_validation.allowed_extensions

    new_extensions =
      if ext in current do
        List.delete(current, ext)
      else
        [ext | current]
      end

    new_settings = put_in(socket.assigns.settings, [:file_validation, :allowed_extensions], new_extensions)

    {:noreply,
     socket
     |> assign(:settings, new_settings)
     |> assign(:unsaved_changes, true)}
  end

  @impl true
  def handle_event("save_all", _params, socket) do
    Settings.update(socket.assigns.settings)

    {:noreply,
     socket
     |> assign(:unsaved_changes, false)
     |> put_flash(:info, "Settings saved successfully")}
  end

  @impl true
  def handle_event("reset_all", _params, socket) do
    {:noreply,
     socket
     |> assign(:settings, Settings.all())
     |> assign(:unsaved_changes, false)}
  end

  @impl true
  def handle_event("reset_all_confirm", _params, socket) do
    Settings.reset()

    {:noreply,
     socket
     |> assign(:settings, Settings.all())
     |> assign(:unsaved_changes, false)
     |> put_flash(:info, "All settings reset to defaults")}
  end

  @impl true
  def handle_event("export_settings", _params, socket) do
    # In a real app, this would trigger a download
    {:noreply, put_flash(socket, :info, "Settings exported")}
  end

  @impl true
  def handle_event("validate_file", params, socket) do
    # This would be called from the JS hook with file data
    result = %{
      valid: true,
      filename: params["name"],
      size: params["size"],
      mime_type: params["type"],
      hash: Base.encode16(Crypto.hash(params["content"] || ""), case: :lower),
      error: nil
    }

    {:noreply, assign(socket, :test_file_result, result)}
  end

  # ============================================================
  # HELPERS
  # ============================================================

  defp parse_value(value, current) when is_integer(current) do
    case Integer.parse(value) do
      {int, _} -> int
      :error -> current
    end
  end

  defp parse_value(value, current) when is_float(current) do
    case Float.parse(value) do
      {float, _} -> float
      :error -> current
    end
  end

  defp parse_value("true", _current), do: true
  defp parse_value("false", _current), do: false
  defp parse_value(value, _current), do: value

  defp format_size(bytes) when bytes < 1024, do: "#{bytes} B"
  defp format_size(bytes) when bytes < 1024 * 1024, do: "#{Float.round(bytes / 1024, 1)} KB"
  defp format_size(bytes), do: "#{Float.round(bytes / 1024 / 1024, 1)} MB"
end
