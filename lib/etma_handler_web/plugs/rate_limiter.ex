# SPDX-FileCopyrightText: 2024 eTMA Handler Contributors
# SPDX-License-Identifier: MIT

defmodule EtmaHandlerWeb.Plugs.RateLimiter do
  @moduledoc """
  Rate limiting to prevent abuse.

  Uses a token bucket algorithm with ETS storage.
  Different limits for different endpoint types.

  ## Limits

  | Endpoint Type | Requests | Window |
  |---------------|----------|--------|
  | General | 100 | 1 minute |
  | Auth (login) | 5 | 1 minute |
  | API | 60 | 1 minute |
  | File upload | 10 | 1 minute |

  ## Headers

  Returns standard rate limit headers:
  - X-RateLimit-Limit: Maximum requests allowed
  - X-RateLimit-Remaining: Requests remaining
  - X-RateLimit-Reset: Unix timestamp when limit resets
  - Retry-After: Seconds until retry allowed (when limited)
  """

  import Plug.Conn
  require Logger

  @behaviour Plug

  # ETS table for rate limiting
  @table :etma_rate_limits

  # Default limits (requests per minute)
  @default_limit 100
  @auth_limit 5
  @api_limit 60
  @upload_limit 10

  # Window in milliseconds
  @window_ms 60_000

  @impl true
  def init(opts) do
    # Ensure ETS table exists
    ensure_table_exists()

    %{
      limit: Keyword.get(opts, :limit, @default_limit),
      window_ms: Keyword.get(opts, :window_ms, @window_ms)
    }
  end

  @impl true
  def call(conn, opts) do
    key = rate_limit_key(conn)
    limit = get_limit_for_path(conn.request_path, opts.limit)
    window_ms = opts.window_ms

    case check_rate_limit(key, limit, window_ms) do
      {:ok, remaining, reset_at} ->
        conn
        |> put_rate_limit_headers(limit, remaining, reset_at)

      {:error, :rate_limited, reset_at} ->
        conn
        |> put_rate_limit_headers(limit, 0, reset_at)
        |> put_resp_header("retry-after", Integer.to_string(seconds_until(reset_at)))
        |> send_resp(429, "Too Many Requests")
        |> halt()
    end
  end

  # ============================================================================
  # Rate Limit Logic
  # ============================================================================

  defp check_rate_limit(key, limit, window_ms) do
    now = System.system_time(:millisecond)
    window_start = now - window_ms

    # Atomic operation: count requests in window and add new one
    case :ets.lookup(@table, key) do
      [] ->
        # First request
        :ets.insert(@table, {key, [{now, 1}]})
        {:ok, limit - 1, now + window_ms}

      [{^key, requests}] ->
        # Filter out old requests
        recent = Enum.filter(requests, fn {ts, _} -> ts > window_start end)
        count = Enum.reduce(recent, 0, fn {_, c}, acc -> acc + c end)

        if count >= limit do
          # Rate limited
          oldest = recent |> Enum.map(fn {ts, _} -> ts end) |> Enum.min()
          reset_at = oldest + window_ms
          {:error, :rate_limited, reset_at}
        else
          # Add request
          :ets.insert(@table, {key, [{now, 1} | recent]})
          {:ok, limit - count - 1, now + window_ms}
        end
    end
  end

  defp rate_limit_key(conn) do
    # Use IP + user agent for better fingerprinting
    ip = get_client_ip(conn)
    ua_hash = :crypto.hash(:sha256, get_req_header(conn, "user-agent") |> List.first() || "")
             |> binary_part(0, 8)
             |> Base.encode16(case: :lower)

    "#{ip}:#{ua_hash}"
  end

  defp get_client_ip(conn) do
    # Check forwarded headers first (for proxies)
    forwarded_for = get_req_header(conn, "x-forwarded-for") |> List.first()
    real_ip = get_req_header(conn, "x-real-ip") |> List.first()

    cond do
      forwarded_for ->
        forwarded_for |> String.split(",") |> List.first() |> String.trim()

      real_ip ->
        real_ip

      true ->
        conn.remote_ip |> :inet.ntoa() |> to_string()
    end
  end

  defp get_limit_for_path(path, default) do
    cond do
      String.starts_with?(path, "/auth") or String.starts_with?(path, "/login") ->
        @auth_limit

      String.starts_with?(path, "/api") ->
        @api_limit

      String.starts_with?(path, "/upload") ->
        @upload_limit

      true ->
        default
    end
  end

  # ============================================================================
  # Headers
  # ============================================================================

  defp put_rate_limit_headers(conn, limit, remaining, reset_at) do
    reset_unix = div(reset_at, 1000)

    conn
    |> put_resp_header("x-ratelimit-limit", Integer.to_string(limit))
    |> put_resp_header("x-ratelimit-remaining", Integer.to_string(max(0, remaining)))
    |> put_resp_header("x-ratelimit-reset", Integer.to_string(reset_unix))
  end

  defp seconds_until(reset_at) do
    now = System.system_time(:millisecond)
    max(1, div(reset_at - now, 1000))
  end

  # ============================================================================
  # ETS Management
  # ============================================================================

  defp ensure_table_exists do
    case :ets.whereis(@table) do
      :undefined ->
        :ets.new(@table, [:named_table, :public, :set, read_concurrency: true, write_concurrency: true])

      _ ->
        :ok
    end
  end

  @doc """
  Clean up old entries. Call periodically via a scheduled task.
  """
  def cleanup do
    now = System.system_time(:millisecond)
    window_start = now - @window_ms

    :ets.foldl(
      fn {key, requests}, acc ->
        recent = Enum.filter(requests, fn {ts, _} -> ts > window_start end)
        if recent == [] do
          :ets.delete(@table, key)
        else
          :ets.insert(@table, {key, recent})
        end
        acc
      end,
      :ok,
      @table
    )
  end
end
