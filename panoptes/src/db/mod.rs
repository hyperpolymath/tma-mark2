// SPDX-FileCopyrightText: 2024 Panoptes Contributors
// SPDX-License-Identifier: MIT

//! SQLite database for storing file metadata, tags, and categories

use rusqlite::{params, Connection, OptionalExtension};
use serde::{Deserialize, Serialize};
use std::path::{Path, PathBuf};
use uuid::Uuid;

use crate::error::{Error, Result};

/// Database connection wrapper
pub struct Database {
    conn: Connection,
}

impl Database {
    /// Open or create a database at the given path
    pub fn open(path: &Path) -> Result<Self> {
        // Ensure parent directory exists
        if let Some(parent) = path.parent() {
            std::fs::create_dir_all(parent)?;
        }

        let conn = Connection::open(path)?;
        let db = Self { conn };
        db.init()?;
        Ok(db)
    }

    /// Open an in-memory database (for testing)
    pub fn open_in_memory() -> Result<Self> {
        let conn = Connection::open_in_memory()?;
        let db = Self { conn };
        db.init()?;
        Ok(db)
    }

    /// Initialize the database schema
    fn init(&self) -> Result<()> {
        self.conn.execute_batch(
            r#"
            -- Files table
            CREATE TABLE IF NOT EXISTS files (
                id TEXT PRIMARY KEY,
                path TEXT NOT NULL UNIQUE,
                hash TEXT,
                size INTEGER NOT NULL,
                mime_type TEXT,
                original_name TEXT NOT NULL,
                suggested_name TEXT,
                current_name TEXT NOT NULL,
                description TEXT,
                confidence REAL,
                analyzer TEXT,
                analyzed_at TEXT,
                created_at TEXT NOT NULL DEFAULT (datetime('now')),
                updated_at TEXT NOT NULL DEFAULT (datetime('now'))
            );

            -- Tags table
            CREATE TABLE IF NOT EXISTS tags (
                id TEXT PRIMARY KEY,
                name TEXT NOT NULL UNIQUE,
                color TEXT,
                description TEXT,
                created_at TEXT NOT NULL DEFAULT (datetime('now'))
            );

            -- File-tag association
            CREATE TABLE IF NOT EXISTS file_tags (
                file_id TEXT NOT NULL,
                tag_id TEXT NOT NULL,
                created_at TEXT NOT NULL DEFAULT (datetime('now')),
                PRIMARY KEY (file_id, tag_id),
                FOREIGN KEY (file_id) REFERENCES files(id) ON DELETE CASCADE,
                FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
            );

            -- Categories table (hierarchical)
            CREATE TABLE IF NOT EXISTS categories (
                id TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                parent_id TEXT,
                path TEXT NOT NULL,
                created_at TEXT NOT NULL DEFAULT (datetime('now')),
                FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE SET NULL,
                UNIQUE(name, parent_id)
            );

            -- File-category association
            CREATE TABLE IF NOT EXISTS file_categories (
                file_id TEXT NOT NULL,
                category_id TEXT NOT NULL,
                PRIMARY KEY (file_id, category_id),
                FOREIGN KEY (file_id) REFERENCES files(id) ON DELETE CASCADE,
                FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
            );

            -- Metadata table (key-value store for file metadata)
            CREATE TABLE IF NOT EXISTS file_metadata (
                file_id TEXT NOT NULL,
                key TEXT NOT NULL,
                value TEXT NOT NULL,
                PRIMARY KEY (file_id, key),
                FOREIGN KEY (file_id) REFERENCES files(id) ON DELETE CASCADE
            );

            -- Indexes
            CREATE INDEX IF NOT EXISTS idx_files_path ON files(path);
            CREATE INDEX IF NOT EXISTS idx_files_hash ON files(hash);
            CREATE INDEX IF NOT EXISTS idx_files_analyzed_at ON files(analyzed_at);
            CREATE INDEX IF NOT EXISTS idx_tags_name ON tags(name);
            CREATE INDEX IF NOT EXISTS idx_categories_path ON categories(path);
            "#,
        )?;
        Ok(())
    }

    // File operations

