# SPDX-License-Identifier: AGPL-3.0-or-later
defmodule EtmaHandler.Application do
  @moduledoc """
  The eTMA Handler Application Supervisor.

  Starts and supervises all core services:
  - CubDB Repository (crash-proof data storage)
  - Phoenix Endpoint (web interface)
  - File Watcher (automatic ingestion from Downloads)
  - Vault Key Manager (encryption keys in memory)

  The supervision strategy is `:one_for_one` - if a child crashes,
  only that child is restarted. This ensures the UI remains available
  even if the file watcher encounters issues.
  """

  use Application
  require Logger

  @impl true
  def start(_type, _args) do
    children = [
      # 1. The Crash-Proof Database (CubDB)
      # Must start first as other services depend on it
      {EtmaHandler.Repo, data_dir: get_data_dir()},

      # 2. The Telemetry (Metrics collection)
      EtmaHandlerWeb.Telemetry,

      # 3. The PubSub (For real-time UI updates via LiveView)
      {Phoenix.PubSub, name: EtmaHandler.PubSub},

      # 4. The Web Server (Bandit - Fast & Modern)
      EtmaHandlerWeb.Endpoint,

      # 5. The "Bouncer" (Watches Downloads folder for student files)
      # Only start if auto_ingest is enabled
      maybe_start_bouncer(),

      # 6. The "Vault" Key Manager (holds Argon2id derived keys in memory)
      # EtmaHandler.Vault.KeyManager  # Uncomment when implemented
    ]
    |> Enum.reject(&is_nil/1)

    opts = [strategy: :one_for_one, name: EtmaHandler.Supervisor]

    Logger.info("Starting eTMA Handler v#{version()}")
    Logger.info("Data directory: #{get_data_dir()}")

    case Supervisor.start_link(children, opts) do
      {:ok, pid} ->
        Logger.info("eTMA Handler started successfully")
        {:ok, pid}

      {:error, reason} ->
        Logger.error("Failed to start eTMA Handler: #{inspect(reason)}")
        {:error, reason}
    end
  end

  @impl true
  def config_change(changed, _new, removed) do
    EtmaHandlerWeb.Endpoint.config_change(changed, removed)
    :ok
  end

  # --- Configuration Helpers ---

  defp get_data_dir do
    Application.get_env(:etma_handler, :data_dir, default_data_dir())
  end

  defp default_data_dir do
    case :os.type() do
      {:win32, _} ->
        Path.join([System.get_env("APPDATA", ""), "EtmaHandler", "data"])

      {:unix, :darwin} ->
        Path.join([System.user_home!(), "Library", "Application Support", "EtmaHandler", "data"])

      {:unix, _} ->
        # XDG Base Directory Specification
        xdg_data = System.get_env("XDG_DATA_HOME", Path.join(System.user_home!(), ".local/share"))
        Path.join([xdg_data, "etma_handler", "data"])
    end
  end

  defp get_downloads_dir do
    Application.get_env(:etma_handler, :downloads_dir, default_downloads_dir())
  end

  defp default_downloads_dir do
    case :os.type() do
      {:win32, _} ->
        Path.join([System.user_home!(), "Downloads"])

      {:unix, :darwin} ->
        Path.join([System.user_home!(), "Downloads"])

      {:unix, _} ->
        # XDG User Directory
        xdg_downloads =
          System.get_env("XDG_DOWNLOAD_DIR", Path.join(System.user_home!(), "Downloads"))

        xdg_downloads
    end
  end

  defp maybe_start_bouncer do
    if Application.get_env(:etma_handler, :auto_ingest, true) do
      downloads_dir = get_downloads_dir()

      if File.dir?(downloads_dir) do
        {EtmaHandler.Bouncer, download_dir: downloads_dir}
      else
        Logger.warning("Downloads directory not found: #{downloads_dir}")
        nil
      end
    else
      nil
    end
  end

  defp version do
    case :application.get_key(:etma_handler, :vsn) do
      {:ok, vsn} -> List.to_string(vsn)
      _ -> "dev"
    end
  end
end
