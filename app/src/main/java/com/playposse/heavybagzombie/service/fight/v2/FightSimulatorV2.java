package com.playposse.heavybagzombie.service.fight.v2;

import android.content.Context;

import com.playposse.heavybagzombie.service.ForegroundService;
import com.playposse.heavybagzombie.service.fight.FightStatsSaver;
import com.playposse.heavybagzombie.service.fight.impl.PunchCombination;

/**
 * The core class that ties all the components involved in simulating a fight together.
 */
public class FightSimulatorV2 implements FightTimerCallbackV2, PunchTimerCallbackV2 {

    private final FightSimulationV2 fightSimulation;
    private final ForegroundService foregroundService;

    private final FightTimerV2 fightTimer;
    private final PunchTimerV2 punchTimer;
    private final VocalQueueV2 vocalQueue;
    private final FightStatsSaver fightStatsSaver;

    public FightSimulatorV2(
            Context context,
            FightSimulationV2 fightSimulation,
            ForegroundService foregroundService) {

        this.fightSimulation = fightSimulation;
        this.foregroundService = foregroundService;

        fightStatsSaver = new FightStatsSaver(context);

        fightTimer = new FightTimerV2(
                fightSimulation.getRoundTime(),
                fightSimulation.getRestTime(),
                fightSimulation.getMaxRound(),
                fightStatsSaver,
                this);

        vocalQueue = new VocalQueueV2(context);

        punchTimer = new PunchTimerV2(context, vocalQueue, this);

        FightContextV2 fightContext =
                new FightContextV2(fightTimer, punchTimer, vocalQueue, fightStatsSaver);
        fightSimulation.init(fightContext);
    }

    public void start() {
        fightStatsSaver.resetFightStats();
        fightTimer.start();
    }

    public void stop() {
        fightTimer.stop();
        punchTimer.stop();
        fightStatsSaver.stopFight();
    }

    public void pause() {

    }

    public void unpause() {

    }

    @Override
    public void onRoundStart(int roundIndex) {
        fightSimulation.onRoundStart(roundIndex);
        scheduleNextPunchCombination();
    }

    @Override
    public void onRestStart(int restIndex) {
        boolean isFinalRound = restIndex + 1 >= fightSimulation.getMaxRound();
        fightSimulation.onRoundEnd(restIndex, isFinalRound);
    }

    @Override
    public void onLastRoundEnd() {
        fightTimer.stop();
        punchTimer.stop();
        fightStatsSaver.stopFight();
        fightSimulation.onRoundEnd(fightSimulation.getMaxRound() - 1, true);
        foregroundService.stopForeground(true);
    }

    @Override
    public void onHit(PunchCombination punchCombination) {
        if (fightTimer.isActiveRound()) {
            fightSimulation.scoreHit(punchCombination);
            scheduleNextPunchCombination();
        }
    }

    @Override
    public void onTimeout(PunchCombination punchCombination) {
        if (fightTimer.isActiveRound()) {
            fightSimulation.scoreTimeout(punchCombination);
            scheduleNextPunchCombination();
        }
    }

    @Override
    public void onMiss() {
        if (fightTimer.isActiveRound()) {
            fightSimulation.scoreMiss();
        }
    }

    private void scheduleNextPunchCombination() {
        punchTimer.schedulePunchCombination(fightSimulation.getNextPunchCombination());
    }
}
