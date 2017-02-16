package com.playposse.heavybagzombie.service.fight.impl;

import android.content.ContentValues;
import android.util.Log;

import com.playposse.heavybagzombie.VocalPlayer;

import java.util.ArrayList;
import java.util.List;

import static com.playposse.heavybagzombie.provider.BagZombieContract.SaveHitAction;

/**
 * A combination of punch instructions.
 */
public class PunchCombination {

    private static final String LOG_CAT = PunchCombination.class.getSimpleName();

    public static final String SPACE_SEPERATOR = " ";

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
        Log.i(LOG_CAT, "getNextCommand() PunchCombination state: " + getCommandString()
                + " playIndex " + playIndex + ", length " + commands.length);
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
        Log.i(LOG_CAT, "canPlayMoreCommands() PunchCombination state: " + getCommandString()
                + " playIndex " + playIndex + ", length " + commands.length);
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

    public static List<PunchCombination> toList(String[] punchCombinationStrs) {
        List<PunchCombination> punchCombinations = new ArrayList<>(punchCombinationStrs.length);
        for (String str : punchCombinationStrs) {
            String[] digits = str.split(SPACE_SEPERATOR);
            VocalPlayer.Message[] commands = new VocalPlayer.Message[digits.length];
            for (int i = 0; i < digits.length; i++) {
                commands[i] = parseCommand(digits[i]);
            }
            punchCombinations.add(new PunchCombination(commands, 0, 0));
        }
        return punchCombinations;
    }

    private static VocalPlayer.Message parseCommand(String digit) {
        VocalPlayer.Message command;
        switch (digit) {
            case "1":
                command = VocalPlayer.Message.one;
                break;
            case "2":
                command = VocalPlayer.Message.two;
                break;
            case "3":
                command = VocalPlayer.Message.three;
                break;
            case "4":
                command = VocalPlayer.Message.four;
                break;
            case "5":
                command = VocalPlayer.Message.five;
                break;
            case "6":
                command = VocalPlayer.Message.six;
                break;
            default:
                command = VocalPlayer.Message.one;
                break;
        }
        return command;
    }
}
