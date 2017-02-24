package com.playposse.heavybagzombie.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.onsets.OnsetHandler;
import be.tarsos.dsp.onsets.PercussionOnsetDetector;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import com.playposse.heavybagzombie.BagZombiePreferences;
import com.playposse.heavybagzombie.R;

public class MicrophoneCalibrationActivity extends PermittedParentActivity {

    private static final String LOG_CAT = MicrophoneCalibrationActivity.class.getSimpleName();

    private final static int SAMPLE_RATE = 22050;
    private final static int BUFFER_SIZE = 1024;

    private SeekBar sensitivitySeekBar;
    private SeekBar thresholdSeekBar;

    private AudioDispatcher dispatcher;
    private PercussionOnsetDetector percussionOnsetDetector;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_microphone_calibration;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sensitivitySeekBar = (SeekBar) findViewById(R.id.sensitivitySeekBar);
        thresholdSeekBar = (SeekBar) findViewById(R.id.thresholdSeekBar);

        sensitivitySeekBar.setProgress(BagZombiePreferences.getSensitivity(this));
        sensitivitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                BagZombiePreferences.setSensitivity(getApplicationContext(), progress);
                resetSlapDetector();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Ignore.
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Ignore.
            }
        });

        thresholdSeekBar.setProgress(BagZombiePreferences.getThreshold(this));
        thresholdSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                BagZombiePreferences.setThreshold(getApplicationContext(), progress);
                resetSlapDetector();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Ignore.
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Ignore.
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (checkMicrophonePermission()) {
            startSlapDetection();
        }
    }

    private void startSlapDetection() {
        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(SAMPLE_RATE, BUFFER_SIZE,0);
        resetSlapDetector();
        new Thread(dispatcher,"Audio Dispatcher").start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (dispatcher != null) {
            dispatcher.stop();
        }
    }

    @Override
    protected void onMicrophonePermissionHasBeenGranted() {
        startSlapDetection();
    }

    private void resetSlapDetector() {
        try {
            dispatcher.removeAudioProcessor(percussionOnsetDetector);
        } catch (Throwable ex) {
            // Ignore for now.
        }

        percussionOnsetDetector = new PercussionOnsetDetector(
                SAMPLE_RATE,
                BUFFER_SIZE,
                new SlapHandler(),
                BagZombiePreferences.getSensitivity(this),
                BagZombiePreferences.getThreshold(this));
        dispatcher.addAudioProcessor(percussionOnsetDetector);
        Log.i(LOG_CAT, "Added percussion detector with sensitivity "
                + BagZombiePreferences.getSensitivity(this)
                + " and threshold " + BagZombiePreferences.getThreshold(this));
    }

    private class SlapHandler implements OnsetHandler {

        @Override
        public void handleOnset(double time, double salience) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView slapsTextView = (TextView) findViewById(R.id.slapsTextView);
                    slapsTextView.setText(slapsTextView.getText() + ".");

                    // Play sound.
                    final MediaPlayer mediaPlayer =
                            MediaPlayer.create(getApplicationContext(), R.raw.hit);
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            if (mediaPlayer != null) {
                                mediaPlayer.release();
                            }
                        }
                    });
                    mediaPlayer.start();
                }
            });
        }
    }
}
