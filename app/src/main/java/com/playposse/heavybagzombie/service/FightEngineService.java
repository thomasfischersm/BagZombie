package com.playposse.heavybagzombie.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.playposse.heavybagzombie.R;
import com.playposse.heavybagzombie.activity.ManualFightActivity;
import com.playposse.heavybagzombie.service.fight.FightEngineCallback;
import com.playposse.heavybagzombie.service.fight.FightSimulation;
import com.playposse.heavybagzombie.service.fight.v2.FightSimulationV2;
import com.playposse.heavybagzombie.service.fight.v2.FightSimulatorV2;
import com.playposse.heavybagzombie.util.IntentParameters;

/**
 * A {@link android.app.Service} that records audio to detect when the heavy bag is hit and
 * simulates the fight. It can be connected to from the app and the app status.
 */
public class FightEngineService extends Service implements FightEngine, FightEngineCallback {

    private static final String LOG_CAT = FightEngineService.class.getSimpleName();

    private FightSimulationV2 fightSimulation;
    private FightSimulatorV2 fightSimulator;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new FightEngineBinder();
    }

    @Override
    public void startFight(FightSimulationV2 fightSimulation, boolean force) {
        if ((fightSimulator != null) && !force) {
            // The activity may have only rotated. The intent may not be to actually start a new
            // fight.
            return;
        }

        stopFight();
        this.fightSimulation = fightSimulation;

        fightSimulator = new FightSimulatorV2(this, fightSimulation);
        fightSimulator.start();

        startForeground();
        Log.i(LOG_CAT, "Service has started fight.");
    }

    @Override
    public void stopFight() {
        if (fightSimulator != null) {
            fightSimulator.stop();
            fightSimulator = null;
        }
        stopForeground(true);
    }

    private void startForeground() {
        Intent intent = IntentParameters.createManualFightIntentForNotification(this);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification notification = new Notification.Builder(this)
                .setContentTitle(getString(R.string.service_notification_title))
                .setContentText(getString(R.string.service_notification_text))
                .setSmallIcon(R.drawable.ic_play_arrow_black_24dp)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);
    }

    public class FightEngineBinder extends Binder {

        public FightEngine getService() {
            // Return this instance of LocalService so clients can call public methods
            return FightEngineService.this;
        }
    }
}
