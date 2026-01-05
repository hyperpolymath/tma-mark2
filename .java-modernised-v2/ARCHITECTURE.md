<!-- SPDX-FileCopyrightText: 2024 eTMA Handler Contributors -->
<!-- SPDX-License-Identifier: MIT -->

# eTMA Handler Modernisation - Architecture Document

## Overview

This document captures the complete modernisation of the eTMA Handler save system,
developed in response to critical data integrity bugs discovered on 2026-01-05.

## The Problem

### Original Bug Analysis (EtmaMonitorJ.java)

The original eTMA Handler had catastrophic save bugs:

#### Bug 1: Empty File on Write Failure (line 5733)
```java
// WRONG: Creates empty file FIRST
new FileWriter(pathname).close();  // File now empty!
// Then tries to write... if this fails, empty file remains
buffer.write(fixedString);
```

#### Bug 2: Silent Failures (lines 5748-5757)
```java
catch (Exception anException) {
    System.out.println("Error2: " + anException);  // Only to console!
    // User never sees this error
}
```

#### Bug 3: No Verification
- Never re-reads file to confirm write succeeded
- Sets `savedFlag = true` regardless of actual outcome

#### Bug 4: Manual XML String Concatenation (line 6492+)
```java
// WRONG: No escaping, easy to corrupt
studentString = studentString + "<student_forenames>" + name + "</student_forenames>";
```

### Real Incident (2026-01-05)

1. User saved feedback for student "Rachael Mortimer"
2. Permission dialog interrupted save operation
3. FHI file corrupted - missing `>` on opening tags:
   ```xml
   <student_details<ou_computer_user_namezt971869</ou_computer_user_name
   ```
4. On reload: "Rachael doesn't exist but Holly exists twice"
5. Feedback appeared lost (actually present but unparseable)

## The Solution

### Architecture Layers

```
┌─────────────────────────────────────────────────────────────┐
│                        UI Layer                             │
│  - Shows UserMessage (success/error/progress)               │
│  - Never shows raw exceptions                               │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                  FormalSaveProtocol                         │
│  - State machine with defined transitions                   │
│  - Mathematical invariants checked at each step             │
│  - No silent failures possible                              │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    SafeFileService                          │
│  - Atomic writes (temp file → rename)                       │
│  - Pre-write backup                                         │
│  - Post-write verification                                  │
│  - XML validation                                           │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    IntegrityStore                           │
│  - Content-addressable storage (SHA-256)                    │
│  - Transaction journal                                      │
│  - Recovery from any previous state                         │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    Instrumentation                          │
│  - Tier 1: User messages (minimal, actionable)              │
│  - Tier 2: Developer logs (structured, filterable)          │
│  - Tier 3: Forensic events (everything, compressed)         │
└─────────────────────────────────────────────────────────────┘
```

### State Machine

```
INITIAL → PREPARING → BACKED_UP → TEMP_WRITTEN → VALIDATED → MOVED → COMPLETE
    ↓          ↓           ↓            ↓             ↓          ↓
  FAILED     FAILED      FAILED       FAILED        FAILED     FAILED
```

Any failure transitions to FAILED state with:
- User notification (never silent)
- Rollback of integrity transaction
- Original file unchanged (INV2)
- Recovery data available (INV3)

### Invariants

| ID | Invariant | Verification |
|----|-----------|--------------|
| INV1 | Content in memory never lost until COMPLETE | Checked at each transition |
| INV2 | Original file never modified until MOVED | Hash comparison |
| INV3 | Recovery possible from any FAILED state | IntegrityStore transaction |
| INV4 | User ALWAYS notified of current state | State change listener |
| INV5 | Hash(saved) == Hash(intended) at COMPLETE | Final verification |

## File Reference

### Core Services

