package battleshipdemo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.Timer;

/**
 * Main game class for the Battleship game
 */
public class BattleshipGame extends JFrame {
    // Constants
    private static final int BOARD_SIZE = 10;
    private static final int CELL_SIZE = 40;
    
    // Game components
    private Player currentPlayer;
    private Player opponent;
    private GameTime gameTime;
    private boolean setupPhase = true;
    private boolean playerTurn;
    private Ship selectedShip;
    private boolean isHorizontal = true;
    
    // Socket components
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean connected = false;
    
    // GUI Components
    private JPanel mainPanel;
    private JPanel boardsPanel;
    private JPanel defensePanel;
    private JPanel attackPanel;
    private JPanel infoPanel;
    private JPanel logPanel;
    
    private JButton[][] defenseCells;
    private JButton[][] attackCells;
    
    private JLabel playerInfoLabel;
    private JLabel opponentInfoLabel;
    private JLabel gameTimeLabel;
    private JLabel coinLabel;
    private JLabel turnLabel;
    
    private JTextArea logArea;
    private JButton exitButton;
    private Timer timer;
    private Timer connectionCheckTimer;
    
    // Keep track of available ships for placement
    private Map<String, Boolean> availableShips = new HashMap<>();
    
    /**
     * Main method to run the game
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create first player instance - will act as server
            BattleshipGame player1Game = new BattleshipGame("Player 1", 9001, 9002, true);
            player1Game.setLocation(50, 50);
            
            // Create second player instance - will connect to server
            BattleshipGame player2Game = new BattleshipGame("Player 2", 9002, 9001, false);
            player2Game.setLocation(975, 50);
            
            // Display instructions
            JOptionPane.showMessageDialog(null,
                "Two Battleship games have been launched.\n" +
                "- Player 1 (left window) will host the game\n" +
                "- Player 2 (right window) will connect to Player 1\n" +
                "Place your ships on each board to begin playing!",
                "Battleship Game",
                JOptionPane.INFORMATION_MESSAGE);
        });
    }
    
    /**
     * Constructor for the Battleship game
     * 
     * @param playerName The name of the player
     * @param myPort The port for this instance
     * @param opponentPort The port for the opponent
     * @param isServer Whether this instance is the server
     */
    public BattleshipGame(String playerName, int myPort, int opponentPort, boolean isServer) {
        super("Battleship Game - " + playerName);
        
        this.playerTurn = isServer; // Server (Player 1) goes first
        
        // Initialize game components
        currentPlayer = new Player(playerName);
        opponent = new Player(isServer ? "Player 2" : "Player 1");
        gameTime = new GameTime();
        
        // Initialize available ships
        availableShips.put("Submarine", true);
        availableShips.put("Destroyer", true);
        availableShips.put("Ferry", true);
        availableShips.put("Aircraft Carrier", true);
        
        // Initialize GUI
        initializeGUI();
        
        // Start connection handling
        if (isServer) {
            startServer(myPort);
        } else {
            startConnectionCheckTimer(opponentPort);
        }
        
        // Start game timer
        gameTime.startTime();
        startTimer();
        
        addToLogArea("Welcome to Battleship! Place your ships to begin.");
        setVisible(true);
    }
    
