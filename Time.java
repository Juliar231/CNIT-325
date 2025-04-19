import java.util.*;

public class Time {
    private long startTime;
    private long endTime;

    public void startTime() {
        startTime = System.currentTimeMillis();
    }

    public void stopTime() {
        endTime = System.currentTimeMillis();
    }

    public void resetTime() {
        startTime = 0;
        endTime = 0;
    }

    public long getGameDuration() {
        return endTime - startTime;
    }
}
