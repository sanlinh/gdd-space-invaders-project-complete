package gdd.scene;

import gdd.AudioPlayer;
import gdd.Game;
import static gdd.Global.*;
import gdd.SoundUtils;
import gdd.SpawnDetails;
import gdd.powerup.PowerUp;
import gdd.powerup.SpeedUp;
import gdd.powerup.TripleShotPowerUp;
import gdd.sprite.Alien1;
import gdd.sprite.Alien2;
import gdd.sprite.Bomb;
import gdd.sprite.Enemy;
import gdd.sprite.Explosion;
import gdd.sprite.Player;
import gdd.sprite.PlayerExplosion;
import gdd.sprite.Shot;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO; // Added for BufferedImage
import javax.swing.ImageIcon; // Added for ImageIO
import javax.swing.JPanel; // Import for font handling
import javax.swing.Timer; // Import for font handling

public class Scene1 extends JPanel {

    private int frame = 0;
    private List<PowerUp> powerups;
    private List<Enemy> enemies;
    private List<Explosion> explosions;
    private List<Shot> shots;
    private List<Bomb> bombs;
    private Player player;

    private int score = 0;
    private int currentSpeed = 0;
    private int shotsUpgradeLevel = 0;
    // Changed Font declarations to be class members
    private Font retroPixelFontSmall;
    private Font retroPixelFontMedium;
    private Font retroPixelFontLarge; // For game over or title
    private Font dashboardFont;
    private Font titleFont;

    final int BLOCKHEIGHT = 50; // These might be related to grid-based movement if applicable
    final int BLOCKWIDTH = 50;

    private int direction = -1;
    private int deaths = 0;

    private boolean inGame = true;
    private String message = "Game Over";

    private final Dimension d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    private final Random randomizer = new Random();

    private Timer timer;
    private final Game game;

    private PlayerExplosion playerExplosion = null;
    private boolean playerDying = false;
    private int deathTimer = 0;

    private HashMap<Integer, List<SpawnDetails>> spawnMap = new HashMap<>();
    private AudioPlayer audioPlayer;
    private AudioPlayer shotAudioPlayer;

    // --- Background Images and related variables (Adapted from Scene2) ---
    private BufferedImage nebulaTopBg;
    private BufferedImage nebulaBottomBg;

    private int backgroundY1 = 0; // Current Y position for the first image
    private int backgroundY2;     // Current Y position for the second image
    private final int BACKGROUND_SCROLL_SPEED = 3; // Adjust this for faster/slower scrolling
    // --- END Background ---

    public Scene1(Game game) {
        this.game = game;
        loadSpawnDetails();
        loadBackgroundImages();
        loadFonts(); // Call new method to load fonts
        // Initialize backgroundY2 after images are loaded, using the height of the first image
        if (nebulaTopBg != null && nebulaBottomBg != null) {
            backgroundY1 = 0; // Starts at the top of the screen
            // Position backgroundY2 directly ABOVE backgroundY1 for downward scrolling
            backgroundY2 = backgroundY1 - nebulaTopBg.getHeight();
        } else {
            // Fallback if images don't load
            backgroundY1 = 0;
            backgroundY2 = -BOARD_HEIGHT; // Position off-screen above
        }
    }

    private void initShotAudio() {
    try {
        shotAudioPlayer = new AudioPlayer("src/audio/laser-shot.wav");
    } catch (Exception e) {
        System.err.println("Error initializing shot audio: " + e.getMessage());
    }
}

    private void loadFonts() {
        try {
            // Load the base font from file
            Font baseFont = Font.createFont(Font.TRUETYPE_FONT, new File(FONT_RETRO_PIXEL));

            // Derive different sizes from the base font
            retroPixelFontSmall = baseFont.deriveFont(Font.PLAIN, 12);
            retroPixelFontMedium = baseFont.deriveFont(Font.PLAIN, 16);
            retroPixelFontLarge = baseFont.deriveFont(Font.PLAIN, 24); // For game over screen

            // Register the fonts with the GraphicsEnvironment (optional but good practice)
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(baseFont); // Registering the base font is usually enough

        } catch (IOException | FontFormatException e) {
            System.err.println("Error loading custom font: " + e.getMessage());
            e.printStackTrace();
            // Fallback to a default system font if custom font fails to load
            retroPixelFontSmall = new Font("Monospaced", Font.PLAIN, 12);
            retroPixelFontMedium = new Font("Monospaced", Font.BOLD, 16);
            retroPixelFontLarge = new Font("Monospaced", Font.BOLD, 24);
        }
    }

