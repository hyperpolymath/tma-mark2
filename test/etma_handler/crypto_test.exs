# SPDX-License-Identifier: AGPL-3.0-or-later
defmodule EtmaHandler.CryptoTest do
  use ExUnit.Case, async: true

  alias EtmaHandler.Crypto

  describe "hash/1" do
    test "produces 32-byte BLAKE3 hash" do
      {:ok, hash} = Crypto.hash("hello world")
      assert byte_size(hash) == 32
    end

    test "same input produces same hash" do
      {:ok, hash1} = Crypto.hash("test data")
      {:ok, hash2} = Crypto.hash("test data")
      assert hash1 == hash2
    end

    test "different input produces different hash" do
      {:ok, hash1} = Crypto.hash("input 1")
      {:ok, hash2} = Crypto.hash("input 2")
      refute hash1 == hash2
    end
  end

  describe "hash_xof/2" do
    test "produces variable-length output" do
      {:ok, hash64} = Crypto.hash_xof("data", 64)
      {:ok, hash128} = Crypto.hash_xof("data", 128)

      assert byte_size(hash64) == 64
      assert byte_size(hash128) == 128
    end
  end

  describe "encrypt/decrypt" do
    test "round-trip encryption works" do
      {:ok, key} = Crypto.random_bytes(32)
      plaintext = "secret message"

      {:ok, encrypted} = Crypto.encrypt(key, plaintext)
      {:ok, decrypted} = Crypto.decrypt(key, encrypted)

      assert decrypted == plaintext
    end

    test "encryption with AAD works" do
      {:ok, key} = Crypto.random_bytes(32)
      plaintext = "secret"
      aad = "authenticated data"

      {:ok, encrypted} = Crypto.encrypt(key, plaintext, aad)
      {:ok, decrypted} = Crypto.decrypt(key, encrypted, aad)

      assert decrypted == plaintext
    end

    test "decryption with wrong AAD fails" do
      {:ok, key} = Crypto.random_bytes(32)
      plaintext = "secret"

      {:ok, encrypted} = Crypto.encrypt(key, plaintext, "correct")
      result = Crypto.decrypt(key, encrypted, "wrong")

      assert match?({:error, _}, result)
    end

    test "decryption with wrong key fails" do
      {:ok, key1} = Crypto.random_bytes(32)
      {:ok, key2} = Crypto.random_bytes(32)
      plaintext = "secret"

      {:ok, encrypted} = Crypto.encrypt(key1, plaintext)
      result = Crypto.decrypt(key2, encrypted)

      assert match?({:error, _}, result)
    end
  end

  describe "seal/unseal" do
    test "password-based encryption round-trip" do
      password = "strong password"
      plaintext = "secret data"

      {:ok, sealed} = Crypto.seal(password, plaintext)
      {:ok, unsealed} = Crypto.unseal(password, sealed)

      assert unsealed == plaintext
    end

    test "wrong password fails" do
      {:ok, sealed} = Crypto.seal("correct", "data")
      result = Crypto.unseal("wrong", sealed)

      assert match?({:error, _}, result)
    end
  end

  describe "Kyber key exchange" do
    test "encapsulation and decapsulation produce same shared secret" do
      {:ok, {public_key, secret_key}} = Crypto.generate_keypair()
      {:ok, {ciphertext, shared_secret1}} = Crypto.encapsulate(public_key)
      {:ok, shared_secret2} = Crypto.decapsulate(ciphertext, secret_key)

      assert shared_secret1 == shared_secret2
    end

    test "wrong secret key produces different shared secret" do
      {:ok, {public_key, _secret_key1}} = Crypto.generate_keypair()
      {:ok, {_public_key2, secret_key2}} = Crypto.generate_keypair()

      {:ok, {ciphertext, shared_secret1}} = Crypto.encapsulate(public_key)
      {:ok, shared_secret2} = Crypto.decapsulate(ciphertext, secret_key2)

      refute shared_secret1 == shared_secret2
    end
  end

  describe "Dilithium signatures" do
    test "signature verification works" do
      {:ok, {public_key, secret_key}} = Crypto.generate_signing_keypair()
      message = "important document"

      {:ok, signature} = Crypto.sign(message, secret_key)
      {:ok, true} = Crypto.verify(message, signature, public_key)
    end

    test "wrong message fails verification" do
      {:ok, {public_key, secret_key}} = Crypto.generate_signing_keypair()

      {:ok, signature} = Crypto.sign("original", secret_key)
      result = Crypto.verify("tampered", signature, public_key)

      assert match?({:error, _}, result)
    end

    test "wrong public key fails verification" do
      {:ok, {_public_key1, secret_key}} = Crypto.generate_signing_keypair()
      {:ok, {public_key2, _secret_key2}} = Crypto.generate_signing_keypair()
      message = "message"

      {:ok, signature} = Crypto.sign(message, secret_key)
      result = Crypto.verify(message, signature, public_key2)

      assert match?({:error, _}, result)
    end
  end

  describe "key derivation" do
    test "derive_key produces consistent output" do
      context = "tma-mark2 test"
      ikm = "input key material"

      {:ok, key1} = Crypto.derive_key(context, ikm, 32)
      {:ok, key2} = Crypto.derive_key(context, ikm, 32)

      assert key1 == key2
    end

    test "different contexts produce different keys" do
      ikm = "same input"

      {:ok, key1} = Crypto.derive_key("context 1", ikm, 32)
      {:ok, key2} = Crypto.derive_key("context 2", ikm, 32)

      refute key1 == key2
    end
  end

  describe "password_hash/1" do
    test "produces PHC-formatted string" do
      hash = Crypto.password_hash("password123")
      assert String.starts_with?(hash, "$argon2id$")
    end

    test "different passwords produce different hashes" do
      hash1 = Crypto.password_hash("password1")
      hash2 = Crypto.password_hash("password2")
      refute hash1 == hash2
    end
  end

  describe "password_verify/2" do
    test "verifies correct password" do
      password = "my secure password"
      hash = Crypto.password_hash(password)
      assert Crypto.password_verify(password, hash)
    end

    test "rejects wrong password" do
      hash = Crypto.password_hash("correct")
      refute Crypto.password_verify("wrong", hash)
    end
  end

  describe "random_bytes/1" do
    test "generates bytes of correct length" do
      {:ok, bytes} = Crypto.random_bytes(64)
      assert byte_size(bytes) == 64
    end

    test "generates unique values" do
      {:ok, bytes1} = Crypto.random_bytes(32)
      {:ok, bytes2} = Crypto.random_bytes(32)
      refute bytes1 == bytes2
    end
  end

  describe "uuid/0" do
    test "generates valid UUID v4 format" do
      uuid = Crypto.uuid()
      assert Regex.match?(~r/^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/, uuid)
    end

    test "generates unique UUIDs" do
      uuid1 = Crypto.uuid()
      uuid2 = Crypto.uuid()
      refute uuid1 == uuid2
    end
  end

  describe "secure_compare/2" do
    test "returns true for equal binaries" do
      assert Crypto.secure_compare("hello", "hello")
    end

    test "returns false for different binaries" do
      refute Crypto.secure_compare("hello", "world")
    end

    test "returns false for different lengths" do
      refute Crypto.secure_compare("short", "longer string")
    end
  end
end
