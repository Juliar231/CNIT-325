package battleshipgame;


import java.time.*;

public class GameTime {
    private Instant startTime;
    private Duration duration;
    
    public void start() {
        startTime = Instant.now();
    }
    
    public void stop() {
        duration = Duration.between(startTime, Instant.now());
    }
    
    public String getDuration() {
        if (duration == null) {
            return "Game still in progress";
        }
        return String.format("%d minutes %d seconds", 
               duration.toMinutesPart(), duration.toSecondsPart());
    }
}
