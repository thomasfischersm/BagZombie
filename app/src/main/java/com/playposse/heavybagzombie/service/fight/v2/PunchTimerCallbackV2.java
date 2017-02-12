package com.playposse.heavybagzombie.service.fight.v2;

import com.playposse.heavybagzombie.service.fight.impl.PunchCombination;

/**
 * Callback for events related to punches (or missing punches).
 */
public interface PunchTimerCallbackV2 {

    void onHit(PunchCombination punchCombination);
    void onTimeout(PunchCombination punchCombination);
    void onMiss();
}
