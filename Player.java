package battleshipdemo;

/**
 * Player class to handle player data and actions
 */
public class Player {
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
    
    public void setName(String name) {
        this.name = name;
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
    
    public void earnCoins(int amount) {
        this.coins += amount;
    }
    
    public boolean fireShot(Player opponent, int row, int col) {
        boolean hit = opponent.getDefenseBoard().receiveAttack(row, col);
        if (hit) {
            attackBoard.markHit(row, col);
            Ship hitShip = opponent.getDefenseBoard().getShipAt(row, col);
            if (hitShip != null && hitShip.isSunk()) {
                earnCoins(hitShip.getLength() * 10); // Reward for sinking a ship
            }
        } else {
            attackBoard.markMiss(row, col);
        }
        return hit;
    }
}
