package battleshipdemo;

/**
 * Abstract Board class that is extended by AttackBoard and DefenseBoard
 */
public abstract class Board {
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
