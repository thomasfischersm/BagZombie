package com.playposse.heavybagzombie.service.fight.v2;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import com.playposse.heavybagzombie.VocalPlayer;
import com.playposse.heavybagzombie.service.fight.impl.PunchCombination;

/**
 * A queue that receives requests to play vocals. The vocals will be played one at a time.
 */
public class VocalQueueV2 {

    private static final String LOG_CAT = VocalQueueV2.class.getSimpleName();

    private final Context context;

    private final List<VocalTask> taskQueue = new LinkedList<>();

    private boolean isPlaying = false;

    public VocalQueueV2(Context context) {
        this.context = context;
    }

    public void scheduleVocal(VocalPlayer.Message message) {
        scheduleVocal(message, null);
    }

    public void scheduleVocal(VocalPlayer.Message message, @Nullable VocalQueueCallbackV2 callback) {
        Log.i(LOG_CAT, "Scheduled vocal: " + message.name());
        taskQueue.add(new VocalTask(message, null, callback));
        tickle();
    }

    public void scheduleVocal(PunchCombination punchCombination, VocalQueueCallbackV2 callback) {
        Log.i(LOG_CAT, "Scheduled punch combination: " + punchCombination.getCommandString());
        taskQueue.add(new VocalTask(null, punchCombination, callback));
        tickle();
    }

    private void tickle() {
        if (!isPlaying) {
            playNextVocal();
        }
    }

    private void playNextVocal() {
        Log.i(LOG_CAT, "Checking queue for next vocal to play.");
        if (taskQueue.isEmpty()) {
            Log.i(LOG_CAT, "No more vocals in queue");
            isPlaying = false;
            return;
        }

        isPlaying = true;
        VocalTask task = taskQueue.get(0);
        Log.i(LOG_CAT, "Playing next vocal: " + task);
        final VocalPlayer.Message message;
        if (task.getMessage() != null) {
            message = task.getMessage();
            taskQueue.remove(0);
        } else {
            PunchCombination punchCombination = task.getPunchCombination();
            message = punchCombination.getNextCommand();
            if (!punchCombination.canPlayMoreCommands()) {
                taskQueue.remove(0);
            }
        }

        Log.i(LOG_CAT, "Playing next vocal: " + message.name());
        VocalPlayer.play(
                context,
                message,
                new VocalPlayer.Callback() {
                    @Override
                    public void onComplete() {
                        playNextVocal();
                    }
                });

        VocalQueueCallbackV2 callback = task.getCallback();
        if (callback != null) {
            callback.onStartPlayback(message);
        }
    }

    private static final class VocalTask {

        @Nullable
        private final VocalPlayer.Message message;
        @Nullable
        private final PunchCombination punchCombination;
        private final VocalQueueCallbackV2 callback;

        private VocalTask(
                VocalPlayer.Message message,
                @Nullable PunchCombination punchCombination,
                @Nullable VocalQueueCallbackV2 callback) {
            this.message = message;
            this.punchCombination = punchCombination;
            this.callback = callback;
        }

        @Nullable
        public VocalPlayer.Message getMessage() {
            return message;
        }

        @Nullable
        public PunchCombination getPunchCombination() {
            return punchCombination;
        }

        public VocalQueueCallbackV2 getCallback() {
            return callback;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("Vocal Task (");
            if (punchCombination != null) {
                sb.append("punch combination: " + punchCombination.getCommandString());
            }
            if (message != null) {
                sb.append("message: " + message.name());
            }
            return sb.toString();
        }
    }
}
