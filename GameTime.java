package battleshipdemo;

/**
 * GameTime class to track and manage game time
 */
public class GameTime {
    private long startTime;
    private long endTime;
    private boolean running;
    
    public GameTime() {
        running = false;
    }
    
    public void startTime() {
        if (!running) {
            startTime = System.currentTimeMillis();
            running = true;
        }
    }
    
    public void stopTime() {
        if (running) {
            endTime = System.currentTimeMillis();
            running = false;
        }
    }
    
    public void resetTime() {
        startTime = 0;
        endTime = 0;
        running = false;
    }
    
    public long getElapsedTime() {
        if (running) {
            return System.currentTimeMillis() - startTime;
        } else {
            return endTime - startTime;
        }
    }
    
    public String getFormattedTime() {
        long elapsedTime = getElapsedTime();
        long seconds = elapsedTime / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
