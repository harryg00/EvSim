import FoodTypes.*;
import Terrains.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

// This class updates all organisms statistics
// Contains many methods and functions, all performing different functions to update the organisms statistics
// where applicable
public class Organisms {
    private String currentTerrain; // Global variables which have to be used commonly throughout the class
    private int organismID; // Such as the current organism's id
    private int currentX; // Or coordinates
    private int currentY;
    private final Random random = new Random();
    private Stats stats = new Stats(); // The organisms stats
    private final Organism[] organisms = GameWindow.organisms; // Takes the organisms stored in game window to be used here
    private final DecimalFormat decimalFormat = new DecimalFormat("#.##"); // Decimal formatter used to smooth new values
    private double organismSpeed;
    private final List<JSONObject> organismsToAdd = new ArrayList<>();
    private int organismCount = organisms.length;
    public static boolean predatorPreyEnabled = true; // Boolean to define what to update as per user requests
    public static boolean seasonsEnabled;
    public static String season;

    // The constructor for Organisms, which calls and links together all methods and functions
    // The organisms array is looped through and each organism is updated if it is not dead
    public Organisms() {
        if (seasonsEnabled) updateSeason(); // Updates the season at the start of the year / simulation if enabled

        ObjectMapper objectMapper = new ObjectMapper();
        try { // To catch errors
            for (Organism organism : organisms) { // Loops through all organisms
                if (!organism.getStats().isDead()) { // If the organism is dead, no need to update
                    // S the current attributes for the organism
                    organismID = organism.getOrganismId();
                    currentTerrain = organism.getCurrentTerrain();
                    currentX = organism.getxCoordinate();
                    currentY = organism.getyCoordinate();
                    stats = organism.getStats();

                    // Calling the methods which update the organisms stats and position (makeDecision)
                    updateStats();
                    organismSpeed = stats.getSpeed(); // Speed updated here as it should use the new stat
                    makeDecision();

                    organism.setCurrentTerrain(GenerateMap.getTerrain(currentX, currentY)); // Saves the new information from makeDecision()
                    organism.setxCoordinate(currentX);
                    organism.setyCoordinate(currentY);
                    organism.setStats(stats); // Saves the new stats from updateStats()
                }
            }
            objectMapper.writeValue(new File("organisms.json"), organisms); // Syncs the JSON with what is stored in organisms
            if (!organismsToAdd.isEmpty())
                addNewOrganisms(organismsToAdd); // If there are new organisms to add, the JSON is updated with the new organisms through addNewOrganisms()
        } catch (Exception e) {
            throw new RuntimeException(e); // Catch and print any errors
        }
    }

    // This method checks the current season and increments if due to.
    // If the user has chosen to turn off seasons then this method is not called.
    private void updateSeason() {
        if (GameWindow.years == 0 || season == null) { // If it is the beginning, select a random season
            int randomSeason = random.nextInt(0, 4);
            season = switch (randomSeason) { // Random season
                case 0 -> "Winter";
                case 1 -> "Spring";
                case 2 -> "Summer";
                default -> "Autumn";
            };
        }
        if (GameWindow.years % 4 == 0 && GameWindow.years != 0) { // Update the season every 4 years
            season = switch (season) { // Move onto the next season
                case "Winter" -> "Spring";
                case "Spring" -> "Summer";
                case "Summer" -> "Autumn";
                default -> "Winter";
            };
        }
    }

    // The calculated decision made by each organism to move to the best position based on survivability
    //  is affected by organism proximity and terrain type
    private void makeDecision() {
        // Lists which will contain the x and y coordinates which can be moved to
        List<Point> positions = new ArrayList<>();

        // Populates the list created above with the appropriate coordinates
        for (int xCounter = (int) (0 - organismSpeed); xCounter < organismSpeed; xCounter++) {
            int newX = Math.abs(currentX + xCounter);
            for (int yCounter = (int) (0 - organismSpeed); yCounter < organismSpeed; yCounter++) { // Check each y and x coordinate around
                int newY = Math.abs(currentY + yCounter);
                positions.add(new Point(newX, newY)); // Add it to the possible positions
            }
        }

        // This ensures that the new X and Y coordinates are not outside the bounds of the window
        for (int index = positions.size() - 1; index >= 0; index--) {
            Point position = positions.get(index);
            if (position.x >= GameWindow.WIDTH - 300 || position.y >= GameWindow.HEIGHT - 45) {
                positions.remove(index); // Removes it if so
            }
        }

        Organism organismToSeek = organismToSeek(); // Find the organism to seek

        // List of survivability levels for each x and y position
        List<Double> listOfSurvivability = new ArrayList<>();
        for (Point position : positions) listOfSurvivability.add(getSurvivability(position.x, position.y, organismToSeek));

        // Find position with one of the highest survivability
        double maxSurvivability = listOfSurvivability.stream().mapToDouble(Double::doubleValue).max().orElse(0); // Find the highest survivability value in the list using stream

        // List to track all the highest ones
        List<Integer> highestSurvivabilityPositions = new ArrayList<>();

        // If the survivability is equal to the highest survivability, add the index to the list of ideal positions
        for (int counter3 = 0; counter3 < listOfSurvivability.size(); counter3++)
            if (listOfSurvivability.get(counter3) == maxSurvivability) highestSurvivabilityPositions.add(counter3);

        // Choose a random position of the list, this translates to the position of the x and y coordinates
        int randomValue = highestSurvivabilityPositions.get(random.nextInt(highestSurvivabilityPositions.size()));
        currentX = positions.get(randomValue).x; // The x and y coordinates of the chosen position are now set and updated
        currentY = positions.get(randomValue).y;

        // This ensures that the new X and Y coordinates are not outside the bounds of the window
        // The x and y coordinate are compared against the width and height of the gameWindow, if higher or lower,
        // the coordinates are adjusted in the appropriate direction
        // This was checked earlier, this is in position as a final check - ensures no errors
        if (currentX >= GameWindow.WIDTH - 260) {
            currentX = GameWindow.WIDTH - 300;
        } else if (currentX < 0) currentX = 10;
        if (currentY >= GameWindow.HEIGHT - 35) {
            currentY = GameWindow.HEIGHT - 75;
        } else if (currentY < 0) currentY = 10;
    }

