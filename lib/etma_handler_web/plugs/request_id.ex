# SPDX-FileCopyrightText: 2024 eTMA Handler Contributors
# SPDX-License-Identifier: MIT

defmodule EtmaHandlerWeb.Plugs.RequestId do
  @moduledoc """
  Generate unique request IDs for tracing and debugging.

  Each request gets a cryptographically random ID that:
  - Appears in logs
  - Returns in X-Request-ID header
  - Can be used for distributed tracing
  """

  import Plug.Conn

  @behaviour Plug

  @impl true
  def init(opts), do: opts

  @impl true
  def call(conn, _opts) do
    request_id = generate_request_id()

    conn
    |> assign(:request_id, request_id)
    |> put_resp_header("x-request-id", request_id)
  end

  defp generate_request_id do
    # 128-bit random ID, URL-safe base64
    :crypto.strong_rand_bytes(16)
    |> Base.url_encode64(padding: false)
  end
end
