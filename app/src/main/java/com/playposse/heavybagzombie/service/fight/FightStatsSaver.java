package com.playposse.heavybagzombie.service.fight;

import android.content.ContentValues;
import android.content.Context;

import com.playposse.heavybagzombie.provider.BagZombieContract;
import com.playposse.heavybagzombie.service.fight.impl.PunchCombination;

import static com.playposse.heavybagzombie.provider.BagZombieContract.ResetFightStatsAction;
import static com.playposse.heavybagzombie.provider.BagZombieContract.SaveHitAction;
import static com.playposse.heavybagzombie.provider.BagZombieContract.SaveMissAction;
import static com.playposse.heavybagzombie.provider.BagZombieContract.SaveTimeoutAction;
import static com.playposse.heavybagzombie.provider.BagZombieContract.StartRoundAction;
import static com.playposse.heavybagzombie.provider.BagZombieContract.UpdateFightStateAction;

/**
 * A helper class that saves fight stats to the content provider.
 */
public class FightStatsSaver {

    private final Context context;

    public FightStatsSaver(Context context) {
        this.context = context;
    }

    public void saveHit(PunchCombination punchCombination, boolean isHeavyHit) {
        ContentValues values = punchCombination.toContentValues();
        values.put(SaveHitAction.IS_HEAVY_HIT_COLUMN, isHeavyHit);
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

    public void resetFightStats() {
        ContentValues values = new ContentValues();
        context.getContentResolver().insert(ResetFightStatsAction.CONTENT_URI, values);
    }

    public void updateFightState(int fightState, int fightTimer, int currentRound) {
        ContentValues values = new ContentValues();
        values.put(UpdateFightStateAction.FIGHT_STATE_COLUMN, fightState);
        values.put(UpdateFightStateAction.TIMER_COLUMN, fightTimer);
        values.put(UpdateFightStateAction.CURRENT_ROUND_COLUMN, currentRound);
        context.getContentResolver().update(UpdateFightStateAction.CONTENT_URI, values, null, null);
    }

    public void startRound(int roundIndex) {
        ContentValues values = new ContentValues();
        values.put(StartRoundAction.ROUND_INDEX_COLUMN, roundIndex);
        context.getContentResolver().insert(StartRoundAction.CONTENT_URI, values);
    }
}
