package Terrains;
import java.util.ArrayList;
import java.util.Random;

// The terrains package contains the different terrains that are
// available within EvSim and the stats for that terrain, such as visibility
public class TerrainAttributes {
    public Random random = new Random(); // For random attributes such as temperature
    public String terrainId = "Default"; // The name of the terrain
    public double visibility = 1.0; // The visibility of the terrain
    public ArrayList<String> foods = new ArrayList<>(); // List of foods available within that terrain
    public int temperature; // The temperature of the terrain
}