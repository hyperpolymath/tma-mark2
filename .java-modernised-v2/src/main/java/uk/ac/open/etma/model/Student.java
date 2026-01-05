// SPDX-License-Identifier: MPL-2.0
package uk.ac.open.etma.model;

import java.util.Objects;
import java.util.Optional;

/**
 * Represents a student enrolled in an Open University module.
 *
 * <p>This is an immutable record containing all student identification
 * and contact details extracted from eTMA submissions.</p>
 *
 * @param piNumber The Personal Identifier - unique OU student ID
 * @param forenames Student's first name(s)
 * @param surname Student's family name
 * @param emailAddress Student's email (usually @open.ac.uk)
 * @param addressLine1 First line of postal address
 * @param addressLine2 Second line of postal address
 * @param addressLine3 Third line of postal address
 * @param addressLine4 Fourth line of postal address
 * @param addressLine5 Fifth line of postal address (usually postcode)
 */
public record Student(
    String piNumber,
    String forenames,
    String surname,
    String emailAddress,
    String addressLine1,
    String addressLine2,
    String addressLine3,
    String addressLine4,
    String addressLine5
) {
    /**
     * Compact constructor with validation.
     */
    public Student {
        Objects.requireNonNull(piNumber, "PI number cannot be null");
        Objects.requireNonNull(forenames, "Forenames cannot be null");
        Objects.requireNonNull(surname, "Surname cannot be null");

        // Normalize empty strings to null for optional fields
        addressLine1 = normalizeEmpty(addressLine1);
        addressLine2 = normalizeEmpty(addressLine2);
        addressLine3 = normalizeEmpty(addressLine3);
        addressLine4 = normalizeEmpty(addressLine4);
        addressLine5 = normalizeEmpty(addressLine5);
        emailAddress = normalizeEmpty(emailAddress);
    }

    /**
     * Create a Student with minimal required fields.
     */
    public static Student of(String piNumber, String forenames, String surname) {
        return new Student(piNumber, forenames, surname, null, null, null, null, null, null);
    }

    /**
     * Returns the student's full name (forenames + surname).
     */
    public String fullName() {
        return forenames + " " + surname;
    }

    /**
     * Returns the student's display name (suitable for UI lists).
     * Format: "Surname, Forenames (PI)"
     */
    public String displayName() {
        return "%s, %s (%s)".formatted(surname, forenames, piNumber);
    }

    /**
     * Returns the email address if present.
     */
    public Optional<String> email() {
        return Optional.ofNullable(emailAddress);
    }

    /**
     * Returns a formatted postal address.
     */
    public String formattedAddress() {
        var sb = new StringBuilder();
        appendIfPresent(sb, addressLine1);
        appendIfPresent(sb, addressLine2);
        appendIfPresent(sb, addressLine3);
        appendIfPresent(sb, addressLine4);
        appendIfPresent(sb, addressLine5);
        return sb.toString().trim();
    }

    private void appendIfPresent(StringBuilder sb, String line) {
        if (line != null && !line.isBlank()) {
            if (!sb.isEmpty()) {
                sb.append("\n");
            }
            sb.append(line);
        }
    }

    private static String normalizeEmpty(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}
