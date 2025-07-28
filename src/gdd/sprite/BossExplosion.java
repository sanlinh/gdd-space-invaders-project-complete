package gdd.sprite;

import static gdd.Global.*;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

public class BossExplosion {
    private int x, y;
    private int currentFrame = 0;
    private int animationTimer = 0;
    private boolean finished = false;
    private BufferedImage[] explosionFrames;
    private final int FRAME_COUNT = 16;//umber of frames in your sprite sheet
    private final int ANIMATION_SPEED = 1;// Change frame every 3 game frames
    
    public BossExplosion(int x, int y) {
        this.x = x;
        this.y = y;
        loadExplosionFrames();
    }
    
    private void loadExplosionFrames() {
        try {
            BufferedImage spriteSheet = ImageIO.read(new File("src/images/playerexplode.png"));
            explosionFrames = new BufferedImage[FRAME_COUNT];
            
            int frameWidth = spriteSheet.getWidth() / FRAME_COUNT;
            int frameHeight = spriteSheet.getHeight();
            
            // Extract each frame from the sprite sheet
            for (int i = 0; i < FRAME_COUNT; i++) {
                explosionFrames[i] = spriteSheet.getSubimage(
                    i * frameWidth, 0, frameWidth, frameHeight
                );
            }
        } catch (Exception e) {
            System.err.println("Error loading explosion sprite sheet: " + e.getMessage());
        }
    }
    
    public void update() {
        animationTimer++;
        
        if (animationTimer >= ANIMATION_SPEED) {
            currentFrame++;
            animationTimer = 0;
            
            if (currentFrame >= FRAME_COUNT) {
                finished = true;
            }
        }
    }
    
    public void draw(Graphics g) {
        if (!finished && explosionFrames != null && currentFrame < explosionFrames.length) {
            // Scale the explosion to be bigger
            int scaledWidth = explosionFrames[currentFrame].getWidth() ;
            int scaledHeight = explosionFrames[currentFrame].getHeight() ;
            
            // Center the explosion on the player
            int drawX = x - scaledWidth ;
            int drawY = y - scaledHeight ;
            
            g.drawImage(explosionFrames[currentFrame], drawX, drawY, 
                       scaledWidth, scaledHeight, null);
        }
    }
    
    public boolean isFinished() {
        return finished;
    }
    
    public int getX() { return x; }
    public int getY() { return y; }
}