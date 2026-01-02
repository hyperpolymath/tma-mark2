// SPDX-License-Identifier: AGPL-3.0-or-later
//! tma_crypto - Post-Quantum Cryptography NIF for tma-mark2
//!
//! Provides cryptographic primitives:
//! - BLAKE3: Fast cryptographic hash and KDF
//! - SHAKE-256: Extensible Output Function (XOF)
//! - Ed448: Edwards curve digital signatures (Curve448)
//! - Kyber-1024: Post-quantum Key Encapsulation (ML-KEM Level 5)
//! - Dilithium5: Post-quantum Digital Signatures (ML-DSA Level 5)
//! - ChaCha20-Poly1305: AEAD symmetric encryption

use rustler::{Binary, Encoder, Env, NifResult, OwnedBinary, Term};
use zeroize::Zeroize;

mod atoms {
    rustler::atoms! {
        ok,
        error,
        invalid_input,
        invalid_key_size,
        invalid_nonce_size,
        verification_failed,
        decryption_failed,
        encapsulation_failed,
        key_generation_failed,
    }
}

// ============================================================
// BLAKE3 HASHING
// ============================================================

/// Hash data using BLAKE3 (256-bit output)
#[rustler::nif]
fn blake3_hash<'a>(env: Env<'a>, data: Binary) -> Term<'a> {
    let hash = blake3::hash(data.as_slice());
    let mut output = OwnedBinary::new(32).unwrap();
    output.as_mut_slice().copy_from_slice(hash.as_bytes());
    (atoms::ok(), output.release(env)).encode(env)
}

/// Hash data using BLAKE3 with custom output length (XOF mode)
#[rustler::nif]
fn blake3_hash_xof<'a>(env: Env<'a>, data: Binary, output_len: usize) -> Term<'a> {
    if output_len == 0 || output_len > 65535 {
        return (atoms::error(), atoms::invalid_input()).encode(env);
    }

    let mut hasher = blake3::Hasher::new();
    hasher.update(data.as_slice());
    let mut output = OwnedBinary::new(output_len).unwrap();
    hasher.finalize_xof().fill(output.as_mut_slice());
    (atoms::ok(), output.release(env)).encode(env)
}

/// Derive a key using BLAKE3-KDF
#[rustler::nif]
fn blake3_derive_key<'a>(env: Env<'a>, context: Binary, ikm: Binary, output_len: usize) -> Term<'a> {
    let context_str = match std::str::from_utf8(context.as_slice()) {
        Ok(s) => s,
        Err(_) => return (atoms::error(), atoms::invalid_input()).encode(env),
    };

    if output_len == 0 || output_len > 65535 {
        return (atoms::error(), atoms::invalid_input()).encode(env);
    }

    let mut output = OwnedBinary::new(output_len).unwrap();

    // Use BLAKE3's derive_key mode
    let mut hasher = blake3::Hasher::new_derive_key(context_str);
    hasher.update(ikm.as_slice());
    hasher.finalize_xof().fill(output.as_mut_slice());

    (atoms::ok(), output.release(env)).encode(env)
}

/// Keyed BLAKE3 MAC
#[rustler::nif]
fn blake3_keyed_hash<'a>(env: Env<'a>, key: Binary, data: Binary) -> Term<'a> {
    if key.len() != 32 {
        return (atoms::error(), atoms::invalid_key_size()).encode(env);
    }

    let key_array: [u8; 32] = key.as_slice().try_into().unwrap();
    let mut hasher = blake3::Hasher::new_keyed(&key_array);
    hasher.update(data.as_slice());
    let hash = hasher.finalize();

    let mut output = OwnedBinary::new(32).unwrap();
    output.as_mut_slice().copy_from_slice(hash.as_bytes());
    (atoms::ok(), output.release(env)).encode(env)
}

// ============================================================
// SHAKE-256 (XOF)
// ============================================================

