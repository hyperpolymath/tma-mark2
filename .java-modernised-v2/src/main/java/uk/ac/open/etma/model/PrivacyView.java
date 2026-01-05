// SPDX-License-Identifier: MPL-2.0
package uk.ac.open.etma.model;

import java.time.LocalDateTime;
import java.util.function.Function;

/**
 * SKETCH: Privacy/PII redaction wrapper.
 *
 * <p>Demonstrates how the Privacy Mode feature from IDEAS.md could work.
 * Wraps a Student and controls what information is visible.</p>
 *
 * <p>Key ideas from IDEAS.md #32:</p>
 * <ul>
 *   <li>Default to PI-only view</li>
 *   <li>Reveal on demand with audit</li>
 *   <li>Time-limited reveals</li>
 *   <li>Export controls</li>
 * </ul>
 */
public class PrivacyView {

    /**
     * Privacy level controls what's visible.
     */
    public enum Level {
        /** Only PI number visible - safest for screen sharing */
        PI_ONLY,
        /** PI + name visible - for normal marking */
        IDENTIFIED,
        /** Everything visible - for correspondence */
        FULL
    }

    private final Student student;
    private Level currentLevel;
    private LocalDateTime revealExpiry;

    // Audit callback - could log to file in real impl
    private final AuditLogger auditLogger;

    @FunctionalInterface
    public interface AuditLogger {
        void log(String action, String studentPi, Level level, String reason);
    }

    public PrivacyView(Student student, AuditLogger auditLogger) {
        this.student = student;
        this.auditLogger = auditLogger;
        this.currentLevel = Level.PI_ONLY;  // Safe default
    }

    /**
     * Get the PI number - always visible.
     */
    public String piNumber() {
        return student.piNumber();
    }

    /**
     * Get name - redacted unless level permits.
     */
    public String name() {
        return switch (effectiveLevel()) {
            case PI_ONLY -> "Student [" + student.piNumber() + "]";
            case IDENTIFIED, FULL -> student.fullName();
        };
    }

    /**
     * Get display name for lists - respects privacy.
     */
    public String displayName() {
        return switch (effectiveLevel()) {
            case PI_ONLY -> "[" + student.piNumber() + "]";
            case IDENTIFIED -> student.surname() + ", " + firstInitial() + ". (" + student.piNumber() + ")";
            case FULL -> student.displayName();
        };
    }

    /**
     * Get email - only visible at FULL level.
     */
    public String email() {
        return switch (effectiveLevel()) {
            case PI_ONLY, IDENTIFIED -> "[email redacted]";
            case FULL -> student.emailAddress() != null
                ? student.emailAddress()
                : "[no email on file]";
        };
    }

    /**
     * Get address - only visible at FULL level.
     */
    public String address() {
        return switch (effectiveLevel()) {
            case PI_ONLY, IDENTIFIED -> "[address redacted]";
            case FULL -> student.formattedAddress();
        };
    }

    /**
     * Temporarily reveal more information.
     *
     * @param level Target level
     * @param reason Why (for audit log)
     * @param durationMinutes How long the reveal lasts
     */
    public void reveal(Level level, String reason, int durationMinutes) {
        if (level.ordinal() <= currentLevel.ordinal()) {
            return; // Already at this level or higher
        }

        auditLogger.log("REVEAL", student.piNumber(), level, reason);
        this.currentLevel = level;
        this.revealExpiry = LocalDateTime.now().plusMinutes(durationMinutes);
    }

    /**
     * Revoke any temporary reveals.
     */
    public void revokeReveal() {
        if (currentLevel != Level.PI_ONLY) {
            auditLogger.log("REVOKE", student.piNumber(), Level.PI_ONLY, "Manual revocation");
            currentLevel = Level.PI_ONLY;
            revealExpiry = null;
        }
    }

    /**
     * Check if reveal has expired and reset if needed.
     */
    private Level effectiveLevel() {
        if (revealExpiry != null && LocalDateTime.now().isAfter(revealExpiry)) {
            auditLogger.log("EXPIRE", student.piNumber(), Level.PI_ONLY, "Reveal expired");
            currentLevel = Level.PI_ONLY;
            revealExpiry = null;
        }
        return currentLevel;
    }

    /**
     * Get underlying student for full access (requires audit).
     */
    public Student unwrap(String reason) {
        auditLogger.log("UNWRAP", student.piNumber(), Level.FULL, reason);
        return student;
    }

    /**
     * Apply a function to the student data without exposing it.
     * Useful for operations that need full data but shouldn't leak it.
     */
    public <T> T withStudent(Function<Student, T> operation, String reason) {
        auditLogger.log("ACCESS", student.piNumber(), Level.FULL, reason);
        return operation.apply(student);
    }

    private String firstInitial() {
        String forenames = student.forenames();
        return forenames != null && !forenames.isEmpty()
            ? String.valueOf(forenames.charAt(0))
            : "?";
    }

    /**
     * SKETCH: Export with redaction level.
     */
    public record ExportData(
        String pi,
        String name,
        String email,
        String address,
        Level level
    ) {
        public static ExportData from(PrivacyView view) {
            return new ExportData(
                view.piNumber(),
                view.name(),
                view.email(),
                view.address(),
                view.effectiveLevel()
            );
        }
    }
}
