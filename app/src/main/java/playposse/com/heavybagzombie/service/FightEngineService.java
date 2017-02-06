package playposse.com.heavybagzombie.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import playposse.com.heavybagzombie.service.fight.FightEngineCallback;
import playposse.com.heavybagzombie.service.fight.FightSimulation;

/**
 * A {@link android.app.Service} that records audio to detect when the heavy bag is hit and
 * simulates the fight. It can be connected to from the app and the app status.
 */
public class FightEngineService extends Service implements FightEngine, FightEngineCallback {

    private static final String LOG_CAT = FightEngineService.class.getSimpleName();

    private FightSimulation fightSimulation;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new FightEngineBinder();
    }

    @Override
    public void startFight(FightSimulation fightSimulation) {
        stopFight();
        fightSimulation.startFight(this);
        Log.i(LOG_CAT, "Service has started fight.");
    }

    @Override
    public void stopFight() {
        if (fightSimulation != null) {
            fightSimulation.stopFight();
        }
    }

    public class FightEngineBinder extends Binder {

        public FightEngine getService() {
            // Return this instance of LocalService so clients can call public methods
            return FightEngineService.this;
        }
    }
}
