# SPDX-FileCopyrightText: 2024 eTMA Handler Contributors
# SPDX-License-Identifier: MIT

defmodule EtmaHandlerWeb.Plugs.SecurityHeaders do
  @moduledoc """
  The Plastic Prison - Maximum Security HTTP Headers.

  Every security header cranked to maximum. No escape routes.
  If Magneto tried to break out of this, he'd need to find
  a vulnerability in the laws of physics, not our headers.

  ## Headers Applied

  | Header | Value | Purpose |
  |--------|-------|---------|
  | Strict-Transport-Security | max-age=63072000; includeSubDomains; preload | HTTPS forever, preload list |
  | Content-Security-Policy | strict, nonce-based | No inline scripts, no eval |
  | X-Content-Type-Options | nosniff | No MIME sniffing |
  | X-Frame-Options | DENY | No framing ever |
  | X-XSS-Protection | 0 | Disabled (CSP handles this) |
  | Referrer-Policy | strict-origin-when-cross-origin | Minimal referrer leakage |
  | Permissions-Policy | deny all | No device access |
  | Cross-Origin-Embedder-Policy | require-corp | Strict embedding |
  | Cross-Origin-Opener-Policy | same-origin | Isolate browsing context |
  | Cross-Origin-Resource-Policy | same-origin | No cross-origin reads |
  | X-Permitted-Cross-Domain-Policies | none | No Flash/PDF policies |
  | X-Download-Options | noopen | IE download protection |
  | X-DNS-Prefetch-Control | off | No DNS prefetching |
  | Cache-Control | no-store, no-cache, must-revalidate | No caching sensitive data |

  ## CSP Nonce

  Each request gets a unique cryptographic nonce for inline scripts.
  Access via `conn.assigns.csp_nonce` in templates.
  """

  import Plug.Conn
  require Logger

  @behaviour Plug

  # Two years in seconds (maximum recommended)
  @hsts_max_age 63_072_000

  @impl true
  def init(opts), do: opts

  @impl true
  def call(conn, _opts) do
    nonce = generate_nonce()

    conn
    |> assign(:csp_nonce, nonce)
    |> put_hsts_header()
    |> put_csp_header(nonce)
    |> put_security_headers()
    |> put_permissions_policy()
    |> put_cross_origin_headers()
  end

  # ============================================================================
  # HSTS - HTTP Strict Transport Security
  # ============================================================================

  defp put_hsts_header(conn) do
    # Only in production (HTTPS)
    if https_enabled?() do
      put_resp_header(
        conn,
        "strict-transport-security",
        "max-age=#{@hsts_max_age}; includeSubDomains; preload"
      )
    else
      conn
    end
  end

  # ============================================================================
  # Content Security Policy - The Big One
  # ============================================================================

  defp put_csp_header(conn, nonce) do
    csp = build_csp(nonce)

    conn
    |> put_resp_header("content-security-policy", csp)
    # Report-only for debugging (remove in production)
    # |> put_resp_header("content-security-policy-report-only", csp)
  end

  defp build_csp(nonce) do
    directives = [
      # Default: nothing allowed unless explicitly permitted
      "default-src 'none'",

      # Scripts: only from same origin with nonce (no inline, no eval)
      "script-src 'self' 'nonce-#{nonce}' 'strict-dynamic'",

      # Styles: same origin + nonce for LiveView
      "style-src 'self' 'nonce-#{nonce}'",

      # Images: same origin + data URIs (for inline images)
      "img-src 'self' data: blob:",

      # Fonts: same origin
      "font-src 'self'",

      # Connect: same origin + WebSocket for LiveView
      "connect-src 'self' wss: ws:",

      # Forms: same origin only
      "form-action 'self'",

      # Frame ancestors: nobody can frame us
      "frame-ancestors 'none'",

      # Base URI: prevent base tag injection
      "base-uri 'self'",

      # Object/embed: blocked
      "object-src 'none'",

      # Media: same origin
      "media-src 'self'",

      # Workers: same origin
      "worker-src 'self' blob:",

      # Child/frame: none
      "child-src 'none'",

      # Manifest: same origin
      "manifest-src 'self'",

      # Prefetch: same origin
      "prefetch-src 'self'",

      # Upgrade insecure requests in production
      if(https_enabled?(), do: "upgrade-insecure-requests", else: nil),

      # Block mixed content
      if(https_enabled?(), do: "block-all-mixed-content", else: nil),

      # Require trusted types (experimental but powerful)
      # "require-trusted-types-for 'script'",

      # Report violations (configure endpoint)
      # "report-uri /api/csp-report",
      # "report-to csp-endpoint"
    ]

    directives
    |> Enum.reject(&is_nil/1)
    |> Enum.join("; ")
  end

  # ============================================================================
  # Standard Security Headers
  # ============================================================================

  defp put_security_headers(conn) do
    conn
    # Prevent MIME type sniffing
    |> put_resp_header("x-content-type-options", "nosniff")

    # Prevent clickjacking (CSP frame-ancestors is better but belt+suspenders)
    |> put_resp_header("x-frame-options", "DENY")

    # Disable XSS auditor (causes more problems than it solves, CSP handles this)
    |> put_resp_header("x-xss-protection", "0")

    # Control referrer information leakage
    |> put_resp_header("referrer-policy", "strict-origin-when-cross-origin")

    # Prevent Flash/Acrobat cross-domain policy loading
    |> put_resp_header("x-permitted-cross-domain-policies", "none")

    # IE-specific download protection
    |> put_resp_header("x-download-options", "noopen")

    # Disable DNS prefetching (privacy)
    |> put_resp_header("x-dns-prefetch-control", "off")

    # Prevent caching of sensitive data
    |> put_resp_header("cache-control", "no-store, no-cache, must-revalidate, private")
    |> put_resp_header("pragma", "no-cache")
    |> put_resp_header("expires", "0")
  end

  # ============================================================================
  # Permissions Policy (formerly Feature-Policy)
  # ============================================================================

  defp put_permissions_policy(conn) do
    # Deny all the things we don't need
    policy = [
      "accelerometer=()",
      "ambient-light-sensor=()",
      "autoplay=()",
      "battery=()",
      "camera=()",
      "cross-origin-isolated=()",
      "display-capture=()",
      "document-domain=()",
      "encrypted-media=()",
      "execution-while-not-rendered=()",
      "execution-while-out-of-viewport=()",
      "fullscreen=(self)",  # Allow fullscreen for document viewer
      "geolocation=()",
      "gyroscope=()",
      "hid=()",
      "idle-detection=()",
      "magnetometer=()",
      "microphone=()",
      "midi=()",
      "navigation-override=()",
      "payment=()",
      "picture-in-picture=()",
      "publickey-credentials-get=(self)",  # Allow WebAuthn
      "screen-wake-lock=()",
      "serial=()",
      "sync-xhr=()",
      "usb=()",
      "web-share=()",
      "xr-spatial-tracking=()"
    ]

    put_resp_header(conn, "permissions-policy", Enum.join(policy, ", "))
  end

  # ============================================================================
  # Cross-Origin Isolation Headers
  # ============================================================================

  defp put_cross_origin_headers(conn) do
    conn
    # Require CORP for embedded resources
    |> put_resp_header("cross-origin-embedder-policy", "require-corp")

    # Isolate browsing context from cross-origin documents
    |> put_resp_header("cross-origin-opener-policy", "same-origin")

    # Prevent cross-origin reads
    |> put_resp_header("cross-origin-resource-policy", "same-origin")
  end

  # ============================================================================
  # Helpers
  # ============================================================================

  defp generate_nonce do
    :crypto.strong_rand_bytes(16) |> Base.encode64(padding: false)
  end

  defp https_enabled? do
    Application.get_env(:etma_handler, :force_ssl, false) or
      Application.get_env(:etma_handler, EtmaHandlerWeb.Endpoint)[:https] != nil
  end
end
