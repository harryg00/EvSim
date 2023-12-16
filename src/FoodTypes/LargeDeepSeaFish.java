package FoodTypes;
// Refer to FoodTypes.java for comments (C3.2.1 documentation)
public class LargeDeepSeaFish extends FoodTypes {
    public LargeDeepSeaFish(){
        parentName = "Fish";
        foodName = "Large Deep Sea Fish";
        foodAvailability = 0.05;
        foodFactor = 0.8;
        terrainName = "Deep Water";
        landBasedFood = false;
        waterBasedFood = true;
    }
}
