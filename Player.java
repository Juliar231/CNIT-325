package battleshipgame;


public class Player {
    private String name;
    private Board defenseBoard;
    private AttackBoard attackBoard;
    private int coins;
    
    public Player(String name) {
        this.name = name;
        this.defenseBoard = new Board();
        this.attackBoard = new AttackBoard();
        this.coins = 0;
    }
    
    public void placeShips() {
        defenseBoard.placeShip(new Submarine(), 0, 0, true);
        defenseBoard.placeShip(new Destroyer(), 2, 2, false);
        defenseBoard.placeShip(new Ferry(), 4, 3, true);
        defenseBoard.placeShip(new AircraftCarrier(), 1, 1, false);
    }
    
    public boolean attack(Player opponent, int x, int y) {
        boolean hit = attackBoard.attack(x, y, opponent.defenseBoard);
        if (hit) {
            coins += 10;
        }
        return hit;
    }
    
    public String getName() {
        return name;
    }
    
    public int getCoins() {
        return coins;
    }
    
    public Board getDefenseBoard() {
        return defenseBoard;
    }
    
    public boolean hasLost() {
        for (Ship ship : defenseBoard.getShips()) {
            if (!ship.isSunk()) {
                return false;
            }
        }
        return true;
    }
}
