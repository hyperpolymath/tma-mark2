<!-- SPDX-FileCopyrightText: 2024 eTMA Handler Contributors -->
<!-- SPDX-License-Identifier: MIT -->

# Reversibility Policy

eTMA Handler is designed with reversibility as a core principle.

## Philosophy

Every operation that modifies data should be undoable. Tutors should be able to experiment with marking without fear of losing work.

## Implemented Reversibility

### Marking Operations

| Operation | Reversible | Method |
|-----------|------------|--------|
| Grade assignment | ✅ | Undo/redo stack |
| Add feedback | ✅ | Undo/redo stack |
| Delete feedback | ✅ | Soft delete + undo |
| Bulk grade | ✅ | Transaction rollback |

### File Operations

| Operation | Reversible | Method |
|-----------|------------|--------|
| Import .fhi | ✅ | Can re-import |
| Export .docx | ✅ | Non-destructive |
| Delete assignment | ✅ | Soft delete (30 days) |
| Clear database | ⚠️ | Backup required |

### Configuration

| Operation | Reversible | Method |
|-----------|------------|--------|
| Change settings | ✅ | Config versioning |
| Reset to defaults | ✅ | Backup preserved |

## Technical Implementation

### CubDB Append-Only

CubDB uses an append-only B-tree, meaning:
- Old versions preserved until compaction
- Point-in-time recovery possible
- No data loss from crashes

### Automatic Backups

```elixir
config :etma_handler,
  auto_backup: true,
  backup_interval: :hourly,
  backup_retention: 7  # days
```

### Undo Stack

- In-memory undo/redo for session
- Persistent history for important operations
- Configurable history depth

## Recovery Procedures

### Accidental Deletion

1. Check soft-deleted items: `Settings → Trash`
2. Restore within 30 days
3. After 30 days: restore from backup

### Corrupted Database

1. Application creates automatic backup on start
2. Restore: `etma_handler restore --backup latest`
3. Manual: Copy from `~/.local/share/etma_handler/backups/`

### Grade Rollback

1. `Edit → Undo` for recent changes
2. `History → View` for older changes
3. Select point-in-time to restore

## Limitations

Operations that are **NOT** reversible:

1. **Backup deletion** - Backups older than retention are purged
2. **Database compaction** - Old versions removed after compaction
3. **Export to student** - Once sent, cannot be recalled

## Best Practices

1. **Enable auto-backup** (default: on)
2. **Review before bulk operations**
3. **Use soft delete** (default: on)
4. **Export periodically** for external backup

---

Last updated: 2024-12-01
