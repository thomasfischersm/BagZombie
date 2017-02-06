package playposse.com.heavybagzombie;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

/**
 * Media player that plays a voice command.
 */
public class VocalPlayer {

    private static final String LOG_CAT = VocalPlayer.class.getSimpleName();

    public static enum Message {
        hit,
        heavy,
        miss,
        tooSlow,
        readyFight,
        stop,
        one,
        two,
        three,
        four,
        five,
        six,
    }

    public static void play(Context context, Message message) {
        Log.i(LOG_CAT, "Playing sound " + message.name());
        switch (message) {
            case hit:
                play(context, R.raw.hit2);
                break;
            case heavy:
                play(context, R.raw.heavy);
            case miss:
                play(context, R.raw.miss);
                break;
            case tooSlow:
                play(context, R.raw.too_slow);
                break;
            case readyFight:
                play(context, R.raw.ready_fight);
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
            case three:
                play(context, R.raw.three);
                break;
            case four:
                play(context, R.raw.four);
                break;
            case five:
                play(context, R.raw.five);
                break;
            case six:
                play(context, R.raw.six);
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
