import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameWindow extends JPanel implements Runnable {
    JFrame frame;
    Thread thread;
    int FPS = 60;
    long currentFPS;
    public GameWindow(int Width, int Height) {

        this.setSize(Width, Height);
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        frame = new JFrame("EvSim");
        frame.setSize(Width, Height);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);

        frame.setVisible(true);
    }
    public void startGameThread(){
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        long drawCount = 0;


        while (thread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;
            if(delta >= 1){
                update();
                repaint();
                delta--;
                drawCount++;
            }
            if(timer >= 1000000000) {
                currentFPS = drawCount;
                drawCount = 0;
                timer = 0;
            }
        }
    }
    public void update() {

    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);

        Color[] terrainColors = {Color.GREEN, Color.BLUE, Color.GRAY, Color.YELLOW, Color.WHITE, new Color(13, 100, 10) /* dark green */ };

        for (int row = 0; row < testMap.NUM_ROWS; row++) {
            for (int col = 0; col < testMap.NUM_COLS; col++) {
                double terrainType = testMap.map[row][col];
                //Color terrainColor = terrainColors[terrainType];

                //g.setColor(terrainColor);
                //g.fillRect(col * testMap.CELL_SIZE, row * testMap.CELL_SIZE, testMap.CELL_SIZE, testMap.CELL_SIZE);
            }
        }


        g.setFont(new Font("Agency FB", Font.BOLD, 20));
        g.setColor(Color.black);

        FontMetrics fontMetrics = g.getFontMetrics();
        int textWidth = fontMetrics.stringWidth("FPS: " + currentFPS);

        int fpsTextX = getWidth() - textWidth - 10;
        int fpsTextY = 30;

        g.drawString("FPS: " + currentFPS, fpsTextX, fpsTextY);
    }
}