| File | Lines | Purpose |
|------|-------|---------|
| `SafeFileService.java` | ~350 | Atomic writes, verification, backup |
| `SaveResult.java` | ~180 | Explicit success/failure results |
| `FhiXmlHandler.java` | ~300 | DOM-based XML handling |
| `IntegrityStore.java` | ~400 | Content-addressable backup |
| `FormalSaveProtocol.java` | ~350 | State machine with invariants |
| `Instrumentation.java` | ~450 | Three-tier logging system |

### Models

| File | Purpose |
|------|---------|
| `Student.java` | Student data model |
| `Tutor.java` | Tutor data model |
| `Submission.java` | Submission data model |
| `Course.java` | Course data model |

## Usage Example

```java
// Create services
SafeFileService fileService = new SafeFileService();
IntegrityStore integrityStore = new IntegrityStore();
Instrumentation instrumentation = Instrumentation.get();

// Create protocol with UI notification
FormalSaveProtocol protocol = new FormalSaveProtocol(
    fileService,
    integrityStore,
    ctx -> {
        // Update UI with current state
        switch (ctx.getState()) {
            case PREPARING -> instrumentation.showUserProgress("Saving...");
            case COMPLETE -> instrumentation.showUserSuccess("Saved");
            case FAILED -> instrumentation.showUserError(
                "Save failed",
                ctx.getErrors().get(0)
            );
        }
    }
);

// Execute save
SaveResult result = protocol.executeSave(
    Path.of("/path/to/file.fhi"),
    xmlContent,
    Charset.forName("ISO-8859-1")
);

// Result is NEVER ambiguous
if (result.isSuccess()) {
    // File definitely saved and verified
} else {
    // File definitely NOT saved, user notified, recovery available
}
```

## Instrumentation Tiers

### Tier 1: User-Facing
```
✓ Save complete                    ← Auto-dismiss after 3s
⚠ File may have unsaved changes   ← Requires acknowledgment
✗ Save failed - Click to retry    ← Requires action
```

### Tier 2: Developer Logs
```
2026-01-05T14:32:15Z [INFO] [abc123] SafeFileService.save: Starting save
2026-01-05T14:32:15Z [DEBUG] [abc123] SafeFileService.backup: Created backup
2026-01-05T14:32:15Z [INFO] [abc123] SafeFileService.save: Save complete {bytes=4521}
```

### Tier 3: Forensic Events
```json
{"seq":1234,"ts":"2026-01-05T14:32:15.123Z","cid":"abc123","c":"SafeFileService","o":"save","p":"start"}
{"seq":1235,"ts":"2026-01-05T14:32:15.456Z","cid":"abc123","c":"SafeFileService","o":"backup","p":"success"}
{"seq":1236,"ts":"2026-01-05T14:32:15.789Z","cid":"abc123","c":"SafeFileService","o":"save","p":"success"}
```

## Recovery Scenarios

### Scenario 1: Corruption Detected on Load
```java
IntegrityStore.IntegrityCheckResult check = integrityStore.verifyIntegrity(path);
if (check.isCorrupted()) {
    // Show user: "File corrupted, restore from backup?"
    SaveResult restored = integrityStore.restoreFromStore(path);
}
```

### Scenario 2: Save Interrupted
```java
// If save fails at any point, transaction is rolled back
// User's content still in memory, IntegrityStore has copy
transaction.getAfterContent()  // ← The content that SHOULD have been saved
```

### Scenario 3: Post-Mortem Analysis
```bash
# Decompress forensic logs
gunzip ~/.etma-logs/forensic-2026-01-05.jsonl.gz

# Find all events for correlation ID
grep '"cid":"abc123"' forensic-2026-01-05.jsonl

# Find all failures
grep '"p":"failure"' forensic-2026-01-05.jsonl
```

## Integration with IDEAS.md

This implementation addresses these IDEAS.md items:
- #54 Pre-Send Validation
- File State Verification
- XML/FHI Data Integrity
- Silent Save Failure

## Version History

| Date | Change |
|------|--------|
| 2026-01-05 | Initial implementation after data corruption incident |

## Authors

- Developed during Claude Code session
- In response to real data loss incident
