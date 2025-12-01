# SPDX-FileCopyrightText: 2024 eTMA Handler Contributors
# SPDX-License-Identifier: MIT
#
# Security Configuration
# ======================
#
# This file contains security settings. Import it from runtime.exs.
#
# For maximum security in production:
# 1. Set FORCE_SSL=true
# 2. Provide TLS certificates
# 3. Set strong SECRET_KEY_BASE
# 4. Enable all security headers

import Config

# Force SSL (HTTPS only)
config :etma_handler, :force_ssl, System.get_env("FORCE_SSL") == "true"

# TLS/HTTPS Configuration
if System.get_env("TLS_CERT_PATH") do
  config :etma_handler, EtmaHandlerWeb.Endpoint,
    https: [
      port: String.to_integer(System.get_env("HTTPS_PORT") || "443"),
      cipher_suite: :strong,
      certfile: System.get_env("TLS_CERT_PATH"),
      keyfile: System.get_env("TLS_KEY_PATH"),
      # Optional: CA bundle for chain
      cacertfile: System.get_env("TLS_CA_PATH"),
      # TLS 1.3 only
      versions: [:"tlsv1.3"],
      # Strong ciphers
      ciphers: [
        "TLS_CHACHA20_POLY1305_SHA256",
        "TLS_AES_256_GCM_SHA384",
        "TLS_AES_128_GCM_SHA256"
      ],
      # ECDH curves
      eccs: [:x448, :x25519, :secp521r1, :secp384r1],
      # Honor cipher order
      honor_cipher_order: true,
      # Secure renegotiation
      secure_renegotiate: true,
      # HTTP/2
      alpn_preferred_protocols: ["h2", "http/1.1"]
    ]
end

# Session security
config :etma_handler, EtmaHandlerWeb.Endpoint,
  secret_key_base: System.get_env("SECRET_KEY_BASE") ||
    raise("""
    environment variable SECRET_KEY_BASE is missing.
    Generate one with: mix phx.gen.secret
    """),
  session: [
    key: "_etma_session",
    same_site: "Strict",
    secure: System.get_env("FORCE_SSL") == "true",
    http_only: true,
    max_age: 86_400,  # 24 hours
    signing_salt: System.get_env("SESSION_SIGNING_SALT") || raise("SESSION_SIGNING_SALT missing"),
    encryption_salt: System.get_env("SESSION_ENCRYPTION_SALT") || raise("SESSION_ENCRYPTION_SALT missing")
  ]

# Rate limiting
config :etma_handler, :rate_limit,
  enabled: System.get_env("RATE_LIMIT_ENABLED", "true") == "true",
  general: String.to_integer(System.get_env("RATE_LIMIT_GENERAL") || "100"),
  auth: String.to_integer(System.get_env("RATE_LIMIT_AUTH") || "5"),
  api: String.to_integer(System.get_env("RATE_LIMIT_API") || "60")

# WebAuthn configuration
config :etma_handler, :webauthn,
  rp_id: System.get_env("WEBAUTHN_RP_ID") || "localhost",
  rp_name: System.get_env("WEBAUTHN_RP_NAME") || "eTMA Handler",
  origin: System.get_env("WEBAUTHN_ORIGIN") || "http://localhost:4000"

# Content Security Policy
config :etma_handler, :csp,
  enabled: System.get_env("CSP_ENABLED", "true") == "true",
  report_uri: System.get_env("CSP_REPORT_URI")
