package battleshipgame;


public class CoinSystem implements Rewards {
    private int balance;
    
    public CoinSystem() {
        this.balance = 0;
    }
    
    @Override
    public void earnCoins(int amount) {
        balance += amount;
    }
    
    @Override
    public int checkBalance() {
        return balance;
    }
}
