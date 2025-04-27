package battleshipgame;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BattleshipGUI extends JFrame {
    private Player humanPlayer;
    private Player computerPlayer;
    private boolean humanTurn = true;
    private JLabel statusLabel;
    private JButton[][] humanGridButtons;
    private JButton[][] computerGridButtons;
    private final int BOARD_SIZE = 10;

    public BattleshipGUI() {
        setTitle("Battleship Game");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize players
        humanPlayer = new Player("Human");
        computerPlayer = new Player("Computer");
        humanPlayer.placeShips();
        computerPlayer.placeShips();

        // Create panels
        JPanel humanPanel = createPlayerPanel(humanPlayer, true);
        JPanel computerPanel = createPlayerPanel(computerPlayer, false);
        computerPanel.setEnabled(false); // Disable computer panel initially

        // Status bar
        statusLabel = new JLabel("Your turn. Place your ships or attack computer's grid.", JLabel.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        // Main game area
        JPanel gamePanel = new JPanel(new GridLayout(1, 2));
        gamePanel.add(humanPanel);
        gamePanel.add(computerPanel);
        add(gamePanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel createPlayerPanel(Player player, boolean isHuman) {
    JPanel panel = new JPanel(new GridLayout(BOARD_SIZE + 1, BOARD_SIZE + 1));
    JButton[][] gridButtons = new JButton[BOARD_SIZE][BOARD_SIZE];

    // Create column headers (A-J)
    panel.add(new JLabel("")); // Empty corner cell
    for (int i = 0; i < BOARD_SIZE; i++) {
        JLabel label = new JLabel(Character.toString((char) ('A' + i)));
        label.setHorizontalAlignment(JLabel.CENTER); // Set alignment here
        panel.add(label);
    }

    for (int row = 0; row < BOARD_SIZE; row++) {
        // Row number (1-10)
        JLabel rowLabel = new JLabel(Integer.toString(row + 1));
        rowLabel.setHorizontalAlignment(JLabel.CENTER);
        panel.add(rowLabel);

        for (int col = 0; col < BOARD_SIZE; col++) {
            final int r = row;
            final int c = col;
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(40, 40));
            gridButtons[row][col] = button;

            if (isHuman) {
                // Human's own board (show ships)
                if (player.getDefenseBoard().grid[row][col] == 'S') {
                    button.setBackground(Color.GRAY);
                } else {
                    button.setBackground(Color.BLUE);
                }
            } else {
                // Computer's board (hidden)
                button.setBackground(Color.BLUE);
                button.addActionListener(e -> handleAttack(r, c));
            }

            panel.add(button);
        }
    }

    if (isHuman) {
        humanGridButtons = gridButtons;
    } else {
        computerGridButtons = gridButtons;
    }

    return panel;
}

    private void handleAttack(int row, int col) {
        if (!humanTurn) return;

        boolean hit = humanPlayer.attack(computerPlayer, row, col);
        updateButtonAfterAttack(computerGridButtons, row, col, hit);
        
        if (hit) {
            statusLabel.setText("Hit! Computer's turn.");
            computerGridButtons[row][col].setBackground(Color.RED);
        } else {
            statusLabel.setText("Miss! Computer's turn.");
            computerGridButtons[row][col].setBackground(Color.WHITE);
        }

        humanTurn = false;
        computerTurn();
    }

    private void computerTurn() {
        // Simple AI - random attack
        int row, col;
        do {
            row = (int) (Math.random() * BOARD_SIZE);
            col = (int) (Math.random() * BOARD_SIZE);
        } while (humanPlayer.getDefenseBoard().grid[row][col] == 'X' || 
                 humanPlayer.getDefenseBoard().grid[row][col] == 'O');

        boolean hit = computerPlayer.attack(humanPlayer, row, col);
        updateButtonAfterAttack(humanGridButtons, row, col, hit);

        if (hit) {
            statusLabel.setText("Computer hit your ship at " + (char)('A'+col) + (row+1));
            humanGridButtons[row][col].setBackground(Color.RED);
        } else {
            statusLabel.setText("Computer missed at " + (char)('A'+col) + (row+1));
            humanGridButtons[row][col].setBackground(Color.WHITE);
        }

        humanTurn = true;
        checkGameOver();
    }

    private void updateButtonAfterAttack(JButton[][] buttons, int row, int col, boolean hit) {
        buttons[row][col].setEnabled(false);
        buttons[row][col].setText(hit ? "X" : "O");
    }

    private void checkGameOver() {
        if (humanPlayer.hasLost()) {
            JOptionPane.showMessageDialog(this, "Computer wins! Game over.");
            System.exit(0);
        } else if (computerPlayer.hasLost()) {
            JOptionPane.showMessageDialog(this, "You win! Congratulations!");
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BattleshipGUI());
    }
}
