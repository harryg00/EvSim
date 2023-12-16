import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Objects;

// The SideMenu class displays the organism information to the user and allows the user to control
// the simulation
public class SideMenu implements MouseListener {
    private JPanel SideMenu; // The panel itself which contains all other components which are listed below
    private JButton speedUpButton;
    private JButton slowDownButton;
    private JButton pauseGameButton;
    private JButton saveSimulationButton;
    private JButton loadPreviousSaveButton;
    private JButton exitToMainMenuButton;
    private JLabel organismCountLabel;
    private JLabel predatorCountLabel;
    private JLabel mostAware;
    private JLabel oldestOrganismLabel;
    private JLabel healthiestOrganismLabel;
    private JLabel fastestOrganismLabel;
    private JLabel averageTemperatureLabel;
    private JLabel mostPopulatedTerrainLabel;
    private JLabel leastPopulatedTerrainLabel;
    private JLabel newOrganismsSinceYearLabel;
    private JLabel currentYearLabel;
    private JLabel currentGameSpeedLabel;
    private JPanel organismInformationPanel;
    private JTextField organismToFind;
    private JButton findButton;
    private JTabbedPane organismInfoTab;
    private JCheckBox showDeadOrganismsCheckBox;
    private JLabel KeyLabel;
    private JCheckBox showConsoleCheckbox;
    private static final DecimalFormat decimalFormat = new DecimalFormat("#.##");

    // Returns the side menu panel to be added to the side of the game window
    public JPanel getSideMenu() {
        return SideMenu; // Returns the panel (with added components)
    }

    // The constructor for SideMenu
    // Initialises all buttons, text, and adds in the key once it is called at the start of the simulation
    public SideMenu(){
        // Setting text such as the current game speed and year from GameWindow
        currentGameSpeedLabel.setText("Current Game Speed: " + (double) GameWindow.timeFactor / 1000 + " seconds/year");
        currentYearLabel.setText("Current Year: " + GameWindow.years);

        slowDownButton.addMouseListener(this); // Adding relevant mouse listeners
        speedUpButton.addMouseListener(this);
        pauseGameButton.addMouseListener(this);
        exitToMainMenuButton.addMouseListener(this);
        saveSimulationButton.addMouseListener(this);
        findButton.addMouseListener(this);
        loadPreviousSaveButton.addMouseListener(this);

        ImageIcon keyImage; // The key image to be added to the side menu
        URL resourceUrl = SideMenu.class.getResource("/KeyResource.png"); // Attempts to find from class (jar)
        if (resourceUrl != null) keyImage = new ImageIcon(resourceUrl);
        else keyImage = new ImageIcon("C:/Users/Harry/IdeaProjects/EvSim/res/KeyResource.png"); // Else get from source file (IDE)

        KeyLabel.setIcon(keyImage); // Adds in the image as an icon
    }

