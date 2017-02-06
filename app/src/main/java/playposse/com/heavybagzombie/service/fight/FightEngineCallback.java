package playposse.com.heavybagzombie.service.fight;

import android.content.Context;

import playposse.com.heavybagzombie.service.FightEngineService;

/**
 * An interface for the {@link FightSimulation} to call back to the {@link FightEngineService}.
 */
public interface FightEngineCallback {

    Context getApplicationContext();
}
