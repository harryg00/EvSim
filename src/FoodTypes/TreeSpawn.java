package FoodTypes;
// Refer to FoodTypes.java for comments (C3.2.1 documentation)
public class TreeSpawn extends FoodTypes {
    public TreeSpawn(){
        parentName = "Shrubs and Trees";
        foodName = "Tree Spawn";
        foodAvailability = 0.4;
        foodFactor = 0.25;
        terrainName = "Forest";
        landBasedFood = true;
        waterBasedFood = false;
    }
}
