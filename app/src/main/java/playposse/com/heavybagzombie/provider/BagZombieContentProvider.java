package playposse.com.heavybagzombie.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import static playposse.com.heavybagzombie.provider.BagZombieContract.FightTable;
import static playposse.com.heavybagzombie.provider.BagZombieContract.HitRecordTable;
import static playposse.com.heavybagzombie.provider.BagZombieContract.ResetFightStatsAction;
import static playposse.com.heavybagzombie.provider.BagZombieContract.SaveHitAction;
import static playposse.com.heavybagzombie.provider.BagZombieContract.SaveMissAction;
import static playposse.com.heavybagzombie.provider.BagZombieContract.SaveTimeoutAction;

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

    private final UriMatcher uriMatcher;

    private int hitCount = 0;
    private int missCount = 0;
    private int timeoutCount = 0;
    private List<HitRecord> hitRecords = new ArrayList<>();

    public BagZombieContentProvider() {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(BagZombieContract.AUTHORITY, FightTable.PATH, FIGHT_TABLE_CODE);
        uriMatcher.addURI(BagZombieContract.AUTHORITY, HitRecordTable.PATH, HIT_RECORD_TABLE_CODE);
        uriMatcher.addURI(BagZombieContract.AUTHORITY, SaveHitAction.PATH, SAVE_HIT_CODE);
        uriMatcher.addURI(BagZombieContract.AUTHORITY, SaveMissAction.PATH, SAVE_MISS_CODE);
        uriMatcher.addURI(BagZombieContract.AUTHORITY, SaveTimeoutAction.PATH, SAVE_TIMEOUT_CODE);
        uriMatcher.addURI(BagZombieContract.AUTHORITY, ResetFightStatsAction.PATH, RESET_FIGHT_STATS_CODE);
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(
            Uri uri,
            String[] projection,
            String selection,
            String[] selectionArgs,
            String sortOrder) {

        MatrixCursor cursor = null;

        switch (uriMatcher.match(uri)) {
            case FIGHT_TABLE_CODE:
                cursor = new MatrixCursor(BagZombieContract.FightTable.COLUMN_NAMES, 1);
                cursor.addRow(new Object[]{hitCount, missCount, timeoutCount});
                break;
            case HIT_RECORD_TABLE_CODE:
                cursor = new MatrixCursor(BagZombieContract.HitRecordTable.COLUMN_NAMES, 1);
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

        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
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

                hitRecords.add(new HitRecord(
                        hitCommand,
                        overallReactionTime,
                        reactionTime0,
                        reactionTime1,
                        reactionTime2,
                        reactionTime3));

                getContext().getContentResolver().notifyChange(FightTable.CONTENT_URI, null);
                getContext().getContentResolver().notifyChange(HitRecordTable.CONTENT_URI, null);
                break;
            case SAVE_MISS_CODE:
                missCount++;
                getContext().getContentResolver().notifyChange(FightTable.CONTENT_URI, null);
                break;
            case SAVE_TIMEOUT_CODE:
                String missCommand = values.getAsString(SaveTimeoutAction.COMMAND_COLUMN);
                timeoutCount++;
                hitRecords.add(new HitRecord(missCommand, -1, null, null, null, null));

                getContext().getContentResolver().notifyChange(FightTable.CONTENT_URI, null);
                break;
            case RESET_FIGHT_STATS_CODE:
                hitCount = 0;
                missCount = 0;
                timeoutCount = 0;
                hitRecords.clear();

                getContext().getContentResolver().notifyChange(FightTable.CONTENT_URI, null);
                getContext().getContentResolver().notifyChange(HitRecordTable.CONTENT_URI, null);
                break;
        }

        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Integer hitIncrement = values.getAsInteger(FightTable.HIT_COUNT_COLUMN);
        if (hitIncrement != null) {
            hitCount += hitIncrement;
        }

        Integer missIncrement = values.getAsInteger(FightTable.MISS_COUNT_COLUMN);
        if (missIncrement != null) {
            missCount += missIncrement;
        }

        getContext().getContentResolver().notifyChange(FightTable.CONTENT_URI, null);

        return 1;
    }
}
