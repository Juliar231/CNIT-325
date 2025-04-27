package battleshipgame;


import java.util.ArrayList;
import java.util.List;

public class Board {
    protected static final int SIZE = 10;
    protected char[][] grid;
    protected List<Ship> ships;
    
    public Board() {
        grid = new char[SIZE][SIZE];
        ships = new ArrayList<>();
        initializeGrid();
    }
    
    private void initializeGrid() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                grid[i][j] = '~';
            }
        }
    }
    
    public boolean placeShip(Ship ship, int x, int y, boolean horizontal) {
        // Check boundaries
        if (x < 0 || y < 0 || x >= SIZE || y >= SIZE) {
            return false;
        }
        
        // Check if ship fits
        if (horizontal) {
            if (y + ship.getSize() > SIZE) return false;
            for (int i = 0; i < ship.getSize(); i++) {
                if (grid[x][y + i] != '~') return false;
            }
        } else {
            if (x + ship.getSize() > SIZE) return false;
            for (int i = 0; i < ship.getSize(); i++) {
                if (grid[x + i][y] != '~') return false;
            }
        }
        
        // Place the ship
        for (int i = 0; i < ship.getSize(); i++) {
            if (horizontal) {
                grid[x][y + i] = 'S';
            } else {
                grid[x + i][y] = 'S';
            }
        }
        ships.add(ship);
        return true;
    }
    
    public List<Ship> getShips() {
        return ships;
    }
}
