package com.playposse.heavybagzombie.service.fight.v2;

import com.playposse.heavybagzombie.service.fight.impl.PunchCombination;

/**
 * Interface for different types of fights to implement.
 */
public interface FightSimulationV2 {

    void init(FightContextV2 fightContext);
    long getRoundTime();
    long getRestTime();
    int getMaxRound();
    PunchCombination getNextPunchCombination();
    void scoreHit(PunchCombination punchCombination);
    void scoreTimeout(PunchCombination punchCombination);
    void scoreMiss();
    void onRoundStart(int roundIndex);
    void onRoundEnd(int roundIndex, boolean isFinalRound);
}
