# SPDX-FileCopyrightText: 2024 eTMA Handler Contributors
# SPDX-License-Identifier: MIT

defmodule EtmaHandler.Crypto.Signatures do
  @moduledoc """
  Digital signature operations using Ed448 (primary) with Ed25519 fallback.

  ## Why Ed448?

  Ed448 provides ~224-bit security level (vs Ed25519's ~128-bit), making it
  suitable for high-security applications and more resistant to future advances.

  - **Ed448-Goldilocks**: 448-bit Edwards curve, SHAKE256 XOF
  - **Ed25519**: 255-bit Edwards curve, SHA-512 (fallback for older systems)

  ## Usage

      # Generate signing keypair
      {public_key, secret_key} = Signatures.generate_keypair()

      # Sign a message
      signature = Signatures.sign(message, secret_key)

      # Verify signature
      :ok = Signatures.verify(message, signature, public_key)

  ## Wire Format

  Keys and signatures are prefixed with a version byte:
  - 0x01 = Ed448 (57-byte keys, 114-byte signatures)
  - 0x00 = Ed25519 (32-byte keys, 64-byte signatures)
  """

  require Logger

  # Version bytes
  @version_ed448 0x01
  @version_ed25519 0x00

  # Key sizes
  @ed448_pk_size 57
  @ed448_sk_size 57
  @ed448_sig_size 114

  @ed25519_pk_size 32
  @ed25519_sk_size 32
  @ed25519_sig_size 64

  @doc """
  Generate a signing keypair.

  Returns `{public_key, secret_key}` with version prefix.
  Uses Ed448 if available, falls back to Ed25519.
  """
  @spec generate_keypair() :: {binary(), binary()}
  def generate_keypair do
    if ed448_available?() do
      generate_ed448_keypair()
    else
      generate_ed25519_keypair()
    end
  end

  @doc """
  Generate an Ed448 keypair specifically.
  Returns `:not_available` if Ed448 is not supported.
  """
  @spec generate_ed448_keypair() :: {binary(), binary()} | :not_available
  def generate_ed448_keypair do
    try do
      {public_key, secret_key} = :crypto.generate_key(:eddsa, :ed448)
      {<<@version_ed448, public_key::binary>>, <<@version_ed448, secret_key::binary>>}
    rescue
      _ -> :not_available
    end
  end

  @doc """
  Generate an Ed25519 keypair.
  """
  @spec generate_ed25519_keypair() :: {binary(), binary()}
  def generate_ed25519_keypair do
    {public_key, secret_key} = :crypto.generate_key(:eddsa, :ed25519)
    {<<@version_ed25519, public_key::binary>>, <<@version_ed25519, secret_key::binary>>}
  end

  @doc """
  Sign a message.

  Returns the signature with version prefix matching the key type.
  """
  @spec sign(binary(), binary()) :: binary() | {:error, term()}
  def sign(message, secret_key) when is_binary(message) and is_binary(secret_key) do
    case parse_secret_key(secret_key) do
      {:ed448, sk} ->
        signature = :crypto.sign(:eddsa, :shake256, message, [sk, :ed448])
        <<@version_ed448, signature::binary>>

      {:ed25519, sk} ->
        signature = :crypto.sign(:eddsa, :sha512, message, [sk, :ed25519])
        <<@version_ed25519, signature::binary>>

      {:error, reason} ->
        {:error, reason}
    end
  rescue
    e -> {:error, {:sign_failed, e}}
  end

  def sign(_, _), do: {:error, :invalid_input}

  @doc """
  Verify a signature.

  Returns `:ok` if valid, `{:error, :invalid_signature}` otherwise.
  """
  @spec verify(binary(), binary(), binary()) :: :ok | {:error, term()}
  def verify(message, signature, public_key)
      when is_binary(message) and is_binary(signature) and is_binary(public_key) do
    case {parse_signature(signature), parse_public_key(public_key)} do
      {{:ed448, sig}, {:ed448, pk}} ->
        if :crypto.verify(:eddsa, :shake256, message, sig, [pk, :ed448]) do
          :ok
        else
          {:error, :invalid_signature}
        end

      {{:ed25519, sig}, {:ed25519, pk}} ->
        if :crypto.verify(:eddsa, :sha512, message, sig, [pk, :ed25519]) do
          :ok
        else
          {:error, :invalid_signature}
        end

      {{:error, reason}, _} ->
        {:error, reason}

      {_, {:error, reason}} ->
        {:error, reason}

      _ ->
        {:error, :key_signature_mismatch}
    end
  rescue
    e -> {:error, {:verify_failed, e}}
  end

  def verify(_, _, _), do: {:error, :invalid_input}

  @doc """
  Check if Ed448 is available on this system.
  """
  @spec ed448_available?() :: boolean()
  def ed448_available? do
    try do
      # Try to generate a keypair to check availability
      {_pk, _sk} = :crypto.generate_key(:eddsa, :ed448)
      true
    rescue
      _ -> false
    end
  end

  @doc """
  Get information about available signature algorithms.
  """
  @spec info() :: map()
  def info do
    %{
      ed448_available: ed448_available?(),
      default_algorithm: if(ed448_available?(), do: :ed448, else: :ed25519),
      ed448_security_bits: 224,
      ed25519_security_bits: 128
    }
  end

  # ============================================================================
  # Private: Parsing
  # ============================================================================

  defp parse_public_key(<<@version_ed448, pk::binary-size(@ed448_pk_size)>>), do: {:ed448, pk}
  defp parse_public_key(<<@version_ed25519, pk::binary-size(@ed25519_pk_size)>>), do: {:ed25519, pk}
  defp parse_public_key(_), do: {:error, :invalid_public_key}

  defp parse_secret_key(<<@version_ed448, sk::binary-size(@ed448_sk_size)>>), do: {:ed448, sk}
  defp parse_secret_key(<<@version_ed25519, sk::binary-size(@ed25519_sk_size)>>), do: {:ed25519, sk}
  defp parse_secret_key(_), do: {:error, :invalid_secret_key}

  defp parse_signature(<<@version_ed448, sig::binary-size(@ed448_sig_size)>>), do: {:ed448, sig}
  defp parse_signature(<<@version_ed25519, sig::binary-size(@ed25519_sig_size)>>), do: {:ed25519, sig}
  defp parse_signature(_), do: {:error, :invalid_signature}
end
