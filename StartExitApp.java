import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class StartExitApp extends JFrame {

    private BufferedImage backgroundImage;
    private Image appIcon;
    private String selectedFilePath; // Holds the selected file path for WordDB
    private String[] playerNames;  // Store player names
    private int numPlayers;        // Store number of players

    public StartExitApp() {
        // Load the background image and app icon
        try {
            backgroundImage = ImageIO.read(new File("src/resources/startscreen.png"));
            appIcon = ImageIO.read(new File("src/resources/logo.png")); // Add path to your icon file
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set up the frame
        setSize(905, 487);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setTitle("Start and Exit Application");
        setIconImage(appIcon); // Set the app icon

        // Create a custom panel to display the background image
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        panel.setLayout(new FlowLayout());
        setContentPane(panel);

        // Create the "Start" button
        JButton startButton = new JButton("Start");
        startButton.setPreferredSize(new Dimension(100, 40));
        startButton.setForeground(Color.WHITE);
        startButton.setBackground(Color.BLACK);
        panel.add(startButton);

        // Create the "Exit" button
        JButton exitButton = new JButton("Exit");
        exitButton.setPreferredSize(new Dimension(100, 40));
        exitButton.setForeground(Color.WHITE);
        exitButton.setBackground(Color.BLACK);
        panel.add(exitButton);

        // Add action listener for the "Start" button
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Input number of players
                String input = JOptionPane.showInputDialog("Enter number of players:");
                if (input != null) {
                    try {
                        numPlayers = Integer.parseInt(input);
                        if (numPlayers > 0) {
                            playerNames = new String[numPlayers];
                            for (int i = 0; i < numPlayers; i++) {
                                playerNames[i] = JOptionPane.showInputDialog("Enter Player " + (i + 1) + " name:");
                            }
                            // Proceed to quiz selection
                            openQuizSelectionPage();
                        } else {
                            JOptionPane.showMessageDialog(null, "Please enter a valid number of players.");
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Please enter a valid number.");
                    }
                }
            }
        });

        // Add action listener for the "Exit" button
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirmed = JOptionPane.showConfirmDialog(StartExitApp.this,
                        "Are you sure you want to exit the program?", "Exit Confirmation",
                        JOptionPane.YES_NO_OPTION);

                if (confirmed == JOptionPane.YES_OPTION) {
                    dispose();  // Close the application
                }
            }
        });

        // Make the frame visible
        setVisible(true);
    }

    private void openQuizSelectionPage() {
        // Create a new frame for the quiz selection page
        JFrame quizFrame = new JFrame("Quiz Selection");
        quizFrame.setSize(905, 487);
        quizFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        quizFrame.setLocationRelativeTo(null);
        quizFrame.setResizable(false);
        setBackgroundImage(quizFrame, "src/resources/startscreen.png"); // Set the background image
        quizFrame.setLayout(new FlowLayout());

        // Create "Java Quiz" button
        JButton javaQuizButton = new JButton("Java Quiz");
        javaQuizButton.setPreferredSize(new Dimension(120, 40));
        javaQuizButton.setForeground(Color.WHITE);
        javaQuizButton.setBackground(Color.BLACK);
        quizFrame.add(javaQuizButton);

        // Create "SQL Quiz" button
        JButton sqlQuizButton = new JButton("SQL Quiz");
        sqlQuizButton.setPreferredSize(new Dimension(120, 40));
        sqlQuizButton.setForeground(Color.WHITE);
        sqlQuizButton.setBackground(Color.BLACK);
        quizFrame.add(sqlQuizButton);

        // Add action listeners to quiz buttons
        javaQuizButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open the Java quiz difficulty selection page
                openDifficultySelectionPage("Java");
            }
        });

        sqlQuizButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openDifficultySelectionPage("SQL");
            }
        });

        // Make the quiz selection frame visible
        quizFrame.setVisible(true);
    }

    private void openDifficultySelectionPage(String quizType) {
        // Create a new frame for the quiz difficulty selection
        JFrame difficultyFrame = new JFrame(quizType + " Quiz Difficulty");
        difficultyFrame.setSize(905, 487);
        difficultyFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        difficultyFrame.setLocationRelativeTo(null);
        difficultyFrame.setResizable(false);
        setBackgroundImage(difficultyFrame, "src/resources/startscreen.png"); // Set the background image
        difficultyFrame.setLayout(new FlowLayout());

        // Create "Easy" button
        JButton easyButton = new JButton("Easy");
        easyButton.setPreferredSize(new Dimension(100, 40));
        easyButton.setForeground(Color.WHITE);
        easyButton.setBackground(Color.BLACK);
        difficultyFrame.add(easyButton);

        // Create "Medium" button
        JButton mediumButton = new JButton("Medium");
        mediumButton.setPreferredSize(new Dimension(100, 40));
        mediumButton.setForeground(Color.WHITE);
        mediumButton.setBackground(Color.BLACK);
        difficultyFrame.add(mediumButton);

        // Create "Difficult" button
        JButton difficultButton = new JButton("Difficult");
        difficultButton.setPreferredSize(new Dimension(100, 40));
        difficultButton.setForeground(Color.WHITE);
        difficultButton.setBackground(Color.BLACK);
        difficultyFrame.add(difficultButton);

        // Add action listeners to difficulty buttons
        easyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedFilePath = getFilePath(quizType, "Easy");
                startGame();
            }
        });

        mediumButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedFilePath = getFilePath(quizType, "Medium");
                startGame();
            }
        });

        difficultButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedFilePath = getFilePath(quizType, "Difficult");
                startGame();
            }
        });

        // Make the difficulty selection frame visible
        difficultyFrame.setVisible(true);
    }

    private String getFilePath(String quizType, String difficulty) {
        // Return the correct file path based on quizType and difficulty
        if (quizType.equals("Java")) {
            switch (difficulty) {
                case "Easy": return CommonConstants.Java_E;
                case "Medium": return CommonConstants.Java_M;
                case "Difficult": return CommonConstants.Java_H;
            }
        } else if (quizType.equals("SQL")) {
            switch (difficulty) {
                case "Easy": return CommonConstants.Sql_E;
                case "Medium": return CommonConstants.Sql_M;
                case "Difficult": return CommonConstants.Sql_H;
            }
        }
        return null;
    }

    private void startGame() {
        // Check if the selected file path is valid
        if (selectedFilePath == null || !new File(selectedFilePath).exists()) {
            JOptionPane.showMessageDialog(this, "Quiz file not found: " + selectedFilePath, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Pass the selected file path and player names to Hangman
        WordDB wordDB = new WordDB(selectedFilePath);
        new Hangman(wordDB, playerNames).setVisible(true);  // Pass player names to Hangman
    }

    private void setBackgroundImage(JFrame frame, String imagePath) {
        try {
            BufferedImage bgImage = ImageIO.read(new File(imagePath));
            JPanel panel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    if (bgImage != null) {
                        g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                    }
                }
            };
            panel.setLayout(new FlowLayout());
            frame.setContentPane(panel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new StartExitApp();
            }
        });
    }
}
