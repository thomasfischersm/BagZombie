package com.playposse.heavybagzombie.service.fight;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A special timer that keeps track of the rounds and rest breaks.
 */
public class RoundTimer {

    private final FightSimulation fightSimulation;
    private final long roundDurationInMs;
    private final long restDurationInMs;
    private final int maxRounds;

    private final Timer timer = new Timer();

    private boolean roundActive = false;
    private int roundIndex = -1;

    public RoundTimer(
            FightSimulation fightSimulation,
            long roundDurationInMs,
            long restDurationInMs,
            int maxRounds) {

        this.fightSimulation = fightSimulation;
        this.roundDurationInMs = roundDurationInMs;
        this.restDurationInMs = restDurationInMs;
        this.maxRounds = maxRounds;
    }

    private void startRound() {
        roundActive = true;
        roundIndex++;

        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        startRest();
                    }
                },
                roundDurationInMs);

        fightSimulation.onRoundStart();
    }

    private void startRest() {
        roundActive = false;

        if (roundIndex + 1 != maxRounds) {
            timer.schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            startRound();
                        }
                    },
                    roundDurationInMs);

            fightSimulation.onRoundEnd();
        } else {
            fightSimulation.onLastRoundEnd();
        }
    }
}
