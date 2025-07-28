package gdd.sprite;

import static gdd.Global.*;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;


public class Alien1 extends Enemy {

    private int movementPattern;

    private int baseSpeed = 1;
    private int horizontalDirection = 1; // 1 for right, -1 for left
    private int zigzagCounter = 0;
    private boolean isDiving = false;
    private int diveCounter = 0;
    private int targetX;

    private static final int PATTERN_ZIGZAG = 0;
    private static final int PATTERN_DIVING = 1;
    private static final int PATTERN_SINE_WAVE = 2;
    private static final int PATTERN_SPIRAL = 3;
    private static final int PATTERN_AGGRESSIVE = 4;


    private Bomb bomb;
     // Frame counter for animation
    private int animationFrame = 0;
    private final int ANIMATION_SPEED = 15;
    private static final Rectangle[] ALIEN1_CLIPS = {
        new Rectangle(0, 85, 16, 16),     // Blue alien frame 1
        new Rectangle(17, 85, 16, 16),    // Blue alien frame 2
        new Rectangle(34, 85, 16, 16),    // Blue alien frame 3
        new Rectangle(51, 85, 16, 16),    // Blue alien frame 4
        new Rectangle(68, 85, 16, 16),    // Blue alien frame 5
        new Rectangle(85, 85, 16, 16),    // Blue alien frame 6
        new Rectangle(102, 85, 16, 16),    // Blue alien frame 7
        new Rectangle(119, 85, 16, 16),   // Blue alien frame 8
        new Rectangle(136, 85, 16, 16),   // Blue alien frame 9
        new Rectangle(153, 85, 16, 16),   // Blue alien frame 10
        new Rectangle(170, 85, 16, 16),   // Blue alien frame 11
        new Rectangle(187, 85, 16, 16),   // Blue alien frame 12
        new Rectangle(204, 85, 16, 16),   // Blue alien frame 13
        new Rectangle(221, 85, 16, 16),   // Blue alien frame 14
        new Rectangle(238, 85, 16, 16),   // Blue alien frame 15
        new Rectangle(255, 85, 16, 16) ,
        new Rectangle(272, 85, 16, 16) ,
        new Rectangle(289, 85, 16, 16) ,
        new Rectangle(306, 85, 16, 16) ,
        new Rectangle(323, 85, 16, 16) ,
    };
    private BufferedImage[] animationFrames;
    private int currentFrame = 0;

    public Alien1(int x, int y) {
        super(x, y);
        this.movementPattern = (int)(Math.random() * 5); // 0-4 patterns
        this.baseSpeed = 1 + (int)(Math.random() * 2); // 1-2 speed
        this.horizontalDirection = Math.random() > 0.5 ? 1 : -1;
        initEnemy(x, y);
    }

