package FoodTypes;
// Refer to FoodTypes.java for comments (C3.2.1 documentation)
public class GrassSeeds extends FoodTypes {
    public GrassSeeds(){
        parentName = "Plant Spawn";
        foodName = "Grass Seeds";
        foodAvailability = 0.6;
        foodFactor = 0.3;
        terrainName = "Grass";
        landBasedFood = true;
        waterBasedFood = false;
    }
}