    // This function updates the UI to show either global or local organism statistics.
    // Takes the organisms' statistics from JSON, and updates for the relevant label
    // Refer to comments for explanation of specific sections
    public void UpdateUI() { // Called each time the game window paints to the screen
        GenerateOrganisms.showDeadOrganisms = showDeadOrganismsCheckBox.isSelected(); // Update whether to show the dead organisms

        if(GameWindow.gamePaused) pauseGameButton.setText("Play Game"); // Update the play / pause button (to show what it is currently)
        else pauseGameButton.setText("Pause Game");

        // If the user wishes to show the console, then show it, if not, remove it
        if(showConsoleCheckbox.isSelected()){
            GameWindow.showConsole = true;
        } else {
            GameWindow.showConsole = false;
            GameWindow.frame.remove(GameWindow.consolePanel);
        }

        // Initialising attributes that are to be used
        int oldestOrganism = 0, healthiestOrganism = 0, fastestOrganism = 0, averageTemp, mostAwareOrganism = 0, predatorCount = 0;
        String mostCommonTerrain = "N/A", leastCommonTerrain = "N/A";

        // If an organism is not selected, display global statistics
        if (GameWindow.organismSelected == -1) {
            // The length is equivalent to the number of objects and therefore the number of organisms
            // Once organisms have the ability to die, this will have to update to only count alive organisms
            organismCountLabel.setText("Total Organism Count: " + GameWindow.organisms.length);

            // Temporary variables which are used to compare organisms
            int tempAge = GameWindow.organisms[0].getStats().getAge();
            double tempHealth = GameWindow.organisms[0].getStats().getHealth();
            double tempSpeed = GameWindow.organisms[0].getStats().getSpeed();
            double tempAwareness = GameWindow.organisms[0].getStats().getCurrentAwareness();
            double totalTemp = 0;
            int grassCount = 0, waterCount = 0, mountainCount = 0, beachCount = 0, snowCount = 0, forestCount = 0, deepWaterCount = 0;

            // Loops through each organism and compares statistics
            // Refer to comments for explanation of specific sections
            for (Organism organism : GameWindow.organisms) {
                // Finds the organism with the highest age
                if (tempAge < organism.getStats().getAge()) {
                    oldestOrganism = organism.getOrganismId();
                    tempAge = organism.getStats().getAge();
                }
                // Finds the organism with the highest health
                if (tempHealth < organism.getStats().getHealth()) {
                    healthiestOrganism = organism.getOrganismId();
                    tempHealth = organism.getStats().getHealth();
                }
                // Finds the organism with the highest speed
                if (tempSpeed < organism.getStats().getSpeed()) {
                    fastestOrganism = organism.getOrganismId();
                    tempSpeed = organism.getStats().getSpeed();
                }
                if(tempAwareness < organism.getStats().getCurrentAwareness()) {
                    mostAwareOrganism = organism.getOrganismId();
                    tempAwareness = organism.getStats().getCurrentAwareness();
                }

                // Adds the preferred temperature of each organism
                // Is used to get the mean temperature
                totalTemp = totalTemp + GameWindow.organisms[0].getStats().getCurrentTemp();

                // To find how many organisms are in each terrain
                switch (organism.getCurrentTerrain()) {
                    case "Grass" -> grassCount++;
                    case "Water" -> waterCount++;
                    case "Mountain" -> mountainCount++;
                    case "Beach" -> beachCount++;
                    case "Snow" -> snowCount++;
                    case "Forest" -> forestCount++;
                    case "Deep Water" -> deepWaterCount++;
                }

                if(organism.isPredator()) predatorCount++; // Total the number of predators
            }

            // Variables which are used to compare how common terrains are, as well as set the equivalent terrain name
            int[] terrainCounts = {grassCount, waterCount, mountainCount, beachCount, snowCount, forestCount, deepWaterCount};
            String[] terrainNames = {"Grass", "Water", "Mountain", "Beach", "Snow", "Forest", "Deep Water"};
            int maxCount = 0, minCount = Integer.MAX_VALUE; // Set to MAX_VALUE so that there can be a large amount of organisms in a terrain, will never reach this value

            // Checks for both the most common and least common terrain
            for (int i = 0; i < terrainCounts.length; i++) {
                if (terrainCounts[i] > maxCount) { // If there are more organisms than the previous
                    maxCount = terrainCounts[i]; // Current highest organism count
                    mostCommonTerrain = terrainNames[i]; // Sets the terrain name as the most common
                }
                if (terrainCounts[i] < minCount) { // If there are fewer organisms than the previous
                    minCount = terrainCounts[i]; // Current lowest organism count
                    leastCommonTerrain = terrainNames[i]; // Sets the terrain name as the least common
                }
            }

            averageTemp = (int) (totalTemp / GameWindow.organisms.length); // Calculates the mean temperature (average)

            // Updates each label to show the relevant information, as well as the statistic to go with it
            oldestOrganismLabel.setText("<html>Oldest Organism ID: " + oldestOrganism + " <u>(" + GameWindow.organisms[oldestOrganism].getStats().getAge() + " years)</u></html>");
            predatorCountLabel.setText("Predator Count: " + predatorCount);
            mostAware.setText("<html>Most Aware: " + mostAwareOrganism + " (<u>" + Double.parseDouble(decimalFormat.format(tempAwareness * 10)) + "px</u>)</html>");
            healthiestOrganismLabel.setText("<html>Healthiest Organism ID: " + healthiestOrganism + " <u>(" + GameWindow.organisms[healthiestOrganism].getStats().getHealth() + ")</u></html>");
            fastestOrganismLabel.setText("<html>Fastest Organism ID: " + fastestOrganism + " <u>(" + GameWindow.organisms[fastestOrganism].getStats().getSpeed() + "px/year)</u></html>");
            averageTemperatureLabel.setText("<html>Average Temperature: " + " <u>(" + averageTemp + ")</u></html>");
            mostPopulatedTerrainLabel.setText("<html>Most Popular: <i>" + mostCommonTerrain + "</i> <u>(" + maxCount + " organisms)</u></html>");
            leastPopulatedTerrainLabel.setText("<html>Least Popular: <i>" + leastCommonTerrain + "</i> <u>(" + minCount + " organisms)</u></html>");
            newOrganismsSinceYearLabel.setText("<html>New Organisms: " + " <u>(" + (GameWindow.organisms.length - GenerateOrganisms.organismCount) + ")</u></html>");

        } else { // There is an organism selected, show its attributes

            // If there is no preferred food, show it as N/A, otherwise show the name of the preferred food
            String preferredFood;
            if(GameWindow.organisms[GameWindow.organismSelected].getStats().getPreferredFood() == null) preferredFood = "N/A";
            else preferredFood = GameWindow.organisms[GameWindow.organismSelected].getStats().getPreferredFood().foodName;

            // Returning the sex of the organism as a String
            String sex = switch (GameWindow.organisms[GameWindow.organismSelected].getSex()){
                case 0 -> "Female";
                case 1 -> "Male";
                case 2 -> "Asexual";
                default -> "N/A"; // Has to have a default case (if no sex then just show N/A)
            };

            // Replace the labels with specific organism statistics
            organismCountLabel.setText("Total Organism Count: " + GameWindow.organisms.length); // The number of organisms
            predatorCountLabel.setText("Selected Organism ID: " + GameWindow.organisms[GameWindow.organismSelected].getOrganismId()); // The organism selected currently
            // The coordinates of the organism (x, y) currentTerrain
            mostAware.setText("<html>Coordinates: (" + GameWindow.organisms[GameWindow.organismSelected].getxCoordinate() + ", " +
                    GameWindow.organisms[GameWindow.organismSelected].getyCoordinate() + ") <i>" +
                    GameWindow.organisms[GameWindow.organismSelected].getCurrentTerrain() + "</i></html>");
            // Whether it is a predator or not and if it has a terrain specific benefit
            oldestOrganismLabel.setText("<html>Predator: " + GameWindow.organisms[GameWindow.organismSelected].isPredator() +
                    "<br>Terrain Specific Benefit: " + GameWindow.organisms[GameWindow.organismSelected].getStats().getTSB() + "</html>");
            // The preferred terrain of the organism if available
            healthiestOrganismLabel.setText("Preferred Terrain: " + GameWindow.organisms[GameWindow.organismSelected].getStats().getPreferredTerrain());
            fastestOrganismLabel.setText("Sex: " + sex); // The sex of the organism (see above for sex initialisation)

            // Displays the other stats in HTML bullet point format
            averageTemperatureLabel.setText("<html><p>Stats: </p>" +
                    "<ul>" +
                    "    <li>Age: " + GameWindow.organisms[GameWindow.organismSelected].getStats().getAge() + "</li>" + // Age
                    "    <li>Size: " + GameWindow.organisms[GameWindow.organismSelected].getStats().getSize() + "sq/m</li>" + // Size
                    "    <li>Awareness: " + decimalFormat.format(GameWindow.organisms[GameWindow.organismSelected].getStats().getCurrentAwareness() * 10) + "px</li>" + // Awareness
                    "    <li>Speed: " + (int) GameWindow.organisms[GameWindow.organismSelected].getStats().getSpeed() + "px / year</li>" + // Speed
                    "    <li>Health: " + GameWindow.organisms[GameWindow.organismSelected].getStats().getHealth() + "</li>" + // Health
                    "    <li>Current Temperature: " + GameWindow.organisms[GameWindow.organismSelected].getStats().getCurrentTemp() + "</li>" + // Current Temperature
                    "    <li>Preferred Temperature: " + GameWindow.organisms[GameWindow.organismSelected].getStats().getPreferredTemp() + "</li>" + // Preferred Temperature
                    "</ul></html>");

            // The parents of the organism (father, mother)
            mostPopulatedTerrainLabel.setText("Parent IDs: (" + GameWindow.organisms[GameWindow.organismSelected].getFatherID()  + "f, " + GameWindow.organisms[GameWindow.organismSelected].getMotherID() + "m)");

            // If the organism has eaten, display the eaten food
            if(GameWindow.organisms[GameWindow.organismSelected].getStats().getEatenFood().foodName != null) leastPopulatedTerrainLabel.setText("Eaten Food: " + GameWindow.organisms[GameWindow.organismSelected].getStats().getEatenFood().foodName);
            else leastPopulatedTerrainLabel.setText("Eaten Food: N/A"); // Otherwise display N/A
            // Displays the organisms preferred food
            newOrganismsSinceYearLabel.setText("Preferred Food: " + preferredFood);
        }

        // Displays the years held in GameWindow and the season held in Organisms at the top of the menu
        currentYearLabel.setText("Current Year: " + GameWindow.years + " - Season: " + Organisms.season);
    }

