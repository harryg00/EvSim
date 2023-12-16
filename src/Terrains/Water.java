package Terrains;
// Refer to TerrainAttributes.java for comments (C3.1.1 documentation)
public class Water extends TerrainAttributes {
    public Water(){
        terrainId = "Water";
        visibility = 0.6;
        foods.add("Aquatic Plants");
        foods.add("Algae");
        foods.add("Insects and Insect Larvae");
        temperature = random.nextInt(6, 13);
    }
}
