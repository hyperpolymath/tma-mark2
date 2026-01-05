// SPDX-License-Identifier: MPL-2.0
package uk.ac.open.etma.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * SKETCH: Inactivity timeout monitor.
 *
 * <p>Implements IDEAS.md #33: Inactivity Timeout & Auto-Lock</p>
 *
 * <p>Progressive response:</p>
 * <ol>
 *   <li>Warning at 10 minutes</li>
 *   <li>Soft lock at 15 minutes (blur data, require re-auth)</li>
 *   <li>Auto-save & close at 30 minutes</li>
 * </ol>
 *
 * <p>Uses virtual threads for the monitoring - lightweight and non-blocking.</p>
 */
public class InactivityMonitor implements AutoCloseable {

    /**
     * Lock state for the application.
     */
    public sealed interface LockState permits
        LockState.Active,
        LockState.Warning,
        LockState.SoftLocked,
        LockState.HardLocked {

        record Active(LocalDateTime lastActivity) implements LockState {}
        record Warning(Duration timeRemaining) implements LockState {}
        record SoftLocked(LocalDateTime lockedAt, String reason) implements LockState {}
        record HardLocked(LocalDateTime lockedAt) implements LockState {}
    }

    /**
     * Callback interface for state changes.
     */
    public interface StateListener {
        void onWarning(Duration timeRemaining);
        void onSoftLock();
        void onHardLock();
        void onUnlock();
    }

    // Default timeouts (configurable)
    private Duration warningTimeout = Duration.ofMinutes(10);
    private Duration softLockTimeout = Duration.ofMinutes(15);
    private Duration hardLockTimeout = Duration.ofMinutes(30);

    private volatile LocalDateTime lastActivity;
    private volatile LockState currentState;
    private final StateListener listener;
    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> monitorTask;

    public InactivityMonitor(StateListener listener) {
        this.listener = listener;
        this.lastActivity = LocalDateTime.now();
        this.currentState = new LockState.Active(lastActivity);
        // Virtual thread executor for monitoring
        this.scheduler = Executors.newScheduledThreadPool(1, Thread.ofVirtual().factory());
    }

    /**
     * Configure timeouts.
     */
    public InactivityMonitor withTimeouts(
        Duration warning,
        Duration softLock,
        Duration hardLock
    ) {
        this.warningTimeout = warning;
        this.softLockTimeout = softLock;
        this.hardLockTimeout = hardLock;
        return this;
    }

    /**
     * Start monitoring for inactivity.
     */
    public void start() {
        // Check every 30 seconds
        monitorTask = scheduler.scheduleAtFixedRate(
            this::checkInactivity,
            30, 30, TimeUnit.SECONDS
        );
    }

    /**
     * Record user activity - call this on any input event.
     */
    public void recordActivity() {
        lastActivity = LocalDateTime.now();

        // If we were locked or warned, go back to active
        if (!(currentState instanceof LockState.Active)) {
            currentState = new LockState.Active(lastActivity);
            listener.onUnlock();
        }
    }

    /**
     * Get current lock state.
     */
    public LockState getState() {
        return currentState;
    }

    /**
     * Check if currently locked.
     */
    public boolean isLocked() {
        return currentState instanceof LockState.SoftLocked ||
               currentState instanceof LockState.HardLocked;
    }

    /**
     * Unlock after authentication.
     */
    public void unlock() {
        if (isLocked()) {
            recordActivity();
        }
    }

    private void checkInactivity() {
        var now = LocalDateTime.now();
        var inactive = Duration.between(lastActivity, now);

        // Hard lock check
        if (inactive.compareTo(hardLockTimeout) >= 0) {
            if (!(currentState instanceof LockState.HardLocked)) {
                currentState = new LockState.HardLocked(now);
                listener.onHardLock();
            }
            return;
        }

        // Soft lock check
        if (inactive.compareTo(softLockTimeout) >= 0) {
            if (!(currentState instanceof LockState.SoftLocked)) {
                currentState = new LockState.SoftLocked(now, "Inactivity timeout");
                listener.onSoftLock();
            }
            return;
        }

        // Warning check
        if (inactive.compareTo(warningTimeout) >= 0) {
            var remaining = softLockTimeout.minus(inactive);
            currentState = new LockState.Warning(remaining);
            listener.onWarning(remaining);
            return;
        }

        // Still active
        if (!(currentState instanceof LockState.Active)) {
            currentState = new LockState.Active(lastActivity);
        }
    }

    @Override
    public void close() {
        if (monitorTask != null) {
            monitorTask.cancel(false);
        }
        scheduler.shutdown();
    }

    /**
     * SKETCH: Integration example showing how to wire up to Swing.
     */
    public static class SwingIntegration {
        /*
        public static void install(JFrame frame, InactivityMonitor monitor) {
            // Track all input events
            var listener = new AWTEventListener() {
                @Override
                public void eventDispatched(AWTEvent event) {
                    monitor.recordActivity();
                }
            };

            Toolkit.getDefaultToolkit().addAWTEventListener(
                listener,
                AWTEvent.KEY_EVENT_MASK |
                AWTEvent.MOUSE_EVENT_MASK |
                AWTEvent.MOUSE_MOTION_EVENT_MASK
            );

            // Start monitoring
            monitor.start();

            // Cleanup on close
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    monitor.close();
                }
            });
        }
        */
    }
}
