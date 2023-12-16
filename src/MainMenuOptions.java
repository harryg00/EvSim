import com.fasterxml.jackson.databind.ObjectMapper;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

// The options menu which displays the accessibility options such as colours and window size
public class MainMenuOptions implements ItemListener, MouseListener {
    private JPanel mainMenu; // The panel containing all other components which are also listed below
    private JComboBox grassColour;
    private JComboBox waterColour;
    private JComboBox mountainColour;
    private JComboBox beachColour;
    private JComboBox snowColour;
    private JComboBox forestColour;
    private JComboBox deepWaterColour;
    private JComboBox windowSize;
    private JButton backButton;
    private JButton saveButton;
    private JPanel grassPreview;
    private JPanel waterPreview;
    private JPanel mountainPreview;
    private JPanel beachPreview;
    private JPanel snowPreview;
    private JPanel forestPreview;
    private JPanel deepWaterPreview;
    JFrame optionsFrame; // The window which will contain the panel
    private final Dimension size = Toolkit.getDefaultToolkit().getScreenSize(); // Getting the users monitor size
    private int newWindowHeight, newWindowWidth; // The new window size to set
    private final int maxWindowHeight = (int) size.getHeight(); // The maximum height and width from the monitor size
    private final int maxWindowWidth = (int) size.getWidth();
    private boolean windowSizeChange = false; // Whether it has been changed or not
    // The terrain colours (similar to what is found in GenerateMap)
    private static final Color[] terrainColours = {Color.GREEN, Color.BLUE, Color.GRAY, Color.YELLOW, Color.WHITE, new Color(13, 100, 10) /* DARK GREEN */, new Color(6, 11, 112) /* DARK BLUE */};

    // Constructor for the MainMenuOptions
    // Adds the action listeners, sets the window, and updates the settings as needed
    public MainMenuOptions() {
        grassColour.addItemListener(this); // Adds the relevant listeners
        waterColour.addItemListener(this); // Used to recognise user inputs and actions
        mountainColour.addItemListener(this);
        beachColour.addItemListener(this);
        forestColour.addItemListener(this);
        deepWaterColour.addItemListener(this);
        windowSize.addItemListener(this);
        backButton.addMouseListener(this);
        saveButton.addMouseListener(this);

        updateCurrentSettings(); // Updates to what is stored in JSON

        optionsFrame = new JFrame("Options");
        optionsFrame.setPreferredSize(new Dimension(357, 388)); // Same width as in the form creator
        optionsFrame.add(mainMenu); // As the panel already contains the labels and drop-downs, only the panel has to be added to the frame.
        optionsFrame.setResizable(false);
        optionsFrame.pack();
        optionsFrame.setLocationRelativeTo(null);
        optionsFrame.setVisible(true);
    }

