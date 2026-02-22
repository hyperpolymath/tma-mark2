# SPDX-License-Identifier: AGPL-3.0-or-later

defmodule EtmaHandler.Application do
  @moduledoc """
  eTMA Handler — Master Supervision Tree.

  This module defines the runtime hierarchy for the marking system. 
  It prioritizes the durability of student data by ensuring the 
  Repository (Repo) starts before any interactive or ingest components.
  """

  use Application
  require Logger

  @impl true
  def start(_type, _args) do
    # SUPERVISION TREE:
    # 1. Repo: The CubDB storage kernel. Must be first.
    # 2. Settings: Global configuration manager.
    # 3. Telemetry: Performance monitoring.
    # 4. PubSub: Real-time event bus for LiveView UI.
    # 5. Endpoint: The web interface (Bandit server).
    # 6. Bouncer: The automated file ingestion service (watcher).
    children = [
      {EtmaHandler.Repo, data_dir: get_data_dir()},
      EtmaHandler.Settings,
      EtmaHandlerWeb.Telemetry,
      {Phoenix.PubSub, name: EtmaHandler.PubSub},
      EtmaHandlerWeb.Endpoint,
      maybe_start_bouncer()
    ]
    |> Enum.reject(&is_nil/1)

    # POLICY: :one_for_one ensures local restarts don't cascade into 
    # unrelated services.
    opts = [strategy: :one_for_one, name: EtmaHandler.Supervisor]

    Logger.info("Starting eTMA Handler high-assurance gateway...")
    Supervisor.start_link(children, opts)
  end

  defp get_data_dir, do: Application.get_env(:etma_handler, :data_dir)
  defp maybe_start_bouncer, do: if(Application.get_env(:etma_handler, :auto_ingest), do: EtmaHandler.Bouncer, else: nil)
end
