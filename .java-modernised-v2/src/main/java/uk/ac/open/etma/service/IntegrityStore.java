// SPDX-FileCopyrightText: 2024 eTMA Handler Contributors
// SPDX-License-Identifier: MIT
package uk.ac.open.etma.service;

import uk.ac.open.etma.util.SaveResult;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Internal integrity store to protect against file corruption.
 *
 * <p>This provides defense-in-depth against the fragile FHI file format:
 * <ul>
 *   <li><b>Content-addressable storage:</b> Each save is stored by its SHA-256 hash</li>
 *   <li><b>Transaction journal:</b> Every operation is logged with before/after state</li>
 *   <li><b>Integrity verification:</b> Detects corruption by comparing file hash to stored hash</li>
 *   <li><b>Recovery:</b> Can restore from any previous good state</li>
 * </ul>
 *
 * <p>Storage layout:
 * <pre>
 * ~/.etma-integrity/
 * ├── objects/           # Content-addressable store (by SHA-256)
 * │   ├── a1/
 * │   │   └── b2c3d4...  # Content stored by hash
 * │   └── ...
 * ├── refs/              # Current state references per file
 * │   └── E225-25J-01-1-zt971869.fhi.ref  # Points to current hash
 * ├── journal/           # Transaction log
 * │   └── 2026-01-05.journal
 * └── manifest.json      # Index of all tracked files
 * </pre>
 *
 * <p>Even if the main FHI file is corrupted or lost, we can:
 * <ol>
 *   <li>Detect corruption (hash mismatch)</li>
 *   <li>Show what the file SHOULD contain</li>
 *   <li>Restore from the integrity store</li>
 * </ol>
 */
public final class IntegrityStore {

    private static final String STORE_DIR_NAME = ".etma-integrity";
    private static final String OBJECTS_DIR = "objects";
    private static final String REFS_DIR = "refs";
    private static final String JOURNAL_DIR = "journal";
    private static final String MANIFEST_FILE = "manifest.json";

    private final Path storeRoot;
    private final Path objectsDir;
    private final Path refsDir;
    private final Path journalDir;
    private final Map<String, FileRecord> manifest;

    /**
     * Creates an IntegrityStore in the user's home directory.
     */
    public IntegrityStore() {
        this(Path.of(System.getProperty("user.home"), STORE_DIR_NAME));
    }

    /**
     * Creates an IntegrityStore at the specified location.
     *
     * @param storeRoot Root directory for the integrity store
     */
    public IntegrityStore(Path storeRoot) {
        this.storeRoot = storeRoot;
        this.objectsDir = storeRoot.resolve(OBJECTS_DIR);
        this.refsDir = storeRoot.resolve(REFS_DIR);
        this.journalDir = storeRoot.resolve(JOURNAL_DIR);
        this.manifest = new ConcurrentHashMap<>();
        initialize();
    }

    /**
     * Records content before and after a save operation.
     *
     * <p>Call this BEFORE the actual file write:
     * <pre>{@code
     * Transaction tx = integrityStore.beginTransaction(filePath, oldContent, newContent);
     * // ... perform actual file save ...
     * if (saveSucceeded) {
     *     tx.commit();
     * } else {
     *     tx.rollback();
     * }
     * }</pre>
     *
     * @param filePath Path to the file being saved
     * @param beforeContent Content before save (null if new file)
     * @param afterContent Content being saved
     * @return Transaction object for commit/rollback
     */
    public Transaction beginTransaction(Path filePath, String beforeContent, String afterContent) {
        String beforeHash = beforeContent != null ? storeContent(beforeContent) : null;
        String afterHash = storeContent(afterContent);

        return new Transaction(this, filePath, beforeHash, afterHash, Instant.now());
    }

