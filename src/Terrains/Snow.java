package Terrains;
// Refer to TerrainAttributes.java for comments (C3.1.1 documentation)
public class Snow extends TerrainAttributes {
    public Snow() {
        terrainId = "Snow";
        visibility = 0.7;
        foods.add("Moss / Lichen");
        foods.add("Algae");
        foods.add("Shrub Berries");
        temperature = random.nextInt(-4, 4);
    }
}
