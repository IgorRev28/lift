package interf;

import lift.Lift;
import disp.Disp;
import zapros.Zapros;
import zapros.Direction;
import building.Building;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class okno extends JFrame {
    private List<Lift> lifts;
    private Disp disp;
    private StatPanel statPanel;
    private Timer updateTimer;

    public okno(List<Lift> lifts, Disp disp) {
        this.lifts = lifts;
        this.disp = disp;

        setTitle("Lift System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);

        initUI();

        updateTimer = new Timer(true);
        updateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> updateAllPanels());
            }
        }, 0, 200);
    }

    private void initUI() {
        Container content = getContentPane();
        content.setLayout(new BorderLayout(10, 10));

        JPanel liftPanel = new JPanel(new GridLayout(1, lifts.size(), 10, 10));
        liftPanel.setBorder(BorderFactory.createTitledBorder("Lifts"));

        for (Lift l : lifts) {
            LiftPanel lp = new LiftPanel(l);
            liftPanel.add(lp);
        }

        content.add(liftPanel, BorderLayout.CENTER);

        JPanel controlPanel = createControlPanel();
        content.add(controlPanel, BorderLayout.EAST);

        statPanel = new StatPanel();
        content.add(statPanel, BorderLayout.SOUTH);

        content.add(createFloorButtons(), BorderLayout.WEST);
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Control"));

        JButton addBtn = new JButton("Random zapros");
        addBtn.addActionListener(e -> {
            int from = (int)(Math.random() * Building.ETAGEI) + 1;
            int to;
            do {
                to = (int)(Math.random() * Building.ETAGEI) + 1;
            } while (to == from);

            Direction dir = from < to ? Direction.UP : Direction.DOWN;
            Zapros z = new Zapros(from, to, dir);

            disp.addZapros(z);
            statPanel.addZapros();
            statPanel.addLog("New zapros: " + z);
        });

        JButton testBtn = new JButton("Test");
        testBtn.addActionListener(e -> {
            statPanel.addLog("Test...");

            new Thread(() -> {
                try {
                    disp.addZapros(new Zapros(1, 10, Direction.UP));
                    Thread.sleep(500);
                    disp.addZapros(new Zapros(5, 15, Direction.UP));
                    Thread.sleep(500);
                    disp.addZapros(new Zapros(20, 1, Direction.DOWN));
                    Thread.sleep(500);
                    disp.addZapros(new Zapros(10, 5, Direction.DOWN));
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        });

        JPanel manualPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        manualPanel.setBorder(BorderFactory.createTitledBorder("Manual"));

        JTextField fromField = new JTextField("1");
        JTextField toField = new JTextField("10");
        JComboBox<Direction> dirCombo = new JComboBox<>(Direction.values());

        manualPanel.add(new JLabel("From:"));
        manualPanel.add(fromField);
        manualPanel.add(new JLabel("To:"));
        manualPanel.add(toField);
        manualPanel.add(new JLabel("Dir:"));
        manualPanel.add(dirCombo);

        JButton manualBtn = new JButton("Send");
        manualBtn.addActionListener(e -> {
            try {
                int from = Integer.parseInt(fromField.getText());
                int to = Integer.parseInt(toField.getText());
                Direction dir = (Direction) dirCombo.getSelectedItem();

                if (Building.validFloor(from) && Building.validFloor(to) && from != to) {
                    Zapros z = new Zapros(from, to, dir);
                    disp.addZapros(z);
                    statPanel.addZapros();
                    statPanel.addLog("Manual: " + z);
                } else {
                    JOptionPane.showMessageDialog(this, "Bad floors!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter numbers!");
            }
        });

        panel.add(addBtn);
        panel.add(Box.createVerticalStrut(10));
        panel.add(testBtn);
        panel.add(Box.createVerticalStrut(10));
        panel.add(manualPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(manualBtn);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createFloorButtons() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(Building.ETAGEI, 1, 2, 2));
        panel.setBorder(BorderFactory.createTitledBorder("Floors"));

        for (int i = Building.ETAGEI; i >= 1; i--) {
            final int floor = i;
            JButton btn = new JButton(String.valueOf(i));
            btn.addActionListener(e -> {
                Direction dir = floor < Building.ETAGEI/2 ? Direction.UP : Direction.DOWN;
                Zapros z = new Zapros(floor, floor, dir);
                disp.addZapros(z);
                statPanel.addLog("Call to floor " + floor);
            });

            if (floor == 1) btn.setBackground(new Color(20, 255, 200));
            if (floor == Building.ETAGEI) btn.setBackground(new Color(255, 200, 200));

            panel.add(btn);
        }

        return panel;
    }

    private void updateAllPanels() {
        statPanel.updateStats();

        for (Component comp : ((JPanel)getContentPane().getComponent(0)).getComponents()) {
            if (comp instanceof LiftPanel) {
                ((LiftPanel) comp).updateUI();
            }
        }
    }

    public void stop() {
        if (updateTimer != null) {
            updateTimer.cancel();
        }
    }
}
