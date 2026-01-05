// SPDX-License-Identifier: MPL-2.0
package uk.ac.open.etma.ui;

import uk.ac.open.etma.model.Submission;
import uk.ac.open.etma.model.PrivacyView;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

/**
 * SKETCH: Marking Playlist UI component.
 *
 * <p>Implements IDEAS.md #6: Marking Playlist</p>
 *
 * <ul>
 *   <li>Queue assignments like a music playlist</li>
 *   <li>Progress bar with ETA</li>
 *   <li>"Now playing" shows current assignment</li>
 *   <li>Privacy-aware display (uses PrivacyView)</li>
 * </ul>
 *
 * <p>Demonstrates:</p>
 * <ul>
 *   <li>Modern Swing patterns (MVC separation)</li>
 *   <li>Records for view models</li>
 *   <li>Pattern matching in renderers</li>
 *   <li>Privacy integration</li>
 * </ul>
 */
public class MarkingPlaylistPanel extends JPanel {

    /**
     * View model for playlist items - decoupled from domain model.
     */
    public record PlaylistItem(
        String displayName,
        String course,
        String tma,
        String status,
        boolean isCurrent,
        String piNumber  // For callbacks
    ) {
        /**
         * Create from submission with privacy view.
         */
        public static PlaylistItem from(Submission sub, PrivacyView privacy, boolean current) {
            return new PlaylistItem(
                privacy.displayName(),
                sub.course().code(),
                "TMA" + sub.tmaNumber(),
                formatStatus(sub.status()),
                current,
                sub.student().piNumber()
            );
        }

        private static String formatStatus(Submission.Status status) {
            return switch (status) {
                case Submission.Status.Pending() -> "⏳ Pending";
                case Submission.Status.InProgress(_, _) -> "✏️ In Progress";
                case Submission.Status.Marked(var score, var max, _, _) ->
                    "✓ %d/%d".formatted(score, max);
                case Submission.Status.Returned(_, _) -> "📤 Returned";
                case Submission.Status.Problem(var reason, _) -> "⚠ " + reason;
            };
        }
    }

    /**
     * Progress information for the header.
     */
    public record PlaylistProgress(
        int completed,
        int total,
        Duration elapsed,
        Duration estimatedRemaining
    ) {
        public String summary() {
            return "%d/%d complete".formatted(completed, total);
        }

        public String eta() {
            if (estimatedRemaining == null || estimatedRemaining.isZero()) {
                return "calculating...";
            }
            long mins = estimatedRemaining.toMinutes();
            if (mins < 60) {
                return mins + " min remaining";
            }
            return "%d hr %d min remaining".formatted(mins / 60, mins % 60);
        }

        public int percentComplete() {
            return total > 0 ? (completed * 100) / total : 0;
        }
    }

    // UI Components
    private final JTable table;
    private final PlaylistTableModel tableModel;
    private final JProgressBar progressBar;
    private final JLabel etaLabel;
    private final JLabel currentLabel;

    // Callbacks
    private Consumer<String> onSelect;  // Called with PI number

    public MarkingPlaylistPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Header with "Now Marking" and progress
        var header = createHeader();
        add(header, BorderLayout.NORTH);

        // Playlist table
        tableModel = new PlaylistTableModel();
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(28);

        // Custom renderer for status column with icons
        table.getColumnModel().getColumn(3).setCellRenderer(new StatusCellRenderer());

        // Selection listener
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && onSelect != null) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    var item = tableModel.getItemAt(row);
                    onSelect.accept(item.piNumber());
                }
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        // Footer with controls
        var footer = createFooter();
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel createHeader() {
        var panel = new JPanel(new BorderLayout(8, 4));

        currentLabel = new JLabel("Ready to mark");
        currentLabel.setFont(currentLabel.getFont().deriveFont(Font.BOLD, 14f));
        panel.add(currentLabel, BorderLayout.NORTH);

        var progressPanel = new JPanel(new BorderLayout(8, 0));
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressPanel.add(progressBar, BorderLayout.CENTER);

        etaLabel = new JLabel("--");
        etaLabel.setPreferredSize(new Dimension(150, 20));
        progressPanel.add(etaLabel, BorderLayout.EAST);

        panel.add(progressPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createFooter() {
        var panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));

        var shuffleBtn = new JButton("🔀 Shuffle");
        shuffleBtn.setToolTipText("Randomize order for variety");
        panel.add(shuffleBtn);

        var sortBtn = new JButton("📊 Sort by Score");
        sortBtn.setToolTipText("Similar scores together for consistency");
        panel.add(sortBtn);

        var skipBtn = new JButton("⏭ Skip");
        skipBtn.setToolTipText("Move current to end of queue");
        panel.add(skipBtn);

        return panel;
    }

    /**
     * Set the playlist items.
     */
    public void setItems(List<PlaylistItem> items) {
        tableModel.setItems(items);

        // Update current marker
        items.stream()
            .filter(PlaylistItem::isCurrent)
            .findFirst()
            .ifPresentOrElse(
                item -> currentLabel.setText("Now marking: " + item.displayName()),
                () -> currentLabel.setText("Ready to mark")
            );
    }

    /**
     * Update progress display.
     */
    public void setProgress(PlaylistProgress progress) {
        progressBar.setValue(progress.percentComplete());
        progressBar.setString(progress.summary());
        etaLabel.setText(progress.eta());
    }

    /**
     * Set callback for item selection.
     */
    public void onItemSelected(Consumer<String> callback) {
        this.onSelect = callback;
    }

    /**
     * Table model - keeps UI concerns separate from domain model.
     */
    private static class PlaylistTableModel extends AbstractTableModel {
        private final String[] columns = {"Student", "Course", "TMA", "Status"};
        private List<PlaylistItem> items = List.of();

        void setItems(List<PlaylistItem> items) {
            this.items = items;
            fireTableDataChanged();
        }

        PlaylistItem getItemAt(int row) {
            return items.get(row);
        }

        @Override
        public int getRowCount() {
            return items.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public Object getValueAt(int row, int column) {
            var item = items.get(row);
            return switch (column) {
                case 0 -> item.displayName();
                case 1 -> item.course();
                case 2 -> item.tma();
                case 3 -> item.status();
                default -> "";
            };
        }
    }

    /**
     * Custom renderer showing status with appropriate styling.
     */
    private static class StatusCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column
        ) {
            var label = (JLabel) super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column
            );

            var status = value.toString();
            if (status.startsWith("✓")) {
                if (!isSelected) label.setForeground(new Color(0, 128, 0));
            } else if (status.startsWith("⚠")) {
                if (!isSelected) label.setForeground(Color.RED);
            } else if (status.startsWith("✏")) {
                if (!isSelected) label.setForeground(Color.BLUE);
            }

            return label;
        }
    }
}