    /// Insert or update a file record
    pub fn upsert_file(&self, file: &FileRecord) -> Result<()> {
        self.conn.execute(
            r#"
            INSERT INTO files (id, path, hash, size, mime_type, original_name,
                               suggested_name, current_name, description, confidence,
                               analyzer, analyzed_at, created_at, updated_at)
            VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9, ?10, ?11, ?12, ?13, ?14)
            ON CONFLICT(path) DO UPDATE SET
                hash = excluded.hash,
                size = excluded.size,
                mime_type = excluded.mime_type,
                suggested_name = excluded.suggested_name,
                current_name = excluded.current_name,
                description = excluded.description,
                confidence = excluded.confidence,
                analyzer = excluded.analyzer,
                analyzed_at = excluded.analyzed_at,
                updated_at = datetime('now')
            "#,
            params![
                file.id,
                file.path.to_string_lossy(),
                file.hash,
                file.size,
                file.mime_type,
                file.original_name,
                file.suggested_name,
                file.current_name,
                file.description,
                file.confidence,
                file.analyzer,
                file.analyzed_at,
                file.created_at,
                file.updated_at,
            ],
        )?;
        Ok(())
    }

    /// Get a file by path
    pub fn get_file_by_path(&self, path: &Path) -> Result<Option<FileRecord>> {
        let path_str = path.to_string_lossy();
        self.conn
            .query_row(
                "SELECT * FROM files WHERE path = ?1",
                params![path_str.as_ref()],
                |row| FileRecord::from_row(row),
            )
            .optional()
            .map_err(Into::into)
    }

    /// Get a file by ID
    pub fn get_file(&self, id: &str) -> Result<Option<FileRecord>> {
        self.conn
            .query_row("SELECT * FROM files WHERE id = ?1", params![id], |row| {
                FileRecord::from_row(row)
            })
            .optional()
            .map_err(Into::into)
    }

    /// Get a file by hash (for deduplication)
    pub fn get_file_by_hash(&self, hash: &str) -> Result<Option<FileRecord>> {
        self.conn
            .query_row(
                "SELECT * FROM files WHERE hash = ?1",
                params![hash],
                |row| FileRecord::from_row(row),
            )
            .optional()
            .map_err(Into::into)
    }

    /// Delete a file record
    pub fn delete_file(&self, id: &str) -> Result<()> {
        self.conn
            .execute("DELETE FROM files WHERE id = ?1", params![id])?;
        Ok(())
    }

    /// List all files
    pub fn list_files(&self, limit: Option<u32>, offset: Option<u32>) -> Result<Vec<FileRecord>> {
        let limit = limit.unwrap_or(100);
        let offset = offset.unwrap_or(0);

        let mut stmt = self.conn.prepare(
            "SELECT * FROM files ORDER BY analyzed_at DESC LIMIT ?1 OFFSET ?2",
        )?;

        let files = stmt
            .query_map(params![limit, offset], |row| FileRecord::from_row(row))?
            .collect::<std::result::Result<Vec<_>, _>>()?;

        Ok(files)
    }

    /// Search files by name
    pub fn search_files(&self, query: &str) -> Result<Vec<FileRecord>> {
        let pattern = format!("%{}%", query);
        let mut stmt = self.conn.prepare(
            "SELECT * FROM files WHERE current_name LIKE ?1 OR original_name LIKE ?1 ORDER BY analyzed_at DESC",
        )?;

        let files = stmt
            .query_map(params![pattern], |row| FileRecord::from_row(row))?
            .collect::<std::result::Result<Vec<_>, _>>()?;

        Ok(files)
    }

    // Tag operations

    /// Create a tag
    pub fn create_tag(&self, name: &str, color: Option<&str>) -> Result<Tag> {
        let id = Uuid::new_v4().to_string();
        self.conn.execute(
            "INSERT INTO tags (id, name, color) VALUES (?1, ?2, ?3)",
            params![id, name, color],
        )?;
        Ok(Tag {
            id,
            name: name.to_string(),
            color: color.map(String::from),
            description: None,
        })
    }

    /// Get or create a tag
    pub fn get_or_create_tag(&self, name: &str) -> Result<Tag> {
        if let Some(tag) = self.get_tag_by_name(name)? {
            Ok(tag)
        } else {
            self.create_tag(name, None)
        }
    }

    /// Get a tag by name
    pub fn get_tag_by_name(&self, name: &str) -> Result<Option<Tag>> {
        self.conn
            .query_row(
                "SELECT id, name, color, description FROM tags WHERE name = ?1",
                params![name],
                |row| {
                    Ok(Tag {
                        id: row.get(0)?,
                        name: row.get(1)?,
                        color: row.get(2)?,
                        description: row.get(3)?,
                    })
                },
            )
            .optional()
            .map_err(Into::into)
    }

    /// List all tags
    pub fn list_tags(&self) -> Result<Vec<Tag>> {
        let mut stmt = self
            .conn
            .prepare("SELECT id, name, color, description FROM tags ORDER BY name")?;

        let tags = stmt
            .query_map([], |row| {
                Ok(Tag {
                    id: row.get(0)?,
                    name: row.get(1)?,
                    color: row.get(2)?,
                    description: row.get(3)?,
                })
            })?
            .collect::<std::result::Result<Vec<_>, _>>()?;

        Ok(tags)
    }

    /// Add a tag to a file
    pub fn add_file_tag(&self, file_id: &str, tag_id: &str) -> Result<()> {
        self.conn.execute(
            "INSERT OR IGNORE INTO file_tags (file_id, tag_id) VALUES (?1, ?2)",
            params![file_id, tag_id],
        )?;
        Ok(())
    }

    /// Remove a tag from a file
    pub fn remove_file_tag(&self, file_id: &str, tag_id: &str) -> Result<()> {
        self.conn.execute(
            "DELETE FROM file_tags WHERE file_id = ?1 AND tag_id = ?2",
            params![file_id, tag_id],
        )?;
        Ok(())
    }

    /// Get tags for a file
    pub fn get_file_tags(&self, file_id: &str) -> Result<Vec<Tag>> {
        let mut stmt = self.conn.prepare(
            r#"
            SELECT t.id, t.name, t.color, t.description
            FROM tags t
            JOIN file_tags ft ON t.id = ft.tag_id
            WHERE ft.file_id = ?1
            ORDER BY t.name
            "#,
        )?;

        let tags = stmt
            .query_map(params![file_id], |row| {
                Ok(Tag {
                    id: row.get(0)?,
                    name: row.get(1)?,
                    color: row.get(2)?,
                    description: row.get(3)?,
                })
            })?
            .collect::<std::result::Result<Vec<_>, _>>()?;

        Ok(tags)
    }

    /// Get files with a specific tag
    pub fn get_files_by_tag(&self, tag_name: &str) -> Result<Vec<FileRecord>> {
        let mut stmt = self.conn.prepare(
            r#"
            SELECT f.*
            FROM files f
            JOIN file_tags ft ON f.id = ft.file_id
            JOIN tags t ON ft.tag_id = t.id
            WHERE t.name = ?1
            ORDER BY f.analyzed_at DESC
            "#,
        )?;

        let files = stmt
            .query_map(params![tag_name], |row| FileRecord::from_row(row))?
            .collect::<std::result::Result<Vec<_>, _>>()?;

        Ok(files)
    }

    // Category operations

    /// Create a category
    pub fn create_category(&self, name: &str, parent_id: Option<&str>) -> Result<Category> {
        let id = Uuid::new_v4().to_string();

        let path = if let Some(pid) = parent_id {
            if let Some(parent) = self.get_category(pid)? {
                format!("{}/{}", parent.path, name)
            } else {
                name.to_string()
            }
        } else {
            name.to_string()
        };

        self.conn.execute(
            "INSERT INTO categories (id, name, parent_id, path) VALUES (?1, ?2, ?3, ?4)",
            params![id, name, parent_id, path],
        )?;

        Ok(Category {
            id,
            name: name.to_string(),
            parent_id: parent_id.map(String::from),
            path,
        })
    }

    /// Get a category by ID
    pub fn get_category(&self, id: &str) -> Result<Option<Category>> {
        self.conn
            .query_row(
                "SELECT id, name, parent_id, path FROM categories WHERE id = ?1",
                params![id],
                |row| {
                    Ok(Category {
                        id: row.get(0)?,
                        name: row.get(1)?,
                        parent_id: row.get(2)?,
                        path: row.get(3)?,
                    })
                },
            )
            .optional()
            .map_err(Into::into)
    }

    /// Get or create a category by path
    pub fn get_or_create_category_by_path(&self, path: &str) -> Result<Category> {
        if let Some(cat) = self.get_category_by_path(path)? {
            return Ok(cat);
        }

        let parts: Vec<&str> = path.split('/').collect();
        let mut parent_id: Option<String> = None;

        for part in parts {
            let existing = self.conn.query_row(
                "SELECT id FROM categories WHERE name = ?1 AND parent_id IS ?2",
                params![part, &parent_id],
                |row| row.get::<_, String>(0),
            );

            parent_id = Some(match existing {
                Ok(id) => id,
                Err(_) => {
                    let cat = self.create_category(part, parent_id.as_deref())?;
                    cat.id
                }
            });
        }

        self.get_category_by_path(path)?
            .ok_or_else(|| Error::database("Failed to create category"))
    }

    /// Get a category by path
    pub fn get_category_by_path(&self, path: &str) -> Result<Option<Category>> {
        self.conn
            .query_row(
                "SELECT id, name, parent_id, path FROM categories WHERE path = ?1",
                params![path],
                |row| {
                    Ok(Category {
                        id: row.get(0)?,
                        name: row.get(1)?,
                        parent_id: row.get(2)?,
                        path: row.get(3)?,
                    })
                },
            )
            .optional()
            .map_err(Into::into)
    }

    /// List all categories
    pub fn list_categories(&self) -> Result<Vec<Category>> {
        let mut stmt = self
            .conn
            .prepare("SELECT id, name, parent_id, path FROM categories ORDER BY path")?;

        let categories = stmt
            .query_map([], |row| {
                Ok(Category {
                    id: row.get(0)?,
                    name: row.get(1)?,
                    parent_id: row.get(2)?,
                    path: row.get(3)?,
                })
            })?
            .collect::<std::result::Result<Vec<_>, _>>()?;

        Ok(categories)
    }

    // Statistics

    /// Get database statistics
    pub fn stats(&self) -> Result<DatabaseStats> {
        let file_count: u64 = self
            .conn
            .query_row("SELECT COUNT(*) FROM files", [], |row| row.get(0))?;
        let tag_count: u64 = self
            .conn
            .query_row("SELECT COUNT(*) FROM tags", [], |row| row.get(0))?;
        let category_count: u64 = self
            .conn
            .query_row("SELECT COUNT(*) FROM categories", [], |row| row.get(0))?;
        let total_size: u64 = self
            .conn
            .query_row("SELECT COALESCE(SUM(size), 0) FROM files", [], |row| {
                row.get(0)
            })?;

        Ok(DatabaseStats {
            file_count,
            tag_count,
            category_count,
            total_size,
        })
    }
}

