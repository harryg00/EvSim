import com.fasterxml.jackson.databind.ObjectMapper;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.ZoneId;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// This allows an old save to be loaded with the appropriate settings being carried over
// Code for linking the load menu as well as the load function itself (see mouseClicked)
public class LoadSimulation implements MouseListener {
    private JButton loadSaveButton;
    private JButton cancelButton;
    private JPanel panel1; // The panel containing the form itself
    private JList<String> list1;
    private JButton deleteButton;
    private JButton renameButton;
    public static JFrame frame; // The window for the load menu

    // The constructor of LoadSimulation which displays the window itself
    // which contains the list of save files
    public LoadSimulation() {
        // This method returns a list model which holds all the names of the saves, as a component which can be displayed on the JList
        String path = "./Save Games"; // Path to the parent folder

        DefaultListModel<String> listModel = new DefaultListModel<>(); // List model storing the save names and details

        File folder = new File(path); // Parent folder
        File[] folders = folder.listFiles(File::isDirectory); // Array of all child folders

        // Loops through folders and adds to list
        if (folders != null) {
            for (File subFolder : folders) {
                // Create a date and time for the last modified date
                LocalDateTime lastModifiedDateTime = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(subFolder.lastModified()), // Time of last modification
                        ZoneId.systemDefault() // Time zone
                );
                // Bullet points and formats date last modified
                listModel.addElement("<html><ul><li>" + subFolder.getName() + " Last Used: " + lastModifiedDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "</li></ul></html>");
            }
        }

        list1.setModel(listModel); // Add the filled model to the JList

        renameButton.setEnabled(false); // Setting to be disabled by default, and adding mouse listeners
        deleteButton.setEnabled(false);
        loadSaveButton.setEnabled(false);
        renameButton.addMouseListener(this); // MouseListeners to check if the user clicks on the button
        deleteButton.addMouseListener(this);
        loadSaveButton.addMouseListener(this);
        cancelButton.addMouseListener(this);
        list1.addMouseListener(this);

        frame = new JFrame("Load Simulation"); // The frame itself
        frame.setPreferredSize(new Dimension(500, 540)); // Ideal size
        frame.add(panel1); // panel1 contains all other components
        frame.setResizable(false); // So no size issues
        frame.pack(); // So all is correct size
        frame.setLocationRelativeTo(null); // Central
        frame.setVisible(true);
    }

    // Checks for the user clicking on components which have a mouseListener
    // In this case the JList and the JButtons
    @Override
    public void mouseClicked(MouseEvent e) {

        if(e.getSource() == list1){ // If the user clicks on an object in the list, enable the buttons
            renameButton.setEnabled(true); // Allow the other buttons to become functional
            deleteButton.setEnabled(true);
            loadSaveButton.setEnabled(true);
        }

        // Load the simulation under the selected file name
        if(e.getSource() == loadSaveButton && loadSaveButton.isEnabled()) {
            String currentlySelectedFile = currentSelectedFile(); // The name of the currently selected file
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                // Replace organisms.json with the stored json file
                Files.copy(Paths.get("./Save Games/" + currentlySelectedFile + "/organisms.json"), Paths.get("./organisms.json"), StandardCopyOption.REPLACE_EXISTING);
                // Load the stored settings
                Settings newLoad = objectMapper.readValue(new File("./Save Games/" + currentlySelectedFile + "/" + currentlySelectedFile + ".json"), Settings.class); // Creating an array of organisms from organisms.json
                // start a new game. Sends loaded save as true to indicate what to load. Sends the newLoad to be loaded
                if(GameWindow.frame != null) {
                    GameWindow.frame.dispose();
                    GameWindow.thread.interrupt();
                }
                GameWindow newGameWindow = new GameWindow(newLoad.getWindowWidth(), newLoad.getWindowHeight(), true, newLoad);
                newGameWindow.startGameThread(); // Start the thread
                frame.dispose(); // Dispose of the load menu
                OpeningMenu.frame.dispose(); // Dispose of the opening menu
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        // Deletes a save (folder and JSON files)
        if(e.getSource() == deleteButton && deleteButton.isEnabled()) {
            String currentlySelectedFile = currentSelectedFile(); // The name of the currently selected file

            // Removes selected save from the list on the screen
            DefaultListModel<String> listModel = (DefaultListModel<String>) list1.getModel();
            listModel.remove(list1.getSelectedIndex());

            // Deletes the folder itself
            try {
                // Deletes the folder and its contents recursively
                deleteFolder(Paths.get("./Save Games/" + currentlySelectedFile));
                frame.dispose(); // Refresh the load save page
                new LoadSimulation();
            } catch (IOException error) {
                throw new RuntimeException(error);
            }
        }

        // Renames a save file
        if(e.getSource() == renameButton && renameButton.isEnabled()) {
            String currentlySelectedFile = currentSelectedFile(); // The name of the currently selected file

            // Gets the name that the user wishes to rename to, and renames in the class renameFile
            new RenameFile(currentlySelectedFile);
        }

        // Close the frame
        if(e.getSource() == cancelButton) {
            frame.dispose();
            GameWindow.gamePaused = false;
        }
    }

    // The folder cannot be deleted if it has contents in it, therefore this method is needed to
    // delete the contents and the folder itself
    private static void deleteFolder(Path folderPath) throws IOException {
        // 'Walk' the file tree to delete each file starting from the given folder path
        Files.walk(folderPath).sorted((p1, p2) -> -p1.compareTo(p2)).forEach(path -> {
                    try {
                        Files.delete(path); // Deletes the sub files/folders, in this case only the sub-files
                    } catch (IOException e) {
                        throw new RuntimeException(e); // Log any errors
                    }
                });
    }

    // Finds the name of the currently selected file from the JList
    private String currentSelectedFile(){
        String currentlySelectedFile = "";
        String htmlString = list1.getSelectedValue(); // The selected value (which returns an HTML string)

        // Uses a pattern and a matcher to extract the name of the file from the HTML string
        Pattern pattern = Pattern.compile("<li>(.*?) Last Used:"); // Finds the file name amongst the last name and HTML data
        Matcher matcher = pattern.matcher(htmlString);

        if (matcher.find()) currentlySelectedFile = matcher.group(1); // If the pattern is found then the file name can be updated

        return currentlySelectedFile; // Return the name of the file
    }

    // Not used but still needed within the program
    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}
