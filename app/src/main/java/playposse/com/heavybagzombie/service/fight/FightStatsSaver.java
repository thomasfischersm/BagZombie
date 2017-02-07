package playposse.com.heavybagzombie.service.fight;

import android.content.ContentValues;
import android.content.Context;

import playposse.com.heavybagzombie.provider.BagZombieContract;

import static playposse.com.heavybagzombie.provider.BagZombieContract.SaveHitAction;
import static playposse.com.heavybagzombie.provider.BagZombieContract.SaveMissAction;
import static playposse.com.heavybagzombie.provider.BagZombieContract.SaveTimeoutAction;

/**
 * A helper class that saves fight stats to the content provider.
 */
public class FightStatsSaver {

    private final Context context;

    public FightStatsSaver(Context context) {
        this.context = context;
    }

    public void saveHit(String command, long delay) {
        ContentValues values = new ContentValues();
        values.put(SaveHitAction.COMMAND_COLUMN, command);
        values.put(SaveHitAction.DELAY_COLUMN, delay);
        context.getContentResolver().insert(SaveHitAction.CONTENT_URI, values);
    }

    public void saveMiss() {
        ContentValues values = new ContentValues();
        context.getContentResolver().insert(SaveMissAction.CONTENT_URI, values);
    }

    public void saveTimeout(String command) {
        ContentValues values = new ContentValues();
        values.put(SaveTimeoutAction.COMMAND_COLUMN, command);
        context.getContentResolver().insert(SaveTimeoutAction.CONTENT_URI, values);
    }
}
