import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;

public class Hangman extends JFrame implements ActionListener {
    private int incorrectGuesses;
    private String[] wordChallenge;
    private final WordDB wordDB;
    private JLabel hangmanImage, categoryLabel, hiddenWordLabel, resultLabel, wordLabel, currentPlayerLabel;
    private JButton[] letterButtons;
    private JDialog resultDialog;
    private Font customFont;

    // New variables for players
    private String[] playerNames;
    private int currentPlayerIndex;
    private int[] correctGuesses;
    private int[] incorrectGuessesArray;
    private int[] scores;

    public Hangman(WordDB wordDB, String[] playerNames) {
        super("Hangman Game (Java Ed.)");
        setSize(CommonConstants.FRAME_SIZE);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);
        getContentPane().setBackground(CommonConstants.BACKGROUND_COLOR);

        this.wordDB = wordDB;
        this.playerNames = playerNames;
        currentPlayerIndex = 0;
        correctGuesses = new int[playerNames.length];
        incorrectGuessesArray = new int[playerNames.length];
        scores = new int[playerNames.length];
        letterButtons = new JButton[26];
        wordChallenge = wordDB.loadChallenge();
        customFont = CustomTools.createFont(CommonConstants.FONT_PATH);
        createResultDialog();

        addGuiComponents();
        updateCurrentPlayerLabel();  // Update label to show the current player
    }

    private void addGuiComponents() {
        // hangman image
        hangmanImage = CustomTools.loadImage(CommonConstants.IMAGE_PATH);
        hangmanImage.setBounds(0, 0, hangmanImage.getPreferredSize().width, hangmanImage.getPreferredSize().height);

        // category display
        categoryLabel = new JLabel(wordChallenge[0]);
        categoryLabel.setFont(customFont.deriveFont(30f));
        categoryLabel.setHorizontalAlignment(SwingConstants.CENTER);
        categoryLabel.setOpaque(true);
        categoryLabel.setForeground(Color.WHITE);
        categoryLabel.setBackground(CommonConstants.SECONDARY_COLOR);
        categoryLabel.setBorder(BorderFactory.createLineBorder(CommonConstants.SECONDARY_COLOR));
        categoryLabel.setBounds(
                0,
                hangmanImage.getPreferredSize().height - 28,
                CommonConstants.FRAME_SIZE.width,
                categoryLabel.getPreferredSize().height
        );

        // hidden word
        hiddenWordLabel = new JLabel(CustomTools.hideWords(wordChallenge[1]));
        hiddenWordLabel.setFont(customFont.deriveFont(64f));
        hiddenWordLabel.setForeground(Color.WHITE);
        hiddenWordLabel.setHorizontalAlignment(SwingConstants.CENTER);
        hiddenWordLabel.setBounds(
                0,
                categoryLabel.getY() + categoryLabel.getPreferredSize().height + 50,
                CommonConstants.FRAME_SIZE.width,
                hiddenWordLabel.getPreferredSize().height
        );

        // current player label
        currentPlayerLabel = new JLabel();
        currentPlayerLabel.setFont(customFont.deriveFont(22f));
        currentPlayerLabel.setForeground(Color.WHITE);
        currentPlayerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        currentPlayerLabel.setBounds(0, hiddenWordLabel.getY() + hiddenWordLabel.getPreferredSize().height + 20,
                CommonConstants.FRAME_SIZE.width, 30);
        updateCurrentPlayerLabel(); // Initial update
        getContentPane().add(currentPlayerLabel);

        // letter buttons
        GridLayout gridLayout = new GridLayout(4, 7);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBounds(
                -5,
                currentPlayerLabel.getY() + currentPlayerLabel.getPreferredSize().height,
                CommonConstants.BUTTON_PANEL_SIZE.width,
                CommonConstants.BUTTON_PANEL_SIZE.height
        );
        buttonPanel.setLayout(gridLayout);

        // create the letter buttons
        for (char c = 'A'; c <= 'Z'; c++) {
            JButton button = new JButton(Character.toString(c));
            button.setBackground(CommonConstants.PRIMARY_COLOR);
            button.setFont(customFont.deriveFont(22f));
            button.setForeground(Color.WHITE);
            button.addActionListener(this);

            int currentIndex = c - 'A';
            letterButtons[currentIndex] = button;
            buttonPanel.add(letterButtons[currentIndex]);
        }

        // next player button
        JButton nextPlayerButton = new JButton("Next Player");
        nextPlayerButton.setFont(customFont.deriveFont(22f));
        nextPlayerButton.setForeground(Color.WHITE);
        nextPlayerButton.setBackground(CommonConstants.SECONDARY_COLOR);
        nextPlayerButton.addActionListener(e -> nextPlayer());
        buttonPanel.add(nextPlayerButton);

        // quit button
        JButton quitButton = new JButton("Quit");
        quitButton.setFont(customFont.deriveFont(22f));
        quitButton.setForeground(Color.WHITE);
        quitButton.setBackground(CommonConstants.SECONDARY_COLOR);
        quitButton.addActionListener(e -> exitGame());
        buttonPanel.add(quitButton);

        getContentPane().add(categoryLabel);
        getContentPane().add(hangmanImage);
        getContentPane().add(hiddenWordLabel);
        getContentPane().add(buttonPanel);
    }

    private void updateCurrentPlayerLabel() {
        currentPlayerLabel.setText("Current Player: " + playerNames[currentPlayerIndex]);
    }

    @Override
