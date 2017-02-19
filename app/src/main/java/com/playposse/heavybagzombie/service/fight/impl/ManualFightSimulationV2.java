package com.playposse.heavybagzombie.service.fight.impl;

import android.util.Log;

import java.util.List;
import java.util.Random;

import com.playposse.heavybagzombie.VocalPlayer;
import com.playposse.heavybagzombie.service.fight.v2.FightContextV2;
import com.playposse.heavybagzombie.service.fight.v2.FightSimulationV2;

/**
 * A fight that issues random commands.
 */
public class ManualFightSimulationV2 implements FightSimulationV2 {

    private static final String LOG_CAT = ManualFightSimulationV2.class.getSimpleName();

//    public static final int MAX_COMMAND_DELAY = 3_000;
//    public static final int COMMAND_TIMEOUT = 1_500;

    public static final Random RANDOM = new Random();

    private final List<PunchCombination> punchCombinations;
    private final long roundTime;
    private final long restTime;
    private final int maxRound;
    private final long maxDelay;
    private final long individualTimeout;
    private final long heavyTimeout;

    private FightContextV2 fightContext;

    public ManualFightSimulationV2(
            List<PunchCombination> punchCombinations,
            long roundTime,
            long restTime,
            int maxRound,
            long maxDelay,
            long individualTimeout,
            long heavyTimeout) {

        this.punchCombinations = punchCombinations;
        this.roundTime = roundTime;
        this.restTime = restTime;
        this.maxRound = maxRound;
        this.maxDelay = maxDelay;
        this.individualTimeout = individualTimeout;
        this.heavyTimeout = heavyTimeout;
    }

    @Override
    public void init(FightContextV2 fightContext) {
        this.fightContext = fightContext;
    }

    @Override
    public long getRoundTime() {
        return roundTime;
    }

    @Override
    public long getRestTime() {
        return restTime;
    }

    @Override
    public int getMaxRound() {
        return maxRound;
    }

    @Override
    public PunchCombination getNextPunchCombination() {
        // Taking a random number of a random number creates a non-uniform distribution. Generally,
        // a fight should have a lot of quick hits with some random outliers that wait longer.
        int number = RANDOM.nextInt((int) maxDelay);
        long delay = RANDOM.nextInt(number);

        int randomIndex = RANDOM.nextInt(punchCombinations.size());
        PunchCombination punchTemplate = punchCombinations.get(randomIndex);
        PunchCombination punchInstance = punchTemplate.getCopy(delay, individualTimeout);
        Log.i(LOG_CAT, "Generated next punch combination: " + punchInstance.getCommandString()
                + " " + punchInstance.hashCode());
        return punchInstance;
    }

    @Override
    public void scoreHit(PunchCombination punchCombination) {
        Log.i(LOG_CAT, "Scored hit for " + punchCombination.getCommandString());
        fightContext.getFightStatsSaver().saveHit(punchCombination);
        if (punchCombination.getOverallReactionTime() <= heavyTimeout) {
            fightContext.getVocalQueue().scheduleVocal(VocalPlayer.Message.heavy);
        } else {
            fightContext.getVocalQueue().scheduleVocal(VocalPlayer.Message.hit);
        }
    }

    @Override
    public void scoreTimeout(PunchCombination punchCombination) {
        fightContext.getVocalQueue().scheduleVocal(VocalPlayer.Message.tooSlow);
        fightContext.getFightStatsSaver().saveTimeout(punchCombination.getCommandString());
    }

    @Override
    public void scoreMiss() {
        Log.i(LOG_CAT, "Scored miss");
        fightContext.getVocalQueue().scheduleVocal(VocalPlayer.Message.miss);
        fightContext.getFightStatsSaver().saveMiss();
    }

    @Override
    public void onRoundStart(int roundIndex) {
        fightContext.getVocalQueue().scheduleVocal(VocalPlayer.Message.readyFight);
        // TODO: start round in content provider.
    }

    @Override
    public void onRoundEnd(int roundIndex, boolean isFinalRound) {
        fightContext.getVocalQueue().scheduleVocal(VocalPlayer.Message.stop);
        // TODO: end round in content provider.
    }
}
