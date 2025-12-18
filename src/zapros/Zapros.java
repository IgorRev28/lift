package zapros;

public class Zapros {
    public int otkuda;
    public int kuda;
    public Direction napr;
    public long vremya;

    public Zapros(int f, int t, Direction d) {
        this.otkuda = f;
        this.kuda = t;
        this.napr = d;
        this.vremya = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "(" + otkuda + "→" + kuda + ")";
    }
}