public void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();

    if (command.equals("Quit")) {
        exitGame();
        return;
    }

    // Disable the pressed button
    JButton button = (JButton) e.getSource();
    button.setEnabled(false);

    // Check if the guessed letter is in the word
    if (wordChallenge[1].contains(command)) {
        // Indicate the correct guess
        button.setBackground(Color.GREEN);

        // Update the hidden word
        char[] hiddenWord = hiddenWordLabel.getText().toCharArray();
        for (int i = 0; i < wordChallenge[1].length(); i++) {
            if (wordChallenge[1].charAt(i) == command.charAt(0)) {
                hiddenWord[i] = command.charAt(0);  // Replace the hidden asterisk with the correct letter
            }
        }
        hiddenWordLabel.setText(String.valueOf(hiddenWord));
        scores[currentPlayerIndex] += 100; // Update score for current player

        // Increment correct guesses for this player
        correctGuesses[currentPlayerIndex]++;

        // If the user has guessed the entire word
        if (!hiddenWordLabel.getText().contains("*")) {
            resultLabel.setText("You got it right!");
            resultDialog.setVisible(true);

            // Save player data to database
            savePlayerDataToDatabase(playerNames[currentPlayerIndex], correctGuesses[currentPlayerIndex], 
                                      incorrectGuessesArray[currentPlayerIndex], scores[currentPlayerIndex]);
        }
    } else {
        // Indicate the incorrect guess
        button.setBackground(Color.RED);
        
        // Increase the incorrect guess counter for this player
        incorrectGuessesArray[currentPlayerIndex]++;
        ++incorrectGuesses;  // Overall incorrect guesses count for the current game
        
        // Update hangman image based on the number of incorrect guesses
        CustomTools.updateImage(hangmanImage, "resources/" + (incorrectGuesses + 1) + ".png");

        // If user has exceeded the maximum number of incorrect guesses
        if (incorrectGuesses >= 6) {
            resultLabel.setText("Too Bad, Try Again?");
            resultDialog.setVisible(true);

            // Save player data to database
            savePlayerDataToDatabase(playerNames[currentPlayerIndex], correctGuesses[currentPlayerIndex], 
                                      incorrectGuessesArray[currentPlayerIndex], scores[currentPlayerIndex]);
        }
    }
    wordLabel.setText("Word: " + wordChallenge[1]);  // For debugging
}


    private void createResultDialog() {
        resultDialog = new JDialog();
        resultDialog.setTitle("Result");
        resultDialog.setSize(CommonConstants.RESULT_DIALOG_SIZE);
        resultDialog.getContentPane().setBackground(CommonConstants.BACKGROUND_COLOR);
        resultDialog.setResizable(false);
        resultDialog.setLocationRelativeTo(this);
        resultDialog.setModal(true);
        resultDialog.setLayout(new GridLayout(3, 1));
        resultDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                nextPlayer();
            }
        });

        resultLabel = new JLabel();
        resultLabel.setForeground(Color.WHITE);
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);

        wordLabel = new JLabel();
        wordLabel.setForeground(Color.WHITE);
        wordLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton nextPlayerButton = new JButton("Next Player");
        nextPlayerButton.setForeground(Color.WHITE);
        nextPlayerButton.setBackground(CommonConstants.SECONDARY_COLOR);
        nextPlayerButton.addActionListener(e -> {
            resultDialog.dispose();
            nextPlayer(); // Move to next player after closing dialog
        });

        resultDialog.add(resultLabel);
        resultDialog.add(wordLabel);
        resultDialog.add(nextPlayerButton);
    }

    private void nextPlayer() {
        currentPlayerIndex++;
        if (currentPlayerIndex >= playerNames.length) {
            showScoreboard();
        } else {
            resetGame();
            updateCurrentPlayerLabel(); // Update current player label
        }
    }

    private void exitGame() {
        int confirmed = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to quit the game?", "Confirm Exit",
                JOptionPane.YES_NO_OPTION);
        if (confirmed == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private void showScoreboard() {
        // Create a new JDialog for the scoreboard
        JDialog scoreboardDialog = new JDialog();
        scoreboardDialog.setTitle("Scoreboard");
        scoreboardDialog.setSize(500, 300);
        scoreboardDialog.setLocationRelativeTo(this);
        scoreboardDialog.setModal(true);
        
        // Set background to white
        scoreboardDialog.getContentPane().setBackground(Color.BLACK);
        
        // Create column headers
        String[] columns = {"Player", "Correct Guesses", "Scores"};
        
        // Prepare the data in a table format
        String[][] data = new String[playerNames.length][3];
        for (int i = 0; i < playerNames.length; i++) {
            data[i][0] = playerNames[i];  // Player name
            data[i][1] = String.valueOf(correctGuesses[i]);  // Correct guesses
            data[i][2] = String.valueOf(scores[i]);  // Scores
            // Save each player's data to the database
            savePlayerDataToDatabase(playerNames[i], correctGuesses[i], incorrectGuessesArray[i], scores[i]);
        }
        
        // Create a JTable to display the data
        JTable table = new JTable(data, columns);
        
        // Customize table appearance
        table.setBackground(Color.black);
        table.setForeground(Color.white);  // Black text for better contrast
        table.setGridColor(Color.white);  // Light grid lines
        table.setFont(new Font("Cartoonero", Font.PLAIN, 16));  // Set font size and style
        table.setRowHeight(30);  // Set row height for better visibility
    
        // Add the table to a JScrollPane in case the data overflows
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(Color.WHITE);
        
        // Add the scroll pane to the dialog
        scoreboardDialog.add(scrollPane, BorderLayout.CENTER);
        
        // Add a close button at the bottom
        JButton closeButton = new JButton("Close");
        closeButton.setBackground(CommonConstants.SECONDARY_COLOR);  // Use secondary color for the button
        closeButton.setForeground(Color.WHITE);
        closeButton.setFont(new Font("Cartoonero", Font.BOLD, 14));
        closeButton.addActionListener(e -> scoreboardDialog.dispose());
        
        // Add the close button in the dialog's south region
        scoreboardDialog.add(closeButton, BorderLayout.SOUTH);
        
        // Make the dialog visible
        scoreboardDialog.setVisible(true);
    }
    

    private void resetGame() {
        incorrectGuesses = 0;
        hangmanImage.setIcon(new ImageIcon(CommonConstants.IMAGE_PATH + "1.png"));
        hiddenWordLabel.setText(CustomTools.hideWords(wordChallenge[1]));

        for (JButton button : letterButtons) {
            button.setEnabled(true);
            button.setBackground(CommonConstants.PRIMARY_COLOR);
        }
    }

    private void savePlayerDataToDatabase(String playerName, int correctGuesses, int incorrectGuesses, int score) {
        // Connect to database
        try (Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/game", "root", "root");
             PreparedStatement pstmt = conn.prepareStatement(
                 "INSERT INTO scoreboard (username, correct_guesses, incorrect_guesses, score) VALUES (?, ?, ?, ?) " +
                 "ON DUPLICATE KEY UPDATE correct_guesses = ?, incorrect_guesses = ?, score = ?")) {
    
            pstmt.setString(1, playerName);
            pstmt.setInt(2, correctGuesses);
            pstmt.setInt(3, incorrectGuesses);
            pstmt.setInt(4, score);
    
            // Add these for the update clause
            pstmt.setInt(5, correctGuesses);
            pstmt.setInt(6, incorrectGuesses);
            pstmt.setInt(7, score);
    
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

}
