package com.playposse.heavybagzombie;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.annotation.Nullable;
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
        play(context, message, null);
    }

    public static void play(Context context, Message message, @Nullable Callback callback) {
        Log.i(LOG_CAT, "Playing sound " + message.name());
        switch (message) {
            case hit:
                play(context, R.raw.hit2, callback);
                break;
            case heavy:
                play(context, R.raw.heavy, callback);
            case miss:
                play(context, R.raw.miss, callback);
                break;
            case tooSlow:
                play(context, R.raw.too_slow, callback);
                break;
            case readyFight:
                play(context, R.raw.ready_fight, callback);
                break;
            case stop:
                play(context, R.raw.stop, callback);
                break;
            case one:
                play(context, R.raw.one, callback);
                break;
            case two:
                play(context, R.raw.two, callback);
                break;
            case three:
                play(context, R.raw.three, callback);
                break;
            case four:
                play(context, R.raw.four, callback);
                break;
            case five:
                play(context, R.raw.five, callback);
                break;
            case six:
                play(context, R.raw.six, callback);
                break;
        }
    }

    private static void play(Context context, int resId, @Nullable final Callback callback) {
        MediaPlayer mediaPlayer = MediaPlayer.create(context, resId);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                }
                if (callback != null) {
                    callback.onComplete();
                }
            }
        });
        mediaPlayer.start();
    }

    /**
     * An optional callback for when the sound clip finished playing.
     */
    public interface Callback {
        void onComplete();
    }
}
