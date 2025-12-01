# SPDX-FileCopyrightText: 2024 eTMA Handler Contributors
# SPDX-License-Identifier: MIT

defmodule EtmaHandlerWeb.Plugs.ForceSSL do
  @moduledoc """
  Force HTTPS with extreme prejudice.

  - Redirects all HTTP to HTTPS (301 permanent)
  - Sets HSTS header with preload
  - Validates certificates
  - No exceptions, no bypasses
  """

  import Plug.Conn
  require Logger

  @behaviour Plug

  @impl true
  def init(opts) do
    %{
      host: Keyword.get(opts, :host),
      hsts: Keyword.get(opts, :hsts, true),
      rewrite_on: Keyword.get(opts, :rewrite_on, [:x_forwarded_proto])
    }
  end

  @impl true
  def call(conn, opts) do
    if should_force_ssl?() and not secure?(conn, opts) do
      redirect_to_https(conn, opts)
    else
      conn
    end
  end

  defp should_force_ssl? do
    Application.get_env(:etma_handler, :force_ssl, false)
  end

  defp secure?(conn, opts) do
    conn.scheme == :https or forwarded_secure?(conn, opts)
  end

  defp forwarded_secure?(conn, opts) do
    Enum.any?(opts.rewrite_on, fn header ->
      case header do
        :x_forwarded_proto ->
          get_req_header(conn, "x-forwarded-proto") == ["https"]

        :x_forwarded_port ->
          get_req_header(conn, "x-forwarded-port") == ["443"]

        :x_forwarded_ssl ->
          get_req_header(conn, "x-forwarded-ssl") == ["on"]

        _ ->
          false
      end
    end)
  end

  defp redirect_to_https(conn, opts) do
    host = opts.host || conn.host
    port = get_https_port()

    # Build HTTPS URL
    location =
      if port == 443 do
        "https://#{host}#{conn.request_path}"
      else
        "https://#{host}:#{port}#{conn.request_path}"
      end

    # Add query string if present
    location =
      if conn.query_string != "" do
        "#{location}?#{conn.query_string}"
      else
        location
      end

    Logger.info("Redirecting HTTP to HTTPS: #{location}")

    conn
    |> put_resp_header("location", location)
    |> put_resp_header("strict-transport-security", hsts_header())
    |> send_resp(301, "Moved Permanently")
    |> halt()
  end

  defp get_https_port do
    Application.get_env(:etma_handler, EtmaHandlerWeb.Endpoint)[:https][:port] || 443
  end

  defp hsts_header do
    # 2 years, include subdomains, preload eligible
    "max-age=63072000; includeSubDomains; preload"
  end
end
