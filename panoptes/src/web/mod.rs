// SPDX-FileCopyrightText: 2024 Panoptes Contributors
// SPDX-License-Identifier: MIT

//! Web dashboard and API for Panoptes

use axum::{
    extract::{Path as AxumPath, Query, State},
    http::StatusCode,
    response::{Html, IntoResponse, Json},
    routing::{get, post},
    Router,
};
use serde::{Deserialize, Serialize};
use std::path::PathBuf;
use std::sync::Arc;
use tokio::sync::RwLock;
use tower_http::cors::{Any, CorsLayer};
use tower_http::services::ServeDir;

use crate::config::WebConfig;
use crate::db::{Database, DatabaseStats, FileRecord, Tag};
use crate::error::Result;

/// Shared application state
pub struct AppState {
    pub db: Arc<RwLock<Database>>,
    pub config: WebConfig,
}

/// Start the web server
pub async fn serve(state: Arc<AppState>) -> Result<()> {
    let addr = format!("{}:{}", state.config.address, state.config.port);

    let app = create_router(state);

    let listener = tokio::net::TcpListener::bind(&addr).await?;
    tracing::info!("Web server listening on http://{}", addr);

    axum::serve(listener, app).await?;

    Ok(())
}

/// Create the router
pub fn create_router(state: Arc<AppState>) -> Router {
    let api_routes = Router::new()
        .route("/files", get(list_files))
        .route("/files/:id", get(get_file))
        .route("/files/search", get(search_files))
        .route("/tags", get(list_tags))
        .route("/tags", post(create_tag))
        .route("/tags/:name/files", get(files_by_tag))
        .route("/stats", get(get_stats))
        .route("/health", get(health_check));

    Router::new()
        .route("/", get(index))
        .nest("/api", api_routes)
        .layer(CorsLayer::new().allow_origin(Any).allow_methods(Any))
        .with_state(state)
}

// HTML Routes

async fn index() -> Html<&'static str> {
    Html(include_str!("index.html"))
}

// API Routes

#[derive(Debug, Deserialize)]
struct ListQuery {
    limit: Option<u32>,
    offset: Option<u32>,
}

async fn list_files(
    State(state): State<Arc<AppState>>,
    Query(query): Query<ListQuery>,
) -> impl IntoResponse {
    let db = state.db.read().await;
    match db.list_files(query.limit, query.offset) {
        Ok(files) => Json(ApiResponse::success(files)).into_response(),
        Err(e) => (
            StatusCode::INTERNAL_SERVER_ERROR,
            Json(ApiResponse::<()>::error(&e.to_string())),
        )
            .into_response(),
    }
}

async fn get_file(
    State(state): State<Arc<AppState>>,
    AxumPath(id): AxumPath<String>,
) -> impl IntoResponse {
    let db = state.db.read().await;
    match db.get_file(&id) {
        Ok(Some(file)) => Json(ApiResponse::success(file)).into_response(),
        Ok(None) => (
            StatusCode::NOT_FOUND,
            Json(ApiResponse::<()>::error("File not found")),
        )
            .into_response(),
        Err(e) => (
            StatusCode::INTERNAL_SERVER_ERROR,
            Json(ApiResponse::<()>::error(&e.to_string())),
        )
            .into_response(),
    }
}

#[derive(Debug, Deserialize)]
struct SearchQuery {
    q: String,
}

async fn search_files(
    State(state): State<Arc<AppState>>,
    Query(query): Query<SearchQuery>,
) -> impl IntoResponse {
    let db = state.db.read().await;
    match db.search_files(&query.q) {
        Ok(files) => Json(ApiResponse::success(files)).into_response(),
        Err(e) => (
            StatusCode::INTERNAL_SERVER_ERROR,
            Json(ApiResponse::<()>::error(&e.to_string())),
        )
            .into_response(),
    }
}

async fn list_tags(State(state): State<Arc<AppState>>) -> impl IntoResponse {
    let db = state.db.read().await;
    match db.list_tags() {
        Ok(tags) => Json(ApiResponse::success(tags)).into_response(),
        Err(e) => (
            StatusCode::INTERNAL_SERVER_ERROR,
            Json(ApiResponse::<()>::error(&e.to_string())),
        )
            .into_response(),
    }
}

#[derive(Debug, Deserialize)]
struct CreateTagRequest {
    name: String,
    color: Option<String>,
}

async fn create_tag(
    State(state): State<Arc<AppState>>,
    Json(request): Json<CreateTagRequest>,
) -> impl IntoResponse {
    let db = state.db.read().await;
    match db.create_tag(&request.name, request.color.as_deref()) {
        Ok(tag) => (StatusCode::CREATED, Json(ApiResponse::success(tag))).into_response(),
        Err(e) => (
            StatusCode::BAD_REQUEST,
            Json(ApiResponse::<()>::error(&e.to_string())),
        )
            .into_response(),
    }
}

async fn files_by_tag(
    State(state): State<Arc<AppState>>,
    AxumPath(name): AxumPath<String>,
) -> impl IntoResponse {
    let db = state.db.read().await;
    match db.get_files_by_tag(&name) {
        Ok(files) => Json(ApiResponse::success(files)).into_response(),
        Err(e) => (
            StatusCode::INTERNAL_SERVER_ERROR,
            Json(ApiResponse::<()>::error(&e.to_string())),
        )
            .into_response(),
    }
}

async fn get_stats(State(state): State<Arc<AppState>>) -> impl IntoResponse {
    let db = state.db.read().await;
    match db.stats() {
        Ok(stats) => Json(ApiResponse::success(stats)).into_response(),
        Err(e) => (
            StatusCode::INTERNAL_SERVER_ERROR,
            Json(ApiResponse::<()>::error(&e.to_string())),
        )
            .into_response(),
    }
}

async fn health_check() -> impl IntoResponse {
    Json(ApiResponse::success(HealthStatus {
        status: "ok".to_string(),
        version: env!("CARGO_PKG_VERSION").to_string(),
    }))
}

// Response types

#[derive(Debug, Serialize)]
struct ApiResponse<T> {
    success: bool,
    #[serde(skip_serializing_if = "Option::is_none")]
    data: Option<T>,
    #[serde(skip_serializing_if = "Option::is_none")]
    error: Option<String>,
}

impl<T> ApiResponse<T> {
    fn success(data: T) -> Self {
        Self {
            success: true,
            data: Some(data),
            error: None,
        }
    }

    fn error(message: &str) -> Self {
        Self {
            success: false,
            data: None,
            error: Some(message.to_string()),
        }
    }
}

#[derive(Debug, Serialize)]
struct HealthStatus {
    status: String,
    version: String,
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_api_response_success() {
        let response = ApiResponse::success("test");
        assert!(response.success);
        assert_eq!(response.data, Some("test"));
        assert!(response.error.is_none());
    }

    #[test]
    fn test_api_response_error() {
        let response: ApiResponse<()> = ApiResponse::error("something went wrong");
        assert!(!response.success);
        assert!(response.data.is_none());
        assert_eq!(response.error, Some("something went wrong".to_string()));
    }
}