    // Ensures that what is stored in JSON is the same as what is displayed to the user
    private void updateCurrentSettings() {
        ObjectMapper objectMapper = new ObjectMapper();
        Options data;
        // Read the JSON data stored
        try {
            File sourceFile = new File("options.json"); // Adjust the path as needed
            if (sourceFile.exists()) {
                data = objectMapper.readValue(sourceFile, Options.class);
            } else {
                // If the file doesn't exist in the source folder, read it from the JAR resources
                InputStream inputStream = getClass().getClassLoader().getResourceAsStream("options.json");
                if (inputStream != null) {
                    data = objectMapper.readValue(inputStream, Options.class);
                    inputStream.close();
                } else {
                    throw new RuntimeException(); // Log any errors
                }
            }
            List<Colours> colours = data.getColours(); // Returns the list of colours stored in JSON

            windowSize.setSelectedItem(data.getWindowWidth() + " x " + data.getWindowHeight()); // The current set window size
            // Represented as "WindowWidth x WindowHeight", e.g. 1920 x 1080.

            grassColour.setSelectedItem(colours.get(0).getSetting()); // Displaying the current selected setting inn the options menu
            waterColour.setSelectedItem(colours.get(1).getSetting()); // Takes the object at the first index, then takes the value of key "Setting" as a string
            mountainColour.setSelectedItem(colours.get(2).getSetting()); // get(2) returns the object at array index 2
            beachColour.setSelectedItem(colours.get(3).getSetting()); // get("Setting").asText() returns the current setting as a String
            forestColour.setSelectedItem(colours.get(4).getSetting()); // Which is then displayed as the selected item on the menu
            deepWaterColour.setSelectedItem(colours.get(5).getSetting());

        } catch (Exception exception) {
            throw new RuntimeException(exception); // Log any errors
        }

        // If the users monitor is smaller than these sizes,
        // then remove them, so they cannot be selected
        // (would cause the window to cover the full screen - unusable)
        if (maxWindowHeight < 1080 || maxWindowWidth < 1920) windowSize.removeItem("1920 x 1080");
        if (maxWindowHeight < 2160 || maxWindowWidth < 3840) windowSize.removeItem("3840 x 2160");
        if (maxWindowHeight < 1440 || maxWindowWidth < 2560) windowSize.removeItem("2560 x 1440");
        if (maxWindowHeight < 768 || maxWindowWidth < 1366) windowSize.removeItem("1366 x 768");
        if (maxWindowHeight < 900 || maxWindowWidth < 1440) windowSize.removeItem("1440 x 900");
        if (maxWindowHeight < 720 || maxWindowWidth < 1280) windowSize.removeItem("1280 x 720");
        if (maxWindowHeight < 1024 || maxWindowWidth < 1280) windowSize.removeItem("1280 x 1024");
    }