/// Hash data using SHAKE-256 with custom output length
#[rustler::nif]
fn shake256<'a>(env: Env<'a>, data: Binary, output_len: usize) -> Term<'a> {
    use sha3::{Shake256, digest::{Update, ExtendableOutput, XofReader}};

    if output_len == 0 || output_len > 65535 {
        return (atoms::error(), atoms::invalid_input()).encode(env);
    }

    let mut hasher = Shake256::default();
    hasher.update(data.as_slice());
    let mut reader = hasher.finalize_xof();
    let mut output = OwnedBinary::new(output_len).unwrap();
    reader.read(output.as_mut_slice());
    (atoms::ok(), output.release(env)).encode(env)
}

// ============================================================
// CHACHA20-POLY1305 AEAD
// ============================================================

/// Encrypt data with ChaCha20-Poly1305
/// Returns ciphertext with appended 16-byte authentication tag
#[rustler::nif]
fn chacha20_poly1305_encrypt<'a>(
    env: Env<'a>,
    key: Binary,
    nonce: Binary,
    plaintext: Binary,
    aad: Binary,
) -> Term<'a> {
    use chacha20poly1305::{
        aead::{Aead, KeyInit, Payload},
        ChaCha20Poly1305, Nonce,
    };

    if key.len() != 32 {
        return (atoms::error(), atoms::invalid_key_size()).encode(env);
    }
    if nonce.len() != 12 {
        return (atoms::error(), atoms::invalid_nonce_size()).encode(env);
    }

    let cipher = match ChaCha20Poly1305::new_from_slice(key.as_slice()) {
        Ok(c) => c,
        Err(_) => return (atoms::error(), atoms::invalid_key_size()).encode(env),
    };

    let nonce = Nonce::from_slice(nonce.as_slice());
    let payload = Payload {
        msg: plaintext.as_slice(),
        aad: aad.as_slice(),
    };

    match cipher.encrypt(nonce, payload) {
        Ok(ciphertext) => {
            let mut output = OwnedBinary::new(ciphertext.len()).unwrap();
            output.as_mut_slice().copy_from_slice(&ciphertext);
            (atoms::ok(), output.release(env)).encode(env)
        }
        Err(_) => (atoms::error(), atoms::invalid_input()).encode(env),
    }
}

/// Decrypt data with ChaCha20-Poly1305
/// Input should be ciphertext with appended 16-byte authentication tag
#[rustler::nif]
fn chacha20_poly1305_decrypt<'a>(
    env: Env<'a>,
    key: Binary,
    nonce: Binary,
    ciphertext: Binary,
    aad: Binary,
) -> Term<'a> {
    use chacha20poly1305::{
        aead::{Aead, KeyInit, Payload},
        ChaCha20Poly1305, Nonce,
    };

    if key.len() != 32 {
        return (atoms::error(), atoms::invalid_key_size()).encode(env);
    }
    if nonce.len() != 12 {
        return (atoms::error(), atoms::invalid_nonce_size()).encode(env);
    }
    if ciphertext.len() < 16 {
        return (atoms::error(), atoms::invalid_input()).encode(env);
    }

    let cipher = match ChaCha20Poly1305::new_from_slice(key.as_slice()) {
        Ok(c) => c,
        Err(_) => return (atoms::error(), atoms::invalid_key_size()).encode(env),
    };

    let nonce = Nonce::from_slice(nonce.as_slice());
    let payload = Payload {
        msg: ciphertext.as_slice(),
        aad: aad.as_slice(),
    };

    match cipher.decrypt(nonce, payload) {
        Ok(plaintext) => {
            let mut output = OwnedBinary::new(plaintext.len()).unwrap();
            output.as_mut_slice().copy_from_slice(&plaintext);
            (atoms::ok(), output.release(env)).encode(env)
        }
        Err(_) => (atoms::error(), atoms::decryption_failed()).encode(env),
    }
}

// ============================================================
// XCHACHA20-POLY1305 AEAD (Extended Nonce)
// ============================================================

