package playposse.com.heavybagzombie.service.fight.v2;

import android.content.Context;

import playposse.com.heavybagzombie.service.fight.FightStatsSaver;
import playposse.com.heavybagzombie.service.fight.impl.PunchCombination;

/**
 * The core class that ties all the components involved in simulating a fight together.
 */
public class FightSimulatorV2 implements FightTimerCallbackV2, PunchTimerCallbackV2 {

    private final FightSimulationV2 fightSimulation;

    private final FightTimerV2 fightTimer;
    private final PunchTimerV2 punchTimer;
    private final VocalQueueV2 vocalQueue;
    private final FightStatsSaver fightStatsSaver;

    public FightSimulatorV2(Context context, FightSimulationV2 fightSimulation) {
        this.fightSimulation = fightSimulation;

        fightTimer = new FightTimerV2(
                fightSimulation.getRoundTime(),
                fightSimulation.getRestTime(),
                fightSimulation.getMaxRound(),
                this);

        vocalQueue = new VocalQueueV2(context);

        punchTimer = new PunchTimerV2(context, vocalQueue, this);

        fightStatsSaver = new FightStatsSaver(context);

        FightContextV2 fightContext =
                new FightContextV2(fightTimer, punchTimer, vocalQueue, fightStatsSaver);
        fightSimulation.init(fightContext);
    }

    public void start() {
        fightTimer.start();
    }

    public void stop() {
        fightTimer.stop();
        punchTimer.stop();
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
    }

    @Override
    public void onHit(PunchCombination punchCombination) {
        fightSimulation.scoreHit(punchCombination);
        scheduleNextPunchCombination();
    }

    @Override
    public void onTimeout(PunchCombination punchCombination) {
        fightSimulation.scoreTimeout(punchCombination);
    }

    @Override
    public void onMiss() {
        fightSimulation.scoreMiss();
    }

    private void scheduleNextPunchCombination() {
        punchTimer.schedulePunchCombination(fightSimulation.getNextPunchCombination());
    }
}
