package battleshipdemo;

/**
 * Attack board class - the board where a player tracks shots against the opponent
 */
public class AttackBoard extends Board {
    public void markHit(int row, int col) {
        System.out.println("Marking hit at: " + row + "," + col);
        grid[row][col] = 'X'; // X for hit
    }
    
    public void markMiss(int row, int col) {
        System.out.println("Marking miss at: " + row + "," + col);
        grid[row][col] = 'O'; // O for miss
    }
    
    public void markPending(int row, int col) {
        System.out.println("Marking pending shot at: " + row + "," + col);
        grid[row][col] = 'P'; // P for pending result
    }
}
