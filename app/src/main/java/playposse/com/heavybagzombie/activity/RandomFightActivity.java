package playposse.com.heavybagzombie.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.onsets.OnsetHandler;
import be.tarsos.dsp.onsets.PercussionOnsetDetector;
import playposse.com.heavybagzombie.BagZombiePreferences;
import playposse.com.heavybagzombie.CommandPlayer;
import playposse.com.heavybagzombie.R;

/**
 * An {@link android.app.Activity} that creates a fight with random commands.
 */
public class RandomFightActivity extends AppCompatActivity {

    private static final String LOG_CAT = RandomFightActivity.class.getSimpleName();

    private final static int SAMPLE_RATE = 22050;
    private final static int BUFFER_SIZE = 1024;

    private AudioDispatcher dispatcher;
    private CommandPlayer.Command currentCommand;
    private long commandStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_random_fight);
    }

    @Override
    protected void onStart() {
        super.onStart();

        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(SAMPLE_RATE, BUFFER_SIZE, 0);

        PercussionOnsetDetector percussionOnsetDetector = new PercussionOnsetDetector(
                SAMPLE_RATE,
                BUFFER_SIZE,
                new RandomFightActivity.SlapHandler(),
                BagZombiePreferences.getSensitivity(this),
                BagZombiePreferences.getThreshold(this));
        dispatcher.addAudioProcessor(percussionOnsetDetector);
        new Thread(dispatcher, "Audio Dispatcher").start();

        queueCommand();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (dispatcher != null) {
            dispatcher.stop();
        }
    }

    private void queueCommand() {
        long delay = new Random().nextInt(5_000) + 500;
        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        switch (new Random().nextInt(2)) {
                            case 0:
                                currentCommand = CommandPlayer.Command.one;
                                break;
                            case 1:
                                currentCommand = CommandPlayer.Command.two;
                                break;
                        }
                        CommandPlayer.play(getApplicationContext(), currentCommand);
                        commandStart = System.currentTimeMillis();
                    }
                },
                delay);
    }

    private class SlapHandler implements OnsetHandler {

        @Override
        public void handleOnset(double time, double salience) {
            if (currentCommand != null) {
                long now = System.currentTimeMillis();
                long duration = now - commandStart;
                Log.i(LOG_CAT, "Reaction time: " + duration);
                Log.i(LOG_CAT, "Reported time: " + time);
                CommandPlayer.play(getApplicationContext(), CommandPlayer.Command.hit);

                currentCommand = null;
                queueCommand();
            }
        }
    }
}