    // This checks for any mouse clicks by the user
    // On components which have a mouse listener, such as the save and load buttons
    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        // Every 1000 is a second, anything longer than 8.5 seconds will be too long.
        // Anything below 0.5 seconds is too short.
        if(mouseEvent.getSource() == slowDownButton && GameWindow.timeFactor < 8500) GameWindow.timeFactor = GameWindow.timeFactor + 500; // Increase the seconds per year by 0.5 seconds
        if(mouseEvent.getSource() == speedUpButton && GameWindow.timeFactor > 500) GameWindow.timeFactor = GameWindow.timeFactor - 500; // Decrease the seconds per year by 0.5 seconds
        if(mouseEvent.getSource() == pauseGameButton) {
            GameWindow.gamePaused = !GameWindow.gamePaused; // Flip the paused boolean
            if(Objects.equals(pauseGameButton.getText(), "Play Game")) pauseGameButton.setText("Pause Game");
            else pauseGameButton.setText("Play Game"); // Flip the text shown to equate whether the game is paused or not
        }

        // Disallow the user from setting anything lower than 0.5 seconds/year
        speedUpButton.setEnabled(GameWindow.timeFactor != 500);
        // Disallow the user from setting anything higher than 8.5 seconds/year
        slowDownButton.setEnabled(GameWindow.timeFactor != 8500);

