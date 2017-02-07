package playposse.com.heavybagzombie.provider;

public class HitRecord {

    private String command;
    private int overallReactionTime;
    private Integer[] reactionTimes;

    public HitRecord(
            String command,
            int overallReactionTime,
            Integer reactionTime0,
            Integer reactionTime1,
            Integer reactionTime2,
            Integer reactionTime3) {

        this.command = command;
        this.overallReactionTime = overallReactionTime;
        this.reactionTimes = new Integer[4];

        reactionTimes[0] = reactionTime0;
        reactionTimes[1] = reactionTime1;
        reactionTimes[2] = reactionTime2;
        reactionTimes[3] = reactionTime3;
    }

    public String getCommand() {
        return command;
    }

    public int getOverallReactionTime() {
        return overallReactionTime;
    }

    public Integer[] getReactionTimes() {
        return reactionTimes;
    }
}
