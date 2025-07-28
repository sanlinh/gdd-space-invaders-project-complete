package gdd.sprite;
import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.ImageIcon;


public class Bomb extends Sprite {

    private boolean destroyed = true;
    private int speed = 3;

    public Bomb(int x, int y) {
        initBomb(x, y);
    }
    private void initBomb(int x, int y) {
        this.x = x;
        this.y = y;
        this.destroyed = false;
        setDestroyed(true); // Initially set to destroyed

        var bombImg = "src/images/bomb.png";
        var ii = new ImageIcon(bombImg);
        Image scaledBomb = ii.getImage().getScaledInstance(
            ii.getIconWidth() / 2,    // Make it 2x wider
            ii.getIconHeight() / 2,   // Make it 2x taller
            Image.SCALE_SMOOTH        // Smooth scaling
        );
        setImage(scaledBomb);
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
        setVisible(!destroyed);
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public void act() {
        if (!destroyed) {
            y += speed; // Move the bomb downwards
            if (y > 800) { // If it goes off screen
                setDestroyed(true); // Mark as destroyed
            }
        }
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, getImage().getWidth(null), getImage().getHeight(null));
    }

}
