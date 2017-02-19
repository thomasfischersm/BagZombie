package com.playposse.heavybagzombie.service.fight.v2;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.onsets.OnsetHandler;
import be.tarsos.dsp.onsets.PercussionOnsetDetector;
import com.playposse.heavybagzombie.BagZombiePreferences;
import com.playposse.heavybagzombie.VocalPlayer;
import com.playposse.heavybagzombie.service.fight.impl.PunchCombination;

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
    private TimerTask lastPunchTask;
    private TimerTask lastTimeoutTask;

    public PunchTimerV2(Context context, VocalQueueV2 vocalQueue, PunchTimerCallbackV2 callback) {
        this.vocalQueue = vocalQueue;
        this.callback = callback;

        dispatcher = startAudioDispatcher(context);
    }

    private AudioDispatcher startAudioDispatcher(Context context) {
        AudioDispatcher dispatcher =
                AudioDispatcherFactory.fromDefaultMicrophone(SAMPLE_RATE, BUFFER_SIZE, 0);

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

    public synchronized void stop() {
        timer.cancel();
        if (dispatcher.isStopped()) {
            dispatcher.stop();
        }
    }

    private static int punchTaskId = 0;

    public synchronized void schedulePunchCombination(final PunchCombination punchCombination) {
        Log.i(LOG_CAT, "Schedule punch combination: " + punchCombination.getCommandString()
                + " " + punchCombination.hashCode());

        if (punchCombination == null) {
            throw new IllegalStateException(
                    "Can't schedule a punch combination when another one is still running!");
        }

        if (dispatcher.isStopped()) {
            // The round has already ended. Ignore this.
            return;
        }

        this.punchCombination = punchCombination;

        isTimeoutScheduled = false;
        hasCommandBeenIssued = false;

        final int currentPunchTaskId = ++punchTaskId;
        lastPunchTask = new TimerTask() {
            @Override
            public void run() {
                Log.i(LOG_CAT, "TimerTask for vocal of punch woke up "
                        + punchCombination.getCommandString() + " " + punchCombination.hashCode()
                        + " taskid " + currentPunchTaskId);
                playPunchVocal(currentPunchTaskId);
            }
        };
        timer.schedule(lastPunchTask, punchCombination.getDelay());
        Log.i(LOG_CAT, "Scheduled TimerTask for punch vocal: "
                + punchCombination.getCommandString() + " " + punchCombination.hashCode()
                + " taskid " + currentPunchTaskId);
    }

    private synchronized void playPunchVocal(final int currentPunchTaskId) {
        Log.i(LOG_CAT, "Sending punch combination to the vocal queue "
                + punchCombination.getCommandString() + " " + punchCombination.hashCode()
                + " taskid " + currentPunchTaskId);
        vocalQueue.scheduleVocal(
                punchCombination,
                new VocalQueueCallbackV2() {
                    @Override
                    public void onStartPlayback(VocalPlayer.Message message) {
                        Log.i(LOG_CAT, "PunchTimerV2.onStartPlayback returns for " + message.name());
                        hasCommandBeenIssued = true; // TODO: Set this when the playback starts, not ends!
                        if (!isTimeoutScheduled) {
                            scheduleTimeout(currentPunchTaskId);
                            punchCombination.recordStartTime();
                        }
                    }
                });
    }

    private synchronized void scheduleTimeout(final int currentPunchTaskId) {
        Log.i(LOG_CAT, "Attempt to schedule timeout task "
                + punchCombination.getCommandString() + " " + punchCombination.hashCode()
                + " taskid " + currentPunchTaskId);
        if (lastTimeoutTask != null) {
            lastTimeoutTask.cancel();
        }

        if (punchCombination == null) {
            // The player already reacted. Skip the timeout.
            return;
        }

        if (dispatcher.isStopped()) {
            // The round has already ended. Ignore this.
            return;
        }
        
        lastTimeoutTask = new TimerTask() {
            @Override
            public void run() {
                Log.i(LOG_CAT, "timeout task woke up for "
                        + punchCombination.getCommandString() + " " + punchCombination.hashCode()
                        + " taskid " + currentPunchTaskId);

                handleTimeout();
            }
        };
        timer.schedule(lastTimeoutTask, punchCombination.getIndividualTimeout());
    }

    private synchronized void handleTimeout() {
        Log.i(LOG_CAT, "Timeout occurred for: " + punchCombination.getCommandString()
            + " " + punchCombination.hashCode());
        PunchCombination lastPunchCombination = punchCombination;
        reset();
        callback.onTimeout(lastPunchCombination);
    }

    private synchronized void handleBangDetected() {
        Log.i(LOG_CAT, "Bang detected!");
        if ((punchCombination == null) || !hasCommandBeenIssued) {
            Log.i(LOG_CAT, "Recorded miss");
            callback.onMiss();
        } else {
            Log.i(LOG_CAT, "Recorded hit");
            punchCombination.recordReactionTime();

            if (!punchCombination.canRecordMoreReactionTimes()) {
                punchCombination.recordEndTime();
                PunchCombination lastPunchCombination = punchCombination;
                reset();
                callback.onHit(lastPunchCombination);
            } else {
                scheduleTimeout(-1);
            }
        }
    }

    private synchronized void reset() {
        Log.i(LOG_CAT, "Resetting PunchTimerV2");
        punchCombination = null;
        isTimeoutScheduled = false;
        hasCommandBeenIssued = false;

        if (lastTimeoutTask != null) {
            lastTimeoutTask.cancel();
        }

        if (lastPunchTask != null) {
            lastPunchTask.cancel();
        }
    }
}
