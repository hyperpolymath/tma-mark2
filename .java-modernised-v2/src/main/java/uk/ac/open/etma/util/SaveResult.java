// SPDX-FileCopyrightText: 2024 eTMA Handler Contributors
// SPDX-License-Identifier: MIT
package uk.ac.open.etma.util;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * Immutable result of a save operation.
 *
 * <p>Unlike the original eTMA Handler which silently failed and set
 * {@code savedFlag = true} regardless of actual outcome, this class
 * provides explicit success/failure status with detailed information.
 *
 * <p>Usage:
 * <pre>{@code
 * SaveResult result = safeFileService.saveFhi(path, content);
 * if (result.isSuccess()) {
 *     showMessage("Saved successfully: " + result.getBytesSaved() + " bytes");
 * } else {
 *     showError("SAVE FAILED: " + result.getMessage());
 *     // User's work is NOT saved - make this VERY clear
 * }
 * }</pre>
 *
 * @see uk.ac.open.etma.service.SafeFileService
 */
public final class SaveResult {

    private final boolean success;
    private final String message;
    private final Path savedPath;
    private final long bytesSaved;
    private final String contentHash;
    private final Instant timestamp;

    private SaveResult(boolean success, String message, Path savedPath,
                       long bytesSaved, String contentHash) {
        this.success = success;
        this.message = message;
        this.savedPath = savedPath;
        this.bytesSaved = bytesSaved;
        this.contentHash = contentHash;
        this.timestamp = Instant.now();
    }

    /**
     * Creates a successful result.
     *
     * @param savedPath Path where content was saved
     * @param bytesSaved Number of bytes written
     * @param contentHash SHA-256 hash of saved content (for verification)
     * @return Successful SaveResult
     */
    public static SaveResult success(Path savedPath, long bytesSaved, String contentHash) {
        return new SaveResult(true, "Save successful", savedPath, bytesSaved, contentHash);
    }

    /**
     * Creates a failure result.
     *
     * <p><strong>IMPORTANT:</strong> When this is returned, the user's work
     * is NOT saved. The UI must make this extremely clear to the user.
     *
     * @param message Detailed error message explaining what went wrong
     * @return Failed SaveResult
     */
    public static SaveResult failure(String message) {
        Objects.requireNonNull(message, "Failure message cannot be null");
        return new SaveResult(false, message, null, 0, null);
    }

    /**
     * Returns whether the save operation succeeded.
     *
     * @return true if save was successful, false otherwise
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Returns whether the save operation failed.
     *
     * @return true if save failed, false if successful
     */
    public boolean isFailure() {
        return !success;
    }

    /**
     * Returns the result message.
     *
     * <p>For failures, this contains detailed error information.
     * For successes, this is a simple confirmation.
     *
     * @return The result message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the path where content was saved.
     *
     * @return Optional containing path if successful, empty if failed
     */
    public Optional<Path> getSavedPath() {
        return Optional.ofNullable(savedPath);
    }

    /**
     * Returns the number of bytes saved.
     *
     * @return Bytes saved if successful, 0 if failed
     */
    public long getBytesSaved() {
        return bytesSaved;
    }

    /**
     * Returns the SHA-256 hash of saved content.
     *
     * <p>Can be used to verify content integrity later.
     *
     * @return Optional containing hash if available
     */
    public Optional<String> getContentHash() {
        return Optional.ofNullable(contentHash);
    }

    /**
     * Returns when this result was created.
     *
     * @return Timestamp of the save operation
     */
    public Instant getTimestamp() {
        return timestamp;
    }

    /**
     * Throws an exception if this result represents a failure.
     *
     * <p>Useful for fail-fast scenarios:
     * <pre>{@code
     * safeFileService.saveFhi(path, content).orElseThrow();
     * }</pre>
     *
     * @return this SaveResult if successful
     * @throws SaveFailedException if the save failed
     */
    public SaveResult orElseThrow() {
        if (!success) {
            throw new SaveFailedException(message);
        }
        return this;
    }

    /**
     * Executes action if save failed.
     *
     * @param action Action to execute with failure message
     * @return this SaveResult for chaining
     */
    public SaveResult onFailure(java.util.function.Consumer<String> action) {
        if (!success) {
            action.accept(message);
        }
        return this;
    }

    /**
     * Executes action if save succeeded.
     *
     * @param action Action to execute with saved path
     * @return this SaveResult for chaining
     */
    public SaveResult onSuccess(java.util.function.Consumer<Path> action) {
        if (success && savedPath != null) {
            action.accept(savedPath);
        }
        return this;
    }

    @Override
    public String toString() {
        if (success) {
            return String.format("SaveResult[SUCCESS, path=%s, bytes=%d, hash=%s]",
                savedPath, bytesSaved, contentHash != null ? contentHash.substring(0, 16) + "..." : "null");
        } else {
            return String.format("SaveResult[FAILURE: %s]", message);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SaveResult that)) return false;
        return success == that.success &&
               bytesSaved == that.bytesSaved &&
               Objects.equals(message, that.message) &&
               Objects.equals(savedPath, that.savedPath) &&
               Objects.equals(contentHash, that.contentHash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(success, message, savedPath, bytesSaved, contentHash);
    }

    /**
     * Exception thrown when a save operation fails and caller used orElseThrow().
     */
    public static class SaveFailedException extends RuntimeException {
        public SaveFailedException(String message) {
            super(message);
        }
    }
}
