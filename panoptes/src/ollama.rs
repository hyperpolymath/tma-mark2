// SPDX-FileCopyrightText: 2024 Panoptes Contributors
// SPDX-License-Identifier: MIT

//! Ollama API client for local AI model inference

use reqwest::Client;
use serde::{Deserialize, Serialize};
use std::time::Duration;

use crate::error::{Error, Result};

/// Ollama API client
pub struct OllamaClient {
    client: Client,
    base_url: String,
}

impl OllamaClient {
    /// Create a new Ollama client
    pub fn new(base_url: &str) -> Self {
        let client = Client::builder()
            .timeout(Duration::from_secs(300))
            .build()
            .expect("Failed to create HTTP client");

        Self {
            client,
            base_url: base_url.trim_end_matches('/').to_string(),
        }
    }

    /// Check if Ollama is available
    pub async fn is_available(&self) -> bool {
        self.client
            .get(format!("{}/api/version", self.base_url))
            .send()
            .await
            .map(|r| r.status().is_success())
            .unwrap_or(false)
    }

    /// Get Ollama version
    pub async fn version(&self) -> Result<String> {
        let response = self
            .client
            .get(format!("{}/api/version", self.base_url))
            .send()
            .await
            .map_err(|e| Error::OllamaUnavailable {
                url: self.base_url.clone(),
            })?;

        if !response.status().is_success() {
            return Err(Error::ollama_with_status(
                "Failed to get version",
                response.status().as_u16(),
            ));
        }

        let version: VersionResponse = response.json().await?;
        Ok(version.version)
    }

    /// List available models
    pub async fn list_models(&self) -> Result<Vec<ModelInfo>> {
        let response = self
            .client
            .get(format!("{}/api/tags", self.base_url))
            .send()
            .await?;

        if !response.status().is_success() {
            return Err(Error::ollama_with_status(
                "Failed to list models",
                response.status().as_u16(),
            ));
        }

        let tags: TagsResponse = response.json().await?;
        Ok(tags.models)
    }

    /// Check if a model is available
    pub async fn has_model(&self, model: &str) -> Result<bool> {
        let models = self.list_models().await?;
        Ok(models.iter().any(|m| m.name == model || m.name.starts_with(&format!("{}:", model))))
    }

    /// Pull a model
    pub async fn pull_model(&self, model: &str) -> Result<()> {
        let request = PullRequest {
            name: model.to_string(),
            insecure: false,
            stream: false,
        };

        let response = self
            .client
            .post(format!("{}/api/pull", self.base_url))
            .json(&request)
            .send()
            .await?;

        if !response.status().is_success() {
            return Err(Error::ollama_with_status(
                format!("Failed to pull model '{}'", model),
                response.status().as_u16(),
            ));
        }

        Ok(())
    }

    /// Generate text completion
    pub async fn generate(&self, model: &str, prompt: &str) -> Result<String> {
        let request = GenerateRequest {
            model: model.to_string(),
            prompt: prompt.to_string(),
            stream: false,
            images: None,
            options: None,
        };

        let response = self
            .client
            .post(format!("{}/api/generate", self.base_url))
            .json(&request)
            .send()
            .await?;

        if !response.status().is_success() {
            let status = response.status().as_u16();
            let text = response.text().await.unwrap_or_default();
            return Err(Error::ollama_with_status(
                format!("Generation failed: {}", text),
                status,
            ));
        }

        let result: GenerateResponse = response.json().await?;
        Ok(result.response)
    }

    /// Generate text completion with an image
    pub async fn generate_with_image(
        &self,
        model: &str,
        prompt: &str,
        base64_image: &str,
    ) -> Result<String> {
        let request = GenerateRequest {
            model: model.to_string(),
            prompt: prompt.to_string(),
            stream: false,
            images: Some(vec![base64_image.to_string()]),
            options: None,
        };

        let response = self
            .client
            .post(format!("{}/api/generate", self.base_url))
            .json(&request)
            .send()
            .await?;

        if !response.status().is_success() {
            let status = response.status().as_u16();
            let text = response.text().await.unwrap_or_default();
            return Err(Error::ollama_with_status(
                format!("Generation failed: {}", text),
                status,
            ));
        }

        let result: GenerateResponse = response.json().await?;
        Ok(result.response)
    }

    /// Generate with custom options
    pub async fn generate_with_options(
        &self,
        model: &str,
        prompt: &str,
        options: GenerateOptions,
    ) -> Result<String> {
        let request = GenerateRequest {
            model: model.to_string(),
            prompt: prompt.to_string(),
            stream: false,
            images: None,
            options: Some(options),
        };

        let response = self
            .client
            .post(format!("{}/api/generate", self.base_url))
            .json(&request)
            .send()
            .await?;

        if !response.status().is_success() {
            let status = response.status().as_u16();
            let text = response.text().await.unwrap_or_default();
            return Err(Error::ollama_with_status(
                format!("Generation failed: {}", text),
                status,
            ));
        }

        let result: GenerateResponse = response.json().await?;
        Ok(result.response)
    }