    private void initAudio() {
        try {
            String filePath = "src/audio/scene1.wav";
            audioPlayer = new AudioPlayer(filePath);
            audioPlayer.play();
        } catch (Exception e) {
            System.err.println("Error initializing audio player: " + e.getMessage());
        }
    }

    private void loadBackgroundImages() {
        try {
            // Use ImageIO.read for BufferedImage, and File for local paths
            nebulaTopBg = ImageIO.read(new File(IMG_NEBULA_TOP));
            nebulaBottomBg = ImageIO.read(new File(IMG_NEBULA_BOTTOM));

            if (nebulaTopBg == null) {
                 System.err.println("Failed to load nebulaTopBg from: " + IMG_NEBULA_TOP);
            }
            if (nebulaBottomBg == null) {
                 System.err.println("Failed to load nebulaBottomBg from: " + IMG_NEBULA_BOTTOM);
            }
        } catch (IOException e) { // Catch IOException for ImageIO.read
            System.err.println("Error loading background images: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for more details
        }
    }

    private void loadSpawnDetails() {
        loadSpawnFromCSV("src/spawns.csv");
    }

    private void loadSpawnFromCSV(String csvFile) {
        String line = "";
        String csvSplitBy = ",";
        boolean isHeader = true;

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {

                if (isHeader) {
                    isHeader = false;
                    continue; // Skip header line
                }
                String[] data = line.split(csvSplitBy);

                if (data.length >= 4) {
                    try {
                        int frame = Integer.parseInt(data[0].trim());
                        String type = data[1].trim();
                        int x = Integer.parseInt(data[2].trim());
                        int y = Integer.parseInt(data[3].trim());

                        spawnMap.computeIfAbsent(frame, k -> new ArrayList<>())
                                .add(new SpawnDetails(type, x, y));

                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing line: " + line);
                    }
                }
            }

            System.out.println("Loaded " + spawnMap.size() + " spawn details from CSV");

        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
            loadDefaultSpawns(); // Fallback to default spawns
        }
    }

    private void loadDefaultSpawns() {
        // ... (Your existing default spawn logic) ...
        List<SpawnDetails> frame0Spawns = new ArrayList<>();
        frame0Spawns.add(new SpawnDetails("Alien1", 100, 50));
        spawnMap.put(0, frame0Spawns);

        List<SpawnDetails> frame60Spawns = new ArrayList<>();
        frame60Spawns.add(new SpawnDetails("Alien2", 200, 50));
        spawnMap.put(60, frame60Spawns);

        List<SpawnDetails> frame120Spawns = new ArrayList<>();
        frame120Spawns.add(new SpawnDetails("Alien1", 300, 50));
        spawnMap.put(120, frame120Spawns);

        List<SpawnDetails> frame180Spawns = new ArrayList<>();
        frame180Spawns.add(new SpawnDetails("Alien2", 400, 50));
        spawnMap.put(180, frame180Spawns);
        System.out.println("Loaded default spawns");
    }

    public void start() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        requestFocusInWindow();
        setBackground(Color.black);

        timer = new Timer(1000 / 60, new GameCycle());
        timer.start();

