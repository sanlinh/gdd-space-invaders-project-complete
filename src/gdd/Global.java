package gdd;

public class Global {
    private Global() {
        // Prevent instantiation
    }

    public static final int SCALE_FACTOR = 3; // Scaling factor for sprites

    public static final int BOARD_WIDTH = 716; // Doubled from 358
    public static final int BOARD_HEIGHT = 700; // Doubled from 350
    public static final int BORDER_RIGHT = 60; // Doubled from 30
    public static final int BORDER_LEFT = 10; // Doubled from 5

    public static final int GROUND = 580; // Doubled from 290
    public static final int BOMB_HEIGHT = 10; // Doubled from 5

    public static final int ALIEN_HEIGHT = 50; // Doubled from 12
    public static final int ALIEN_WIDTH = 50; // Doubled from 12
    public static final int ALIEN_INIT_X = 300; // Doubled from 150
    public static final int ALIEN_INIT_Y = 10; // Doubled from 5
    public static final int ALIEN_GAP = 30; // Gap between aliens

    public static final int GO_DOWN = 30; // Doubled from 15
   // public static final int NUMBER_OF_ALIENS_TO_DESTROY = 24;
    public static final int CHANCE = 5;
    public static final int DELAY = 17;
    public static final int PLAYER_WIDTH = 0; // Doubled from 15
    public static final int PLAYER_HEIGHT = 0; // Doubled from 10 
    // number of aliens to destroy
    public static final int NUMBER_OF_ALIENS_TO_DESTROY = 30;

    // Images

    public static final String IMG_NEBULA_TOP = "src/images/n2-top@3x.png";
    public static final String IMG_NEBULA_BOTTOM = "src/images/n2-bottom@3x.png"; 
    public static final String IMG_NEBULA_TOP1 = "src/images/n3-top@3x.png";
    public static final String IMG_NEBULA_BOTTOM1 = "src/images/n3-bottom@3x.png";
    public static final String IMG_ENEMY = "src/images/alien.png";
    public static final String IMG_PLAYER = "src/images/player1.png";
    public static final String IMG_SHOT = "src/images/shot.png";
    public static final String IMG_EXPLOSION = "src/images/explosion.png";
    public static final String IMG_BOSS = "src/images/boss.png"; // Path to the boss sprite
    public static final String IMG_BOSS_EXOPLODE = "src/images/boss_explode.png"; // Path to the bomb sprite
    public static final String IMG_TITLE = "src/images/title.png";
    public static final String IMG_POWERUP_SPEEDUP = "src/images/powerup-s.png";
    public static final String IMG_POWERUP_TRIPLE = "src/images/powerup_triple.png"; 
    public static final String IMG_ALIENUFO="src/images/AlienUFO.png";
    public static final String IMG_ALIEN3 = "src/images/alien3.png"; // Path to the second alien sprite

    // Fonts
    public static final String FONT_RETRO_PIXEL = "src/fonts/dogica.ttf";

    public static final String[] EXPLOSION_IMAGES ={
        "src/images/explode-0.png",
        "src/images/explode-1.png",
        "src/images/explode-2.png",
        "src/images/explode-3.png",
        "src/images/explode-4.png"
    };

    public static final String[] BULLET_IMAGES={
        "src/images/bullet-0.png",
        "src/images/bullet-1.png",
        "src/images/bullet-2.png"
       };

    public static final String[] PLAYER_IMAGES={
        "src/images/playerLeft.png",   // Index 0 - moving left
        "src/images/player.png",       // Index 1 - center/idle
        "src/images/playerRight.png"   // Index 2 - moving right
    };
    public static final int PLAYER_LEFT = 0;
    public static final int PLAYER_CENTER = 1;
    public static final int PLAYER_RIGHT = 2;
}
