import com.fasterxml.jackson.databind.ObjectMapper;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

// This is where the simulation itself runs within.
// Paints the map and organisms, calls the organisms to update, and updates the side menu
public class GameWindow extends JPanel implements Runnable, MouseListener {
    public static JFrame frame; // The window it is contained in
    public static Thread thread; // To have other processes running such as the updating of organisms in a run() method
    public static int years = 0; // The current year of the program
    public static int timeFactor = 2500; // The time in ms to execute each year
    public static int WIDTH, HEIGHT; // The size of the window
    private final GenerateMap mapPanel; // The map
    public static boolean gamePaused = false; // Whether the simulation is paused
    private final SideMenu sm; // The side menu
    public static int organismSelected = -1; // The currently selected organism, -1 by default meaning unselected
    public static Organism[] organisms; // The array of organisms currently being operated
    private final ObjectMapper objectMapper = new ObjectMapper(); // Jackson's object mapper to read JSON
    private static final JTextArea consoleTextArea = new JTextArea(); // The console text
    private static final JScrollPane consoleScrollPane = new JScrollPane(consoleTextArea); // Surround the text with a scroll pane
    public static JPanel consolePanel = new JPanel(new BorderLayout()); // The console panel itself
    public static boolean showConsole; // Whether to show the console panel or not
    // Constructor for GameWindow, takes in window size, whether it is to load a save or not and the save file itself if applicable
    public GameWindow(int Width, int Height, boolean LoadedSave, Settings newLoad) {
        WIDTH = Width; // Updating the global window size
        HEIGHT = Height;
        frame = new JFrame("EvSim"); // The title of the window
        frame.setPreferredSize(new Dimension(WIDTH, HEIGHT)); // The size to set
        frame.setLocation((int) (OpeningMenu.screenSize.getWidth() / 2) - Width / 2, 0); // Places it in the centre top of the screen
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Exit the program if this window is closed
        frame.setResizable(false); // So there are no size/map issues the window size is fixed

        if(!LoadedSave) { // If it is not a loaded save, start a new one
            years = 0;
            mapPanel = new GenerateMap(Width, Height, false, null);
            frame.add(mapPanel); // Create and add the map to the window

            GenerateOrganisms organismGeneration = new GenerateOrganisms(Width, Height);
            frame.add(organismGeneration); // Create and add the organisms to the window
        } else { // If it is a loaded save
            years = newLoad.getCurrentYear(); // Set the years to be the same as stored
            PerlinNoise.setSeed(newLoad.getSeed()); // Set the map seed to paint as the one stored
            Organisms.season = newLoad.getCurrentSeason(); // Set all boolean variables to be the same as stored
            Organisms.seasonsEnabled = newLoad.isSeasonsEnabled();
            Organisms.predatorPreyEnabled = newLoad.isPredatorPreyEnabled();
            mapPanel = new GenerateMap(newLoad.getWindowWidth(), newLoad.getWindowHeight(), true, newLoad);
            frame.add(mapPanel); // Create and add the map (from the stored seed)
        }

        update(); // Ensuring organisms contains what was written in JSON before it needs to be used

        consoleTextArea.setEditable(false); // So that the user cannot type in the console
        consoleLog("New Simulation Started!"); // Displaying a welcome message in the console

        frame.add(this);
        sm = new SideMenu();
        frame.add(sm.getSideMenu(), BorderLayout.WEST); // Create and add the side menu on the left
        frame.addMouseListener(this); // So any mouse clicks are recognised
        frame.pack();
        frame.setVisible(true);
    }

