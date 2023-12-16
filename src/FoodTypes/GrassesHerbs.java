package FoodTypes;
// Refer to FoodTypes.java for comments (C3.2.1 documentation)
public class GrassesHerbs extends FoodTypes {
    public GrassesHerbs(){
        parentName = "Plant Spawn";
        terrainName = "Mountain";
        foodName = "Grasses and Herbs";
        foodAvailability = 0.4;
        foodFactor = 0.25;
        landBasedFood = true;
        waterBasedFood = false;
    }
}
