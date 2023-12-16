import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

// This class is the layout for the start game menu which allows the user to select their options to set
// before starting the simulation
// The user can either start a simulation, return to the main menu, or preview the map from this menu
public class StartGameMenu implements MouseListener {
    private JPanel panel1; // The panel containing the components below
    private JTextField seedTextField;
    private JTextField organismTextField;
    private JCheckBox allowSeasonsCheckBox; // Not used yet
    private JCheckBox allowPredatorPreyOrganismsCheckBox; // Not used yet
    private JButton startGameButton;
    private JButton backToHomeButton;
    private JTextField a1920TextField;
    private JTextField a1080TextField;
    private JButton previewButton;
    private final JFrame frame; // The window itself
    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // The users monitor size
    public StartGameMenu() {
        ObjectMapper objectMapper = new ObjectMapper(); // Initialising the JSON Object Mapper

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

            // Gets the default window size from the JSON and sets it as the placeholder text for the window size
            // of the start menu
            if(newData.getWindowWidth() >= (int) screenSize.getWidth()) a1920TextField.setText(String.valueOf((int) screenSize.getWidth()));
            else if(newData.getWindowWidth() <= (int) screenSize.getWidth()) a1920TextField.setText(String.valueOf(newData.getWindowWidth()));
            if(newData.getWindowHeight() >= (int) screenSize.getHeight()) a1080TextField.setText(String.valueOf((int) screenSize.getHeight()));
            else if(newData.getWindowHeight() <= (int) screenSize.getHeight()) a1080TextField.setText(String.valueOf(newData.getWindowHeight()));
        } catch (IOException e) {
            throw new RuntimeException(e); // Log any errors
        }

        // Generate a new random seed
        seedTextField.setText(Double.toString(new Random().nextGaussian() * 255));
        PerlinNoise.setSeed(Double.parseDouble(seedTextField.getText())); // Set the seed in Perlin noise

        panel1.setPreferredSize(new Dimension(500,500)); // Set the preferred size of the panel

        startGameButton.addMouseListener(this); // Add required mouse listeners to buttons
        backToHomeButton.addMouseListener(this);
        previewButton.addMouseListener(this);

