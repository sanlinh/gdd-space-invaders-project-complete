package gdd.sprite;

import static gdd.Global.*;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

public class Alien2 extends Enemy {
    private int animationFrame = 0;
    private final int ANIMATION_SPEED = 10;

    private Bomb bomb;

    // Enhanced AI variables
    private double dx, dy; // Use double for smoother movement
    private int behaviorState = 0; // Different behavior states
    private int stateTimer = 0;
    private double targetX, targetY; // Target position
    private boolean isEvading = false;
    private int evadeTimer = 0;

    // Behavior constants
    private static final int STATE_PATROL = 0;
    private static final int STATE_ATTACK = 1;
    private static final int STATE_EVADE = 2;
    private static final int STATE_CIRCLE = 3;

     // Vertical speed
    private static final Rectangle[] ALIEN2_CLIPS = {
        new Rectangle(0, 0, 32, 32),    // First UFO
        new Rectangle(32, 0, 32, 32),   // Second UFO
        new Rectangle(64, 0, 32, 32),   // Third UFO
        new Rectangle(96, 0, 32, 32)  

    };

    private BufferedImage[] animationFrames;
    private int currentFrame = 0;
    
    public Alien2(int x, int y) {
        super(x, y);
        this.dx = 2 + Math.random() * 2; // Random speed 2-4
        this.dy = 1 + Math.random();     // Random vertical speed
        this.behaviorState = (int)(Math.random() * 4); //
        initAlien();
    }
    
    private void initAlien() {
        var ii = new ImageIcon(IMG_ALIENUFO);
         // Use same image for now, you can change later
          bomb = new Bomb(x, y);
        
        BufferedImage originalImage = new BufferedImage(
            ii.getIconWidth(), 
            ii.getIconHeight(), 
            BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g2d = originalImage.createGraphics();
        g2d.drawImage(ii.getImage(), 0, 0, null);
        g2d.dispose();

        animationFrames = new BufferedImage[ALIEN2_CLIPS.length];

        for (int i = 0; i < ALIEN2_CLIPS.length; i++) {
            // Clip the specific frame from the sprite sheet
            BufferedImage clippedImage = originalImage.getSubimage(
                ALIEN2_CLIPS[i].x, 
                ALIEN2_CLIPS[i].y, 
                ALIEN2_CLIPS[i].width, 
                ALIEN2_CLIPS[i].height
            );

        animationFrames[i] = new BufferedImage(
            clippedImage.getWidth() , 
            clippedImage.getHeight() , 
            BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g2dScaled = animationFrames[i].createGraphics();
        g2dScaled.drawImage(clippedImage, 0, 0, 
            clippedImage.getWidth() , 
            clippedImage.getHeight() , null);
        g2dScaled.dispose();
    }    
        setImage(animationFrames[0]);
        // Different movement pattern - zigzag movement
        this.dx = 3; // Horizontal speed
        this.dy = 1; // Vertical speed
    }
    
    @Override
    public void act(int direction) {
        stateTimer++;
        animationFrame++;

        if (animationFrame % ANIMATION_SPEED == 0) {
            currentFrame = (currentFrame + 1) % animationFrames.length;
            setImage(animationFrames[currentFrame]);
        }

        // Change behavior state occasionally
        if (stateTimer > 120 + (int)(Math.random() * 180)) { // 2-5 seconds
            behaviorState = (int)(Math.random() * 4);
            stateTimer = 0;
        }
        switch(behaviorState) {
            case STATE_PATROL:
                performPatrolBehavior();
                break;
            case STATE_ATTACK:
                performAttackBehavior();
                break;
            case STATE_EVADE:
                performEvadeBehavior();
                break;
            case STATE_CIRCLE:
                performCircleBehavior();
                break;
        }
        // Zigzag movement pattern
        x += dx;
        y += dy;

        // Add some vertical oscillation
        y += Math.sin(animationFrame * 0.1) * 0.5;

        handleBoundaries();

        // Remove if too far off screen
        if (y > BOARD_HEIGHT + 100 || x < -100 || x > BOARD_WIDTH + 100) {
            setVisible(false);
        }
    
    }
    private void performPatrolBehavior() {
        // Patrol horizontally with slight vertical movement
        if (Math.abs(dx) < 0.5) dx = 1; // Ensure minimum speed
        
        // Gentle vertical oscillation
        dy = Math.sin(animationFrame * 0.05) * 0.3+ 0.5;
        
        // Occasional direction change
        if (Math.random() < 0.005) { // 1% chance
            dx *= -1;
        }
    }
    
    private void performAttackBehavior() {
        // More aggressive downward movement
        dy = Math.abs(dy) ; // Ensure moving down

        // Zigzag attack pattern
        dx = Math.sin(animationFrame * 0.1) * 2;
        
        // Increase bomb drop chance during attack
        // (This would be handled in Scene1 with higher probability)
    }
    
    private void performEvadeBehavior() {
        // Quick evasive movements
        if (evadeTimer <= 0) {
            // Choose new evasion direction
            dx = (Math.random() - 0.5) * 4; // Random horizontal movement
            dy = -1 + Math.random() * 1;    // Slight upward bias
            evadeTimer = 30 + (int)(Math.random() * 30); // 0.5-1 second
        }
        evadeTimer--;
    }
    
    private void performCircleBehavior() {
        // Circular movement pattern
        double centerX = BOARD_WIDTH / 2;
        double centerY = y + 30;
        double radius = 60;
        double angle = animationFrame * 0.04;
        
        targetX = centerX + Math.cos(angle) * radius;
        targetY = centerY + Math.sin(angle) * radius * 0.3;
        
        // Move toward target
        dx = (targetX - x) * 0.05;
        dy = (targetY - y) * 0.05 + 0.5; // Still move generally downward
    }
    
    private void handleBoundaries() {
        if (x < 20) {
            dx = Math.abs(dx); // Force rightward movement
            x = 20;
        }
        if (x > BOARD_WIDTH - ALIEN_WIDTH - 20) {
            dx = -Math.abs(dx); // Force leftward movement
            x = BOARD_WIDTH - ALIEN_WIDTH - 20;
        }
        
        // Prevent getting stuck at top
        if (y < 20) {
            dy = Math.abs(dy);
            y = 20;
        }
    }

    public Bomb getBomb() {
        return bomb;
    }
}