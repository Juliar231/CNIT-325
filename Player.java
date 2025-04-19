public class Player {
    private String name;
    private int coins;

    public Player(String name) {
        this.name = name;
        this.coins = 0;
    }

    public void earnCoins(int amount) {
        coins += amount;
    }

    public int getCoins() {
        return coins;
    }

    public String getName() {
        return name;
    }
}
