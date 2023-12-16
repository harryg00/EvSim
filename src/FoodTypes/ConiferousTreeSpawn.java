package FoodTypes;
// Refer to FoodTypes.java for comments (C3.2.1 documentation)
public class ConiferousTreeSpawn extends FoodTypes {
    public ConiferousTreeSpawn(){
        parentName = "Shrubs and Trees";
        foodName = "Coniferous Tree Spawn";
        foodAvailability = 0.3;
        foodFactor =  0.4;
        terrainName = "Mountain";
        landBasedFood = true;
        waterBasedFood = false;
    }
}
