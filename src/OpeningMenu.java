import com.fasterxml.jackson.databind.ObjectMapper;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

// This is the opening menu which is displayed to the user once they start the game
public class OpeningMenu extends JPanel {
    public static JFrame frame; // The window frame itself
    public static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // Gets the users screen size
    public static int windowWidth = (int) screenSize.getWidth(); // Default value
    public static int windowHeight = (int) screenSize.getHeight(); // Default value

    // The constructor for OpeningMenu which creates the side menu and buttons
    public OpeningMenu() {
        ObjectMapper objectMapper = new ObjectMapper(); // Initialising the JSON Object Mapper
        Options options;

        // Attempts to read options.json
        try {
            File sourceFile = new File("options.json");
            if (sourceFile.exists()) { // If the file is within the source folder then read from there
                options = objectMapper.readValue(sourceFile, Options.class);
            } else {
                // If the file doesn't exist in the source folder, read it from the JAR resources
                InputStream inputStream = getClass().getClassLoader().getResourceAsStream("options.json");
                if (inputStream != null) { // If the file has been read successfully
                    options = objectMapper.readValue(inputStream, Options.class); // Pass the JSON
                    inputStream.close();
                } else {
                    throw new RuntimeException(); // Otherwise print an error
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e); // Print error
        }

        windowWidth = options.getWindowWidth(); // Gets the value stored at the key, and returns as Integer
        windowHeight = options.getWindowHeight();

        this.setSize(windowWidth, windowHeight); // Sets the JPanel size to what has changed
        this.setBackground(Color.black); // Sets the JPanel background colour to be black

        frame = new JFrame("EvSim"); // Sets the title to be EvSim
        frame.setSize(windowWidth, windowHeight); // Sets the window size
        frame.setLocationRelativeTo(null); // Window location relative to nothing (top left / full window)
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Exit the application if the window is closed
        frame.setVisible(true); // Set it to be visible

        JPanel menuPanel = createVerticalMenu(); // Creates a vertical side menu which contains the buttons
        frame.add(this, BorderLayout.CENTER); // Adds the JPanel to the centre of the frame
        frame.add(menuPanel, BorderLayout.WEST); // Add the menu panel to the left side

        frame.setVisible(true);
    }

    // Creates the vertical menu containing the buttons for start, load, options and exit
    private JPanel createVerticalMenu() {
        JPanel menu = new JPanel(); // New JPanel to hold the buttons
        menu.setBackground(Color.darkGray); // Sets the background to be dark grey
        menu.setPreferredSize(new Dimension(100, getHeight())); // Sets the width to 100px and the height to the same as the page

        JButton startButton = new JButton("Start"); // Creates the JButtons
        JButton loadButton = new JButton("Load");
        JButton optionButton = new JButton("Options");
        JButton exitButton = new JButton("Exit");

        setButtonProperties(startButton, "Starts a new iteration"); // Sets the styling to the buttons
        setButtonProperties(loadButton, "Loads into a previous save");
        setButtonProperties(optionButton, "Set options for EvSim");
        setButtonProperties(exitButton, "Exit the program");

        // Creates and adds an action listener from the ActionListener class

        // Exits the currently running Java process
        exitButton.addActionListener(actionEvent -> System.exit(0));

        // Starts a new simulation through the start game menu
        startButton.addActionListener(actionEvent -> {
            new StartGameMenu(); // Opens a new start menu
            frame.dispose(); // Closes the current frame
        });

        // Opens the options menu
        optionButton.addActionListener(e -> {
            new MainMenuOptions(); // Creates a new options menu and displays
            frame.dispose(); // Removes the current frame
        });

        loadButton.addActionListener(e -> new LoadSimulation()); // Creates and opens the load menu

        menu.add(startButton); // Add all buttons to the JPanel
        menu.add(loadButton);
        menu.add(optionButton);
        menu.add(exitButton);

        return menu; // Return the JPanel to be added to the frame
    }

    // This styles the buttons to be the same appearance
    private static void setButtonProperties(JButton button, String tooltip) { // Generic menu button style
        button.setForeground(Color.black); // Sets the text colour to be black
        button.setBackground(Color.darkGray); // Sets the background button colour to be dark gray (same as the menu)
        button.setToolTipText(tooltip); // Sets the help text that displays on mouse hover
        button.setPreferredSize(new Dimension(100, 50)); // Sets the button size
        button.setBorder(BorderFactory.createLineBorder(Color.darkGray, 1)); // Button border to be same as background
        button.setFont(new Font("Arial", Font.BOLD, 16)); // The font and size of the text
    }

    // Paints the EvSim logo at the centre (no matter the size of the window)
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension gameWindowSize = getSize(); // Gets the current window size as a Dimension
        FontMetrics fontMetrics = g.getFontMetrics(); // Class to get the characteristics of a string of text

        g.setColor(Color.white); // Sets the colour to white
        g.setFont(new Font("Agency FB", Font.BOLD, 108));

        int textWidth = fontMetrics.stringWidth("EVSIM"); // Gets the width of the title "EVSIM"
        int textHeight = fontMetrics.getHeight(); // Returns the height of the font (which would be 108 in this case).

        // variable for the x and y position of where the title should be.
        int titleTextX = (gameWindowSize.width - textWidth) / 2 - 100; // -100 to account for the size of the menu on the left
        int titleTextY = (gameWindowSize.height - textHeight) / 2;
        // Takes the size of the game window, e.g. 1920, takes away the size of the text, e.g. 35,
        // and divides by 2 to get the exact centre on any window size.

        g.drawString("EVSIM", titleTextX, titleTextY); // Draws the string to the page at position x and y

        g.setFont(new Font("Agency FB", Font.BOLD, 36));
        g.drawString("Press 'Start' to begin", titleTextX - 15 /*Left of title*/, titleTextY + 50 /*Below the title*/);
        // Draws the subtitle on the page just below and to the left of the title.
    }
}
