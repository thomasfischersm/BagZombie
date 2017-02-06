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
import playposse.com.heavybagzombie.CommandPlayer;

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

    private CommandPlayer.Command currentCommand;
    private Long commandStart;

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
                    long bufferSizeInMs = BUFFER_SIZE * 1_000 / SAMPLE_RATE;
                    long bufferProcessed = (long) (time * 1_000);
                    CommandPlayer.Command command = currentCommand;
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

        onFightStart();
    }

    @Override
    public final void stopFight() {
        dispatcher.stop();
        timer.cancel();
        onFightAborted();
    }

    protected final void scheduleCommand(final CommandPlayer.Command command, long delayMs) {
        currentCommand = command;
        Log.i(LOG_CAT, "Schedule currentCommand: " + command.name());

        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        CommandPlayer.play(context, command);
                        commandStart = System.currentTimeMillis();
                    }
                },
                delayMs);
    }

    protected Context getContext() {
        return context;
    }

    protected FightStatsSaver getFightStatsSaver() {
        return fightStatsSaver;
    }

    protected abstract void onFightStart();

    protected abstract void onScoreHit(CommandPlayer.Command command, long reactionTime);

    protected abstract void onScoreMiss();

    protected abstract void onFightDone();

    protected abstract void onFightAborted();
}
