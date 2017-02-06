package playposse.com.heavybagzombie.service.fight.impl;

import android.util.Log;

import java.util.Random;

import playposse.com.heavybagzombie.CommandPlayer;
import playposse.com.heavybagzombie.service.fight.AbstractFightSimulation;

/**
 * A fight that issues random commands.
 */
public class RandomFightSimulation extends AbstractFightSimulation {

    private static final String LOG_CAT = RandomFightSimulation.class.getSimpleName();

    public RandomFightSimulation(long duration) {
        super(duration);
    }

    @Override
    protected void onFightStart() {
        CommandPlayer.play(getContext(), CommandPlayer.Command.readyfight);
        scheduleRandomCommand();
    }

    @Override
    protected void onScoreHit(CommandPlayer.Command command, long reactionTime) {
        Log.i(LOG_CAT, "Scored hit for " + command);
        getFightStatsSaver().saveHit(command.name(), reactionTime);
        CommandPlayer.play(getContext(), CommandPlayer.Command.hit);
        scheduleRandomCommand();
    }

    @Override
    protected void onScoreMiss() {
        Log.i(LOG_CAT, "Scored miss");
        CommandPlayer.play(getContext(), CommandPlayer.Command.miss);
        getFightStatsSaver().saveMiss();
    }

    @Override
    protected void onFightDone() {
        CommandPlayer.play(getContext(), CommandPlayer.Command.stop);
    }

    @Override
    protected void onFightAborted() {
        CommandPlayer.play(getContext(), CommandPlayer.Command.stop);
    }

    private void scheduleRandomCommand() {
        long delay = new Random().nextInt(3_000) + 500;

        final CommandPlayer.Command command;
        switch (new Random().nextInt(2)) {
            case 0:
                command = CommandPlayer.Command.one;
                break;
            case 1:
                command = CommandPlayer.Command.two;
                break;
            default:
                command = CommandPlayer.Command.one;
                break;
        }

        scheduleCommand(command, delay);
        Log.i(LOG_CAT, "Issued command " + command.name());
    }
}
