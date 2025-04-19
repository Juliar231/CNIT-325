
//Class Definition (these are the vanilla ships without upgrades)
 /*Attribute
 
 -size
 -direction
 -start point
 -list of ship grid points

  Methods
  -getHit
  -getsunk
  


 
   */
import java.util.*;

public class Ship {
    // Attributes
    private int size; // Size of the ship (e.g., number of grid points it occupies)
    private String direction; // Direction of the ship ("horizontal" or "vertical")
    private int[] startPoint; // The starting point (row, column) where the ship is placed
    private List<int[]> shipGridPoints; // List of grid points where the ship occupies
    
    private int health; // The number of hits the ship can take before sinking

    // Constructor for creating a ship
    public Ship(int size, String direction, int[] startPoint) {
        this.size = size;
        this.direction = direction;
        this.startPoint = startPoint;
        this.shipGridPoints = new ArrayList<>();
        this.health = size; // Health starts equal to size (every hit reduces health)
        
        // Calculate ship grid points based on the direction and size
        calculateShipGridPoints();
    }

    // Method to calculate the ship's grid points based on its direction and starting point
    private void calculateShipGridPoints() {
        int row = startPoint[0]; // Starting row
        int col = startPoint[1]; // Starting column
        
        for (int i = 0; i < size; i++) {
            if (direction.equalsIgnoreCase("horizontal")) {
                shipGridPoints.add(new int[] {row, col + i}); // Add horizontal grid points
            } else if (direction.equalsIgnoreCase("vertical")) {
                shipGridPoints.add(new int[] {row + i, col}); // Add vertical grid points
            }
        }
    }

    // Method to register a hit on the ship
    public boolean getHit(int row, int col) {
        for (int[] gridPoint : shipGridPoints) {
            if (gridPoint[0] == row && gridPoint[1] == col) {
                health--; // Decrease health if the ship is hit
                shipGridPoints.remove(gridPoint); // Remove the hit grid point
                return true; // Ship was hit
            }
        }
        return false; // Ship was not hit at the given coordinates
    }

    // Method to check if the ship is sunk (health reaches zero)
    public boolean isSunk() {
        return health <= 0; // The ship is sunk if health is zero or less
    }

    // Getter for size
    public int getSize() {
        return size;
    }

    // Getter for direction
    public String getDirection() {
        return direction;
    }

    // Getter for startPoint
    public int[] getStartPoint() {
        return startPoint;
    }

    // Getter for the ship's grid points
    public List<int[]> getShipGridPoints() {
        return shipGridPoints;
    }
}
