// SPDX-FileCopyrightText: 2024 eTMA Handler Contributors
// SPDX-License-Identifier: MIT
package uk.ac.open.etma.service;

import uk.ac.open.etma.util.SaveResult;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Safe file operations with atomic writes, verification, and backup.
 *
 * <p>This service ensures data integrity by:
 * <ul>
 *   <li>Writing to temp file first, then atomic rename</li>
 *   <li>Verifying content after write by re-reading</li>
 *   <li>Creating backups before overwriting</li>
 *   <li>Never silently failing - always returns explicit result</li>
 *   <li>Validating XML structure for .fhi files</li>
 * </ul>
 *
 * <p>Fixes critical bugs from original eTMA Handler:
 * <ul>
 *   <li>BUG 1: Original created empty file first, then wrote - if write failed, empty file remained</li>
 *   <li>BUG 2: Exceptions silently swallowed, only printed to console</li>
 *   <li>BUG 3: No verification that write succeeded</li>
 *   <li>BUG 4: No backup before overwrite</li>
 * </ul>
 *
 * @see SaveResult
 */
public final class SafeFileService {

    private static final Charset FHI_CHARSET = Charset.forName("ISO-8859-1");
    private static final String TEMP_SUFFIX = ".tmp";
    private static final String BACKUP_SUFFIX = ".bak";
    private static final String CORRUPTED_SUFFIX = ".corrupted.bak";

    private final Path backupDirectory;
    private final Consumer<String> errorNotifier;

    /**
     * Creates a SafeFileService with default backup directory and System.err notifier.
     */
    public SafeFileService() {
        this(null, System.err::println);
    }

    /**
     * Creates a SafeFileService with custom backup directory and error notifier.
     *
     * @param backupDirectory Directory for storing backups, or null for same-directory backups
     * @param errorNotifier Consumer to receive error messages (for UI notification)
     */
    public SafeFileService(Path backupDirectory, Consumer<String> errorNotifier) {
        this.backupDirectory = backupDirectory;
        this.errorNotifier = errorNotifier != null ? errorNotifier : (s) -> {};
    }

