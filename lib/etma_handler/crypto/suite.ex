# SPDX-FileCopyrightText: 2024 eTMA Handler Contributors
# SPDX-License-Identifier: MIT

defmodule EtmaHandler.Crypto.Suite do
  @moduledoc """
  Comprehensive Cryptographic Suite for eTMA Handler (Pure Elixir fallback).

  Implements defense-in-depth security using best-in-class algorithms:

  ## Encryption (Hybrid Post-Quantum)
  - **ML-KEM-1024** (FIPS 203) - Post-quantum key encapsulation
  - **X25519** - Classical elliptic curve Diffie-Hellman
  - **XChaCha20-Poly1305** - Authenticated encryption (AEAD)

  ## Signatures
  - **Ed448** - Primary signature algorithm (224-bit security)
  - **Ed25519** - Fallback for older systems (128-bit security)

  ## Hashing
  - **BLAKE3** - Fast, secure hashing and key derivation
  - **SHA3-512** - Maximum security margin (256-bit)
  - **SHAKE256** - Variable-length output (XOF)

  ## Password Hashing
  - **Argon2id** - Memory-hard, side-channel resistant

  ## Authentication
  - **FIDO2/WebAuthn** - Passwordless and 2FA with hardware keys

  ## Key Derivation
  - **HKDF-SHA256** - Standard KDF for key material
  - **BLAKE3-KDF** - Built-in BLAKE3 key derivation mode

  ## Why These Choices?

  | Algorithm     | Why                                                    |
  |---------------|--------------------------------------------------------|
  | ML-KEM-1024   | NIST FIPS 203, quantum-safe, conservative parameter   |
  | X25519        | Battle-tested ECDH, defense-in-depth with ML-KEM      |
  | Ed448         | ~224-bit security vs Ed25519's ~128-bit                |
  | BLAKE3        | 5x faster than SHA-256, proven security (ChaCha-based)|
  | SHA3-512      | NIST standard, maximum security margin                 |
  | Argon2id      | Winner of Password Hashing Competition, side-channel safe |
  | XChaCha20     | Extended nonce (safe random generation), fast          |

  ## Usage

      # Hybrid encryption (post-quantum safe)
      {public_key, secret_key} = Crypto.generate_keypair()
      {:ok, ciphertext} = Crypto.encrypt(plaintext, public_key)
      {:ok, plaintext} = Crypto.decrypt(ciphertext, secret_key)

      # Digital signatures (Ed448)
      {sign_pk, sign_sk} = Crypto.generate_signing_keypair()
      signature = Crypto.sign(message, sign_sk)
      :ok = Crypto.verify(message, signature, sign_pk)

      # Hashing
      hash = Crypto.hash(data)                    # BLAKE3
      hash = Crypto.hash(data, :sha3_512)        # SHA3-512

      # Password hashing
      hash = Crypto.hash_password(password)
      :ok = Crypto.verify_password(password, hash)

  ## Security Properties

  - **Post-quantum resistant**: ML-KEM-1024 protects against quantum computers
  - **Forward secrecy**: Ephemeral keys per encryption operation
  - **Defense-in-depth**: Hybrid KEM (break both to decrypt)
  - **Side-channel resistant**: Argon2id, constant-time operations
  - **Phishing resistant**: WebAuthn origin binding
  """

  alias EtmaHandler.Crypto.{Hybrid, Backend, Signatures, Hashing, WebAuthn}

  @type public_key :: binary()
  @type secret_key :: binary()
  @type keypair :: {public_key(), secret_key()}
  @type ciphertext :: binary()
  @type plaintext :: binary()
  @type signature :: binary()

  # ============================================================================
  # Hybrid Encryption (Post-Quantum)
  # ============================================================================

  @doc """
  Generate a hybrid keypair (X25519 + ML-KEM-1024).

  Returns `{public_key, secret_key}` where both keys contain
  concatenated classical and post-quantum components.
  """
  @spec generate_keypair() :: keypair()
  defdelegate generate_keypair(), to: Hybrid

  @doc """
  Encrypt plaintext using hybrid post-quantum encryption.

  1. Generates ephemeral X25519 keypair
  2. Encapsulates shared secret via ML-KEM-1024
  3. Derives symmetric key using HKDF
  4. Encrypts with XChaCha20-Poly1305

  Returns `{:ok, ciphertext}` or `{:error, reason}`.
  """
  @spec encrypt(plaintext(), public_key()) :: {:ok, ciphertext()} | {:error, term()}
  defdelegate encrypt(plaintext, public_key), to: Hybrid

  @doc """
  Decrypt ciphertext using hybrid decryption.

  Returns `{:ok, plaintext}` or `{:error, reason}`.
  """
  @spec decrypt(ciphertext(), secret_key()) :: {:ok, plaintext()} | {:error, term()}
  defdelegate decrypt(ciphertext, secret_key), to: Hybrid

  # ============================================================================
  # Digital Signatures (Ed448/Ed25519)
  # ============================================================================

  @doc """
  Generate a signing keypair.

  Uses Ed448 if available (224-bit security), otherwise Ed25519 (128-bit).
  """
  @spec generate_signing_keypair() :: keypair()
  defdelegate generate_signing_keypair(), to: Signatures, as: :generate_keypair

  @doc """
  Sign a message.

  Returns the signature with version prefix indicating algorithm used.
  """
  @spec sign(binary(), secret_key()) :: signature() | {:error, term()}
  defdelegate sign(message, secret_key), to: Signatures

  @doc """
  Verify a signature.

  Returns `:ok` if valid, `{:error, reason}` otherwise.
  """
  @spec verify(binary(), signature(), public_key()) :: :ok | {:error, term()}
  defdelegate verify(message, signature, public_key), to: Signatures

  # ============================================================================
  # Hashing
  # ============================================================================

  @doc """
  Compute hash of data.

  Default algorithm is BLAKE3 (fastest, proven security).

  ## Options

  - `:blake3` - BLAKE3, 256-bit output (default)
  - `:sha3_512` - SHA3-512, 512-bit output
  - `:sha3_256` - SHA3-256, 256-bit output
  - `:sha256` - SHA-256 (compatibility)
  """
  @spec hash(binary(), atom()) :: binary()
  def hash(data, algorithm \\ :blake3) do
    case algorithm do
      :blake3 -> Hashing.blake3(data)
      :sha3_512 -> Hashing.sha3_512(data)
      :sha3_256 -> Hashing.sha3_256(data)
      :sha256 -> Hashing.sha256(data)
      :shake256 -> Hashing.shake256(data, 64)
    end
  end

  @doc """
  Compute BLAKE3 keyed hash (MAC).

  Key must be exactly 32 bytes.
  """
  @spec keyed_hash(binary(), binary()) :: binary()
  defdelegate keyed_hash(data, key), to: Hashing, as: :blake3_keyed

  @doc """
  Derive a key using BLAKE3-KDF.

  Context should be a unique string for your application/use case.
  """
  @spec derive_key(String.t(), binary(), non_neg_integer()) :: binary()
  defdelegate derive_key(context, ikm, output_size), to: Hashing, as: :blake3_derive_key

  # ============================================================================
  # Password Hashing (Argon2id)
  # ============================================================================

  @doc """
  Hash a password using Argon2id.

  Returns encoded hash string suitable for storage.
  """
  @spec hash_password(String.t()) :: String.t()
  def hash_password(password) when is_binary(password) do
    Argon2.hash_pwd_salt(password)
  end

  @doc """
  Verify a password against a stored hash.

  Returns `:ok` if valid, `{:error, :invalid_password}` otherwise.
  """
  @spec verify_password(String.t(), String.t()) :: :ok | {:error, :invalid_password}
  def verify_password(password, hash) when is_binary(password) and is_binary(hash) do
    if Argon2.verify_pass(password, hash) do
      :ok
    else
      {:error, :invalid_password}
    end
  end

  # ============================================================================
  # WebAuthn / FIDO2
  # ============================================================================

  @doc """
  Create a WebAuthn registration challenge.

  See `EtmaHandler.Crypto.WebAuthn.create_registration_challenge/2` for options.
  """
  @spec create_registration_challenge(binary(), keyword()) :: {:ok, map()}
  defdelegate create_registration_challenge(user_id, opts), to: WebAuthn

  @doc """
  Verify a WebAuthn registration response.
  """
  @spec verify_registration(map(), map()) :: {:ok, map()} | {:error, term()}
  defdelegate verify_registration(response, challenge), to: WebAuthn

  @doc """
  Create a WebAuthn authentication challenge.
  """
  @spec create_auth_challenge([binary()], keyword()) :: {:ok, map()}
  defdelegate create_auth_challenge(credential_ids, opts \\ []), to: WebAuthn

  @doc """
  Verify a WebAuthn authentication response.
  """
  @spec verify_authentication(map(), map(), map()) :: {:ok, binary(), non_neg_integer()} | {:error, term()}
  defdelegate verify_authentication(response, challenge, credential), to: WebAuthn

  # ============================================================================
  # Backend Information
  # ============================================================================

  @doc """
  Returns comprehensive information about the cryptographic backend.
  """
  @spec backend_info() :: map()
  def backend_info do
    %{
      encryption: Backend.backend_info(),
      signatures: Signatures.info(),
      hashing: Hashing.info(),
      webauthn: WebAuthn.info(),
      algorithms: %{
        kem: ["ML-KEM-1024", "X25519"],
        aead: "XChaCha20-Poly1305",
        signatures: ["Ed448", "Ed25519"],
        hashing: ["BLAKE3", "SHA3-512", "SHA3-256", "SHAKE256"],
        password: "Argon2id",
        kdf: ["BLAKE3-KDF", "HKDF-SHA256"]
      }
    }
  end

  @doc """
  Check if post-quantum cryptography is available.
  """
  @spec pqc_available?() :: boolean()
  def pqc_available?, do: Backend.pqc_available?()

  @doc """
  Check if Ed448 signatures are available.
  """
  @spec ed448_available?() :: boolean()
  def ed448_available?, do: Signatures.ed448_available?()

  @doc """
  Check if BLAKE3 is available.
  """
  @spec blake3_available?() :: boolean()
  def blake3_available?, do: Hashing.blake3_available?()
end
