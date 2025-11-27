// SPDX-FileCopyrightText: 2024 Panoptes Contributors
// SPDX-License-Identifier: MIT

//! History management for undo/redo operations
//!
//! All file operations are logged to a JSONL file for reversibility.

use serde::{Deserialize, Serialize};
use std::fs::{File, OpenOptions};
use std::io::{BufRead, BufReader, Write};
use std::path::{Path, PathBuf};
use uuid::Uuid;

use crate::error::{Error, Result};

/// History manager for tracking file operations
pub struct History {
    path: PathBuf,
}

impl History {
    /// Open or create a history file
    pub fn open(path: &Path) -> Result<Self> {
        // Ensure parent directory exists
        if let Some(parent) = path.parent() {
            std::fs::create_dir_all(parent)?;
        }

        // Create file if it doesn't exist
        if !path.exists() {
            File::create(path)?;
        }

        Ok(Self {
            path: path.to_path_buf(),
        })
    }

    /// Record an operation
    pub fn record(&self, entry: HistoryEntry) -> Result<()> {
        let mut file = OpenOptions::new()
            .create(true)
            .append(true)
            .open(&self.path)?;

        let json = serde_json::to_string(&entry)?;
        writeln!(file, "{}", json)?;

        Ok(())
    }

    /// Record a rename operation
    pub fn record_rename(&self, from: &Path, to: &Path) -> Result<String> {
        let entry = HistoryEntry::new(Operation::Rename {
            from: from.to_path_buf(),
            to: to.to_path_buf(),
        });
        let id = entry.id.clone();
        self.record(entry)?;
        Ok(id)
    }

    /// Record a tag operation
    pub fn record_tag(&self, file_path: &Path, tag: &str, added: bool) -> Result<String> {
        let entry = HistoryEntry::new(if added {
            Operation::TagAdded {
                file: file_path.to_path_buf(),
                tag: tag.to_string(),
            }
        } else {
            Operation::TagRemoved {
                file: file_path.to_path_buf(),
                tag: tag.to_string(),
            }
        });
        let id = entry.id.clone();
        self.record(entry)?;
        Ok(id)
    }

    /// Record a delete operation
    pub fn record_delete(&self, path: &Path, backup: &Path) -> Result<String> {
        let entry = HistoryEntry::new(Operation::Delete {
            path: path.to_path_buf(),
            backup: backup.to_path_buf(),
        });
        let id = entry.id.clone();
        self.record(entry)?;
        Ok(id)
    }

    /// Get the last N entries
    pub fn last(&self, count: usize) -> Result<Vec<HistoryEntry>> {
        let entries = self.all()?;
        Ok(entries.into_iter().rev().take(count).collect())
    }

    /// Get all entries
    pub fn all(&self) -> Result<Vec<HistoryEntry>> {
        let file = File::open(&self.path)?;
        let reader = BufReader::new(file);

        let mut entries = Vec::new();
        for line in reader.lines() {
            let line = line?;
            if line.trim().is_empty() {
                continue;
            }
            match serde_json::from_str(&line) {
                Ok(entry) => entries.push(entry),
                Err(e) => {
                    tracing::warn!("Failed to parse history entry: {}", e);
                }
            }
        }

        Ok(entries)
    }

    /// Get entry by ID
    pub fn get(&self, id: &str) -> Result<Option<HistoryEntry>> {
        let entries = self.all()?;
        Ok(entries.into_iter().find(|e| e.id == id))
    }

    /// Get undoable entries (not yet undone)
    pub fn undoable(&self) -> Result<Vec<HistoryEntry>> {
        let entries = self.all()?;
        Ok(entries.into_iter().filter(|e| !e.undone).collect())
    }

    /// Mark an entry as undone
    pub fn mark_undone(&self, id: &str) -> Result<()> {
        let entries = self.all()?;
        let updated: Vec<_> = entries
            .into_iter()
            .map(|mut e| {
                if e.id == id {
                    e.undone = true;
                    e.undone_at = Some(chrono::Utc::now().to_rfc3339());
                }
                e
            })
            .collect();

        self.rewrite(updated)
    }

    /// Undo an operation by ID
    pub fn undo(&self, id: &str) -> Result<()> {
        let entry = self
            .get(id)?
            .ok_or_else(|| Error::History {
                message: format!("Entry not found: {}", id),
            })?;

        if entry.undone {
            return Err(Error::History {
                message: format!("Entry already undone: {}", id),
            });
        }

        // Perform the undo
        match &entry.operation {
            Operation::Rename { from, to } => {
                std::fs::rename(to, from)?;
            }
            Operation::Delete { path, backup } => {
                std::fs::rename(backup, path)?;
            }
            Operation::TagAdded { .. } | Operation::TagRemoved { .. } => {
                // Tag operations need database access
                // This would be handled by the caller
            }
            Operation::Batch { operations } => {
                // Undo in reverse order
                for op in operations.iter().rev() {
                    match op {
                        Operation::Rename { from, to } => {
                            std::fs::rename(to, from)?;
                        }
                        Operation::Delete { path, backup } => {
                            std::fs::rename(backup, path)?;
                        }
                        _ => {}
                    }
                }
            }
        }

        self.mark_undone(id)?;
        Ok(())
    }

