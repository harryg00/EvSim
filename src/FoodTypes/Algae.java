package FoodTypes;
// Refer to FoodTypes.java for comments (C3.2.1 documentation)
public class Algae extends FoodTypes {
    public Algae(){
        parentName = "Algae and Aquatic Plants";
        foodName = "Algae";
        foodAvailability = 0.4;
        foodFactor = 0.25;
        terrainName = "Water";
        landBasedFood = false;
        waterBasedFood = true;
    }
}
