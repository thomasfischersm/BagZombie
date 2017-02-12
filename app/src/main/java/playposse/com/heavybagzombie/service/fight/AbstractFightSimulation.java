package playposse.com.heavybagzombie.service.fight;

import android.content.Context;
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
 * An implementation of {@link FightSimulation} that implements all the basics of recognizing
 * bag hits and playing commands. Child classes can focus on the logic of the actual fight.
 */
public abstract class AbstractFightSimulation implements FightSimulation {

    private static final String LOG_CAT = AbstractFightSimulation.class.getSimpleName();

    private final static int SAMPLE_RATE = 22050;
    private final static int BUFFER_SIZE = 1024;

    private final long duration;

    private FightEngineCallback fightEngineCallback;
    private Context context;
    private Timer timer;
    private AudioDispatcher dispatcher;
    private FightStatsSaver fightStatsSaver;

    private PunchCombination punchCombination = null;
    private Long timeout = null;
    private TimerTask commandTask;
    private TimerTask timeoutTask;
    private boolean isCommandPending = false;
    private boolean isSoundActive = false;
    private boolean isFightActive = false;

    public AbstractFightSimulation(long duration) {
        this.duration = duration;
    }

    @Override
    public final void startFight(FightEngineCallback fightEngineCallback) {
        this.fightEngineCallback = fightEngineCallback;
        this.context = fightEngineCallback.getApplicationContext();

        fightStatsSaver = new FightStatsSaver(fightEngineCallback.getApplicationContext());
        timer = new Timer();

        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(SAMPLE_RATE, BUFFER_SIZE, 0);

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

        isFightActive = true;
        fightStatsSaver.resetFightStats();
        onFightStart();
    }

    private void handleBangDetected() {
        if ((punchCombination != null) && punchCombination.hasStarted()) {
            Log.i(LOG_CAT, "Register hit for command "
                    + punchCombination.getCommandString());
            punchCombination.recordReactionTime();

            if (!punchCombination.canRecordMoreReactionTimes()) {
                punchCombination.recordEndTime();
                timeoutTask.cancel();
                onScoreHit(punchCombination);
            } else {
                scheduleTimeoutTask();
            }
        } else {
            onScoreMiss();
        }
    }

    @Override
    public final void stopFight() {
        if (!dispatcher.isStopped()) {
            dispatcher.stop();
        }
        timer.cancel();
        isFightActive = false;
        onFightAborted();
    }

    /**
     * Schedules a command. The command will be played after the {@code delayMs}. If the player
     * doesn't make a hit within the {@code timeout}, the command is canceled as failed.
     */
    protected final void scheduleCommand(
            final PunchCombination punchCombination,
            long delayMs,
            long timeout) {

        this.punchCombination = punchCombination;
        this.timeout = timeout;
        Log.i(LOG_CAT, "Schedule currentCommand: " + punchCombination.getCommandString());

        commandTask = new TimerTask() {
            @Override
            public void run() {
                if (isSoundActive) {
                    isCommandPending = true;
                } else {
                    punchCombination.recordStartTime();
                    startCommand(punchCombination.getNextCommand());
                }
            }
        };
        timer.schedule(commandTask, delayMs);

        scheduleTimeoutTask();
        Log.i(LOG_CAT, "Scheduled timeout task. " + timeout);
    }

    private void scheduleTimeoutTask() {
        if (timeoutTask != null) {
            timeoutTask.cancel();
        }

        timeoutTask = new TimerTask() {
            @Override
            public void run() {
                Log.i(LOG_CAT, "Execute timeout task");
                commandTask.cancel();
                PunchCombination currentPunchCombination = punchCombination;
                punchCombination = null;
                isCommandPending = false;

                onScoreTimeout(currentPunchCombination);
            }
        };
        timer.schedule(timeoutTask, timeout);
    }

    private void startCommand(VocalPlayer.Message message) {
        playSound(message);
    }

    protected Context getContext() {
        return context;
    }

    protected FightStatsSaver getFightStatsSaver() {
        return fightStatsSaver;
    }

    protected void playSound(VocalPlayer.Message message) {
        isSoundActive = true;
        VocalPlayer.play(
                context,
                message,
                new VocalPlayer.Callback() {
                    @Override
                    public void onComplete() {
                        isSoundActive = false;
                        if (isCommandPending) {
                            if (!punchCombination.hasStarted()) {
                                punchCombination.recordStartTime();
                            }
                            startCommand(punchCombination.getNextCommand());
                            isCommandPending = false;
                        } else if ((punchCombination != null)
                                && punchCombination.hasStarted()
                                && punchCombination.canPlayMoreCommands()) {
                            startCommand(punchCombination.getNextCommand());
                        }
                    }
                });
    }

    protected boolean isFightActive() {
        return isFightActive;
    }

    @Override
    public void onRoundStart() {
        isFightActive = true;
        onFightStart();
    }

    @Override
    public void onRoundEnd() {

    }

    @Override
    public void onLastRoundEnd() {
        if ((dispatcher != null) && !dispatcher.isStopped()) {
            dispatcher.stop();
        }
        isFightActive = false;
        onFightDone();
    }

    protected abstract void onFightStart();

    protected abstract void onScoreHit(PunchCombination punchCombination);

    protected abstract void onScoreMiss();

    protected abstract void onScoreTimeout(PunchCombination punchCombination);

    protected abstract void onFightDone();

    protected abstract void onFightAborted();
}