    // If the drop-down box changes state then update the menu accordingly
    // Updates the JPanels showing the user the file currently selected.
    @Override
    public void itemStateChanged(ItemEvent e) {
        // These statements check which box was changed, and alters the relating JPanel
        // Overloaded and cannot be simplified in any way
        // Majority of this is just setting the colours which will be.
        // Refer to the comments on the side to know which colours it is

        // If the user changed the grass drop down
        if (e.getSource() == grassColour) {
            if (grassColour.getSelectedItem() == "Default")
                grassPreview.setBackground(Color.GREEN); // Sets the colour for each colour-blindness
            else if (grassColour.getSelectedItem() == "Deuteranopia (1)")
                grassPreview.setBackground(new Color(255, 88, 51)); // Orange-red
            else if (grassColour.getSelectedItem() == "Deuteranopia (2)")
                grassPreview.setBackground(new Color(0, 153, 0)); // Darker green
            else if (grassColour.getSelectedItem() == "Protanopia (1)")
                grassPreview.setBackground(new Color(0, 153, 0)); // Darker green
            else if (grassColour.getSelectedItem() == "Protanopia (2)")
                grassPreview.setBackground(new Color(51, 51, 204)); // Purple-blue
            else if (grassColour.getSelectedItem() == "Tritanopia (1)")
                grassPreview.setBackground(new Color(255, 88, 51)); // Orange-red
            else if (grassColour.getSelectedItem() == "Tritanopia (2)")
                grassPreview.setBackground(new Color(255, 217, 0)); // Yellow
            else if (grassColour.getSelectedItem() == "Monochromacy (1)")
                grassPreview.setBackground(new Color(0, 0, 0)); // Black
            else if (grassColour.getSelectedItem() == "Monochromacy (2)")
                grassPreview.setBackground(new Color(255, 255, 255)); // White
            else if (grassColour.getSelectedItem() == "Trichromacy (1)")
                grassPreview.setBackground(new Color(255, 88, 51)); // Orange-red
            else if (grassColour.getSelectedItem() == "Trichromacy (2)")
                grassPreview.setBackground(new Color(0, 153, 0)); // Darker green
            terrainColours[0] = grassPreview.getBackground(); // Sets the grass (first index) to be the colour of the panel.
        }
        // If the user changed the water drop down
        else if (e.getSource() == waterColour) {
            if (waterColour.getSelectedItem() == "Default")
                waterPreview.setBackground(Color.BLUE); // Sets the colour for each colour-blindness
            else if (waterColour.getSelectedItem() == "Deuteranopia (1)")
                waterPreview.setBackground(new Color(0, 128, 128)); // Teal
            else if (waterColour.getSelectedItem() == "Deuteranopia (2)")
                waterPreview.setBackground(new Color(255, 0, 0)); // Red
            else if (waterColour.getSelectedItem() == "Protanopia (1)")
                waterPreview.setBackground(new Color(0, 128, 128)); // Teal
            else if (waterColour.getSelectedItem() == "Protanopia (2)")
                waterPreview.setBackground(new Color(255, 0, 0)); // Red
            else if (waterColour.getSelectedItem() == "Tritanopia (1)")
                waterPreview.setBackground(new Color(0, 128, 128)); // Teal
            else if (waterColour.getSelectedItem() == "Tritanopia (2)")
                waterPreview.setBackground(new Color(255, 255, 0)); // Yellow
            else if (waterColour.getSelectedItem() == "Monochromacy (1)")
                waterPreview.setBackground(new Color(0, 0, 50)); // Dark Blue/Black
            else if (waterColour.getSelectedItem() == "Monochromacy (2)")
                waterPreview.setBackground(new Color(150, 150, 150)); // Light Grey
            else if (waterColour.getSelectedItem() == "Trichromacy (1)")
                waterPreview.setBackground(new Color(0, 128, 128)); // Teal
            else if (waterColour.getSelectedItem() == "Trichromacy (2)")
                waterPreview.setBackground(new Color(255, 0, 0)); // Red
            terrainColours[1] = waterPreview.getBackground(); // Sets the water (second index) to be the colour of the panel
        }
        // If the user changed the mountain drop down
        else if (e.getSource() == mountainColour) {
            if (mountainColour.getSelectedItem() == "Default")
                mountainPreview.setBackground(Color.GRAY); // Sets the colour for each colour-blindness
            else if (mountainColour.getSelectedItem() == "Deuteranopia (1)")
                mountainPreview.setBackground(new Color(96, 96, 96)); // All of these are shades of grey (to avoid biome blending), the higher the number in each, the lighter the grey
            else if (mountainColour.getSelectedItem() == "Deuteranopia (2)")
                mountainPreview.setBackground(new Color(160, 160, 160));
            else if (mountainColour.getSelectedItem() == "Protanopia (1)")
                mountainPreview.setBackground(new Color(50, 50, 50));
            else if (mountainColour.getSelectedItem() == "Protanopia (2)")
                mountainPreview.setBackground(new Color(150, 150, 150));
            else if (mountainColour.getSelectedItem() == "Tritanopia (1)")
                mountainPreview.setBackground(new Color(60, 60, 60));
            else if (mountainColour.getSelectedItem() == "Tritanopia (2)")
                mountainPreview.setBackground(new Color(140, 140, 140));
            else if (mountainColour.getSelectedItem() == "Monochromacy (1)")
                mountainPreview.setBackground(new Color(70, 70, 70));
            else if (mountainColour.getSelectedItem() == "Monochromacy (2)")
                mountainPreview.setBackground(new Color(130, 130, 130));
            else if (mountainColour.getSelectedItem() == "Trichromacy (1)")
                mountainPreview.setBackground(new Color(80, 80, 80));
            else if (mountainColour.getSelectedItem() == "Trichromacy (2)")
                mountainPreview.setBackground(new Color(120, 120, 120));
            terrainColours[2] = mountainPreview.getBackground(); // Sets the mountain (third index) to be the colour of the panel
        }
        // If the user changed the beach drop down
        else if (e.getSource() == beachColour) {
            if (beachColour.getSelectedItem() == "Default")
                beachPreview.setBackground(Color.YELLOW); // Sets the colour for each colour-blindness
            else if (beachColour.getSelectedItem() == "Deuteranopia (1)")
                beachPreview.setBackground(new Color(128, 128, 0)); // yellow-brown
            else if (beachColour.getSelectedItem() == "Deuteranopia (2)")
                beachPreview.setBackground(new Color(0, 0, 255)); // light blue
            else if (beachColour.getSelectedItem() == "Protanopia (1)")
                beachPreview.setBackground(new Color(128, 100, 0)); // orange-brown
            else if (beachColour.getSelectedItem() == "Protanopia (2)")
                beachPreview.setBackground(new Color(0, 0, 200)); // dark blue
            else if (beachColour.getSelectedItem() == "Tritanopia (1)")
                beachPreview.setBackground(new Color(100, 100, 0)); // olive green
            else if (beachColour.getSelectedItem() == "Tritanopia (2)")
                beachPreview.setBackground(new Color(50, 50, 200)); // Blue-purple
            else if (beachColour.getSelectedItem() == "Monochromacy (1)")
                beachPreview.setBackground(new Color(90, 90, 90)); // dark grey
            else if (beachColour.getSelectedItem() == "Monochromacy (2)")
                beachPreview.setBackground(new Color(170, 170, 170)); // Light grey
            else if (beachColour.getSelectedItem() == "Trichromacy (1)")
                beachPreview.setBackground(new Color(100, 100, 50)); // Brown
            else if (beachColour.getSelectedItem() == "Trichromacy (2)")
                beachPreview.setBackground(new Color(100, 50, 255)); // Purple
            terrainColours[3] = beachPreview.getBackground(); // Sets the beach (fourth index) to be the colour of the panel
        }
        // If the user changed the forest drop down
        else if (e.getSource() == forestColour) {
            if (forestColour.getSelectedItem() == "Default")
                forestPreview.setBackground(new Color(13, 100, 10)); // Sets the colour for each colour-blindness
            else if (forestColour.getSelectedItem() == "Deuteranopia (1)")
                forestPreview.setBackground(new Color(106, 106, 106)); // Light grey
            else if (forestColour.getSelectedItem() == "Deuteranopia (2)")
                forestPreview.setBackground(new Color(55, 55, 55)); // Dark Grey
            else if (forestColour.getSelectedItem() == "Protanopia (1)")
                forestPreview.setBackground(new Color(106, 106, 106)); // Light grey
            else if (forestColour.getSelectedItem() == "Protanopia (2)")
                forestPreview.setBackground(new Color(55, 55, 55)); // Dark Grey
            else if (forestColour.getSelectedItem() == "Tritanopia (1)")
                forestPreview.setBackground(new Color(106, 106, 106)); // Light grey
            else if (forestColour.getSelectedItem() == "Tritanopia (2)")
                forestPreview.setBackground(new Color(55, 55, 55)); // Dark Grey
            else if (forestColour.getSelectedItem() == "Monochromacy (1)")
                forestPreview.setBackground(new Color(15, 15, 15)); // Lighter shade of black
            else if (forestColour.getSelectedItem() == "Monochromacy (2)")
                forestPreview.setBackground(new Color(205, 180, 255)); // Lilac
            else if (forestColour.getSelectedItem() == "Trichromacy (1)")
                forestPreview.setBackground(new Color(55, 55, 55)); // Dark Grey
            else if (forestColour.getSelectedItem() == "Trichromacy (2)")
                forestPreview.setBackground(new Color(150, 150, 150)); // Lighter Grey
            terrainColours[5] = forestPreview.getBackground(); // Sets the forest (fifth index) to be the colour of the panel
        }
        // If the user changed the deep water drop down
        else if (e.getSource() == deepWaterColour) {
            if (deepWaterColour.getSelectedItem() == "Default")
                deepWaterPreview.setBackground(new Color(6, 11, 112)); // Sets the colour for each colour-blindness
            else if (deepWaterColour.getSelectedItem() == "Deuteranopia (1)")
                deepWaterPreview.setBackground(new Color(8, 81, 68)); // Teal
            else if (deepWaterColour.getSelectedItem() == "Deuteranopia (2)")
                deepWaterPreview.setBackground(new Color(125, 14, 7)); // Red
            else if (deepWaterColour.getSelectedItem() == "Protanopia (1)")
                deepWaterPreview.setBackground(new Color(8, 81, 68)); // Teal
            else if (deepWaterColour.getSelectedItem() == "Protanopia (2)")
                deepWaterPreview.setBackground(new Color(125, 14, 7)); // Red
            else if (deepWaterColour.getSelectedItem() == "Tritanopia (1)")
                deepWaterPreview.setBackground(new Color(125, 14, 7)); // Red
            else if (deepWaterColour.getSelectedItem() == "Tritanopia (2)")
                deepWaterPreview.setBackground(new Color(255, 255, 0)); // Yellow
            else if (deepWaterColour.getSelectedItem() == "Monochromacy (1)")
                deepWaterPreview.setBackground(new Color(30, 30, 30)); // Very dark grey
            else if (deepWaterColour.getSelectedItem() == "Monochromacy (2)")
                deepWaterPreview.setBackground(new Color(180, 255, 200)); // Mint Green
            else if (deepWaterColour.getSelectedItem() == "Trichromacy (1)")
                deepWaterPreview.setBackground(new Color(8, 81, 68)); // Teal
            else if (deepWaterColour.getSelectedItem() == "Trichromacy (2)")
                deepWaterPreview.setBackground(new Color(125, 14, 7)); // Red
            terrainColours[6] = deepWaterPreview.getBackground(); // Sets the deep water (sixth index to be the colour of the panel
        }
        // SNOW is white so does not need to be changed

        // Otherwise if the user changed the window size drop down
        if (e.getSource() == windowSize) {
            // Link the selected size
            if (windowSize.getSelectedItem() == "1920 x 1080") {
                if (newWindowHeight != 1080) windowSizeChange = true; // Show that the size has been changed
                newWindowHeight = 1080; // Sets globally what the user wishes to change to
                newWindowWidth = 1920;
            } else if (windowSize.getSelectedItem() == "3840 x 2160") {
                if (newWindowHeight != 2160) windowSizeChange = true;
                newWindowHeight = 2160;
                newWindowWidth = 3840;
            } else if (windowSize.getSelectedItem() == "2560 x 1440") {
                if (newWindowHeight != 1440) windowSizeChange = true;
                newWindowHeight = 1440;
                newWindowWidth = 2560;
            } else if (windowSize.getSelectedItem() == "1366 x 768") {
                if (newWindowHeight != 768) windowSizeChange = true;
                newWindowHeight = 768;
                newWindowWidth = 1366;
            } else if (windowSize.getSelectedItem() == "1440 x 900") {
                if (newWindowHeight != 900) windowSizeChange = true;
                newWindowHeight = 900;
                newWindowWidth = 1440;
            } else if (windowSize.getSelectedItem() == "1280 x 720") {
                if (newWindowHeight != 720) windowSizeChange = true;
                newWindowHeight = 720;
                newWindowWidth = 1280;
            } else if (windowSize.getSelectedItem() == "1280 x 1024") {
                if (newWindowHeight != 1024) windowSizeChange = true;
                newWindowHeight = 1024;
                newWindowWidth = 1280;
            }
        }
    }

