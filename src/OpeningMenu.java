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
        frame.add(this);
        frame.setVisible(true);

        menuPanel = createVerticalMenu();

        frame.setLayout(new BorderLayout());
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
        menu.setPreferredSize(new Dimension(100, getHeight())); // Adjust the width as needed

        JButton startButton = new JButton("Start");
        JButton loadButton = new JButton("Load");
        JButton optionButton = new JButton("Options");
        JButton exitButton = new JButton("Exit");
        startButton.setForeground(Color.black);
        startButton.setBackground(Color.darkGray);
        startButton.setToolTipText("Starts a new iteration");
        startButton.setPreferredSize(new Dimension(100, 50));
        startButton.setBorder(BorderFactory.createLineBorder(Color.darkGray, 1));
        startButton.setFont(new Font("Arial", Font.BOLD, 16));
        loadButton.setForeground(Color.black);
        loadButton.setBackground(Color.darkGray);
        loadButton.setToolTipText("Loads into a previous save");
        loadButton.setPreferredSize(new Dimension(100, 50));
        loadButton.setBorder(BorderFactory.createLineBorder(Color.darkGray, 1));
        loadButton.setFont(new Font("Arial", Font.BOLD, 16));
        optionButton.setForeground(Color.black);
        optionButton.setBackground(Color.darkGray);
        optionButton.setToolTipText("Set options for EvSim");
        optionButton.setPreferredSize(new Dimension(100, 50));
        optionButton.setBorder(BorderFactory.createLineBorder(Color.darkGray, 1));
        optionButton.setFont(new Font("Arial", Font.BOLD, 16));
        exitButton.setForeground(Color.black);
        exitButton.setBackground(Color.darkGray);
        exitButton.setToolTipText("Exit the program");
        exitButton.setPreferredSize(new Dimension(100, 50));
        exitButton.setBorder(BorderFactory.createLineBorder(Color.darkGray, 1));
        exitButton.setFont(new Font("Arial", Font.BOLD, 16));

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.exit(0);
            }
        });
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                GameWindow gameWindow = new GameWindow(1920, 1080);
                gameWindow.startGameThread();
                frame.dispose();
            }
        });
        menu.add(startButton);
        menu.add(loadButton);
        menu.add(optionButton);
        menu.add(exitButton);
        return menu;
    }
    @Override
    public void run() {

    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setFont(new Font("Agency FB", Font.BOLD, 108));
        g.setColor(Color.white);

        Dimension gameWindowSize = getSize();

        FontMetrics fontMetrics = g.getFontMetrics();
        int textWidth = fontMetrics.stringWidth("EVSIM");
        int textHeight = fontMetrics.getHeight();

        int titleTextX = (gameWindowSize.width - textWidth) / 2;
        int titleTextY = (gameWindowSize.height + textHeight) / 2;

        g.drawString("EVSIM", titleTextX, titleTextY);
        g.setFont(new Font("Agency FB", Font.BOLD, 36));

        g.drawString("Press 'Start' to begin", titleTextX - 15, titleTextY + 50);
    }
}
