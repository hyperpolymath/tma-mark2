// SPDX-License-Identifier: AGPL-3.0-or-later
//! API client for communicating with the eTMA Handler backend

use crate::crypto;
use crate::state::{Course, Submission, User};
use reqwest::Client;
use serde::{Deserialize, Serialize};
use thiserror::Error;

#[derive(Error, Debug)]
pub enum ApiError {
    #[error("Network error: {0}")]
    Network(#[from] reqwest::Error),
    #[error("Authentication failed")]
    AuthenticationFailed,
    #[error("Not found")]
    NotFound,
    #[error("Server error: {0}")]
    ServerError(String),
    #[error("Crypto error: {0}")]
    Crypto(#[from] crypto::CryptoError),
}

/// API client for the eTMA Handler backend
pub struct ApiClient {
    client: Client,
    base_url: String,
    auth_token: Option<String>,
    /// Kyber public key for key exchange
    server_public_key: Option<Vec<u8>>,
}

#[derive(Serialize)]
struct LoginRequest {
    username: String,
    password_hash: String,
}

#[derive(Deserialize)]
struct LoginResponse {
    token: String,
    user: User,
    server_public_key: String,
}

#[derive(Deserialize)]
struct SubmissionsResponse {
    submissions: Vec<Submission>,
}

#[derive(Deserialize)]
struct CoursesResponse {
    courses: Vec<Course>,
}

impl ApiClient {
    /// Create a new API client
    pub fn new(base_url: &str) -> Self {
        Self {
            client: Client::new(),
            base_url: base_url.to_string(),
            auth_token: None,
            server_public_key: None,
        }
    }

    /// Authenticate with the server
    pub async fn login(&mut self, username: &str, password: &str) -> Result<User, ApiError> {
        // Hash password client-side before sending
        let password_hash = hex::encode(crypto::hash(password.as_bytes()));

        let response: LoginResponse = self
            .client
            .post(format!("{}/api/auth/login", self.base_url))
            .json(&LoginRequest {
                username: username.to_string(),
                password_hash,
            })
            .send()
            .await?
            .json()
            .await?;

        self.auth_token = Some(response.token);
        self.server_public_key = Some(hex::decode(&response.server_public_key).unwrap_or_default());

        Ok(response.user)
    }

    /// Logout and clear credentials
    pub fn logout(&mut self) {
        self.auth_token = None;
        self.server_public_key = None;
    }

    /// Get list of submissions
    pub async fn get_submissions(&self) -> Result<Vec<Submission>, ApiError> {
        let token = self.auth_token.as_ref().ok_or(ApiError::AuthenticationFailed)?;

        let response: SubmissionsResponse = self
            .client
            .get(format!("{}/api/submissions", self.base_url))
            .bearer_auth(token)
            .send()
            .await?
            .json()
            .await?;

        Ok(response.submissions)
    }

    /// Get a single submission by ID
    pub async fn get_submission(&self, id: &str) -> Result<Submission, ApiError> {
        let token = self.auth_token.as_ref().ok_or(ApiError::AuthenticationFailed)?;

        let submission: Submission = self
            .client
            .get(format!("{}/api/submissions/{}", self.base_url, id))
            .bearer_auth(token)
            .send()
            .await?
            .json()
            .await?;

        Ok(submission)
    }

    /// Get list of courses
    pub async fn get_courses(&self) -> Result<Vec<Course>, ApiError> {
        let token = self.auth_token.as_ref().ok_or(ApiError::AuthenticationFailed)?;

        let response: CoursesResponse = self
            .client
            .get(format!("{}/api/courses", self.base_url))
            .bearer_auth(token)
            .send()
            .await?
            .json()
            .await?;

        Ok(response.courses)
    }

    /// Establish a post-quantum secure channel using Kyber
    pub async fn establish_secure_channel(&self) -> Result<Vec<u8>, ApiError> {
        let server_pk = self
            .server_public_key
            .as_ref()
            .ok_or(ApiError::AuthenticationFailed)?;

        // Encapsulate shared secret using server's Kyber public key
        let (ciphertext, shared_secret) = crypto::encapsulate(server_pk)?;

        let token = self.auth_token.as_ref().ok_or(ApiError::AuthenticationFailed)?;

        // Send ciphertext to server
        self.client
            .post(format!("{}/api/secure/establish", self.base_url))
            .bearer_auth(token)
            .json(&serde_json::json!({
                "ciphertext": hex::encode(&ciphertext)
            }))
            .send()
            .await?;

        Ok(shared_secret)
    }

    /// Check if authenticated
    pub fn is_authenticated(&self) -> bool {
        self.auth_token.is_some()
    }
}

impl Default for ApiClient {
    fn default() -> Self {
        Self::new("http://localhost:4000")
    }
}
