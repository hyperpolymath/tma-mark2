// SPDX-License-Identifier: AGPL-3.0-or-later
//! Cryptographic operations for the mobile app
//!
//! Uses the same post-quantum algorithms as the main application:
//! - BLAKE3 for hashing
//! - Argon2id for password hashing
//! - XChaCha20-Poly1305 for symmetric encryption
//! - Kyber-1024 (ML-KEM) for key encapsulation
//! - Dilithium5 (ML-DSA) for signatures

use blake3::Hasher;
use chacha20poly1305::{
    aead::{Aead, KeyInit},
    XChaCha20Poly1305, XNonce,
};
use pqcrypto_mlkem::mlkem1024;
use pqcrypto_mldsa::mldsa87;
use thiserror::Error;

#[derive(Error, Debug)]
pub enum CryptoError {
    #[error("Encryption failed")]
    EncryptionFailed,
    #[error("Decryption failed")]
    DecryptionFailed,
    #[error("Invalid key length")]
    InvalidKeyLength,
    #[error("Signature verification failed")]
    VerificationFailed,
}

/// Hash data using BLAKE3
pub fn hash(data: &[u8]) -> [u8; 32] {
    blake3::hash(data).into()
}

/// Hash with extendable output
pub fn hash_xof(data: &[u8], output_len: usize) -> Vec<u8> {
    let mut hasher = Hasher::new();
    hasher.update(data);
    let mut output = vec![0u8; output_len];
    hasher.finalize_xof().fill(&mut output);
    output
}

/// Derive a key using BLAKE3-KDF
pub fn derive_key(context: &str, key_material: &[u8], output_len: usize) -> Vec<u8> {
    let mut hasher = Hasher::new_derive_key(context);
    hasher.update(key_material);
    let mut output = vec![0u8; output_len];
    hasher.finalize_xof().fill(&mut output);
    output
}

/// Encrypt data using XChaCha20-Poly1305
pub fn encrypt(key: &[u8; 32], nonce: &[u8; 24], plaintext: &[u8]) -> Result<Vec<u8>, CryptoError> {
    let cipher = XChaCha20Poly1305::new(key.into());
    let nonce = XNonce::from_slice(nonce);

    cipher
        .encrypt(nonce, plaintext)
        .map_err(|_| CryptoError::EncryptionFailed)
}

/// Decrypt data using XChaCha20-Poly1305
pub fn decrypt(key: &[u8; 32], nonce: &[u8; 24], ciphertext: &[u8]) -> Result<Vec<u8>, CryptoError> {
    let cipher = XChaCha20Poly1305::new(key.into());
    let nonce = XNonce::from_slice(nonce);

    cipher
        .decrypt(nonce, ciphertext)
        .map_err(|_| CryptoError::DecryptionFailed)
}

/// Generate a Kyber-1024 keypair
pub fn generate_keypair() -> (Vec<u8>, Vec<u8>) {
    let (pk, sk) = mlkem1024::keypair();
    (pk.as_bytes().to_vec(), sk.as_bytes().to_vec())
}

/// Encapsulate a shared secret using Kyber public key
pub fn encapsulate(public_key: &[u8]) -> Result<(Vec<u8>, Vec<u8>), CryptoError> {
    let pk = mlkem1024::PublicKey::from_bytes(public_key)
        .map_err(|_| CryptoError::InvalidKeyLength)?;
    let (ss, ct) = mlkem1024::encapsulate(&pk);
    Ok((ct.as_bytes().to_vec(), ss.as_bytes().to_vec()))
}

/// Decapsulate a shared secret using Kyber secret key
pub fn decapsulate(ciphertext: &[u8], secret_key: &[u8]) -> Result<Vec<u8>, CryptoError> {
    let sk = mlkem1024::SecretKey::from_bytes(secret_key)
        .map_err(|_| CryptoError::InvalidKeyLength)?;
    let ct = mlkem1024::Ciphertext::from_bytes(ciphertext)
        .map_err(|_| CryptoError::InvalidKeyLength)?;
    let ss = mlkem1024::decapsulate(&ct, &sk);
    Ok(ss.as_bytes().to_vec())
}

/// Generate a Dilithium5 signing keypair
pub fn generate_signing_keypair() -> (Vec<u8>, Vec<u8>) {
    let (pk, sk) = mldsa87::keypair();
    (pk.as_bytes().to_vec(), sk.as_bytes().to_vec())
}

/// Sign a message using Dilithium5
pub fn sign(message: &[u8], secret_key: &[u8]) -> Result<Vec<u8>, CryptoError> {
    let sk = mldsa87::SecretKey::from_bytes(secret_key)
        .map_err(|_| CryptoError::InvalidKeyLength)?;
    let sig = mldsa87::detached_sign(message, &sk);
    Ok(sig.as_bytes().to_vec())
}

/// Verify a Dilithium5 signature
pub fn verify(message: &[u8], signature: &[u8], public_key: &[u8]) -> Result<bool, CryptoError> {
    let pk = mldsa87::PublicKey::from_bytes(public_key)
        .map_err(|_| CryptoError::InvalidKeyLength)?;
    let sig = mldsa87::DetachedSignature::from_bytes(signature)
        .map_err(|_| CryptoError::InvalidKeyLength)?;

    match mldsa87::verify_detached_signature(&sig, message, &pk) {
        Ok(()) => Ok(true),
        Err(_) => Err(CryptoError::VerificationFailed),
    }
}

/// Generate cryptographically secure random bytes
pub fn random_bytes(len: usize) -> Vec<u8> {
    use rand::RngCore;
    let mut bytes = vec![0u8; len];
    rand::thread_rng().fill_bytes(&mut bytes);
    bytes
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_hash() {
        let hash1 = hash(b"hello");
        let hash2 = hash(b"hello");
        assert_eq!(hash1, hash2);

        let hash3 = hash(b"world");
        assert_ne!(hash1, hash3);
    }

    #[test]
    fn test_encrypt_decrypt() {
        let key = [0u8; 32];
        let nonce = [0u8; 24];
        let plaintext = b"secret message";

        let ciphertext = encrypt(&key, &nonce, plaintext).unwrap();
        let decrypted = decrypt(&key, &nonce, &ciphertext).unwrap();

        assert_eq!(plaintext.as_slice(), decrypted.as_slice());
    }

    #[test]
    fn test_kyber_roundtrip() {
        let (pk, sk) = generate_keypair();
        let (ct, ss1) = encapsulate(&pk).unwrap();
        let ss2 = decapsulate(&ct, &sk).unwrap();

        assert_eq!(ss1, ss2);
    }

    #[test]
    fn test_dilithium_signature() {
        let (pk, sk) = generate_signing_keypair();
        let message = b"important document";

        let signature = sign(message, &sk).unwrap();
        let valid = verify(message, &signature, &pk).unwrap();

        assert!(valid);
    }
}
