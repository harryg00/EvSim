import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class testMap extends JPanel {
    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;
    public static final int CELL_SIZE = 1;
    public static final int NUM_ROWS = HEIGHT / CELL_SIZE;
    public static final int NUM_COLS = WIDTH / CELL_SIZE;

    public static double[][] map;
    private Random random = new Random();

    public testMap() {
        // Initialize the map
        map = new double[NUM_ROWS][NUM_COLS];

        // Generate the world map
        generateWorldMap();
    }
    public void generateWorldMap(){
        double scale = 2000.0; // Adjust this value to change the terrain appearance
        perlinNoise noiseGenerator = new perlinNoise();
        for (int x = 0; x < NUM_ROWS; x++) {
            for (int y = 0; y < NUM_COLS; y++) {
                // Normalize coordinates and generate noise
                double nx = (double) x / NUM_COLS;
                double ny = (double) y / NUM_ROWS;
                double terrainValue = noiseGenerator.noise(nx * scale, ny * scale);
                double normalisedValue = (terrainValue + 1.0) / 2.0;
                map[x][y] = normalisedValue;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Define colors for terrain types
        Color[] terrainColors = {Color.GREEN, Color.BLUE, Color.GRAY, Color.YELLOW, Color.WHITE, new Color(13, 100, 10) /* dark green */, new Color(6, 11, 112) /* DARK BLUE */ };

        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COLS; col++) {
                double terrainType = map[row][col];
                Color terrainColor;
                if(terrainType < 0.3){
                    terrainColor = terrainColors[6];
                } else if(terrainType < 0.5){
                    terrainColor = terrainColors[1];
                } else if (terrainType < 0.55) {
                    terrainColor = terrainColors[3];
                } else if (terrainType < 0.6) {
                    terrainColor = terrainColors[0];
                } else if (terrainType < 0.65) {
                    terrainColor = terrainColors[5];
                } else if (terrainType < 0.7) {
                    terrainColor = terrainColors[0];
                } else if (terrainType < 0.8) {
                    terrainColor = terrainColors[2];
                } else terrainColor = terrainColors[4];

                g.setColor(terrainColor);
                g.fillRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("EvSim Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            testMap mapPanel = new testMap();
            frame.setSize(NUM_ROWS, NUM_COLS);
            frame.add(mapPanel);

            frame.setSize(WIDTH, HEIGHT);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
