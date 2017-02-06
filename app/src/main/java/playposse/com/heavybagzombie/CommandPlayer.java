package playposse.com.heavybagzombie;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import playposse.com.heavybagzombie.service.FightEngineService;

/**
 * Media player that plays a voice command.
 */
public class CommandPlayer {

    private static final String LOG_CAT = CommandPlayer.class.getSimpleName();

    public static enum Command {
        hit,
        miss,
        readyfight,
        stop,
        one,
        two,
    }

    public static void play(Context context, Command command) {
        Log.i(LOG_CAT, "Playing sound " + command.name());
        switch (command) {
            case hit:
                play(context, R.raw.hit2);
                break;
            case miss:
                play(context, R.raw.miss);
                break;
            case readyfight:
                play(context, R.raw.readyfight);
                break;
            case stop:
                play(context, R.raw.stop);
                break;
            case one:
                play(context, R.raw.one);
                break;
            case two:
                play(context, R.raw.two);
                break;
        }
    }

    private static void play(Context context, int resId) {
        MediaPlayer mediaPlayer = MediaPlayer.create(context, resId);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                }
            }
        });
        mediaPlayer.start();
    }
}
