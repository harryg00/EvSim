import org.json.simple.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.nio.file.Path;

// This class allows the user to save the simulation at its current state.
// When it is called, it allows the user to select a name to save as,
// before saving it physically
public class SaveSimulation implements MouseListener {
    private final JFrame frame; // The window containing the save options
    private JPanel panel1; // The panel containing other components listed below
    private JTextField saveNameTextField;
    private JButton cancelButton;
    private JButton saveSimulationButton;

    // The constructor for SaveSimulation
    // Opens the window, and places placeholder text holding the default name for a new file (current date and time)
    public SaveSimulation(){
        LocalDateTime currentDateTime = LocalDateTime.now(); // To find the current date and time (default file name)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"); // Formatting the current date and time)
        String defaultSaveName = currentDateTime.format(formatter); // For example 20231216_1311
        saveNameTextField.setText(defaultSaveName); // Sets it to be the placeholder

        frame = new JFrame("Save Simulation"); // The window name
        frame.setPreferredSize(new Dimension(300,175)); // The window size
        frame.add(panel1); // As the panel already contains the text, labels and buttons, only the panel has to be added to the frame.
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        cancelButton.addMouseListener(this); // Adding the required mouse listeners to listen for a button press
        saveSimulationButton.addMouseListener(this);
    }

    // This class listens for components that have been clicked (which have a listener)
    @Override
    @SuppressWarnings("unchecked") // As this warning can be ignored
    public void mouseClicked(MouseEvent e) {
        // If the user chooses to save the simulation
        if(e.getSource() == saveSimulationButton) {
            String saveFileName = saveNameTextField.getText(); // The name of the folder and JSON file to save as

            // File to be copied
            Path sourcePath = Paths.get("./organisms.json");

            // Directory path for Save Games
            Path saveGamesDirectory = Paths.get("./Save Games");

            // The path of the directory to create - ./Save Games/saveFileName
            Path destinationDirectory = saveGamesDirectory.resolve(saveFileName);

            // The path of the file to create - ./Save Games/saveFileName/organisms.json
            Path finalDestination = destinationDirectory.resolve("organisms.json");

            // The second JSON file content containing the year, seed etc
            JSONObject jsonObject = new JSONObject(); // The JSON which will be saved
            jsonObject.put("seed", PerlinNoise.getSeed()); // The current seed
            jsonObject.put("currentYear", GameWindow.years); // The current year
            jsonObject.put("windowHeight", GameWindow.HEIGHT); // The window height and width
            jsonObject.put("windowWidth", GameWindow.WIDTH);
            jsonObject.put("predatorPreyEnabled", Organisms.predatorPreyEnabled); // Whether predator prey is enabled
            jsonObject.put("seasonsEnabled", Organisms.seasonsEnabled);
            jsonObject.put("currentSeason", Organisms.season);
            try {
                if(!Files.exists(destinationDirectory) || !Files.exists(finalDestination)) { // Ensures no error when 2 saves have the same name
                    // Create the directory
                    Files.createDirectories(destinationDirectory);

                    // Copies the ./organisms.json into ./Save Games/saveFileName/organisms.json
                    Files.copy(sourcePath, finalDestination);

                    // Write the second JSON file
                    FileWriter fileWriter = new FileWriter("./Save Games/" + saveFileName + "/" + saveFileName + ".json");
                    fileWriter.write(jsonObject.toJSONString());
                    fileWriter.flush();

                    frame.dispose(); // Removing the frame
                    GameWindow.gamePaused = false;
                    ErrorWindow.windowName = "Success";
                    new ErrorWindow("<html><p style=\"text-align:center;\">Simulation saved as:<br>" + saveFileName + "</p></html>"); // Displaying a success message
                } else {
                    new ErrorWindow("That file name is already in use"); // If the file already exists, display to the user
                }
            } catch (IOException error) {
                throw new RuntimeException(error); // Log any errors
            }
        }

        if(e.getSource() == cancelButton){ // Else if they choose to cancel resume the game and close the window
            GameWindow.gamePaused = false; // Resume the game
            frame.dispose(); // Dispose of the current window
        }
    }

    // Not used, but required by the program
    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}
