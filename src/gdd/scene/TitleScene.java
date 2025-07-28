package gdd.scene;

import gdd.AudioPlayer;
import gdd.Game;
import static gdd.Global.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class TitleScene extends JPanel {

    private int frame = 0;
    private Image image;
    private AudioPlayer audioPlayer;
    private final Dimension d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    private Timer timer;
    private Game game;

    public TitleScene(Game game) {
        this.game = game;
        // initBoard();
        // initTitle();
    }

    private void initBoard() {

    }

    public void start() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        setBackground(Color.black);

        timer = new Timer(1000 / 60, new GameCycle());
        timer.start();

        initTitle();
        initAudio();
        
    }

    public void stop() {
        try {
            if (timer != null) {
                timer.stop();
            }

            if (audioPlayer != null) {
                audioPlayer.stop();
            }
        } catch (Exception e) {
            System.err.println("Error closing audio player.");
        }
    }

    private void initTitle() {
        var ii = new ImageIcon(IMG_TITLE);
        image = ii.getImage();

    }

    private void initAudio() {
        try {
            String filePath = "src/audio/title.wav";
            audioPlayer = new AudioPlayer(filePath);

            audioPlayer.play();
        } catch (Exception e) {
            System.err.println("Error with playing sound.");
        }

    }

    @Override
public void paintComponent(Graphics g) {
    super.paintComponent(g);

    doDrawing(g);
}

private void doDrawing(Graphics g) {
    // Black background
    g.setColor(Color.BLACK);
    g.fillRect(0, 0, d.width, d.height);

    // Draw animated stars background
    drawStarField(g);

    // Main title
    g.setColor(Color.WHITE);
    g.setFont(g.getFont().deriveFont(Font.BOLD, 48f));
    String title = "GALACTIC DEFENDER";
    int titleWidth = g.getFontMetrics().stringWidth(title);
    g.drawString(title, (BOARD_WIDTH - titleWidth) / 2, 150);

    // Add subtitle
    g.setColor(Color.YELLOW);
    g.setFont(g.getFont().deriveFont(Font.ITALIC, 24f));
    String subtitle = "Space Invaders";
    int subtitleWidth = g.getFontMetrics().stringWidth(subtitle);
    g.drawString(subtitle, (BOARD_WIDTH - subtitleWidth) / 2, 180);

    // Team members section
    g.setColor(Color.CYAN);
    g.setFont(g.getFont().deriveFont(Font.BOLD, 28f));
    String teamHeader = "Team Members:";
    int headerWidth = g.getFontMetrics().stringWidth(teamHeader);
    g.drawString(teamHeader, (BOARD_WIDTH - headerWidth) / 2, 280);

    // Team member names
    g.setColor(Color.LIGHT_GRAY);
    g.setFont(g.getFont().deriveFont(Font.PLAIN, 22f));
    String[] teamMembers = {
        "Sai Naing Yi Tun",
        "San Lin Htet"
    };

    int startY = 320;
    for (int i = 0; i < teamMembers.length; i++) {
        int width = g.getFontMetrics().stringWidth(teamMembers[i]);
        g.drawString(teamMembers[i], (BOARD_WIDTH - width) / 2, startY + i * 35);
    }

    // Animated "Press SPACE to Start" text
    if (frame % 60 < 30) {
        g.setColor(Color.RED);
    } else {
        g.setColor(Color.WHITE);
    }
    g.setFont(g.getFont().deriveFont(Font.BOLD, 32f));
    String text = "Press SPACE to Start";
    int stringWidth = g.getFontMetrics().stringWidth(text);
    int x = (d.width - stringWidth) / 2;
    g.drawString(text, x, 500);

    // Credits
    g.setColor(Color.GRAY);
    g.setFont(g.getFont().deriveFont(Font.PLAIN, 12f));
    String credits = "Game Design and Development Project";
    int creditsWidth = g.getFontMetrics().stringWidth(credits);
    g.drawString(credits, (BOARD_WIDTH - creditsWidth) / 2, 580);

    Toolkit.getDefaultToolkit().sync();
}

private void drawStarField(Graphics g) {
    g.setColor(Color.WHITE);
    
    // Draw animated stars
    for (int i = 0; i < 100; i++) {
        int x = (i * 17) % BOARD_WIDTH;
        int y = (i * 23 + frame / 2) % BOARD_HEIGHT;
        int brightness = (i % 3) + 1;
        
        // Vary star sizes and brightness
        if (brightness == 1) {
            g.fillOval(x, y, 1, 1);
        } else if (brightness == 2) {
            g.fillOval(x, y, 2, 2);
        } else {
            g.fillOval(x, y, 3, 3);
        }
    }
    
    // Add some twinkling effect
    g.setColor(Color.YELLOW);
    for (int i = 0; i < 20; i++) {
        int x = (i * 31) % BOARD_WIDTH;
        int y = (i * 37 + frame / 3) % BOARD_HEIGHT;
        if ((frame + i * 10) % 120 < 60) {
            g.fillOval(x, y, 2, 2);
        }
    }
}
    private void update() {
        frame++;
    }

    private void doGameCycle() {
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

        }

        @Override
        public void keyPressed(KeyEvent e) {
            System.out.println("Title.keyPressed: " + e.getKeyCode());
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_SPACE) {
                // Load the next scene
                game.loadScene1();
            }

        }
    }
}
