package FoodTypes;
// Refer to FoodTypes.java for comments (C3.2.1 documentation)
public class AquaticPlants extends FoodTypes {
    public AquaticPlants(){
        parentName = "Algae and Aquatic Plants";
        foodName = "Aquatic Plants";
        foodAvailability = 0.5;
        foodFactor = 0.2;
        terrainName = "Water";
        landBasedFood = false;
        waterBasedFood = true;
    }
}
