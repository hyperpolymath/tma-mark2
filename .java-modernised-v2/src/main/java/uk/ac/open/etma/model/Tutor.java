// SPDX-License-Identifier: MPL-2.0
package uk.ac.open.etma.model;

import java.util.Objects;
import java.util.Optional;

/**
 * Represents an Open University tutor (Associate Lecturer).
 *
 * <p>Contains tutor identification and contact details used for
 * correspondence with students and the OU systems.</p>
 *
 * @param staffId OU staff identifier
 * @param forenames Tutor's first name(s)
 * @param surname Tutor's family name
 * @param initials Tutor's initials (used in feedback)
 * @param emailAddress Tutor's email address
 * @param smtpServer SMTP server for sending emails (if custom)
 */
public record Tutor(
    String staffId,
    String forenames,
    String surname,
    String initials,
    String emailAddress,
    String smtpServer
) {
    /**
     * Compact constructor with validation.
     */
    public Tutor {
        Objects.requireNonNull(forenames, "Forenames cannot be null");
        Objects.requireNonNull(surname, "Surname cannot be null");

        // Derive initials if not provided
        if (initials == null || initials.isBlank()) {
            initials = deriveInitials(forenames, surname);
        }
    }

    /**
     * Create a Tutor with minimal required fields.
     */
    public static Tutor of(String forenames, String surname, String email) {
        return new Tutor(null, forenames, surname, null, email, null);
    }

    /**
     * Returns the tutor's full name.
     */
    public String fullName() {
        return forenames + " " + surname;
    }

    /**
     * Returns the tutor's display name for signatures.
     */
    public String signatureName() {
        return forenames + " " + surname;
    }

    /**
     * Returns the email address if present.
     */
    public Optional<String> email() {
        return Optional.ofNullable(emailAddress);
    }

    /**
     * Returns the SMTP server, defaulting to OU's server if not set.
     */
    public String effectiveSmtpServer() {
        return smtpServer != null && !smtpServer.isBlank()
            ? smtpServer
            : "smtp.open.ac.uk";
    }

    private static String deriveInitials(String forenames, String surname) {
        var sb = new StringBuilder();
        if (forenames != null && !forenames.isBlank()) {
            for (String part : forenames.split("\\s+")) {
                if (!part.isEmpty()) {
                    sb.append(Character.toUpperCase(part.charAt(0)));
                }
            }
        }
        if (surname != null && !surname.isBlank()) {
            sb.append(Character.toUpperCase(surname.charAt(0)));
        }
        return sb.toString();
    }
}
