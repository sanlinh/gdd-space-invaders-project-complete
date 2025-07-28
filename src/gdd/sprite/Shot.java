package gdd.sprite;

import static gdd.Global.*;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

public class Shot extends Sprite {

    private static final int H_SPACE = 13;
    private static final int V_SPACE = 1;
    private BufferedImage[] bulletFrames;
    private int currentFrame = 0;
    private int animationFrame = 0;
    private final int ANIMATION_SPEED = 10; // Speed of bullet animation
    private boolean finished = false; // To track if the shot animation is finished

    public Shot() {
    }

    public Shot(int x, int y) {

        initShot(x, y);
    }

    private void initShot(int x, int y) {


        //var ii = new ImageIcon(IMG_SHOT);

        // Scale the image to use the global scaling factor
        //var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() * SCALE_FACTOR,
               // ii.getIconHeight() * SCALE_FACTOR, 
               // java.awt.Image.SCALE_SMOOTH);
        //setImage(scaledImage);

    bulletFrames = new BufferedImage[BULLET_IMAGES.length];
        for (int i = 0; i < BULLET_IMAGES.length; i++) {
            var ii = new ImageIcon(BULLET_IMAGES[i]);
            
            // Scale the bullet images to make them smaller
            BufferedImage scaledImage = new BufferedImage(
                ii.getIconWidth() /2,
                ii.getIconHeight() /2,
                BufferedImage.TYPE_INT_ARGB
            );
            
            Graphics2D g2d = scaledImage.createGraphics();
            g2d.drawImage(ii.getImage(), 0, 0, 
                ii.getIconWidth() /2, 
                ii.getIconHeight() /2, null);
            g2d.dispose();
            
            bulletFrames[i] = scaledImage;
        }
        setImage(bulletFrames[0]);

        setX(x + H_SPACE);
        setY(y - V_SPACE);
    }
     @Override
    public void act() {
        animationFrame++;
        
        // Change frame every ANIMATION_SPEED ticks
        if (animationFrame % ANIMATION_SPEED == 0) {
            currentFrame++;

            if (currentFrame >= bulletFrames.length) {
                // Animation finished
                finished = true;
                setVisible(false);
            } else {
                setImage(bulletFrames[currentFrame]);
            }
        }
    }

    public boolean isFinished() {
        return finished;
    }

    // Alternative method for frame-based visibility countdown
    public void visibleCountDown() {
        if (currentFrame < bulletFrames.length - 1) {
            act(); // Continue animation
        } else {
            setVisible(false);
        }
    }
}
