package FoodTypes;
// Refer to FoodTypes.java for comments (C3.2.1 documentation)
public class Fungi extends FoodTypes {
    public Fungi(){
        parentName = "Plant Spawn";
        foodName = "Fungi";
        terrainName = "Forest";
        foodAvailability = 0.3;
        foodFactor = 0.3;
        landBasedFood = true;
        waterBasedFood = false;
    }
}
