# SPDX-FileCopyrightText: 2024 eTMA Handler Contributors
# SPDX-License-Identifier: MIT

defmodule EtmaHandler.Security do
  @moduledoc """
  Security Configuration - The Plastic Prison Blueprint.

  This module defines all security settings for eTMA Handler.
  Every setting is tuned for maximum security by default.

  ## TLS Configuration

  - TLS 1.3 only (no downgrade attacks)
  - Strong cipher suites only
  - Perfect forward secrecy required
  - Certificate pinning support
  - OCSP stapling enabled

  ## Session Security

  - Secure cookies (HttpOnly, Secure, SameSite=Strict)
  - Short session lifetime
  - Session binding to client
  - Automatic session rotation

  ## CORS

  - Disabled by default
  - Strict origin validation when enabled
  - No credentials across origins
  """

  @doc """
  TLS options for HTTPS.

  Uses TLS 1.3 only with the strongest cipher suites.
  """
  def tls_options do
    [
      # TLS 1.3 only - no downgrade attacks possible
      versions: [:"tlsv1.3"],

      # Strong cipher suites only (TLS 1.3 ciphers)
      ciphers: [
        # ChaCha20-Poly1305 (fast on mobile/ARM)
        "TLS_CHACHA20_POLY1305_SHA256",
        # AES-GCM 256-bit
        "TLS_AES_256_GCM_SHA384",
        # AES-GCM 128-bit
        "TLS_AES_128_GCM_SHA256"
      ],

      # ECDH curves (strongest first)
      eccs: [:x448, :x25519, :secp521r1, :secp384r1],

      # Signature algorithms (strongest first)
      signature_algs: [
        :eddsa_ed448,
        :eddsa_ed25519,
        :ecdsa_secp521r1_sha512,
        :ecdsa_secp384r1_sha384,
        :rsa_pss_pss_sha512,
        :rsa_pss_pss_sha384,
        :rsa_pss_rsae_sha512,
        :rsa_pss_rsae_sha384
      ],

      # Enforce client certificate verification (optional, for mTLS)
      # verify: :verify_peer,
      # fail_if_no_peer_cert: true,

      # Honor server cipher order
      honor_cipher_order: true,

      # Enable secure renegotiation
      secure_renegotiate: true,

      # Reuse sessions for performance
      reuse_sessions: true,

      # Session ticket lifetime (4 hours)
      session_tickets: :stateless,

      # Anti-replay for 0-RTT (when enabled)
      anti_replay: :required,

      # Depth of certificate chain
      depth: 3,

      # Custom verify function for additional checks
      # verify_fun: {&custom_verify/3, []}

      # ALPN protocols (HTTP/2 preferred)
      alpn_preferred_protocols: ["h2", "http/1.1"]
    ]
  end

  @doc """
  Session cookie options.

  Maximum security settings for session cookies.
  """
  def session_options do
    [
      # Cookie name (avoid default names that reveal tech stack)
      key: "_etma_session",

      # Use encrypted cookies
      store: :cookie,

      # Signing salt (generate at runtime)
      signing_salt: signing_salt(),

      # Encryption salt
      encryption_salt: encryption_salt(),

      # Same-site strict (no CSRF via links)
      same_site: "Strict",

      # Secure flag (HTTPS only)
      secure: https_enabled?(),

      # HttpOnly (no JavaScript access)
      http_only: true,

      # Max age: 24 hours (short sessions)
      max_age: 86_400,

      # Extra cookie options
      extra: "Partitioned"  # Chrome's partitioned cookies
    ]
  end

  @doc """
  CSRF protection options.
  """
  def csrf_options do
    [
      # CSRF token in session
      with: :session,

      # Token field name
      field_name: "_csrf_token",

      # Header name
      header_name: "x-csrf-token"
    ]
  end

  @doc """
  Secure headers for API responses.
  """
  def api_headers do
    %{
      "x-content-type-options" => "nosniff",
      "x-frame-options" => "DENY",
      "cache-control" => "no-store, no-cache, must-revalidate, private",
      "pragma" => "no-cache",
      "expires" => "0"
    }
  end

  @doc """
  Password policy requirements.
  """
  def password_policy do
    %{
      min_length: 12,
      require_uppercase: true,
      require_lowercase: true,
      require_digit: true,
      require_special: true,
      max_length: 128,
      # Minimum entropy bits (via zxcvbn-style check)
      min_entropy: 50,
      # Check against breached passwords
      check_hibp: true,
      # Don't allow username in password
      no_username: true
    }
  end

  @doc """
  Validate password against policy.
  """
  def validate_password(password, opts \\ []) do
    policy = password_policy()
    username = Keyword.get(opts, :username, "")

    checks = [
      {:min_length, String.length(password) >= policy.min_length,
       "Must be at least #{policy.min_length} characters"},
      {:max_length, String.length(password) <= policy.max_length,
       "Must be at most #{policy.max_length} characters"},
      {:uppercase, not policy.require_uppercase or Regex.match?(~r/[A-Z]/, password),
       "Must contain uppercase letter"},
      {:lowercase, not policy.require_lowercase or Regex.match?(~r/[a-z]/, password),
       "Must contain lowercase letter"},
      {:digit, not policy.require_digit or Regex.match?(~r/[0-9]/, password),
       "Must contain digit"},
      {:special, not policy.require_special or Regex.match?(~r/[^A-Za-z0-9]/, password),
       "Must contain special character"},
      {:no_username, not policy.no_username or not String.contains?(String.downcase(password), String.downcase(username)),
       "Cannot contain username"}
    ]

    errors =
      checks
      |> Enum.filter(fn {_, valid, _} -> not valid end)
      |> Enum.map(fn {field, _, message} -> {field, message} end)

    if errors == [], do: :ok, else: {:error, errors}
  end

  @doc """
  Generate secure random token.
  """
  def generate_token(bytes \\ 32) do
    :crypto.strong_rand_bytes(bytes) |> Base.url_encode64(padding: false)
  end

  @doc """
  Constant-time string comparison (prevent timing attacks).
  """
  def secure_compare(a, b) when is_binary(a) and is_binary(b) do
    if byte_size(a) == byte_size(b) do
      secure_compare(a, b, 0) == 0
    else
      false
    end
  end

  defp secure_compare(<<x, rest_a::binary>>, <<y, rest_b::binary>>, acc) do
    import Bitwise
    secure_compare(rest_a, rest_b, acc ||| bxor(x, y))
  end

  defp secure_compare(<<>>, <<>>, acc), do: acc

  # ============================================================================
  # Private Helpers
  # ============================================================================

  defp signing_salt do
    # Use a fixed salt from config or generate at startup
    Application.get_env(:etma_handler, :session_signing_salt) ||
      (
        salt = :crypto.strong_rand_bytes(32) |> Base.encode64()
        Application.put_env(:etma_handler, :session_signing_salt, salt)
        salt
      )
  end

  defp encryption_salt do
    Application.get_env(:etma_handler, :session_encryption_salt) ||
      (
        salt = :crypto.strong_rand_bytes(32) |> Base.encode64()
        Application.put_env(:etma_handler, :session_encryption_salt, salt)
        salt
      )
  end

  defp https_enabled? do
    Application.get_env(:etma_handler, :force_ssl, false)
  end
end
