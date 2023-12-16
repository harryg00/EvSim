import javax.swing.*;
import java.awt.*;

// This class is used to display information on demand, such as an error or a success message.
// Is used to display any message, very useful for giving the user information
public class ErrorWindow {
    public static String windowName = "Error"; // Displays as an error by default, but can be changed to success or anything else
    public ErrorWindow(String errorMessage){ // Takes the desired error message as a string
        JFrame frame = new JFrame(windowName);
        frame.setPreferredSize(new Dimension(250,150));
        JLabel errorLabel = new JLabel(); // Creates a new JLabel which displays the string
        errorLabel.setText(errorMessage); // Adds the error message to the created JLabel
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center-align the text
        frame.add(errorLabel); // Adds this to the centre of the frame
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