    /// Get model information
    pub async fn model_info(&self, model: &str) -> Result<ModelDetails> {
        let request = ShowRequest {
            name: model.to_string(),
        };

        let response = self
            .client
            .post(format!("{}/api/show", self.base_url))
            .json(&request)
            .send()
            .await?;

        if !response.status().is_success() {
            let status = response.status().as_u16();
            if status == 404 {
                let models = self.list_models().await?;
                return Err(Error::ModelNotFound {
                    model: model.to_string(),
                    available: models.into_iter().map(|m| m.name).collect(),
                });
            }
            return Err(Error::ollama_with_status(
                format!("Failed to get model info for '{}'", model),
                status,
            ));
        }

        response.json().await.map_err(Into::into)
    }
}

// API Types

#[derive(Debug, Deserialize)]
struct VersionResponse {
    version: String,
}

#[derive(Debug, Deserialize)]
struct TagsResponse {
    models: Vec<ModelInfo>,
}

/// Information about an available model
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct ModelInfo {
    /// Model name
    pub name: String,
    /// Model size
    #[serde(default)]
    pub size: u64,
    /// Model digest
    #[serde(default)]
    pub digest: String,
    /// Modified timestamp
    #[serde(default)]
    pub modified_at: String,
}

#[derive(Debug, Serialize)]
struct PullRequest {
    name: String,
    insecure: bool,
    stream: bool,
}

#[derive(Debug, Serialize)]
struct GenerateRequest {
    model: String,
    prompt: String,
    stream: bool,
    #[serde(skip_serializing_if = "Option::is_none")]
    images: Option<Vec<String>>,
    #[serde(skip_serializing_if = "Option::is_none")]
    options: Option<GenerateOptions>,
}

/// Options for text generation
#[derive(Debug, Clone, Serialize, Deserialize, Default)]
pub struct GenerateOptions {
    /// Temperature (0.0-2.0)
    #[serde(skip_serializing_if = "Option::is_none")]
    pub temperature: Option<f32>,
    /// Top-p sampling
    #[serde(skip_serializing_if = "Option::is_none")]
    pub top_p: Option<f32>,
    /// Top-k sampling
    #[serde(skip_serializing_if = "Option::is_none")]
    pub top_k: Option<u32>,
    /// Number of tokens to generate
    #[serde(skip_serializing_if = "Option::is_none")]
    pub num_predict: Option<u32>,
    /// Stop sequences
    #[serde(skip_serializing_if = "Option::is_none")]
    pub stop: Option<Vec<String>>,
    /// Repeat penalty
    #[serde(skip_serializing_if = "Option::is_none")]
    pub repeat_penalty: Option<f32>,
    /// Seed for reproducibility
    #[serde(skip_serializing_if = "Option::is_none")]
    pub seed: Option<u64>,
}

#[derive(Debug, Deserialize)]
struct GenerateResponse {
    response: String,
    #[serde(default)]
    done: bool,
    #[serde(default)]
    total_duration: u64,
    #[serde(default)]
    eval_count: u32,
}

#[derive(Debug, Serialize)]
struct ShowRequest {
    name: String,
}

/// Detailed model information
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct ModelDetails {
    /// Model license
    #[serde(default)]
    pub license: String,
    /// Model file path
    #[serde(default)]
    pub modelfile: String,
    /// Model parameters
    #[serde(default)]
    pub parameters: String,
    /// Model template
    #[serde(default)]
    pub template: String,
    /// Model details
    #[serde(default)]
    pub details: ModelDetailInfo,
}

/// Model detail information
#[derive(Debug, Clone, Serialize, Deserialize, Default)]
pub struct ModelDetailInfo {
    /// Parent model
    #[serde(default)]
    pub parent_model: String,
    /// Model format
    #[serde(default)]
    pub format: String,
    /// Model family
    #[serde(default)]
    pub family: String,
    /// Families
    #[serde(default)]
    pub families: Vec<String>,
    /// Parameter size
    #[serde(default)]
    pub parameter_size: String,
    /// Quantization level
    #[serde(default)]
    pub quantization_level: String,
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_client_creation() {
        let client = OllamaClient::new("http://localhost:11434");
        assert_eq!(client.base_url, "http://localhost:11434");

        let client = OllamaClient::new("http://localhost:11434/");
        assert_eq!(client.base_url, "http://localhost:11434");
    }

    #[test]
    fn test_generate_options_default() {
        let options = GenerateOptions::default();
        assert!(options.temperature.is_none());
        assert!(options.top_p.is_none());
    }
}
