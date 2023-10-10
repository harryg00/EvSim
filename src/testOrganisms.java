import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class testOrganisms extends JPanel {
    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;
    private static final int ORGANISM_COUNT = 100;
    private static int ORGANISM_RADIUS = 2;
    private Random random = new Random();

    private void generateOrganisms(Graphics g) {
        g.setColor(Color.RED);
        for (int organismCounter = 0; organismCounter < ORGANISM_COUNT; organismCounter++){
            int x = random.nextInt(WIDTH - 2 * ORGANISM_RADIUS) + ORGANISM_RADIUS;
            int y = random.nextInt(HEIGHT - 2 * ORGANISM_RADIUS) + ORGANISM_RADIUS;
            g.fillOval(x, y, 2 * ORGANISM_RADIUS, 2 * ORGANISM_RADIUS);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        generateOrganisms(g);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("EvSim Organism Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(WIDTH, HEIGHT);
            frame.add(new testOrganisms());
            frame.setVisible(true);
        });
    }
}