/// Encrypt data with XChaCha20-Poly1305 (24-byte nonce)
#[rustler::nif]
fn xchacha20_poly1305_encrypt<'a>(
    env: Env<'a>,
    key: Binary,
    nonce: Binary,
    plaintext: Binary,
    aad: Binary,
) -> Term<'a> {
    use chacha20poly1305::{
        aead::{Aead, KeyInit, Payload},
        XChaCha20Poly1305, XNonce,
    };

    if key.len() != 32 {
        return (atoms::error(), atoms::invalid_key_size()).encode(env);
    }
    if nonce.len() != 24 {
        return (atoms::error(), atoms::invalid_nonce_size()).encode(env);
    }

    let cipher = match XChaCha20Poly1305::new_from_slice(key.as_slice()) {
        Ok(c) => c,
        Err(_) => return (atoms::error(), atoms::invalid_key_size()).encode(env),
    };

    let nonce = XNonce::from_slice(nonce.as_slice());
    let payload = Payload {
        msg: plaintext.as_slice(),
        aad: aad.as_slice(),
    };

    match cipher.encrypt(nonce, payload) {
        Ok(ciphertext) => {
            let mut output = OwnedBinary::new(ciphertext.len()).unwrap();
            output.as_mut_slice().copy_from_slice(&ciphertext);
            (atoms::ok(), output.release(env)).encode(env)
        }
        Err(_) => (atoms::error(), atoms::invalid_input()).encode(env),
    }
}

/// Decrypt data with XChaCha20-Poly1305 (24-byte nonce)
#[rustler::nif]
fn xchacha20_poly1305_decrypt<'a>(
    env: Env<'a>,
    key: Binary,
    nonce: Binary,
    ciphertext: Binary,
    aad: Binary,
) -> Term<'a> {
    use chacha20poly1305::{
        aead::{Aead, KeyInit, Payload},
        XChaCha20Poly1305, XNonce,
    };

    if key.len() != 32 {
        return (atoms::error(), atoms::invalid_key_size()).encode(env);
    }
    if nonce.len() != 24 {
        return (atoms::error(), atoms::invalid_nonce_size()).encode(env);
    }
    if ciphertext.len() < 16 {
        return (atoms::error(), atoms::invalid_input()).encode(env);
    }

    let cipher = match XChaCha20Poly1305::new_from_slice(key.as_slice()) {
        Ok(c) => c,
        Err(_) => return (atoms::error(), atoms::invalid_key_size()).encode(env),
    };

    let nonce = XNonce::from_slice(nonce.as_slice());
    let payload = Payload {
        msg: ciphertext.as_slice(),
        aad: aad.as_slice(),
    };

    match cipher.decrypt(nonce, payload) {
        Ok(plaintext) => {
            let mut output = OwnedBinary::new(plaintext.len()).unwrap();
            output.as_mut_slice().copy_from_slice(&plaintext);
            (atoms::ok(), output.release(env)).encode(env)
        }
        Err(_) => (atoms::error(), atoms::decryption_failed()).encode(env),
    }
}

// ============================================================
// KYBER-1024 (ML-KEM) POST-QUANTUM KEY ENCAPSULATION
// ============================================================

/// Generate a Kyber-1024 keypair
/// Returns {ok, {public_key, secret_key}} or {error, reason}
#[rustler::nif]
fn kyber1024_keypair<'a>(env: Env<'a>) -> Term<'a> {
    use pqcrypto_kyber::kyber1024;
    use pqcrypto_traits::kem::{PublicKey, SecretKey};

    let (pk, sk) = kyber1024::keypair();

    let pk_bytes = pk.as_bytes();
    let sk_bytes = sk.as_bytes();

    let mut pk_out = OwnedBinary::new(pk_bytes.len()).unwrap();
    let mut sk_out = OwnedBinary::new(sk_bytes.len()).unwrap();

    pk_out.as_mut_slice().copy_from_slice(pk_bytes);
    sk_out.as_mut_slice().copy_from_slice(sk_bytes);

    (atoms::ok(), (pk_out.release(env), sk_out.release(env))).encode(env)
}