        gameInit();
        initAudio();
    }

    public void stop() {
        timer.stop();
        try {
            if (audioPlayer != null) {
                audioPlayer.stop();
            }
        } catch (Exception e) {
            System.err.println("Error closing audio player.");
        }
    }

    private void gameInit() {
        enemies = new ArrayList<>();
        powerups = new ArrayList<>();
        explosions = new ArrayList<>();
        shots = new ArrayList<>();
        bombs = new ArrayList<>();
        player = new Player();
    }

    private void drawMap(Graphics g) {
        // Draw background elements in correct order: Nebulas -> Stars
        drawNebulaBackground(g);
        drawTwinklingStars(g);
        drawColoredStars(g);
    }

    // --- REVISED METHOD: Draw Nebula Background (Adapted from Scene2) ---
    private void drawNebulaBackground(Graphics g) {
        if (nebulaTopBg != null && nebulaBottomBg != null) {
            // Draw the two background images
            g.drawImage(nebulaTopBg, 0, backgroundY1, BOARD_WIDTH, nebulaTopBg.getHeight(), this);
            g.drawImage(nebulaBottomBg, 0, backgroundY2, BOARD_WIDTH, nebulaBottomBg.getHeight(), this);

            // If an image scrolls completely off-screen, reset its position to loop
            // For continuous scrolling, imagine image1 is followed by image2, and when image1
            // is fully off-screen, it 'teleports' to follow image2.
            if (backgroundY1 >= BOARD_HEIGHT) { // If top image goes off bottom of screen
                backgroundY1 = backgroundY2 - nebulaTopBg.getHeight(); // Position it above the other image
            }
            if (backgroundY2 >= BOARD_HEIGHT) { // If bottom image goes off bottom of screen
                backgroundY2 = backgroundY1 - nebulaBottomBg.getHeight(); // Position it above the other image
            } // Position it below image1
            }
        }
    
    // --- END REVISED METHOD ---


    private void drawTwinklingStars(Graphics g) {
        Random twinkleRandom = new Random(54321);

        for (int i = 0; i < 30; i++) {
            int baseX = twinkleRandom.nextInt(BOARD_WIDTH);
            // Adjust base Y relative to the entire possible scroll area to keep stars somewhat static in the *background* space
            int baseY = twinkleRandom.nextInt(BOARD_HEIGHT * 2);

            // Simulate parallax scrolling: Stars move slower than the background
            // Use backgroundY1 or backgroundY2, or an average, for consistent parallax.
            // A common approach is to make stars scroll at a fraction of the background speed.
            int starY = (baseY + (backgroundY1 / 3)) % (BOARD_HEIGHT + 100) - 50; // Slower than nebula

            if (starY >= 0 && starY <= BOARD_HEIGHT) {
                int twinkle = (frame + i * 10) % 120;
                int alpha = 100 + (int) (Math.sin(twinkle * 0.1) * 155);
                alpha = Math.max(50, Math.min(255, alpha));

                Color twinkleColor = new Color(255, 255, 255, alpha);
                g.setColor(twinkleColor);

                int size = 2 + (alpha > 200 ? 1 : 0);
                g.fillOval(baseX, starY, size, size);

                if (alpha > 180) {
                    g.drawLine(baseX - 2, starY + 1, baseX + size + 2, starY + 1);
                    g.drawLine(baseX + 1, starY - 2, baseX + 1, starY + size + 2);
                }
            }
        }
    }


    private void drawColoredStars(Graphics g) {
        Color[] starColors = {
            new Color(255, 200, 200), // Light red
            new Color(200, 200, 255), // Light blue
            new Color(255, 255, 200), // Light yellow
            new Color(200, 255, 200) // Light green
        };

        Random colorRandom = new Random(98765);

        for (int i = 0; i < 20; i++) {
            int baseX = colorRandom.nextInt(BOARD_WIDTH);
            int baseY = colorRandom.nextInt(BOARD_HEIGHT * 2);

            // Adjust star scrolling based on the nebula scroll speed for parallax effect
            int starY = (baseY + (backgroundY1 / 6)) % (BOARD_HEIGHT + 100) - 50; // Even slower than closer stars

            if (starY >= 0 && starY <= BOARD_HEIGHT) {
                Color starColor = starColors[i % starColors.length];
                g.setColor(starColor);
                g.fillOval(baseX, starY, 2, 2);
            }
        }
    }

    private void drawAliens(Graphics g) {
        for (Enemy enemy : enemies) {
            if (enemy.isVisible()) {
                g.drawImage(enemy.getImage(), enemy.getX(), enemy.getY(), this);
            }
            if (enemy.isDying()) {
                enemy.die();
            }
        }
    }

    private void drawDashboard(Graphics g) {

        g.setColor(Color.WHITE);
        g.setFont(retroPixelFontMedium); // Use a medium size for titles
        g.drawString("Score: " + score, 20, 30);

        g.setColor(Color.YELLOW);
        g.setFont(retroPixelFontSmall); // Use a smaller size for values
        g.drawString("Speed: " + getspeedstatus(), 20, 50);

        g.setColor(Color.GREEN);
        g.setFont(retroPixelFontSmall);
        String shotStatus = getShotUpgradeStatus();
        g.drawString("Shots Level: " + shotStatus, 20, 70); // Adjust Y-coordinate

        g.setColor(Color.CYAN);
        g.setFont(retroPixelFontLarge); // Use a larger font for stage title
        g.drawString("STAGE 1",500, 30);
    }

    public String getspeedstatus() {
        if (currentSpeed <= 0) {
            return "0";
        }
        switch (currentSpeed) {
            case 1: return "1";
            case 2: return "2";
            case 3: return "3";
            case 4: return "4(MAX)";
            default: return "4(MAX)";
        }
    }

    private String getShotUpgradeStatus() {
        int shotCount = player.getEffectiveShotCount();
        switch (shotCount) {
            case 1: return "Single Shot";
            case 2: return "Double Shot";
            case 3: return "Triple Shot";
            case 4: return "Quad Shot-MAX";
            default: return "Quad Shot-MAX";
        }
    }

    private void drawPowerUps(Graphics g) {
        for (PowerUp p : powerups) {
            if (p.isVisible()) {
                g.drawImage(p.getImage(), p.getX(), p.getY(), this);
            }
            if (p.isDying()) {
                p.die();
            }
        }
    }

    private void drawPlayer(Graphics g) {
        if (player.isVisible()) {
            g.drawImage(player.getImage(), player.getX(), player.getY(), this);
        }
    }

    private void drawShot(Graphics g) {
        List<Shot> toRemove = new ArrayList<>();
        for (Shot shot : shots) {
            if (shot.isVisible()) {
                g.drawImage(shot.getImage(), shot.getX(), shot.getY(), this);
                shot.act();
                if (!shot.isVisible()) {
                    toRemove.add(shot);
                }
            }
            if (shot.isFinished()) {
                toRemove.add(shot);
            }
        }
        shots.removeAll(toRemove);
    }

    public void drawBombs(Graphics g) {
        List<Bomb> toRemove = new ArrayList<>();
        for (Bomb bomb : bombs) {
            if(bomb.isVisible() && !bomb.isDestroyed()){
                g.drawImage(bomb.getImage(), bomb.getX(), bomb.getY(), this);
                bomb.act();
                if (bomb.isDestroyed()){
                    toRemove.add(bomb);
                }
            }
        }
        bombs.removeAll(toRemove);
    }

    private void drawExplosions(Graphics g) {
        List<Explosion> toRemove = new ArrayList<>();
        for (Explosion explosion : explosions) {
            if (explosion.isVisible()) {
                g.drawImage(explosion.getImage(), explosion.getX(), explosion.getY(), this);
                explosion.act();
                if (!explosion.isVisible()) {
                    toRemove.add(explosion);
                }
            }
            if (explosion.isFinished()) {
                toRemove.add(explosion);
            }
        }
        explosions.removeAll(toRemove);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Call JPanel's paintComponent to ensure proper rendering

        doDrawing(g);
    }

    private void doDrawing(Graphics g) {
        // Clear the entire screen with black, before drawing anything
        g.setColor(Color.black);
        g.fillRect(0, 0, d.width, d.height);

        // Debug string "FRAME: " can be removed later
        g.setColor(Color.white);
        g.drawString("FRAME: " + frame, 10, 10); 

        if (inGame) {
            drawMap(g); // Draws nebula and stars (background)

            // Draw all game elements on top of the background
            drawExplosions(g);
            drawPowerUps(g);
            drawAliens(g);
            drawPlayer(g);
            drawShot(g);
            drawBombs(g);
            handleAlien2Bombs(g);

            if (playerExplosion != null && !playerExplosion.isFinished()) {
                playerExplosion.draw(g);
            }
            
            drawDashboard(g); // Draw dashboard last so it's on top of everything
        } else {
            if (timer.isRunning()) {
                timer.stop();
            }
            gameOver(g);
        }

        Toolkit.getDefaultToolkit().sync();
    }

    private void gameOver(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        g.setColor(new Color(0, 32, 48));
        g.fillRect(50, BOARD_HEIGHT / 2 - 30, BOARD_WIDTH - 100, 50); // Use BOARD_HEIGHT for vertical centering
        g.setColor(Color.white);
        g.drawRect(50, BOARD_HEIGHT / 2 - 30, BOARD_WIDTH - 100, 50);

        var small = new Font("Helvetica", Font.BOLD, 14);
        var fontMetrics = this.getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(message, (BOARD_WIDTH - fontMetrics.stringWidth(message)) / 2,
                BOARD_HEIGHT / 2); // Use BOARD_HEIGHT for vertical centering
    }

    private void handleAlien2Bombs(Graphics g) {
        for (Enemy enemy : enemies) {
            if (enemy instanceof Alien2 && enemy.isVisible()) {
                Alien2 alien2 = (Alien2) enemy;
                Bomb bomb = alien2.getBomb();

                if (bomb != null && !bomb.isDestroyed() && bomb.isVisible()) {
                    g.drawImage(bomb.getImage(), bomb.getX(), bomb.getY(), this);
                    bomb.act();

                    if (bomb.getBounds().intersects(player.getBounds())) {
                        bomb.setDestroyed(true);
                        playerExplosion = new PlayerExplosion(
                            player.getX() + PLAYER_WIDTH / 2,
                            player.getY() + PLAYER_HEIGHT / 2
                        );
                        explosions.add(new Explosion(player.getX(), player.getY()));
                        bomb.setDestroyed(true);
                        playerDying = true;
                        deathTimer = 60; // Give time for explosion animation
                        player.setVisible(false);
                        enemy.setVisible(false); 
                        // The playerDying flag in update() will handle the inGame=false transition
                    }
                }
            }
        }
    }

    private void checkEnemyPlayerCollisions() {
        if (!player.isVisible() || player.isDying() || playerDying) {
            return;
        }

        for (Enemy enemy : enemies) {
            if (enemy.isVisible()) {
                if (enemy.getBounds().intersects(player.getBounds())) {
                    playerExplosion = new PlayerExplosion(
                            player.getX() + PLAYER_WIDTH / 2,
                            player.getY() + PLAYER_HEIGHT / 2
                    );
                    explosions.add(new Explosion(enemy.getX(), enemy.getY()));

                    playerDying = true;
                    deathTimer = 60; // Give time for explosion animation
                    player.setVisible(false);
                    enemy.setVisible(false); // Enemy also "dies" on collision

                    return;
                }
            }
        }
    }

    private void update() {
        // Handle player dying state first
        if (playerDying) {
            if (playerExplosion != null) {
                playerExplosion.update();
                if (playerExplosion.isFinished() && deathTimer <= 0) {
                    inGame = false;
                    timer.stop();
                    message = "GAME OVER - PLAYER DESTROYED!";
                    playerDying = false; // Reset flag
                }
            }
            deathTimer--;
            return; // Exit update early if player is dying
        }

        // --- REVISED: Update nebula background scroll position (Adapted from Scene2) ---
        backgroundY1 += BACKGROUND_SCROLL_SPEED;
        backgroundY2 += BACKGROUND_SCROLL_SPEED;

        // If an image scrolls completely off-screen, reset its position to loop
        if (nebulaTopBg != null && nebulaBottomBg != null) {
            if (backgroundY1 >= BOARD_HEIGHT) {
                backgroundY1 = backgroundY2 - nebulaTopBg.getHeight();
            }
            if (backgroundY2 >= BOARD_HEIGHT) {
                backgroundY2 = backgroundY1 - nebulaBottomBg.getHeight();
            }
        }
        // --- END REVISED ---


        // Check enemy spawn
        List<SpawnDetails> spawnsThisFrame = spawnMap.get(frame);
        if (spawnsThisFrame != null) {
            for (SpawnDetails sd : spawnsThisFrame) {
                for (int i = 0; i < sd.count; i++) {
                    int spawnX = sd.x + (i * sd.spacing);
                    switch (sd.type) {
                        case "Alien1": enemies.add(new Alien1(spawnX, sd.y)); break;
                        case "Alien2": enemies.add(new Alien2(sd.x, sd.y)); break;
                        case "Bomb": bombs.add(new Bomb(sd.x, sd.y)); break;
                        case "PowerUp-SpeedUp": powerups.add(new SpeedUp(spawnX, sd.y)); break; // Adjusted to use spawnX
                        case "PowerUp-TripleShotPowerUp": powerups.add(new TripleShotPowerUp(spawnX, sd.y)); break; // Adjusted to use spawnX
                        default: System.out.println("Unknown enemy type: " + sd.type); break;
                    }
                }
            }
        }

        // player
        player.act();

        // Power-ups
        List<PowerUp> powerupsToRemove = new ArrayList<>();
        for (PowerUp powerup : powerups) {
            if (powerup.isVisible()) {
                powerup.act();
                if (powerup.collidesWith(player)) {
                    powerup.upgrade(player);
                    if (powerup instanceof SpeedUp) { currentSpeed++; score += 50; }
                    else if (powerup instanceof TripleShotPowerUp) { shotsUpgradeLevel++; score += 75; }
                    powerupsToRemove.add(powerup);
                }
            }
        }
        powerups.removeAll(powerupsToRemove);

        // Enemies
        for (Enemy enemy : enemies) {
            if (enemy.isVisible()) {
                enemy.act(direction);
                if(enemy.hasReachedBottom()){
                    inGame = false;
                    timer.stop();
                    message = "Aliens reached the bottom!";
                    return;
                }
                if (randomizer.nextInt(1000) < getEnemyAggressiveness()) {
                    if (enemy instanceof Alien2) {
                        Alien2 alien2 = (Alien2) enemy;
                        Bomb enemyBomb = alien2.getBomb();
                        if (enemyBomb != null && enemyBomb.isDestroyed()) {
                            int bombX = enemy.getX() + ALIEN_WIDTH / 2;
                            int bombY = enemy.getY() + ALIEN_HEIGHT;
                            int playerX = player.getX();
                            if (Math.abs(bombX - playerX) < 100) {
                                bombX += (playerX - bombX) * 0.3;
                            }
                            enemyBomb.setX(bombX);
                            enemyBomb.setY(bombY);
                            enemyBomb.setDestroyed(false);
                        }
                    }
                }
            }
            if (enemy.isDying()) {
                enemy.die();
                
            }
        }
        checkEnemyPlayerCollisions();

        // shot
        List<Shot> shotsToRemove = new ArrayList<>();
        for (Shot shot : shots) {
            if (shot.isVisible()) {
               
            
                int shotX = shot.getX();
                int shotY = shot.getY();

                for (Enemy enemy : enemies) {
                    if (enemy.isVisible() && shot.isVisible() && shot.getBounds().intersects(enemy.getBounds())) {
                        var ii = new ImageIcon(IMG_EXPLOSION);
                        enemy.setImage(ii.getImage());
                        enemy.setDying(true);
                        explosions.add(new Explosion(enemy.getX(), enemy.getY())); // Use enemy's position for explosion
                        SoundUtils.playSound("src/audio/explode.wav");
                        deaths++;
                        score += 100;
                        shot.die();
                        shotsToRemove.add(shot);
                        break;
                    }
                }
                int y = shot.getY();
                y -= 20; // This is the shot's vertical speed
                if (y < 0) {
                    shot.die();
                    shotsToRemove.add(shot);
                } else {
                    shot.setY(y);
                }
            }
        }
        shots.removeAll(shotsToRemove);

        if (deaths == NUMBER_OF_ALIENS_TO_DESTROY){
            inGame = false;
            timer.stop();
            message = "YOU WON! ALL ALIENS DESTROYED!";
            System.out.println("You won! All aliens destroyed.");
            game.loadScene2();
        }
    }

    

    private int getEnemyAggressiveness() {
        int baseRate = 5;
        int timeBonus = frame / 1800;
        return Math.min(baseRate + timeBonus, 20);
    }

    private void doGameCycle() {
        frame++;
        update();
        repaint();
    }

    private class GameCycle implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            doGameCycle();
        }
    }

    private class TAdapter extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent e) {
            player.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            System.out.println("Scene1.keyPressed: " + e.getKeyCode()); 

            player.keyPressed(e);

            int x = player.getX();
            int y = player.getY();

            int key = e.getKeyCode();

            if (key == KeyEvent.VK_SPACE && inGame) {
                System.out.println("Shots: " + shots.size());
                if (shots.size() < 8) {
                    int centerX = x;
                    int shotCount = player.getEffectiveShotCount();
                    if (shotCount == 1) {
                        shots.add(new Shot(centerX, y));
                    } else if (shotCount == 2) {
                        shots.add(new Shot(centerX - 10, y));
                        shots.add(new Shot(centerX + 10, y));
                    } else if (shotCount == 3) {
                        shots.add(new Shot(centerX, y));
                        shots.add(new Shot(centerX - 15, y));
                        shots.add(new Shot(centerX + 15, y));
                    } else if (shotCount >= 4) {
                        shots.add(new Shot(centerX - 20, y));
                        shots.add(new Shot(centerX - 7, y));
                        shots.add(new Shot(centerX + 7, y));
                        shots.add(new Shot(centerX + 20, y));
                    }
                }

                SoundUtils.playSound( "src/audio/laser-shot.wav");
        
            }
            
            
        }
    }
}