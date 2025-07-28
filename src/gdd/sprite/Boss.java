package gdd.sprite;

import static gdd.Global.*;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;




public class Boss extends Enemy {
    private int animationFrame = 0;
    private final int ANIMATION_SPEED = 10;
    private int health = 50; // Example health value
    private int shotCooldown = 0;

    // Enhanced AI variables
    

    

     // Vertical speed
    private static final Rectangle[] BOSS_CLIPS = {
        new Rectangle(2, 1, 28, 27),
        new Rectangle(34, 1, 28, 28),
        new Rectangle(66, 1, 28, 29),
        new Rectangle(98, 1, 28, 28)
    };
    private BufferedImage[] animationFrames;
    private int currentFrame = 0;

    public Boss(int x, int y) {
        super(x, y);
        
        initBoss();
    }

    private void initBoss() {
        var ii = new ImageIcon(IMG_BOSS);
         // Use same image for now, you can change later
          
        
        BufferedImage originalImage = new BufferedImage(
            ii.getIconWidth(), 
            ii.getIconHeight(), 
            BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g2d = originalImage.createGraphics();
        g2d.drawImage(ii.getImage(), 0, 0, null);
        g2d.dispose();

        animationFrames = new BufferedImage[BOSS_CLIPS.length];

        for (int i = 0; i < BOSS_CLIPS.length; i++) {
            Rectangle clip = BOSS_CLIPS[i];
    // Prevent out-of-bounds
            if (clip.x + clip.width > originalImage.getWidth() || clip.y + clip.height > originalImage.getHeight()) {
                System.err.println("Boss frame " + i + " is out of bounds! Skipping.");
                continue;
            }
            BufferedImage clippedImage = originalImage.getSubimage(
            clip.x, 
            clip.y, 
            clip.width, 
            clip.height
        );
            animationFrames[i] = new BufferedImage(
                clippedImage.getWidth(),
                clippedImage.getHeight(),
                BufferedImage.TYPE_INT_ARGB
         );
        
        int scaledWidth = clippedImage.getWidth() * 3;
        int scaledHeight = clippedImage.getHeight() * 3;
    // Create a new image for the rotated frame
    BufferedImage rotated = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2dRot = rotated.createGraphics();
    // Rotate 180 degrees around the center
    g2dRot.rotate(Math.PI, scaledWidth / 2.0, scaledHeight / 2.0);
    g2dRot.drawImage(clippedImage, 0, 0, scaledWidth, scaledHeight, null);
    g2dRot.dispose();
    animationFrames[i] = rotated;
    }    
        setImage(animationFrames[0]);
        // Different movement pattern - zigzag movement
        this.dx = 3; // Horizontal speed
        this.dy = 1; // Vertical speed
    }

    public boolean canShoot() {
    return shotCooldown <= 0;
}

    public BossShot shoot() {
        shotCooldown = 90; // Cooldown (frames) between shots
        int shotStartX = x + animationFrames[0].getWidth() / 2;
        int shotStartY = y + animationFrames[0].getHeight();
        return new BossShot(shotStartX, shotStartY);
    }

    
    @Override
    public void act(int direction) {
        
        animationFrame++;
        if (shotCooldown > 0) {
        shotCooldown--;
     }

        if (animationFrame % ANIMATION_SPEED == 0) {
            currentFrame = (currentFrame + 1) % animationFrames.length;
            setImage(animationFrames[currentFrame]);
        }

        x += dx;
        if (x < 0 || x > BOARD_WIDTH - animationFrames[0].getWidth()) {
        dx = -dx;
        x += dx;
        // Move down a bit each time boss bounces off the wall
        y += 32;
    }

    // Sine wave vertical movement for unpredictability
    y += Math.sin(animationFrame * 0.05) * 2;

    // Occasional dash (fast move) every few seconds
    if (animationFrame % 300 == 0) {
        dx = (int)(Math.signum(Math.random() - 0.5) * (3 + Math.random() * 4)); // Random fast dash left/right
    }

    // Clamp boss within screen bounds vertically
    if (y < 0) y = 0;
    if (y > BOARD_HEIGHT / 2) y = BOARD_HEIGHT / 2;
    


    }
    
    
    

    public void takeDamage(int dmg) {
    health -= dmg;
    if (health <= 0) {
        setDying(true);
    }
    }

    public int getHealth() {
    return health;
}
}
   

    
