import javax.swing.*;
import java.awt.*;

public class generateMap extends JPanel {
    public static final int WIDTH = 1920;
    public static final int HEIGHT = 1080;
    public double scale = 100; // This can be changed / adjusted to change the size of the map that is generated
    public static double[][] map;

    public generateMap() {
        map = new double[HEIGHT][WIDTH];
        perlinNoise noiseGenerator = new perlinNoise(); // Initialises the perlin noise generator
        for (int x = 0; x < HEIGHT; x++) {
            for (int y = 0; y < WIDTH; y++) {
                // Casts coordinates to generate noise
                double nx = (double) x / WIDTH;
                double ny = (double) y / HEIGHT;
                double terrainValue = noiseGenerator.noise(nx * scale, ny * scale);
                double normalisedValue = (terrainValue + 1.0) / 2.0; // Makes the generated value between 0 and 1
                map[x][y] = normalisedValue;
            }
        }
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Define colors for terrain types
        Color[] terrainColors = {Color.GREEN, Color.BLUE, Color.GRAY, Color.YELLOW, Color.WHITE, new Color(13, 100, 10) /* DARK GREEN */, new Color(6, 11, 112) /* DARK BLUE */};

        for (int row = 0; row < HEIGHT; row++) {
            for (int col = 0; col < WIDTH; col++) {
                double terrainType = map[row][col];
                Color terrainColor = getColor(terrainType, terrainColors); // Sets the correct colour based on the value generated from noise at coordinates row, col

                g.setColor(terrainColor);
                g.fillRect(col, row, 1, 1); // Paints them to the screen
            }
        }
    }
    private static Color getColor(double terrainType, Color[] terrainColors) { // Defines how the biomes are generated.
        Color terrainColor;
        if (terrainType < 0.3) {
            terrainColor = terrainColors[6]; // Perlin noise can only generate numbers between 0 and 1, anything that is generated below 0.3 will be deep water.
        } else if (terrainType < 0.5) {
            terrainColor = terrainColors[1]; // Anything below 0.5 will be water
        } else if (terrainType < 0.55) {
            terrainColor = terrainColors[3]; // Anything below 0.55 will be desert
        } else if (terrainType < 0.6) {
            terrainColor = terrainColors[0]; // Anything below 0.6 will be grass
        } else if (terrainType < 0.65) {
            terrainColor = terrainColors[5]; // Anything below 0.65 will be forest
        } else if (terrainType < 0.7) {
            terrainColor = terrainColors[0]; // Anything below 0.7 will be grass
        } else if (terrainType < 0.8) {
            terrainColor = terrainColors[2]; // Anything below 0.8 will be mountains
        } else terrainColor = terrainColors[4]; // Anything above that will be snow
        return terrainColor;
    }
}
/*    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("EvSim Test");
            JButton newMap = new JButton("New Map");
            newMap.setPreferredSize(new Dimension(100, 50));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            testMap mapPanel = new testMap();
            frame.setSize(WIDTH, HEIGHT);

            frame.add(newMap, BorderLayout.WEST);
            frame.add(mapPanel);

            newMap.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    mapPanel.generateWorldMap();
                    //frame.add(mapPanel);
                    frame.repaint();
                }
            });
            frame.setSize(WIDTH, HEIGHT);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }*/
//}
