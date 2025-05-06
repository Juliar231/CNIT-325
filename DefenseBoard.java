package battleshipdemo;

import java.util.ArrayList;
import java.util.List;

/**
 * Defense board class - the board where a player places their ships
 */
public class DefenseBoard extends Board {
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
        
        System.out.println("Defense board receiving attack at: " + row + "," + col + ", current cell value: " + grid[row][col]);
        
        if (grid[row][col] == 'S') {
            Ship ship = getShipAt(row, col);
            if (ship != null){
                ship.isHit(row, col);
                System.out.println("Ship hit: " + ship.getName() + ", remaining health: " + ship.getHealth());
                grid[row][col] = 'X';
                return true;
            }
            
            System.out.println("ERROR: Ship cell with no ship object");
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
