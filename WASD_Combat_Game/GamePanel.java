import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import javax.swing.*;

// GamePanel must be public to be accessible from SimpleWASDCombatGame
public class GamePanel extends JPanel implements ActionListener, KeyListener {
    // --- CLASS VARIABLES (Game Settings & State) ---
    private final int cols = 40;
    private final int rows = 30;
    private final int tileSize = 24;
    private final int[][] map = new int[rows][cols];
    private final Random rnd = new Random();

    private final int viewW = 800, viewH = 600;

    private double px, py;
    private final int playerSize = 28;
    private final double playerSpeed = 3.5;
    private int playerHP = 10;
    private boolean playerHitFlash = false;
    private int playerHitTimer = 0;

    private int facing = 2; // 0: Up, 1: Right, 2: Down, 3: Left

    private boolean up, down, left, right;
    private boolean running = false;

    private boolean attackPressed = false;
    private int attackCooldown = 0;
    private final int attackCooldownMax = 18;
    private final int attackRange = 44;
    private final int attackDamage = 1;

    private final ArrayList<Goblin> goblins = new ArrayList<>(); // Uses Goblin.java class

    private final Timer timer;

    public GamePanel() {
        setPreferredSize(new Dimension(viewW, viewH));
        setBackground(new Color(80, 140, 80));
        setFocusable(true);
        addKeyListener(this);

        generateMap();
        fixBottomBorder();

        px = (cols * tileSize) / 2.0 - playerSize / 2.0;
        py = (rows * tileSize) / 2.0 - playerSize / 2.0;

        timer = new Timer(16, this); // ~60 FPS
        timer.start();
    }
    
