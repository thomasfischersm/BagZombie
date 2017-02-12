package com.playposse.heavybagzombie.service.fight;

import android.content.Context;

import com.playposse.heavybagzombie.service.FightEngineService;

/**
 * An interface for the {@link FightSimulation} to call back to the {@link FightEngineService}.
 */
public interface FightEngineCallback {

    Context getApplicationContext();
}