    // Gets the survivability of an organism at the coordinate position newX, newY
    // Survivability depends on organism proximity and terrain type
    public double getSurvivability(int newX, int newY, Organism organismToSeek) {
        double survivability = 0.1; // Default value
        double chanceToMisjudge = random.nextDouble(0, 1); // Small chance to make a mistake
        String newTerrain = GenerateMap.getTerrain(newX, newY); // Gets the terrain at position newX, newY

        if (chanceToMisjudge <= 0.02) { // Small chance to misjudge (move to a position it doesn't particularly benefit from)
            if (organismToSeek != null) { // If it is not null (no organisms left to seek)
                // Calculate the Euclidean distance between current organism and target organism
                double currentDistance = Math.sqrt(Math.pow(organismToSeek.getxCoordinate() - newX, 2) + Math.pow(organismToSeek.getyCoordinate() - newY, 2));

                // Calculates a value between -2 and 2 to distance how close the organism is to the target organism.
                // The closer the value is, the higher the survivability will be set
                survivability = (1.0 / (1.0 + currentDistance)) * 10;
                survivability = Double.parseDouble(decimalFormat.format(Math.max(Math.min(survivability - 1, 1.5), 0))); // Limit it to 0 and 1.5
            }

            // Terrain survivability calculations
            if (newTerrain.equals(organisms[this.organismID].getStats().getPreferredTerrain()))
                survivability = survivability + 0.15; // If it is the preferred terrain receive a boost
            else if (newTerrain.equals(organisms[this.organismID].getStats().getSecondPreferredTerrain()))
                survivability = survivability + 0.10; // If it is the second most preferred terrain receive a boost
            else if (newTerrain.equals(organisms[this.organismID].getStats().getThirdPreferredTerrain()))
                survivability = survivability + 0.05; // If it is the third most preferred terrain receive a boost
        } else survivability = 1.5; // Default to 1.5 as a misjudgement

        return survivability; // Returns the calculated survivability
    }

    // This will cover the organisms selection of organism to seek out, whether it be as prey or as breeding
    public Organism organismToSeek() {
        Organism currentOrganism = organisms[this.organismID]; // Localises the current organism
        List<Organism> closestOrganisms = listClosestOrganisms(currentOrganism); // Creates the list of closest organisms
        Organism targetOrganism = new Organism(); // Will be set to the target organism when found

        // If the organism is a predator and cannot breed
        if (currentOrganism.isPredator() && !(stats.getAge() >= 15) && !(stats.getAgeLastBred() < stats.getAge() - 5)) {
            for (Organism organism : closestOrganisms) {
                if (!organism.isPredator()) {
                    targetOrganism = organism; // Target the closest organism as prey
                    break;
                }
            }
        }
        // If the organism is not a predator and cannot breed
        else if (!currentOrganism.isPredator() && !(stats.getAge() >= 15) && !(stats.getAgeLastBred() < stats.getAge() - 5)) {
            targetOrganism = null; // Do not target for another organism
        }
        // If the organism is not a predator but can breed
        else if (!currentOrganism.isPredator() && stats.getAge() >= 15 && stats.getAgeLastBred() < stats.getAge() - 5) {
            for (Organism organism : closestOrganisms) {
                // If the other organism is not a predator, not the same sex, and not asexual
                if (!organism.isPredator() && organism.getStats().getAge() >= 15 &&
                        organism.getStats().getAgeLastBred() < organism.getStats().getAge() - 5 &&
                        organism.getSex() != 2 && organism.getSex() != currentOrganism.getSex()) {

                    targetOrganism = organism; // Sets to the closest organism it can breed with
                    break;
                }
            }
            // Target the closest organism to breed or attack
        } else targetOrganism = closestOrganisms.get(0);

        return targetOrganism; // Return the target organism
    }

    // This returns a list of closest organisms to organism sent in
    // Returns a list of points which contains an x and y coordinate
    private List<Organism> listClosestOrganisms(Organism currentOrganism) {
        List<Organism> closestOrganisms = new ArrayList<>();
        Point currentLocation = new Point(currentOrganism.getxCoordinate(), currentOrganism.getyCoordinate());

        // Filter and sort Organisms to Points which are closest to the current organism
        // Uses .stream to process the array
        // Filters out organisms that have the same ID and are dead
        // Maps each organism to a Point using the organisms x and y coordinates
        // Sorts the points created and converts into a list
        List<Point> organismPoints = Arrays.stream(organisms)
                .filter(organism -> organism.getOrganismId() != currentOrganism.getOrganismId() && !organism.getStats().isDead())
                .map(organism -> new Point(organism.getxCoordinate(), organism.getyCoordinate())).sorted(Comparator.comparingDouble(p -> Math.sqrt(Math.pow(currentLocation.x - p.x, 2) + Math.pow(currentLocation.y - p.y, 2)))).toList();

        // Transfers the list of points into a list of correlating organisms
        for (Point point : organismPoints) {
            for (Organism organism : organisms) {
                if (organism.getxCoordinate() == point.x && organism.getyCoordinate() == point.y && !organism.getStats().isDead()) {
                    closestOrganisms.add(organism);
                    break;
                }
            }
        }

        return closestOrganisms; // Returns the list of closest organisms
    }

    // This method calls together all update methods such as updateSpeed and updateAwareness
    // Only updates if an organism isn't dead (see constructor).
    private void updateStats() {
        // Fetch the current terrain to be used for some methods
        TerrainAttributes terrainAttributes = getTerrainAttributes(currentTerrain);

        updateAwareness(terrainAttributes); // Updating the organism's awareness
        updateFood(terrainAttributes); // Updating the food an organism eats
        stats.setSizeFactor(stats.getSizeFactor() + stats.getEatenFood().foodFactor); // Update the total food factor
        updateSpeed(); // Updating the speed of an organism
        updateTemperature(terrainAttributes); // Updating the current temperature factor
        updateHabitat(terrainAttributes); // Updating the preferred habitat of an organism if applicable
        updateHealth(); // Updating the health of an organism
        if (organisms[this.organismID].getSex() != 2)
            checkAsexual(); // If an organism is not already asexual, check if it can become one
        if (organisms[this.organismID].getStats().getTSB() == null)
            checkTSB(); // Checks if the organism should have TSB, if it doesn't already
        if (!organisms[this.organismID].isPredator() && predatorPreyEnabled)
            checkPredation(); // Checks if the organism should be a predator
        if (organisms[this.organismID].getSex() == 2 && stats.getAgeLastBred() < stats.getAge() - 40 && stats.getAge() >= 15)
            // If the organism is asexual give it opportunity to create a child
            createChildOrganism(organisms[this.organismID], organisms[this.organismID]);

        stats.setAge(stats.getAge() + 1); // adding 1 year to the organisms age
    }