/// File record
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct FileRecord {
    pub id: String,
    pub path: PathBuf,
    pub hash: Option<String>,
    pub size: i64,
    pub mime_type: Option<String>,
    pub original_name: String,
    pub suggested_name: Option<String>,
    pub current_name: String,
    pub description: Option<String>,
    pub confidence: Option<f64>,
    pub analyzer: Option<String>,
    pub analyzed_at: Option<String>,
    pub created_at: String,
    pub updated_at: String,
}

impl FileRecord {
    /// Create a new file record
    pub fn new(path: &Path, size: i64) -> Self {
        let name = path
            .file_name()
            .and_then(|n| n.to_str())
            .unwrap_or("unknown")
            .to_string();

        Self {
            id: Uuid::new_v4().to_string(),
            path: path.to_path_buf(),
            hash: None,
            size,
            mime_type: None,
            original_name: name.clone(),
            suggested_name: None,
            current_name: name,
            description: None,
            confidence: None,
            analyzer: None,
            analyzed_at: None,
            created_at: chrono::Utc::now().to_rfc3339(),
            updated_at: chrono::Utc::now().to_rfc3339(),
        }
    }

    fn from_row(row: &rusqlite::Row) -> rusqlite::Result<Self> {
        Ok(Self {
            id: row.get(0)?,
            path: PathBuf::from(row.get::<_, String>(1)?),
            hash: row.get(2)?,
            size: row.get(3)?,
            mime_type: row.get(4)?,
            original_name: row.get(5)?,
            suggested_name: row.get(6)?,
            current_name: row.get(7)?,
            description: row.get(8)?,
            confidence: row.get(9)?,
            analyzer: row.get(10)?,
            analyzed_at: row.get(11)?,
            created_at: row.get(12)?,
            updated_at: row.get(13)?,
        })
    }
}

