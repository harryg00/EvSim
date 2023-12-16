package FoodTypes;
// Refer to FoodTypes.java for comments (C3.2.1 documentation)
public class LeavesPlants extends FoodTypes {
    public LeavesPlants(){
        parentName = "Plant Parts";
        foodName = "Leaves / Large Plants";
        foodAvailability = 0.25;
        foodFactor = 0.4;
        terrainName = "Grass";
        landBasedFood = true;
        waterBasedFood = false;
    }
}