    // This method returns the current terrain as an object
    // So it can be used by certain methods in this class
    private TerrainAttributes getTerrainAttributes(String currentTerrain) {
        TerrainAttributes terrainAttributes; // The object which is returned

        terrainAttributes = switch (currentTerrain) {
            case "Grass" -> new Grass(); // Returns the grass class from Terrains package
            case "Water" -> new Water(); // Returns the water class from Terrains package
            case "Mountain" -> new Mountain();
            case "Beach" -> new Beach();
            case "Snow" -> new Snow();
            case "Forest" -> new Forest();
            case "Deep Water" -> new DeepWater();
            default -> new TerrainAttributes();
        };
        return terrainAttributes; // Returns the terrain as an object
    }

    // This is similar to the getTerrainAttributes
    // Returns the food type as an object
    // Takes in a string (how food is stored) and returns the linking food type
    private FoodTypes getFoodType(String foodName) {
        FoodTypes foodType; // The object which is returned

        foodType = switch (foodName) { // Switch statement to find and set the food type
            case "Grass Seeds" -> new GrassSeeds(); // Returns the grass class from Terrains package
            case "Beach Grass Seeds" -> new BeachGrassSeeds(); // Returns the water class from Terrains package
            case "Grasses and Herbs" -> new GrassesHerbs(); // ""
            case "Fungi" -> new Fungi();
            case "Leaves / Large Plants" -> new LeavesPlants();
            case "Tree Roots / Plant Remains" -> new RootsPlantRemains();
            case "Berries" -> new Berries();
            case "Coastal Fruit / Plants" -> new CoastalFruitPlants();
            case "Shrub Berries" -> new ShrubBerries();
            case "Shrubs" -> new Shrubs();
            case "Coniferous Tree Spawn" -> new ConiferousTreeSpawn();
            case "Tree Spawn" -> new TreeSpawn();
            case "Moss / Lichen" -> new MossLichen();
            case "Aquatic Plants" -> new AquaticPlants();
            case "Algae" -> new Algae();
            case "Insects and Insect Larvae" -> new InsectsLarvae();
            case "Shellfish and Crustaceans" -> new ShellfishCrustaceans();
            case "Plankton" -> new Plankton();
            case "Deep Sea Fish" -> new DeepSeaFish();
            case "Large Deep Sea Fish" -> new LargeDeepSeaFish();
            default -> new FoodTypes();
        };
        return foodType; // Return the food type
    }

    // This method updates an organisms awareness, taking into account the terrain's visibility
    private void updateAwareness(TerrainAttributes terrainAttributes) {
        // Calls the method to check if the organism has encountered another organism
        checkOrganismProximity(stats.getCurrentAwareness());
        // Sets the variables that are read throughout
        double terrainVisibility = terrainAttributes.visibility; // The visibility attribute for each terrain
        double organismsEncountered = stats.getOrganismsEncountered(); // The updated number of organisms encountered

        List<Double> awarenessHistory = stats.getAwarenessHistory(); // The list of previous organism awareness'
        int awarenessHistoryArraySize; // Required as the size of the array is not always >= 4

        // This switch expression sets awarenessHistoryArraySize and totalHistory variables.
        // Total history is a total of previous awareness', it is used to get the average awareness (part of the algorithm)
        // awarenessHistoryArraySize is dependent on how old the organism is, therefore needs to also be set depending on
        // the size of the array stored in stats.
        double totalHistory = switch (awarenessHistory.size()) {
            case 0 -> {
                awarenessHistoryArraySize = 1;
                yield 0.0;
            }
            case 1 -> {
                awarenessHistoryArraySize = 2;
                yield awarenessHistory.get(0);
            }
            case 2 -> {
                awarenessHistoryArraySize = 3;
                yield awarenessHistory.get(0) + awarenessHistory.get(1);
            }
            case 3 -> {
                awarenessHistoryArraySize = 4;
                yield awarenessHistory.get(0) + awarenessHistory.get(1) + awarenessHistory.get(2);
            }
            default -> {
                awarenessHistoryArraySize = 5;
                yield awarenessHistory.get(0) + awarenessHistory.get(1) + awarenessHistory.get(2) + awarenessHistory.get(3);
            }
        };

        // Calculates the awareness, which is set to 2 decimal places (to ensure no long decimals)
        // and used to calculate the new currentAwareness
        double awareness = Double.parseDouble(decimalFormat.format(terrainVisibility + organismsEncountered));
        // Calculates the organisms new currentAwareness. Set to 2 decimal places (to ensure no long decimals)
        double currentAwareness = Math.max(0.4, (awareness + totalHistory) / awarenessHistoryArraySize) + organismsEncountered;
        // Both use same calculations as logic in documentation (see 3.3.1.1)

        if (Objects.equals(stats.getTSB(), organisms[organismID].getCurrentTerrain()))
            currentAwareness = currentAwareness + 0.2; // If it has a TSB, give an awareness boost

        stats.setCurrentAwareness(Double.parseDouble(decimalFormat.format(currentAwareness))); // Updates the local stats variable
        awarenessHistory.add(0, awareness); // Adds the awareness to the locally held
        awarenessHistory = awarenessHistory.subList(0, Math.min(awarenessHistory.size(), 4)); // Restricts the size of awarenessHistory to 4
        stats.setAwarenessHistory(awarenessHistory); // Updates the awarenessHistory which is held in JSON
    }

