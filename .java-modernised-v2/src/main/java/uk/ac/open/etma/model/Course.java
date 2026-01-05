// SPDX-License-Identifier: MPL-2.0
package uk.ac.open.etma.model;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Represents an Open University course/module.
 *
 * <p>OU course codes follow a pattern like "DD102-24J" where:
 * <ul>
 *   <li>DD102 = Module code</li>
 *   <li>24 = Year (2024)</li>
 *   <li>J = Presentation (October start)</li>
 * </ul></p>
 *
 * @param code Full course code (e.g., "DD102-24J")
 * @param moduleCode Module code without presentation (e.g., "DD102")
 * @param version Course version number
 * @param year Academic year
 * @param presentation Presentation code (J=October, B=February, etc.)
 * @param title Human-readable course title
 */
public record Course(
    String code,
    String moduleCode,
    String version,
    int year,
    String presentation,
    String title
) {
    private static final Pattern COURSE_PATTERN =
        Pattern.compile("^([A-Z]{1,4}\\d{2,4})-(\\d{2})([A-Z])$");

    /**
     * Compact constructor with validation.
     */
    public Course {
        Objects.requireNonNull(code, "Course code cannot be null");

        // Parse the code if module details not provided
        if (moduleCode == null) {
            var parsed = parse(code);
            moduleCode = parsed.moduleCode();
            year = parsed.year();
            presentation = parsed.presentation();
        }
    }

    /**
     * Parse a course code string into a Course record.
     *
     * @param code Course code like "DD102-24J"
     * @return Parsed Course record
     * @throws IllegalArgumentException if code doesn't match expected pattern
     */
    public static Course parse(String code) {
        Objects.requireNonNull(code, "Course code cannot be null");

        var matcher = COURSE_PATTERN.matcher(code.toUpperCase().trim());
        if (!matcher.matches()) {
            // Return with code only if pattern doesn't match
            return new Course(code, code, null, 0, null, null);
        }

        var moduleCode = matcher.group(1);
        var yearStr = matcher.group(2);
        var presentation = matcher.group(3);

        // Convert 2-digit year to 4-digit
        int year = Integer.parseInt(yearStr);
        year = year < 50 ? 2000 + year : 1900 + year;

        return new Course(code, moduleCode, null, year, presentation, null);
    }

    /**
     * Create a Course with just the code.
     */
    public static Course of(String code) {
        return parse(code);
    }

    /**
     * Returns the full course code.
     */
    @Override
    public String toString() {
        return code;
    }

    /**
     * Returns the course title if known, otherwise the code.
     */
    public String displayName() {
        return title != null ? title : code;
    }

    /**
     * Returns the title if present.
     */
    public Optional<String> getTitle() {
        return Optional.ofNullable(title);
    }

    /**
     * Returns the presentation name (e.g., "October 2024").
     */
    public String presentationName() {
        if (presentation == null || year == 0) {
            return code;
        }
        String month = switch (presentation) {
            case "J" -> "October";
            case "B" -> "February";
            case "D" -> "May";
            case "K" -> "November";
            default -> presentation;
        };
        return month + " " + year;
    }

    /**
     * Returns the expected folder name for this course's files.
     */
    public String folderName() {
        return code;
    }
}
