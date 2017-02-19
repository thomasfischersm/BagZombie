package com.playposse.heavybagzombie.activity;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.playposse.heavybagzombie.BagZombiePreferences;
import com.playposse.heavybagzombie.R;
import com.playposse.heavybagzombie.provider.BagZombieContract;
import com.playposse.heavybagzombie.service.FightEngine;
import com.playposse.heavybagzombie.service.FightEngineService;
import com.playposse.heavybagzombie.service.FightEngineServiceConnection;
import com.playposse.heavybagzombie.service.fight.impl.ManualFightSimulationV2;
import com.playposse.heavybagzombie.service.fight.impl.PunchCombination;
import com.playposse.heavybagzombie.service.fight.v2.FightSimulationV2;
import com.playposse.heavybagzombie.util.IntentParameters;

import static com.playposse.heavybagzombie.provider.BagZombieContract.FightTable;
import static com.playposse.heavybagzombie.provider.BagZombieContract.UpdateFightStateAction;

import java.util.List;

public class ManualFightActivity
        extends PermittedParentActivity
        implements FightEngineServiceConnection.Callback {

    private static final String LOG_CAT = ManualFightActivity.class.getSimpleName();

    private static final long DEFAULT_MAX_DELAY = 3_000;
    private static final long DEFAULT_INDIVIDUAL_TIMEOUT = 1_500;
    private static final long DEFAULT_HEAVY_TIMEOUT = 500;

    private static final int FIGHT_STATE_LOADER = 1;

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

        getLoaderManager().initLoader(FIGHT_STATE_LOADER, null, new FightStateLoaderCallback());
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
                roundDuration * 1_000,
                restDuration * 1_000,
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

    /**
     * A {@link android.app.LoaderManager.LoaderCallbacks} that handles fight state datea.
     */
    private class FightStateLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {

        @Override
        public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
            return new CursorLoader(
                    getApplicationContext(),
                    BagZombieContract.FightTable.CONTENT_URI,
                    BagZombieContract.FightTable.COLUMN_NAMES,
                    null,
                    null,
                    null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            Log.i(LOG_CAT, "onLoadFinished for FightTable finished");
            if (cursor.moveToFirst()) {
                int fightState =
                        cursor.getInt(cursor.getColumnIndex(FightTable.FIGHT_STATE_COLUMN));
                int currentRoundIndex =
                        cursor.getInt(cursor.getColumnIndex(FightTable.CURRENT_ROUND_COLUMN)) + 1;
                int timer =
                        cursor.getInt(cursor.getColumnIndex(FightTable.TIMER_COLUMN));

                final int fightStateResId;
                switch (fightState) {
                    case UpdateFightStateAction.NO_FIGHT_STATE:
                        fightStateResId = R.string.fight_state_none;
                        break;
                    case UpdateFightStateAction.ACTIVE_FIGHT_STATE:
                        fightStateResId = R.string.fight_state_active;
                        break;
                    case UpdateFightStateAction.REST_FIGHT_STATE:
                        fightStateResId = R.string.fight_state_rest;
                        break;
                    default:
                        throw new IllegalArgumentException("Unexpected fight state: " + fightState);
                }

                String roundCounterString = getString(R.string.round_counter, currentRoundIndex);

                int minutes = timer / 60;
                int seconds = timer % 60;
                String timerString = getString(R.string.round_timer, minutes, seconds);

                fightStateTextView.setText(fightStateResId);
                roundCountTextView.setText(roundCounterString);
                timerTextView.setText(timerString);
            }

            cursor.setNotificationUri(
                    getContentResolver(),
                    BagZombieContract.FightTable.CONTENT_URI);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }
}
