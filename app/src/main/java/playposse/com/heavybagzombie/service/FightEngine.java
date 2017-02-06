package playposse.com.heavybagzombie.service;

import android.os.IBinder;

import playposse.com.heavybagzombie.service.fight.FightSimulation;

/**
 * Public interface for the {@link FightEngineService}. It allows the app to start and stop fights.
 */
public interface FightEngine {

    void startFight(FightSimulation fightSimulation);
    void stopFight();
}
