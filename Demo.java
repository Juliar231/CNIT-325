package battleshipdemo;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.text.SimpleDateFormat;
import javax.swing.Timer;
import java.util.List;

public class BattleshipDemo extends JFrame {
    // Constants
    private static final int BOARD_SIZE = 10;
    private static final int CELL_SIZE = 40;
    
    // Game components
    private Player humanPlayer;
    private Player computerPlayer;
    private GameTime gameTime;
    private boolean setupPhase = true;
    private boolean playerTurn = true;
    private Ship selectedShip;
    private boolean isHorizontal = true;
    
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
    
    // Keep track of available ships for placement
    private Map<String, Boolean> availableShips = new HashMap<>();
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String playerName = JOptionPane.showInputDialog(null, 
                "Enter your name:", 
                "Battleship Demo", 
                JOptionPane.QUESTION_MESSAGE);
                
            if (playerName == null || playerName.trim().isEmpty()) {
                playerName = "Player";
            }
            
            new BattleshipDemo(playerName);
        });
    }
    
    public BattleshipDemo(String playerName) {
        super("Battleship Demo");
        
        // Initialize game components
        humanPlayer = new Player(playerName);
        computerPlayer = new Player("Computer");
        gameTime = new GameTime();
        
        // Initialize available ships
        availableShips.put("Submarine", true);
        availableShips.put("Destroyer", true);
        availableShips.put("Ferry", true);
        availableShips.put("Aircraft Carrier", true);
        
        // Place computer ships randomly
        placeComputerShips();
        
        // Initialize GUI
        initializeGUI();
        
        // Start game timer
        gameTime.startTime();
        startTimer();
        
        addToLogArea("Welcome to Battleship! Place your ships to begin.");
    }
    
    private void initializeGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setResizable(false);
        
        mainPanel = new JPanel(new BorderLayout());
        
        // Info panel (top)
        infoPanel = new JPanel(new BorderLayout());
        JPanel playerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        playerInfoLabel = new JLabel("Player: " + humanPlayer.getName());
        opponentInfoLabel = new JLabel("Opponent: Computer");
        gameTimeLabel = new JLabel("Time: 00:00");
        turnLabel = new JLabel("Status: Place your ships");
        
        playerPanel.add(playerInfoLabel);
        playerPanel.add(opponentInfoLabel);
        playerPanel.add(gameTimeLabel);
        
        JPanel coinPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        coinLabel = new JLabel("Coins: " + humanPlayer.getCoins());
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
        
        // Ship selection panel with fixed layout
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
        setLocationRelativeTo(null);
        setVisible(true);
        
        // Initially disable attack grid during setup
        setAttackGridEnabled(false);
    }
    
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
    
    private void selectShip(Ship ship) {
        String shipName = ship.getName();
        if (availableShips.get(shipName)) {
            selectedShip = ship;
            addToLogArea("Selected " + shipName + " (" + ship.getLength() + " units)");
        } else {
            addToLogArea("You've already placed your " + shipName);
        }
    }
    
    private void handleDefenseClick(int row, int col) {
        if (setupPhase && selectedShip != null) {
            boolean placed = humanPlayer.getDefenseBoard().placeShip(selectedShip, row, col, isHorizontal);
            if (placed) {
                updateDefenseBoard();
                addToLogArea(selectedShip.getName() + " placed successfully");
                availableShips.put(selectedShip.getName(), false);
                selectedShip = null;
                
                // Check if all ships are placed
                if (!availableShips.containsValue(true)) {
                    setupPhase = false;
                    playerTurn = true;
                    setAttackGridEnabled(true);
                    addToLogArea("All ships placed. Game starts! Your turn to fire.");
                    turnLabel.setText("Status: Your turn");
                }
            } else {
                addToLogArea("Invalid ship placement! Try again.");
            }
        }
    }
    
    private void handleAttackClick(int row, int col) {
        if (!setupPhase && playerTurn) {
            // Check if the cell has already been targeted
            if (humanPlayer.getAttackBoard().getCell(row, col) != '~') {
                addToLogArea("You've already fired at this location. Try again.");
                return;
            }
            
            boolean hit = humanPlayer.fireShot(computerPlayer, row, col);
            updateAttackBoard();
            
            if (hit) {
                addToLogArea("Hit at (" + row + ", " + col + ")!");
                Ship hitShip = computerPlayer.getDefenseBoard().getShipAt(row, col);
                if (hitShip != null && hitShip.isSunk()) {
                    addToLogArea("You sunk the " + hitShip.getName() + "!");
                    humanPlayer.addCoins(hitShip.getLength() * 10);
                    updateCoinLabel();
                    
                    // Check for victory
                    boolean allSunk = true;
                    for (Ship ship : computerPlayer.getDefenseBoard().getShips()) {
                        if (!ship.isSunk()) {
                            allSunk = false;
                            break;
                        }
                    }
                    
                    if (allSunk) {
                        gameOver(true);
                        return;
                    }
                }
            } else {
                addToLogArea("Miss at (" + row + ", " + col + ")");
            }
            
            // Computer's turn
            playerTurn = false;
            turnLabel.setText("Status: Computer's turn");
            
            // Delay the computer's move slightly for better UX
            Timer computerMoveTimer = new Timer(1000, e -> computerMove());
            computerMoveTimer.setRepeats(false);
            computerMoveTimer.start();
        }
    }
    
    private void computerMove() {
        if (setupPhase) return;
        
        int row, col;
        char cell;
        
        // Simple AI - try to find adjacent cells to hits, or random if none
        List<int[]> adjacentToHits = findAdjacentToHits();
        
        if (!adjacentToHits.isEmpty()) {
            // Pick one of the adjacent cells to hits
            int[] coords = adjacentToHits.get(new Random().nextInt(adjacentToHits.size()));
            row = coords[0];
            col = coords[1];
        } else {
            // Random shot
            do {
                row = new Random().nextInt(BOARD_SIZE);
                col = new Random().nextInt(BOARD_SIZE);
                cell = computerPlayer.getAttackBoard().getCell(row, col);
            } while (cell == 'X' || cell == 'O'); // Already fired here
        }
        
        addToLogArea("Computer fires at (" + row + ", " + col + ")");
        
        boolean hit = computerPlayer.fireShot(humanPlayer, row, col);
        updateDefenseBoard();
        
        if (hit) {
            addToLogArea("Computer hit your ship at (" + row + ", " + col + ")!");
            Ship hitShip = humanPlayer.getDefenseBoard().getShipAt(row, col);
            if (hitShip != null && hitShip.isSunk()) {
                addToLogArea("Computer sunk your " + hitShip.getName() + "!");
                
                // Check for defeat
                boolean allSunk = true;
                for (Ship ship : humanPlayer.getDefenseBoard().getShips()) {
                    if (!ship.isSunk()) {
                        allSunk = false;
                        break;
                    }
                }
                
                if (allSunk) {
                    gameOver(false);
                    return;
                }
            }
        } else {
            addToLogArea("Computer missed!");
        }
        
        // Player's turn
        playerTurn = true;
        turnLabel.setText("Status: Your turn");
    }
    
    private List<int[]> findAdjacentToHits() {
        List<int[]> candidates = new ArrayList<>();
        
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                // If this is a hit
                if (computerPlayer.getAttackBoard().getCell(i, j) == 'X') {
                    // Check adjacent cells
                    int[][] directions = {{0,1}, {1,0}, {0,-1}, {-1,0}};
                    for (int[] dir : directions) {
                        int newRow = i + dir[0];
                        int newCol = j + dir[1];
                        
                        // Check if valid position and not already fired at
                        if (newRow >= 0 && newRow < BOARD_SIZE && 
                            newCol >= 0 && newCol < BOARD_SIZE &&
                            computerPlayer.getAttackBoard().getCell(newRow, newCol) == '~') {
                            candidates.add(new int[]{newRow, newCol});
                        }
                    }
                }
            }
        }
        
        return candidates;
    }
    
    private void updateDefenseBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                char cell = humanPlayer.getDefenseBoard().getCell(i, j);
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
    
    private void updateAttackBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                char cell = humanPlayer.getAttackBoard().getCell(i, j);
                if (cell == 'X') {
                    attackCells[i][j].setBackground(Color.RED);
                } else if (cell == 'O') {
                    attackCells[i][j].setBackground(Color.WHITE);
                } else {
                    attackCells[i][j].setBackground(Color.BLUE);
                }
            }
        }
    }
    
    private void setAttackGridEnabled(boolean enabled) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                attackCells[i][j].setEnabled(enabled);
            }
        }
    }
    
    private void placeComputerShips() {
        Ship[] ships = {
            new Submarine(),
            new Destroyer(),
            new Ferry(),
            new AircraftCarrier()
        };
        
        for (Ship ship : ships) {
            boolean placed = false;
            while (!placed) {
                int row = new Random().nextInt(BOARD_SIZE);
                int col = new Random().nextInt(BOARD_SIZE);
                boolean horizontal = new Random().nextBoolean();
                
                placed = computerPlayer.getDefenseBoard().placeShip(ship, row, col, horizontal);
            }
        }
    }
    
    private void addToLogArea(String message) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String timestamp = sdf.format(new Date());
        logArea.append("[" + timestamp + "] " + message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
    
    private void updateCoinLabel() {
        coinLabel.setText("Coins: " + humanPlayer.getCoins());
    }
    
    private void startTimer() {
        timer = new Timer(1000, e -> {
            gameTimeLabel.setText("Time: " + gameTime.getFormattedTime());
        });
        timer.start();
    }
    
    private void gameOver(boolean playerWon) {
        gameTime.stopTime();
        timer.stop();
        setAttackGridEnabled(false);
        
        if (playerWon) {
            humanPlayer.addCoins(100); // Victory bonus
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
    
    private void exitGame() {
        int option = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to exit the game?",
            "Exit Game",
            JOptionPane.YES_NO_OPTION);
            
        if (option == JOptionPane.YES_OPTION) {
            if (timer != null) timer.stop();
            dispose();
            System.exit(0);
        }
    }
    
    // Ship class and subclasses
    abstract static class Ship {
        protected int length;
        protected int health;
        protected String name;
        protected boolean isSunk;
        protected List<int[]> coordinates;

        public Ship(String name, int length) {
            this.name = name;
            this.length = length;
            this.health = length;
            this.isSunk = false;
            this.coordinates = new ArrayList<>();
        }

        public boolean isHit(int row, int col) {
            for (int[] coord : coordinates) {
                if (coord[0] == row && coord[1] == col) {
                    health--;
                    if (health <= 0) {
                        isSunk = true;
                    }
                    return true;
                }
            }
            return false;
        }

        public boolean isSunk() {
            return isSunk;
        }

        public String getName() {
            return name;
        }

        public int getLength() {
            return length;
        }

        public void setCoordinates(List<int[]> coordinates) {
            this.coordinates = coordinates;
        }

        public List<int[]> getCoordinates() {
            return coordinates;
        }
    }

    static class Submarine extends Ship {
        public Submarine() {
            super("Submarine", 2);
        }
    }

    static class AircraftCarrier extends Ship {
        public AircraftCarrier() {
            super("Aircraft Carrier", 5);
        }
    }

    static class Destroyer extends Ship {
        public Destroyer() {
            super("Destroyer", 3);
        }
    }

    static class Ferry extends Ship {
        public Ferry() {
            super("Ferry", 4);
        }
    }

    // Board class and subclasses
    abstract static class Board {
        protected static final int BOARD_SIZE = 10;
        protected char[][] grid;
        
        public Board() {
            grid = new char[BOARD_SIZE][BOARD_SIZE];
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    grid[i][j] = '~'; // Water
                }
            }
        }
        
        public char getCell(int row, int col) {
            if (row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE) {
                return grid[row][col];
            }
            return ' '; // Out of bounds
        }
        
        public void setCell(int row, int col, char value) {
            if (row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE) {
                grid[row][col] = value;
            }
        }
    }

    static class DefenseBoard extends Board {
        private List<Ship> ships;
        
        public DefenseBoard() {
            super();
            ships = new ArrayList<>();
        }
        
        public boolean placeShip(Ship ship, int startRow, int startCol, boolean isHorizontal) {
            // Check if ship placement is valid
            if (isValidPlacement(ship, startRow, startCol, isHorizontal)) {
                List<int[]> coords = new ArrayList<>();
                // Place ship on board
                if (isHorizontal) {
                    for (int i = 0; i < ship.getLength(); i++) {
                        grid[startRow][startCol + i] = 'S'; // S for ship
                        coords.add(new int[]{startRow, startCol + i});
                    }
                } else {
                    for (int i = 0; i < ship.getLength(); i++) {
                        grid[startRow + i][startCol] = 'S'; // S for ship
                        coords.add(new int[]{startRow + i, startCol});
                    }
                }
                ship.setCoordinates(coords);
                ships.add(ship);
                return true;
            }
            return false;
        }
        
        private boolean isValidPlacement(Ship ship, int startRow, int startCol, boolean isHorizontal) {
            if (isHorizontal) {
                if (startCol + ship.getLength() > BOARD_SIZE) {
                    return false;
                }
                for (int i = 0; i < ship.getLength(); i++) {
                    if (grid[startRow][startCol + i] != '~') {
                        return false;
                    }
                }
            } else {
                if (startRow + ship.getLength() > BOARD_SIZE) {
                    return false;
                }
                for (int i = 0; i < ship.getLength(); i++) {
                    if (grid[startRow + i][startCol] != '~') {
                        return false;
                    }
                }
            }
            return true;
        }
        
        public Ship getShipAt(int row, int col) {
            for (Ship ship : ships) {
                for (int[] coord : ship.getCoordinates()) {
                    if (coord[0] == row && coord[1] == col) {
                        return ship;
                    }
                }
            }
            return null;
        }
        
        public boolean receiveAttack(int row, int col) {
            if (grid[row][col] == 'S') {
                Ship ship = getShipAt(row, col);
                boolean hit = ship.isHit(row, col);
                grid[row][col] = 'X'; // X for hit
                return true;
            } else {
                grid[row][col] = 'O'; // O for miss
                return false;
            }
        }
        
        public List<Ship> getShips() {
            return ships;
        }
    }

    static class AttackBoard extends Board {
        public void markHit(int row, int col) {
            grid[row][col] = 'X'; // X for hit
        }
        
        public void markMiss(int row, int col) {
            grid[row][col] = 'O'; // O for miss
        }
    }

    // Player class
    static class Player {
        private String name;
        private DefenseBoard defenseBoard;
        private AttackBoard attackBoard;
        private int coins;
        
        public Player(String name) {
            this.name = name;
            this.defenseBoard = new DefenseBoard();
            this.attackBoard = new AttackBoard();
            this.coins = 0;
        }
        
        public String getName() {
            return name;
        }
        
        public DefenseBoard getDefenseBoard() {
            return defenseBoard;
        }
        
        public AttackBoard getAttackBoard() {
            return attackBoard;
        }
        
        public int getCoins() {
            return coins;
        }
        
        public void addCoins(int amount) {
            coins += amount;
        }
        
        public boolean fireShot(Player opponent, int row, int col) {
            boolean hit = opponent.getDefenseBoard().receiveAttack(row, col);
            if (hit) {
                attackBoard.markHit(row, col);
                Ship hitShip = opponent.getDefenseBoard().getShipAt(row, col);
                if (hitShip != null && hitShip.isSunk()) {
                    addCoins(hitShip.getLength() * 10); // Reward for sinking a ship
                }
            } else {
                attackBoard.markMiss(row, col);
            }
            return hit;
        }
    }

    // Time class
    static class GameTime {
        private long startTime;
        private long endTime;
        private boolean running;
        
        public GameTime() {
            running = false;
        }
        
        public void startTime() {
            if (!running) {
                startTime = System.currentTimeMillis();
                running = true;
            }
        }
        
        public void stopTime() {
            if (running) {
                endTime = System.currentTimeMillis();
                running = false;
            }
        }
        
        public void resetTime() {
            startTime = 0;
            endTime = 0;
            running = false;
        }
        
        public long getElapsedTime() {
            if (running) {
                return System.currentTimeMillis() - startTime;
            } else {
                return endTime - startTime;
            }
        }
        
        public String getFormattedTime() {
            long elapsedTime = getElapsedTime();
            long seconds = elapsedTime / 1000;
            long minutes = seconds / 60;
            seconds = seconds % 60;
            return String.format("%02d:%02d", minutes, seconds);
        }
    }
}
