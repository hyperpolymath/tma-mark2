// SPDX-FileCopyrightText: 2024 eTMA Handler Contributors
// SPDX-License-Identifier: MIT
package uk.ac.open.etma.service;

import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.zip.GZIPOutputStream;

/**
 * Three-tier instrumentation system:
 *
 * <h2>Tier 1: User-Facing</h2>
 * <ul>
 *   <li>Minimal, actionable messages</li>
 *   <li>No jargon, no stack traces</li>
 *   <li>"Save complete" / "Save failed - try again"</li>
 *   <li>Progress indicators for long operations</li>
 * </ul>
 *
 * <h2>Tier 2: Developer</h2>
 * <ul>
 *   <li>Structured logs with levels (DEBUG/INFO/WARN/ERROR)</li>
 *   <li>Correlation IDs to trace operations</li>
 *   <li>Filterable by component, operation, severity</li>
 *   <li>Performance metrics</li>
 * </ul>
 *
 * <h2>Tier 3: Forensic</h2>
 * <ul>
 *   <li>EVERYTHING - complete event stream</li>
 *   <li>Before/after state snapshots</li>
 *   <li>Microsecond timing</li>
 *   <li>Compressed, rotated, never shown live</li>
 *   <li>For post-mortem analysis only</li>
 * </ul>
 *
 * <p>Key principle: <b>Log everything, show almost nothing.</b>
 */
public final class Instrumentation {

    // === Tier 1: User Messages ===

    /**
     * User-facing message types.
     */
    public enum UserMessageType {
        SUCCESS,    // Green checkmark, auto-dismiss
        INFO,       // Blue info, auto-dismiss
        WARNING,    // Yellow warning, requires acknowledgment
        ERROR,      // Red error, requires action
        PROGRESS    // Progress bar/spinner
    }

    /**
     * A message suitable for showing to end users.
     */
    public record UserMessage(
        UserMessageType type,
        String title,           // Short: "Save Complete"
        String detail,          // Optional: "Saved to E225-25J-01-1-zt971869.fhi"
        String action,          // Optional: "Click to retry" or null
        boolean autoDismiss     // Disappear after 3 seconds?
    ) {
        public static UserMessage success(String title) {
            return new UserMessage(UserMessageType.SUCCESS, title, null, null, true);
        }

        public static UserMessage success(String title, String detail) {
            return new UserMessage(UserMessageType.SUCCESS, title, detail, null, true);
        }

        public static UserMessage error(String title, String action) {
            return new UserMessage(UserMessageType.ERROR, title, null, action, false);
        }

        public static UserMessage error(String title, String detail, String action) {
            return new UserMessage(UserMessageType.ERROR, title, detail, action, false);
        }

        public static UserMessage progress(String title) {
            return new UserMessage(UserMessageType.PROGRESS, title, null, null, false);
        }

        public static UserMessage warning(String title, String detail) {
            return new UserMessage(UserMessageType.WARNING, title, detail, null, false);
        }
    }

    // === Tier 2: Developer Logs ===

    public enum LogLevel {
        TRACE(0),   // Extremely verbose, method entry/exit
        DEBUG(1),   // Detailed diagnostic info
        INFO(2),    // Normal operation milestones
        WARN(3),    // Something unexpected but handled
        ERROR(4),   // Something failed
        FATAL(5);   // Unrecoverable

        final int severity;
        LogLevel(int severity) { this.severity = severity; }
    }

