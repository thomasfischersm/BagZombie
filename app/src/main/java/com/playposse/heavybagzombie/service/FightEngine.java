package com.playposse.heavybagzombie.service;

import com.playposse.heavybagzombie.service.fight.FightSimulation;
import com.playposse.heavybagzombie.service.fight.v2.FightSimulationV2;

/**
 * Public interface for the {@link FightEngineService}. It allows the app to start and stop fights.
 */
public interface FightEngine {

    void startFight(FightSimulationV2 fightSimulation, boolean force);
    void stopFight();
}
