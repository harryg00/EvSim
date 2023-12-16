package Terrains;
// Refer to TerrainAttributes.java for comments (C3.1.1 documentation)
public class Grass extends TerrainAttributes {
    public Grass(){
        terrainId = "Grass";
        visibility = 1.0;
        foods.add("Grass Seeds");
        foods.add("Leaves / Large Plants");
        foods.add("Tree Roots / Plant Remains");
        temperature = random.nextInt(16, 22);
    }
}