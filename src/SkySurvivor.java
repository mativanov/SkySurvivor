import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class SkySurvivor extends JPanel implements ActionListener, KeyListener {

    // Board dimensions
    private static final int BOARD_WIDTH = 360;
    private static final int BOARD_HEIGHT = 640;

    // Plane dimensions and initial position
    private static final int PLANE_WIDTH = 54;
    private static final int PLANE_HEIGHT = 44;
    private static final int PLANE_START_X = BOARD_WIDTH / 8;
    private static final int PLANE_START_Y = BOARD_WIDTH / 2;

    // Obstacle  dimensions and initial position
    private static final int OBSTACLE_WIDTH = 64;
    private static final int OBSTACLE_HEIGHT = 512;
    private static final int OBSTACLE_START_X = BOARD_WIDTH;

    // Gravity and velocity
    private static final int GRAVITY = 1;
    private static final int JUMP_VELOCITY = -11;
    private static final int OBSTACLE_VELOCITY = -4;

    // Images
    private final Image backgroundImg;
    private final Image planeImg;
    private final Image topObstacleImg;
    private final Image bottomObstacleImg;

    // Plane and obstacles
    private final Plane plane;
    private final ArrayList<Obstacle> obstacles;

    // Timers
    private final Timer gameLoop;
    private final Timer obstacleSpawner;

    // Game state
    private int velocityY = 0;
    private boolean gameOver = false;
    private double score = 0;

    // Random for obstacle positioning
    private final Random random = new Random();

    // Plane class
    private static class Plane {
        int x = PLANE_START_X;
        int y = PLANE_START_Y;
        int width = PLANE_WIDTH;
        int height = PLANE_HEIGHT;
        Image img;

        Plane(Image img) {
            this.img = img;
        }
    }

    // Obstacle class
    private static class Obstacle {
        int x;
        int y;
        int width = OBSTACLE_WIDTH;
        int height = OBSTACLE_HEIGHT;
        Image img;
        boolean passed = false;

        Obstacle(Image img, int x, int y) {
            this.img = img;
            this.x = x;
            this.y = y;
        }
    }

    public SkySurvivor () {
        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        setFocusable(true);
        addKeyListener(this);

        backgroundImg = new ImageIcon(getClass().getResource("/assets/background.png")).getImage();
        planeImg = new ImageIcon(getClass().getResource("/assets/pilot.png")).getImage();
        topObstacleImg = new ImageIcon(getClass().getResource("/assets/lighting.png")).getImage();
        bottomObstacleImg = new ImageIcon(getClass().getResource("/assets/building.png")).getImage();

        plane = new Plane(planeImg);
        obstacles = new ArrayList<>();

        obstacleSpawner = new Timer(1100, e -> spawnObstacles());
        obstacleSpawner.start();

        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();
    }

    private void spawnObstacles() {
        int randomY = OBSTACLE_HEIGHT / -4 - random.nextInt(OBSTACLE_HEIGHT / 2);
        int gap = BOARD_HEIGHT / 3;
    
        Obstacle bottomObstacle = new Obstacle(bottomObstacleImg, OBSTACLE_START_X, randomY + OBSTACLE_HEIGHT + gap);
        obstacles.add(bottomObstacle);
    
        if (score >= 5 && random.nextBoolean()) {  
            Obstacle topObstacle = new Obstacle(topObstacleImg, OBSTACLE_START_X, randomY);
            obstacles.add(topObstacle);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {
        // Draw background
        g.drawImage(backgroundImg, 0, 0, BOARD_WIDTH, BOARD_HEIGHT, null);

        // Draw plane
        g.drawImage(plane.img, plane.x, plane.y, plane.width, plane.height, null);

        // Draw obstacles
        for (Obstacle obstacle : obstacles) {
            g.drawImage(obstacle.img, obstacle.x, obstacle.y, obstacle.width, obstacle.height, null);
        }

        // Draw score or game over message
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver) {
            g.drawString("Game Over: " + (int) score, 10, 35);
        } else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }
    }

    private void updateGame() {
        // Update plane position
        velocityY += GRAVITY;
        plane.y += velocityY;
        plane.y = Math.max(plane.y, 0);

        // Update obstacles and check collisions
        for (Obstacle obstacle : obstacles) {
            obstacle.x += OBSTACLE_VELOCITY;

            if (!obstacle.passed && plane.x > obstacle.x + OBSTACLE_WIDTH) {
                score += 1;
                obstacle.passed = true;
            }

            if (isCollision(plane, obstacle)) {
                gameOver = true;
            }
        }

        // Check if plane hits the ground
        if (plane.y > BOARD_HEIGHT) {
            gameOver = true;
        }
    }

    private boolean isCollision(Plane plane, Obstacle obstacle) {
        return plane.x < obstacle.x + obstacle.width &&
        plane.x + plane.width > obstacle.x &&
        plane.y < obstacle.y + obstacle.height &&
        plane.y + plane.height > obstacle.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            updateGame();
            repaint();
        } else {
            obstacleSpawner.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (gameOver) {
                resetGame();
            } else {
                velocityY = JUMP_VELOCITY;
            }
        }
    }

    private void resetGame() {
        plane.y = PLANE_START_Y;
        velocityY = 0;
        obstacles.clear();
        gameOver = false;
        score = 0;
        gameLoop.start();
        obstacleSpawner.start();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    
}
