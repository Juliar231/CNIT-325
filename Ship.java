package battleshipdemo;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract Ship class that provides base functionality for all ship types
 */
public abstract class Ship {
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
    
    public int getHealth(){
        return health;
    }
}
