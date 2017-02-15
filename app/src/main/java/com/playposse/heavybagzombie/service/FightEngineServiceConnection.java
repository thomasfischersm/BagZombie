package com.playposse.heavybagzombie.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * A {@link ServiceConnection} that connects to the {@link FightEngineService}.
 */
public class FightEngineServiceConnection implements ServiceConnection {

    private final Callback callback;

    private FightEngine fightEngine;
    private boolean isConnected;

    public FightEngineServiceConnection(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        fightEngine = ((FightEngineService.FightEngineBinder) service).getService();
        isConnected = true;

        callback.onFightEngineBound(fightEngine);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        isConnected = false;
    }

    public FightEngine getFightEngine() {
        return fightEngine;
    }

    public boolean isConnected() {
        return isConnected;
    }

    /**
     * A callback interface for when the service is bound.
     */
    public interface Callback {
        void onFightEngineBound(FightEngine fightEngine);
    }
}
