import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

// This allows a save file to be renamed to the users choice of new name
// Once the button is pressed on the LoadSimulation menu, this frame
// opens and the user is asked what name they would like to change to.
// This class handles that operation.
public class RenameFile implements MouseListener {
    private JPanel panel1; // The panel containing the components below
    private JTextField renameNameTextField;
    private JButton cancelButton;
    private JButton renameFileButton;
    private final JFrame frame; // The window which contains the menu
    private final String currentlySelectedFile; // The file which is selected by the user

    // The constructor for RenameFile, fetches the name of the selected file, and opens the window
    // which is set as a placeholder for the text field renameNameTextField
    public RenameFile(String originalFileName) {
        currentlySelectedFile = originalFileName; // Stores the old string to be used to find the file
        renameNameTextField.setText(originalFileName); // Sets the placeholder as the current file name

        frame = new JFrame("Rename File"); // Window title
        frame.setPreferredSize(new Dimension(300,175)); // Size of the window
        frame.add(panel1); // As the panel already contains the text, labels and buttons, only the panel has to be added to the frame.
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        cancelButton.addMouseListener(this); // Add required mouse listeners
        renameFileButton.addMouseListener(this);
    }

    // This detects the user clicking the mouse on components with a mouse listnener, in this case
    // it is listening for the buttons being clicked.
    @Override
    public void mouseClicked(MouseEvent e) {
        // If they choose to rename the file, find the old files and rename appropriately
        if(e.getSource() == renameFileButton){
            String newFileName = renameNameTextField.getText(); // The new name

            if(newFileName.isEmpty()){ // Ensure the user has not entered an empty string
                new ErrorWindow("Please enter a name! It cannot be blank"); // Display an appropriate message if so
            } else {
                try { // Renames the file and folder
                    // First path is the json file containing seed and years, second is the same file but what to name it as
                    Files.move(Paths.get("./Save Games/" + currentlySelectedFile + "/" + currentlySelectedFile + ".json"), Paths.get("./Save Games/" + currentlySelectedFile + "/" + newFileName + ".json"));
                    // First path is the folder, second is the new name for the folder
                    Files.move(Paths.get("./Save Games/" + currentlySelectedFile), Paths.get("./Save Games/" + newFileName));

                    LoadSimulation.frame.dispose(); // remove the old load page
                    frame.dispose(); // Remove the rename page
                    new LoadSimulation(); // Bring back the load page with the updated names
                } catch (IOException ex) {
                    throw new RuntimeException(ex); // Print any error
                }
            }
        }
        // If they choose to cancel, just remove the window and do no other updates
        if(e.getSource() == cancelButton){
            frame.dispose(); // Remove the window
        }
    }

    // Not used, but needed by the program
    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}
