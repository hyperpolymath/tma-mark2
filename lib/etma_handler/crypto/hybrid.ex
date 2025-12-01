# SPDX-FileCopyrightText: 2024 eTMA Handler Contributors
# SPDX-License-Identifier: MIT

defmodule EtmaHandler.Crypto.Hybrid do
  @moduledoc """
  Hybrid Key Encapsulation Mechanism combining X25519 and ML-KEM-1024.

  ## Security Model

  This implements a "belt and suspenders" approach:

  1. **X25519** - Battle-tested elliptic curve DH (classical)
  2. **ML-KEM-1024** - NIST FIPS 203 post-quantum KEM

  Both shared secrets are combined via HKDF. An attacker must break
  BOTH algorithms to recover the plaintext.

  ## Wire Format

  ### Public Key (1600 bytes when PQC available, 32 bytes fallback)
  ```
  | Version (1) | X25519 PK (32) | ML-KEM-1024 PK (1568) |
  ```

  ### Ciphertext
  ```
  | Version (1) | X25519 PK (32) | ML-KEM CT (1568) | AEAD Ciphertext (variable) |
  ```

  Version byte:
  - 0x01 = Hybrid (X25519 + ML-KEM-1024)
  - 0x00 = Classical only (X25519)
  """

  alias EtmaHandler.Crypto.Backend

  require Logger

  # Version bytes
  @version_hybrid 0x01
  @version_classical 0x00

  # Key/ciphertext component sizes
  @x25519_pk_size 32
  @x25519_sk_size 32
  @ml_kem_pk_size 1568
  @ml_kem_sk_size 3168
  @ml_kem_ct_size 1568

  @doc """
  Generate a hybrid keypair.

  Returns `{public_key, secret_key}` where:
  - If PQC available: keys contain both X25519 and ML-KEM-1024 components
  - If fallback: keys contain only X25519

  The version byte prefix indicates which mode was used.
  """
  @spec generate_keypair() :: {binary(), binary()}
  def generate_keypair do
    # Generate X25519 keypair (always available)
    {x25519_pk, x25519_sk} = Backend.x25519_keypair()

    # Try to generate ML-KEM keypair
    case Backend.ml_kem_keypair() do
      {ml_kem_pk, ml_kem_sk} ->
        # Hybrid mode
        public_key = <<@version_hybrid, x25519_pk::binary, ml_kem_pk::binary>>
        secret_key = <<@version_hybrid, x25519_sk::binary, ml_kem_sk::binary>>
        {public_key, secret_key}

      :not_available ->
        # Classical-only fallback
        Logger.warning(
          "ML-KEM-1024 not available, using classical-only encryption. " <>
            "Upgrade to OTP 27+ with OpenSSL 3.5 for post-quantum security."
        )

        public_key = <<@version_classical, x25519_pk::binary>>
        secret_key = <<@version_classical, x25519_sk::binary>>
        {public_key, secret_key}
    end
  end

  @doc """
  Encrypt plaintext using hybrid encryption.

  1. Parse recipient's public key to determine mode
  2. Generate ephemeral X25519 keypair
  3. If hybrid: encapsulate via ML-KEM-1024
  4. Derive symmetric key from shared secret(s)
  5. Encrypt with XChaCha20-Poly1305
  """
  @spec encrypt(binary(), binary()) :: {:ok, binary()} | {:error, term()}
  def encrypt(plaintext, public_key) when is_binary(plaintext) and is_binary(public_key) do
    case parse_public_key(public_key) do
      {:hybrid, x25519_pk, ml_kem_pk} ->
        encrypt_hybrid(plaintext, x25519_pk, ml_kem_pk)

      {:classical, x25519_pk} ->
        encrypt_classical(plaintext, x25519_pk)

      {:error, reason} ->
        {:error, reason}
    end
  end

  def encrypt(_, _), do: {:error, :invalid_input}

  @doc """
  Decrypt ciphertext using hybrid decryption.
  """
  @spec decrypt(binary(), binary()) :: {:ok, binary()} | {:error, term()}
  def decrypt(ciphertext, secret_key) when is_binary(ciphertext) and is_binary(secret_key) do
    case {parse_ciphertext(ciphertext), parse_secret_key(secret_key)} do
      {{:hybrid, ephemeral_pk, ml_kem_ct, aead_ct}, {:hybrid, x25519_sk, ml_kem_sk}} ->
        decrypt_hybrid(ephemeral_pk, ml_kem_ct, aead_ct, x25519_sk, ml_kem_sk)

      {{:classical, ephemeral_pk, aead_ct}, {:classical, x25519_sk}} ->
        decrypt_classical(ephemeral_pk, aead_ct, x25519_sk)

      {{:hybrid, _, _, _}, {:classical, _}} ->
        {:error, :key_mode_mismatch}

      {{:classical, _, _}, {:hybrid, _, _}} ->
        # Can decrypt classical ciphertext with hybrid key (use X25519 portion)
        case parse_ciphertext(ciphertext) do
          {:classical, ephemeral_pk, aead_ct} ->
            {:hybrid, x25519_sk, _ml_kem_sk} = parse_secret_key(secret_key)
            decrypt_classical(ephemeral_pk, aead_ct, x25519_sk)

          _ ->
            {:error, :parse_failed}
        end

      {err1, err2} when elem(err1, 0) == :error or elem(err2, 0) == :error ->
        {:error, :parse_failed}
    end
  end

  def decrypt(_, _), do: {:error, :invalid_input}

  # ============================================================================
  # Private: Hybrid Encryption
  # ============================================================================

  defp encrypt_hybrid(plaintext, x25519_pk, ml_kem_pk) do
    # Generate ephemeral X25519 keypair
    {ephemeral_pk, ephemeral_sk} = Backend.x25519_keypair()

    # X25519 key agreement
    x25519_shared = Backend.x25519_shared_secret(x25519_pk, ephemeral_sk)

    # ML-KEM encapsulation
    case Backend.ml_kem_encapsulate(ml_kem_pk) do
      {ml_kem_shared, ml_kem_ct} ->
        # Combine shared secrets via HKDF
        symmetric_key = Backend.derive_key([x25519_shared, ml_kem_shared], "etma-hybrid-v1")

        # Encrypt with XChaCha20-Poly1305
        aead_ct = Backend.aead_encrypt(plaintext, symmetric_key, <<@version_hybrid>>)

        # Assemble ciphertext
        ciphertext = <<@version_hybrid, ephemeral_pk::binary, ml_kem_ct::binary, aead_ct::binary>>

        {:ok, ciphertext}

      :not_available ->
        # Fallback to classical if ML-KEM fails at runtime
        encrypt_classical(plaintext, x25519_pk)
    end
  end

  defp encrypt_classical(plaintext, x25519_pk) do
    # Generate ephemeral X25519 keypair
    {ephemeral_pk, ephemeral_sk} = Backend.x25519_keypair()

    # X25519 key agreement
    x25519_shared = Backend.x25519_shared_secret(x25519_pk, ephemeral_sk)

    # Derive symmetric key
    symmetric_key = Backend.derive_key([x25519_shared], "etma-classical-v1")

    # Encrypt with XChaCha20-Poly1305
    aead_ct = Backend.aead_encrypt(plaintext, symmetric_key, <<@version_classical>>)

    # Assemble ciphertext
    ciphertext = <<@version_classical, ephemeral_pk::binary, aead_ct::binary>>

    {:ok, ciphertext}
  end

  # ============================================================================
  # Private: Hybrid Decryption
  # ============================================================================

  defp decrypt_hybrid(ephemeral_pk, ml_kem_ct, aead_ct, x25519_sk, ml_kem_sk) do
    # X25519 key agreement
    x25519_shared = Backend.x25519_shared_secret(ephemeral_pk, x25519_sk)

    # ML-KEM decapsulation
    case Backend.ml_kem_decapsulate(ml_kem_ct, ml_kem_sk) do
      ml_kem_shared when is_binary(ml_kem_shared) ->
        # Combine shared secrets via HKDF
        symmetric_key = Backend.derive_key([x25519_shared, ml_kem_shared], "etma-hybrid-v1")

        # Decrypt with XChaCha20-Poly1305
        Backend.aead_decrypt(aead_ct, symmetric_key, <<@version_hybrid>>)

      :not_available ->
        {:error, :ml_kem_decapsulate_failed}
    end
  end

  defp decrypt_classical(ephemeral_pk, aead_ct, x25519_sk) do
    # X25519 key agreement
    x25519_shared = Backend.x25519_shared_secret(ephemeral_pk, x25519_sk)

    # Derive symmetric key
    symmetric_key = Backend.derive_key([x25519_shared], "etma-classical-v1")

    # Decrypt with XChaCha20-Poly1305
    Backend.aead_decrypt(aead_ct, symmetric_key, <<@version_classical>>)
  end

  # ============================================================================
  # Private: Parsing
  # ============================================================================

  defp parse_public_key(<<@version_hybrid, x25519_pk::binary-size(@x25519_pk_size),
                          ml_kem_pk::binary-size(@ml_kem_pk_size)>>) do
    {:hybrid, x25519_pk, ml_kem_pk}
  end

  defp parse_public_key(<<@version_classical, x25519_pk::binary-size(@x25519_pk_size)>>) do
    {:classical, x25519_pk}
  end

  defp parse_public_key(_), do: {:error, :invalid_public_key}

  defp parse_secret_key(<<@version_hybrid, x25519_sk::binary-size(@x25519_sk_size),
                          ml_kem_sk::binary-size(@ml_kem_sk_size)>>) do
    {:hybrid, x25519_sk, ml_kem_sk}
  end

  defp parse_secret_key(<<@version_classical, x25519_sk::binary-size(@x25519_sk_size)>>) do
    {:classical, x25519_sk}
  end

  defp parse_secret_key(_), do: {:error, :invalid_secret_key}

  defp parse_ciphertext(<<@version_hybrid,
                          ephemeral_pk::binary-size(@x25519_pk_size),
                          ml_kem_ct::binary-size(@ml_kem_ct_size),
                          aead_ct::binary>>) do
    {:hybrid, ephemeral_pk, ml_kem_ct, aead_ct}
  end

  defp parse_ciphertext(<<@version_classical,
                          ephemeral_pk::binary-size(@x25519_pk_size),
                          aead_ct::binary>>) do
    {:classical, ephemeral_pk, aead_ct}
  end

  defp parse_ciphertext(_), do: {:error, :invalid_ciphertext}
end
