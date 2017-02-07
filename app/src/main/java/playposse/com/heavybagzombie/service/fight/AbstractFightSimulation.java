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

    private VocalPlayer.Message currentCommand;
    private VocalPlayer.Message pendingCommand;
    private Long commandStart;
    TimerTask commandTask;
    TimerTask timeoutTask;
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

        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        dispatcher.stop();
                        isFightActive = false;
                        onFightDone();
                    }
                },
                duration);

        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(SAMPLE_RATE, BUFFER_SIZE, 0);

        OnsetHandler onsetHandler = new OnsetHandler() {
            @Override
            public void handleOnset(double time, double salience) {
                long now = System.currentTimeMillis();
                if (commandStart != null) {
                    Log.i(LOG_CAT, "Register hit for command " + currentCommand.name());
                    timeoutTask.cancel();

                    long bufferSizeInMs = BUFFER_SIZE * 1_000 / SAMPLE_RATE;
                    long bufferProcessed = (long) (time * 1_000);
                    VocalPlayer.Message command = currentCommand;
                    long reactionTime = now - commandStart;

                    // Reset for the next command.
                    Log.i(LOG_CAT, "Set currentCommand to null");
                    currentCommand = null;
                    commandStart = null;

                    onScoreHit(command, reactionTime);
                } else {
                    onScoreMiss();
                }
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

    @Override
    public final void stopFight() {
        dispatcher.stop();
        timer.cancel();
        isFightActive = false;
        onFightAborted();
    }

    /**
     * Schedules a command. The command will be played after the {@code delayMs}. If the player
     * doesn't make a hit within the {@code timeout}, the command is canceled as failed.
     */
    protected final void scheduleCommand(
            final VocalPlayer.Message message,
            long delayMs,
            long timeout) {

        currentCommand = message;
        pendingCommand = null;
        Log.i(LOG_CAT, "Schedule currentCommand: " + message.name());

        commandTask = new TimerTask() {
            @Override
            public void run() {
                if (isSoundActive) {
                    pendingCommand = message;
                } else {
                    startCommand(message);
                }
            }
        };
        timer.schedule(commandTask, delayMs);

        timeoutTask = new TimerTask() {
            @Override
            public void run() {
                Log.i(LOG_CAT, "Execute timeout task");
                commandTask.cancel();
                VocalPlayer.Message command = currentCommand;
                currentCommand = null;
                commandStart = null;

                onScoreTimeout(command);
            }
        };
        timer.schedule(timeoutTask, timeout);
        Log.i(LOG_CAT, "Scheduled timeout task. " + timeout);
    }

    private void startCommand(VocalPlayer.Message message) {
        playSound(message);
        commandStart = System.currentTimeMillis();
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
                        if (pendingCommand != null) {
                            startCommand(pendingCommand);
                            pendingCommand = null;
                        }
                    }
                });
    }

    protected boolean isFightActive() {
        return isFightActive;
    }

    protected abstract void onFightStart();

    protected abstract void onScoreHit(VocalPlayer.Message command, long reactionTime);

    protected abstract void onScoreMiss();

    protected abstract void onScoreTimeout(VocalPlayer.Message command);

    protected abstract void onFightDone();

    protected abstract void onFightAborted();
}