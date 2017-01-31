package playposse.com.heavybagzombie;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Media player that plays a voice command.
 */
public class CommandPlayer {

    public static enum Command {
        hit,
        one,
        two,
    }

    public static void play(Context context, Command command) {
        switch (command) {
            case hit:
                play(context, R.raw.hit);
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
