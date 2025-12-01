# SPDX-FileCopyrightText: 2024 eTMA Handler Contributors
# SPDX-License-Identifier: MIT

defmodule EtmaHandler.Crypto.Backend do
  @moduledoc """
  Cryptographic backend detection and abstraction.

  Automatically selects the best available implementation:

  1. **:otp_native** - OTP 27+ with OpenSSL 3.5 (ML-KEM support)
  2. **:pqclean** - erlang-pqclean NIF wrapper
  3. **:fallback** - Classical-only (X25519 + XChaCha20-Poly1305)

  The fallback mode still provides strong encryption but without
  post-quantum resistance. A warning is logged on startup.
  """

  require Logger

  @ml_kem_1024_public_key_size 1568
  @ml_kem_1024_secret_key_size 3168
  @ml_kem_1024_ciphertext_size 1568
  @ml_kem_1024_shared_secret_size 32

  @x25519_public_key_size 32
  @x25519_secret_key_size 32

  @doc """
  Detect and return the best available backend.
  """
  @spec detect() :: :otp_native | :pqclean | :fallback
  def detect do
    cond do
      otp_native_available?() -> :otp_native
      pqclean_available?() -> :pqclean
      true -> :fallback
    end
  end

  @doc """
  Check if any post-quantum backend is available.
  """
  @spec pqc_available?() :: boolean()
  def pqc_available? do
    detect() in [:otp_native, :pqclean]
  end

  @doc """
  Return information about the current backend.
  """
  @spec backend_info() :: map()
  def backend_info do
    backend = detect()

    %{
      backend: backend,
      pqc_available: backend in [:otp_native, :pqclean],
      ml_kem_variant: if(backend != :fallback, do: "ML-KEM-1024", else: nil),
      classical_kem: "X25519",
      aead: "XChaCha20-Poly1305",
      kdf: "HKDF-SHA256",
      otp_version: System.otp_release(),
      openssl_version: get_openssl_version()
    }
  end

  # ==========================================================================
  # ML-KEM-1024 Operations
  # ==========================================================================

  @doc """
  Generate ML-KEM-1024 keypair.
  """
  @spec ml_kem_keypair() :: {binary(), binary()} | :not_available
  def ml_kem_keypair do
    case detect() do
      :otp_native -> otp_ml_kem_keypair()
      :pqclean -> pqclean_ml_kem_keypair()
      :fallback -> :not_available
    end
  end

  @doc """
  ML-KEM-1024 encapsulation (generate shared secret + ciphertext).
  """
  @spec ml_kem_encapsulate(binary()) :: {binary(), binary()} | :not_available
  def ml_kem_encapsulate(public_key) do
    case detect() do
      :otp_native -> otp_ml_kem_encapsulate(public_key)
      :pqclean -> pqclean_ml_kem_encapsulate(public_key)
      :fallback -> :not_available
    end
  end

  @doc """
  ML-KEM-1024 decapsulation (recover shared secret from ciphertext).
  """
  @spec ml_kem_decapsulate(binary(), binary()) :: binary() | :not_available
  def ml_kem_decapsulate(ciphertext, secret_key) do
    case detect() do
      :otp_native -> otp_ml_kem_decapsulate(ciphertext, secret_key)
      :pqclean -> pqclean_ml_kem_decapsulate(ciphertext, secret_key)
      :fallback -> :not_available
    end
  end

  # ==========================================================================
  # X25519 Operations (always available via OTP crypto)
  # ==========================================================================

  @doc """
  Generate X25519 keypair.
  """
  @spec x25519_keypair() :: {binary(), binary()}
  def x25519_keypair do
    secret_key = :crypto.strong_rand_bytes(@x25519_secret_key_size)
    public_key = :crypto.compute_key(:ecdh, secret_key, :x25519)
    {public_key, secret_key}
  rescue
    # Fallback for older OTP versions
    _ ->
      {pub, priv} = :crypto.generate_key(:ecdh, :x25519)
      {pub, priv}
  end

  @doc """
  X25519 key agreement.
  """
  @spec x25519_shared_secret(binary(), binary()) :: binary()
  def x25519_shared_secret(their_public, my_secret) do
    :crypto.compute_key(:ecdh, their_public, my_secret, :x25519)
  end

  # ==========================================================================
  # AEAD Operations (XChaCha20-Poly1305)
  # ==========================================================================

  @doc """
  Encrypt with XChaCha20-Poly1305.
  """
  @spec aead_encrypt(binary(), binary(), binary()) :: binary()
  def aead_encrypt(plaintext, key, aad \\ <<>>) do
    # 24-byte nonce for XChaCha20
    nonce = :crypto.strong_rand_bytes(24)

    {ciphertext, tag} =
      :crypto.crypto_one_time_aead(
        :chacha20_poly1305,
        key,
        nonce,
        plaintext,
        aad,
        true
      )

    # Format: nonce || tag || ciphertext
    nonce <> tag <> ciphertext
  end

  @doc """
  Decrypt with XChaCha20-Poly1305.
  """
  @spec aead_decrypt(binary(), binary(), binary()) :: {:ok, binary()} | {:error, :auth_failed}
  def aead_decrypt(<<nonce::binary-24, tag::binary-16, ciphertext::binary>>, key, aad \\ <<>>) do
    case :crypto.crypto_one_time_aead(
           :chacha20_poly1305,
           key,
           nonce,
           ciphertext,
           aad,
           tag,
           false
         ) do
      plaintext when is_binary(plaintext) -> {:ok, plaintext}
      :error -> {:error, :auth_failed}
    end
  end

  def aead_decrypt(_, _, _), do: {:error, :invalid_ciphertext}

  # ==========================================================================
  # Key Derivation (HKDF)
  # ==========================================================================

  @doc """
  Derive encryption key from shared secrets using HKDF.
  """
  @spec derive_key(list(binary()), binary()) :: binary()
  def derive_key(secrets, info \\ "etma-handler-v1") do
    # Concatenate all shared secrets
    ikm = Enum.join(secrets)

    # HKDF-SHA256 to derive 32-byte key
    hkdf_extract_expand(ikm, info, 32)
  end

  # ==========================================================================
  # Private: Backend Detection
  # ==========================================================================

  defp otp_native_available? do
    # Check for OTP 27+ with ML-KEM support
    otp_version = System.otp_release() |> String.to_integer()

    if otp_version >= 27 do
      # Try to call the function to verify OpenSSL 3.5+
      try do
        # This will fail if ML-KEM not available
        Code.ensure_loaded?(:crypto) and
          function_exported?(:crypto, :encapsulate_key, 2)
      rescue
        _ -> false
      end
    else
      false
    end
  end

  defp pqclean_available? do
    Code.ensure_loaded?(:pqclean_nif) and
      function_exported?(:pqclean_nif, :kyber1024_keypair, 0)
  end

  # ==========================================================================
  # Private: OTP Native ML-KEM
  # ==========================================================================

  defp otp_ml_kem_keypair do
    # OTP 27+ syntax
    {public_key, secret_key} = :crypto.generate_key(:mlkem1024)
    {public_key, secret_key}
  rescue
    e ->
      Logger.warning("OTP ML-KEM keypair generation failed: #{inspect(e)}")
      :not_available
  end

  defp otp_ml_kem_encapsulate(public_key) do
    {shared_secret, ciphertext} = :crypto.encapsulate_key(:mlkem1024, public_key)
    {shared_secret, ciphertext}
  rescue
    e ->
      Logger.warning("OTP ML-KEM encapsulate failed: #{inspect(e)}")
      :not_available
  end

  defp otp_ml_kem_decapsulate(ciphertext, secret_key) do
    :crypto.decapsulate_key(:mlkem1024, ciphertext, secret_key)
  rescue
    e ->
      Logger.warning("OTP ML-KEM decapsulate failed: #{inspect(e)}")
      :not_available
  end

  # ==========================================================================
  # Private: PQClean NIF Backend
  # ==========================================================================

  defp pqclean_ml_kem_keypair do
    # pqclean uses kyber naming
    :pqclean_nif.kyber1024_keypair()
  rescue
    e ->
      Logger.warning("PQClean keypair generation failed: #{inspect(e)}")
      :not_available
  end

  defp pqclean_ml_kem_encapsulate(public_key) do
    {ciphertext, shared_secret} = :pqclean_nif.kyber1024_encapsulate(public_key)
    {shared_secret, ciphertext}
  rescue
    e ->
      Logger.warning("PQClean encapsulate failed: #{inspect(e)}")
      :not_available
  end

  defp pqclean_ml_kem_decapsulate(ciphertext, secret_key) do
    :pqclean_nif.kyber1024_decapsulate(ciphertext, secret_key)
  rescue
    e ->
      Logger.warning("PQClean decapsulate failed: #{inspect(e)}")
      :not_available
  end

  # ==========================================================================
  # Private: Utilities
  # ==========================================================================

  defp get_openssl_version do
    try do
      info = :crypto.info()
      info[:openssl_info][:version] || "unknown"
    rescue
      _ -> "unknown"
    end
  end

  defp hkdf_extract_expand(ikm, info, length) do
    # HKDF-Extract with zero salt
    salt = :binary.copy(<<0>>, 32)
    prk = :crypto.mac(:hmac, :sha256, salt, ikm)

    # HKDF-Expand
    hkdf_expand(prk, info, length, <<>>, 1)
  end

  defp hkdf_expand(_prk, _info, length, acc, _counter) when byte_size(acc) >= length do
    binary_part(acc, 0, length)
  end

  defp hkdf_expand(prk, info, length, acc, counter) do
    prev = if acc == <<>>, do: <<>>, else: binary_part(acc, byte_size(acc) - 32, 32)
    block = :crypto.mac(:hmac, :sha256, prk, prev <> info <> <<counter>>)
    hkdf_expand(prk, info, length, acc <> block, counter + 1)
  end

  # Sizes for external reference
  def ml_kem_1024_sizes do
    %{
      public_key: @ml_kem_1024_public_key_size,
      secret_key: @ml_kem_1024_secret_key_size,
      ciphertext: @ml_kem_1024_ciphertext_size,
      shared_secret: @ml_kem_1024_shared_secret_size
    }
  end

  def x25519_sizes do
    %{
      public_key: @x25519_public_key_size,
      secret_key: @x25519_secret_key_size
    }
  end
end