/// Encapsulate a shared secret using Kyber-1024 public key
/// Returns {ok, {ciphertext, shared_secret}} or {error, reason}
#[rustler::nif]
fn kyber1024_encapsulate<'a>(env: Env<'a>, public_key: Binary) -> Term<'a> {
    use pqcrypto_kyber::kyber1024;
    use pqcrypto_traits::kem::{PublicKey, SharedSecret, Ciphertext};

    let pk = match kyber1024::PublicKey::from_bytes(public_key.as_slice()) {
        Ok(pk) => pk,
        Err(_) => return (atoms::error(), atoms::invalid_input()).encode(env),
    };

    let (ss, ct) = kyber1024::encapsulate(&pk);

    let ct_bytes = ct.as_bytes();
    let ss_bytes = ss.as_bytes();

    let mut ct_out = OwnedBinary::new(ct_bytes.len()).unwrap();
    let mut ss_out = OwnedBinary::new(ss_bytes.len()).unwrap();

    ct_out.as_mut_slice().copy_from_slice(ct_bytes);
    ss_out.as_mut_slice().copy_from_slice(ss_bytes);

    (atoms::ok(), (ct_out.release(env), ss_out.release(env))).encode(env)
}

/// Decapsulate a shared secret using Kyber-1024 secret key
/// Returns {ok, shared_secret} or {error, reason}
#[rustler::nif]
fn kyber1024_decapsulate<'a>(env: Env<'a>, ciphertext: Binary, secret_key: Binary) -> Term<'a> {
    use pqcrypto_kyber::kyber1024;
    use pqcrypto_traits::kem::{SecretKey, SharedSecret, Ciphertext};

    let ct = match kyber1024::Ciphertext::from_bytes(ciphertext.as_slice()) {
        Ok(ct) => ct,
        Err(_) => return (atoms::error(), atoms::invalid_input()).encode(env),
    };

    let sk = match kyber1024::SecretKey::from_bytes(secret_key.as_slice()) {
        Ok(sk) => sk,
        Err(_) => return (atoms::error(), atoms::invalid_input()).encode(env),
    };

    let ss = kyber1024::decapsulate(&ct, &sk);
    let ss_bytes = ss.as_bytes();

    let mut ss_out = OwnedBinary::new(ss_bytes.len()).unwrap();
    ss_out.as_mut_slice().copy_from_slice(ss_bytes);

    (atoms::ok(), ss_out.release(env)).encode(env)
}

// ============================================================
// DILITHIUM5 (ML-DSA) POST-QUANTUM SIGNATURES
// ============================================================

/// Generate a Dilithium5 keypair
/// Returns {ok, {public_key, secret_key}} or {error, reason}
#[rustler::nif]
fn dilithium5_keypair<'a>(env: Env<'a>) -> Term<'a> {
    use pqcrypto_dilithium::dilithium5;
    use pqcrypto_traits::sign::{PublicKey, SecretKey};

    let (pk, sk) = dilithium5::keypair();

    let pk_bytes = pk.as_bytes();
    let sk_bytes = sk.as_bytes();

    let mut pk_out = OwnedBinary::new(pk_bytes.len()).unwrap();
    let mut sk_out = OwnedBinary::new(sk_bytes.len()).unwrap();

    pk_out.as_mut_slice().copy_from_slice(pk_bytes);
    sk_out.as_mut_slice().copy_from_slice(sk_bytes);

    (atoms::ok(), (pk_out.release(env), sk_out.release(env))).encode(env)
}

/// Sign a message using Dilithium5 secret key
/// Returns {ok, signature} or {error, reason}
#[rustler::nif]
fn dilithium5_sign<'a>(env: Env<'a>, message: Binary, secret_key: Binary) -> Term<'a> {
    use pqcrypto_dilithium::dilithium5;
    use pqcrypto_traits::sign::{SecretKey, SignedMessage};

    let sk = match dilithium5::SecretKey::from_bytes(secret_key.as_slice()) {
        Ok(sk) => sk,
        Err(_) => return (atoms::error(), atoms::invalid_input()).encode(env),
    };

    let signed = dilithium5::sign(message.as_slice(), &sk);
    let sig_bytes = signed.as_bytes();

    // Extract just the signature (signed message = signature + message)
    let sig_len = sig_bytes.len() - message.len();
    let mut sig_out = OwnedBinary::new(sig_len).unwrap();
    sig_out.as_mut_slice().copy_from_slice(&sig_bytes[..sig_len]);

    (atoms::ok(), sig_out.release(env)).encode(env)
}