    // This method updates the number of organism encounters which are stored locally.
    // Takes the double doubleAwareness as an input, which is the area in which an organism can see (awareness)
    private void checkOrganismProximity(double doubleAwareness) {
        // Multiplies by 10 so that the bounds are 10px instead of 1
        int currentAwareness = (int) (doubleAwareness * 10); // Passing the awareness into an integer, so it can be compared to coordinates.
        for (Organism organism : organisms) {
            if (organism.getOrganismId() != this.organismID && !organism.getStats().isDead()) { // So it does not compare to its own (would always be true)
                int checkX = organism.getxCoordinate(), checkY = organism.getyCoordinate(); // The coordinates to check
                int xDifference = Math.abs(this.currentX - checkX); // Returning the difference between mouse and organism coordinates
                int yDifference = Math.abs(this.currentY - checkY); // Returns as an absolute number, so it can be compared to only one bound
                // If the coordinates are within the bound, then increase the number of organisms encountered
                if (xDifference <= currentAwareness && yDifference <= currentAwareness) { // Bound is the organisms awareness
                    stats.setOrganismsEncountered(stats.getOrganismsEncountered() + 0.1); // Increase the number of organisms encountered
                    newOrganismEncounter(this.organismID, organism.getOrganismId());
                    if (organism.getSex() != organisms[this.organismID].getSex() && organism.getStats().getAge() >= 15 && stats.getAge() >= 15 && organism.getStats().getAgeLastBred() < organism.getStats().getAge() - 5 && organisms[this.organismID].getStats().getAgeLastBred() < organisms[this.organismID].getStats().getAge() - 5)
                        stats.setOrganismsMet(stats.getOrganismsMet() + 1);
                    stats.setTotalOrganismsMet(stats.getTotalOrganismsMet() + 1);
                }
            }
        }
        stats.setOrganismsEncountered(Math.min(0.5, stats.getOrganismsEncountered())); // Hard-cap the amount of organisms encountered
        stats.setOrganismsEncountered(Math.max(0.0, stats.getOrganismsEncountered())); // Ensure it's not negative
    }

    // This method updates the organisms current stored food factor, as well as preferred and eaten food
    private void updateFood(TerrainAttributes terrainAttributes) {
        // Finding the food types for the current terrain
        ArrayList<String> availableFoods = terrainAttributes.foods;

        // This is in the case that a file is being loaded
        // The preferred food type of organisms was stored as a string
        // Therefore if it is currently holding a string (and not null) then transfer it over
        if (stats.getPreferredFoodString() != null) {
            stats.setPreferredFood(getFoodType(stats.getPreferredFoodString())); // Transfer and store the food type
            stats.setPreferredFoodString(null); // Return the preferred string to be null
        }

        if (stats.getAge() < 5 && stats.getPreferredFood() == null) {
            // If the organism is younger than 5, as by documentation the organism can eat anything
            // What the organism eats within the first 5 years is added to a list of recent foods
            // Once the organism passes year 5, they eat the food they ate the most within the first 5 years
            String tempFood; // The food that will be chosen
            FoodTypes potentialFood1 = getFoodType(availableFoods.get(0)), potentialFood2 = getFoodType(availableFoods.get(1)), potentialFood3 = getFoodType(availableFoods.get(2)); // The foods that can be chosen

            double totalAvailability = potentialFood1.foodAvailability + potentialFood2.foodAvailability + potentialFood3.foodAvailability;
            double randomChoice = random.nextDouble(0, totalAvailability);
            if (randomChoice > 0 && randomChoice <= potentialFood1.foodAvailability)
                tempFood = availableFoods.remove(0); // Returns the first item from the list
            else if (randomChoice > potentialFood1.foodAvailability && randomChoice <= potentialFood2.foodAvailability + potentialFood1.foodAvailability)
                tempFood = availableFoods.remove(1); // Returns the second item from the list
            else tempFood = availableFoods.remove(2); // Returns the last food in the list

            List<FoodTypes> recentFoods = new ArrayList<>();

            if (stats.getRecentFood() != null) recentFoods = stats.getRecentFood();
            recentFoods.add(getFoodType(tempFood)); // Adds the most recent food
            stats.setRecentFood(recentFoods); // Updates the food eaten list
            stats.setEatenFood(getFoodType(tempFood)); // The food which was selected is set to as eaten
        } else if (stats.getAge() == 5 && stats.getPreferredFood() == null) {
            // Once the organism passes year 5, they select preferred food based on the most common food found during first 5 years
            List<FoodTypes> recentFoods = stats.getRecentFood();
            int count = 0;
            FoodTypes mostCommon = new FoodTypes();
            // Choosing the most common food that was eaten
            for (int counter1 = 0; counter1 < recentFoods.size(); counter1++) {
                int counter3 = 0;
                for (FoodTypes recentFood : recentFoods) if (recentFoods.get(counter1) == recentFood) counter3++;
                if (counter3 > count) {
                    count = counter3;
                    mostCommon = recentFoods.get(counter1);
                }
            }
            stats.setPreferredFood(mostCommon); // Set the preferred food to the food type of most eaten food
            stats.setEatenFood(mostCommon); // Set the eaten food to the food type of most eaten food
            stats.setRecentFood(new ArrayList<>()); // Clear the array to free space
        } else {
            // If the age is over 5, get the preferred food. Check if the preferred food is part of the available foods list,
            // if it is set to the eaten food, if it isn't, use foodAvailability to set a new eaten food from then environment
            // preferred and eaten food below
            if (availableFoods.contains(stats.getPreferredFood().foodName))
                stats.setEatenFood(stats.getPreferredFood()); // If the preferred food is available, then eat it
            else {
                // If preferred food is not available, check each food available for the closest match to the preferred food
                for (String food : availableFoods) {
                    FoodTypes currentFood = getFoodType(food);
                    if (currentFood.parentName.equals(stats.getPreferredFood().parentName)) {
                        stats.setEatenFood(currentFood); // Within the same food category, therefore closest
                        break;
                    } else if (currentFood.landBasedFood == stats.getPreferredFood().landBasedFood) {
                        stats.setEatenFood(currentFood); // Within same food type, continue checking as may not be same class
                        if (food.equals(availableFoods.get(availableFoods.size() - 1)))
                            break; // If there are no more available foods, then this is the closest
                    } else
                        stats.setEatenFood(getFoodType(food)); // If all available food types are not within the same food type or category
                }
            }
        }
        // If it has a preferred food then update depending on that
        if (stats.getPreferredFood() != null) {
            FoodTypes preferredFood = stats.getPreferredFood(); // Preferred Food
            FoodTypes eatenFood = stats.getEatenFood(); // Current Food
            double foodFactor;

            // If the two foods are the same
            if (Objects.equals(preferredFood.foodName, eatenFood.foodName)) foodFactor = eatenFood.foodFactor;
            else if (Objects.equals(preferredFood.parentName, eatenFood.parentName))
                foodFactor = eatenFood.foodFactor * 0.8; // If they are part of the same food class
            else if ((preferredFood.waterBasedFood == eatenFood.waterBasedFood) || (preferredFood.landBasedFood && eatenFood.landBasedFood))
                foodFactor = eatenFood.foodFactor * 0.6; // If they are the same food type
            else foodFactor = eatenFood.foodFactor * 0.3; // If they are different food types

            if (Objects.equals(organisms[organismID].getCurrentTerrain(), stats.getTSB()))
                foodFactor = foodFactor + (foodFactor * 0.4); // If it has a TSB, then give a food factor benefit

            stats.setFoodFactor(foodFactor);
        } else {
            // If the organism is younger than 5 then just normal food factor
            stats.setFoodFactor(stats.getEatenFood().foodFactor);
        }
    }

