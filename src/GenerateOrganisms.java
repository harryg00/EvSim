import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Random;
import com.fasterxml.jackson.databind.DeserializationFeature;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.io.IOException;
import java.io.FileWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
public class GenerateOrganisms extends JPanel {
    public static int organismCount = 50; // Default is 50, can be changed
    private final Random random = new Random(); // For random attributes
    private int x; // The current coordinates
    private int y;
    public static int organismRadius = 1; // Default is 1, can be changed
    public static boolean showDeadOrganisms = false; // Whether to paint dead organisms or not
    @SuppressWarnings("unchecked")
    public GenerateOrganisms(int width, int height) {
        width = width - 300; // Counter the side menu and place them away from the edges
        height = height - 35;
        String filename = "organisms.json"; // The file for the program to write to
        JSONArray jsonArray = new JSONArray(); // Creates a new JSON Array (which will contain objects/organisms)

        int tempSex = 0; // The default value for the organisms sex

        for (int organismCounter = 0; organismCounter < organismCount; organismCounter++) {
            JSONObject jsonObject = new JSONObject(); // Creates a new JSON object for each organism
            newOrganismLocation(width, height); // generates new x and y coordinates

            // Adds a key/value pair to the json object initialised above
            jsonObject.put("organismId", organismCounter);
            jsonObject.put("xCoordinate", x);
            jsonObject.put("yCoordinate", y);
            jsonObject.put("currentTerrain", GenerateMap.getTerrain(x, y));

            JSONObject statsObject = getStatsObject();

            jsonObject.put("stats", statsObject);
            if(organismCount == 2) {
                jsonObject.put("sex", tempSex); // Ensures that if there are 2 organisms, one is male and one is female
                tempSex = 1;
            } else jsonObject.put("sex", random.nextInt(0, 2)); // Random between male and female

            jsonObject.put("fatherID", -1);
            jsonObject.put("motherID", -1);
            jsonObject.put("predator", false);

            jsonArray.add(jsonObject); // Adding the individual objects to the array
        }
        // Write the JSON array to the file
        try (FileWriter fileWriter = new FileWriter(filename)) {
            fileWriter.write(jsonArray.toJSONString());
            fileWriter.flush();
            //System.out.println("JSON array has been written to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @SuppressWarnings("unchecked")
    private JSONObject getStatsObject() {
        JSONObject statsObject = new JSONObject(); // The stats object is initialised
        statsObject.put("currentAwareness", 1.0); // Theses are all the stats to be stored as well as their default values
        statsObject.put("organismsEncountered", 0.0); // Refer to documentation for explanation of these values and their purposes
        statsObject.put("awarenessHistory", new JSONArray());
        statsObject.put("age", 0);
        statsObject.put("preferredFood", null);
        statsObject.put("preferredFoodString", null);
        statsObject.put("eatenFood", null);
        statsObject.put("recentFood", new JSONArray());
        statsObject.put("foodFactor", 0.0);
        statsObject.put("sizeFactor", 0.0);
        statsObject.put("size", 0.5);
        statsObject.put("speed", 5.0);
        statsObject.put("startSpeed", 5.0);
        statsObject.put("currentTemp", 0);
        statsObject.put("preferredTemp", 0);
        statsObject.put("recentTemp", new JSONArray());
        statsObject.put("tempFactor", 1);
        statsObject.put("preferredTerrain", "Default");
        statsObject.put("secondPreferredTerrain", "Default");
        statsObject.put("thirdPreferredTerrain", "Default");
        statsObject.put("health", 100.0);
        statsObject.put("dead", false);
        statsObject.put("ageLastBred", 15);
        statsObject.put("generation", 1);
        statsObject.put("organismsMet", 0);
        statsObject.put("totalOrganismsMet", 0);
        statsObject.put("tsb", null);
        statsObject.put("lastAttack", 10);
        return statsObject; // Returns the stats as a JSONArray
    }

    // Generates a random new organism location in a circle at the centre of the map
    private void newOrganismLocation(int Width, int Height) {
        int epicenterX = Width / 2; // Calculate where the centre of the epicentre should be (middle of the map)
        int epicenterY = Height / 2;

        // Set the epicenter radius to be at most one-fourth of the map width or preferably 5 times the number of organisms
        int epicenterRadius = Math.min(Width / 4, organismCount * 5);

        // Generate random coordinates for each organism
        for (int i = 0; i < organismCount; i++) {
            // Generate a random angle in radians
            double angle = 2 * Math.PI * random.nextDouble();

            // Generate a random distance within the epicenter radius
            double distance = epicenterRadius * Math.sqrt(random.nextDouble());

            // Calculate the x and y coordinates for the organism based on the polar coordinates
            // Uses trigonometry to convert polar coordinates (angle and distance ^^) to cartesian coordinates (x and y - what this program uses)
            int organismX = (int) (epicenterX + distance * Math.cos(angle));
            int organismY = (int) (epicenterY + distance * Math.sin(angle));

            // Ensure the organism is within the map boundaries
            x = Math.max(0, Math.min(Width - 1, organismX));
            y = Math.max(0, Math.min(Height - 1, organismY));
        }
    }

    // Draws the organisms onto the map each year (is called by GameWindow)
    public static void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g; // Creates a graphics 2d component
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true); // To avoid any null errors
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        File file = new File("organisms.json"); // The file to read
        if (file.length() > 0) { // If the file is not complete / empty then don't paint anything (as an error will be returned)
            for (Organism organism : GameWindow.organisms) { // For each organism (value) in Organisms (array)
                // The radius to set the organisms on screen
                organismRadius = Math.min(20, Math.max(5, (int) (organism.getStats().getSize() * 10))); // Max radius of 20 and minimum of 5

                if (organism.getStats().isDead()) { // If the organism is dead
                    if (showDeadOrganisms) { // and if user wants to show dead organisms then paint them
                        g.setColor(Color.BLACK);
                        g.fillOval(organism.getxCoordinate(), organism.getyCoordinate(), organismRadius, organismRadius); // Paint a red oval at position x, y, dimension 5, 5
                    }
                } else {
                    // Set color based on generation
                    int generation = organism.getStats().getGeneration();
                    if (generation < 10) g.setColor(Color.YELLOW); // Color for generation 0-10
                    else if (generation < 30) g.setColor(Color.ORANGE); // Color for generation 10-30
                    else g.setColor(Color.RED); // Color for generation 30+

                    // If organism is asexual, paint as pink
                    if (organism.getSex() == 2) g.setColor(Color.PINK);

                    g.fillOval(organism.getxCoordinate(), organism.getyCoordinate(), organismRadius, organismRadius); // Paint a red oval at position x, y, dimension 5, 5

                    g2d.setColor(Color.BLACK); // Default outline colour

                    // If the organism is a predator, outline in red
                    if (organism.isPredator()) g2d.setColor(Color.RED);

                    // If the organism is the selected one outline in cyan
                    if(organism.getOrganismId() == GameWindow.organismSelected) g2d.setColor(Color.CYAN);

                    // Draw the organism itself at the coordinates with the set radius
                    g2d.drawOval(organism.getxCoordinate(), organism.getyCoordinate(), organismRadius, organismRadius);
                }
            }
        } else GameWindow.timeFactor += 500; // Allows the program to catch up with itself if the process is expensive (file has not saved in time)
    }
}