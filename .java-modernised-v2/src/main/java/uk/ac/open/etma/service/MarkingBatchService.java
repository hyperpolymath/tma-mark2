// SPDX-License-Identifier: MPL-2.0
package uk.ac.open.etma.service;

import uk.ac.open.etma.model.Submission;
import uk.ac.open.etma.model.Student;
import uk.ac.open.etma.model.Course;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * SKETCH: Service for batch marking operations.
 *
 * <p>Demonstrates Java 21+ virtual threads for concurrent operations
 * without the complexity of traditional thread pools.</p>
 *
 * <p>Ideas from IDEAS.md:</p>
 * <ul>
 *   <li>#6 Marking Playlist - queue management</li>
 *   <li>#7 Consistency Guardian - tracking feedback depth</li>
 *   <li>#10 Return Roulette Prevention - batch release</li>
 * </ul>
 */
public class MarkingBatchService {

    /**
     * Batch statistics for the Consistency Guardian feature.
     */
    public record BatchStats(
        int total,
        int completed,
        int inProgress,
        Duration totalTime,
        Duration averageTime,
        int averageFeedbackLength,
        int shortestFeedback,
        int longestFeedback
    ) {
        public double completionPercentage() {
            return total > 0 ? (completed * 100.0) / total : 0;
        }

        public boolean hasConsistencyWarning() {
            // Warn if feedback length varies by more than 50%
            return longestFeedback > 0 &&
                shortestFeedback < (longestFeedback * 0.5);
        }
    }

    /**
     * Marking session with progress tracking.
     */
    public static class MarkingSession implements AutoCloseable {
        private final List<Submission> queue;
        private final ConcurrentMap<String, FeedbackMetrics> metrics;
        private final LocalDateTime startTime;
        private volatile boolean cancelled;
        private Consumer<Progress> progressListener;

        public record Progress(
            int current,
            int total,
            String currentStudentPi,
            Duration elapsed,
            Duration estimatedRemaining
        ) {}

        public record FeedbackMetrics(
            int wordCount,
            Duration timeSpent,
            LocalDateTime completedAt
        ) {}

        MarkingSession(List<Submission> submissions) {
            this.queue = new CopyOnWriteArrayList<>(submissions);
            this.metrics = new ConcurrentHashMap<>();
            this.startTime = LocalDateTime.now();
        }

        public void onProgress(Consumer<Progress> listener) {
            this.progressListener = listener;
        }

        /**
         * Get next submission to mark.
         */
        public Submission next() {
            return queue.stream()
                .filter(s -> s.status() instanceof Submission.Status.Pending)
                .findFirst()
                .map(Submission::startMarking)
                .orElse(null);
        }

        /**
         * Record completion of a marking.
         */
        public void recordCompletion(String studentPi, int feedbackWordCount, Duration timeSpent) {
            metrics.put(studentPi, new FeedbackMetrics(feedbackWordCount, timeSpent, LocalDateTime.now()));

            // Check for consistency warnings
            if (metrics.size() > 3) {
                var avg = averageFeedbackLength();
                if (feedbackWordCount < avg * 0.5) {
                    // This is where we'd trigger the Consistency Guardian warning
                    System.err.println("⚠ Consistency warning: Feedback for " + studentPi +
                        " is much shorter than average (" + feedbackWordCount + " vs " + avg + " words)");
                }
            }

            notifyProgress();
        }

        private int averageFeedbackLength() {
            return (int) metrics.values().stream()
                .mapToInt(FeedbackMetrics::wordCount)
                .average()
                .orElse(0);
        }

        private void notifyProgress() {
            if (progressListener == null) return;

            int completed = metrics.size();
            int total = queue.size();
            var elapsed = Duration.between(startTime, LocalDateTime.now());
            var avgPerItem = completed > 0 ? elapsed.dividedBy(completed) : Duration.ZERO;
            var remaining = avgPerItem.multipliedBy(total - completed);

            var currentPi = queue.stream()
                .filter(s -> s.status() instanceof Submission.Status.InProgress)
                .map(s -> s.student().piNumber())
                .findFirst()
                .orElse("none");

            progressListener.accept(new Progress(completed, total, currentPi, elapsed, remaining));
        }

        public BatchStats getStats() {
            var feedbackLengths = metrics.values().stream()
                .mapToInt(FeedbackMetrics::wordCount)
                .toArray();

            var totalTime = Duration.between(startTime, LocalDateTime.now());
            var avgTime = metrics.isEmpty() ? Duration.ZERO :
                totalTime.dividedBy(metrics.size());

            return new BatchStats(
                queue.size(),
                metrics.size(),
                (int) queue.stream()
                    .filter(s -> s.status() instanceof Submission.Status.InProgress)
                    .count(),
                totalTime,
                avgTime,
                feedbackLengths.length > 0 ?
                    (int) java.util.Arrays.stream(feedbackLengths).average().orElse(0) : 0,
                feedbackLengths.length > 0 ?
                    java.util.Arrays.stream(feedbackLengths).min().orElse(0) : 0,
                feedbackLengths.length > 0 ?
                    java.util.Arrays.stream(feedbackLengths).max().orElse(0) : 0
            );
        }

        public void cancel() {
            cancelled = true;
        }

        @Override
        public void close() {
            // Cleanup resources
        }
    }

    /**
     * SKETCH: Batch return with scheduled release.
     *
     * <p>Uses virtual threads to handle scheduled returns without
     * blocking the main thread pool.</p>
     */
    public CompletableFuture<Void> scheduleReturn(
        List<Submission> markedWork,
        LocalDateTime releaseTime,
        Consumer<Submission> onReturn
    ) {
        return CompletableFuture.runAsync(() -> {
            // Virtual thread - cheap to block
            var delay = Duration.between(LocalDateTime.now(), releaseTime);
            if (delay.isPositive()) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            // Return all at once - fair to students
            markedWork.forEach(onReturn);

        }, Executors.newVirtualThreadPerTaskExecutor());
    }

    /**
     * SKETCH: Pattern detection across batch.
     *
     * <p>Demonstrates parallel stream processing with virtual threads.</p>
     */
    public record CommonIssue(
        String pattern,
        int occurrences,
        List<String> affectedStudentPis
    ) {}

    public List<CommonIssue> detectCommonIssues(List<Submission> submissions) {
        // In real impl, would analyze feedback text for patterns
        // This is just a sketch showing the structure

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = submissions.stream()
                .filter(s -> s.status() instanceof Submission.Status.Marked)
                .map(s -> CompletableFuture.supplyAsync(
                    () -> extractIssues((Submission.Status.Marked) s.status()),
                    executor
                ))
                .toList();

            // Wait for all analysis to complete
            return futures.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .collect(Collectors.groupingBy(
                    issue -> issue,
                    Collectors.counting()
                ))
                .entrySet().stream()
                .filter(e -> e.getValue() > 1)  // Only issues appearing multiple times
                .map(e -> new CommonIssue(e.getKey(), e.getValue().intValue(), List.of()))
                .toList();
        }
    }

    private List<String> extractIssues(Submission.Status.Marked marked) {
        // Placeholder - would do NLP analysis in real impl
        return List.of();
    }

    /**
     * Create a new marking session.
     */
    public MarkingSession createSession(List<Submission> submissions) {
        return new MarkingSession(submissions);
    }
}