    // Updates the organisms speed
    // Takes the old speed, age and foodFactor into a formula
    private void updateSpeed() {
        // newSpeed = oldSpeed * (1.01 + foodFactor * age / (1 + age ^ 3)
        double oldSpeed = stats.getSpeed();
        double foodFactor = stats.getFoodFactor();
        int age = stats.getAge();

        // Formatted to 2dp to make more readable and take less space in JSON
        double newSpeed = Double.parseDouble(decimalFormat.format(oldSpeed * (1.01 + foodFactor * age / (1 + Math.pow(age, 3)))));
        stats.setSpeed(newSpeed); // Updating the stats
    }

    // This method updates the organisms preferred temperature as well as the temperature factor
    // Takes in the current temperature of the terrain, as well as the preferred temp (if applicable)
    // Is affected by the current season (if enabled) See 3.10 Documentation
    private void updateTemperature(TerrainAttributes terrainAttributes) {
        int temperatureChange = 0;
        if (seasonsEnabled && season != null) {
            temperatureChange = switch (season) { // The temperature change which will take place due to the season
                case "Winter" -> random.nextInt(-11, -7); // Bound is exclusive so set to -7
                case "Spring" -> random.nextInt(-8, -2);
                case "Summer" -> random.nextInt(8, 11);
                default -> random.nextInt(3, 9);
            };
        }

        int newTemp = terrainAttributes.temperature + temperatureChange; // The new temperature for the organism

        if (stats.getAge() < 5) { // If the organism is younger than 5, it can handle any temperature
            List<Integer> recentTemp = stats.getRecentTemp(); // Update the list of recent temperatures
            recentTemp.add(newTemp); // Add in the new temperature
            stats.setRecentTemp(recentTemp); // Update the array in stats
        } else if (stats.getAge() == 5) { // If the organism is 5 years old, select a preferred temperature
            List<Integer> recentTemp = stats.getRecentTemp(); // Gets the list of previous temperatures
            int mostCommon;
            // Find the average temperature from the 5 previous years
            mostCommon = recentTemp.get(0) + recentTemp.get(1) + recentTemp.get(2) + recentTemp.get(3) + recentTemp.get(4);
            mostCommon = mostCommon / 5;
            stats.setPreferredTemp(mostCommon); // Set that as the preferred temperature
            stats.setRecentTemp(new ArrayList<>()); // Clear the recent temperature array to save space
            stats.setCurrentTemp(newTemp);
        } else { // Find the organisms temperature factor - based on the organisms preferred and current temperature
            int preferredTemp = stats.getPreferredTemp();
            int difference = Math.abs(newTemp - preferredTemp); // Finding the difference (as a positive (absolute value)) between the preferred and the current
            stats.setTempFactor(1 - difference * 0.04); // Setting the new temperature factor which will be used later.
            stats.setCurrentTemp(newTemp);
        }
    }

    // Compare the attributes of the current terrain to the organisms preferred attributes.
    // If the attributes are more favourable than its other preferred terrains, add it as preferred to the appropriate position
    private void updateHabitat(TerrainAttributes terrainAttributes) {
        String preferredTerrain = stats.getPreferredTerrain();
        String secondPreferredTerrain = stats.getSecondPreferredTerrain();
        String thirdPreferredTerrain = stats.getThirdPreferredTerrain();
        if (stats.getAge() >= 5 && !Objects.equals(currentTerrain, preferredTerrain) && !Objects.equals(currentTerrain, secondPreferredTerrain) && !Objects.equals(currentTerrain, thirdPreferredTerrain)) { // If the organism has chosen its preferred attributes
            int newCount = newCount(terrainAttributes); // The current terrain's score

            int currentCount = newCount(getTerrainAttributes(preferredTerrain)); // The preferred terrain's score
            int secondCurrentCount = newCount(getTerrainAttributes(secondPreferredTerrain)); // The score of the second and third terrains
            int thirdCurrentCount = newCount(getTerrainAttributes(thirdPreferredTerrain));

            // Checks the current score compared to other preferred terrains
            if (currentCount < newCount) {
                stats.setPreferredTerrain(terrainAttributes.terrainId);
                stats.setSecondPreferredTerrain(preferredTerrain); // Moving the other items down the list
                stats.setThirdPreferredTerrain(secondPreferredTerrain);
            } else if (secondCurrentCount < newCount && !preferredTerrain.equals(terrainAttributes.terrainId)) {
                stats.setSecondPreferredTerrain(terrainAttributes.terrainId);
                stats.setThirdPreferredTerrain(secondPreferredTerrain); // Moving the other items down the list
            } else if (thirdCurrentCount < newCount && !preferredTerrain.equals(terrainAttributes.terrainId) && !secondPreferredTerrain.equals(terrainAttributes.terrainId))
                stats.setThirdPreferredTerrain(terrainAttributes.terrainId);
        } else stats.setPreferredTerrain(preferredTerrain); // Default to the current terrain
    }

    // Adds up the score of the terrain sent in, is used to check against preferred terrains
    private int newCount(TerrainAttributes terrainAttributes) {
        if (!Objects.equals(terrainAttributes.terrainId, "Default")) { // If it is not set to the TerrainAttributes (null attributes)
            int currentTemp = terrainAttributes.temperature;
            List<String> availableFoods = terrainAttributes.foods;
            int preferredTemp = stats.getPreferredTemp();
            String preferredFood = stats.getPreferredFood().foodName;

            int newCount = 0;
            if (availableFoods.contains(preferredFood)) {
                newCount = newCount + 2; // If the food is contained in that terrain get an extra point
            } else newCount = newCount + 1;

            int tempDiff = Math.abs(preferredTemp - currentTemp);

            if (tempDiff < 5)
                newCount = newCount + 3; // If the preferred and current temperatures are within a range of 5 from each other get 3 points
            else if (tempDiff < 10) newCount = newCount + 2; // Within 10 then 2 points
            else newCount = newCount + 1;
            return newCount;
        }
        return 0; // Default the score at 0
    }

