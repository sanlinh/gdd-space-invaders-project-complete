package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;
import java.awt.Rectangle;

public class Enemy extends Sprite {

    // private Bomb bomb;

    public Enemy(int x, int y) {

        initEnemy(x, y);
    }

    private void initEnemy(int x, int y) {

        this.x = x;
        this.y = y;

        // bomb = new Bomb(x, y);

        var ii = new ImageIcon(IMG_ENEMY);

        // Scale the image to use the global scaling factor
        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() * SCALE_FACTOR,
                ii.getIconHeight() * SCALE_FACTOR,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
    }

    public void act(int direction) {

        this.x += direction;

        if(this.y >BOARD_HEIGHT-ALIEN_HEIGHT){
            setVisible(visible = false);
        }
    }
    public boolean hasReachedBottom() {
        return this.y > BOARD_HEIGHT - ALIEN_HEIGHT;
    }

    // In Enemy.java, add getBounds() method if missing:
    @Override
    public Rectangle getBounds() {
        if (getImage() != null) {
            return new Rectangle(x, y, getImage().getWidth(null), getImage().getHeight(null));
        } else {
             return new Rectangle(x, y, ALIEN_WIDTH, ALIEN_HEIGHT);
        }
}

}
