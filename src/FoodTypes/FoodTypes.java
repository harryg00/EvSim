package FoodTypes;
// The parent FoodTypes class, which has 20 inheritors (all other food types)
public class FoodTypes {
    public String foodName; // The name of the food
    public double foodAvailability; // The availability
    public double foodFactor; // The factor
    public boolean waterBasedFood = false; // Whether it is land or food based
    public boolean landBasedFood = false;
    public String parentName; // The name of the parent food class (see 3.3.2 documentation)
    public String terrainName; // The name of the terrain where this food is found
}
