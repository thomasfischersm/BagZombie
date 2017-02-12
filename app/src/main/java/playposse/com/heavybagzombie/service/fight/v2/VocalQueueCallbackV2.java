package playposse.com.heavybagzombie.service.fight.v2;

import playposse.com.heavybagzombie.VocalPlayer;
import playposse.com.heavybagzombie.service.fight.impl.PunchCombination;

/**
 * A callback to notify when a vocal is played in the queue.
 */
public interface VocalQueueCallbackV2 {

    void onStartPlayback(VocalPlayer.Message message);
//    void onStartPlayback(PunchCombination punchCombination);
}
