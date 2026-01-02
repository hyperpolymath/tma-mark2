// SPDX-License-Identifier: AGPL-3.0-or-later
//! Application state management

use serde::{Deserialize, Serialize};

/// User authentication state
#[derive(Clone, Debug, Default)]
pub enum AuthState {
    #[default]
    Unauthenticated,
    Loading,
    Authenticated { user: User },
}

/// Sync status for offline-first functionality
#[derive(Clone, Debug, Default)]
pub enum SyncStatus {
    #[default]
    Idle,
    Syncing,
    Error(String),
    Offline,
}

/// User information
#[derive(Clone, Debug, Serialize, Deserialize)]
pub struct User {
    pub id: String,
    pub name: String,
    pub email: String,
    pub role: UserRole,
}

/// User roles
#[derive(Clone, Debug, Serialize, Deserialize)]
pub enum UserRole {
    Tutor,
    SeniorTutor,
    StaffTutor,
    Admin,
}

/// Submission data
#[derive(Clone, Debug, Serialize, Deserialize)]
pub struct Submission {
    pub id: String,
    pub student_name: String,
    pub student_oucu: String,
    pub course_code: String,
    pub presentation: String,
    pub tma_number: String,
    pub status: SubmissionStatus,
    pub marks: Option<Marks>,
    pub last_modified: String,
}

/// Submission status
#[derive(Clone, Debug, Serialize, Deserialize)]
pub enum SubmissionStatus {
    Pending,
    InProgress,
    Completed,
    Returned,
}

/// Marks for a submission
#[derive(Clone, Debug, Serialize, Deserialize)]
pub struct Marks {
    pub questions: Vec<QuestionMark>,
    pub total: f64,
    pub percentage: f64,
}

/// Mark for a single question
#[derive(Clone, Debug, Serialize, Deserialize)]
pub struct QuestionMark {
    pub question_number: u32,
    pub score: f64,
    pub max_score: f64,
    pub feedback: String,
}

/// Course information
#[derive(Clone, Debug, Serialize, Deserialize)]
pub struct Course {
    pub code: String,
    pub title: String,
    pub presentation: String,
    pub submission_count: u32,
    pub pending_count: u32,
}
