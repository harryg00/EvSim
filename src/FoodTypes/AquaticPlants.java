package FoodTypes;

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
