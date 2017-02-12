package playposse.com.heavybagzombie.service.fight.v2;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.onsets.OnsetHandler;
import be.tarsos.dsp.onsets.PercussionOnsetDetector;
import playposse.com.heavybagzombie.BagZombiePreferences;
import playposse.com.heavybagzombie.VocalPlayer;
import playposse.com.heavybagzombie.service.fight.impl.PunchCombination;

/**
 * Timer that manages scheduling a punch and tricking its hit/miss. This runs the code that
 * listens to the microphone for hits.
 */
public class PunchTimerV2 {

    private static final String LOG_CAT = PunchTimerV2.class.getSimpleName();

    private final static int SAMPLE_RATE = 22050;
    private final static int BUFFER_SIZE = 1024;

    private final VocalQueueV2 vocalQueue;

    private final Timer timer = new Timer();
    private final AudioDispatcher dispatcher;
    private final PunchTimerCallbackV2 callback;

    @Nullable
    private PunchCombination punchCombination;
    private boolean isTimeoutScheduled = false;
    private boolean hasCommandBeenIssued = false;

    public PunchTimerV2(Context context, VocalQueueV2 vocalQueue, PunchTimerCallbackV2 callback) {
        this.vocalQueue = vocalQueue;
        this.callback = callback;

        dispatcher = startAudioDispatcher(context);
    }

    private AudioDispatcher startAudioDispatcher(Context context) {
        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(SAMPLE_RATE, BUFFER_SIZE, 0);

        OnsetHandler onsetHandler = new OnsetHandler() {
            @Override
            public void handleOnset(double time, double salience) {
                handleBangDetected();
            }
        };
        PercussionOnsetDetector percussionOnsetDetector = new PercussionOnsetDetector(
                SAMPLE_RATE,
                BUFFER_SIZE,
                onsetHandler,
                BagZombiePreferences.getSensitivity(context),
                BagZombiePreferences.getThreshold(context));
        dispatcher.addAudioProcessor(percussionOnsetDetector);
        new Thread(dispatcher, "Audio Dispatcher").start();

        return dispatcher;
    }

    public void stop() {
        timer.cancel();
        if (dispatcher.isStopped()) {
            dispatcher.stop();
        }
    }

    public void schedulePunchCombination(PunchCombination punchCombination) {
        Log.i(LOG_CAT, "Schedule punch combination: " + punchCombination.getCommandString());

        if (punchCombination == null) {
            throw new IllegalStateException(
                    "Can't schedule a punch combination when another one is still running!");
        }

        this.punchCombination = punchCombination;

        isTimeoutScheduled = false;
        hasCommandBeenIssued = false;

        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        playPunchVocal();
                    }
                },
                punchCombination.getDelay());
    }

    private void playPunchVocal() {
        vocalQueue.scheduleVocal(
                punchCombination,
                new VocalQueueCallbackV2() {
                    @Override
                    public void onStartPlayback(VocalPlayer.Message message) {
                        hasCommandBeenIssued = true;
                        if (!isTimeoutScheduled) {
                            scheduleTimeout();
                            punchCombination.recordStartTime();
                        }
                    }
                });
    }

    private void scheduleTimeout() {
        timer.cancel();
        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        handleTimeout();
                    }
                },
                punchCombination.getIndividualTimeout());
    }

    private void handleTimeout() {
        Log.i(LOG_CAT, "Timeout occured for: " + punchCombination.getCommandString());
        callback.onTimeout(punchCombination);
        reset();
    }

    private void handleBangDetected() {
        Log.i(LOG_CAT, "Bang detected!");
        if ((punchCombination == null) || !hasCommandBeenIssued) {
            Log.i(LOG_CAT, "Recorded miss");
            callback.onMiss();
        } else {
            Log.i(LOG_CAT, "Recorded hit");
            scheduleTimeout();
            punchCombination.recordReactionTime();

            if (!punchCombination.canRecordMoreReactionTimes()) {
                punchCombination.recordEndTime();
                callback.onHit(punchCombination);
                reset();
            }
        }
    }

    private void reset() {
        punchCombination = null;
        isTimeoutScheduled = false;
        hasCommandBeenIssued = false;
    }
}