    // Updates the health of an organism
    // Uses the organisms temperature and food factors to calculate the new health
    private void updateHealth() {
        if (!stats.isDead()) { // If the organism isn't dead already
            double currentHealth = stats.getHealth(); // The current health
            double healthDecrease = 0.5; // Default decrease
            double temperatureDecrease = Math.abs(1 - stats.getTempFactor()); // The decrease due to temperature factor
            double foodDecrease = Math.abs(1 - stats.getFoodFactor()); // The decrease due to food factor
            double totalDecrease = healthDecrease + temperatureDecrease + foodDecrease; // The total decrease amount

            if (Objects.equals(stats.getTSB(), organisms[organismID].getCurrentTerrain()))
                totalDecrease = totalDecrease + 0.2;

            currentHealth = currentHealth - totalDecrease; // Decrease the health by the decreasing amount

            if (currentHealth <= 0) { // If the organism is dead, set it to be dead and then round the health to 0
                stats.setDead(true);
                currentHealth = 0.0;
            } else if (currentHealth > 100)
                stats.setHealth(100.0); // If the health is ever greater than 100, cap it at 100
            stats.setHealth(Double.parseDouble(decimalFormat.format(currentHealth))); // Setting to 2 decimal places
        }
    }

    // This will contain both breeding cycle calculations and predator / prey cycle calculations
    private void newOrganismEncounter(int originOrganismId, int secondaryOrganismId) {
        Organism originOrganism = organisms[originOrganismId]; // The two organisms are fetched
        Organism secondaryOrganism = organisms[secondaryOrganismId];
        int defaultAgeBarrier = 5;
        if (organisms.length > 150) defaultAgeBarrier = 20; // The amount of time between organisms giving birth
        else if (organisms.length > 100) defaultAgeBarrier = 10;

        if (originOrganism.getSex() != secondaryOrganism.getSex() && 30 > Math.abs(originOrganism.getStats().getAge() - secondaryOrganism.getStats().getAge())) { // If they are male and female
            if (originOrganism.getStats().getAgeLastBred() < originOrganism.getStats().getAge() - defaultAgeBarrier && secondaryOrganism.getStats().getAgeLastBred() < secondaryOrganism.getStats().getAge() - defaultAgeBarrier) { // If both are older than 15
                if (Math.abs(originOrganism.getxCoordinate() - secondaryOrganism.getxCoordinate()) <= 5 && Math.abs(originOrganism.getyCoordinate() - secondaryOrganism.getyCoordinate()) <= 5) { // If they are within a radius of 5
                    if (originOrganism.getSex() == 1) { // If the current organism is male
                        createChildOrganism(originOrganism, secondaryOrganism); // createChildOrganism (male, female)
                        originOrganism.getStats().setAgeLastBred(stats.getAge()); // Update age last bred for both
                        secondaryOrganism.getStats().setAgeLastBred(stats.getAge());
                    } else if (originOrganism.getSex() == 0) { // If current organism is female
                        createChildOrganism(secondaryOrganism, originOrganism); // createChildOrganism (male, female)
                        secondaryOrganism.getStats().setAgeLastBred(stats.getAge()); // Update age last bred for both
                        originOrganism.getStats().setAgeLastBred(stats.getAge());
                    }
                }
            }
        } else if (originOrganism.isPredator() || secondaryOrganism.isPredator()) { // Cause a fight if the organisms are the same sex and one is a predator

            if (originOrganism.isPredator() && !secondaryOrganism.isPredator())
                // If the origin organism is the predator and secondary is prey
                newOrganismAttack(originOrganism, secondaryOrganism);

            else if (!originOrganism.isPredator() && secondaryOrganism.isPredator())
                // If the origin organism is prey and secondary is predator
                newOrganismAttack(secondaryOrganism, originOrganism);

            else {
                // If both are predators, then a random one attacks with the other defending
                if (random.nextInt(0, 2) == 0) newOrganismAttack(originOrganism, secondaryOrganism);
                else newOrganismAttack(secondaryOrganism, originOrganism);
            }
        }
    }

    // Creates a JSON Object (new child) which will be added to a list,
    // which will be added to the JSON at the end of the year
    @SuppressWarnings("unchecked") // As it can be ignored
    private void createChildOrganism(Organism father, Organism mother) {
        // Create a new organism as a JSON object
        JSONObject newOrganism = new JSONObject(); // Creates a new JSON object for each organism

        // Adds a key/value pair to the json object initialised above
        newOrganism.put("organismId", organismCount);
        newOrganism.put("xCoordinate", mother.getxCoordinate()); // Spawns at its mother
        newOrganism.put("yCoordinate", mother.getyCoordinate());
        newOrganism.put("currentTerrain", GenerateMap.getTerrain(mother.getxCoordinate(), mother.getyCoordinate()));

        JSONObject statsObject = getStatsObject(father, mother); // Create the stats, will need to take attributes from mother and father

        newOrganism.put("stats", statsObject);
        newOrganism.put("sex", random.nextInt(0, 2)); // Random between male and female
        newOrganism.put("fatherID", father.getOrganismId());
        newOrganism.put("motherID", mother.getOrganismId());
        newOrganism.put("predator", false);

        organismsToAdd.add(newOrganism); // Adding to the new organisms to be saved at the end of the year
        organismCount++;
        if (father == mother) stats.setAgeLastBred(stats.getAge());
    }

