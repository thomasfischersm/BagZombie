package com.playposse.heavybagzombie.service.fight.v2;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Timekeeper for the fight and its rounds.
 */
public class FightTimerV2 {

    private static final String LOG_CAT = FightTimerV2.class.getSimpleName();

    private final long roundTime;
    private final long restTime;
    private final int maxRound;
    private final FightTimerCallbackV2 callback;

    private final Timer timer = new Timer();

    private int currentRoundIndex = -1;
    private boolean isActive = false;
    private boolean isResting = false;
    private boolean isPaused = false;

    public FightTimerV2(
            long roundTime,
            long restTime,
            int maxRound,
            FightTimerCallbackV2 callback) {

        this.roundTime = roundTime;
        this.restTime = restTime;
        this.maxRound = maxRound;
        this.callback = callback;
    }

    public void start() {
        startRound();
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

        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        if (currentRoundIndex + 1 < maxRound) {
                            startRest();
                        } else {
                            Log.i(LOG_CAT, "Finished last round.");
                            callback.onLastRoundEnd();
                        }
                    }
                },
                roundTime);

        callback.onRoundStart(currentRoundIndex);
    }

    private void startRest() {
        Log.i(LOG_CAT, "Starting rest " + currentRoundIndex);
        isActive = false;
        isResting = true;

        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        startRound();
                    }
                },
                restTime);

        callback.onRestStart(currentRoundIndex);
    }
}
