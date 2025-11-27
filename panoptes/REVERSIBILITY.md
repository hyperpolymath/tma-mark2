# Reversibility Policy

Panoptes is designed with full reversibility as a core principle. All operations that modify files can be undone.

## Design Principles

### 1. No Destructive Operations

- Original files are **never** deleted automatically
- Renames are tracked and reversible
- Tags and metadata are stored separately from files

### 2. Complete History

Every operation is logged with:
- Timestamp
- Operation type
- Original state
- New state
- User/process initiator

### 3. Multiple Recovery Paths

- **Immediate undo**: Via `panoptes undo`
- **History browse**: Via `panoptes history`
- **Database restore**: Via SQLite backup
- **Log replay**: Via JSONL history file

## How Undo Works

### File Renames

```bash
# Undo the last rename
panoptes undo

# Undo multiple operations
panoptes undo --count 5

# Undo specific operation by ID
panoptes undo --id <operation-id>

# Show what would be undone
panoptes undo --dry-run
```

### History Management

```bash
# View recent history
panoptes history

# View history for specific file
panoptes history --file /path/to/file

# Export history
panoptes history --export history-backup.jsonl

# Import history
panoptes history --import history-backup.jsonl
```

## History Format

Operations are stored in JSONL format:

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "timestamp": "2024-12-01T12:00:00Z",
  "operation": "rename",
  "original_path": "/path/to/IMG_1234.jpg",
  "new_path": "/path/to/sunset_beach_2024.jpg",
  "reversible": true
}
```

## Database Backups

The SQLite database includes:
- File metadata
- Tags and categories
- Analysis results
- Operation log

### Creating Backups

```bash
# Create backup
panoptes db backup

# Restore from backup
panoptes db restore backup.db

# Export to JSON
panoptes db export --format json
```

## Limitations

Some operations have limited reversibility:

| Operation | Reversibility | Notes |
|-----------|---------------|-------|
| Rename | Full | Complete undo available |
| Tag add | Full | Tag removal tracked |
| Tag remove | Full | Original tags stored |
| Delete | Partial | Moves to trash, configurable |
| External edit | None | File changes outside Panoptes |

## Configuration

Control history in `config.json`:

```json
{
  "history": {
    "path": "~/.local/share/panoptes/history.jsonl",
    "max_entries": 10000,
    "retention_days": 90,
    "auto_backup": true,
    "backup_interval_hours": 24
  }
}
```

## Best Practices

1. **Regular backups**: Enable `auto_backup`
2. **Test undo**: Use `--dry-run` before operations
3. **Monitor history size**: Prune old entries periodically
4. **External backup**: Copy history file to backup storage

## Recovery Scenarios

### Scenario 1: Accidental Rename

```bash
# Oops, wrong file renamed
panoptes undo  # Reverts last operation
```

### Scenario 2: Batch Operation Gone Wrong

```bash
# Renamed 100 files incorrectly
panoptes undo --count 100
```

### Scenario 3: Database Corruption

```bash
# Restore from backup
panoptes db restore ~/.local/share/panoptes/backup.db
```

### Scenario 4: Complete Recovery

```bash
# Export current state
panoptes db export --format json > state.json

# Replay from history
panoptes history --replay --from "2024-12-01"
```

---

_Reversibility is not optional - it's essential._
