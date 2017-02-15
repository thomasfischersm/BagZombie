package com.playposse.heavybagzombie.activity;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.playposse.heavybagzombie.BagZombiePreferences;
import com.playposse.heavybagzombie.R;
import com.playposse.heavybagzombie.service.FightEngine;
import com.playposse.heavybagzombie.service.FightEngineService;
import com.playposse.heavybagzombie.service.FightEngineServiceConnection;
import com.playposse.heavybagzombie.service.fight.impl.ManualFightSimulationV2;
import com.playposse.heavybagzombie.service.fight.impl.PunchCombination;
import com.playposse.heavybagzombie.service.fight.v2.FightSimulationV2;
import com.playposse.heavybagzombie.util.IntentParameters;

import java.util.List;

public class ManualFightActivity
        extends PermittedParentActivity
        implements FightEngineServiceConnection.Callback {

    private static final long DEFAULT_MAX_DELAY = 3_000;
    private static final long DEFAULT_INDIVIDUAL_TIMEOUT = 1_500;
    private static final long DEFAULT_HEAVY_TIMEOUT = 500;

    private TextView fightStateTextView;
    private TextView roundCountTextView;
    private TextView timerTextView;

    private FightEngineServiceConnection serviceConnection = new FightEngineServiceConnection(this);

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_manual_fight;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fightStateTextView = (TextView) findViewById(R.id.fightStateTextView);
        roundCountTextView = (TextView) findViewById(R.id.roundCountTextView);
        timerTextView = (TextView) findViewById(R.id.timerTextView);
    }

    private void startFight() {
        int roundCount = getIntent().getIntExtra(
                IntentParameters.ROUND_COUNT_EXTRA,
                BagZombiePreferences.DEFAULT_ROUND_COUNT);
        int roundDuration = getIntent().getIntExtra(
                IntentParameters.ROUND_DURATION_EXTRA,
                BagZombiePreferences.DEFAULT_ROUND_DURATION);
        int restDuration = getIntent().getIntExtra(
                IntentParameters.REST_DURATION_EXTRA,
                BagZombiePreferences.DEFAULT_REST_DURATION);
        List<PunchCombination> punchCombinations = PunchCombination.toList(
                getIntent().getStringArrayExtra(IntentParameters.PUNCH_COMBINATIONS_EXTRA));

        FightSimulationV2 fightSimulation = new ManualFightSimulationV2(
                punchCombinations,
                roundDuration,
                restDuration,
                roundCount,
                DEFAULT_MAX_DELAY,
                DEFAULT_INDIVIDUAL_TIMEOUT,
                DEFAULT_HEAVY_TIMEOUT);
        serviceConnection.getFightEngine().startFight(fightSimulation);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = new Intent(this, FightEngineService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();

        unbindService(serviceConnection);
    }

    @Override
    public void onFightEngineBound(FightEngine fightEngine) {
        if (checkMicrophonePermission()) {
            startFight();
        }
    }

    @Override
    protected void onMicrophonePermissionHasBeenGranted() {
        if (serviceConnection.isConnected()) {
            startFight();
        }
    }
}