    public void startGameThread(){ // Starts the execution
        thread = new Thread(this);
        thread.start();
    }
    @Override
    public void run() { // This controls the speed at which the simulation runs
        int FPS = 60;
        double drawInterval = (double) 1000000000 / FPS; // Time interval between desired frames
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;

        while (thread != null) { // If the thread is started
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval; // Time between last frame and delta
            timer += (currentTime - lastTime); // Time since last frame
            lastTime = currentTime;
            if(delta >= 1) { // When to update
                try {
                    if (!gamePaused && organisms != null) { // If the game is paused, and the organisms variable has data
                        new Organisms(); // Updates all the organisms
                        update(); // Calls the update method (to sync JSON and organisms variable)
                        years++; // Update the years
                    }
                    Thread.sleep(timeFactor); // 1000 is every second, 10000 is every 10 seconds
                } catch (InterruptedException e) {
                    System.out.println();
                }
                repaint(); // Repaints the paintComponent
                delta--;
            }
            if(timer >= 1000000000) { // Checks to see if a second has passed
                timer = 0; // resets the timer
            }
        }
    }
    public void update() { // Syncs the JSON with the organisms variable
        try {
            organisms = objectMapper.readValue(new File("organisms.json"), Organism[].class); // Creating an array of organisms from organisms.json
            //objectMapper.readValue(a,b) takes the data from the file (a), and parses it into the class array (b)
        } catch (IOException e) {
            throw new RuntimeException(e); // Log any errors
        }
    }
    public void paintComponent(Graphics g) { // Painting the organisms and the map
        super.paintComponent(g);

        mapPanel.draw(g, WIDTH, HEIGHT); // Draw the map
        if(organisms != null) GenerateOrganisms.draw(g); // Draw the organisms
        sm.UpdateUI(); // Update the side menu

        if(!showConsole) remove(consolePanel); // If the user doesn't want to see the console, remove it
        else {
            consolePanel.add(consoleScrollPane); // Otherwise add in the console
            consolePanel.setPreferredSize(new Dimension(300, 100)); // Sets the size
            add(consolePanel); // Adds to the frame
        }
    }

    // This adds a message to the console, can be called from anywhere
    public static void consoleLog(String message) { // Takes a string input and adds to the console
        consoleTextArea.append(message + "\n"); // Add to the text area, and allow for the next line to be added to through \n
        SwingUtilities.invokeLater(() -> { // Sets the scroll bar to be at the bottom to always show most recent messages
            JScrollBar verticalScrollBar = consoleScrollPane.getVerticalScrollBar();
            verticalScrollBar.setValue(verticalScrollBar.getMaximum());
        });
    }

    // This checks whether the mouse has been clicked (up and down in short succession)
    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        // Gets the mouse location in relation to the map (offset by 260 and 35
        int newMouseX = mouseEvent.getX() - 260, newMouseY = mouseEvent.getY() - 35;
        Organism[] newOrganisms; // A separate variable read from the JSON so it is not dependant on the update() being completed
        try {
            newOrganisms = objectMapper.readValue(new File("organisms.json"), Organism[].class); // Creating an array of organisms from organisms.json
            for (Organism organism : newOrganisms) { // For each organism (object) in Organisms (array)
                int tempOrganismX = organism.getxCoordinate(), tempOrganismY = organism.getyCoordinate(); // Setting the current organisms coordinates into variables
                int xDifference = Math.abs(newMouseX - tempOrganismX); // Returning the difference between mouse and organism coordinates
                int yDifference = Math.abs(newMouseY - tempOrganismY); // Returns as an absolute number, so it can be compared to only one bound

                // If both are within the bound of 10
                if(xDifference <= 10 && yDifference <= 10) {
                    if(organism.getStats().isDead() && GenerateOrganisms.showDeadOrganisms){
                        // Updating both boolean variables which are to be checked
                        organismSelected = organism.getOrganismId();
                        sm.UpdateUI();
                        break; // To ensure organismSelected is not replaced
                    } else if (!organism.getStats().isDead() && !GenerateOrganisms.showDeadOrganisms){ // Ensures the user cannot select an organism which is dead if dead organisms are not showing
                        // Updating both boolean variables which are to be checked
                        organismSelected = organism.getOrganismId();
                        sm.UpdateUI();
                        break; // To ensure organismSelected is not replaced
                    }
                } else {
                    organismSelected = -1; // To ensure all other organisms are not selected
                    if(organism == newOrganisms[newOrganisms.length - 1]) sm.UpdateUI(); // If the user has clicked away from an organism, update to show global statistics
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e); // Log any errors
        }
    }

    // Not used, but required to be present
    @Override
    public void mousePressed(MouseEvent mouseEvent) {}

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {}

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {}

    @Override
    public void mouseExited(MouseEvent mouseEvent) {}
}