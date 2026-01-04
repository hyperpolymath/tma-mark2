# SPDX-License-Identifier: AGPL-3.0-or-later
defmodule EtmaHandler.Settings do
  @moduledoc """
  Application settings management.

  Handles persistent configuration with sensible defaults.
  Settings are stored in CubDB and cached in ETS for fast access.
  """

  use GenServer
  require Logger

  @table :settings_cache
  @db_path "priv/data/settings"

  # ============================================================
  # DEFAULT SETTINGS
  # ============================================================

  @default_settings %{
    # Folders & Files
    folders: %{
      etmas_folder: Path.expand("~/etmas"),
      watch_folder: Path.expand("~/Downloads"),
      commentbank_file: :automatic,
      returns_folder: Path.expand("~/etmas/returns")
    },

    # External Applications
    external_apps: %{
      browser: :system_default,
      word_processor: :system_default,
      audio_app: :system_default,
      commentbank_editor: :system_default,
      pdf_viewer: :system_default
    },

    # eTMA Site Connection
    etma_site: %{
      base_url: "https://css3.open.ac.uk/etma/tutor",
      weightings_url: "https://css3.open.ac.uk/etma/tutor/",
      auto_login: false,
      remember_credentials: false
    },

    # File Validation (integrates with Bouncer)
    file_validation: %{
      # Detection toggles
      detect_corrupt: true,
      detect_wrong_format: true,
      detect_empty: true,
      detect_oversized: true,

      # Display options
      show_filename: true,
      show_hash: true,
      show_size: true,
      show_mime_type: true,
      hash_algorithm: :blake3,

      # Limits
      max_file_size_mb: 50,
      min_file_size_bytes: 1,
      warn_large_folder_mb: 100,

      # Allowed formats
      allowed_extensions: ~w(.doc .docx .odt .rtf .pdf .txt),
      allowed_mime_types: [
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.oasis.opendocument.text",
        "application/rtf",
        "application/pdf",
        "text/plain"
      ]
    },

    # Marking Behavior
    marking: %{
      autofill_marks: false,
      add_initials_to_marked: true,
      create_marked_copy: true,
      enable_direct_entry: false,
      double_click_open_pt3: false,
      show_latest_only: true,
      check_open_scripts_before_zip: true,
      auto_import: true
    },

    # Spellcheck & Language
    spellcheck: %{
      enabled: true,
      suggest_corrections: false,
      dictionary_file: nil,
      custom_words: [],
      language: "en_GB"
    },

    # Startup & Performance
    startup: %{
      show_startup_screen: false,
      open_tma_list_on_launch: true,
      check_returns_on_launch: true,
      auto_check_updates: true,
      preload_commentbank: true
    },

    # Display & Accessibility
    display: %{
      tooltips_enabled: true,
      global_font_size: 14,
      high_contrast: false,
      theme: :system,
      reduce_animations: false,
      large_click_targets: false
    }
  }

  # ============================================================
  # PUBLIC API
  # ============================================================

  @doc """
  Start the settings server.
  """
  def start_link(opts \\ []) do
    GenServer.start_link(__MODULE__, opts, name: __MODULE__)
  end

  @doc """
  Get all settings.
  """
  def all do
    GenServer.call(__MODULE__, :all)
  end

  @doc """
  Get a setting by path.

  ## Examples

      iex> Settings.get([:folders, :etmas_folder])
      "/home/user/etmas"

      iex> Settings.get([:file_validation, :detect_corrupt])
      true
  """
  def get(path) when is_list(path) do
    GenServer.call(__MODULE__, {:get, path})
  end

  def get(key) when is_atom(key) do
    GenServer.call(__MODULE__, {:get, [key]})
  end

  @doc """
  Get a setting with a default value if not found.

  ## Examples

      iex> Settings.get(:my_setting, "default")
      "default"

      iex> Settings.get([:nested, :setting], %{})
      %{}
  """
  def get(path, default) when is_list(path) do
    case get(path) do
      nil -> default
      value -> value
    end
  end

  def get(key, default) when is_atom(key) do
    case get(key) do
      nil -> default
      value -> value
    end
  end

  @doc """
  Alias for `set/2` for convenience.
  """
  def put(path, value) when is_list(path), do: set(path, value)
  def put(key, value) when is_atom(key), do: set(key, value)

  @doc """
  Set a setting by path.

  ## Examples

      iex> Settings.set([:folders, :etmas_folder], "/new/path")
      :ok
  """
  def set(path, value) when is_list(path) do
    GenServer.call(__MODULE__, {:set, path, value})
  end

  def set(key, value) when is_atom(key) do
    GenServer.call(__MODULE__, {:set, [key], value})
  end

  @doc """
  Update multiple settings at once.
  """
  def update(changes) when is_map(changes) do
    GenServer.call(__MODULE__, {:update, changes})
  end

  @doc """
  Reset settings to defaults.
  """
  def reset do
    GenServer.call(__MODULE__, :reset)
  end

  @doc """
  Reset a specific section to defaults.
  """
  def reset(section) when is_atom(section) do
    GenServer.call(__MODULE__, {:reset, section})
  end

  @doc """
  Get the default value for a setting path.
  """
  def default(path) when is_list(path) do
    get_in(@default_settings, path)
  end

  @doc """
  Export settings to a map (for backup/transfer).
  """
  def export do
    GenServer.call(__MODULE__, :export)
  end

  @doc """
  Import settings from a map.
  """
  def import(settings) when is_map(settings) do
    GenServer.call(__MODULE__, {:import, settings})
  end

  @doc """
  Get file validation config for Bouncer.
  """
  def bouncer_config do
    validation = get(:file_validation)

    %{
      max_size: validation.max_file_size_mb * 1024 * 1024,
      min_size: validation.min_file_size_bytes,
      allowed_extensions: validation.allowed_extensions,
      allowed_mime_types: validation.allowed_mime_types,
      check_corrupt: validation.detect_corrupt,
      check_format: validation.detect_wrong_format,
      check_empty: validation.detect_empty
    }
  end

  # ============================================================
  # GENSERVER CALLBACKS
  # ============================================================

  @impl true
  def init(_opts) do
    # Create ETS table for fast reads
    :ets.new(@table, [:named_table, :public, read_concurrency: true])

    # Load settings from disk or use defaults
    settings = load_settings()

    # Cache in ETS
    :ets.insert(@table, {:settings, settings})

    {:ok, %{settings: settings}}
  end

  @impl true
  def handle_call(:all, _from, state) do
    {:reply, state.settings, state}
  end

  @impl true
  def handle_call({:get, path}, _from, state) do
    value = get_in(state.settings, path)
    {:reply, value, state}
  end

  @impl true
  def handle_call({:set, path, value}, _from, state) do
    new_settings = put_in(state.settings, path, value)
    persist_settings(new_settings)
    :ets.insert(@table, {:settings, new_settings})
    {:reply, :ok, %{state | settings: new_settings}}
  end

  @impl true
  def handle_call({:update, changes}, _from, state) do
    new_settings = deep_merge(state.settings, changes)
    persist_settings(new_settings)
    :ets.insert(@table, {:settings, new_settings})
    {:reply, :ok, %{state | settings: new_settings}}
  end

  @impl true
  def handle_call(:reset, _from, _state) do
    persist_settings(@default_settings)
    :ets.insert(@table, {:settings, @default_settings})
    {:reply, :ok, %{settings: @default_settings}}
  end

  @impl true
  def handle_call({:reset, section}, _from, state) do
    default_section = Map.get(@default_settings, section)
    new_settings = Map.put(state.settings, section, default_section)
    persist_settings(new_settings)
    :ets.insert(@table, {:settings, new_settings})
    {:reply, :ok, %{state | settings: new_settings}}
  end

  @impl true
  def handle_call(:export, _from, state) do
    {:reply, state.settings, state}
  end

  @impl true
  def handle_call({:import, imported}, _from, _state) do
    # Merge with defaults to ensure all keys exist
    new_settings = deep_merge(@default_settings, imported)
    persist_settings(new_settings)
    :ets.insert(@table, {:settings, new_settings})
    {:reply, :ok, %{settings: new_settings}}
  end

  # ============================================================
  # PRIVATE FUNCTIONS
  # ============================================================

  defp load_settings do
    case File.read(settings_file()) do
      {:ok, content} ->
        case :erlang.binary_to_term(content) do
          settings when is_map(settings) ->
            deep_merge(@default_settings, settings)

          _ ->
            Logger.warning("Invalid settings file, using defaults")
            @default_settings
        end

      {:error, :enoent} ->
        Logger.info("No settings file found, using defaults")
        @default_settings

      {:error, reason} ->
        Logger.warning("Failed to load settings: #{inspect(reason)}, using defaults")
        @default_settings
    end
  end

  defp persist_settings(settings) do
    File.mkdir_p!(Path.dirname(settings_file()))
    File.write!(settings_file(), :erlang.term_to_binary(settings))
  end

  defp settings_file do
    Path.join([@db_path, "settings.etf"])
  end

  defp deep_merge(left, right) when is_map(left) and is_map(right) do
    Map.merge(left, right, fn
      _key, left_val, right_val when is_map(left_val) and is_map(right_val) ->
        deep_merge(left_val, right_val)

      _key, _left_val, right_val ->
        right_val
    end)
  end

  defp deep_merge(_left, right), do: right
end
