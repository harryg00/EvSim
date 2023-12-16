package FoodTypes;
// Refer to FoodTypes.java for comments (C3.2.1 documentation)
public class Berries extends FoodTypes {
    public Berries(){
        parentName = "Fruits and Berries";
        foodName = "Berries";
        foodAvailability = 0.5;
        foodFactor = 0.15;
        terrainName = "Forest";
        landBasedFood = true;
        waterBasedFood = false;
    }
}
