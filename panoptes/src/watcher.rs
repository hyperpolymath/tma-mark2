// SPDX-FileCopyrightText: 2024 Panoptes Contributors
// SPDX-License-Identifier: MIT

//! File system watcher for automatic file processing

use notify::{
    event::{CreateKind, ModifyKind},
    Config, Event, EventKind, RecommendedWatcher, RecursiveMode, Watcher,
};
use std::collections::HashSet;
use std::path::{Path, PathBuf};
use std::sync::mpsc::{channel, Receiver};
use std::time::{Duration, Instant};
use tokio::sync::mpsc as tokio_mpsc;

use crate::config::WatchConfig;
use crate::error::{Error, Result};

/// File system watcher
pub struct FileWatcher {
    watcher: RecommendedWatcher,
    receiver: Receiver<notify::Result<Event>>,
    config: WatchConfig,
    watched_paths: HashSet<PathBuf>,
}

impl FileWatcher {
    /// Create a new file watcher
    pub fn new(config: WatchConfig) -> Result<Self> {
        let (tx, rx) = channel();

        let watcher = RecommendedWatcher::new(
            move |res| {
                let _ = tx.send(res);
            },
            Config::default()
                .with_poll_interval(Duration::from_millis(config.debounce_ms)),
        )?;

        Ok(Self {
            watcher,
            receiver: rx,
            config,
            watched_paths: HashSet::new(),
        })
    }

    /// Watch a directory
    pub fn watch(&mut self, path: &Path) -> Result<()> {
        let mode = if self.config.recursive {
            RecursiveMode::Recursive
        } else {
            RecursiveMode::NonRecursive
        };

        self.watcher.watch(path, mode)?;
        self.watched_paths.insert(path.to_path_buf());

        Ok(())
    }

    /// Stop watching a directory
    pub fn unwatch(&mut self, path: &Path) -> Result<()> {
        self.watcher.unwatch(path)?;
        self.watched_paths.remove(path);
        Ok(())
    }

    /// Get list of watched paths
    pub fn watched_paths(&self) -> &HashSet<PathBuf> {
        &self.watched_paths
    }

    /// Check if a path should be ignored
    fn should_ignore(&self, path: &Path) -> bool {
        let name = path
            .file_name()
            .and_then(|n| n.to_str())
            .unwrap_or("");

        // Check ignore patterns
        for pattern in &self.config.ignore_patterns {
            if let Ok(glob) = glob::Pattern::new(pattern) {
                if glob.matches(name) {
                    return true;
                }
            }
        }

        // Check if hidden
        if name.starts_with('.') {
            return true;
        }

        // Check for temporary files
        if name.ends_with('~') || name.ends_with(".tmp") || name.ends_with(".swp") {
            return true;
        }

        false
    }

    /// Process events and return file change events
    pub fn poll(&self) -> Vec<FileEvent> {
        let mut events = Vec::new();

        while let Ok(result) = self.receiver.try_recv() {
            match result {
                Ok(event) => {
                    if let Some(file_event) = self.process_event(event) {
                        events.push(file_event);
                    }
                }
                Err(e) => {
                    tracing::warn!("Watch error: {}", e);
                }
            }
        }

        events
    }

    /// Process a single notify event
    fn process_event(&self, event: Event) -> Option<FileEvent> {
        // Only process file events (not directories)
        let path = event.paths.first()?;

        if path.is_dir() || self.should_ignore(path) {
            return None;
        }

        match event.kind {
            EventKind::Create(CreateKind::File) => Some(FileEvent {
                kind: FileEventKind::Created,
                path: path.clone(),
            }),
            EventKind::Modify(ModifyKind::Data(_)) | EventKind::Modify(ModifyKind::Any) => {
                Some(FileEvent {
                    kind: FileEventKind::Modified,
                    path: path.clone(),
                })
            }
            EventKind::Remove(_) => Some(FileEvent {
                kind: FileEventKind::Removed,
                path: path.clone(),
            }),
            _ => None,
        }
    }
}

/// A file event
#[derive(Debug, Clone)]
pub struct FileEvent {
    /// Event kind
    pub kind: FileEventKind,
    /// Affected path
    pub path: PathBuf,
}

/// File event kind
#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub enum FileEventKind {
    /// File was created
    Created,
    /// File was modified
    Modified,
    /// File was removed
    Removed,
}

/// Debounced file watcher
///
/// Groups rapid file changes into single events
pub struct DebouncedWatcher {
    inner: FileWatcher,
    debounce_ms: u64,
    pending: std::collections::HashMap<PathBuf, (FileEventKind, Instant)>,
}

