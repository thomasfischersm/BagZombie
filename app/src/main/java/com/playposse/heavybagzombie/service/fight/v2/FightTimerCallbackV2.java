package com.playposse.heavybagzombie.service.fight.v2;

/**
 * A callback to inform about critical time events in the fight.
 */
public interface FightTimerCallbackV2 {

    void onRoundStart(int roundIndex);
    void onRestStart(int restIndex);
    void onLastRoundEnd();
}
