package FoodTypes;
// Refer to FoodTypes.java for comments (C3.2.1 documentation)
public class Plankton extends FoodTypes {
    public Plankton(){
        foodName = "Plankton";
        foodAvailability = 0.3;
        foodFactor = 0.1;
        landBasedFood = false;
        waterBasedFood = true;
        parentName = "Aquatic life / Spawn";
        terrainName = "Deep Water";
    }
}
