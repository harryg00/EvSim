package FoodTypes;
// Refer to FoodTypes.java for comments (C3.2.1 documentation)
public class BeachGrassSeeds extends FoodTypes {
    public BeachGrassSeeds(){
        parentName = "Plant Spawn";
        foodName = "Beach Grass Seeds";
        foodAvailability = 0.4;
        foodFactor = 0.25;
        terrainName = "Beach";
        landBasedFood = true;
        waterBasedFood = false;
    }
}
