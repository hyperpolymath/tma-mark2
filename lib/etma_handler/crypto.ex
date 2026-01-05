# SPDX-License-Identifier: AGPL-3.0-or-later
defmodule EtmaHandler.Crypto do
  @moduledoc """
  High-level cryptographic operations for tma-mark2.

  Provides a safe, ergonomic API over the native Rust NIFs:

  ## Hashing
  - `hash/1` - BLAKE3 hash (256-bit)
  - `hash_xof/2` - BLAKE3 extendable output
  - `mac/2` - BLAKE3 keyed MAC

  ## Encryption (AEAD)
  - `encrypt/2` - XChaCha20-Poly1305 with random nonce
  - `decrypt/2` - XChaCha20-Poly1305 decryption
  - `seal/2` - Encrypt with derived key from password

  ## Key Exchange (Post-Quantum)
  - `generate_keypair/0` - Kyber-1024 keypair
  - `encapsulate/1` - Encapsulate shared secret
  - `decapsulate/2` - Decapsulate shared secret

  ## Signatures (Post-Quantum)
  - `sign/2` - Dilithium5 signature
  - `verify/3` - Dilithium5 verification

  ## Key Derivation
  - `derive_key/3` - BLAKE3-KDF key derivation
  - `password_hash/1` - Argon2id password hashing
  """

  alias EtmaHandler.Crypto.Native

  # ============================================================
  # HASHING
  # ============================================================

  @doc """
  Hash data using BLAKE3.

  Returns a 32-byte (256-bit) hash.

  ## Example

      iex> {:ok, hash} = EtmaHandler.Crypto.hash("hello world")
      iex> byte_size(hash)
      32
  """
  @spec hash(binary()) :: {:ok, binary()} | {:error, atom()}
  def hash(data) when is_binary(data), do: Native.blake3_hash(data)

  @doc """
  Hash data using BLAKE3 with extendable output.

  ## Example

      iex> {:ok, hash} = EtmaHandler.Crypto.hash_xof("hello", 64)
      iex> byte_size(hash)
      64
  """
  @spec hash_xof(binary(), pos_integer()) :: {:ok, binary()} | {:error, atom()}
  def hash_xof(data, output_len) when is_binary(data) and output_len > 0,
    do: Native.blake3_hash_xof(data, output_len)

  @doc """
  Compute a keyed BLAKE3 MAC.

  Requires a 32-byte key.

  ## Example

      iex> {:ok, key} = EtmaHandler.Crypto.random_bytes(32)
      iex> {:ok, mac} = EtmaHandler.Crypto.mac(key, "message")
  """
  @spec mac(binary(), binary()) :: {:ok, binary()} | {:error, atom()}
  def mac(key, data) when byte_size(key) == 32 and is_binary(data),
    do: Native.blake3_keyed_hash(key, data)

  # ============================================================
  # ENCRYPTION (AEAD)
  # ============================================================

  @doc """
  Encrypt data using XChaCha20-Poly1305.

  Generates a random 24-byte nonce and prepends it to the ciphertext.
  The result format is: `nonce (24 bytes) || ciphertext || tag (16 bytes)`

  ## Example

      iex> {:ok, key} = EtmaHandler.Crypto.random_bytes(32)
      iex> {:ok, encrypted} = EtmaHandler.Crypto.encrypt(key, "secret message")
      iex> {:ok, decrypted} = EtmaHandler.Crypto.decrypt(key, encrypted)
      iex> decrypted
      "secret message"
  """
  @spec encrypt(binary(), binary(), binary()) :: {:ok, binary()} | {:error, atom()}
  def encrypt(key, plaintext, aad \\ <<>>) when byte_size(key) == 32 do
    with {:ok, nonce} <- Native.generate_xnonce(),
         {:ok, ciphertext} <- Native.xchacha20_poly1305_encrypt(key, nonce, plaintext, aad) do
      {:ok, nonce <> ciphertext}
    end
  end

  @doc """
  Decrypt data using XChaCha20-Poly1305.

  Expects input format: `nonce (24 bytes) || ciphertext || tag (16 bytes)`
  """
  @spec decrypt(binary(), binary(), binary()) :: {:ok, binary()} | {:error, atom()}
  def decrypt(key, encrypted, aad \\ <<>>) when byte_size(key) == 32 do
    case encrypted do
      <<nonce::binary-24, ciphertext::binary>> ->
        Native.xchacha20_poly1305_decrypt(key, nonce, ciphertext, aad)

      _ ->
        {:error, :invalid_ciphertext}
    end
  end

  @doc """
  Encrypt data with a password-derived key.

  Uses Argon2id for key derivation and XChaCha20-Poly1305 for encryption.
  """
  @spec seal(String.t(), binary()) :: {:ok, binary()} | {:error, atom()}
  def seal(password, plaintext) when is_binary(password) and is_binary(plaintext) do
    salt = :crypto.strong_rand_bytes(16)
    # Use Argon2.Base.hash_password/3 for raw key derivation
    key = Argon2.Base.hash_password(password, salt, format: :raw_hash, hashlen: 32)

    with {:ok, encrypted} <- encrypt(key, plaintext) do
      {:ok, salt <> encrypted}
    end
  end

  @doc """
  Decrypt data with a password-derived key.
  """
  @spec unseal(String.t(), binary()) :: {:ok, binary()} | {:error, atom()}
  def unseal(password, sealed) when is_binary(password) do
    case sealed do
      <<salt::binary-16, encrypted::binary>> ->
        # Use Argon2.Base.hash_password/3 for raw key derivation
        key = Argon2.Base.hash_password(password, salt, format: :raw_hash, hashlen: 32)
        decrypt(key, encrypted)

      _ ->
        {:error, :invalid_sealed_data}
    end
  end

  # ============================================================
  # KEY EXCHANGE (POST-QUANTUM)
  # ============================================================

  @doc """
  Generate a Kyber-1024 keypair for post-quantum key exchange.

  ## Example

      iex> {:ok, {public_key, secret_key}} = EtmaHandler.Crypto.generate_keypair()
  """
  @spec generate_keypair() :: {:ok, {binary(), binary()}} | {:error, atom()}
  def generate_keypair, do: Native.kyber1024_keypair()

  @doc """
  Encapsulate a shared secret using recipient's Kyber public key.

  Returns `{ciphertext, shared_secret}`.
  Send the ciphertext to the recipient who can decapsulate with their secret key.
  """
  @spec encapsulate(binary()) :: {:ok, {binary(), binary()}} | {:error, atom()}
  def encapsulate(public_key), do: Native.kyber1024_encapsulate(public_key)

  @doc """
  Decapsulate a shared secret using your Kyber secret key.

  Returns the same shared secret that was generated during encapsulation.
  """
  @spec decapsulate(binary(), binary()) :: {:ok, binary()} | {:error, atom()}
  def decapsulate(ciphertext, secret_key), do: Native.kyber1024_decapsulate(ciphertext, secret_key)

  # ============================================================
  # SIGNATURES (POST-QUANTUM)
  # ============================================================

  @doc """
  Generate a Dilithium5 keypair for post-quantum signatures.

  ## Example

      iex> {:ok, {public_key, secret_key}} = EtmaHandler.Crypto.generate_signing_keypair()
  """
  @spec generate_signing_keypair() :: {:ok, {binary(), binary()}} | {:error, atom()}
  def generate_signing_keypair, do: Native.dilithium5_keypair()

  @doc """
  Sign a message using Dilithium5 secret key.

  ## Example

      iex> {:ok, {_pk, sk}} = EtmaHandler.Crypto.generate_signing_keypair()
      iex> {:ok, signature} = EtmaHandler.Crypto.sign("message", sk)
  """
  @spec sign(binary(), binary()) :: {:ok, binary()} | {:error, atom()}
  def sign(message, secret_key), do: Native.dilithium5_sign(message, secret_key)

  @doc """
  Verify a Dilithium5 signature.

  Returns `{:ok, true}` if valid, `{:error, :verification_failed}` otherwise.
  """
  @spec verify(binary(), binary(), binary()) :: {:ok, true} | {:error, atom()}
  def verify(message, signature, public_key),
    do: Native.dilithium5_verify(message, signature, public_key)

  # ============================================================
  # KEY DERIVATION
  # ============================================================

  @doc """
  Derive a key using BLAKE3-KDF.

  ## Parameters
  - `context` - Domain separation string (e.g., "tma-mark2 session key")
  - `ikm` - Input key material
  - `length` - Output key length in bytes

  ## Example

      iex> {:ok, key} = EtmaHandler.Crypto.derive_key("tma-mark2 session", master_secret, 32)
  """
  @spec derive_key(String.t(), binary(), pos_integer()) :: {:ok, binary()} | {:error, atom()}
  def derive_key(context, ikm, length) when is_binary(context) and is_binary(ikm) and length > 0,
    do: Native.blake3_derive_key(context, ikm, length)

  @doc """
  Hash a password using Argon2id.

  Returns a PHC-formatted string suitable for storage.
  """
  @spec password_hash(String.t()) :: String.t()
  def password_hash(password) when is_binary(password) do
    Argon2.hash_pwd_salt(password)
  end

  @doc """
  Verify a password against an Argon2id hash.
  """
  @spec password_verify(String.t(), String.t()) :: boolean()
  def password_verify(password, hash) when is_binary(password) and is_binary(hash) do
    Argon2.verify_pass(password, hash)
  end

  # ============================================================
  # RANDOM
  # ============================================================

  @doc """
  Generate cryptographically secure random bytes.

  ## Example

      iex> {:ok, bytes} = EtmaHandler.Crypto.random_bytes(32)
      iex> byte_size(bytes)
      32
  """
  @spec random_bytes(pos_integer()) :: {:ok, binary()} | {:error, atom()}
  def random_bytes(length) when length > 0, do: Native.random_bytes(length)

  @doc """
  Generate a random UUID v4.
  """
  @spec uuid() :: String.t()
  def uuid do
    <<a::48, _::4, b::12, _::2, c::62>> = :crypto.strong_rand_bytes(16)

    <<a::48, 4::4, b::12, 2::2, c::62>>
    |> Base.encode16(case: :lower)
    |> String.replace(~r/(.{8})(.{4})(.{4})(.{4})(.{12})/, "\\1-\\2-\\3-\\4-\\5")
  end

  # ============================================================
  # COMPARISON
  # ============================================================

  @doc """
  Constant-time byte comparison (timing attack safe).
  """
  @spec secure_compare(binary(), binary()) :: boolean()
  def secure_compare(a, b) when is_binary(a) and is_binary(b), do: Native.secure_compare(a, b)
end
