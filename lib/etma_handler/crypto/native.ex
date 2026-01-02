# SPDX-License-Identifier: AGPL-3.0-or-later
defmodule EtmaHandler.Crypto.Native do
  @moduledoc """
  Native Rust NIF bindings for cryptographic operations.

  This module provides post-quantum cryptography via Rustler NIFs:
  - BLAKE3: Fast cryptographic hashing and KDF
  - SHAKE-256: Extensible output function
  - ChaCha20-Poly1305: AEAD symmetric encryption
  - XChaCha20-Poly1305: Extended nonce AEAD
  - Kyber-1024 (ML-KEM): Post-quantum key encapsulation
  - Dilithium5 (ML-DSA): Post-quantum digital signatures

  All functions return `{:ok, result}` or `{:error, reason}`.
  """

  use Rustler,
    otp_app: :etma_handler,
    crate: :tma_crypto

  # BLAKE3 Hashing
  @doc "Hash data using BLAKE3 (256-bit output)"
  def blake3_hash(_data), do: :erlang.nif_error(:nif_not_loaded)

  @doc "Hash data using BLAKE3 with custom output length (XOF mode)"
  def blake3_hash_xof(_data, _output_len), do: :erlang.nif_error(:nif_not_loaded)

  @doc "Derive a key using BLAKE3-KDF"
  def blake3_derive_key(_context, _ikm, _output_len), do: :erlang.nif_error(:nif_not_loaded)

  @doc "Keyed BLAKE3 MAC (32-byte key required)"
  def blake3_keyed_hash(_key, _data), do: :erlang.nif_error(:nif_not_loaded)

  # SHAKE-256
  @doc "Hash data using SHAKE-256 with custom output length"
  def shake256(_data, _output_len), do: :erlang.nif_error(:nif_not_loaded)

  # ChaCha20-Poly1305
  @doc "Encrypt with ChaCha20-Poly1305 (32-byte key, 12-byte nonce)"
  def chacha20_poly1305_encrypt(_key, _nonce, _plaintext, _aad),
    do: :erlang.nif_error(:nif_not_loaded)

  @doc "Decrypt with ChaCha20-Poly1305"
  def chacha20_poly1305_decrypt(_key, _nonce, _ciphertext, _aad),
    do: :erlang.nif_error(:nif_not_loaded)

  # XChaCha20-Poly1305
  @doc "Encrypt with XChaCha20-Poly1305 (32-byte key, 24-byte nonce)"
  def xchacha20_poly1305_encrypt(_key, _nonce, _plaintext, _aad),
    do: :erlang.nif_error(:nif_not_loaded)

  @doc "Decrypt with XChaCha20-Poly1305"
  def xchacha20_poly1305_decrypt(_key, _nonce, _ciphertext, _aad),
    do: :erlang.nif_error(:nif_not_loaded)

  # Kyber-1024 (ML-KEM)
  @doc "Generate Kyber-1024 keypair: {:ok, {public_key, secret_key}}"
  def kyber1024_keypair(), do: :erlang.nif_error(:nif_not_loaded)

  @doc "Encapsulate shared secret: {:ok, {ciphertext, shared_secret}}"
  def kyber1024_encapsulate(_public_key), do: :erlang.nif_error(:nif_not_loaded)

  @doc "Decapsulate shared secret: {:ok, shared_secret}"
  def kyber1024_decapsulate(_ciphertext, _secret_key), do: :erlang.nif_error(:nif_not_loaded)

  # Dilithium5 (ML-DSA)
  @doc "Generate Dilithium5 keypair: {:ok, {public_key, secret_key}}"
  def dilithium5_keypair(), do: :erlang.nif_error(:nif_not_loaded)

  @doc "Sign message: {:ok, signature}"
  def dilithium5_sign(_message, _secret_key), do: :erlang.nif_error(:nif_not_loaded)

  @doc "Verify signature: {:ok, true} or {:error, :verification_failed}"
  def dilithium5_verify(_message, _signature, _public_key),
    do: :erlang.nif_error(:nif_not_loaded)

  # Random
  @doc "Generate cryptographically secure random bytes"
  def random_bytes(_len), do: :erlang.nif_error(:nif_not_loaded)

  @doc "Generate 12-byte nonce for ChaCha20-Poly1305"
  def generate_nonce(), do: :erlang.nif_error(:nif_not_loaded)

  @doc "Generate 24-byte nonce for XChaCha20-Poly1305"
  def generate_xnonce(), do: :erlang.nif_error(:nif_not_loaded)

  # Utils
  @doc "Constant-time byte comparison (timing attack safe)"
  def secure_compare(_a, _b), do: :erlang.nif_error(:nif_not_loaded)
end
