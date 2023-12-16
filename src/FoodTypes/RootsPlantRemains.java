package FoodTypes;
// Refer to FoodTypes.java for comments (C3.2.1 documentation)
public class RootsPlantRemains extends FoodTypes {
    public RootsPlantRemains(){
        foodName = "Tree Roots / Plant Remains";
        foodAvailability = 0.15;
        foodFactor = 0.5;
        landBasedFood = true;
        waterBasedFood = false;
        parentName = "Plant Parts";
        terrainName = "Grass";
    }
}
