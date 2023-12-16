package Terrains;
// Refer to TerrainAttributes.java for comments (C3.1.1 documentation)
public class Forest extends TerrainAttributes {
    public Forest() {
        terrainId = "Forest";
        visibility = 0.55;
        foods.add("Tree Spawn");
        foods.add("Berries");
        foods.add("Fungi");
        temperature = random.nextInt(20, 25);
    }
}
