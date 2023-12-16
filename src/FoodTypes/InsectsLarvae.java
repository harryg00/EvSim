package FoodTypes;
// Refer to FoodTypes.java for comments (C3.2.1 documentation)
public class InsectsLarvae extends FoodTypes {
    public InsectsLarvae(){
        parentName = "Aquatic life / Spawn";
        foodName = "Insects and Insect Larvae";
        foodAvailability = 0.2;
        foodFactor = 0.5;
        terrainName = "Water";
        landBasedFood = false;
        waterBasedFood = true;
    }
}