impl DebouncedWatcher {
    /// Create a new debounced watcher
    pub fn new(config: WatchConfig) -> Result<Self> {
        let debounce_ms = config.debounce_ms;
        Ok(Self {
            inner: FileWatcher::new(config)?,
            debounce_ms,
            pending: std::collections::HashMap::new(),
        })
    }

    /// Watch a directory
    pub fn watch(&mut self, path: &Path) -> Result<()> {
        self.inner.watch(path)
    }

    /// Stop watching a directory
    pub fn unwatch(&mut self, path: &Path) -> Result<()> {
        self.inner.unwatch(path)
    }

    /// Poll for debounced events
    pub fn poll(&mut self) -> Vec<FileEvent> {
        // Get raw events
        let raw_events = self.inner.poll();

        // Update pending events
        let now = Instant::now();
        for event in raw_events {
            self.pending.insert(event.path, (event.kind, now));
        }

        // Emit events that have settled
        let debounce_duration = Duration::from_millis(self.debounce_ms);
        let mut emitted = Vec::new();
        let mut to_remove = Vec::new();

        for (path, (kind, timestamp)) in &self.pending {
            if now.duration_since(*timestamp) >= debounce_duration {
                emitted.push(FileEvent {
                    kind: *kind,
                    path: path.clone(),
                });
                to_remove.push(path.clone());
            }
        }

        for path in to_remove {
            self.pending.remove(&path);
        }

        emitted
    }
}

/// Async file watcher using tokio channels
pub struct AsyncWatcher {
    watcher: RecommendedWatcher,
    event_rx: tokio_mpsc::Receiver<FileEvent>,
    config: WatchConfig,
}

impl AsyncWatcher {
    /// Create a new async watcher
    pub fn new(config: WatchConfig) -> Result<Self> {
        let (event_tx, event_rx) = tokio_mpsc::channel(100);
        let config_clone = config.clone();

        let watcher = RecommendedWatcher::new(
            move |res: notify::Result<Event>| {
                if let Ok(event) = res {
                    if let Some(file_event) = process_notify_event(&event, &config_clone) {
                        let _ = event_tx.blocking_send(file_event);
                    }
                }
            },
            Config::default()
                .with_poll_interval(Duration::from_millis(config.debounce_ms)),
        )?;

        Ok(Self {
            watcher,
            event_rx,
            config,
        })
    }

    /// Watch a directory
    pub fn watch(&mut self, path: &Path) -> Result<()> {
        let mode = if self.config.recursive {
            RecursiveMode::Recursive
        } else {
            RecursiveMode::NonRecursive
        };

        self.watcher.watch(path, mode)?;
        Ok(())
    }

    /// Receive the next event
    pub async fn recv(&mut self) -> Option<FileEvent> {
        self.event_rx.recv().await
    }
}

/// Process a notify event into a FileEvent
fn process_notify_event(event: &Event, config: &WatchConfig) -> Option<FileEvent> {
    let path = event.paths.first()?;

    if path.is_dir() {
        return None;
    }

    // Check ignore patterns
    let name = path.file_name().and_then(|n| n.to_str()).unwrap_or("");
    for pattern in &config.ignore_patterns {
        if let Ok(glob) = glob::Pattern::new(pattern) {
            if glob.matches(name) {
                return None;
            }
        }
    }

    match event.kind {
        EventKind::Create(CreateKind::File) => Some(FileEvent {
            kind: FileEventKind::Created,
            path: path.clone(),
        }),
        EventKind::Modify(ModifyKind::Data(_)) | EventKind::Modify(ModifyKind::Any) => {
            Some(FileEvent {
                kind: FileEventKind::Modified,
                path: path.clone(),
            })
        }
        EventKind::Remove(_) => Some(FileEvent {
            kind: FileEventKind::Removed,
            path: path.clone(),
        }),
        _ => None,
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_should_ignore() {
        let config = WatchConfig::default();
        let watcher = FileWatcher::new(config).unwrap();

        assert!(watcher.should_ignore(Path::new(".hidden")));
        assert!(watcher.should_ignore(Path::new("file.tmp")));
        assert!(watcher.should_ignore(Path::new("file~")));
        assert!(!watcher.should_ignore(Path::new("file.jpg")));
    }

    #[test]
    fn test_file_event_kind() {
        assert_eq!(FileEventKind::Created, FileEventKind::Created);
        assert_ne!(FileEventKind::Created, FileEventKind::Modified);
    }
}
