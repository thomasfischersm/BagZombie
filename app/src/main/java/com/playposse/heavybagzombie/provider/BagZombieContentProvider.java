package com.playposse.heavybagzombie.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.playposse.heavybagzombie.provider.BagZombieContract.FightTable;
import static com.playposse.heavybagzombie.provider.BagZombieContract.FightTable.STOPPED_FIGHT_STATE;
import static com.playposse.heavybagzombie.provider.BagZombieContract.HitRecordTable;
import static com.playposse.heavybagzombie.provider.BagZombieContract.ResetFightStatsAction;
import static com.playposse.heavybagzombie.provider.BagZombieContract.RoundStatsTable;
import static com.playposse.heavybagzombie.provider.BagZombieContract.UpdateFightStateAction;
import static com.playposse.heavybagzombie.provider.BagZombieContract.SaveHitAction;
import static com.playposse.heavybagzombie.provider.BagZombieContract.SaveMissAction;
import static com.playposse.heavybagzombie.provider.BagZombieContract.SaveTimeoutAction;
import static com.playposse.heavybagzombie.provider.BagZombieContract.StartRoundAction;
import static com.playposse.heavybagzombie.provider.BagZombieContract.StopFightAction;
import static com.playposse.heavybagzombie.provider.BagZombieContract.FightTable.NO_FIGHT_STATE;

/**
 * A {@link ContentProvider} that provides information about the current fight.
 */
public class BagZombieContentProvider extends ContentProvider {

    private static final int FIGHT_TABLE_CODE = 1;
    private static final int HIT_RECORD_TABLE_CODE = 2;
    private static final int SAVE_HIT_CODE = 3;
    private static final int SAVE_MISS_CODE = 4;
    private static final int SAVE_TIMEOUT_CODE = 5;
    private static final int RESET_FIGHT_STATS_CODE = 6;
    private static final int UPDATE_FIGHT_STATE_CODE = 7;
    private static final int ROUND_STATS_TABLE_CODE = 8;
    private static final int START_ROUND_ACTION_CODE = 9;
    private static final int STOP_FIGHT_ACTION_CODE = 10;

    private final UriMatcher uriMatcher;

    private int hitCount = 0;
    private int missCount = 0;
    private int timeoutCount = 0;
    private int fightState = NO_FIGHT_STATE;
    private int fightTimer = 0;
    private int currentRound = 0;
    private List<HitRecord> hitRecords = new ArrayList<>();
    private List<RoundStatsRecord> roundStatsRecords = initRoundStatsRecord();

