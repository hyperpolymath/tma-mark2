# SPDX-License-Identifier: AGPL-3.0-or-later
defmodule EtmaHandlerWeb.ApiController do
  @moduledoc """
  API endpoints for eTMA Handler.

  Provides:
  - Health check endpoint for container orchestration
  - Version information
  """

  use EtmaHandlerWeb, :controller

  @doc """
  Health check endpoint.

  Returns 200 OK if the application is running and healthy.
  Used by container health checks and load balancers.
  """
  def health(conn, _params) do
    # Basic health check - verify CubDB is accessible
    health_status =
      try do
        # Try to access the repo
        case EtmaHandler.Repo.get(:health_check) do
          _ -> :ok
        end
      rescue
        _ -> :error
      end

    case health_status do
      :ok ->
        json(conn, %{
          status: "healthy",
          version: EtmaHandler.version(),
          timestamp: DateTime.utc_now()
        })

      :error ->
        conn
        |> put_status(:service_unavailable)
        |> json(%{
          status: "unhealthy",
          version: EtmaHandler.version(),
          timestamp: DateTime.utc_now()
        })
    end
  end
end