    // This checks for any mouse clicks by the user
    // On components which have a mouse listener, such as the save and cancel buttons
    @Override
    public void mouseClicked(MouseEvent e) {
        // Checks which button was pressed
        if (e.getSource() == saveButton) { // If it was the save button
            ObjectMapper objectMapper = new ObjectMapper(); // Initialising the object mapper

            if (newWindowHeight == 0) { // If the window height was not changed, or is 0
                // Ensure the window height is not saved as 0
                newWindowHeight = 1080;
                windowSizeChange = true;
            }
            if (newWindowWidth == 0) { // If the window width was not changed, or is 0
                // Ensure the window width is not saved as 0
                newWindowWidth = 1920;
                windowSizeChange = true;
            }

            // Checking that the window size can fit
            if (newWindowHeight <= maxWindowHeight && newWindowWidth <= maxWindowWidth && windowSizeChange) {
                // Attempt to save to the JSON
                try {
                    Options newData; // The options object which will be saved
                    File sourceFile = new File("options.json"); // The file to save to

                    if (sourceFile.exists()) { // If it is present in the source folder then set the object
                        newData = objectMapper.readValue(sourceFile, Options.class);
                    } else {
                        // If the file doesn't exist in the source folder, read it from the JAR resources
                        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("options.json");
                        if (inputStream != null) {
                            newData = objectMapper.readValue(inputStream, Options.class);
                            inputStream.close();
                        } else {
                            throw new RuntimeException(); // Catch and print any errors
                        }
                    }

                    newData.setWindowHeight(newWindowHeight); // Changing the values, first part is the key to change, second is what to change the value to
                    newData.setWindowWidth(newWindowWidth);

                    // Updating what is selected by the user with what will be stored
                    int colourCount = 0;
                    List<Colours> newColours = new ArrayList<>(); // The list of new colours to set
                    // For each colour in terrainColours
                    // Terrain colours was updated to the same as what was displayed in the coloured JPanel
                    for (Color color : terrainColours) {
                        Colours newColour = new Colours(); // The new colour
                        newColour.setRed(color.getRed()); // Adding the red, green and blue values for each colours into individual objects
                        newColour.setGreen(color.getGreen());
                        newColour.setBlue(color.getBlue());

                        if (colourCount == 0) newColour.setSetting((String) grassColour.getSelectedItem());
                        else if (colourCount == 1) newColour.setSetting((String) waterColour.getSelectedItem());
                        else if (colourCount == 2) newColour.setSetting((String) mountainColour.getSelectedItem());
                        else if (colourCount == 3) newColour.setSetting((String) beachColour.getSelectedItem());
                        else if (colourCount == 4) newColour.setSetting((String) forestColour.getSelectedItem());
                        else if (colourCount == 5) newColour.setSetting((String) deepWaterColour.getSelectedItem());

                        newColours.add(newColour); // Adding it to the object array
                        colourCount++; // Which colour is being set
                    }

                    newData.setColours(newColours); // Writing to the new JSON

                    String newJSON = objectMapper.writeValueAsString(newData); // Changing to a string, so it can be written to the file
                    FileWriter fileWriter = new FileWriter("options.json");
                    fileWriter.write(newJSON); // Writing to the file
                    fileWriter.close(); // No longer needed
                } catch (IOException ex) {
                    throw new RuntimeException(ex); // Log any errors
                }

                new OpeningMenu(); // Return to the opening menu
                optionsFrame.dispose(); // Dispose of the current frame

            } else {
                // If the window size is larger than the screen, then the appropriate error message is displayed
                new ErrorWindow("<html><p style=\"text-align:center;\">That size is not available for your monitor.<br>Please select a smaller size<br>Monitor Size: " + maxWindowWidth + " x " + maxWindowHeight + "</p></html>");
            }
        }

        // If the user does not wish to save any changes
        if (e.getSource() == backButton) {
            new OpeningMenu(); // Return to the opening menu
            optionsFrame.dispose(); // Ignores any inputs, and closes the menu
        }
    }

    // Not used but still needed to be present in the program
    @Override
    public void mousePressed(MouseEvent e) {} // Not Used

    @Override
    public void mouseReleased(MouseEvent e) {} // Not Used

    @Override
    public void mouseEntered(MouseEvent e) {} // Not Used

    @Override
    public void mouseExited(MouseEvent e) {} // Not Used
}