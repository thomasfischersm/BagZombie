package playposse.com.heavybagzombie.service.fight.impl;

import android.util.Log;

import java.util.Random;

import playposse.com.heavybagzombie.VocalPlayer;
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
        VocalPlayer.play(getContext(), VocalPlayer.Message.readyFight);
        scheduleRandomCommand();
    }

    @Override
    protected void onScoreHit(VocalPlayer.Message command, long reactionTime) {
        Log.i(LOG_CAT, "Scored hit for " + command);
        getFightStatsSaver().saveHit(command.name(), reactionTime);
        VocalPlayer.play(getContext(), VocalPlayer.Message.hit);
        scheduleRandomCommand();
    }

    @Override
    protected void onScoreMiss() {
        Log.i(LOG_CAT, "Scored miss");
        VocalPlayer.play(getContext(), VocalPlayer.Message.miss);
        getFightStatsSaver().saveMiss();
    }

    @Override
    protected void onFightDone() {
        VocalPlayer.play(getContext(), VocalPlayer.Message.stop);
    }

    @Override
    protected void onFightAborted() {
        VocalPlayer.play(getContext(), VocalPlayer.Message.stop);
    }

    private void scheduleRandomCommand() {
        long delay = new Random().nextInt(3_000) + 500;

        final VocalPlayer.Message command;
        switch (new Random().nextInt(6)) {
            case 0:
                command = VocalPlayer.Message.one;
                break;
            case 1:
                command = VocalPlayer.Message.two;
                break;
            case 2:
                command = VocalPlayer.Message.three;
                break;
            case 3:
                command = VocalPlayer.Message.four;
                break;
            case 4:
                command = VocalPlayer.Message.five;
                break;
            case 5:
                command = VocalPlayer.Message.six;
                break;
            default:
                command = VocalPlayer.Message.one;
                break;
        }

        scheduleCommand(command, delay);
        Log.i(LOG_CAT, "Issued command " + command.name());
    }
}
