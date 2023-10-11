import javax.swing.*;
import java.awt.*;

public class GameWindow extends JPanel implements Runnable {
    JFrame frame;
    Thread thread;
    int FPS = 1;
    long currentFPS;
    generateMap mapPanel;
    testOrganisms organismGeneration;
    public GameWindow(int Width, int Height) {
        frame = new JFrame("EvSim");
        frame.setSize(Width, Height);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mapPanel = new generateMap();
        frame.add(mapPanel);

        organismGeneration = new testOrganisms();
        frame.add(organismGeneration);

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

        mapPanel.paintComponent(g);
        organismGeneration.paintComponent(g);

        g.setFont(new Font("Agency FB", Font.BOLD, 20));
        g.setColor(Color.black);

        FontMetrics fontMetrics = g.getFontMetrics();
        int textWidth = fontMetrics.stringWidth("FPS: " + currentFPS);

        int fpsTextX = getWidth() - textWidth - 10;
        int fpsTextY = 30;

        g.drawString("FPS: " + currentFPS, fpsTextX, fpsTextY);
    }
}
