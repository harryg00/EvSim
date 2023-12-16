package FoodTypes;
// Refer to FoodTypes.java for comments (C3.2.1 documentation)
public class CoastalFruitPlants extends FoodTypes {
    public CoastalFruitPlants(){
        parentName = "Fruits and Berries";
        foodName = "Coastal Fruit / Plants";
        foodAvailability = 0.3;
        foodFactor = 0.4;
        terrainName = "Beach";
        landBasedFood = true;
        waterBasedFood = false;
    }
}
