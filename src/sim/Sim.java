package sim;

import lift.Lift;
import disp.Disp;
import interf.okno;
import building.Building;
import zapros.Zapros;
import zapros.Direction;
import java.util.*;
import javax.swing.*;

public class Sim {
    private List<Lift> lifts = new ArrayList<>();
    private Disp disp;
    private List<Thread> threads = new ArrayList<>();
    private okno window;

    public void start() {
        System.out.println("=== Start ===");
        System.out.println("Etazhei: " + Building.ETAGEI);
        System.out.println("Liftov: " + Building.LIFTCOUNT);

        for (int i = 1; i <= Building.LIFTCOUNT; i++) {
            int startFloor = (i * 5) % Building.ETAGEI + 1;
            Lift l = new Lift(i, startFloor);
            lifts.add(l);
        }

        disp = new Disp(lifts);

        for (Lift l : lifts) {
            Thread t = new Thread(l, "Lift-" + l.getId());
            threads.add(t);
            t.start();
        }

        Thread dispThread = new Thread(disp, "Dispatcher");
        threads.add(dispThread);
        dispThread.start();

        SwingUtilities.invokeLater(() -> {
            window = new okno(lifts, disp);
            window.setVisible(true);
        });

        startAutoZapros();
    }

    private void startAutoZapros() {
        Thread autoThread = new Thread(() -> {
            try {
                Thread.sleep(3000);

                while (!Thread.currentThread().isInterrupted()) {
                    int delay = 2000 + (int)(Math.random() * 5000);
                    Thread.sleep(delay);

                    if (Math.random() > 0.3) {
                        int from = (int)(Math.random() * Building.ETAGEI) + 1;
                        int to;
                        do {
                            to = (int)(Math.random() * Building.ETAGEI) + 1;
                        } while (to == from);

                        Direction dir = from < to ? Direction.UP : Direction.DOWN;
                        disp.addZapros(new Zapros(from, to, dir));
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "AutoZapros");

        autoThread.setDaemon(true);
        autoThread.start();
    }

    public void stop() {
        System.out.println("\n=== Stop ===");

        if (window != null) {
            window.stop();
            SwingUtilities.invokeLater(() -> window.dispose());
        }

        disp.stop();

        for (Lift l : lifts) {
            l.stop();
        }

        for (Thread t : threads) {
            try {
                t.interrupt();
                t.join(1000);
            } catch (InterruptedException e) {}
        }

        printFinalStats();
    }

    private void printFinalStats() {
        System.out.println("\n=== STATS ===");
        for (Lift l : lifts) {
            l.printStats();
        }
        disp.printStats();
    }
}