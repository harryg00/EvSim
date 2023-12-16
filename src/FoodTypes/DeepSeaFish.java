package FoodTypes;
// Refer to FoodTypes.java for comments (C3.2.1 documentation)
public class DeepSeaFish extends FoodTypes {
    public DeepSeaFish(){
        parentName = "Fish";
        foodName = "Deep Sea Fish";
        foodAvailability = 0.1;
        foodFactor = 0.5;
        terrainName = "Deep Water";
        landBasedFood = false;
        waterBasedFood = true;
    }
}
