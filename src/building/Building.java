package building;

public class Building {
    public static final int ETAGEI = 20;
    public static final int LIFTCOUNT = 3;

    public static boolean validFloor(int f) {
        return f >= 1 && f <= ETAGEI;
    }
}