    // Sets the new stats to be representative of parental attributes
    @SuppressWarnings("unchecked") // As it can be ignored
    private JSONObject getStatsObject(Organism father, Organism mother) {
        JSONObject statsObject = new JSONObject(); // The stats object is initialised

        double randomVariation = random.nextDouble(-0.2, 0.2); // Random variation to either side
        int randomVariation2 = random.nextInt(-2, 2); // Random integer variation to either side
        double chooseParent = random.nextInt(0, 2); // Choose to take from the mother or father

        // Random awareness from average of both parents with added double variation
        double newAwareness = (father.getStats().getCurrentAwareness() + mother.getStats().getCurrentAwareness()) / 2.0;
        statsObject.put("currentAwareness", newAwareness + randomVariation);

        statsObject.put("organismsEncountered", 0.0); // Updated during execution of program
        statsObject.put("awarenessHistory", new JSONArray()); // Only used during first 5 years, default value
        statsObject.put("age", 0); // The age needs to be representative and therefore start at 0

        statsObject.put("preferredFood", null); // Has to be set as FoodTypes however json simple cannot set as type class. See below

        FoodTypes newFood; // Chooses to eat food which either parent eats
        if (chooseParent == 1) newFood = father.getStats().getPreferredFood();
        else newFood = mother.getStats().getPreferredFood();
        statsObject.put("preferredFoodString", newFood.foodName); // Sets as a string to be changed to a FoodType

        statsObject.put("eatenFood", null); // Has not eaten yet, so does not need to be set
        statsObject.put("recentFood", new JSONArray()); // Used during first few years of organisms life
        statsObject.put("foodFactor", 0.0); // Calculated after an organism eats
        statsObject.put("sizeFactor", 0.0); // Calculated after an organism eats

        // An average size factor is calculated for both mother and father
        double averageFatherSizeFactor = father.getStats().getSizeFactor() / stats.getAge();
        double averageMotherSizeFactor = mother.getStats().getSizeFactor() / stats.getAge();
        double newSize = 0;
        if (averageFatherSizeFactor >= 0.25)
            newSize = newSize + 0.05; // These contribute to the increase or decrease in size for the new organism
        else newSize = newSize - 0.05;
        if (averageMotherSizeFactor >= 0.25) newSize = newSize + 0.05;
        else newSize = newSize - 0.05;
        if (chooseParent == 1)
            newSize = newSize + father.getStats().getSize(); // Then chooses a random parent to take size from
        else newSize = newSize + mother.getStats().getSize();
        statsObject.put("size", Double.parseDouble(decimalFormat.format(Math.max(0.3, newSize)))); // Sets to 2dp

        // Takes the parents starting speed, and decreases if the current speed is low, and visa-versa
        double newSpeed;
        if (chooseParent == 1) newSpeed = father.getStats().getStartSpeed();
        else newSpeed = mother.getStats().getStartSpeed();
        if (mother.getStats().getSpeed() > 10.0) newSpeed = newSpeed + 0.5;
        else newSpeed = newSpeed - 0.5;
        statsObject.put("speed", Math.min(30.0, Math.max(3.0, newSpeed))); // Lowest it can be is 3, highest is 30.

        statsObject.put("startSpeed", newSpeed);

        statsObject.put("currentTemp", 0); // Changes during the organisms life, no default value

        int newTemp; // Temp is taken from either father or mother, with an added integer variation
        if (chooseParent == 1) newTemp = father.getStats().getPreferredTemp();
        else newTemp = mother.getStats().getPreferredTemp();
        statsObject.put("preferredTemp", newTemp + randomVariation2);

        statsObject.put("recentTemp", new JSONArray()); // Changes during organisms life, no default value
        statsObject.put("tempFactor", 1); // Changes during the organisms life, no default value

        String newTerrain; // Preferred terrain is taken from either father or mother
        if (chooseParent == 1) newTerrain = father.getStats().getPreferredTerrain();
        else newTerrain = mother.getStats().getPreferredTerrain();
        statsObject.put("preferredTerrain", newTerrain);

        statsObject.put("secondPreferredTerrain", "Default"); // Will be chosen by the organism itself
        statsObject.put("thirdPreferredTerrain", "Default"); // Will be chosen by the organism itself

        statsObject.put("health", 100.0); // Default value for all organisms
        statsObject.put("dead", false); // Default value for all organisms
        statsObject.put("ageLastBred", -1); // Default value for all organisms

        // The generation of the organism set to the lowest of both parents + 1
        int generation = Math.min(father.getStats().getGeneration(), mother.getStats().getGeneration());
        statsObject.put("generation", generation + 1);

        statsObject.put("organismsMet", 0); // Default values
        statsObject.put("totalOrganismsMet", 0);
        statsObject.put("tsb", father.getStats().getTSB()); // Take the fathers TSB if it has one
        statsObject.put("lastAttack", -1);

        return statsObject; // Returns the stats as a JSONObject
    }

