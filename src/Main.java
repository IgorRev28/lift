import sim.Sim;

public class Main {
    public static void main(String[] args) {
        System.out.println("Lab: Lift System");

        final Sim sim = new Sim();

        sim.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nShutdown...");
            sim.stop();
        }));

        System.out.println("\nPress Enter to stop...");
        try {
            System.in.read();
            sim.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}