/// Tag record
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct Tag {
    pub id: String,
    pub name: String,
    pub color: Option<String>,
    pub description: Option<String>,
}

/// Category record
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct Category {
    pub id: String,
    pub name: String,
    pub parent_id: Option<String>,
    pub path: String,
}

/// Database statistics
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct DatabaseStats {
    pub file_count: u64,
    pub tag_count: u64,
    pub category_count: u64,
    pub total_size: u64,
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_database_creation() {
        let db = Database::open_in_memory().unwrap();
        let stats = db.stats().unwrap();
        assert_eq!(stats.file_count, 0);
    }

    #[test]
    fn test_file_operations() {
        let db = Database::open_in_memory().unwrap();

        let file = FileRecord::new(Path::new("/tmp/test.jpg"), 1024);
        db.upsert_file(&file).unwrap();

        let retrieved = db.get_file(&file.id).unwrap().unwrap();
        assert_eq!(retrieved.id, file.id);
        assert_eq!(retrieved.size, 1024);
    }

    #[test]
    fn test_tag_operations() {
        let db = Database::open_in_memory().unwrap();

        let tag = db.create_tag("nature", Some("#00ff00")).unwrap();
        assert_eq!(tag.name, "nature");

        let found = db.get_tag_by_name("nature").unwrap().unwrap();
        assert_eq!(found.id, tag.id);
    }

    #[test]
    fn test_file_tags() {
        let db = Database::open_in_memory().unwrap();

        let file = FileRecord::new(Path::new("/tmp/test.jpg"), 1024);
        db.upsert_file(&file).unwrap();

        let tag = db.create_tag("test", None).unwrap();
        db.add_file_tag(&file.id, &tag.id).unwrap();

        let tags = db.get_file_tags(&file.id).unwrap();
        assert_eq!(tags.len(), 1);
        assert_eq!(tags[0].name, "test");
    }
}
