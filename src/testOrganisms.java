import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.util.Random;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class testOrganisms extends JPanel {
    public static final int WIDTH = 1920; // Default is 1920, can be changed
    public static final int HEIGHT = 1080; // Default is 1080, can be changed
    private static final int ORGANISM_COUNT = 50; // Default is 50, can be changed
    private Random random = new Random();
    public int x;
    public int y;
    public int ORGANISM_RADIUS = 3; // Default is 3, can be changed

    private void generateOrganisms(Graphics g) {
        g.setColor(Color.RED);
        String filename = "organisms.json";
        JSONObject jsonObject = new JSONObject();
        for (int organismCounter = 0; organismCounter < ORGANISM_COUNT; organismCounter++) {
            newOrganismLocation();
            // Create a JSON object that represents the data structure you provided
            jsonObject.put("organismId", organismCounter);
            jsonObject.put("x-coordinate", x);
            jsonObject.put("y-coordinate", y);
            jsonObject.put("currentTerrain", "getTerrain(x, y)");

            JSONObject statsObject = new JSONObject();
            statsObject.put("currentForm", 0);
            statsObject.put("retentionRate", 50);
            statsObject.put("age", 0);
            statsObject.put("health", 100);
            statsObject.put("speed", 1);
            statsObject.put("awareness", 0);
            statsObject.put("size", 10);
            statsObject.put("attack", 0);
            statsObject.put("defence", 0);
            statsObject.put("preferredTemperature", null);
            statsObject.put("preferredFood", null);

            jsonObject.put("stats", statsObject);
            jsonObject.put("preferredTerrain", null);
            jsonObject.put("isDead", false);

            // Write the JSON object to a file
            try (FileWriter fileWriter = new FileWriter(filename)) {
                fileWriter.write(jsonObject.toJSONString());
                System.out.println("JSON object has been written to " + filename);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(jsonObject);
    }
    private void newOrganismLocation(){
        x = random.nextInt(0, WIDTH);
        y = random.nextInt(0, HEIGHT);
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        generateOrganisms(g);
        g.fillOval(x, y, 2 * ORGANISM_RADIUS, 2 * ORGANISM_RADIUS);
    }

    public static JSONObject loadJSON(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            reader.close();

            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(jsonString.toString());
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }
}

