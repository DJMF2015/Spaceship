import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class Board extends JPanel implements ActionListener {
    private final int ICRAFT_X = 40;
    private final int ICRAFT_Y = 60;
    private final int B_WIDTH = 600;
    private final int B_HEIGHT = 500;
    private final int DELAY = 10;
    private Timer timer;
    private SpaceShip spaceShip;
    private List <Alien> aliens;
    private boolean ingame ;

    //  variable to store 2D array of coordinates (for now) of alien positions
    private final int[][] pos = {
            {2380, 29}, {2500, 59}, {1380, 89},
            {780, 109}, {580, 139}, {680, 239},
            {790, 259}, {760, 50}, {790, 150},
            {980, 209}, {560, 45}, {510, 70},
            {930, 159}, {590, 80}, {530, 60},
            {940, 59}, {990, 30}, {920, 200},
            {900, 259}, {660, 50}, {540, 90},
            {810, 220}, {860, 20}, {740, 180},
            {820, 128}, {490, 170}, {700, 30}
    };

    public Board() {
        initBoard();
    }

    private void initBoard() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        setBackground(Color.white);
        setDoubleBuffered(true);
        ingame = true;
        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        spaceShip = new SpaceShip(ICRAFT_X, ICRAFT_Y);
        initAliens();
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void initAliens() {
        aliens = new ArrayList <>();
        for (int[] p : pos) {
            aliens.add(new Alien(p[0], p[1]));
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (ingame) {
//
            doDrawing(g);
        } else {
            drawGameOver(g);
        }
        Toolkit.getDefaultToolkit().sync();
    }

    //draw the spaceship, missile and alien objects
    private void doDrawing(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(spaceShip.getImage(), spaceShip.getX(),
                spaceShip.getY(), this);

        List <Missile> missiles = spaceShip.getMissiles();
        for (Missile missile : missiles) {
            g2d.drawImage(missile.getImage(), missile.getX(),
                    missile.getY(), this);
        }

        for (Alien alien : aliens) {
            if (alien.isVisible()) {
                g.drawImage(alien.getImage(), alien.getX(), alien.getY(), this);
            }
            g.setColor(Color.red);
            g.drawString("ALiens remaining: " + aliens.size(), 35, 55);
        }

    }
        private void drawGameOver (Graphics g){
            String msg = "Game Over" + "Score :" + aliens.size();
            Font small = new Font("Helvetica", Font.BOLD, 16);
            FontMetrics fm = getFontMetrics(small);
            g.setColor(Color.blue);
            g.setFont(small);
            g.drawString(msg, (B_WIDTH - fm.stringWidth(msg)) / 2,
                    B_HEIGHT / 2);
        }


    @Override
    public void actionPerformed(ActionEvent e) {
        updateMissiles();
        updateSpaceShip();
        updateAliens();
        checkCollisions();
        repaint();
    }

    private void ingame() {
        if (!ingame) {
            timer.stop();
        }
    }

    private void updateSpaceShip() {
        if (spaceShip.isVisible()) {
            spaceShip.move();
        }
    }
    private void updateMissiles() {

        List <Missile> missiles = spaceShip.getMissiles();
        for (int i = 0; i < missiles.size(); i++) {
            Missile missile = missiles.get(i);

            if (missile.isVisible()) {
                missile.move();
            } else {
                missiles.remove(i);
            }
        }
    }

    //update alien if 'alive' - else (if hit) remove from list
    private void updateAliens() {
        if (aliens.isEmpty()){
            ingame = false;return;}

        for (int i = 0; i < aliens.size(); i++) {

            Alien alie = aliens.get(i);
            if (alie.isVisible()) {
                alie.move();
            } else {
                aliens.remove(i);
            }
        }
    }

    private class TAdapter extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent e) {
            spaceShip.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            spaceShip.keyPressed(e);
        }

    }

//check whether are spaceship has collided with an alien
    public void checkCollisions() {
        Rectangle r3 = spaceShip.getBounds();
        for (Alien alien : aliens) {
            Rectangle r2 = alien.getBounds();
            if (r3.intersects(r2)) {
                spaceShip.setVisible(false);
                alien.setVisible(false);
                ingame = false;
            }
        }
        //check whether a missile hits an alien
        List <Missile> ms = spaceShip.getMissiles();
        for (Missile m : ms) {
            Rectangle r1 = m.getBounds();
            for (Alien alien : aliens) {
                Rectangle r2 = alien.getBounds();
                if (r1.intersects(r2)) {
                    m.setVisible(false);
                    alien.setVisible(false);
                }
            }
        }

    }
}