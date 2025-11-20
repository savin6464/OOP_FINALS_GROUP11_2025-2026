import javax.swing.*;

public class SimpleWASDCombatGame extends JFrame {
    public SimpleWASDCombatGame() {
        setTitle("Simple WASD Combat Game");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // The GamePanel class is defined in GamePanel.java
        GamePanel panel = new GamePanel();
        add(panel);
        setVisible(true);
        panel.requestFocusInWindow();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SimpleWASDCombatGame::new);
     }
    }