    public BagZombieContentProvider() {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(BagZombieContract.AUTHORITY, FightTable.PATH, FIGHT_TABLE_CODE);
        uriMatcher.addURI(BagZombieContract.AUTHORITY, HitRecordTable.PATH, HIT_RECORD_TABLE_CODE);
        uriMatcher.addURI(BagZombieContract.AUTHORITY, SaveHitAction.PATH, SAVE_HIT_CODE);
        uriMatcher.addURI(BagZombieContract.AUTHORITY, SaveMissAction.PATH, SAVE_MISS_CODE);
        uriMatcher.addURI(BagZombieContract.AUTHORITY, SaveTimeoutAction.PATH, SAVE_TIMEOUT_CODE);
        uriMatcher.addURI(BagZombieContract.AUTHORITY, ResetFightStatsAction.PATH, RESET_FIGHT_STATS_CODE);
        uriMatcher.addURI(BagZombieContract.AUTHORITY, UpdateFightStateAction.PATH, UPDATE_FIGHT_STATE_CODE);
        uriMatcher.addURI(BagZombieContract.AUTHORITY, RoundStatsTable.PATH, ROUND_STATS_TABLE_CODE);
        uriMatcher.addURI(BagZombieContract.AUTHORITY, StartRoundAction.PATH, START_ROUND_ACTION_CODE);
        uriMatcher.addURI(BagZombieContract.AUTHORITY, StopFightAction.PATH, STOP_FIGHT_ACTION_CODE);
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(
            @NonNull Uri uri,
            String[] projection,
            String selection,
            String[] selectionArgs,
            String sortOrder) {

        MatrixCursor cursor = null;

        switch (uriMatcher.match(uri)) {
            case FIGHT_TABLE_CODE:
                cursor = new MatrixCursor(FightTable.COLUMN_NAMES, 1);
                cursor.addRow(new Object[]{
                        hitCount,
                        missCount,
                        timeoutCount,
                        fightState,
                        fightTimer,
                        currentRound});
                break;
            case HIT_RECORD_TABLE_CODE:
                cursor = new MatrixCursor(HitRecordTable.COLUMN_NAMES, hitRecords.size());
                for (HitRecord hitRecord : hitRecords) {
                    cursor.addRow(new Object[]{
                            hitRecords.indexOf(hitRecord),
                            hitRecord.getCommand(),
                            hitRecord.getOverallReactionTime(),
                            hitRecord.getReactionTimes()[0],
                            hitRecord.getReactionTimes()[1],
                            hitRecord.getReactionTimes()[2],
                            hitRecord.getReactionTimes()[3]});
                }
                break;
            case ROUND_STATS_TABLE_CODE:
                cursor = new MatrixCursor(RoundStatsTable.COLUMN_NAMES, roundStatsRecords.size());
                for (RoundStatsRecord roundStatsRecord : roundStatsRecords) {
                    cursor.addRow(roundStatsRecord.toArray());
                }
                break;
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        if (getContext() == null) {
            throw new IllegalStateException("Context is null!");
        }

        RoundStatsRecord summaryRound = roundStatsRecords.get(0);
        RoundStatsRecord currentRound = roundStatsRecords.get(roundStatsRecords.size() - 1);

        switch (uriMatcher.match(uri)) {
            case SAVE_HIT_CODE:
                hitCount++;

                String hitCommand = values.getAsString(SaveHitAction.COMMAND_COLUMN);
                int overallReactionTime =
                        values.getAsInteger(SaveHitAction.OVERALL_REACTION_TIME_COLUMN);
                Integer reactionTime0 = values.getAsInteger(SaveHitAction.REACTION_TIME_0);
                Integer reactionTime1 = values.getAsInteger(SaveHitAction.REACTION_TIME_1);
                Integer reactionTime2 = values.getAsInteger(SaveHitAction.REACTION_TIME_2);
                Integer reactionTime3 = values.getAsInteger(SaveHitAction.REACTION_TIME_3);
                Boolean isHeavyHit = values.getAsBoolean(SaveHitAction.IS_HEAVY_HIT_COLUMN);

                summaryRound.addHit(isHeavyHit, overallReactionTime);
                currentRound.addHit(isHeavyHit, overallReactionTime);

                hitRecords.add(new HitRecord(
                        hitCommand,
                        overallReactionTime,
                        reactionTime0,
                        reactionTime1,
                        reactionTime2,
                        reactionTime3));

                getContext().getContentResolver().notifyChange(FightTable.CONTENT_URI, null);
                getContext().getContentResolver().notifyChange(RoundStatsTable.CONTENT_URI, null);
                getContext().getContentResolver().notifyChange(HitRecordTable.CONTENT_URI, null);
                break;
            case SAVE_MISS_CODE:
                missCount++;
                hitRecords.add(new HitRecord(null, -1, null, null, null, null));
                summaryRound.addMiss();
                currentRound.addMiss();

                getContext().getContentResolver().notifyChange(FightTable.CONTENT_URI, null);
                getContext().getContentResolver().notifyChange(RoundStatsTable.CONTENT_URI, null);
                getContext().getContentResolver().notifyChange(HitRecordTable.CONTENT_URI, null);
                break;
            case SAVE_TIMEOUT_CODE:
                String missCommand = values.getAsString(SaveTimeoutAction.COMMAND_COLUMN);
                timeoutCount++;
                hitRecords.add(new HitRecord(missCommand, -1, null, null, null, null));
                summaryRound.addTimeout();
                currentRound.addTimeout();

                getContext().getContentResolver().notifyChange(FightTable.CONTENT_URI, null);
                getContext().getContentResolver().notifyChange(RoundStatsTable.CONTENT_URI, null);
                getContext().getContentResolver().notifyChange(HitRecordTable.CONTENT_URI, null);
                break;
            case RESET_FIGHT_STATS_CODE:
                hitCount = 0;
                missCount = 0;
                timeoutCount = 0;
                fightState = NO_FIGHT_STATE;
                fightTimer = 0;
                this.currentRound = 0;
                hitRecords.clear();
                roundStatsRecords = initRoundStatsRecord();

                getContext().getContentResolver().notifyChange(FightTable.CONTENT_URI, null);
                getContext().getContentResolver().notifyChange(RoundStatsTable.CONTENT_URI, null);
                getContext().getContentResolver().notifyChange(HitRecordTable.CONTENT_URI, null);
                break;
            case START_ROUND_ACTION_CODE:
                Integer roundIndex = values.getAsInteger(StartRoundAction.ROUND_INDEX_COLUMN);
                if (roundIndex != (roundStatsRecords.size() - 1)) {
                    throw new IllegalArgumentException("Got START_ROUND_ACTION_CODE for round "
                            + roundIndex + ", but the round records are already of size "
                            + roundStatsRecords.size());
                }
                roundStatsRecords.add(new RoundStatsRecord(roundIndex));
                getContext().getContentResolver().notifyChange(RoundStatsTable.CONTENT_URI, null);
                break;
            case STOP_FIGHT_ACTION_CODE:
                fightState = STOPPED_FIGHT_STATE;
                getContext().getContentResolver().notifyChange(FightTable.CONTENT_URI, null);
                break;
        }

        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(
            @NonNull Uri uri,
            ContentValues values,
            String selection,
            String[] selectionArgs) {

        if (getContext() == null) {
            throw new IllegalStateException("Context is null!");
        }

        switch (uriMatcher.match(uri)) {
            case UPDATE_FIGHT_STATE_CODE:
                fightState = values.getAsInteger(UpdateFightStateAction.FIGHT_STATE_COLUMN);
                fightTimer = values.getAsInteger(UpdateFightStateAction.TIMER_COLUMN);
                currentRound = values.getAsInteger(UpdateFightStateAction.CURRENT_ROUND_COLUMN);

                getContext().getContentResolver().notifyChange(FightTable.CONTENT_URI, null);
                return 1;
        }

        return 0;
    }

    private List<RoundStatsRecord> initRoundStatsRecord() {
        ArrayList<RoundStatsRecord> roundStatsRecords = new ArrayList<>();
        roundStatsRecords.add(new RoundStatsRecord(-1)); // Add summary record.
        return roundStatsRecords;
    }
}
