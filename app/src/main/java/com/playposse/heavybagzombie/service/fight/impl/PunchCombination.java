package com.playposse.heavybagzombie.service.fight.impl;

import android.content.ContentValues;

import com.playposse.heavybagzombie.VocalPlayer;

import static com.playposse.heavybagzombie.provider.BagZombieContract.SaveHitAction;

/**
 * A combination of punch instructions.
 */
public class PunchCombination {

    private final VocalPlayer.Message[] commands;
    private final long delay;
    private final long individualTimeout;
    private final int[] reactionTimes;

    private int playIndex = 0;
    private int hitIndex = 0;

    private Long startTime;
    private Long endTime;

    public PunchCombination(VocalPlayer.Message[] commands, long delay, long individualTimeout) {
        this.commands = commands;
        this.delay = delay;
        this.individualTimeout = individualTimeout;

        reactionTimes = new int[4];
    }

    public void recordStartTime() {
        startTime = System.currentTimeMillis();
    }

    public void recordEndTime() {
        endTime = System.currentTimeMillis();
    }

    public VocalPlayer.Message getNextCommand() {
        if (playIndex < commands.length) {
            return commands[playIndex++];
        } else {
            return null;
        }
    }

    public void recordReactionTime() {
        reactionTimes[hitIndex++] = (int) (System.currentTimeMillis() - startTime);
    }

    public boolean canPlayMoreCommands() {
        return playIndex < commands.length;
    }

    public boolean canRecordMoreReactionTimes() {
        return hitIndex < commands.length;
    }

    public ContentValues toContentValues() {
        // Create row to send to the ContentProvider.
        ContentValues values = new ContentValues();
        values.put(SaveHitAction.COMMAND_COLUMN, getCommandString());
        values.put(SaveHitAction.OVERALL_REACTION_TIME_COLUMN, getOverallReactionTime());
        values.put(SaveHitAction.REACTION_TIME_0, reactionTimes[0]);
        values.put(SaveHitAction.REACTION_TIME_1, reactionTimes[1]);
        values.put(SaveHitAction.REACTION_TIME_2, reactionTimes[2]);
        values.put(SaveHitAction.REACTION_TIME_3, reactionTimes[3]);
        return values;
    }

    public String getCommandString() {
        StringBuilder commandBuilder = new StringBuilder();
        for (VocalPlayer.Message command : commands) {
            if (commandBuilder.length() > 0) {
                commandBuilder.append(" ");
            }
            commandBuilder.append(command.name());
        }
        return commandBuilder.toString();
    }

    public boolean hasStarted() {
        return startTime != null;
    }

    public long getOverallReactionTime() {
        return endTime - startTime;
    }

    public long getDelay() {
        return delay;
    }

    public long getIndividualTimeout() {
        return individualTimeout;
    }

    public PunchCombination getCopy(long delay, long individualTimeout) {
        return new PunchCombination(commands, delay, individualTimeout);
    }
}
