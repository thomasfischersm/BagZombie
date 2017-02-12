package com.playposse.heavybagzombie.service.fight.v2;

import com.playposse.heavybagzombie.service.fight.FightStatsSaver;

/**
 * A context class that holds all the references to fight related infrastructure objects together.
 */
public class FightContextV2 {

    private final FightTimerV2 fightTimer;
    private final PunchTimerV2 punchTimer;
    private final VocalQueueV2 vocalQueue;
    private final FightStatsSaver fightStatsSaver;

    public FightContextV2(
            FightTimerV2 fightTimer,
            PunchTimerV2 punchTimer,
            VocalQueueV2 vocalQueue,
            FightStatsSaver fightStatsSaver) {
        this.fightTimer = fightTimer;
        this.punchTimer = punchTimer;
        this.vocalQueue = vocalQueue;
        this.fightStatsSaver = fightStatsSaver;
    }

    public FightTimerV2 getFightTimer() {
        return fightTimer;
    }

    public PunchTimerV2 getPunchTimer() {
        return punchTimer;
    }

    public VocalQueueV2 getVocalQueue() {
        return vocalQueue;
    }

    public FightStatsSaver getFightStatsSaver() {
        return fightStatsSaver;
    }
}
