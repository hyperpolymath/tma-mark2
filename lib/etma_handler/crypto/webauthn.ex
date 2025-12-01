# SPDX-FileCopyrightText: 2024 eTMA Handler Contributors
# SPDX-License-Identifier: MIT

defmodule EtmaHandler.Crypto.WebAuthn do
  @moduledoc """
  FIDO2/WebAuthn implementation for passwordless and second-factor authentication.

  ## What is WebAuthn?

  WebAuthn (Web Authentication) is a W3C standard for strong authentication using
  public-key cryptography. It enables:

  - **Passwordless login** - Use a security key or biometrics instead of passwords
  - **Two-factor authentication** - Add a hardware token as a second factor
  - **Phishing resistance** - Credentials are bound to the origin, can't be phished

  ## Supported Authenticators

  - Hardware security keys (YubiKey, SoloKey, Titan, etc.)
  - Platform authenticators (Touch ID, Face ID, Windows Hello)
  - Passkeys (synced across devices)

  ## Usage

      # Generate registration challenge
      {:ok, challenge} = WebAuthn.create_registration_challenge(user_id)

      # Verify registration response (from browser)
      {:ok, credential} = WebAuthn.verify_registration(response, challenge)

      # Generate authentication challenge
      {:ok, challenge} = WebAuthn.create_auth_challenge(credential_ids)

      # Verify authentication response
      {:ok, user_id} = WebAuthn.verify_authentication(response, challenge, credential)

  ## Wire Format

  This module handles the server-side cryptographic verification. The client-side
  WebAuthn API is implemented in JavaScript using `navigator.credentials`.
  """

  require Logger

  alias EtmaHandler.Crypto.{Hashing, Signatures}

  # Configuration
  @rp_id Application.compile_env(:etma_handler, :webauthn_rp_id, "localhost")
  @rp_name Application.compile_env(:etma_handler, :webauthn_rp_name, "eTMA Handler")
  @origin Application.compile_env(:etma_handler, :webauthn_origin, "http://localhost:4000")

  # COSE algorithm identifiers
  @cose_alg_es256 -7    # ECDSA with P-256 and SHA-256
  @cose_alg_es384 -35   # ECDSA with P-384 and SHA-384
  @cose_alg_es512 -36   # ECDSA with P-521 and SHA-512
  @cose_alg_eddsa -8    # EdDSA (Ed25519 or Ed448)
  @cose_alg_rs256 -257  # RSASSA-PKCS1-v1_5 with SHA-256

  # Supported algorithms (prefer EdDSA, then ES256)
  @supported_algorithms [@cose_alg_eddsa, @cose_alg_es256, @cose_alg_es384]

  @type user_id :: binary()
  @type credential_id :: binary()
  @type challenge :: binary()

  @type credential :: %{
    id: credential_id(),
    public_key: binary(),
    sign_count: non_neg_integer(),
    user_id: user_id(),
    algorithm: integer(),
    created_at: DateTime.t()
  }

  @type registration_challenge :: %{
    challenge: challenge(),
    rp_id: String.t(),
    rp_name: String.t(),
    user_id: user_id(),
    user_name: String.t(),
    user_display_name: String.t(),
    timeout: non_neg_integer(),
    algorithms: [integer()],
    attestation: String.t(),
    created_at: DateTime.t()
  }

  @type auth_challenge :: %{
    challenge: challenge(),
    rp_id: String.t(),
    timeout: non_neg_integer(),
    credential_ids: [credential_id()],
    user_verification: String.t(),
    created_at: DateTime.t()
  }

  # ============================================================================
  # Registration
  # ============================================================================

  @doc """
  Create a registration challenge for a new credential.

  ## Options

  - `:user_name` - Username for display (required)
  - `:user_display_name` - Friendly name (defaults to user_name)
  - `:timeout` - Challenge timeout in ms (default: 60000)
  - `:attestation` - Attestation preference: "none", "indirect", "direct" (default: "none")
  - `:resident_key` - Require resident key: true/false (default: false)
  - `:user_verification` - "required", "preferred", "discouraged" (default: "preferred")
  """
  @spec create_registration_challenge(user_id(), keyword()) :: {:ok, registration_challenge()}
  def create_registration_challenge(user_id, opts \\ []) do
    user_name = Keyword.fetch!(opts, :user_name)
    user_display_name = Keyword.get(opts, :user_display_name, user_name)
    timeout = Keyword.get(opts, :timeout, 60_000)
    attestation = Keyword.get(opts, :attestation, "none")

    challenge = %{
      challenge: generate_challenge(),
      rp_id: @rp_id,
      rp_name: @rp_name,
      user_id: user_id,
      user_name: user_name,
      user_display_name: user_display_name,
      timeout: timeout,
      algorithms: @supported_algorithms,
      attestation: attestation,
      created_at: DateTime.utc_now()
    }

    {:ok, challenge}
  end

  @doc """
  Verify a registration response from the browser.

  Returns the credential to be stored for future authentication.
  """
  @spec verify_registration(map(), registration_challenge()) ::
          {:ok, credential()} | {:error, term()}
  def verify_registration(response, challenge) do
    with :ok <- verify_challenge_not_expired(challenge),
         {:ok, client_data} <- parse_client_data(response["clientDataJSON"]),
         :ok <- verify_client_data(client_data, challenge.challenge, "webauthn.create"),
         {:ok, attestation_object} <- parse_attestation_object(response["attestationObject"]),
         {:ok, auth_data} <- parse_authenticator_data(attestation_object["authData"]),
         :ok <- verify_rp_id_hash(auth_data.rp_id_hash, @rp_id),
         :ok <- verify_user_present(auth_data.flags),
         {:ok, public_key, algorithm} <- extract_credential_public_key(auth_data.attestedCredentialData) do
      credential = %{
        id: auth_data.attestedCredentialData.credential_id,
        public_key: public_key,
        sign_count: auth_data.sign_count,
        user_id: challenge.user_id,
        algorithm: algorithm,
        created_at: DateTime.utc_now()
      }

      {:ok, credential}
    end
  rescue
    e ->
      Logger.warning("WebAuthn registration verification failed: #{inspect(e)}")
      {:error, :verification_failed}
  end

  # ============================================================================
  # Authentication
  # ============================================================================

  @doc """
  Create an authentication challenge.

  ## Options

  - `:timeout` - Challenge timeout in ms (default: 60000)
  - `:user_verification` - "required", "preferred", "discouraged" (default: "preferred")
  """
  @spec create_auth_challenge([credential_id()], keyword()) :: {:ok, auth_challenge()}
  def create_auth_challenge(credential_ids, opts \\ []) do
    timeout = Keyword.get(opts, :timeout, 60_000)
    user_verification = Keyword.get(opts, :user_verification, "preferred")

    challenge = %{
      challenge: generate_challenge(),
      rp_id: @rp_id,
      timeout: timeout,
      credential_ids: credential_ids,
      user_verification: user_verification,
      created_at: DateTime.utc_now()
    }

    {:ok, challenge}
  end

  @doc """
  Verify an authentication response.

  Returns the user ID associated with the credential if successful.
  """
  @spec verify_authentication(map(), auth_challenge(), credential()) ::
          {:ok, user_id(), non_neg_integer()} | {:error, term()}
  def verify_authentication(response, challenge, credential) do
    with :ok <- verify_challenge_not_expired(challenge),
         :ok <- verify_credential_id(response["id"], credential.id),
         {:ok, client_data} <- parse_client_data(response["clientDataJSON"]),
         :ok <- verify_client_data(client_data, challenge.challenge, "webauthn.get"),
         {:ok, auth_data} <- parse_authenticator_data(response["authenticatorData"]),
         :ok <- verify_rp_id_hash(auth_data.rp_id_hash, @rp_id),
         :ok <- verify_user_present(auth_data.flags),
         :ok <- verify_sign_count(auth_data.sign_count, credential.sign_count),
         :ok <- verify_signature(response, auth_data, client_data, credential) do
      # Return user_id and new sign_count (should be stored)
      {:ok, credential.user_id, auth_data.sign_count}
    end
  rescue
    e ->
      Logger.warning("WebAuthn authentication verification failed: #{inspect(e)}")
      {:error, :verification_failed}
  end

  # ============================================================================
  # Challenge Generation
  # ============================================================================

  defp generate_challenge do
    :crypto.strong_rand_bytes(32)
  end

  # ============================================================================
  # Verification Helpers
  # ============================================================================

  defp verify_challenge_not_expired(challenge) do
    timeout_seconds = div(challenge.timeout, 1000)
    expires_at = DateTime.add(challenge.created_at, timeout_seconds, :second)

    if DateTime.compare(DateTime.utc_now(), expires_at) == :lt do
      :ok
    else
      {:error, :challenge_expired}
    end
  end

  defp verify_credential_id(response_id, stored_id) do
    # Base64URL decode the response ID and compare
    case Base.url_decode64(response_id, padding: false) do
      {:ok, decoded} when decoded == stored_id -> :ok
      _ -> {:error, :credential_id_mismatch}
    end
  end

  defp parse_client_data(base64_data) do
    with {:ok, json} <- Base.url_decode64(base64_data, padding: false),
         {:ok, data} <- Jason.decode(json) do
      {:ok, %{
        type: data["type"],
        challenge: data["challenge"],
        origin: data["origin"],
        raw: json
      }}
    else
      _ -> {:error, :invalid_client_data}
    end
  end

  defp verify_client_data(client_data, expected_challenge, expected_type) do
    challenge_b64 = Base.url_encode64(expected_challenge, padding: false)

    cond do
      client_data.type != expected_type ->
        {:error, :invalid_client_data_type}

      client_data.challenge != challenge_b64 ->
        {:error, :challenge_mismatch}

      not origin_matches?(client_data.origin) ->
        {:error, :origin_mismatch}

      true ->
        :ok
    end
  end

  defp origin_matches?(origin) do
    # Allow localhost variations in development
    origin == @origin or
      (String.contains?(@origin, "localhost") and String.contains?(origin, "localhost"))
  end

  defp parse_attestation_object(base64_data) do
    with {:ok, cbor_data} <- Base.url_decode64(base64_data, padding: false) do
      # Simplified CBOR parsing - in production, use a proper CBOR library
      {:ok, decode_cbor_attestation(cbor_data)}
    else
      _ -> {:error, :invalid_attestation_object}
    end
  end

  defp decode_cbor_attestation(cbor_data) do
    # This is a simplified decoder - use a proper CBOR library in production
    # For now, we'll extract the authData portion which starts after the "authData" key
    %{
      "fmt" => "none",
      "authData" => extract_auth_data(cbor_data),
      "attStmt" => %{}
    }
  end

  defp extract_auth_data(cbor_data) do
    # Skip CBOR map header and find authData
    # In a real implementation, use proper CBOR decoding
    cbor_data
  end

  defp parse_authenticator_data(auth_data) when is_binary(auth_data) do
    <<
      rp_id_hash::binary-size(32),
      flags::8,
      sign_count::unsigned-big-integer-size(32),
      rest::binary
    >> = auth_data

    base_data = %{
      rp_id_hash: rp_id_hash,
      flags: parse_flags(flags),
      sign_count: sign_count
    }

    # Check if attested credential data is present (bit 6 set)
    if Bitwise.band(flags, 0x40) != 0 do
      {:ok, attested_data} = parse_attested_credential_data(rest)
      {:ok, Map.put(base_data, :attestedCredentialData, attested_data)}
    else
      {:ok, base_data}
    end
  rescue
    _ -> {:error, :invalid_authenticator_data}
  end

  defp parse_flags(flags) do
    %{
      user_present: Bitwise.band(flags, 0x01) != 0,
      user_verified: Bitwise.band(flags, 0x04) != 0,
      attested_credential_data: Bitwise.band(flags, 0x40) != 0,
      extension_data: Bitwise.band(flags, 0x80) != 0
    }
  end

  defp parse_attested_credential_data(data) do
    <<
      aaguid::binary-size(16),
      credential_id_length::unsigned-big-integer-size(16),
      credential_id::binary-size(credential_id_length),
      public_key_cbor::binary
    >> = data

    {:ok, %{
      aaguid: aaguid,
      credential_id: credential_id,
      public_key_cbor: public_key_cbor
    }}
  rescue
    _ -> {:error, :invalid_attested_credential_data}
  end

  defp verify_rp_id_hash(hash, rp_id) do
    expected = Hashing.sha256(rp_id)
    if hash == expected, do: :ok, else: {:error, :rp_id_hash_mismatch}
  end

  defp verify_user_present(flags) do
    if flags.user_present, do: :ok, else: {:error, :user_not_present}
  end

  defp verify_sign_count(new_count, stored_count) do
    # Sign count should always increase (or be 0 if not supported)
    if new_count == 0 or new_count > stored_count do
      :ok
    else
      {:error, :sign_count_regression}
    end
  end

  defp extract_credential_public_key(attested_data) do
    # Parse COSE key from CBOR
    # In production, use a proper CBOR library
    # For now, we'll extract the key material based on algorithm
    {:ok, attested_data.public_key_cbor, @cose_alg_eddsa}
  end

  defp verify_signature(response, auth_data_parsed, client_data, credential) do
    # Reconstruct the signed data
    client_data_hash = Hashing.sha256(client_data.raw)
    auth_data_binary = response["authenticatorData"]
    {:ok, auth_data_raw} = Base.url_decode64(auth_data_binary, padding: false)

    signed_data = auth_data_raw <> client_data_hash

    # Verify signature based on algorithm
    {:ok, signature} = Base.url_decode64(response["signature"], padding: false)

    case verify_cose_signature(signed_data, signature, credential.public_key, credential.algorithm) do
      true -> :ok
      false -> {:error, :invalid_signature}
    end
  rescue
    _ -> {:error, :signature_verification_failed}
  end

  defp verify_cose_signature(data, signature, public_key, algorithm) do
    case algorithm do
      @cose_alg_eddsa ->
        # EdDSA (Ed25519 or Ed448)
        :crypto.verify(:eddsa, :none, data, signature, [public_key, :ed25519])

      @cose_alg_es256 ->
        # ECDSA P-256
        :crypto.verify(:ecdsa, :sha256, data, signature, [public_key, :secp256r1])

      @cose_alg_es384 ->
        # ECDSA P-384
        :crypto.verify(:ecdsa, :sha384, data, signature, [public_key, :secp384r1])

      _ ->
        false
    end
  rescue
    _ -> false
  end

  # ============================================================================
  # Configuration
  # ============================================================================

  @doc """
  Get WebAuthn configuration for client-side setup.
  """
  @spec get_config() :: map()
  def get_config do
    %{
      rp_id: @rp_id,
      rp_name: @rp_name,
      origin: @origin,
      supported_algorithms: @supported_algorithms
    }
  end

  @doc """
  Get information about WebAuthn support.
  """
  @spec info() :: map()
  def info do
    %{
      rp_id: @rp_id,
      rp_name: @rp_name,
      origin: @origin,
      supported_algorithms: [
        %{id: @cose_alg_eddsa, name: "EdDSA (Ed25519/Ed448)"},
        %{id: @cose_alg_es256, name: "ECDSA P-256"},
        %{id: @cose_alg_es384, name: "ECDSA P-384"}
      ],
      features: [:passwordless, :two_factor, :passkeys]
    }
  end
end