/// Verify a Dilithium5 signature
/// Returns {ok, true} or {error, verification_failed}
#[rustler::nif]
fn dilithium5_verify<'a>(
    env: Env<'a>,
    message: Binary,
    signature: Binary,
    public_key: Binary,
) -> Term<'a> {
    use pqcrypto_dilithium::dilithium5;
    use pqcrypto_traits::sign::{PublicKey, SignedMessage};

    let pk = match dilithium5::PublicKey::from_bytes(public_key.as_slice()) {
        Ok(pk) => pk,
        Err(_) => return (atoms::error(), atoms::invalid_input()).encode(env),
    };

    // Reconstruct signed message (signature + message)
    let mut signed_msg = Vec::with_capacity(signature.len() + message.len());
    signed_msg.extend_from_slice(signature.as_slice());
    signed_msg.extend_from_slice(message.as_slice());

    let signed = match dilithium5::SignedMessage::from_bytes(&signed_msg) {
        Ok(s) => s,
        Err(_) => return (atoms::error(), atoms::invalid_input()).encode(env),
    };

    match dilithium5::open(&signed, &pk) {
        Ok(_) => (atoms::ok(), true).encode(env),
        Err(_) => (atoms::error(), atoms::verification_failed()).encode(env),
    }
}

// ============================================================
// RANDOM NUMBER GENERATION
// ============================================================

/// Generate cryptographically secure random bytes
#[rustler::nif]
fn random_bytes<'a>(env: Env<'a>, len: usize) -> Term<'a> {
    use rand::RngCore;

    if len == 0 || len > 65535 {
        return (atoms::error(), atoms::invalid_input()).encode(env);
    }

    let mut output = OwnedBinary::new(len).unwrap();
    rand::thread_rng().fill_bytes(output.as_mut_slice());
    (atoms::ok(), output.release(env)).encode(env)
}

/// Generate a random 12-byte nonce for ChaCha20-Poly1305
#[rustler::nif]
fn generate_nonce<'a>(env: Env<'a>) -> Term<'a> {
    use rand::RngCore;

    let mut output = OwnedBinary::new(12).unwrap();
    rand::thread_rng().fill_bytes(output.as_mut_slice());
    (atoms::ok(), output.release(env)).encode(env)
}

/// Generate a random 24-byte nonce for XChaCha20-Poly1305
#[rustler::nif]
fn generate_xnonce<'a>(env: Env<'a>) -> Term<'a> {
    use rand::RngCore;

    let mut output = OwnedBinary::new(24).unwrap();
    rand::thread_rng().fill_bytes(output.as_mut_slice());
    (atoms::ok(), output.release(env)).encode(env)
}

// ============================================================
// CONSTANT-TIME COMPARISON
// ============================================================

/// Constant-time comparison of two byte arrays
#[rustler::nif]
fn secure_compare(a: Binary, b: Binary) -> bool {
    use subtle::ConstantTimeEq;

    if a.len() != b.len() {
        return false;
    }

    a.as_slice().ct_eq(b.as_slice()).into()
}

// ============================================================
// NIF INITIALIZATION
// ============================================================

rustler::init!(
    "Elixir.EtmaHandler.Crypto.Native",
    [
        // BLAKE3
        blake3_hash,
        blake3_hash_xof,
        blake3_derive_key,
        blake3_keyed_hash,
        // SHAKE-256
        shake256,
        // ChaCha20-Poly1305
        chacha20_poly1305_encrypt,
        chacha20_poly1305_decrypt,
        xchacha20_poly1305_encrypt,
        xchacha20_poly1305_decrypt,
        // Kyber-1024
        kyber1024_keypair,
        kyber1024_encapsulate,
        kyber1024_decapsulate,
        // Dilithium5
        dilithium5_keypair,
        dilithium5_sign,
        dilithium5_verify,
        // Random
        random_bytes,
        generate_nonce,
        generate_xnonce,
        // Utils
        secure_compare,
    ]
);
