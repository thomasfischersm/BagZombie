package playposse.com.heavybagzombie.service.fight;

/**
 * A class that simulates a fight.
 */
public interface FightSimulation {

    void startFight(FightEngineCallback fightEngineCallback);
    void stopFight();

    void onRoundStart();
    void onRoundEnd();
    void onLastRoundEnd();
}
