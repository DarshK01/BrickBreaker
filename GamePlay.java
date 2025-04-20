import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Main game panel handling core gameplay mechanics:
 * - Player input
 * - Ball physics
 * - Collision detection
 * - Rendering
 * - Game state management
 */
public class GamePlay extends JPanel implements KeyListener, ActionListener {
    // Game state flags
    private boolean play = false;         // True when game is active
    private int score = 0;                // Current player score
    private int totalBricks = 21;         // Remaining bricks count
    private final int screenWidth;        // Window width
    private final int screenHeight;       // Window height
    
    // Animation control
    private final Timer timer;           // Game loop timer
    private final int delay = 8;         // Frame delay in milliseconds
    
    // Paddle properties
    private int paddleX;                 // X-position of paddle
    private int paddleWidth;             // Width of paddle
    
    // Ball properties
    private int ballX, ballY;            // Ball coordinates
    private int ballSize;                // Ball diameter
    private int ballDirX = -1;           // Horizontal direction (-1 left, 1 right)
    private int ballDirY = -2;           // Vertical direction (-1 up, 1 down)
    
    // Brick grid
    private BrickMap map;                // Brick layout manager
    private final int brickRows = 15;    // Number of brick rows
    private final int brickCols = 15;    // Number of brick columns

    /**
     * Initialize game components and scaling
     * @param width Window width
     * @param height Window height
     */
    public GamePlay(int width, int height) {
        this.screenWidth = width;
        this.screenHeight = height;
        
        // Scale game elements proportionally
        paddleWidth = screenWidth / 7;
        ballSize = screenWidth / 50;
        resetGame();
        
        // Set up input handling
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        
        // Initialize game timer
        timer = new Timer(delay, this);
        timer.start();
    }

    /**
     * Reset game to initial state
     */
    private void resetGame() {
        paddleX = (screenWidth - paddleWidth) / 2;  // Center paddle
        ballX = screenWidth / 4;                    // Initial ball position
        ballY = screenHeight / 2;
        map = new BrickMap(brickRows, brickCols, screenWidth, screenHeight);
    }

    /**
     * Main rendering method
     * @param g Graphics context for drawing
     */
    @Override
    public void paint(Graphics g) {
        // Draw black background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, screenWidth, screenHeight);

        // Draw brick grid
        map.draw((Graphics2D) g);

        // Draw borders
        g.setColor(Color.YELLOW);
        g.fillRect(0, 0, screenWidth, 3);            // Top border
        g.fillRect(0, 0, 3, screenHeight);          // Left border
        g.fillRect(screenWidth-3, 0, 3, screenHeight); // Right border

        // Draw score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, screenWidth/40));
        g.drawString("Score: " + score, screenWidth - 200, 30);

        // Draw paddle
        g.setColor(Color.GREEN);
        g.fillRect(paddleX, (int)(screenHeight * 0.9), paddleWidth, 8);

        // Draw ball
        g.setColor(Color.RED);
        g.fillOval(ballX, ballY, ballSize, ballSize);

        // Game over state
        if (ballY > screenHeight * 0.95) {
            play = false;
            ballDirX = 0;
            ballDirY = 0;
            drawCenteredText(g, "Game Over, Score: " + score, Color.RED, screenWidth/20);
            drawCenteredText(g, "Press Enter to Restart", Color.WHITE, screenWidth/30);
        }

        // Victory state
        if (totalBricks == 0) {
            play = false;
            ballDirX = 0;
            ballDirY = 0;
            drawCenteredText(g, "You Won! Score: " + score, Color.GREEN, screenWidth/20);
            drawCenteredText(g, "Press Enter to Restart", Color.WHITE, screenWidth/30);
        }
    }

    /**
     * Helper method for centered text drawing
     */
    private void drawCenteredText(Graphics g, String text, Color color, float fontSize) {
        g.setColor(color);
        g.setFont(new Font("Arial", Font.BOLD, (int)fontSize));
        FontMetrics metrics = g.getFontMetrics();
        int x = (screenWidth - metrics.stringWidth(text)) / 2;
        int y = screenHeight/2;
        g.drawString(text, x, y);
    }

    /**
     * Game loop updates (called by Timer)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (play) {
            // Paddle collision
            if (new Rectangle(ballX, ballY, ballSize, ballSize).intersects(
                new Rectangle(paddleX, (int)(screenHeight * 0.9), paddleWidth, 8))) {
                ballDirY = -ballDirY;
            }

            // Brick collisions
            map.checkCollision(ballX, ballY, ballDirX, ballDirY, this);
            
            // Update ball position
            ballX += ballDirX;
            ballY += ballDirY;

            // Wall collisions
            if (ballX < 0 || ballX > screenWidth - ballSize) {
                ballDirX = -ballDirX;
            }
            if (ballY < 0) {
                ballDirY = -ballDirY;
            }
        }
        repaint();
    }

    /**
     * Handle key presses
     */
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_RIGHT:
                paddleX = Math.min(paddleX + 20, screenWidth - paddleWidth);
                break;
            case KeyEvent.VK_LEFT:
                paddleX = Math.max(paddleX - 20, 0);
                break;
            case KeyEvent.VK_ENTER:
                restartGame();
                break;
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
                break;
        }
    }

    /**
     * Reset game state for new game
     */
    public void restartGame() {
        play = true;
        resetGame();
        score = 0;
        totalBricks = brickRows * brickCols;
        ballDirX = -1;
        ballDirY = -2;
    }

    // Unused interface methods
    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    // Getters and setters
    public void decreaseTotalBricks() { totalBricks--; }
    public void increaseScore(int points) { score += points; }
    public void setBallDirX(int dir) { ballDirX = dir; }
    public void setBallDirY(int dir) { ballDirY = dir; }
    public int getBallSize() { return ballSize; }
    public int getBallDirX() { return ballDirX; }
    public int getBallDirY() { return ballDirY; }
}