        // For every 1000 in gameWindow.timeFactor, it is 1 second. Therefore, to divide it by 1000 is the time in seconds.
        currentGameSpeedLabel.setText("Current Game Speed: " + (double) GameWindow.timeFactor / 1000 + " seconds/year"); // Update the label to show the current game speed in seconds per year

        // If the user wishes to exit to the main menu
        if(mouseEvent.getSource() == exitToMainMenuButton){
            // Reset all attributes
            GameWindow.organismSelected = -1;
            new OpeningMenu(); // Return to the opening menu
            GameWindow.years = 0;
            GameWindow.timeFactor = 2500;
            GameWindow.gamePaused = false;
            GameWindow.thread.interrupt(); // Stop from updating and running
            GameWindow.frame.dispose(); // Remove the game window
        }

        // Load and save simulation buttons
        if(mouseEvent.getSource() == saveSimulationButton){
            new SaveSimulation(); // Load the save simulation window
            GameWindow.gamePaused = true; // Pause the game (usability)
        }
        // Load simulation button
        if(mouseEvent.getSource() == loadPreviousSaveButton){
            new LoadSimulation(); // Load the load simulation window
            GameWindow.gamePaused = true; // Pause the game (usability)
        }

        // If the found button was pressed (find organism)
        if(mouseEvent.getSource() == findButton){
            try { // Attempt to pass the input into an integer
                GameWindow.organismSelected = Integer.parseInt(organismToFind.getText().trim()); // Validate and set the input
                organismInfoTab.setSelectedIndex(0); // Return to the organism information tab
                organismToFind.setText(""); // Clear the text from the text box
            } catch (NumberFormatException e) { // If it could not be validated create the error message
                new ErrorWindow("Organism not found!");
            }
        }
    }
    // Needed but not used by the program
    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
}