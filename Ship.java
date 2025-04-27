package battleshipgame;


public abstract class Ship {
    protected int size;
    protected int hits;
    protected String name;
    
    public Ship(String name, int size) {
        this.name = name;
        this.size = size;
        this.hits = 0;
    }
    
    public boolean isSunk() {
        return hits >= size;
    }
    
    public void hit() {
        hits++;
    }
    
    public String getName() {
        return name;
    }
    
    public int getSize() {
        return size;
    }
}
