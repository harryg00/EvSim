import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OpeningMenu extends JPanel implements Runnable {
    JFrame frame;
    Thread thread;
    private JPanel menuPanel;

    public OpeningMenu(int Width, int Height) {
        this.setSize(Width, Height);
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        frame = new JFrame("EvSim");
        frame.setSize(Width, Height);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        menuPanel = createVerticalMenu();
        frame.add(this, BorderLayout.CENTER);
        frame.add(menuPanel, BorderLayout.WEST); // Add the menu panel to the left side

        frame.setVisible(true);
    }

    public void startMenuThread(){
        thread = new Thread(this);
        thread.start();
    }

    private JPanel createVerticalMenu() {
        JPanel menu = new JPanel();
        menu.setBackground(Color.darkGray);
        menu.setPreferredSize(new Dimension(100, getHeight())); // Sets the width to 100px and the height to the same as the page

        JButton startButton = new JButton("Start"); // Creates a new JButton
        JButton loadButton = new JButton("Load");
        JButton optionButton = new JButton("Options");
        JButton exitButton = new JButton("Exit");

        setButtonProperties(startButton, "Starts a new iteration"); // Sets the styling to the button
        setButtonProperties(loadButton, "Loads into a previous save");
        setButtonProperties(optionButton, "Set options for EvSim");
        setButtonProperties(exitButton, "Exit the program");
        exitButton.addActionListener(new ActionListener() { // Creates and adds an action listener from the ActionListener class
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.exit(0);
            } // Exits the currently running Java process
        });
        startButton.addActionListener(new ActionListener() { // Creates and adds an action listener from the ActionListener class
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                GameWindow gameWindow = new GameWindow(getWidth(), getHeight()); // Creates a new game window from the game window class, same width and height as the current menu
                gameWindow.startGameThread();
                frame.dispose(); // Removes the current window that is displaying
            }
        });
        menu.add(startButton);
        menu.add(loadButton);
        menu.add(optionButton);
        menu.add(exitButton);
        return menu;
    }
    private static void setButtonProperties(JButton button, String tooltip) { // Generic menu button style
        button.setForeground(Color.black); // Sets the text colour to be black
        button.setBackground(Color.darkGray); // Sets the background button colour to be dark gray (same as the menu)
        button.setToolTipText(tooltip); // Sets the help text that displays on mouse hover
        button.setPreferredSize(new Dimension(100, 50)); // Sets the button size
        button.setBorder(BorderFactory.createLineBorder(Color.darkGray, 1)); // Button border to be same as background
        button.setFont(new Font("Arial", Font.BOLD, 16)); // The font and size of the text
    }
    @Override
    public void run() {

    }
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
