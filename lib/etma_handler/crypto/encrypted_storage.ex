# SPDX-FileCopyrightText: 2024 eTMA Handler Contributors
# SPDX-License-Identifier: MIT

defmodule EtmaHandler.Crypto.EncryptedStorage do
  @moduledoc """
  Encrypted storage layer for CubDB using hybrid post-quantum cryptography.

  This module provides transparent encryption for stored data:

  1. On first run, generates a master keypair (stored encrypted with passphrase)
  2. All data is encrypted before writing to CubDB
  3. Data is decrypted transparently on read

  ## Key Hierarchy

  ```
  Passphrase (user input)
      │
      ▼
  Master Key (Argon2id derivation)
      │
      ▼
  Encrypted Storage Keypair (ML-KEM-1024 + X25519)
      │
      ├──▶ Data Encryption Key (per-record, ephemeral)
      │
      └──▶ Stored encrypted in keyfile
  ```

  ## Usage

      # Initialize with passphrase
      {:ok, storage} = EncryptedStorage.init("/path/to/data", "my-passphrase")

      # Store encrypted data
      :ok = EncryptedStorage.put(storage, "key", %{secret: "data"})

      # Retrieve and decrypt
      {:ok, data} = EncryptedStorage.get(storage, "key")

  ## Security Properties

  - **Post-quantum resistant**: ML-KEM-1024 (FIPS 203)
  - **Forward secrecy**: Ephemeral keys per encryption
  - **Key stretching**: Argon2id for passphrase derivation
  - **Authenticated encryption**: XChaCha20-Poly1305
  """

  alias EtmaHandler.Crypto
  alias EtmaHandler.Crypto.Backend

  require Logger

  @keyfile_name "etma.key"
  @argon2_time_cost 3
  @argon2_memory_cost 65536  # 64 MB
  @argon2_parallelism 4

  defstruct [:db, :public_key, :secret_key, :path]

  @type t :: %__MODULE__{
    db: CubDB.db(),
    public_key: binary(),
    secret_key: binary(),
    path: String.t()
  }

  @doc """
  Initialize encrypted storage.

  If keyfile exists, decrypts it with the passphrase.
  If keyfile doesn't exist, generates new keypair and encrypts it.
  """
  @spec init(String.t(), String.t(), keyword()) :: {:ok, t()} | {:error, term()}
  def init(path, passphrase, opts \\ []) do
    keyfile_path = Path.join(path, @keyfile_name)
    db_path = Path.join(path, "encrypted.cub")

    # Ensure directory exists
    File.mkdir_p!(path)

    # Derive key encryption key from passphrase
    kek = derive_key_encryption_key(passphrase, path)

    # Load or generate keypair
    case load_or_generate_keypair(keyfile_path, kek) do
      {:ok, {public_key, secret_key}} ->
        # Open CubDB
        {:ok, db} = CubDB.start_link(data_dir: db_path)

        storage = %__MODULE__{
          db: db,
          public_key: public_key,
          secret_key: secret_key,
          path: path
        }

        Logger.info("Encrypted storage initialized: #{inspect(Backend.backend_info())}")

        {:ok, storage}

      {:error, reason} ->
        {:error, reason}
    end
  end

  @doc """
  Store encrypted data.
  """
  @spec put(t(), term(), term()) :: :ok | {:error, term()}
  def put(%__MODULE__{db: db, public_key: pk}, key, value) do
    # Serialize value
    plaintext = :erlang.term_to_binary(value)

    # Encrypt with hybrid crypto
    case Crypto.encrypt(plaintext, pk) do
      {:ok, ciphertext} ->
        CubDB.put(db, key, ciphertext)
        :ok

      {:error, reason} ->
        {:error, reason}
    end
  end

  @doc """
  Retrieve and decrypt data.
  """
  @spec get(t(), term()) :: {:ok, term()} | {:error, term()} | :not_found
  def get(%__MODULE__{db: db, secret_key: sk}, key) do
    case CubDB.get(db, key) do
      nil ->
        :not_found

      ciphertext ->
        case Crypto.decrypt(ciphertext, sk) do
          {:ok, plaintext} ->
            value = :erlang.binary_to_term(plaintext)
            {:ok, value}

          {:error, reason} ->
            {:error, reason}
        end
    end
  end

  @doc """
  Delete data.
  """
  @spec delete(t(), term()) :: :ok
  def delete(%__MODULE__{db: db}, key) do
    CubDB.delete(db, key)
    :ok
  end

  @doc """
  Check if key exists (without decrypting).
  """
  @spec has_key?(t(), term()) :: boolean()
  def has_key?(%__MODULE__{db: db}, key) do
    CubDB.has_key?(db, key)
  end

  @doc """
  Get all keys (without decrypting values).
  """
  @spec keys(t()) :: [term()]
  def keys(%__MODULE__{db: db}) do
    CubDB.select(db)
    |> Enum.map(fn {k, _v} -> k end)
  end

  @doc """
  Re-encrypt all data with a new passphrase.
  """
  @spec change_passphrase(t(), String.t(), String.t()) :: {:ok, t()} | {:error, term()}
  def change_passphrase(%__MODULE__{} = storage, old_passphrase, new_passphrase) do
    keyfile_path = Path.join(storage.path, @keyfile_name)

    # Verify old passphrase by trying to decrypt
    old_kek = derive_key_encryption_key(old_passphrase, storage.path)

    case decrypt_keyfile(keyfile_path, old_kek) do
      {:ok, {public_key, secret_key}} ->
        # Re-encrypt with new passphrase
        new_kek = derive_key_encryption_key(new_passphrase, storage.path)
        :ok = encrypt_and_save_keypair(keyfile_path, public_key, secret_key, new_kek)

        {:ok, storage}

      {:error, _} ->
        {:error, :invalid_passphrase}
    end
  end

  @doc """
  Close the storage.
  """
  @spec close(t()) :: :ok
  def close(%__MODULE__{db: db}) do
    CubDB.stop(db)
  end

  @doc """
  Get storage info.
  """
  @spec info(t()) :: map()
  def info(%__MODULE__{db: db, path: path}) do
    %{
      path: path,
      crypto: Backend.backend_info(),
      db_size: CubDB.size(db),
      db_dirt: CubDB.dirt_factor(db)
    }
  end

  # ============================================================================
  # Private
  # ============================================================================

  defp derive_key_encryption_key(passphrase, path) do
    # Use path as salt component for domain separation
    salt = :crypto.hash(:sha256, "etma-kek-v1:" <> path)

    # Argon2id key derivation
    Argon2.Base.hash_password(
      passphrase,
      salt,
      t_cost: @argon2_time_cost,
      m_cost: @argon2_memory_cost,
      parallelism: @argon2_parallelism,
      hashlen: 32,
      format: :raw_hash
    )
  end

  defp load_or_generate_keypair(keyfile_path, kek) do
    if File.exists?(keyfile_path) do
      decrypt_keyfile(keyfile_path, kek)
    else
      generate_and_save_keypair(keyfile_path, kek)
    end
  end

  defp generate_and_save_keypair(keyfile_path, kek) do
    # Generate new hybrid keypair
    {public_key, secret_key} = Crypto.generate_keypair()

    # Encrypt and save
    :ok = encrypt_and_save_keypair(keyfile_path, public_key, secret_key, kek)

    Logger.info("Generated new encryption keypair (#{Backend.backend_info().ml_kem_variant || "classical"})")

    {:ok, {public_key, secret_key}}
  end

  defp encrypt_and_save_keypair(keyfile_path, public_key, secret_key, kek) do
    # Serialize keypair
    keypair_data = :erlang.term_to_binary({public_key, secret_key})

    # Encrypt with KEK using AEAD
    encrypted = Backend.aead_encrypt(keypair_data, kek, "etma-keyfile-v1")

    # Write atomically
    tmp_path = keyfile_path <> ".tmp"
    File.write!(tmp_path, encrypted)
    File.rename!(tmp_path, keyfile_path)

    :ok
  end

  defp decrypt_keyfile(keyfile_path, kek) do
    encrypted = File.read!(keyfile_path)

    case Backend.aead_decrypt(encrypted, kek, "etma-keyfile-v1") do
      {:ok, plaintext} ->
        {public_key, secret_key} = :erlang.binary_to_term(plaintext)
        {:ok, {public_key, secret_key}}

      {:error, :auth_failed} ->
        {:error, :invalid_passphrase}
    end
  rescue
    e ->
      {:error, {:keyfile_read_error, e}}
  end
end
