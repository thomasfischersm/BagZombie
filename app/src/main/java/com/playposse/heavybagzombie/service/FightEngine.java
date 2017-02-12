package com.playposse.heavybagzombie.service;

import com.playposse.heavybagzombie.service.fight.FightSimulation;

/**
 * Public interface for the {@link FightEngineService}. It allows the app to start and stop fights.
 */
public interface FightEngine {

    void startFight(FightSimulation fightSimulation);
    void stopFight();
}