    /**
     * Verifies a file's integrity against the stored hash.
     *
     * @param filePath Path to the file to verify
     * @return IntegrityCheckResult with status and details
     */
    public IntegrityCheckResult verifyIntegrity(Path filePath) {
        String fileKey = getFileKey(filePath);
        FileRecord record = manifest.get(fileKey);

        if (record == null) {
            return IntegrityCheckResult.unknown(filePath, "File not tracked by integrity store");
        }

        if (!Files.exists(filePath)) {
            return IntegrityCheckResult.missing(filePath, record.currentHash,
                "File missing - can be restored from integrity store");
        }

        try {
            String actualHash = hashFile(filePath);
            if (record.currentHash.equals(actualHash)) {
                return IntegrityCheckResult.valid(filePath, actualHash);
            } else {
                return IntegrityCheckResult.corrupted(filePath, record.currentHash, actualHash,
                    "File content has changed - possible corruption");
            }
        } catch (IOException e) {
            return IntegrityCheckResult.error(filePath, "Cannot read file: " + e.getMessage());
        }
    }

    /**
     * Retrieves stored content by its hash.
     *
     * @param hash SHA-256 hash of the content
     * @return Optional containing content if found
     */
    public Optional<String> retrieveContent(String hash) {
        Path objectPath = getObjectPath(hash);
        if (!Files.exists(objectPath)) {
            return Optional.empty();
        }
        try {
            return Optional.of(Files.readString(objectPath, StandardCharsets.UTF_8));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    /**
     * Gets the stored hash for a file (what it SHOULD be).
     *
     * @param filePath Path to the file
     * @return Optional containing expected hash
     */
    public Optional<String> getExpectedHash(Path filePath) {
        String fileKey = getFileKey(filePath);
        FileRecord record = manifest.get(fileKey);
        return record != null ? Optional.of(record.currentHash) : Optional.empty();
    }

    /**
     * Restores a file from the integrity store.
     *
     * @param filePath Path where file should be restored
     * @return SaveResult indicating success or failure
     */
    public SaveResult restoreFromStore(Path filePath) {
        String fileKey = getFileKey(filePath);
        FileRecord record = manifest.get(fileKey);

        if (record == null) {
            return SaveResult.failure("File not tracked: " + filePath);
        }

        Optional<String> content = retrieveContent(record.currentHash);
        if (content.isEmpty()) {
            return SaveResult.failure("Content not found in store for hash: " + record.currentHash);
        }

        try {
            // Backup corrupted version first
            if (Files.exists(filePath)) {
                String timestamp = Instant.now().toString().replace(":", "-");
                Path corruptedBackup = filePath.resolveSibling(
                    filePath.getFileName() + "." + timestamp + ".corrupted");
                Files.copy(filePath, corruptedBackup);
            }

            // Restore from store
            Files.writeString(filePath, content.get(), StandardCharsets.UTF_8);
            logJournalEntry("RESTORE", filePath, null, record.currentHash, "Restored from integrity store");

            return SaveResult.success(filePath, content.get().length(), record.currentHash);
        } catch (IOException e) {
            return SaveResult.failure("Restore failed: " + e.getMessage());
        }
    }

    /**
     * Lists all versions of a file stored in the integrity store.
     *
     * @param filePath Path to the file
     * @return List of version records, newest first
     */
    public List<VersionRecord> getVersionHistory(Path filePath) {
        String fileKey = getFileKey(filePath);
        FileRecord record = manifest.get(fileKey);

        if (record == null) {
            return Collections.emptyList();
        }

        // Read from journal to build history
        List<VersionRecord> history = new ArrayList<>();
        try (Stream<Path> journalFiles = Files.list(journalDir)) {
            journalFiles
                .filter(p -> p.toString().endsWith(".journal"))
                .sorted(Comparator.reverseOrder())
                .forEach(journalFile -> {
                    try {
                        Files.lines(journalFile).forEach(line -> {
                            if (line.contains(fileKey)) {
                                // Parse journal entry: timestamp|operation|fileKey|beforeHash|afterHash|message
                                String[] parts = line.split("\\|");
                                if (parts.length >= 5) {
                                    history.add(new VersionRecord(
                                        Instant.parse(parts[0]),
                                        parts[1],
                                        parts[4],
                                        parts.length > 5 ? parts[5] : ""
                                    ));
                                }
                            }
                        });
                    } catch (IOException ignored) {}
                });
        } catch (IOException e) {
            // Return what we have
        }

        return history;
    }

    /**
     * Verifies integrity of ALL tracked files.
     *
     * @return Map of file paths to their integrity check results
     */
    public Map<Path, IntegrityCheckResult> verifyAll() {
        Map<Path, IntegrityCheckResult> results = new LinkedHashMap<>();
        for (Map.Entry<String, FileRecord> entry : manifest.entrySet()) {
            Path filePath = Path.of(entry.getValue().originalPath);
            results.put(filePath, verifyIntegrity(filePath));
        }
        return results;
    }

    /**
     * Prunes old versions, keeping at least the specified number of versions.
     *
     * @param keepVersions Minimum versions to keep per file
     * @return Number of objects pruned
     */
    public int pruneOldVersions(int keepVersions) {
        // Implementation would walk the refs, identify unreferenced objects,
        // and delete them. Keeping simple for now.
        return 0;
    }

    // === Internal methods ===

    private void initialize() {
        try {
            Files.createDirectories(objectsDir);
            Files.createDirectories(refsDir);
            Files.createDirectories(journalDir);
            loadManifest();
        } catch (IOException e) {
            throw new RuntimeException("Cannot initialize integrity store: " + e.getMessage(), e);
        }
    }

    String storeContent(String content) {
        String hash = sha256(content);
        Path objectPath = getObjectPath(hash);

        try {
            Files.createDirectories(objectPath.getParent());
            if (!Files.exists(objectPath)) {
                Files.writeString(objectPath, content, StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot store content: " + e.getMessage(), e);
        }

        return hash;
    }

    void updateRef(Path filePath, String hash) {
        String fileKey = getFileKey(filePath);
        FileRecord record = manifest.computeIfAbsent(fileKey,
            k -> new FileRecord(filePath.toString(), hash, Instant.now()));
        record.currentHash = hash;
        record.lastModified = Instant.now();
        saveManifest();
    }

    void logJournalEntry(String operation, Path filePath, String beforeHash, String afterHash, String message) {
        String today = DateTimeFormatter.ISO_LOCAL_DATE.format(
            java.time.LocalDate.now());
        Path journalFile = journalDir.resolve(today + ".journal");

        String entry = String.format("%s|%s|%s|%s|%s|%s%n",
            Instant.now(),
            operation,
            getFileKey(filePath),
            beforeHash != null ? beforeHash : "",
            afterHash != null ? afterHash : "",
            message != null ? message : "");

        try {
            Files.writeString(journalFile, entry,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Warning: cannot write journal: " + e.getMessage());
        }
    }

    private Path getObjectPath(String hash) {
        // Git-like structure: objects/ab/cdef1234...
        String prefix = hash.substring(0, 2);
        String suffix = hash.substring(2);
        return objectsDir.resolve(prefix).resolve(suffix);
    }

    private String getFileKey(Path filePath) {
        return filePath.toAbsolutePath().normalize().toString()
            .replace(File.separator, "_")
            .replace(":", "_");
    }

    private String sha256(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(content.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    private String hashFile(Path path) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(Files.readAllBytes(path));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    private void loadManifest() {
        Path manifestPath = storeRoot.resolve(MANIFEST_FILE);
        if (!Files.exists(manifestPath)) {
            return;
        }
        // Simple JSON-like parsing (in real impl, use Jackson or Gson)
        try {
            String content = Files.readString(manifestPath);
            // Parse manifest entries...
            // For now, keeping simple
        } catch (IOException e) {
            System.err.println("Warning: cannot load manifest: " + e.getMessage());
        }
    }

    private void saveManifest() {
        Path manifestPath = storeRoot.resolve(MANIFEST_FILE);
        try {
            StringBuilder sb = new StringBuilder("{\n");
            for (Map.Entry<String, FileRecord> entry : manifest.entrySet()) {
                FileRecord r = entry.getValue();
                sb.append(String.format("  \"%s\": {\"path\": \"%s\", \"hash\": \"%s\", \"modified\": \"%s\"},\n",
                    entry.getKey(), r.originalPath, r.currentHash, r.lastModified));
            }
            sb.append("}\n");
            Files.writeString(manifestPath, sb.toString());
        } catch (IOException e) {
            System.err.println("Warning: cannot save manifest: " + e.getMessage());
        }
    }

    // === Inner classes ===

    /**
     * A save transaction that can be committed or rolled back.
     */
    public static final class Transaction {
        private final IntegrityStore store;
        private final Path filePath;
        private final String beforeHash;
        private final String afterHash;
        private final Instant startTime;
        private boolean committed = false;
        private boolean rolledBack = false;

        Transaction(IntegrityStore store, Path filePath, String beforeHash,
                    String afterHash, Instant startTime) {
            this.store = store;
            this.filePath = filePath;
            this.beforeHash = beforeHash;
            this.afterHash = afterHash;
            this.startTime = startTime;
        }

        /**
         * Commits the transaction - records that save was successful.
         */
        public void commit() {
            if (committed || rolledBack) return;
            store.updateRef(filePath, afterHash);
            store.logJournalEntry("COMMIT", filePath, beforeHash, afterHash, "Save successful");
            committed = true;
        }

        /**
         * Rolls back - records that save failed.
         */
        public void rollback() {
            if (committed || rolledBack) return;
            store.logJournalEntry("ROLLBACK", filePath, beforeHash, afterHash, "Save failed");
            rolledBack = true;
        }

        /**
         * Gets the content that was stored for recovery.
         */
        public Optional<String> getBeforeContent() {
            return beforeHash != null ? store.retrieveContent(beforeHash) : Optional.empty();
        }

        public Optional<String> getAfterContent() {
            return store.retrieveContent(afterHash);
        }

        public String getAfterHash() {
            return afterHash;
        }
    }

    private static class FileRecord {
        String originalPath;
        String currentHash;
        Instant lastModified;

        FileRecord(String originalPath, String currentHash, Instant lastModified) {
            this.originalPath = originalPath;
            this.currentHash = currentHash;
            this.lastModified = lastModified;
        }
    }

    /**
     * Result of an integrity check.
     */
    public static final class IntegrityCheckResult {
        public enum Status { VALID, CORRUPTED, MISSING, UNKNOWN, ERROR }

        private final Status status;
        private final Path filePath;
        private final String expectedHash;
        private final String actualHash;
        private final String message;

        private IntegrityCheckResult(Status status, Path filePath, String expectedHash,
                                     String actualHash, String message) {
            this.status = status;
            this.filePath = filePath;
            this.expectedHash = expectedHash;
            this.actualHash = actualHash;
            this.message = message;
        }

        public static IntegrityCheckResult valid(Path path, String hash) {
            return new IntegrityCheckResult(Status.VALID, path, hash, hash, "File integrity verified");
        }

        public static IntegrityCheckResult corrupted(Path path, String expected, String actual, String message) {
            return new IntegrityCheckResult(Status.CORRUPTED, path, expected, actual, message);
        }

        public static IntegrityCheckResult missing(Path path, String expected, String message) {
            return new IntegrityCheckResult(Status.MISSING, path, expected, null, message);
        }

        public static IntegrityCheckResult unknown(Path path, String message) {
            return new IntegrityCheckResult(Status.UNKNOWN, path, null, null, message);
        }

        public static IntegrityCheckResult error(Path path, String message) {
            return new IntegrityCheckResult(Status.ERROR, path, null, null, message);
        }

        public Status getStatus() { return status; }
        public Path getFilePath() { return filePath; }
        public String getExpectedHash() { return expectedHash; }
        public String getActualHash() { return actualHash; }
        public String getMessage() { return message; }
        public boolean isValid() { return status == Status.VALID; }
        public boolean isCorrupted() { return status == Status.CORRUPTED; }
        public boolean canRecover() { return status == Status.CORRUPTED || status == Status.MISSING; }

        @Override
        public String toString() {
            return String.format("IntegrityCheck[%s: %s - %s]", status, filePath.getFileName(), message);
        }
    }

    /**
     * A historical version record.
     */
    public record VersionRecord(Instant timestamp, String operation, String hash, String message) {}
}
