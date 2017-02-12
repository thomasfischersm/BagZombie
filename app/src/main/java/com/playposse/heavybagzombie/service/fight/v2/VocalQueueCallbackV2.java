package com.playposse.heavybagzombie.service.fight.v2;

import com.playposse.heavybagzombie.VocalPlayer;

/**
 * A callback to notify when a vocal is played in the queue.
 */
public interface VocalQueueCallbackV2 {

    void onStartPlayback(VocalPlayer.Message message);
//    void onStartPlayback(PunchCombination punchCombination);
}
