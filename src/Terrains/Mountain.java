package Terrains;
// Refer to TerrainAttributes.java for comments (C3.1.1 documentation)
public class Mountain extends TerrainAttributes {
    public Mountain() {
        terrainId = "Mountain";
        visibility = 1.2;
        foods.add("Grasses and Herbs");
        foods.add("Shrubs");
        foods.add("Coniferous Tree Spawn");
        temperature = random.nextInt(4, 10);
    }
}