    private void initEnemy(int x, int y) {

        this.x = x;
        this.y = y;

        bomb = new Bomb(x, y);

        var ii = new ImageIcon(IMG_ENEMY); 
        // Use same image for now, you can change later
        BufferedImage originalImage = new BufferedImage(
            ii.getIconWidth(), 
            ii.getIconHeight(), 
            BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D g2d = originalImage.createGraphics();
        g2d.drawImage(ii.getImage(), 0, 0, null);
        g2d.dispose();

        animationFrames = new BufferedImage[ALIEN1_CLIPS.length];

        for (int i = 0; i < ALIEN1_CLIPS.length; i++) {
            // Clip the specific frame from the sprite sheet
            BufferedImage clippedImage = originalImage.getSubimage(
                ALIEN1_CLIPS[i].x, 
                ALIEN1_CLIPS[i].y, 
                ALIEN1_CLIPS[i].width, 
                ALIEN1_CLIPS[i].height
            );
            int scaledWidth = clippedImage.getWidth() * 3/2;  // 3x bigger (48px)
            int scaledHeight = clippedImage.getHeight() * 3/2; // 3x bigger (48px)


            animationFrames[i] = new BufferedImage(
                scaledWidth, 
                scaledHeight, 
                BufferedImage.TYPE_INT_ARGB
            );
            Graphics2D g2dClip = animationFrames[i].createGraphics();
            g2dClip.drawImage(clippedImage, 0, 0, scaledWidth, scaledHeight, null);
            g2dClip.dispose();
        }
        // Set the first frame as the initial image
        setImage(animationFrames[0]);
    }

    public void act(int direction) {
        animationFrame++;
        if (animationFrame % ANIMATION_SPEED == 0) {
            currentFrame = (currentFrame + 1) % animationFrames.length;
            setImage(animationFrames[currentFrame]);
        }
        switch(movementPattern) {
            case PATTERN_ZIGZAG:
                performZigzagMovement();
                break;
            case PATTERN_DIVING:
                performDivingMovement();
                break;
            case PATTERN_SINE_WAVE:
                performSineWaveMovement();
                break;
            case PATTERN_SPIRAL:
                performSpiralMovement();
                break;
            case PATTERN_AGGRESSIVE:
                performAggressiveMovement();
                break;
            default:
                performBasicMovement();
                break;
        }
       if (this.x < 0) {
            this.x = 0;
            horizontalDirection = 1;
        }
        if (this.x > BOARD_WIDTH - ALIEN_WIDTH) {
            this.x = BOARD_WIDTH - ALIEN_WIDTH;
            horizontalDirection = -1;
        }
        
        // Check if reached bottom
        if (this.y > BOARD_HEIGHT - ALIEN_HEIGHT) {
            setVisible(false);
        } 
    }
    private void performZigzagMovement() {
        // Smooth zigzag pattern
        this.x += Math.sin(animationFrame * 0.08) * 2.5;
        this.y += baseSpeed;
        
        // Add occasional speed bursts
        if (animationFrame % 180 == 0) {
            this.y += 3; // Speed burst
        }
    }
    
    private void performDivingMovement() {
        if (!isDiving) {
            // Normal movement
            this.y += baseSpeed;
            this.x += horizontalDirection * 0.5;
            
            // Start diving randomly
            if (Math.random() < 0.02) { // 2% chance per frame
                isDiving = true;
                diveCounter = 60; // Dive for 60 frames
            }
        } else {
            // Diving behavior
            this.y += baseSpeed * 1; // Fast downward movement
            this.x += horizontalDirection * 0.5; // Faster horizontal movement
            diveCounter--;
            
            if (diveCounter <= 0) {
                isDiving = false;
                horizontalDirection *= -1; // Change direction after dive
            }
        }
    }
    
    private void performSineWaveMovement() {
        // Smooth sine wave pattern
        double amplitude = 3.0;
        double frequency = 0.05;
        
        this.x += Math.sin(animationFrame * frequency) * amplitude;
        this.y += baseSpeed;
        
        // Add vertical oscillation
        this.y += Math.cos(animationFrame * frequency * 2) * 0.5;
    }
    
    private void performSpiralMovement() {
        // Spiral pattern
        double radius = 30;
        double angleIncrement = 0.1;
        
        double centerX = BOARD_WIDTH / 2;
        double angle = animationFrame * angleIncrement;
        
        this.x = (int)(centerX + Math.cos(angle) * radius);
        this.y += baseSpeed;
        
        // Gradually decrease spiral radius
        if (animationFrame % 300 == 0) {
            radius *= 0.9;
        }
    }
    
    private void performAggressiveMovement() {
        // Try to move toward player position (if available)
        // This requires passing player position from Scene1
        this.y += baseSpeed * 1; // Faster movement
        
        // Aggressive horizontal movement
        if (animationFrame % 30 == 0) {
            horizontalDirection = Math.random() > 0.5 ? 1 : -1;
        }
        this.x += horizontalDirection * 2;
        
        // Sudden direction changes
        if (Math.random() < 0.05) { // 5% chance
            horizontalDirection *= -1;
        }
    }
    
    private void performBasicMovement() {
        this.y += baseSpeed;
        this.x += horizontalDirection * 0.3;
    }


    public Bomb getBomb() {

        return bomb;
    }

    
}
