import java.awt.*;

/**
 * Manages brick grid:
 * - Initialization with pattern
 * - Drawing
 * - Collision detection
 */
public class BrickMap {
    private final int[][] bricks;       // 0=empty, 1=blue(5pts), 2=pink(20pts)
    private final int brickWidth;      // Width of individual brick
    private final int brickHeight;     // Height of individual brick
    private final int screenWidth;     // Reference to window width
    private final int screenHeight;    // Reference to window height

    /**
     * Initialize brick grid with pattern
     */
    public BrickMap(int rows, int cols, int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        bricks = new int[rows][cols];
        
        // Create checkerboard pattern with different brick types
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                bricks[i][j] = ((i % 2 == 0 && j % 2 != 0) || (i % 2 != 0 && j % 2 == 0)) ? 2 : 1;
            }
        }
        
        // Calculate brick dimensions with margins
        brickWidth = (int)(screenWidth * 0.7) / cols;  // 70% of screen width
        brickHeight = (int)(screenHeight * 0.2) / rows; // 20% of screen height
    }

    /**
     * Draw all bricks
     */
    public void draw(Graphics2D g) {
        for (int i = 0; i < bricks.length; i++) {
            for (int j = 0; j < bricks[i].length; j++) {
                if (bricks[i][j] > 0) {
                    // Set color based on brick type
                    g.setColor(bricks[i][j] == 1 ? 
                        new Color(30, 144, 255) : // Blue
                        new Color(255, 105, 180)); // Pink
                    
                    // Calculate position with 15% horizontal margin
                    int x = (int)(j * brickWidth + screenWidth * 0.15);
                    int y = (int)(i * brickHeight + screenHeight * 0.1);
                    
                    // Draw brick and border
                    g.fillRect(x, y, brickWidth, brickHeight);
                    g.setStroke(new BasicStroke(2));
                    g.setColor(Color.BLACK);
                    g.drawRect(x, y, brickWidth, brickHeight);
                }
            }
        }
    }

    /**
     * Check and handle ball-brick collisions
     */
    public void checkCollision(int ballX, int ballY, int ballDirX, int ballDirY, GamePlay game) {
        int ballSize = game.getBallSize();
        
        for (int i = 0; i < bricks.length; i++) {
            for (int j = 0; j < bricks[i].length; j++) {
                if (bricks[i][j] > 0) {
                    // Calculate brick position
                    int x = (int)(j * brickWidth + screenWidth * 0.15);
                    int y = (int)(i * brickHeight + screenHeight * 0.1);
                    Rectangle brickRect = new Rectangle(x, y, brickWidth, brickHeight);
                    
                    // Check collision with ball
                    Rectangle ballRect = new Rectangle(ballX, ballY, ballSize, ballSize);
                    if (ballRect.intersects(brickRect)) {
                        // Update score and brick state
                        game.increaseScore(bricks[i][j] == 1 ? 5 : 20);
                        bricks[i][j] = 0;
                        game.decreaseTotalBricks();

                        // Determine collision direction
                        if (ballX + ballSize - 1 <= brickRect.x || 
                            ballX + 1 >= brickRect.x + brickWidth) {
                            game.setBallDirX(-game.getBallDirX());
                        } else {
                            game.setBallDirY(-game.getBallDirY());
                        }
                        return; // Only handle one collision per frame
                    }
                }
            }
        }
    }
}