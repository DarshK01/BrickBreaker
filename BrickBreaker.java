import javax.swing.*;
import java.awt.*;

/**
 * Main application class for launching the game
 */
public class BrickBreaker {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        
        // Configure fullscreen window
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
        frame.setUndecorated(true); // Remove window decorations
        frame.setTitle("Brick Breaker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Initialize game and add to window
        GamePlay game = new GamePlay(screenSize.width, screenSize.height);
        frame.add(game);
        frame.setVisible(true);
    }
}