        frame = new JFrame("Start Game"); // Set title of the window
        frame.setPreferredSize(new Dimension(500,500)); // Set size of the window
        frame.add(panel1); // As the panel already contains the other components, only the panel has to be added to the frame.
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // This checks for any mouse clicks by the user
    // On components which have a mouse listener, such as the start game and return to home buttons
    @Override
    public void mouseClicked(MouseEvent e) {
        // When they press on 'Start Game', the information should be validated.
        if(e.getSource() == startGameButton){
            String textInput = seedTextField.getText().trim(); // Gets the data from seedTextField, and removes any blank space(.trim())
            String text2Input = organismTextField.getText().trim(); // Takes the number of organisms and removes whitespace (.trim())
            String windowWidth = a1920TextField.getText().trim();
            String windowHeight = a1080TextField.getText().trim();

            boolean invalidInput = false; // Tracks any invalidation so the game doesn't start with invalid inputs

            try {
                // Map seed validation
                double seedInput = Double.parseDouble(textInput); // Sends the trimmed data from seedTextField into a double
                if (seedInput >= -1000 && seedInput <= 1000) { // Bounds
                    PerlinNoise.setSeed(seedInput); // Sets the seed in perlin noise
                } else {
                    // If it is not in the bounds, then it displays the ErrorWindow, with html styled text
                    new ErrorWindow("<html><p style=\"text-align:center;\">Please provide a number that is within the range of -1000.0 and 1000.0</p></html>");
                    invalidInput = true;
                }

                // Organism count validation
                int organismInput = Integer.parseInt(text2Input); // Passes it into an integer
                if (organismInput <= 300 && organismInput >= 1) { // Bounds
                    GenerateOrganisms.organismCount = organismInput; // Sets the organism count to be generated
                } else {
                    // Display an error message
                    new ErrorWindow("<html><p style=\"text-align:center;\">Please provide a number that is within the range of 2 and 50</p></html>");
                    invalidInput = true;
                }

                // Window size validation
                int windowHeightInput = Integer.parseInt(windowHeight); // Passing into Integer
                int windowWidthInput = Integer.parseInt(windowWidth);
                if(windowHeightInput <= screenSize.getHeight() && windowHeightInput >= 500) { // Bounds
                    windowHeight = Integer.toString(windowHeightInput); // Sets the window to a validated Integer
                } else {
                    // Display an error message
                    new ErrorWindow("<html><p style=\"text-align:center;\">Please provide a window height between 500 and " + screenSize.getHeight() + "</p></html>");
                    invalidInput = true;
                }
                if(windowWidthInput <= screenSize.getWidth() && windowWidthInput >= 500) { // Bounds
                    windowWidth = Integer.toString(windowWidthInput); // Sets the window to a validated Integer
                } else {
                    // display an error message
                    new ErrorWindow("<html><p style=\"text-align:center;\">Please provide a window width between 500 and " + screenSize.getWidth() + "</p></html>");
                    invalidInput = true;
                }

            } catch (NumberFormatException error) { // Catch any passing errors
                // If there are any other errors, it gives the user the error message in html format
                new ErrorWindow("<html><p style=\"text-align:center;\">Please provide valid inputs.<br>One of your inputs contains an unexpected character</p></html>");
                invalidInput = true;
            }

            // If there have been no invalid inputs, then load the game with the settings
            if(!invalidInput){
                GameWindow gameWindow = new GameWindow(Integer.parseInt(windowWidth), Integer.parseInt(windowHeight), false, null);
                gameWindow.startGameThread(); // Creates a new game window from the game window class, width and height as the input from the user
                frame.dispose(); // Removes the start game menu
                // Updates the users chosen options
                Organisms.predatorPreyEnabled = allowPredatorPreyOrganismsCheckBox.isSelected();
                Organisms.seasonsEnabled = allowSeasonsCheckBox.isSelected();
            }
        }

        // If the user desires to go back to home, display the start menu
        if(e.getSource() == backToHomeButton){
            new OpeningMenu(); // New opening menu
            frame.dispose(); // Remove the start menu
        }

        // If they want to preview the map
        if(e.getSource() == previewButton){
            double seedInput = Double.parseDouble(seedTextField.getText()); // Parses the data from seedTextField into a double
            // Validates the input and gets the desired window size
            if (seedInput >= -1000 && seedInput <= 1000) { // Bounds
                int width = Integer.parseInt(a1920TextField.getText()), height = Integer.parseInt(a1080TextField.getText());
                PreviewMap(width, height); // Calls the preview map method to display what the map will look like
            } else {
                // If it is not in the bounds, then it displays the ErrorWindow, with html styled text
                new ErrorWindow("<html><p style=\"text-align:center;\">Please provide a seed that is within the range of -1000.0 and 1000.0</p></html>");
            }
        }
    }

    // The PreviewMap function allows the user to see the map before the simulation starts
    // Paints only the map in the window
    public void PreviewMap(int width, int height) {
        JFrame frame = new JFrame("Map Preview"); // New window
        // Offset of -250 to counter for what would usually be the side menu
        frame.setPreferredSize(new Dimension(width - 250, height)); // Set the size to be equal to the map size
        frame.setLocation((int) (OpeningMenu.screenSize.getWidth() / 2) - width / 2, 0); // Middle of the screen
        frame.setResizable(false);

        PerlinNoise.setSeed(Double.parseDouble(seedTextField.getText())); // Send to perlin noise
        GenerateMap mapPanel = new GenerateMap(width + 250, height, false, null);
        frame.add(mapPanel); // Generate the map and add it to the window

        frame.pack();
        frame.setVisible(true);
    }

    // Not used, but required by the program
    @Override
    public void mousePressed(MouseEvent e) {} // Not needed

    @Override
    public void mouseReleased(MouseEvent e) {} // Not needed

    @Override
    public void mouseEntered(MouseEvent e) {} /// Not needed

    @Override
    public void mouseExited(MouseEvent e) {} // Not needed
}