    // --- MAP GENERATION ---
    private void generateMap() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (r == 0 || c == 0 || r == rows - 1 || c == cols - 1) {
                    map[r][c] = 3; // Wall tile
                } else {
                    map[r][c] = 0; // Ground tile
                }
            }
        }
    }

    private void fixBottomBorder() {
        int last = rows - 1;
        for (int c = 0; c < cols; c++) map[last][c] = 3;
    }

    // --- RENDERING ---
    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;

        // Calculate Camera Position
        int mapPixW = cols * tileSize;
        int mapPixH = rows * tileSize;
        int camX = (int) (px + playerSize / 2.0) - viewW / 2;
        int camY = (int) (py + playerSize / 2.0) - viewH / 2;
        camX = Math.max(0, Math.min(camX, mapPixW - viewW));
        camY = Math.max(0, Math.min(camY, mapPixH - viewH));

        int startCol = camX / tileSize;
        int startRow = camY / tileSize;
        int endCol = Math.min(cols - 1, (camX + viewW) / tileSize + 1);
        int endRow = Math.min(rows - 1, (camY + viewH) / tileSize + 1);

        // Draw Map Tiles
        for (int r = startRow; r <= endRow; r++) {
            for (int c = startCol; c <= endCol; c++) {
                int tx = c * tileSize - camX;
                int ty = r * tileSize - camY;
                if (map[r][c] == 3) {
                    g.setColor(new Color(100, 100, 100)); // Wall color
                    g.fillRect(tx, ty, tileSize, tileSize);
                    g.setColor(new Color(70, 70, 70));
                    g.drawRect(tx + 1, ty + 1, tileSize - 2, tileSize - 2);
                } else {
                    g.setColor(new Color(90, 170, 100)); // Ground color
                    g.fillRect(tx, ty, tileSize, tileSize);
                    if ((r + c) % 7 == 0) {
                        g.setColor(new Color(75, 150, 85));
                        g.fillRect(tx + 4, ty + 4, 2, 2);
                    }
                }
            }
        }

        // Draw Goblins
        for (Goblin gbl : goblins) {
            if (!gbl.alive) continue;
            int gx = (int) Math.round(gbl.x) - camX;
            int gy = (int) Math.round(gbl.y) - camY;
            
            // Goblin body
            g.setColor(new Color(40, 160, 50));
            g.fillRect(gx, gy, gbl.size, gbl.size);
            
            // Goblin HP bar background
            g.setColor(Color.DARK_GRAY);
            g.fillRect(gx, gy - 8, gbl.size, 6);
            
            // Goblin HP bar foreground
            g.setColor(Color.RED);
            int hpw = Math.max(0, gbl.size * gbl.hp / gbl.maxHp);
            g.fillRect(gx, gy - 8, hpw, 6);
        }

        // Draw Player
        int pdx = (int) Math.round(px) - camX;
        int pdy = (int) Math.round(py) - camY;
        if (playerHitFlash && (playerHitTimer % 6) < 3) {
            g.setColor(new Color(255, 80, 80)); // Flash red when hit
        } else {
            g.setColor(new Color(200, 50, 50)); // Player body color
        }
        g.fillRect(pdx, pdy, playerSize, playerSize);

        // Draw Player "Sword" indicator (simple rectangle)
        g.setColor(new Color(200, 200, 120));
        int sx = pdx, sy = pdy;
        int sw = 6, sh = 24;
        
        // Adjust sword position based on facing direction
        if (facing == 1) { // Right
            sx = pdx + playerSize; sy = pdy + playerSize / 2 - sh / 2;
            g.fillRect(sx, sy, sw, sh);
        } else if (facing == 3) { // Left
            sx = pdx - sw; sy = pdy + playerSize / 2 - sh / 2;
            g.fillRect(sx, sy, sw, sh);
        } else if (facing == 0) { // Up
            sx = pdx + playerSize / 2 - sh / 2; sy = pdy - sw;
            g.fillRect(sx, sy, sh, sw);
        } else { // Down (facing == 2)
            sx = pdx + playerSize / 2 - sh / 2; sy = pdy + playerSize;
            g.fillRect(sx, sy, sh, sw);
        }

        // Draw HUD (HP and Controls)
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRect(8, 8, 220, 44);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("Player (Knight)", 16, 26);
        g.drawString("HP: " + playerHP, 16, 44);

        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.setColor(Color.WHITE);
        g.drawString("W A S D = move | SHIFT = run | SPACE = attack | ENTER = spawn goblin | ESC = exit", 140, 18);
    }

    // --- GAME LOOP (Runs every 16ms) ---
    @Override
    public void actionPerformed(ActionEvent e) {
        // Player Movement
        double moveSpeed = running ? playerSpeed * 1.8 : playerSpeed;

        double nx = px, ny = py;
        if (up) { ny -= moveSpeed; facing = 0; }
        if (down) { ny += moveSpeed; facing = 2; }
        if (left) { nx -= moveSpeed; facing = 3; }
        if (right) { nx += moveSpeed; facing = 1; }

        // Collision Check (Tile map)
        if (!collidesWithTileType(nx, ny, 3)) {
            px = clamp(nx, 0, cols * tileSize - playerSize);
            py = clamp(ny, 0, rows * tileSize - playerSize);
        } else {
            // Sliding collision detection
            if (!collidesWithTileType(nx, py, 3)) px = clamp(nx, 0, cols * tileSize - playerSize);
            if (!collidesWithTileType(px, ny, 3)) py = clamp(ny, 0, rows * tileSize - playerSize);
        }

        // Collision Resolution (Player vs. Goblin)
        for (Goblin gbl : goblins) {
            if (!gbl.alive) continue;
            if (rectsOverlap(px, py, playerSize, playerSize, gbl.x, gbl.y, gbl.size, gbl.size)) {
                double dx = (px + playerSize / 2.0) - (gbl.x + gbl.size / 2.0);
                double dy = (py + playerSize / 2.0) - (gbl.y + gbl.size / 2.0);
                double dist = Math.max(1, Math.hypot(dx, dy));
                double overlap = (playerSize / 2.0 + gbl.size / 2.0) - dist;
                if (overlap > 0) {
                    px += (dx / dist) * overlap * 0.5;
                    py += (dy / dist) * overlap * 0.5;
                    gbl.x -= (dx / dist) * overlap * 0.5;
                    gbl.y -= (dy / dist) * overlap * 0.5;
                }
            }
        }

        // Player Attack Logic
        if (attackPressed && attackCooldown <= 0) {
            attackCooldown = attackCooldownMax;
            for (Goblin gbl : goblins) {
                if (!gbl.alive) continue;
                if (isGoblinInFrontAndInRange(gbl)) {
                    gbl.hp -= attackDamage;
                    if (gbl.hp <= 0) gbl.alive = false;
                }
            }
        }
        if (attackCooldown > 0) attackCooldown--;

        // Goblin AI and Cleanup
        Iterator<Goblin> it = goblins.iterator();
        while (it.hasNext()) {
            Goblin gbl = it.next();
            if (!gbl.alive) {
                it.remove();
                continue;
            }

            // Move towards player
            double dx = (px + playerSize / 2.0) - (gbl.x + gbl.size / 2.0);
            double dy = (py + playerSize / 2.0) - (gbl.y + gbl.size / 2.0);
            double dist = Math.hypot(dx, dy);
            if (dist > 1) {
                double vx = (dx / dist) * gbl.speed;
                double vy = (dy / dist) * gbl.speed;
                double ngx = gbl.x + vx;
                double ngy = gbl.y + vy;
                if (!collidesWithTileType(ngx, ngy, 3)) {
                    gbl.x = clamp(ngx, 0, cols * tileSize - gbl.size);
                    gbl.y = clamp(ngy, 0, rows * tileSize - gbl.size);
                }
            }

            // Goblin Attack Player
            double cx = (px + playerSize / 2.0) - (gbl.x + gbl.size / 2.0);
            double cy = (py + playerSize / 2.0) - (gbl.y + gbl.size / 2.0);
            double cdist = Math.hypot(cx, cy);
            if (cdist < gbl.attackRange) {
                gbl.attackTimer--;
                if (gbl.attackTimer <= 0) {
                    playerHP -= gbl.attackDamage;
                    playerHitFlash = true;
                    playerHitTimer = 24; // 24 frames of flash
                    gbl.attackTimer = gbl.attackCooldown;
                }
            }

            // Player Hit Flash Timer
            if (playerHitTimer > 0) {
                playerHitTimer--;
                if (playerHitTimer <= 0) playerHitFlash = false;
            }
        }

        // Game Over / Restart Logic
        if (playerHP <= 0) {
            playerHP = 0;
            timer.stop();
            JOptionPane.showMessageDialog(this, "You died! Press OK to restart.", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            goblins.clear();
            generateMap();
            fixBottomBorder();
            px = (cols * tileSize) / 2.0 - playerSize / 2.0;
            py = (rows * tileSize) / 2.0 - playerSize / 2.0;
            playerHP = 10;
            timer.start();
        }

        repaint();
    }

    // --- UTILITY METHODS ---

    private boolean rectsOverlap(double x1, double y1, double w1, double h1, double x2, double y2, double w2, double h2) {
        return x1 < x2 + w2 && x1 + w1 > x2 && y1 < y2 + h2 && y1 + h1 > y2;
    }

    private boolean collidesWithTileType(double nx, double ny, int tileType) {
        int leftTile = (int) (nx) / tileSize;
        int rightTile = (int) (nx + playerSize - 1) / tileSize;
        int topTile = (int) (ny) / tileSize;
        int bottomTile = (int) (ny + playerSize - 1) / tileSize;
        
        leftTile = clamp(leftTile, 0, cols - 1);
        rightTile = clamp(rightTile, 0, cols - 1);
        topTile = clamp(topTile, 0, rows - 1);
        bottomTile = clamp(bottomTile, 0, rows - 1);
        
        for (int r = topTile; r <= bottomTile; r++) {
            for (int c = leftTile; c <= rightTile; c++) {
                if (map[r][c] == tileType) return true;
            }
        }
        return false;
    }

    private boolean isGoblinInFrontAndInRange(Goblin gbl) {
        double pxCenterX = px + playerSize / 2.0;
        double pyCenterY = py + playerSize / 2.0;
        double gxCenterX = gbl.x + gbl.size / 2.0;
        double gyCenterY = gbl.y + gbl.size / 2.0;
        double dx = gxCenterX - pxCenterX;
        double dy = gyCenterY - pyCenterY;

        switch (facing) {
            case 0: // Up
                if (dy < 0 && Math.abs(dx) < playerSize) return Math.abs(dy) <= attackRange;
                break;
            case 2: // Down
                if (dy > 0 && Math.abs(dx) < playerSize) return Math.abs(dy) <= attackRange;
                break;
            case 1: // Right
                if (dx > 0 && Math.abs(dy) < playerSize) return Math.abs(dx) <= attackRange;
                break;
            case 3: // Left
                if (dx < 0 && Math.abs(dy) < playerSize) return Math.abs(dx) <= attackRange;
                break;
        }
        return false;
    }

    private double clamp(double v, double a, double b) {
        return Math.max(a, Math.min(b, v));
    }

    private int clamp(int v, int a, int b) {
        return Math.max(a, Math.min(b, v));
    }

    // --- INPUT HANDLING ---
    @Override
    public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();
        if (k == KeyEvent.VK_W) up = true;
        if (k == KeyEvent.VK_S) down = true;
        if (k == KeyEvent.VK_A) left = true;
        if (k == KeyEvent.VK_D) right = true;
        if (k == KeyEvent.VK_SPACE) attackPressed = true;
        if (k == KeyEvent.VK_ENTER) spawnGoblin();
        if (k == KeyEvent.VK_ESCAPE) System.exit(0);
        if (k == KeyEvent.VK_SHIFT) running = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int k = e.getKeyCode();
        if (k == KeyEvent.VK_W) up = false;
        if (k == KeyEvent.VK_S) down = false;
        if (k == KeyEvent.VK_A) left = false;
        if (k == KeyEvent.VK_D) right = false;
        if (k == KeyEvent.VK_SPACE) attackPressed = false;
        if (k == KeyEvent.VK_SHIFT) running = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    private void spawnGoblin() {
        int tries = 0;
        while (tries++ < 200) {
            int c = 1 + rnd.nextInt(cols - 2);
            int r = 1 + rnd.nextInt(rows - 2);
            double gx = c * tileSize + (tileSize - 28) / 2.0;
            double gy = r * tileSize + (tileSize - 28) / 2.0;
            double dist = Math.hypot(gx - px, gy - py);
            // Spawn goblin far from the player (dist > 120)
            if (dist > 120) {
                Goblin g = new Goblin(gx, gy);
                goblins.add(g);
                return;
            }
        }
        // Fallback spawn if random tries fail
        Goblin fallback = new Goblin(px + 120, py + 60);
        goblins.add(fallback);
    }
}