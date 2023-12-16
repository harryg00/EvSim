import com.fasterxml.jackson.databind.ObjectMapper;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.util.List;

// This is how the map is generated
// Uses the PerlinNoise class to generate a random terrain
public class GenerateMap extends JPanel {
    public double scale = 100; // This can be changed / adjusted to change the size of the map that is generated
    public static double[][] map; // The map array
    // The default colours to show
    public static Color[] terrainColors = {Color.GREEN, Color.BLUE, Color.GRAY, Color.YELLOW, Color.WHITE, new Color(13, 100, 10) /* DARK GREEN */, new Color(6, 11, 112) /* DARK BLUE */};

    // The constructor for GenerateMap which creates the map itself
    public GenerateMap(int Width, int Height, boolean LoadedSave, Settings newLoad) {
        setLayout(null); // So there is no layout which may cause an issue painting
        Width = Width - 250; // Offset the width (to count for the side menu
        readJSONColours(); // Updates the terrainColours with what is stored in JSON
        map = new double[Width][Height]; // Sets the size to be equal to the size of the map
        PerlinNoise noiseGenerator;
        if (!LoadedSave) noiseGenerator = new PerlinNoise(); // Initialises the perlin noise generator if it is a new simulation
        else noiseGenerator = new PerlinNoise(newLoad.getSeed()); // Else initialised with the set seed
        for (int y = 0; y < Height; y++) { // Paint along the x and y axis of the map
            for (int x = 0; x < Width; x++) {
                // Casts coordinates to generate noise
                double nx = (double) x / Width;
                double ny = (double) y / Height;
                double terrainValue = noiseGenerator.noise(nx * scale, ny * scale);
                double normalisedValue = (terrainValue + 1.0) / 2.0; // Makes the generated value between 0 and 1
                map[x][y] = normalisedValue; // Adding it to the map array
            }
        }
    }

    // Reads and updates the colour array with what is stored in JSON
    private void readJSONColours() {
        ObjectMapper objectMapper = new ObjectMapper(); // Initializing the Jackson JSON Object Mapper
        Options options;
        // Try to read from the source folder
        try {
            File sourceFile = new File("options.json");
            if (sourceFile.exists()) { // If the file is within the source folder then read from there
                options = objectMapper.readValue(sourceFile, Options.class);
            } else {
                // If the file doesn't exist in the source folder, read it from the JAR resources
                InputStream inputStream = getClass().getClassLoader().getResourceAsStream("options.json");
                if (inputStream != null) { // If the file has been read successfully
                    options = objectMapper.readValue(inputStream, Options.class); // Pass the JSON
                    inputStream.close();
                } else {
                    throw new RuntimeException(); // Otherwise print an error
                }
            }

            List<Colours> coloursList = options.getColours(); // The list of colours

            int counter = 0;
            for (Colours colours : coloursList) { // Updating each colour to the terrainColours array
                terrainColors[counter] = new Color(colours.getRed(), colours.getGreen(), colours.getBlue());
                counter++;
            }
        } catch (Exception e) {
            throw new RuntimeException(e); // Any issues output the error
        }
    }

    // This returns the terrain type as a string (to be set as the current terrain of the organism for example
    public static String getTerrain(int x, int y) {
        double terrainType = map[x][y]; // The terrain type at the current x and y coordinates
        Color currentTerrain = getColor(terrainType); // Returns the colour at that position
        if (currentTerrain == terrainColors[0]) return "Grass"; // Return the terrain name
        else if (currentTerrain == terrainColors[1]) return "Water";
        else if (currentTerrain == terrainColors[2]) return "Mountain";
        else if (currentTerrain == terrainColors[3]) return "Beach";
        else if (currentTerrain == terrainColors[4]) return "Snow";
        else if (currentTerrain == terrainColors[5]) return "Forest";
        else return currentTerrain == terrainColors[6] ? "Deep Water" : "Default";
    }

    // Two draw methods as the way that the preview map and the simulation work together is different.
    public void draw(Graphics g, int Width, int Height) { // Used during the simulation as there are multiple components
        Width = Width - 250; // Offset the width to count for the side menu
        for (int row = 0; row < Width; row++) {
            for (int col = 0; col < Height; col++) {
                double terrainType = map[row][col];
                Color terrainColor = getColor(terrainType); // Sets the correct colour based on the value generated from noise at coordinates row, col
                g.setColor(terrainColor);
                g.fillRect(row, col, 1, 1); // Paints the map to the screen
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) { // Used during the map preview as only the map needs to be shown
        int Width = getWidth(); // Counter the space usually taken by the side menu
        int Height = getHeight();
        super.paintComponent(g);

        for (int row = 0; row < Width; row++) { // Painting the map
            for (int col = 0; col < Height; col++) {
                double terrainType = map[row][col];
                Color terrainColor = getColor(terrainType); // Sets the correct colour based on the value generated from noise at coordinates row, col
                g.setColor(terrainColor);
                g.fillRect(row, col, 1, 1); // Paints them to the screen
            }
        }
    }

    // The terrain type (colour) that is to be painted depending on the double value generated by Perlin Noise
    private static Color getColor(double terrainType) { // Defines how the biomes are generated.
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
        return terrainColor; // Returns the colour
    }
}
