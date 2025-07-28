package gdd.sprite;

import java.awt.Image;
import static gdd.Global.*;
import java.awt.Rectangle;
import javax.swing.ImageIcon;

public class BossShot {
    private int x, y;
    private int dx, dy; // Velocity
    private boolean visible;
    private Image image;

    public BossShot(int x, int y) {
        this.x = x;
        this.y = y;
        this.dx = 0; // Default horizontal velocity
        this.dy = 5; // Default vertical velocity (adjust as needed)
        this.visible = true;

        // Load the beam image
        var ii = new ImageIcon("src/images/beams.png");
        this.image = ii.getImage();
    }

    public void move() {
        x += dx;
        y += dy;

        // Mark as invisible if it goes off-screen
        if (y > BOARD_HEIGHT || y < 0) {
            visible = false;
        }
    }

    public Image getImage() {
        return image;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isVisible() {
        return visible;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, image.getWidth(null), image.getHeight(null));
    }
}