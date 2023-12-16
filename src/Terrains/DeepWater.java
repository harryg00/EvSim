package Terrains;
// Refer to TerrainAttributes.java for comments (C3.1.1 documentation)
public class DeepWater extends TerrainAttributes {
    public DeepWater(){
        terrainId = "Deep Water";
        visibility = 0.4;
        foods.add("Plankton");
        foods.add("Deep Sea Fish");
        foods.add("Large Deep Sea Fish");
        temperature = random.nextInt(0, 4);
    }
}
