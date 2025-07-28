package gdd.sprite;

import static gdd.Global.*;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

public class Explosion extends Sprite {

    private int animationFrame = 0;
    private final int ANIMATION_SPEED = 7; // How fast the explosion animates
    private BufferedImage[] explosionFrames;
    private int currentFrame = 0;
    private boolean finished = false;

    public Explosion(int x, int y) {
        initExplosion(x, y);
        setVisible(true);
    }

    private void initExplosion(int x, int y) {
        this.x = x;
        this.y = y;

        explosionFrames = new BufferedImage[EXPLOSION_IMAGES.length];
        
        for (int i = 0; i < EXPLOSION_IMAGES.length; i++) {
            var ii = new ImageIcon(EXPLOSION_IMAGES[i]);
            
            // Scale the explosion images to make them smaller
            BufferedImage scaledImage = new BufferedImage(
                ii.getIconWidth() ,  // Make it smaller
                ii.getIconHeight() ,
                BufferedImage.TYPE_INT_ARGB
            );
            
            Graphics2D g2d = scaledImage.createGraphics();
            g2d.drawImage(ii.getImage(), 0, 0, 
                ii.getIconWidth() , 
                ii.getIconHeight(), null);
            g2d.dispose();
            
            explosionFrames[i] = scaledImage;
        }
        
        // Set the first frame as the initial image
        setImage(explosionFrames[0]);
    }

    @Override
    public void act() {
        animationFrame++;
        
        // Change frame every ANIMATION_SPEED ticks
        if (animationFrame % ANIMATION_SPEED == 0) {
            currentFrame++;
            
            if (currentFrame >= explosionFrames.length) {
                // Animation finished
                finished = true;
                setVisible(false);
            } else {
                setImage(explosionFrames[currentFrame]);
            }
        }
    }

    public boolean isFinished() {
        return finished;
    }

    // Alternative method for frame-based visibility countdown
    public void visibleCountDown() {
        if (currentFrame < explosionFrames.length - 1) {
            act(); // Continue animation
        } else {
            setVisible(false);
        }
    }
}