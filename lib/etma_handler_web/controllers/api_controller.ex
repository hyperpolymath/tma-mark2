# SPDX-License-Identifier: MPL-2.0
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
    case Process.whereis(EtmaHandler.Repo.cubdb()) do
      pid when is_pid(pid) ->
        json(conn, %{
          status: "healthy",
          version: EtmaHandler.version(),
          timestamp: DateTime.utc_now()
        })

      nil ->
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
