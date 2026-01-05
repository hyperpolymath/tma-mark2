// SPDX-License-Identifier: MPL-2.0
package uk.ac.open.etma.model;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a student TMA submission.
 *
 * <p>SKETCH: Demonstrates Java 21+ sealed classes for type-safe state machine.</p>
 *
 * @param student The student who submitted
 * @param course The course this submission is for
 * @param tmaNumber TMA number (e.g., 01, 02, 03)
 * @param submittedAt When the student submitted
 * @param filePath Path to the submission file
 * @param status Current marking status
 */
public record Submission(
    Student student,
    Course course,
    String tmaNumber,
    LocalDateTime submittedAt,
    Path filePath,
    Status status
) {
    public Submission {
        Objects.requireNonNull(student, "Student cannot be null");
        Objects.requireNonNull(course, "Course cannot be null");
        Objects.requireNonNull(tmaNumber, "TMA number cannot be null");
        Objects.requireNonNull(status, "Status cannot be null");
    }

    /**
     * SKETCH: Sealed hierarchy - compiler knows all subtypes.
     * Modern Java pattern for type-safe state machines.
     */
    public sealed interface Status permits
        Status.Pending,
        Status.InProgress,
        Status.Marked,
        Status.Returned,
        Status.Problem {

        // Pattern matching friendly methods
        default boolean isComplete() {
            return this instanceof Marked || this instanceof Returned;
        }

        default boolean needsAttention() {
            return this instanceof Problem;
        }

        // Concrete states as records
        record Pending() implements Status {}

        record InProgress(LocalDateTime startedAt, String notes) implements Status {
            public InProgress(LocalDateTime startedAt) {
                this(startedAt, null);
            }
        }

        record Marked(
            int score,
            int maxScore,
            LocalDateTime markedAt,
            String feedback
        ) implements Status {
            public double percentage() {
                return maxScore > 0 ? (score * 100.0) / maxScore : 0;
            }

            public String grade() {
                double pct = percentage();
                return switch ((int) pct / 10) {
                    case 10, 9 -> "Distinction";
                    case 8, 7 -> "Pass 2";
                    case 6, 5 -> "Pass 3";
                    case 4 -> "Pass 4";
                    default -> "Fail";
                };
            }
        }

        record Returned(Marked marking, LocalDateTime returnedAt) implements Status {}

        record Problem(String reason, LocalDateTime occurredAt) implements Status {}
    }

    /**
     * SKETCH: Pattern matching on sealed types.
     * Compiler enforces exhaustive handling.
     */
    public String statusDescription() {
        return switch (status) {
            case Status.Pending() -> "Awaiting marking";
            case Status.InProgress(var started, var notes) ->
                "In progress since " + started.toLocalDate() +
                (notes != null ? " (" + notes + ")" : "");
            case Status.Marked(var score, var max, _, var feedback) ->
                "Marked: %d/%d (%s)".formatted(score, max,
                    ((Status.Marked) status).grade());
            case Status.Returned(var marking, var returned) ->
                "Returned on " + returned.toLocalDate() +
                " - " + marking.grade();
            case Status.Problem(var reason, _) ->
                "⚠ Problem: " + reason;
        };
    }

    /**
     * SKETCH: State transitions as methods returning new immutable instances.
     */
    public Submission startMarking() {
        if (!(status instanceof Status.Pending)) {
            throw new IllegalStateException("Can only start marking pending submissions");
        }
        return new Submission(student, course, tmaNumber, submittedAt, filePath,
            new Status.InProgress(LocalDateTime.now()));
    }

    public Submission completeMark(int score, int maxScore, String feedback) {
        if (!(status instanceof Status.InProgress)) {
            throw new IllegalStateException("Can only complete marking for in-progress submissions");
        }
        return new Submission(student, course, tmaNumber, submittedAt, filePath,
            new Status.Marked(score, maxScore, LocalDateTime.now(), feedback));
    }

    public Submission returnToStudent() {
        if (!(status instanceof Status.Marked marked)) {
            throw new IllegalStateException("Can only return marked submissions");
        }
        return new Submission(student, course, tmaNumber, submittedAt, filePath,
            new Status.Returned(marked, LocalDateTime.now()));
    }

    public Submission flagProblem(String reason) {
        return new Submission(student, course, tmaNumber, submittedAt, filePath,
            new Status.Problem(reason, LocalDateTime.now()));
    }

    /**
     * File name for the submission (e.g., "TM129-24J-TMA01-A1234567").
     */
    public String fileName() {
        return "%s-TMA%s-%s".formatted(
            course.code(),
            tmaNumber,
            student.piNumber()
        );
    }
}
