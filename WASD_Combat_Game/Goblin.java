// Goblin.java
// This must be public so GamePanel can access it.
public class Goblin {
    public double x, y;
    public final int size = 28;
    public int hp = 3;
    public final int maxHp = 3;
    public final double speed = 1.2;
    public final int attackRange = 40;
    public final int attackDamage = 1;
    public final int attackCooldown = 40;
    public int attackTimer = 0;
    public boolean alive = true;

    public Goblin(double x, double y) {
        this.x = x;
        this.y = y;
        this.attackTimer = attackCooldown;
    }
}