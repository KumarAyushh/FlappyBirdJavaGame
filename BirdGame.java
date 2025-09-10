import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

class BirdGame extends JPanel implements ActionListener, KeyListener {
    private int birdY = 200;
    private int birdVelocity = 0;
    private int gravity = 1;
    private boolean isJumping = false;
    private int pipeWidth = 100;
    private Timer timer;
    private boolean gameStarted = false;
    private boolean gameOver = false;
    private int score = 0;
    private boolean restartClicked = false;
    private BufferedImage birdImage;
    private ArrayList<Pipe> pipes; // List of pipes
    private Random rand;

    public BirdGame() {
        setPreferredSize(new Dimension(800, 600));
        addKeyListener(this);
        setFocusable(true);
        timer = new Timer(20, this);
        timer.start();

        rand = new Random();
        pipes = new ArrayList<>();

        // Create initial pipes
        for (int i = 0; i < 3; i++) {
            int pipeX = 500 + i * 300; // distance between consecutive pipes
            int pipeHeight = 100 + rand.nextInt(300); // random pipe height
            int pipeGap = 160 + rand.nextInt(100); // gap always ≥ 160
            pipes.add(new Pipe(pipeX, pipeHeight, pipeGap));
        }

        // Load bird image
        try {
            birdImage = ImageIO.read(new File("C:/Users/krayu/Downloads/bird.png.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int mouseX = e.getX();
                int mouseY = e.getY();

                if (gameOver && mouseX >= 350 && mouseX <= 450 && mouseY >= 400 && mouseY <= 450) {
                    restartGame();
                }
            }
        });
    }

    private void restartGame() {
        birdY = 200;
        birdVelocity = 0;
        score = 0;
        gameOver = false;
        restartClicked = true;
        pipes.clear();

        // Reset pipes
        for (int i = 0; i < 3; i++) {
            int pipeX = 500 + i * 300;
            int pipeHeight = 100 + rand.nextInt(300);
            int pipeGap = 160 + rand.nextInt(100);
            pipes.add(new Pipe(pipeX, pipeHeight, pipeGap));
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!gameOver) {
            // Draw background
            g.setColor(Color.cyan);
            g.fillRect(0, 0, getWidth(), getHeight());

            // Draw bird image
            g.drawImage(birdImage, 100, birdY, 50, 50, null);

            // Draw pipes
            g.setColor(Color.green);
            for (Pipe pipe : pipes) {
                g.fillRect(pipe.x, 0, pipeWidth, pipe.height); // top pipe
                g.fillRect(pipe.x, pipe.height + pipe.gap, pipeWidth,
                        getHeight() - pipe.height - pipe.gap); // bottom pipe
            }

            // Display the score
            g.setColor(Color.black);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Score: " + score, 20, 20);
        } else {
            // Display game over message
            g.setColor(Color.red);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("Game Over", 300, 300);

            // Display final score
            g.setFont(new Font("Arial", Font.PLAIN, 30));
            g.drawString("Score: " + score, 350, 350);

            // Add a restart button
            g.setColor(Color.blue);
            g.fillRect(350, 400, 100, 50);
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Restart", 365, 430);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameStarted && !gameOver) {
            // Bird physics
            birdVelocity += gravity;
            birdY += birdVelocity;

            // Move the pipes
            for (Pipe pipe : pipes) {
                pipe.x -= 5;

                // Reset pipe if it goes off screen
                if (pipe.x + pipeWidth < 0) {
                    pipe.x = getWidth();
                    pipe.height = 100 + rand.nextInt(300); // new random height
                    pipe.gap = 160 + rand.nextInt(100);    // new random gap ≥ 160
                    score++;
                }

                // Check collision
                if (birdY < 0 || birdY + 50 > getHeight() ||
                        (100 + 50 > pipe.x && 100 < pipe.x + pipeWidth &&
                                (birdY < pipe.height || birdY + 50 > pipe.height + pipe.gap))) {
                    gameOver = true;
                }
            }

            repaint();
        } else if (restartClicked) {
            restartClicked = false;
            repaint();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (!gameStarted) {
                gameStarted = true;
            }
            if (!gameOver) {
                birdVelocity = -15; // Jump by reducing the Y velocity
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Flappy Bird Game");
        BirdGame game = new BirdGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    // Pipe class for multiple pipes
    class Pipe {
        int x, height, gap;

        Pipe(int x, int height, int gap) {
            this.x = x;
            this.height = height;
            this.gap = gap;
        }
    }
}
