# SPDX-FileCopyrightText: 2024 eTMA Handler Contributors
# SPDX-License-Identifier: MIT

defmodule EtmaHandler.Crypto.Hashing do
  @moduledoc """
  Cryptographic hash functions with BLAKE3, SHA3-512, and SHAKE256 support.

  ## Algorithm Selection

  | Algorithm  | Output  | Speed    | Use Case |
  |------------|---------|----------|----------|
  | BLAKE3     | 256-bit | Fastest  | General hashing, key derivation |
  | SHA3-512   | 512-bit | Fast     | Maximum security margin |
  | SHAKE256   | Variable| Fast     | XOF for Ed448 signatures |
  | SHA-256    | 256-bit | Fast     | Compatibility |

  ## BLAKE3 Advantages

  - ~5x faster than SHA-256
  - Built-in key derivation (KDF) mode
  - Parallel processing support
  - Proven security (based on BLAKE2/ChaCha)

  ## Usage

      # BLAKE3 hashing
      hash = Hashing.blake3("data")

      # BLAKE3 keyed hashing (MAC)
      mac = Hashing.blake3_keyed("data", key)

      # BLAKE3 key derivation
      derived = Hashing.blake3_derive_key("context", ikm, 32)

      # SHA3-512
      hash = Hashing.sha3_512("data")

      # SHAKE256 (variable output)
      hash = Hashing.shake256("data", 64)
  """

  # BLAKE3 constants
  @blake3_key_size 32
  @blake3_out_size 32

  @doc """
  Compute BLAKE3 hash of input.

  Returns 32-byte hash by default. Uses OTP :crypto.hash if BLAKE3 native
  not available, falling back to SHA-256 for compatibility.
  """
  @spec blake3(binary(), non_neg_integer()) :: binary()
  def blake3(data, output_size \\ @blake3_out_size) when is_binary(data) do
    case blake3_native(data, output_size) do
      {:ok, hash} -> hash
      :not_available -> fallback_hash(data, output_size)
    end
  end

  @doc """
  Compute BLAKE3 keyed hash (MAC).

  Key must be exactly 32 bytes. Use for authentication codes.
  """
  @spec blake3_keyed(binary(), binary()) :: binary()
  def blake3_keyed(data, key) when is_binary(data) and byte_size(key) == @blake3_key_size do
    case blake3_keyed_native(data, key) do
      {:ok, mac} -> mac
      :not_available -> fallback_mac(data, key)
    end
  end

  @doc """
  BLAKE3 key derivation function.

  Derives a key from input keying material (IKM) with a context string.
  The context should be unique to your application and use case.

  ## Example

      # Derive an encryption key
      key = Hashing.blake3_derive_key("etma-handler v1 file encryption", password, 32)
  """
  @spec blake3_derive_key(String.t(), binary(), non_neg_integer()) :: binary()
  def blake3_derive_key(context, ikm, output_size)
      when is_binary(context) and is_binary(ikm) and is_integer(output_size) do
    case blake3_kdf_native(context, ikm, output_size) do
      {:ok, key} -> key
      :not_available -> fallback_kdf(context, ikm, output_size)
    end
  end

  @doc """
  Compute SHA3-512 hash.

  Returns 64-byte hash. Maximum security margin (256-bit security).
  """
  @spec sha3_512(binary()) :: binary()
  def sha3_512(data) when is_binary(data) do
    :crypto.hash(:sha3_512, data)
  end

  @doc """
  Compute SHA3-256 hash.

  Returns 32-byte hash.
  """
  @spec sha3_256(binary()) :: binary()
  def sha3_256(data) when is_binary(data) do
    :crypto.hash(:sha3_256, data)
  end

  @doc """
  Compute SHAKE256 (SHA-3 extendable output function).

  Variable output length, used by Ed448 signatures.
  """
  @spec shake256(binary(), non_neg_integer()) :: binary()
  def shake256(data, output_size) when is_binary(data) and is_integer(output_size) do
    :crypto.hash(:shake256, data)
    |> binary_part(0, min(output_size, 64))
  rescue
    # SHAKE256 with variable output may not be available in all OTP versions
    _ ->
      # Fallback: use SHA3-512 and truncate/extend
      hash = :crypto.hash(:sha3_512, data)
      extend_hash(hash, output_size)
  end

  @doc """
  Compute SHA-256 hash (compatibility fallback).
  """
  @spec sha256(binary()) :: binary()
  def sha256(data) when is_binary(data) do
    :crypto.hash(:sha256, data)
  end

  @doc """
  Check if BLAKE3 native support is available.
  """
  @spec blake3_available?() :: boolean()
  def blake3_available? do
    # Check for blake3 NIF or OTP support
    Code.ensure_loaded?(:blake3) or
      (Code.ensure_loaded?(:crypto) and function_exported?(:crypto, :hash, 2) and
         try do
           :crypto.hash(:blake3, "test")
           true
         rescue
           _ -> false
         end)
  end

  @doc """
  Get information about available hash algorithms.
  """
  @spec info() :: map()
  def info do
    %{
      blake3_available: blake3_available?(),
      sha3_512_available: sha3_available?(:sha3_512),
      sha3_256_available: sha3_available?(:sha3_256),
      shake256_available: sha3_available?(:shake256),
      default_hash: if(blake3_available?(), do: :blake3, else: :sha3_256),
      algorithms: [
        %{name: :blake3, output_bits: 256, security_bits: 128, available: blake3_available?()},
        %{name: :sha3_512, output_bits: 512, security_bits: 256, available: sha3_available?(:sha3_512)},
        %{name: :sha3_256, output_bits: 256, security_bits: 128, available: sha3_available?(:sha3_256)},
        %{name: :shake256, output_bits: :variable, security_bits: 256, available: sha3_available?(:shake256)}
      ]
    }
  end

  # ============================================================================
  # Private: Native BLAKE3 (via NIF or OTP)
  # ============================================================================

  defp blake3_native(data, output_size) do
    cond do
      # Check for blake3 Elixir library
      Code.ensure_loaded?(:blake3) and function_exported?(:blake3, :hash, 2) ->
        {:ok, :blake3.hash(data, output_size)}

      # Check for OTP native (future)
      try_otp_blake3(data, output_size) != :not_available ->
        try_otp_blake3(data, output_size)

      true ->
        :not_available
    end
  rescue
    _ -> :not_available
  end

  defp try_otp_blake3(data, _output_size) do
    try do
      hash = :crypto.hash(:blake3, data)
      {:ok, hash}
    rescue
      _ -> :not_available
    end
  end

  defp blake3_keyed_native(data, key) do
    if Code.ensure_loaded?(:blake3) and function_exported?(:blake3, :keyed_hash, 2) do
      {:ok, :blake3.keyed_hash(key, data)}
    else
      :not_available
    end
  rescue
    _ -> :not_available
  end

  defp blake3_kdf_native(context, ikm, output_size) do
    if Code.ensure_loaded?(:blake3) and function_exported?(:blake3, :derive_key, 3) do
      {:ok, :blake3.derive_key(context, ikm, output_size)}
    else
      :not_available
    end
  rescue
    _ -> :not_available
  end

  # ============================================================================
  # Private: Fallbacks
  # ============================================================================

  # Fallback hash using SHA3-256
  defp fallback_hash(data, output_size) do
    hash = :crypto.hash(:sha3_256, data)
    extend_hash(hash, output_size)
  end

  # Fallback MAC using HMAC-SHA3-256
  defp fallback_mac(data, key) do
    :crypto.mac(:hmac, :sha3_256, key, data)
  end

  # Fallback KDF using HKDF-SHA3-256
  defp fallback_kdf(context, ikm, output_size) do
    # HKDF-Extract with context as salt
    salt = :crypto.hash(:sha3_256, context)
    prk = :crypto.mac(:hmac, :sha3_256, salt, ikm)

    # HKDF-Expand
    hkdf_expand(prk, context, output_size)
  end

  defp hkdf_expand(prk, info, length) do
    hkdf_expand_acc(prk, info, length, <<>>, 1)
  end

  defp hkdf_expand_acc(_prk, _info, length, acc, _counter) when byte_size(acc) >= length do
    binary_part(acc, 0, length)
  end

  defp hkdf_expand_acc(prk, info, length, acc, counter) do
    prev = if acc == <<>>, do: <<>>, else: binary_part(acc, byte_size(acc) - 32, 32)
    block = :crypto.mac(:hmac, :sha3_256, prk, prev <> info <> <<counter>>)
    hkdf_expand_acc(prk, info, length, acc <> block, counter + 1)
  end

  # Extend hash to desired output size
  defp extend_hash(hash, output_size) when byte_size(hash) >= output_size do
    binary_part(hash, 0, output_size)
  end

  defp extend_hash(hash, output_size) do
    # XOF-like extension using counter mode
    extend_hash_acc(hash, output_size, hash, 1)
  end

  defp extend_hash_acc(_base, output_size, acc, _counter) when byte_size(acc) >= output_size do
    binary_part(acc, 0, output_size)
  end

  defp extend_hash_acc(base, output_size, acc, counter) do
    block = :crypto.hash(:sha3_256, base <> <<counter::32>>)
    extend_hash_acc(base, output_size, acc <> block, counter + 1)
  end

  defp sha3_available?(algo) do
    try do
      :crypto.hash(algo, "test")
      true
    rescue
      _ -> false
    end
  end
end
