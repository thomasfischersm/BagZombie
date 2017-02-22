package com.playposse.heavybagzombie.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.playposse.heavybagzombie.BagZombiePreferences;
import com.playposse.heavybagzombie.R;
import com.playposse.heavybagzombie.provider.BagZombieContract;
import com.playposse.heavybagzombie.provider.RoundStatsRecord;
import com.playposse.heavybagzombie.service.FightEngine;
import com.playposse.heavybagzombie.service.FightEngineService;
import com.playposse.heavybagzombie.service.FightEngineServiceConnection;
import com.playposse.heavybagzombie.service.fight.impl.ManualFightSimulationV2;
import com.playposse.heavybagzombie.service.fight.impl.PunchCombination;
import com.playposse.heavybagzombie.service.fight.v2.FightSimulationV2;
import com.playposse.heavybagzombie.util.IntentParameters;

import static com.playposse.heavybagzombie.provider.BagZombieContract.FightTable;
import static com.playposse.heavybagzombie.provider.BagZombieContract.UpdateFightStateAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManualFightActivity
        extends PermittedParentActivity
        implements FightEngineServiceConnection.Callback {

    private static final String LOG_CAT = ManualFightActivity.class.getSimpleName();

    private static final long DEFAULT_MAX_DELAY = 3_000;
    private static final long DEFAULT_INDIVIDUAL_TIMEOUT = 1_500;
    private static final long DEFAULT_HEAVY_TIMEOUT = 500;

    private static final int FIGHT_STATE_LOADER = 1;
    private static final int ROUND_STATS_LOADER = 2;
    private static final int HIT_LOG_LOADER = 3;

    private TextView fightStateTextView;
    private TextView roundInfoTextView;
    private ViewPager roundStatsViewPager;
    private ListView hitLogListView;


    private FightEngineServiceConnection serviceConnection = new FightEngineServiceConnection(this);

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_manual_fight;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fightStateTextView = (TextView) findViewById(R.id.fightStateTextView);
        roundInfoTextView = (TextView) findViewById(R.id.roundInfoTextView);
        roundStatsViewPager = (ViewPager) findViewById(R.id.roundStatsViewPager);
        hitLogListView = (ListView) findViewById(R.id.hitLogListView);

        getLoaderManager().initLoader(FIGHT_STATE_LOADER, null, new FightStateLoaderCallback());
        getLoaderManager().initLoader(ROUND_STATS_LOADER, null, new RoundStatsLoaderCallback());
        getLoaderManager().initLoader(HIT_LOG_LOADER, null, new HitLogLoaderCallback());
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
        serviceConnection.getFightEngine().startFight(fightSimulation, false);
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
     * A {@link android.app.LoaderManager.LoaderCallbacks} that handles fight state data.
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

                int minutes = timer / 60;
                int seconds = timer % 60;
                String roundInfoStr =
                        getString(R.string.round_info, currentRoundIndex, minutes, seconds);

                fightStateTextView.setText(fightStateResId);
                roundInfoTextView.setText(roundInfoStr);
            }

            cursor.setNotificationUri(
                    getContentResolver(),
                    BagZombieContract.FightTable.CONTENT_URI);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }

    /**
     * A {@link android.app.LoaderManager.LoaderCallbacks} that handles the round stats data.
     */
    private class RoundStatsLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(
                    getApplicationContext(),
                    BagZombieContract.RoundStatsTable.CONTENT_URI,
                    BagZombieContract.RoundStatsTable.COLUMN_NAMES,
                    null,
                    null,
                    null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            if ((roundStatsViewPager.getAdapter() == null)
                    || !(roundStatsViewPager.getAdapter() instanceof RoundStatsPagerAdapter)) {
                roundStatsViewPager.setAdapter(new RoundStatsPagerAdapter(
                        getFragmentManager(),
                        cursor));
            } else {
                RoundStatsPagerAdapter adapter =
                        (RoundStatsPagerAdapter) roundStatsViewPager.getAdapter();
                adapter.swapCursor(cursor, roundStatsViewPager);
            }

            cursor.setNotificationUri(
                    getContentResolver(),
                    BagZombieContract.RoundStatsTable.CONTENT_URI);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            // Nothing to do.
        }
    }

    /**
     * A {@link android.app.LoaderManager.LoaderCallbacks} that loads the hit log.
     */
    private class HitLogLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(
                    getApplicationContext(),
                    BagZombieContract.HitRecordTable.CONTENT_URI,
                    BagZombieContract.HitRecordTable.COLUMN_NAMES,
                    null,
                    null,
                    null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            ListAdapter adapter = hitLogListView.getAdapter();
            if ((adapter != null) && (adapter instanceof CursorAdapter)) {
                ((CursorAdapter) adapter).swapCursor(cursor);
            } else {
                hitLogListView.setAdapter(
                        new HitLogCursorAdapter(getApplicationContext(), cursor, false));
            }

            cursor.setNotificationUri(
                    getContentResolver(),
                    BagZombieContract.HitRecordTable.CONTENT_URI);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            ListAdapter adapter = hitLogListView.getAdapter();
            if ((adapter != null) && (adapter instanceof CursorAdapter)) {
                ((CursorAdapter) adapter).swapCursor(null);
            }
        }
    }

    /**
     * A {@link android.support.v4.view.PagerAdapter} that shows a round in each fragment. The
     * first fragment shows a summary.
     */
    private class RoundStatsPagerAdapter extends FragmentPagerAdapter {

        private final List<RoundStatsRecord> roundStatsRecords = new ArrayList<>();

        private Cursor cursor;

        public RoundStatsPagerAdapter(FragmentManager fm, Cursor cursor) {
            super(fm);

            init(cursor);
        }

        private void init(Cursor cursor) {
            this.cursor = cursor;

            roundStatsRecords.clear();
            while (cursor.moveToNext()) {
                roundStatsRecords.add(new RoundStatsRecord(cursor));
            }
        }

        @Override
        public Fragment getItem(int position) {
            return RoundStatsFragment.newInstance(roundStatsRecords.get(position));
        }

        @Override
        public int getCount() {
            return roundStatsRecords.size();
        }

        public void swapCursor(Cursor cursor, ViewPager viewPager) {
            init(cursor);
            notifyDataSetChanged();

            // Find all fragments and update them.
            for (int i = 0; i < getCount(); i++) {
                String tag = "android:switcher:" + viewPager.getId() + ":" + i;
                RoundStatsFragment fragment =
                        (RoundStatsFragment) getFragmentManager().findFragmentByTag(tag);
                if (fragment != null) {
                    fragment.updateUi(roundStatsRecords.get(i));
                }
            }
        }
    }

    /**
     * A {@link CursorAdapter} that shows a list of all the hits, misses, and timeouts that the user
     * has made.
     */
    public class HitLogCursorAdapter extends CursorAdapter {

        public HitLogCursorAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return getLayoutInflater().inflate(
                    R.layout.hit_record_list_item,
                    parent,
                    false);
        }

        @Override
        public void bindView(View rootView, Context context, Cursor cursor) {
            TextView commandTextView =
                    (TextView) rootView.findViewById(R.id.commandTextView);
            TextView reactionTimeTextView =
                    (TextView) rootView.findViewById(R.id.reactionTimeTextView);

            String commandStr =
                    cursor.getString(cursor.getColumnIndex(BagZombieContract.HitRecordTable.COMMAND_COLUMN));
            commandTextView.setText(commandStr);

            int reactionTime =
                    cursor.getInt(
                            cursor.getColumnIndex(
                                    BagZombieContract.HitRecordTable.OVERALL_REACTION_TIME_COLUMN));
            if (reactionTime >= 0) {
                reactionTimeTextView.setText(
                        getString(R.string.reaction_time, reactionTime));
            } else {
                reactionTimeTextView.setText(R.string.timeout_reaction_time_constant);
            }
        }
    }
}
