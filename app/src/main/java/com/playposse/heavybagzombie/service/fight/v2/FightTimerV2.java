package com.playposse.heavybagzombie.service.fight.v2;

import android.util.Log;

import com.playposse.heavybagzombie.service.fight.FightStatsSaver;

import java.util.Timer;
import java.util.TimerTask;

import static com.playposse.heavybagzombie.provider.BagZombieContract.UpdateFightStateAction.ACTIVE_FIGHT_STATE;
import static com.playposse.heavybagzombie.provider.BagZombieContract.UpdateFightStateAction.NO_FIGHT_STATE;
import static com.playposse.heavybagzombie.provider.BagZombieContract.UpdateFightStateAction.REST_FIGHT_STATE;

/**
 * Timekeeper for the fight and its rounds.
 */
public class FightTimerV2 {

    private static final String LOG_CAT = FightTimerV2.class.getSimpleName();

    private final FightStatsSaver fightStatsSaver;
    private final long roundTime;
    private final long restTime;
    private final int maxRound;
    private final FightTimerCallbackV2 callback;

    private final Timer timer = new Timer();

    private int currentRoundIndex = -1;
    private boolean isActive = false;
    private boolean isResting = false;
    private boolean isPaused = false;
    private int secondsSincePeriodStart = 0;

    public FightTimerV2(
            long roundTime,
            long restTime,
            int maxRound,
            FightStatsSaver fightStatsSaver,
            FightTimerCallbackV2 callback) {

        this.roundTime = roundTime;
        this.restTime = restTime;
        this.maxRound = maxRound;
        this.fightStatsSaver = fightStatsSaver;
        this.callback = callback;
    }

    public void start() {
        startRound();

        timer.scheduleAtFixedRate(
                new TimerTask() {
                    @Override
                    public void run() {
                        updateContentProvider();
                        secondsSincePeriodStart++;
                    }
                },
                0,
                1_000);
    }

    public void stop() {
        timer.cancel();
    }

    public void pause() {

    }

    public void resume() {

    }

    private void startRound() {
        Log.i(LOG_CAT, "Starting round " + (currentRoundIndex + 1));
        currentRoundIndex++;
        isActive = true;
        isResting = false;
        secondsSincePeriodStart = 0;

        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        if (currentRoundIndex + 1 < maxRound) {
                            startRest();
                        } else {
                            Log.i(LOG_CAT, "Finished last round.");
                            timer.cancel();
                            callback.onLastRoundEnd();
                        }
                    }
                },
                roundTime);

        callback.onRoundStart(currentRoundIndex);

        updateContentProvider();
    }

    private void startRest() {
        Log.i(LOG_CAT, "Starting rest " + currentRoundIndex);
        isActive = false;
        isResting = true;
        secondsSincePeriodStart = 0;

        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        startRound();
                    }
                },
                restTime);

        callback.onRestStart(currentRoundIndex);

        updateContentProvider();
    }

    private void updateContentProvider() {
        final int fightState;
        if (isActive) {
            fightState = ACTIVE_FIGHT_STATE;
        } else if (isResting) {
            fightState = REST_FIGHT_STATE;
        } else {
            fightState = NO_FIGHT_STATE;
        }

        fightStatsSaver.updateFightState(fightState, secondsSincePeriodStart, currentRoundIndex);
    }

    public boolean isActiveRound() {
        return isActive;
    }
}