    /**
     * Structured log entry for developers.
     */
    public record LogEntry(
        Instant timestamp,
        LogLevel level,
        String correlationId,   // Links related operations
        String component,       // "SafeFileService", "IntegrityStore", etc.
        String operation,       // "save", "validate", "backup"
        String message,
        Map<String, Object> context,  // Structured data
        Throwable exception     // Optional
    ) {
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(DateTimeFormatter.ISO_INSTANT.format(timestamp));
            sb.append(" [").append(level).append("] ");
            sb.append("[").append(correlationId).append("] ");
            sb.append(component).append(".").append(operation).append(": ");
            sb.append(message);
            if (!context.isEmpty()) {
                sb.append(" ").append(context);
            }
            return sb.toString();
        }
    }

    // === Tier 3: Forensic Events ===

    /**
     * Complete event for forensic analysis.
     * Contains EVERYTHING needed to reconstruct what happened.
     */
    public record ForensicEvent(
        long sequenceNumber,        // Global ordering
        Instant timestamp,
        long nanoTime,              // For sub-millisecond timing
        String correlationId,
        String sessionId,           // Links to user session
        String component,
        String operation,
        String phase,               // "start", "success", "failure", "rollback"
        Map<String, Object> before, // State before operation
        Map<String, Object> after,  // State after operation
        Map<String, Object> context,// Additional context
        String stackTrace,          // Where this was called from
        long threadId,
        String threadName
    ) {
        public String toCompactJson() {
            // Minimal JSON for storage efficiency
            return String.format(
                "{\"seq\":%d,\"ts\":\"%s\",\"cid\":\"%s\",\"c\":\"%s\",\"o\":\"%s\",\"p\":\"%s\"}",
                sequenceNumber, timestamp, correlationId, component, operation, phase);
        }
    }

    // === The Instrumentation Instance ===

    private static final Instrumentation INSTANCE = new Instrumentation();

    private final List<Consumer<UserMessage>> userMessageListeners = new CopyOnWriteArrayList<>();
    private final List<Consumer<LogEntry>> logListeners = new CopyOnWriteArrayList<>();
    private final BlockingQueue<ForensicEvent> forensicQueue = new LinkedBlockingQueue<>(100_000);

    private LogLevel minimumLogLevel = LogLevel.INFO;
    private final AtomicLong sequenceCounter = new java.util.concurrent.atomic.AtomicLong(0);
    private final String sessionId = UUID.randomUUID().toString().substring(0, 8);

    private Path forensicLogDir;
    private volatile boolean forensicLoggingEnabled = true;
    private final Thread forensicWriter;

    private Instrumentation() {
        // Start background forensic writer
        forensicWriter = new Thread(this::forensicWriterLoop, "forensic-writer");
        forensicWriter.setDaemon(true);
        forensicWriter.start();

        // Default forensic log location
        forensicLogDir = Path.of(System.getProperty("user.home"), ".etma-logs");
        try {
            Files.createDirectories(forensicLogDir);
        } catch (IOException e) {
            System.err.println("Warning: cannot create forensic log directory: " + e.getMessage());
        }
    }

    public static Instrumentation get() {
        return INSTANCE;
    }

    // === User-Facing API ===

    /**
     * Show a message to the user.
     */
    public void showUser(UserMessage message) {
        for (Consumer<UserMessage> listener : userMessageListeners) {
            try {
                listener.accept(message);
            } catch (Exception e) {
                // Don't let UI errors break instrumentation
            }
        }
    }

    public void showUserSuccess(String title) {
        showUser(UserMessage.success(title));
    }

    public void showUserSuccess(String title, String detail) {
        showUser(UserMessage.success(title, detail));
    }

    public void showUserError(String title, String action) {
        showUser(UserMessage.error(title, action));
    }

    public void showUserProgress(String title) {
        showUser(UserMessage.progress(title));
    }

    /**
     * Register a UI component to receive user messages.
     */
    public void addUserMessageListener(Consumer<UserMessage> listener) {
        userMessageListeners.add(listener);
    }

    // === Developer Logging API ===

    /**
     * Create a logger for a specific component.
     */
    public ComponentLogger logger(String component) {
        return new ComponentLogger(this, component);
    }

    /**
     * Log an entry (internal).
     */
    void log(LogEntry entry) {
        if (entry.level().severity < minimumLogLevel.severity) {
            return;
        }

        for (Consumer<LogEntry> listener : logListeners) {
            try {
                listener.accept(entry);
            } catch (Exception e) {
                // Don't let log errors break anything
            }
        }

        // Also write to stderr for development
        if (entry.level().severity >= LogLevel.WARN.severity) {
            System.err.println(entry);
        }
    }

    /**
     * Register a log listener (for log viewer UI, external logging, etc.)
     */
    public void addLogListener(Consumer<LogEntry> listener) {
        logListeners.add(listener);
    }

    public void setMinimumLogLevel(LogLevel level) {
        this.minimumLogLevel = level;
    }

    // === Forensic API ===

    /**
     * Record a forensic event (for post-mortem analysis).
     */
    public void recordForensic(String component, String operation, String phase,
                                Map<String, Object> before, Map<String, Object> after,
                                Map<String, Object> context) {
        if (!forensicLoggingEnabled) return;

        ForensicEvent event = new ForensicEvent(
            sequenceCounter.incrementAndGet(),
            Instant.now(),
            System.nanoTime(),
            CorrelationContext.current(),
            sessionId,
            component,
            operation,
            phase,
            before != null ? Map.copyOf(before) : Map.of(),
            after != null ? Map.copyOf(after) : Map.of(),
            context != null ? Map.copyOf(context) : Map.of(),
            captureStackTrace(),
            Thread.currentThread().getId(),
            Thread.currentThread().getName()
        );

        // Non-blocking enqueue
        if (!forensicQueue.offer(event)) {
            // Queue full - drop oldest (shouldn't happen often)
            forensicQueue.poll();
            forensicQueue.offer(event);
        }
    }

    /**
     * Convenience for simple forensic events.
     */
    public void forensic(String component, String operation, String phase) {
        recordForensic(component, operation, phase, null, null, null);
    }

    /**
     * Forensic event with context.
     */
    public void forensic(String component, String operation, String phase,
                         Map<String, Object> context) {
        recordForensic(component, operation, phase, null, null, context);
    }

    // === Correlation Context ===

    /**
     * Manages correlation IDs for tracing operations across components.
     */
    public static final class CorrelationContext {
        private static final ThreadLocal<String> CURRENT = ThreadLocal.withInitial(
            () -> UUID.randomUUID().toString().substring(0, 8));

        public static String current() {
            return CURRENT.get();
        }

        public static void set(String id) {
            CURRENT.set(id);
        }

        public static String newOperation() {
            String id = UUID.randomUUID().toString().substring(0, 8);
            CURRENT.set(id);
            return id;
        }

        public static void clear() {
            CURRENT.remove();
        }
    }

    // === Component Logger ===

    /**
     * Logger bound to a specific component.
     */
    public static final class ComponentLogger {
        private final Instrumentation instrumentation;
        private final String component;

        ComponentLogger(Instrumentation instrumentation, String component) {
            this.instrumentation = instrumentation;
            this.component = component;
        }

        public void trace(String operation, String message) {
            log(LogLevel.TRACE, operation, message, Map.of(), null);
        }

        public void debug(String operation, String message) {
            log(LogLevel.DEBUG, operation, message, Map.of(), null);
        }

        public void debug(String operation, String message, Map<String, Object> context) {
            log(LogLevel.DEBUG, operation, message, context, null);
        }

        public void info(String operation, String message) {
            log(LogLevel.INFO, operation, message, Map.of(), null);
        }

        public void info(String operation, String message, Map<String, Object> context) {
            log(LogLevel.INFO, operation, message, context, null);
        }

        public void warn(String operation, String message) {
            log(LogLevel.WARN, operation, message, Map.of(), null);
        }

        public void warn(String operation, String message, Throwable t) {
            log(LogLevel.WARN, operation, message, Map.of(), t);
        }

        public void error(String operation, String message) {
            log(LogLevel.ERROR, operation, message, Map.of(), null);
        }

        public void error(String operation, String message, Throwable t) {
            log(LogLevel.ERROR, operation, message, Map.of(), t);
        }

        public void error(String operation, String message, Map<String, Object> context, Throwable t) {
            log(LogLevel.ERROR, operation, message, context, t);
        }

        private void log(LogLevel level, String operation, String message,
                         Map<String, Object> context, Throwable t) {
            LogEntry entry = new LogEntry(
                Instant.now(),
                level,
                CorrelationContext.current(),
                component,
                operation,
                message,
                context,
                t
            );
            instrumentation.log(entry);

            // Also record forensic for WARN and above
            if (level.severity >= LogLevel.WARN.severity) {
                instrumentation.forensic(component, operation, level.name().toLowerCase(),
                    Map.of("message", message));
            }
        }

        /**
         * Time an operation and log results.
         */
        public <T> T timed(String operation, String description, Callable<T> task) throws Exception {
            long start = System.nanoTime();
            instrumentation.forensic(component, operation, "start");

            try {
                T result = task.call();
                long durationMs = (System.nanoTime() - start) / 1_000_000;

                info(operation, description + " completed",
                    Map.of("durationMs", durationMs));
                instrumentation.forensic(component, operation, "success",
                    Map.of("durationMs", durationMs));

                return result;
            } catch (Exception e) {
                long durationMs = (System.nanoTime() - start) / 1_000_000;

                error(operation, description + " failed: " + e.getMessage(),
                    Map.of("durationMs", durationMs), e);
                instrumentation.forensic(component, operation, "failure",
                    Map.of("durationMs", durationMs, "error", e.getMessage()));

                throw e;
            }
        }
    }

    // === Internal ===

    private String captureStackTrace() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        // Skip first few frames (getStackTrace, captureStackTrace, recordForensic, ...)
        StringBuilder sb = new StringBuilder();
        for (int i = 4; i < Math.min(stack.length, 10); i++) {
            sb.append(stack[i].toString()).append("\n");
        }
        return sb.toString();
    }

    private void forensicWriterLoop() {
        Path currentFile = null;
        BufferedWriter writer = null;
        LocalDate currentDate = null;

        while (true) {
            try {
                ForensicEvent event = forensicQueue.poll(1, TimeUnit.SECONDS);

                // Rotate file daily
                LocalDate today = LocalDate.now();
                if (!today.equals(currentDate) || writer == null) {
                    if (writer != null) {
                        writer.close();
                        compressOldLog(currentFile);
                    }
                    currentDate = today;
                    currentFile = forensicLogDir.resolve(
                        "forensic-" + today.format(DateTimeFormatter.ISO_LOCAL_DATE) + ".jsonl");
                    writer = Files.newBufferedWriter(currentFile,
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                }

                if (event != null) {
                    writer.write(event.toCompactJson());
                    writer.newLine();
                    writer.flush();
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (IOException e) {
                System.err.println("Forensic writer error: " + e.getMessage());
            }
        }
    }

    private void compressOldLog(Path logFile) {
        if (logFile == null || !Files.exists(logFile)) return;

        try {
            Path gzFile = logFile.resolveSibling(logFile.getFileName() + ".gz");
            try (InputStream in = Files.newInputStream(logFile);
                 OutputStream out = new GZIPOutputStream(Files.newOutputStream(gzFile))) {
                in.transferTo(out);
            }
            Files.delete(logFile);
        } catch (IOException e) {
            // Best effort compression
        }
    }
}
