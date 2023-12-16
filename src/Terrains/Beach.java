package Terrains;
// Refer to TerrainAttributes.java for comments (C3.1.1 documentation)
public class Beach extends TerrainAttributes {
    public Beach(){
        terrainId = "Beach";
        visibility = 1.1;
        foods.add("Beach Grass Seeds");
        foods.add("Shellfish and Crustaceans");
        foods.add("Coastal Fruit / Plants");
        temperature = random.nextInt(13, 23);
    }
}
