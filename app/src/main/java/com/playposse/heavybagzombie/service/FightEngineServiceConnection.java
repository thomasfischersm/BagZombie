package com.playposse.heavybagzombie.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * A {@link ServiceConnection} that connects to the {@link FightEngineService}.
 */
public class FightEngineServiceConnection implements ServiceConnection {

    private FightEngine fightEngine;
    private boolean isConnected;

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        fightEngine = ((FightEngineService.FightEngineBinder) service).getService();
        isConnected = true;
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
}
