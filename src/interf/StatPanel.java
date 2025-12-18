package interf;

import javax.swing.*;
import java.awt.*;

public class StatPanel extends JPanel {
    private JLabel zaprosLabel;
    private JLabel timeLabel;
    private JTextArea logArea;
    private int zaprosCount = 0;
    private long startTime;

    public StatPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Stats"));

        startTime = System.currentTimeMillis();

        JPanel topPanel = new JPanel(new GridLayout(2, 2, 10, 5));

        zaprosLabel = new JLabel("Zaprosi: 0");
        timeLabel = new JLabel("Time: 0s");

        topPanel.add(zaprosLabel);
        topPanel.add(timeLabel);
        topPanel.add(new JLabel("Lifts: 3"));
        topPanel.add(new JLabel("Floors: 20"));

        add(topPanel, BorderLayout.NORTH);

        logArea = new JTextArea(8, 30);
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(logArea);

        add(scroll, BorderLayout.CENTER);
    }

    public void addZapros() {
        zaprosCount++;
        zaprosLabel.setText("Zaprosi: " + zaprosCount);

        long elapsed = (System.currentTimeMillis() - startTime) / 1000;
        timeLabel.setText("Time: " + elapsed + "s");
    }

    public void addLog(String message) {
        String time = String.format("%tT", new java.util.Date());
        logArea.append("[" + time + "] " + message + "\n");

        logArea.setCaretPosition(logArea.getDocument().getLength());

        if (logArea.getLineCount() > 50) {
            try {
                int end = logArea.getLineEndOffset(0);
                logArea.replaceRange("", 0, end);
            } catch (Exception e) {}
        }
    }

    public void updateStats() {
        long elapsed = (System.currentTimeMillis() - startTime) / 1000;
        timeLabel.setText("Time: " + elapsed + "s");
    }
}