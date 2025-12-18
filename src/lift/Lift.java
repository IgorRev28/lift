package lift;

import zapros.Direction;
import building.Building;
import java.util.*;
import java.util.concurrent.locks.*;

public class Lift implements Runnable {
    private int id;
    private int tekEtazh;
    private Direction napr;
    private LiftState sost;
    private int kolvo;


    private TreeSet<Integer> upStops = new TreeSet<>();
    private TreeSet<Integer> downStops = new TreeSet<>((a,b)->b-a);

    private final Lock lock = new ReentrantLock();
    private final Condition estZadachi = lock.newCondition();
    private volatile boolean rabotaet = true;

    private int poezdki = 0;
    private int vsegoLudei = 0;


    public Lift(int id, int startFloor) {
        this.id = id;
        this.tekEtazh = startFloor;
        this.napr = Direction.NONE;
        this.sost = LiftState.STOIT;
        this.kolvo = 0;
    }
    @Override
    public void run() {
        System.out.println("Lift " + id + " start na etazhe " + tekEtazh);

        while (rabotaet) {
            try {
                lock.lock();
                if (upStops.isEmpty() && downStops.isEmpty() && napr == Direction.NONE) {
                    estZadachi.await();
                }
                lock.unlock();

                dvig();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        System.out.println("Lift " + id + " ostanovlen");
    }

    private void dvig() throws InterruptedException {
        Integer cel = sledCel();

        if (cel == null) {
            napr = Direction.NONE;
            sost = LiftState.STOIT;
            Thread.sleep(100);
            return;
        }

        if (cel > tekEtazh) napr = Direction.UP;
        else if (cel < tekEtazh) napr = Direction.DOWN;

        sost = LiftState.EDET;

        while (tekEtazh != cel && rabotaet) {
            Thread.sleep(500);

            if (napr == Direction.UP) tekEtazh++;
            else if (napr == Direction.DOWN) tekEtazh--;

            checkStop();
        }

        if (rabotaet) {
            otkritDveri();
            zakritDveri();
        }
    }

    private void checkStop() {
        lock.lock();
        try {
            if ((napr == Direction.UP && upStops.contains(tekEtazh)) ||
                    (napr == Direction.DOWN && downStops.contains(tekEtazh))) {

                sost = LiftState.DVERI_OTKR;
                removeStop(tekEtazh);

                int vishel = (int)(Math.random() * 3);
                kolvo = Math.max(0, kolvo - vishel);
                vsegoLudei += vishel;

                System.out.println("[Lift " + id + "] Stop na " + tekEtazh +
                        ", vishelo " + vishel + ", vnutri " + kolvo);
            }
        } finally {
            lock.unlock();
        }
    }

    private void otkritDveri() throws InterruptedException {
        sost = LiftState.DVERI_OTKR;
        System.out.println("[Lift " + id + "] Dveri otkr na " + tekEtazh);
        Thread.sleep(2000);
    }

    private void zakritDveri() throws InterruptedException {
        System.out.println("[Lift " + id + "] Dveri zakr...");
        Thread.sleep (1000);
        sost = LiftState.STOIT;
        poezdki++;
    }

    private Integer sledCel() {
        lock.lock();
        try {
            if (napr == Direction.UP && !upStops.isEmpty()) {
                return upStops.first();
            } else if (napr == Direction.DOWN && !downStops.isEmpty()) {
                return downStops.first();
            } else if (!upStops.isEmpty()) {
                napr = Direction.UP;
                return upStops.first();
            } else if (!downStops.isEmpty()) {
                napr = Direction.DOWN;
                return downStops.first();
            }
            return null;
        } finally {
            lock.unlock();
        }
    }

    public boolean addStop(int etazh, Direction d) {
        if (!Building.validFloor(etazh)) return false;

        lock.lock();
        try {
            if (d == Direction.UP) {
                upStops.add(etazh);
            } else {
                downStops.add(etazh);
            }
            estZadachi.signal();
            return true;
        } finally {
            lock.unlock();
        }
    }

    private void removeStop(int etazh) {
        upStops.remove(etazh);
        downStops.remove(etazh);
    }
    public int getTekEtazh() { return tekEtazh; }
    public Direction getNapr() { return napr; }
    public LiftState getSost() { return sost; }
    public int getId() { return id; }
    public int getKolvo() { return kolvo; }
    public TreeSet<Integer> getUpStops() { return new TreeSet<>(upStops); }
    public TreeSet<Integer> getDownStops() { return new TreeSet<>(downStops); }



    public void stop() {
        rabotaet = false;
        lock.lock();
        try {
            estZadachi.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void printStats() {
        System.out.println("=== Lift " + id + " ===");
        System.out.println("Poezdki: " + poezdki);
        System.out.println("Perevezeno: " + vsegoLudei);
    }
}