    /**
     * Safely saves content to a file with atomic write and verification.
     *
     * <p>Process:
     * <ol>
     *   <li>Create backup of existing file (if any)</li>
     *   <li>Write content to temp file</li>
     *   <li>Validate temp file (for XML: parse to verify well-formed)</li>
     *   <li>Atomic rename temp → target</li>
     *   <li>Re-read target and verify content matches</li>
     *   <li>Return success only if ALL steps complete</li>
     * </ol>
     *
     * @param targetPath The file to write to
     * @param content The content to write
     * @param charset The character encoding to use
     * @return SaveResult indicating success or failure with details
     */
    public SaveResult saveWithVerification(Path targetPath, String content, Charset charset) {
        if (targetPath == null) {
            return SaveResult.failure("Target path is null");
        }
        if (content == null) {
            return SaveResult.failure("Content is null");
        }

        Path tempPath = getTempPath(targetPath);
        String originalHash = null;

        try {
            // Step 1: Backup existing file if present
            if (Files.exists(targetPath)) {
                SaveResult backupResult = createBackup(targetPath);
                if (!backupResult.isSuccess()) {
                    return SaveResult.failure("Failed to create backup: " + backupResult.getMessage());
                }
                originalHash = hashFile(targetPath);
            }

            // Step 2: Write to temp file (NOT target!)
            try {
                Files.writeString(tempPath, content, charset,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);
            } catch (IOException e) {
                cleanupTempFile(tempPath);
                return SaveResult.failure("Failed to write temp file: " + e.getMessage());
            }

            // Step 3: Validate temp file
            if (targetPath.toString().endsWith(".fhi")) {
                SaveResult validationResult = validateXml(tempPath, charset);
                if (!validationResult.isSuccess()) {
                    cleanupTempFile(tempPath);
                    return SaveResult.failure("XML validation failed: " + validationResult.getMessage());
                }
            }

            // Step 4: Verify temp file content matches what we intended to write
            String tempContent;
            try {
                tempContent = Files.readString(tempPath, charset);
            } catch (IOException e) {
                cleanupTempFile(tempPath);
                return SaveResult.failure("Failed to verify temp file: " + e.getMessage());
            }

            if (!content.equals(tempContent)) {
                cleanupTempFile(tempPath);
                return SaveResult.failure("Content verification failed: written content does not match");
            }

            // Step 5: Atomic rename temp → target
            try {
                Files.move(tempPath, targetPath,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                // Fallback for filesystems that don't support atomic move
                try {
                    Files.move(tempPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e2) {
                    cleanupTempFile(tempPath);
                    return SaveResult.failure("Failed to move temp to target: " + e2.getMessage());
                }
            } catch (IOException e) {
                cleanupTempFile(tempPath);
                return SaveResult.failure("Failed atomic move: " + e.getMessage());
            }

            // Step 6: Final verification - re-read target
            String finalContent;
            try {
                finalContent = Files.readString(targetPath, charset);
            } catch (IOException e) {
                return SaveResult.failure("Failed to verify final file: " + e.getMessage());
            }

            if (!content.equals(finalContent)) {
                return SaveResult.failure("Final verification failed: saved content does not match");
            }

            // Step 7: Compute final hash for audit
            String finalHash = hashFile(targetPath);

            return SaveResult.success(targetPath, content.length(), finalHash);

        } catch (Exception e) {
            cleanupTempFile(tempPath);
            String msg = "Unexpected error during save: " + e.getClass().getSimpleName() + ": " + e.getMessage();
            errorNotifier.accept(msg);
            return SaveResult.failure(msg);
        }
    }

    /**
     * Saves FHI file content using ISO-8859-1 encoding (required for eTMA format).
     *
     * @param targetPath The .fhi file to write
     * @param content The XML content
     * @return SaveResult indicating success or failure
     */
    public SaveResult saveFhi(Path targetPath, String content) {
        return saveWithVerification(targetPath, content, FHI_CHARSET);
    }

    /**
     * Saves UTF-8 encoded content.
     *
     * @param targetPath The file to write
     * @param content The content
     * @return SaveResult indicating success or failure
     */
    public SaveResult saveUtf8(Path targetPath, String content) {
        return saveWithVerification(targetPath, content, StandardCharsets.UTF_8);
    }

    /**
     * Creates a backup of the specified file.
     *
     * @param sourcePath The file to back up
     * @return SaveResult indicating success or failure
     */
    public SaveResult createBackup(Path sourcePath) {
        if (!Files.exists(sourcePath)) {
            return SaveResult.success(sourcePath, 0, null); // Nothing to backup
        }

        try {
            Path backupPath = getBackupPath(sourcePath);

            // Ensure backup directory exists
            Path backupDir = backupPath.getParent();
            if (backupDir != null && !Files.exists(backupDir)) {
                Files.createDirectories(backupDir);
            }

            Files.copy(sourcePath, backupPath, StandardCopyOption.REPLACE_EXISTING);

            return SaveResult.success(backupPath, Files.size(backupPath), hashFile(backupPath));
        } catch (IOException e) {
            return SaveResult.failure("Backup failed: " + e.getMessage());
        }
    }

    /**
     * Validates that an XML file is well-formed.
     *
     * @param xmlPath Path to XML file
     * @param charset Character encoding
     * @return SaveResult indicating valid or invalid with error details
     */
    public SaveResult validateXml(Path xmlPath, Charset charset) {
        try {
            String content = Files.readString(xmlPath, charset);
            return validateXmlContent(content);
        } catch (IOException e) {
            return SaveResult.failure("Cannot read XML file: " + e.getMessage());
        }
    }

    /**
     * Validates XML content string is well-formed.
     *
     * @param xmlContent The XML string to validate
     * @return SaveResult indicating valid or invalid
     */
    public SaveResult validateXmlContent(String xmlContent) {
        if (xmlContent == null || xmlContent.isBlank()) {
            return SaveResult.failure("XML content is empty");
        }

        try {
            javax.xml.parsers.DocumentBuilderFactory factory =
                javax.xml.parsers.DocumentBuilderFactory.newInstance();
            // Disable external entities for security
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

            javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new org.xml.sax.helpers.DefaultHandler() {
                @Override
                public void error(org.xml.sax.SAXParseException e) throws org.xml.sax.SAXException {
                    throw e;
                }
                @Override
                public void fatalError(org.xml.sax.SAXParseException e) throws org.xml.sax.SAXException {
                    throw e;
                }
            });

            builder.parse(new org.xml.sax.InputSource(new StringReader(xmlContent)));
            return SaveResult.success(null, xmlContent.length(), null);

        } catch (javax.xml.parsers.ParserConfigurationException e) {
            return SaveResult.failure("XML parser configuration error: " + e.getMessage());
        } catch (org.xml.sax.SAXParseException e) {
            return SaveResult.failure(String.format(
                "XML parse error at line %d, column %d: %s",
                e.getLineNumber(), e.getColumnNumber(), e.getMessage()));
        } catch (org.xml.sax.SAXException e) {
            return SaveResult.failure("XML validation error: " + e.getMessage());
        } catch (IOException e) {
            return SaveResult.failure("XML read error: " + e.getMessage());
        }
    }

    /**
     * Attempts to repair common XML corruption patterns.
     *
     * @param corruptedContent The corrupted XML string
     * @return Optional containing repaired content, or empty if repair failed
     */
    public Optional<String> attemptXmlRepair(String corruptedContent) {
        if (corruptedContent == null) {
            return Optional.empty();
        }

        String repaired = corruptedContent;

        // Fix pattern: <tagname<nextag  → <tagname><nexttag
        // This is the exact corruption we found in the real incident
        repaired = repaired.replaceAll("<([a-z_]+)<", "<$1><");

        // Fix pattern: </tagname<nexttag → </tagname><nexttag
        repaired = repaired.replaceAll("</([a-z_]+)<", "</$1><");

        // Validate the repair worked
        SaveResult validation = validateXmlContent(repaired);
        if (validation.isSuccess()) {
            return Optional.of(repaired);
        }

        return Optional.empty();
    }

    /**
     * Saves corrupted file with .corrupted.bak extension for forensics.
     *
     * @param corruptedPath Path to the corrupted file
     * @return SaveResult indicating success or failure
     */
    public SaveResult preserveCorruptedFile(Path corruptedPath) {
        if (!Files.exists(corruptedPath)) {
            return SaveResult.failure("Corrupted file does not exist");
        }

        try {
            String timestamp = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
            Path preservedPath = corruptedPath.resolveSibling(
                corruptedPath.getFileName() + "." + timestamp + CORRUPTED_SUFFIX);

            Files.copy(corruptedPath, preservedPath);
            return SaveResult.success(preservedPath, Files.size(preservedPath), null);
        } catch (IOException e) {
            return SaveResult.failure("Failed to preserve corrupted file: " + e.getMessage());
        }
    }

    /**
     * Checks if a file is locked by another process.
     *
     * @param path The file to check
     * @return true if file appears to be locked
     */
    public boolean isFileLocked(Path path) {
        if (!Files.exists(path)) {
            return false;
        }

        // Try to get exclusive lock
        try (FileChannel channel = FileChannel.open(path,
                StandardOpenOption.WRITE, StandardOpenOption.READ)) {
            try (java.nio.channels.FileLock lock = channel.tryLock()) {
                return lock == null; // null means couldn't acquire lock
            }
        } catch (IOException e) {
            // If we can't even open for write, it's probably locked
            return true;
        }
    }

    /**
     * Reads file content safely.
     *
     * @param path File to read
     * @param charset Character encoding
     * @return Optional containing content, or empty on error
     */
    public Optional<String> readSafely(Path path, Charset charset) {
        try {
            return Optional.of(Files.readString(path, charset));
        } catch (IOException e) {
            errorNotifier.accept("Failed to read file: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Reads FHI file content with ISO-8859-1 encoding.
     *
     * @param path FHI file to read
     * @return Optional containing content, or empty on error
     */
    public Optional<String> readFhi(Path path) {
        return readSafely(path, FHI_CHARSET);
    }

    // === Private helper methods ===

    private Path getTempPath(Path targetPath) {
        return targetPath.resolveSibling(targetPath.getFileName() + TEMP_SUFFIX);
    }

    private Path getBackupPath(Path sourcePath) {
        if (backupDirectory != null) {
            String timestamp = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
            return backupDirectory.resolve(
                sourcePath.getFileName() + "." + timestamp + BACKUP_SUFFIX);
        } else {
            return sourcePath.resolveSibling(sourcePath.getFileName() + BACKUP_SUFFIX);
        }
    }

    private void cleanupTempFile(Path tempPath) {
        try {
            Files.deleteIfExists(tempPath);
        } catch (IOException e) {
            // Best effort cleanup
            errorNotifier.accept("Warning: could not clean up temp file: " + tempPath);
        }
    }

    private String hashFile(Path path) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(Files.readAllBytes(path));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException | IOException e) {
            return null;
        }
    }
}
