package playposse.com.heavybagzombie.provider;

public class HitRecord {

    private String command;
    private int delay;

    public HitRecord(String command, int delay) {
        this.command = command;
        this.delay = delay;
    }

    public String getCommand() {
        return command;
    }

    public int getDelay() {
        return delay;
    }
}
