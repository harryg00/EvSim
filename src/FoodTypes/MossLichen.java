package FoodTypes;
// Refer to FoodTypes.java for comments (C3.2.1 documentation)
public class MossLichen extends FoodTypes {
    public MossLichen(){
        parentName = "Shrubs and Trees";
        foodName = "Moss / Lichen";
        foodAvailability = 0.4;
        foodFactor = 0.2;
        terrainName = "Snow";
        landBasedFood = true;
        waterBasedFood = false;
    }
}
