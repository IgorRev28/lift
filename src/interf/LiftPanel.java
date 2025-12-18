package interf;

import lift.Lift;
import lift.LiftState;
import zapros.Direction;
import java.awt.*;
import javax.swing.*;

public class LiftPanel extends JPanel {
    private Lift lift;
    private JLabel etazhLabel;
    private JLabel sostLabel;
    private JLabel ludiLabel;
    private JLabel stopLabel;

    public LiftPanel(Lift l) {
        this.lift = l;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

        JLabel title = new JLabel("Lift " + l.getId(), SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 14));
        add(title, BorderLayout.NORTH);

        JPanel infoPanel = new JPanel(new GridLayout(4, 1, 5, 5));

        etazhLabel = new JLabel("Etazh: " + l.getTekEtazh());
        sostLabel = new JLabel("Sost: " + l.getSost());
        ludiLabel = new JLabel("Ludi: " + l.getKolvo());
        stopLabel = new JLabel("Stops: 0");

        infoPanel.add(etazhLabel);
        infoPanel.add(sostLabel);
        infoPanel.add(ludiLabel);
        infoPanel.add(stopLabel);

        add(infoPanel, BorderLayout.CENTER);

        updateUI();
    }

    public void updateUI() {
        if (lift == null) return;

        etazhLabel.setText("Etazh: " + lift.getTekEtazh());
        sostLabel.setText("Sost: " + lift.getSost());
        ludiLabel.setText("Ludi: " + lift.getKolvo());

        int stops = lift.getUpStops().size() + lift.getDownStops().size();
        stopLabel.setText("Stops: " + stops);

        Color bgColor;
        switch (lift.getSost()) {
            case EDET:
                bgColor = lift.getNapr() == Direction.UP ?
                        new Color(140, 245, 220) :
                        new Color(255, 220, 90);
                break;
            case DVERI_OTKR:
                bgColor = new Color(255, 5, 200);
                break;
            case STOIT:
                bgColor = Color.WHITE;
                break;
            default:
                bgColor = Color.LIGHT_GRAY;
        }

        setBackground(bgColor);

        String arrow = "";
        if (lift.getNapr() == Direction.UP) arrow = " ↑";
        else if (lift.getNapr() == Direction.DOWN) arrow = " ↓";

        etazhLabel.setText("Etazh: " + lift.getTekEtazh() + arrow);

        repaint();
    }
}