    /**
     * Starts the server to accept connections
     * 
     * @param port The port to listen on
     */
    private void startServer(int port) {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(port);
                addToLogArea("Waiting for opponent to connect...");
                
                clientSocket = serverSocket.accept();
                connected = true;
                
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                
                addToLogArea("Opponent connected! Ready to play.");
                
                // Start message receiver
                startMessageReceiver();
                
            } catch (IOException e) {
                addToLogArea("Server error: " + e.getMessage());
            }
        }).start();
    }
    
    /**
     * Connects to the opponent server
     * 
     * @param port The port to connect to
     */
    private void connectToOpponent(int port) {
        new Thread(() -> {
            try {
                addToLogArea("Connecting to opponent...");
                clientSocket = new Socket("localhost", port);
                connected = true;
                
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                
                addToLogArea("Connected to opponent! Ready to play.");
                
                // Start message receiver
                startMessageReceiver();
                
            } catch (IOException e) {
                addToLogArea("Failed to connect. Retrying in 3 seconds...");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }
    
    /**
     * Starts a timer to periodically check and attempt connection
     * 
     * @param opponentPort The port to connect to
     */
    private void startConnectionCheckTimer(int opponentPort) {
        connectionCheckTimer = new Timer(3000, e -> {
            if (!connected) {
                connectToOpponent(opponentPort);
            } else {
                connectionCheckTimer.stop();
            }
        });
        connectionCheckTimer.start();
    }
    
    /**
     * Starts a thread to receive messages from the opponent
     */
    private void startMessageReceiver() {
        new Thread(() -> {
            try {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    processMessage(inputLine);
                }
            } catch (IOException e) {
                addToLogArea("Connection lost: " + e.getMessage());
            }
        }).start();
    }
    
    /**
     * Sends a message to the opponent
     * 
     * @param message The message to send
     */
    private void sendMessage(String message) {
        if (connected && out != null) {
            out.println(message);
        }
    }
    
    /**
     * Processes messages received from the opponent
     * 
     * @param message The message to process
     */
   private void processMessage(String message) {
        // Debug statement
        System.out.println("Received message: " + message);

        // Messages are formatted as: COMMAND|data1|data2|...
        String[] parts = message.split("\\|");
        if (parts.length == 0) return;

        String command = parts[0];

        switch (command) {
            case "SHOT":
                if (parts.length >= 3) {
                    int row = Integer.parseInt(parts[1]);
                    int col = Integer.parseInt(parts[2]);
                    handleOpponentShot(row, col);
                } else {
                    System.out.println("Invalid SHOT message format");
                }
                break;

            case "RESULT":
                if (parts.length >= 2) {
                    boolean hit = Boolean.parseBoolean(parts[1]);
                    processLastShotResult(hit);
                }
                break;

            case "SUNK":
                if (parts.length >= 2) {
                    String shipName = parts[1];
                    addToLogArea("You sunk the opponent's " + shipName + "!");
                    currentPlayer.earnCoins(20);
                    updateCoinLabel();
                }
                break;

            case "GAME_OVER":
                gameOver(true);
                break;

            case "CHAT":
                if (parts.length >= 2) {
                    String chatMessage = message.substring(command.length() + 1);
                    addToLogArea("Opponent: " + chatMessage);
                }
                break;
        }
    }
    
    /**
     * Handles a shot from the opponent
     * 
     * @param row The row of the shot
     * @param col The column of the shot
     */
   private void handleOpponentShot(int row, int col) {
        // Process opponent's shot
        addToLogArea("Opponent fired at (" + row + ", " + col + ")");

        // Debug statement
        System.out.println("Processing opponent shot at: " + row + "," + col);

        boolean hit = currentPlayer.getDefenseBoard().receiveAttack(row, col);
        updateDefenseBoard();

        // Send result back
        sendMessage("RESULT|" + hit);
        System.out.println("Sent result: " + hit);

        if (hit) {
            addToLogArea("Opponent hit your ship!");

            // Check if a ship was sunk
            Ship hitShip = currentPlayer.getDefenseBoard().getShipAt(row, col);
            if (hitShip != null && hitShip.isSunk()) {
                addToLogArea("Opponent sunk your " + hitShip.getName() + "!");
                sendMessage("SUNK|" + hitShip.getName());

                // Check if all ships are sunk (game over)
                boolean allSunk = true;
                for (Ship ship : currentPlayer.getDefenseBoard().getShips()) {
                    if (!ship.isSunk()) {
                        allSunk = false;
                        break;
                    }
                }

                if (allSunk) {
                    sendMessage("GAME_OVER");
                    gameOver(false);
                    return;
                }
            }
        } else {
            addToLogArea("Opponent missed!");
        }

        // Switch turns (opponent fired, now it's player's turn)
        playerTurn = true;
        turnLabel.setText("Status: Your turn");

        // Make sure to update UI in the EDT
        SwingUtilities.invokeLater(() -> {
            turnLabel.setText("Status: Your turn");
        });
    }

    
    /**
     * Processes the result of the last shot
     * 
     * @param hit Whether the shot was a hit
     */
    private void processLastShotResult(boolean hit) {
        // Find the last shot position
        int lastRow = -1, lastCol = -1;

        // Debug statement
        System.out.println("Processing shot result: " + (hit ? "HIT" : "MISS"));

        // Find the pending shot
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (currentPlayer.getAttackBoard().getCell(i, j) == 'P') {
                    lastRow = i;
                    lastCol = j;
                    break;
                }
            }
            if (lastRow != -1) break;
        }

        if (lastRow != -1) {
            System.out.println("Found pending shot at: " + lastRow + "," + lastCol);

            if (hit) {
                currentPlayer.getAttackBoard().markHit(lastRow, lastCol);
                addToLogArea("Hit at (" + lastRow + ", " + lastCol + ")!");
                currentPlayer.earnCoins(10);
                updateCoinLabel();
            } else {
                currentPlayer.getAttackBoard().markMiss(lastRow, lastCol);
                addToLogArea("Miss at (" + lastRow + ", " + lastCol + ")");

                // Switch turns only on a miss (hit allows another shot in this version)
                playerTurn = false;

                // Make sure to update UI in the EDT
                SwingUtilities.invokeLater(() -> {
                    turnLabel.setText("Status: Opponent's turn");
                });
            }

            updateAttackBoard();
        } else {
            System.out.println("No pending shot found");
        }
    }
    
    /**
     * Initializes the game's graphical user interface
     */
    private void initializeGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setResizable(false);
        
        mainPanel = new JPanel(new BorderLayout());
        
        // Info panel (top)
        infoPanel = new JPanel(new BorderLayout());
        JPanel playerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        playerInfoLabel = new JLabel("Player: " + currentPlayer.getName());
        opponentInfoLabel = new JLabel("Opponent: " + opponent.getName());
        gameTimeLabel = new JLabel("Time: 00:00");
        turnLabel = new JLabel("Status: " + (setupPhase ? "Place your ships" : 
            (playerTurn ? "Your turn" : "Opponent's turn")));
        
        playerPanel.add(playerInfoLabel);
        playerPanel.add(opponentInfoLabel);
        playerPanel.add(gameTimeLabel);
        
        JPanel coinPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        coinLabel = new JLabel("Coins: " + currentPlayer.getCoins());
        coinPanel.add(coinLabel);
        coinPanel.add(turnLabel);
        
        infoPanel.add(playerPanel, BorderLayout.WEST);
        infoPanel.add(coinPanel, BorderLayout.EAST);
        
        // Boards panel (center)
        boardsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        
        // Defense board
        defensePanel = new JPanel(new BorderLayout());
        JPanel defenseGrid = createBoardPanel(true);
        defensePanel.add(new JLabel("Your Ships", SwingConstants.CENTER), BorderLayout.NORTH);
        defensePanel.add(defenseGrid, BorderLayout.CENTER);
        
        // Ship selection panel
        JPanel shipSelectionPanel = new JPanel(new GridLayout(1, 5, 5, 0));
        
        JButton submarineBtn = new JButton("Submarine (2)");
        JButton destroyerBtn = new JButton("Destroyer (3)");
        JButton ferryBtn = new JButton("Ferry (4)");
        JButton aircraftBtn = new JButton("Aircraft (5)");
        JButton rotateBtn = new JButton("Rotate");
        
        submarineBtn.addActionListener(e -> selectShip(new Submarine()));
        destroyerBtn.addActionListener(e -> selectShip(new Destroyer()));
        ferryBtn.addActionListener(e -> selectShip(new Ferry()));
        aircraftBtn.addActionListener(e -> selectShip(new AircraftCarrier()));
        rotateBtn.addActionListener(e -> {
            isHorizontal = !isHorizontal;
            addToLogArea("Ship orientation " + (isHorizontal ? "horizontal" : "vertical"));
        });
        
        shipSelectionPanel.add(submarineBtn);
        shipSelectionPanel.add(destroyerBtn);
        shipSelectionPanel.add(ferryBtn);
        shipSelectionPanel.add(aircraftBtn);
        shipSelectionPanel.add(rotateBtn);
        
        defensePanel.add(shipSelectionPanel, BorderLayout.SOUTH);
        
        // Attack board
        attackPanel = new JPanel(new BorderLayout());
        JPanel attackGrid = createBoardPanel(false);
        attackPanel.add(new JLabel("Enemy Waters", SwingConstants.CENTER), BorderLayout.NORTH);
        attackPanel.add(attackGrid, BorderLayout.CENTER);
        
        // Chat panel
        JPanel chatPanel = new JPanel(new BorderLayout());
        JTextField chatField = new JTextField();
        JButton chatBtn = new JButton("Send");
        
        ActionListener chatAction = e -> {
            String message = chatField.getText();
            if (!message.isEmpty()) {
                sendMessage("CHAT|" + message);
                addToLogArea("You: " + message);
                chatField.setText("");
            }
        };
        
        chatField.addActionListener(chatAction);
        chatBtn.addActionListener(chatAction);
        
        chatPanel.add(chatField, BorderLayout.CENTER);
        chatPanel.add(chatBtn, BorderLayout.EAST);
        
        attackPanel.add(chatPanel, BorderLayout.SOUTH);
        
        boardsPanel.add(defensePanel);
        boardsPanel.add(attackPanel);
        
        // Log panel (bottom)
        logPanel = new JPanel(new BorderLayout());
        logArea = new JTextArea(5, 30);
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        
        exitButton = new JButton("Exit Game");
        exitButton.addActionListener(e -> exitGame());
        
        logPanel.add(scrollPane, BorderLayout.CENTER);
        logPanel.add(exitButton, BorderLayout.EAST);
        
        // Add panels to main panel
        mainPanel.add(infoPanel, BorderLayout.NORTH);
        mainPanel.add(boardsPanel, BorderLayout.CENTER);
        mainPanel.add(logPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Initially disable attack grid during setup
        setAttackGridEnabled(false);
    }
    
    /**
     * Creates a game board panel with buttons
     * 
     * @param isDefense Whether this is the defense board
     * @return The created board panel
     */
    private JPanel createBoardPanel(boolean isDefense) {
        JPanel panel = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        JButton[][] buttons = new JButton[BOARD_SIZE][BOARD_SIZE];
        
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                final int row = i;
                final int col = j;
                
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
                button.setBackground(Color.BLUE);
                button.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                
                if (isDefense) {
                    button.addActionListener(e -> handleDefenseClick(row, col));
                } else {
                    button.addActionListener(e -> handleAttackClick(row, col));
                }
                
                panel.add(button);
                buttons[i][j] = button;
            }
        }
        
        if (isDefense) {
            defenseCells = buttons;
        } else {
            attackCells = buttons;
        }
        
        return panel;
    }
    
    /**
     * Selects a ship for placement
     * 
     * @param ship The ship to select
     */
    private void selectShip(Ship ship) {
        String shipName = ship.getName();
        if (availableShips.get(shipName)) {
            selectedShip = ship;
            addToLogArea("Selected " + shipName + " (" + ship.getLength() + " units)");
        } else {
            addToLogArea("You've already placed your " + shipName);
        }
    }
    
    /**
     * Handles a click on the defense board
     * 
     * @param row The row of the click
     * @param col The column of the click
     */
    private void handleDefenseClick(int row, int col) {
        if (setupPhase && selectedShip != null) {
            boolean placed = currentPlayer.getDefenseBoard().placeShip(selectedShip, row, col, isHorizontal);
            if (placed) {
                updateDefenseBoard();
                addToLogArea(selectedShip.getName() + " placed successfully");
                availableShips.put(selectedShip.getName(), false);
                selectedShip = null;
                
                // Check if all ships are placed
                if (!availableShips.containsValue(true)) {
                    setupPhase = false;
                    setAttackGridEnabled(true);
                    addToLogArea("All ships placed. Ready to play!");
                    turnLabel.setText("Status: " + (playerTurn ? "Your turn" : "Opponent's turn"));
                }
            } else {
                addToLogArea("Invalid ship placement! Try again.");
            }
        }
    }
    
    /**
     * Handles a click on the attack board
     * 
     * @param row The row of the click
     * @param col The column of the click
     */
    private void handleAttackClick(int row, int col) {
        if (!setupPhase && playerTurn && connected) {
            // Check if the cell has already been targeted
            char cell = currentPlayer.getAttackBoard().getCell(row, col);
            if (cell != '~') {
                addToLogArea("You've already fired at this location. Try again.");
                return;
            }

            // Mark as pending
            currentPlayer.getAttackBoard().markPending(row, col);
            updateAttackBoard();

            // Send shot to opponent
            sendMessage("SHOT|" + row + "|" + col);
            addToLogArea("You fired at (" + row + ", " + col + ")");

            // Add debug statement
            System.out.println("Sent shot to opponent: " + row + "," + col);
        } else {
            // Debug why attack is not working
            if (setupPhase) {
                addToLogArea("Cannot attack during setup phase");
            } else if (!playerTurn) {
                addToLogArea("Not your turn");
            } else if (!connected) {
                addToLogArea("Not connected to opponent");
            }
        }
    }
    
    /**
     * Updates the defense board display
     */
    private void updateDefenseBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                char cell = currentPlayer.getDefenseBoard().getCell(i, j);
                if (cell == 'S') {
                    defenseCells[i][j].setBackground(Color.DARK_GRAY);
                } else if (cell == 'X') {
                    defenseCells[i][j].setBackground(Color.RED);
                } else if (cell == 'O') {
                    defenseCells[i][j].setBackground(Color.WHITE);
                } else {
                    defenseCells[i][j].setBackground(Color.BLUE);
                }
            }
        }
    }
    
    /**
     * Updates the attack board display
     */
    private void updateAttackBoard() {
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    char cell = currentPlayer.getAttackBoard().getCell(i, j);
                    if (cell == 'X') {
                        attackCells[i][j].setBackground(Color.RED);
                    } else if (cell == 'O') {
                        attackCells[i][j].setBackground(Color.WHITE);
                    } else if (cell == 'P') {
                        attackCells[i][j].setBackground(Color.YELLOW); // Pending
                    } else {
                        attackCells[i][j].setBackground(Color.BLUE);
                    }
                }
            }
        });
    }
    
    /**
     * Enables or disables the attack grid
     * 
     * @param enabled Whether the attack grid should be enabled
     */
    private void setAttackGridEnabled(boolean enabled) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                attackCells[i][j].setEnabled(enabled);
            }
        }
    }
    
    /**
     * Adds a message to the log area
     * 
     * @param message The message to add
     */
    private void addToLogArea(String message) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String timestamp = sdf.format(new Date());
        
        SwingUtilities.invokeLater(() -> {
            logArea.append("[" + timestamp + "] " + message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
    
    /**
     * Updates the coin label
     */
    private void updateCoinLabel() {
        SwingUtilities.invokeLater(() -> {
            coinLabel.setText("Coins: " + currentPlayer.getCoins());
        });
    }
    
    /**
     * Starts the game timer
     */
    private void startTimer() {
        timer = new Timer(1000, e -> {
            gameTimeLabel.setText("Time: " + gameTime.getFormattedTime());
        });
        timer.start();
    }
    
    /**
     * Handles the end of the game
     * 
     * @param playerWon Whether the player won
     */
    private void gameOver(boolean playerWon) {
        gameTime.stopTime();
        timer.stop();
        setAttackGridEnabled(false);
        
        if (playerWon) {
            currentPlayer.earnCoins(100); // Victory bonus
            updateCoinLabel();
            turnLabel.setText("Status: You win!");
            addToLogArea("Congratulations! You've won in " + gameTime.getFormattedTime() + "!");
            JOptionPane.showMessageDialog(this, 
                "Victory! You sank all enemy ships in " + gameTime.getFormattedTime(), 
                "Game Over", 
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            turnLabel.setText("Status: You lose!");
            addToLogArea("Game over! You've been defeated in " + gameTime.getFormattedTime() + ".");
            JOptionPane.showMessageDialog(this, 
                "Defeat! All your ships have been sunk in " + gameTime.getFormattedTime(), 
                "Game Over", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Exits the game
     */
    private void exitGame() {
        int option = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to exit the game?",
            "Exit Game",
            JOptionPane.YES_NO_OPTION);
            
        if (option == JOptionPane.YES_OPTION) {
            if (timer != null) timer.stop();
            if (connectionCheckTimer != null) connectionCheckTimer.stop();
            
            try {
                if (out != null) out.close();
                if (in != null) in.close();
                if (clientSocket != null) clientSocket.close();
                if (serverSocket != null) serverSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing connections: " + e.getMessage());
            }
            
            dispose();
            System.exit(0);
        }
    }
    
    /**
     * Sets the location of the game window
     */
    @Override
    public void setLocation(int x, int y) {
        super.setLocation(x, y);
    }
}