    // This method ensures that the new organisms are added at the end of the year (when organisms have finished updating)
    // so that there are no discrepancies between GameWindow's organisms and the JSON file
    private void addNewOrganisms(List<JSONObject> newOrganisms) {
        JSONArray jsonArray; // This will contain the current JSON array
        JSONParser parser = new JSONParser();

        try (FileReader reader = new FileReader("organisms.json")) {
            // Parse the JSON file
            Object obj = parser.parse(reader);
            jsonArray = (JSONArray) obj;

            jsonArray.addAll(newOrganisms); // Adding in the new organisms from the organismsToAdd list

            // Save the JSON array to the file
            FileWriter fileWriter = new FileWriter("organisms.json");
            fileWriter.write(jsonArray.toJSONString());
            fileWriter.flush();
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e); // Catch any errors
        }
    }

    // Checks if the organism should become asexual
    // Checks the mothers of the organism for the number of fecund organisms encountered, if it is less than 10 each, the organism can asexually reproduce
    private void checkAsexual() {
        int generationsToCheck = 12; // The number of generations of mothers to check
        Organism currentOrganism = organisms[this.organismID]; // The organism being operated on

        // If the organism is water based then there is a significant boost in the chance it becomes asexual
        if (Objects.equals(currentOrganism.getCurrentTerrain(), "Deep Water") || Objects.equals(currentOrganism.getCurrentTerrain(), "Water"))
            generationsToCheck = 3;

        if (currentOrganism.getSex() != 2) { // If it is not already asexual
            // Check the generations of mothers for how many organisms have been encountered
            if (stats.getGeneration() >= generationsToCheck) {
                generationsToCheck = generationsToCheck - 3;
                int totalEncounters = 0;
                List<Organism> organismsToCheck = new ArrayList<>(); // List of mothers to check
                for (int counter = 0; counter < generationsToCheck; counter++) {
                    if (currentOrganism.getMotherID() != -1) {
                        organismsToCheck.add(organisms[currentOrganism.getMotherID()]); // Add the immediate mother
                        currentOrganism = organisms[currentOrganism.getMotherID()]; // Set the current organism to be the mother
                    }
                }
                for (Organism organism : organismsToCheck) { // Add up the total encounters
                    totalEncounters = totalEncounters + organism.getStats().getOrganismsMet();
                }
                if (totalEncounters <= 5 * generationsToCheck) { // If the number of encounters are less than the 10 each
                    organisms[this.organismID].setSex(2); // Set to be asexual
                    GameWindow.consoleLog(this.organismID + " has become asexual");
                    stats.setAgeLastBred(stats.getAge());
                }
            }
        }
    }

    // Checks if the organism should have terrain specific benefits
    // Checks 5 generations of fathers, if they all have the same preferred terrain,
    // then the organism will have terrain specific benefits (TSB) for that terrain
    private void checkTSB() {
        int generationsToCheck = 8; // Number of generations to check
        Organism currentOrganism = organisms[this.organismID]; // The organism to be operated on
        String preferredTerrain = currentOrganism.getStats().getPreferredTerrain(); // The current organisms preferred terrain
        int terrainLog = 0; // Tracks how many of the fathers have the same preferred terrain

        if (stats.getTSB() == null && stats.getGeneration() >= generationsToCheck) { // If the TSB is not already set and organism is old enough
            generationsToCheck = generationsToCheck - 3;
            List<Organism> organismsToCheck = new ArrayList<>(); // The list of father organisms to check
            for (int counter = 0; counter < generationsToCheck; counter++) { // Populates the list
                if (currentOrganism.getFatherID() != -1) {
                    organismsToCheck.add(organisms[currentOrganism.getFatherID()]); // Adds the father to the list
                    currentOrganism = organisms[currentOrganism.getFatherID()]; // Sets the current one to operate on to the father of the previous
                }
            }
            for (Organism organism : organismsToCheck) { // Checks the preferred terrain of each
                if (preferredTerrain.equals(organism.getStats().getPreferredTerrain())) {
                    terrainLog++; // Increments if the preferred terrain is the same
                }
            }
            if (terrainLog == organismsToCheck.size()) {
                stats.setTSB(preferredTerrain); // All preferred terrains are the same and therefore organism will have TSB for that terrain
                GameWindow.consoleLog(currentOrganism.getOrganismId() + " has a new terrain specific benefit on " + stats.getTSB());
            }
        }
    }

    // This method checks if the organism should be a predator if it isn't already
    private void checkPredation() {
        int generationsToCheck = 5; // Number of generations to check
        Organism currentOrganism = organisms[this.organismID]; // The organism to be operated on

        if (stats.getGeneration() >= generationsToCheck) { // If the organism is old enough
            int totalEncounters = 0; // To count the number of organisms met
            generationsToCheck = generationsToCheck - 3;
            List<Organism> organismsToCheck = new ArrayList<>(); // The list of father organisms to check
            for (int counter = 0; counter < generationsToCheck; counter++) { // Populates the list
                if (currentOrganism.getFatherID() != -1) {
                    organismsToCheck.add(organisms[currentOrganism.getFatherID()]); // Adds the father to the list
                    currentOrganism = organisms[currentOrganism.getFatherID()]; // Sets the current one to operate on to the father of the previous
                }
            }
            for (Organism organism : organismsToCheck) { // Add up the total encounters
                totalEncounters = totalEncounters + organism.getStats().getTotalOrganismsMet();
            }
            if (totalEncounters >= 150 * generationsToCheck) { // If the number of encounters are less than 450 each
                organisms[this.organismID].setPredator(true); // Organism becomes a predator
                GameWindow.consoleLog(this.organismID + " is now a predator");
            }
        }
    }

    // The decision between which organism wins during an attack
    private void newOrganismAttack(Organism attackingOrganism, Organism defendingOrganism) {
        double attackerSpeed, defenderSpeed, attackerSize, defenderSize; // Defining the attributes to be used
        int attackerAge, defenderAge;
        int attackerHealth = 0, defenderHealth = 0; // Default health decrease

        if (attackingOrganism.getStats().getLastAttack() < attackingOrganism.getStats().getAge() - 15) {
            // Initialising the attributes
            attackerSpeed = attackingOrganism.getStats().getSpeed();
            attackerSize = attackingOrganism.getStats().getSize();
            attackerAge = attackingOrganism.getStats().getAge();
            defenderSpeed = defendingOrganism.getStats().getSpeed();
            defenderSize = defendingOrganism.getStats().getSize();
            defenderAge = defendingOrganism.getStats().getAge();

            // Getting the differences between the attributes
            double speedScore = (attackerSpeed - defenderSpeed);
            double sizeScore = (attackerSize - defenderSize);
            int ageScore = (attackerAge - defenderAge);

            if ((speedScore >= 0 && sizeScore >= 0) || (sizeScore >= 0 && ageScore < 20) || (speedScore >= 0 && ageScore < 20)) { // The attacker has won, give the attacker benefits
                attackerHealth = attackerHealth - random.nextInt(1, 4); // Attacker health high decrease
                defenderHealth = defenderHealth - random.nextInt(7, 13); // Defender low health decrease
                // Give the attacker an awareness increase
                organisms[attackingOrganism.getOrganismId()].getStats().setCurrentAwareness(organisms[attackingOrganism.getOrganismId()].getStats().getCurrentAwareness() + 0.2);
                GameWindow.consoleLog(attackingOrganism.getOrganismId() + " has attacked " + defendingOrganism.getOrganismId() + " and has won");
            } else { // The defendant has won, give the defendant benefits
                attackerHealth = attackerHealth - random.nextInt(4, 8); // Attacker health high decrease
                defenderHealth = defenderHealth - random.nextInt(3, 7); // Defender low health decrease
                organisms[defendingOrganism.getOrganismId()].getStats().setCurrentAwareness(organisms[defendingOrganism.getOrganismId()].getStats().getCurrentAwareness() + 0.2); // Give the defender an awareness increase
                GameWindow.consoleLog(attackingOrganism.getOrganismId() + " has attacked " + defendingOrganism.getOrganismId() + " and has lost");
            }
            // Increase their abilities and update the health
            organisms[attackingOrganism.getOrganismId()].getStats().setHealth(organisms[attackingOrganism.getOrganismId()].getStats().getHealth() + attackerHealth);
            organisms[defendingOrganism.getOrganismId()].getStats().setHealth(organisms[defendingOrganism.getOrganismId()].getStats().getHealth() + defenderHealth);
            organisms[attackingOrganism.getOrganismId()].getStats().setLastAttack(organisms[attackingOrganism.getOrganismId()].getStats().getAge()); // Set the attacking organisms age to its current age
        }
    }
}