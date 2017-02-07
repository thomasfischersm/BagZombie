package playposse.com.heavybagzombie.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import playposse.com.heavybagzombie.R;
import playposse.com.heavybagzombie.service.FightEngine;
import playposse.com.heavybagzombie.service.FightEngineService;
import playposse.com.heavybagzombie.service.fight.impl.RandomFightSimulation;

import static playposse.com.heavybagzombie.provider.BagZombieContract.FightTable;
import static playposse.com.heavybagzombie.provider.BagZombieContract.HitRecordTable;

public class FightActivity extends AppCompatActivity {

    private static final String LOG_CAT = FightActivity.class.getSimpleName();

    private TextView hitCountTextView;
    private TextView missCountTextView;
    private TextView timeoutCountTextView;
    private ListView hitLogListView;
    private ImageButton startButton;
    private ImageButton pauseButton;
    private ImageButton stopButton;

    private ServiceConnection serviceConnection = new FightEngineServiceConnection();
    private boolean isConnected = false;
    private FightEngine fightEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fight);

        hitCountTextView = (TextView) findViewById(R.id.hitCountTextView);
        missCountTextView = (TextView) findViewById(R.id.missCountTextView);
        timeoutCountTextView = (TextView) findViewById(R.id.timeoutCountTextView);
        hitLogListView = (ListView) findViewById(R.id.hitLogListView);
        startButton = (ImageButton) findViewById(R.id.startButton);
        pauseButton = (ImageButton) findViewById(R.id.pauseButton);
        stopButton = (ImageButton) findViewById(R.id.stopButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(LOG_CAT, "Gott start button click.");
                if (fightEngine != null) {
                    Log.i(LOG_CAT, "Telling service to start the fight.");
                    fightEngine.startFight(new RandomFightSimulation(60_000));
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fightEngine != null) {
                    fightEngine.stopFight();
                }
            }
        });

        new TestAsyncTask().execute();

        getContentResolver().registerContentObserver(
                FightTable.CONTENT_URI,
                true,
                new ContentObserver(new Handler()) {

                    @Override
                    public void onChange(boolean selfChange) {
                        super.onChange(selfChange, null);
                    }

                    @Override
                    public void onChange(boolean selfChange, Uri uri) {
                        new TestAsyncTask().execute();
                        new Test2AsyncTask().execute();
                    }
                });
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

    private class FightEngineServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            fightEngine = ((FightEngineService.FightEngineBinder) service).getService();
            isConnected = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isConnected = false;
        }
    }

    private class TestAsyncTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... params) {
            return getContentResolver().query(
                    FightTable.CONTENT_URI,
                    FightTable.COLUMN_NAMES,
                    null,
                    null,
                    null);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor.moveToFirst()) {
                int hitCount = cursor.getInt(cursor.getColumnIndex(FightTable.HIT_COUNT_COLUMN));
                hitCountTextView.setText(Integer.toString(hitCount));

                int missCount = cursor.getInt(cursor.getColumnIndex(FightTable.MISS_COUNT_COLUMN));
                missCountTextView.setText(Integer.toString(missCount));

                int timeoutCount =
                        cursor.getInt(cursor.getColumnIndex(FightTable.TIMEOUT_COUNT_COLUMN));
                timeoutCountTextView.setText(Integer.toString(timeoutCount));
            }
        }
    }

    private class Test2AsyncTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... params) {
            return getContentResolver().query(
                    HitRecordTable.CONTENT_URI,
                    HitRecordTable.COLUMN_NAMES,
                    null,
                    null,
                    null);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            hitLogListView.setAdapter(new CursorAdapter(getApplicationContext(), cursor, false) {
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
                            cursor.getString(cursor.getColumnIndex(HitRecordTable.COMMAND_COLUMN));
                    commandTextView.setText(commandStr);

                    int reactionTime =
                            cursor.getInt(cursor.getColumnIndex(HitRecordTable.OVERALL_REACTION_TIME_COLUMN));
                    if (reactionTime >= 0) {
                        reactionTimeTextView.setText("" + reactionTime);
                    } else {
                        reactionTimeTextView.setText(R.string.timeout_reaction_time_constant);
                    }
                }
            });
        }
    }
}