    /// Undo the last N operations
    pub fn undo_last(&self, count: usize) -> Result<Vec<String>> {
        let undoable = self.undoable()?;
        let to_undo: Vec<_> = undoable.into_iter().rev().take(count).collect();

        let mut undone_ids = Vec::new();
        for entry in to_undo {
            self.undo(&entry.id)?;
            undone_ids.push(entry.id);
        }

        Ok(undone_ids)
    }

    /// Clear all history
    pub fn clear(&self) -> Result<()> {
        File::create(&self.path)?;
        Ok(())
    }

    /// Rewrite the history file
    fn rewrite(&self, entries: Vec<HistoryEntry>) -> Result<()> {
        let mut file = File::create(&self.path)?;
        for entry in entries {
            let json = serde_json::to_string(&entry)?;
            writeln!(file, "{}", json)?;
        }
        Ok(())
    }

    /// Get history statistics
    pub fn stats(&self) -> Result<HistoryStats> {
        let entries = self.all()?;
        let total = entries.len();
        let undone = entries.iter().filter(|e| e.undone).count();
        let renames = entries
            .iter()
            .filter(|e| matches!(e.operation, Operation::Rename { .. }))
            .count();
        let tags = entries
            .iter()
            .filter(|e| {
                matches!(
                    e.operation,
                    Operation::TagAdded { .. } | Operation::TagRemoved { .. }
                )
            })
            .count();

        Ok(HistoryStats {
            total,
            undone,
            pending: total - undone,
            renames,
            tags,
        })
    }
}

/// A history entry
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct HistoryEntry {
    /// Unique entry ID
    pub id: String,
    /// Timestamp
    pub timestamp: String,
    /// The operation performed
    pub operation: Operation,
    /// Whether this has been undone
    #[serde(default)]
    pub undone: bool,
    /// When it was undone
    #[serde(default)]
    pub undone_at: Option<String>,
    /// Optional description
    #[serde(default)]
    pub description: Option<String>,
}

impl HistoryEntry {
    /// Create a new history entry
    pub fn new(operation: Operation) -> Self {
        Self {
            id: Uuid::new_v4().to_string(),
            timestamp: chrono::Utc::now().to_rfc3339(),
            operation,
            undone: false,
            undone_at: None,
            description: None,
        }
    }

    /// Add a description
    pub fn with_description(mut self, desc: impl Into<String>) -> Self {
        self.description = Some(desc.into());
        self
    }
}

/// An operation that can be undone
#[derive(Debug, Clone, Serialize, Deserialize)]
#[serde(tag = "type", rename_all = "snake_case")]
pub enum Operation {
    /// File rename
    Rename {
        /// Original path
        from: PathBuf,
        /// New path
        to: PathBuf,
    },
    /// File deletion (with backup)
    Delete {
        /// Deleted path
        path: PathBuf,
        /// Backup location
        backup: PathBuf,
    },
    /// Tag added
    TagAdded {
        /// File path
        file: PathBuf,
        /// Tag name
        tag: String,
    },
    /// Tag removed
    TagRemoved {
        /// File path
        file: PathBuf,
        /// Tag name
        tag: String,
    },
    /// Batch of operations
    Batch {
        /// Individual operations
        operations: Vec<Operation>,
    },
}

/// History statistics
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct HistoryStats {
    /// Total entries
    pub total: usize,
    /// Undone entries
    pub undone: usize,
    /// Pending (undoable) entries
    pub pending: usize,
    /// Rename operations
    pub renames: usize,
    /// Tag operations
    pub tags: usize,
}

#[cfg(test)]
mod tests {
    use super::*;
    use tempfile::tempdir;

    #[test]
    fn test_history_creation() {
        let dir = tempdir().unwrap();
        let path = dir.path().join("history.jsonl");
        let history = History::open(&path).unwrap();

        let entries = history.all().unwrap();
        assert!(entries.is_empty());
    }

    #[test]
    fn test_record_and_retrieve() {
        let dir = tempdir().unwrap();
        let path = dir.path().join("history.jsonl");
        let history = History::open(&path).unwrap();

        let id = history
            .record_rename(Path::new("/old/path"), Path::new("/new/path"))
            .unwrap();

        let entry = history.get(&id).unwrap().unwrap();
        assert!(!entry.undone);
        assert!(matches!(entry.operation, Operation::Rename { .. }));
    }

    #[test]
    fn test_history_stats() {
        let dir = tempdir().unwrap();
        let path = dir.path().join("history.jsonl");
        let history = History::open(&path).unwrap();

        history
            .record_rename(Path::new("/a"), Path::new("/b"))
            .unwrap();
        history
            .record_tag(Path::new("/a"), "test", true)
            .unwrap();

        let stats = history.stats().unwrap();
        assert_eq!(stats.total, 2);
        assert_eq!(stats.renames, 1);
        assert_eq!(stats.tags, 1);
    }
}
