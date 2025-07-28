package gdd.powerup;

import static gdd.Global.*;
import gdd.sprite.Player;
import javax.swing.ImageIcon;

public class TripleShotPowerUp extends PowerUp {
    
    public TripleShotPowerUp(int x, int y) {
        super(x, y);
        initPowerUp();
    }
    
    private void initPowerUp() {
        // You'll need to add a power-up image to your assets
        var ii = new ImageIcon(IMG_POWERUP_TRIPLE);
        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() / 8,
                ii.getIconHeight() / 8,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
    }
    
    @Override
    public void upgrade(Player player) {
        player.upgradeShotLevel();
        this.die();
    }

    @Override
    public void act(){
        y+=2;
        if (y>BOARD_HEIGHT){
            setVisible(false);
        }
    }

    
}
