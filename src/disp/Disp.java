package disp;

import zapros.Zapros;
import zapros.Direction;
import lift.Lift;
import building.Building;
import java.util.*;
import java.util.concurrent.*;

public class Disp implements Runnable {
    private BlockingQueue<Zapros> ochered = new LinkedBlockingQueue<>();
    private List<Lift> lifts;
    private Map<Integer, Long> vremenaOzhid = new ConcurrentHashMap<>();
    private int vsegoZaprosov = 0;
    private volatile boolean rabotaet = true;

    public Disp(List<Lift> lifts) {
        this.lifts = lifts;
    }

    public void addZapros(Zapros z) {
        if (!Building.validFloor(z.otkuda) || !Building.validFloor(z.kuda)) {
            System.out.println("Oshibka etazh " + z.otkuda + " ili " + z.kuda);
            return;
        }

        ochered.offer(z);
        vsegoZaprosov++;
        vremenaOzhid.put(vsegoZaprosov, System.currentTimeMillis());

        System.out.println("[Disp] Noviy zapros: " + z);
    }

    @Override
    public void run() {
        System.out.println("Disp start");

        while (rabotaet || !ochered.isEmpty()) {
            try {
                Zapros z = ochered.poll(100, TimeUnit.MILLISECONDS);

                if (z != null) {
                    Lift vibran = selectLift(z);

                    if (vibran != null) {
                        vibran.addStop(z.otkuda, z.napr);
                        vibran.addStop(z.kuda, z.napr);

                        long waitTime = System.currentTimeMillis() - vremenaOzhid.get(vsegoZaprosov);
                        System.out.println("[Disp] Zapros " + z + " → Lift " +
                                vibran.getId() + " (wait: " + waitTime + "ms)");
                    }
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        System.out.println("Disp stop");
    }

    private Lift selectLift(Zapros z) {
        Lift best = null;
        int bestScore = Integer.MAX_VALUE;

        for (Lift l : lifts) {
            int score = calcScore(l, z);
            if (score < bestScore) {
                bestScore = score;
                best = l;
            }
        }

        return best;
    }

    private int calcScore(Lift l, Zapros z) {
        int dist = Math.abs(l.getTekEtazh() - z.otkuda);
        int score = dist * 10;

        Direction lDir = l.getNapr();
        if (lDir != Direction.NONE) {
            if (lDir == Direction.UP && z.otkuda > l.getTekEtazh() && z.napr == Direction.UP) {
                score -= 20;
            } else if (lDir == Direction.DOWN && z.otkuda < l.getTekEtazh() && z.napr == Direction.DOWN) {
                score -= 20;
            }
        }

        int stops = l.getUpStops().size() + l.getDownStops().size();
        score += stops * 5;

        return score;
    }

    public void stop() {
        rabotaet = false;
    }

    public void printStats() {
        System.out.println("\n=== Disp stats ===");
        System.out.println("Vsego zaprosov: " + vsegoZaprosov);
        System.out.println("V ocheredi: " + ochered.size());
    }
}