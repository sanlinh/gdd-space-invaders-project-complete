package gdd.sprite;

import static gdd.Global.*;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;

public class Player extends Sprite {

    private static final int START_X = 270;
    private static final int START_Y = 540;
    private int width;
    private int height;
    private int baseSpeed = 3;
    private int currentSpeed = 3;


    private Image[] playerSprites;
    private int currentDirection = PLAYER_CENTER; // Default to center position

    private int speedLevel = 1;        // Speed upgrade level (1-4)
    private int shotLevel = 1;         // Shot upgrade level (1-4)
    private static final int MAX_LEVEL = 4;

    // Add these fields to the Player class
    private boolean tripleShotEnabled = false;
    private long tripleShotEndTime = 0;
    private static final long TRIPLE_SHOT_DURATION = 10000;

    private Rectangle bounds = new Rectangle(175,135,17,32);

    public void enableTripleShot() {
        tripleShotEnabled = true;
        tripleShotEndTime = System.currentTimeMillis() + TRIPLE_SHOT_DURATION;
        System.out.println("Triple shot enabled!");
    }

    public boolean isTripleShotEnabled() {
        if (tripleShotEnabled && System.currentTimeMillis() > tripleShotEndTime) {
            tripleShotEnabled = false;
             // Disable after duration
            System.out.println("Triple shot expired.");
        }
        return tripleShotEnabled;
    }

    public int getEffectiveShotCount() {
        if (isTripleShotEnabled()) {
            return Math.max(3, shotLevel); // At least 3 shots when triple shot is active
        }
        return shotLevel;
    }

    public void fireTripleShot() {
        if (isTripleShotEnabled()) {
            // Fire three missiles: center, left, right
            int centerX = getX()+ width / 2;
            int startY = getY();
            int spread = 15; // Horizontal spread between missiles
            
            // Center missile - straight up
            // missiles.add(new Missile(centerX, startY, 0, -MISSILE_SPEED));
            
            // Left missile - angled left
            // missiles.add(new Missile(centerX - spread, startY, -1, -MISSILE_SPEED));
            
            // Right missile - angled right
            // missiles.add(new Missile(centerX + spread, startY, 1, -MISSILE_SPEED));
        }
    }

    public Player() {
        initPlayer();
    }

    private void initPlayer() {
        playerSprites = new Image[PLAYER_IMAGES.length];
        for (int i = 0; i < PLAYER_IMAGES.length; i++) {
            var ii = new ImageIcon(PLAYER_IMAGES[i]);
            // Scale the image to use the global scaling factor
            playerSprites[i] = ii.getImage().getScaledInstance(
                ii.getIconWidth()/2 ,
                ii.getIconHeight()/2 ,
                java.awt.Image.SCALE_SMOOTH);
        }
        setImage(playerSprites[PLAYER_CENTER]); // Set initial image to center

        var ii = new ImageIcon(PLAYER_IMAGES[PLAYER_CENTER]);
        width = ii.getIconWidth() / 2;
        height = ii.getIconHeight() / 2;

        setX(START_X);
        setY(START_Y);
    }
    public void upgradeSpeed() {
        if (speedLevel < MAX_LEVEL) {
            speedLevel++;
            currentSpeed = baseSpeed + speedLevel; // Speed increases with level
            System.out.println("Speed upgraded to Level " + speedLevel + "/" + MAX_LEVEL + 
                             " (Speed: " + currentSpeed + ")");
        } else {
            System.out.println("Speed already at maximum level!");
        }
    }

    public void upgradeShotLevel() {
        if (shotLevel < MAX_LEVEL) {
            shotLevel++;
            System.out.println("Shot Level upgraded to " + shotLevel + "/" + MAX_LEVEL);
        } else {
            System.out.println("Shot Level already at maximum!");
        }
    }

    public int getSpeed() {
        return currentSpeed;
    }

    public int setSpeed(int speed) {
        if (speed < 1) {
            speed = 1; // Ensure speed is at least 1
        }
        this.currentSpeed = speed;
        return currentSpeed;
    }

    public void act() {
        x += dx;
        y += dy;

        if (x <= 2) {
            x = 2;
        }

        if (x >= BOARD_WIDTH - 2 * width) {
            x = BOARD_WIDTH - 2 * width;
        }
        
        if (y <= BOARD_HEIGHT / 2) {  // Don't go above middle of screen
            y = BOARD_HEIGHT / 2;
        }
        if (y >= BOARD_HEIGHT - height - 10) {  // Don't go below bottom
            y = BOARD_HEIGHT - height - 10;
        }
        updateSprite();

    }
    private void updateSprite() {
        int newDirection = currentDirection;
        
        if (dx < 0) {
            newDirection = PLAYER_LEFT;
        } else if (dx > 0) {
            newDirection = PLAYER_RIGHT;
        } else {
            newDirection = PLAYER_CENTER;
        }
        
        // Only update image if direction changed
        if (newDirection != currentDirection) {
            currentDirection = newDirection;
            setImage(playerSprites[currentDirection]);
        }
    }

    // In Player.java, make sure you have getBounds() method:
    @Override
    public Rectangle getBounds() {
        if (getImage() != null) {
            return new Rectangle(x, y, getImage().getWidth(null), getImage().getHeight(null));
        } else {
            return new Rectangle(x, y, PLAYER_WIDTH, PLAYER_HEIGHT);
        }
    }


// Also add dying state methods if not already present:
    public boolean isDying() {
        return dying;
    }

    public void setDying(boolean dying) {
        this.dying = dying;
        if (dying) {
            setVisible(false);

        }
        
    }


    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            dx = -currentSpeed;
        }

        if (key == KeyEvent.VK_RIGHT) {
            dx = currentSpeed;
        }

        if (key == KeyEvent.VK_UP) {
            dy = -currentSpeed;
        }
        if (key == KeyEvent.VK_DOWN) {
            dy = currentSpeed;
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            dx = 0;
        }

        if (key == KeyEvent.VK_RIGHT) {
            dx = 0;
        }
        if (key == KeyEvent.VK_UP) {
            dy = 0;
        }
        if (key == KeyEvent.VK_DOWN) {
            dy = 0;
        }
    }
}