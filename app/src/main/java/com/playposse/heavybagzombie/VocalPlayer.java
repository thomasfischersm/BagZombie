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
        final Voice voice;
        switch (BagZombiePreferences.getVoiceIndex(context)) {
            case 1:
                voice = new MaleVoice();
                break;
            case 2:
                voice = new FemaleVoice();
                break;
            case 3:
                voice = new DeveloperVoice();
                break;
            default:
                // Shouldn't happen.
                voice = new DeveloperVoice();
        }

        play(context, voice.getSoundResource(message), callback);
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

    private interface Voice {
        int getSoundResource(Message message);
    }

    private static class DeveloperVoice implements Voice {

        @Override
        public int getSoundResource(Message message) {
            switch (message) {
                case hit:
                    return R.raw.hit2;
                case heavy:
                    return R.raw.heavy;
                case miss:
                    return R.raw.miss;
                case tooSlow:
                    return R.raw.too_slow;
                case readyFight:
                    return R.raw.ready_fight;
                case stop:
                    return R.raw.stop;
                case one:
                    return R.raw.one;
                case two:
                    return R.raw.two;
                case three:
                    return R.raw.three;
                case four:
                    return R.raw.four;
                case five:
                    return R.raw.five;
                case six:
                    return R.raw.six;
                default:
                    throw new IllegalArgumentException("Unrecognized message: " + message.name());
            }
        }
    }

    private static class MaleVoice implements Voice {

        @Override
        public int getSoundResource(Message message) {
            switch (message) {
                case hit:
                    return R.raw.wadedauksch_hit;
                case heavy:
                    return R.raw.wadedauksch_heavy;
                case miss:
                    return R.raw.wadedauksch_miss;
                case tooSlow:
                    return R.raw.wadedauksch_too_slow;
                case readyFight:
                    return R.raw.wadedauksch_go;
                case stop:
                    return R.raw.wadedauksch_stop;
                case one:
                    return R.raw.wadedauksch_one;
                case two:
                    return R.raw.wadedauksch_two;
                case three:
                    return R.raw.wadedauksch_three;
                case four:
                    return R.raw.wadedauksch_four;
                case five:
                    return R.raw.wadedauksch_five;
                case six:
                    return R.raw.wadedauksch_six;
                default:
                    throw new IllegalArgumentException("Unrecognized message: " + message.name());
            }
        }
    }

    private static class FemaleVoice implements Voice {

        @Override
        public int getSoundResource(Message message) {
            switch (message) {
                case hit:
                    return R.raw.katabelle_hit;
                case heavy:
                    return R.raw.katabelle_heavy;
                case miss:
                    return R.raw.katabelle_miss;
                case tooSlow:
                    return R.raw.katabelle_too_slow;
                case readyFight:
                    return R.raw.katabelle_go;
                case stop:
                    return R.raw.katabelle_stop;
                case one:
                    return R.raw.katabelle_one;
                case two:
                    return R.raw.katabelle_two;
                case three:
                    return R.raw.katabelle_three;
                case four:
                    return R.raw.katabelle_four;
                case five:
                    return R.raw.katabelle_five;
                case six:
                    return R.raw.katabelle_six;
                default:
                    throw new IllegalArgumentException("Unrecognized message: " + message.name());
            }
        }
    }
}
