package playposse.com.heavybagzombie.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
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
import playposse.com.heavybagzombie.BagZombiePreferences;
import playposse.com.heavybagzombie.R;

public class MicrophoneCalibrationActivity extends ParentActivity {

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

        if (!checkMicrophonePermission()) {
            return;
        }

        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(SAMPLE_RATE, BUFFER_SIZE,0);

        PitchDetectionHandler pdh = new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult result, AudioEvent e) {
                final float pitchInHz = result.getPitch();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView text = (TextView) findViewById(R.id.pitchTextView);
                        text.setText("" + pitchInHz);
                    }
                });
            }
        };

        AudioProcessor p = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, SAMPLE_RATE, BUFFER_SIZE, pdh);
        dispatcher.addAudioProcessor(p);
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

    private boolean checkMicrophonePermission() {
        int permissionCheck =
                ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        // Need to request permissions.
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                1);
        return false;
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
