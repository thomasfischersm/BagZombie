package com.playposse.heavybagzombie.service.fight.impl;

import android.util.Log;

import java.util.Random;

import com.playposse.heavybagzombie.VocalPlayer;
import com.playposse.heavybagzombie.service.fight.AbstractFightSimulation;

/**
 * A fight that issues random commands.
 */
public class RandomFightSimulation extends AbstractFightSimulation {

    private static final String LOG_CAT = RandomFightSimulation.class.getSimpleName();

    public static final int MAX_COMMAND_DELAY = 3_000;
    public static final int COMMAND_TIMEOUT = 1_500;

    public static final Random RANDOM = new Random();

    public RandomFightSimulation(long duration) {
        super(duration);
    }

    @Override
    protected void onFightStart() {
        playSound(VocalPlayer.Message.readyFight);
        scheduleRandomCommand();
    }

    @Override
    protected void onScoreHit(PunchCombination punchCombination) {
        Log.i(LOG_CAT, "Scored hit for " + punchCombination);
//        getFightStatsSaver().saveHit(punchCombination); // TODO: outdated code
        if (punchCombination.getOverallReactionTime() < 500) {
            playSound(VocalPlayer.Message.heavy);
        } else {
            playSound(VocalPlayer.Message.hit);
        }
        scheduleRandomCommand();
    }

    @Override
    protected void onScoreMiss() {
        Log.i(LOG_CAT, "Scored miss");
        playSound(VocalPlayer.Message.miss);
        getFightStatsSaver().saveMiss();
    }

    @Override
    protected void onFightDone() {
        playSound(VocalPlayer.Message.stop);
    }

    @Override
    protected void onScoreTimeout(PunchCombination punchCombination) {
        playSound(VocalPlayer.Message.tooSlow);
        getFightStatsSaver().saveTimeout(punchCombination.getCommandString());
        scheduleRandomCommand();
    }

    @Override
    protected void onFightAborted() {
        playSound(VocalPlayer.Message.stop);
    }

    private void scheduleRandomCommand() {
        if (!isFightActive()) {
            return;
        }

        // Taking a random number of a random number creates a non-uniform distribution. Generally,
        // a fight should have a lot of quick hits with some random outliers that wait longer.
        int number = RANDOM.nextInt(MAX_COMMAND_DELAY);
        long delay = RANDOM.nextInt(number);

        VocalPlayer.Message command = pickRandomPunch();
        VocalPlayer.Message[] commands = {command};
        // DYSFUNCTIONAL
//        PunchCombination punchCombination = new PunchCombination(commands, delay, individualTimeout);

//        scheduleCommand(punchCombination, delay, delay + (COMMAND_TIMEOUT));
//        Log.i(LOG_CAT, "Issued command " + punchCombination.getCommandString());
    }

    private VocalPlayer.Message pickRandomPunch() {
        VocalPlayer.Message command;
        switch (new Random().nextInt(6)) {
            case 0:
                command = VocalPlayer.Message.one;
                break;
            case 1:
                command = VocalPlayer.Message.two;
                break;
            case 2:
                command = VocalPlayer.Message.three;
                break;
            case 3:
                command = VocalPlayer.Message.four;
                break;
            case 4:
                command = VocalPlayer.Message.five;
                break;
            case 5:
                command = VocalPlayer.Message.six;
                break;
            default:
                command = VocalPlayer.Message.one;
                break;
        }
        return command;
    }
}
