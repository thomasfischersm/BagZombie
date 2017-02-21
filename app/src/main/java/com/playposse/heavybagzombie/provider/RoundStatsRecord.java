package com.playposse.heavybagzombie.provider;

import android.database.Cursor;
import android.os.Bundle;

import com.playposse.heavybagzombie.util.CursorUtil;

import static com.playposse.heavybagzombie.provider.BagZombieContract.RoundStatsTable;

/**
 * A data class that the {@link BagZombieContentProvider} uses to store stats about a round.
 */
public class RoundStatsRecord {

    // -1 means that it is the summary round.
    private final int roundIndex;

    private int hitCount = 0;
    private int heavyHitCount = 0;
    private int missCount = 0;
    private int timeoutCount = 0;
    private Long fastestReactionTime = null;
    private Long averageReactionTime = null;

    public RoundStatsRecord(int roundIndex) {
        this.roundIndex = roundIndex;
    }

    public RoundStatsRecord(Cursor cursor) {
        roundIndex = CursorUtil.getInt(cursor, RoundStatsTable.ROUND_INDEX_COLUMN);
        hitCount = CursorUtil.getInt(cursor, RoundStatsTable.HIT_COUNT_COLUMN);
        heavyHitCount = CursorUtil.getInt(cursor, RoundStatsTable.HEAVY_HIT_COUNT_COLUMN);
        missCount = CursorUtil.getInt(cursor, RoundStatsTable.MISS_COUNT_COLUMN);
        timeoutCount = CursorUtil.getInt(cursor, RoundStatsTable.TIMEOUT_COUNT_COLUMN);
        fastestReactionTime = CursorUtil.getLong(cursor, RoundStatsTable.FASTEST_REACTION_TIME_COLUMN);
        averageReactionTime = CursorUtil.getLong(cursor, RoundStatsTable.AVERAGE_REACTION_TIME_COLUMN);
    }

    public RoundStatsRecord(Bundle args) {
        roundIndex = args.getInt(RoundStatsTable.ROUND_INDEX_COLUMN);
        hitCount = args.getInt(RoundStatsTable.HIT_COUNT_COLUMN);
        heavyHitCount = args.getInt(RoundStatsTable.HEAVY_HIT_COUNT_COLUMN);
        missCount = args.getInt(RoundStatsTable.MISS_COUNT_COLUMN);
        timeoutCount = args.getInt(RoundStatsTable.TIMEOUT_COUNT_COLUMN);
        fastestReactionTime = args.getLong(RoundStatsTable.FASTEST_REACTION_TIME_COLUMN);
        averageReactionTime = args.getLong(RoundStatsTable.AVERAGE_REACTION_TIME_COLUMN);
    }

    public void addMiss() {
        missCount++;
    }

    public void addTimeout() {
        timeoutCount++;
    }

    public void addHit(boolean isHeavyHit, long reactionTime) {
        hitCount++;
        if (isHeavyHit) {
            heavyHitCount++;
        }

        if (fastestReactionTime == null) {
            fastestReactionTime = reactionTime;
            averageReactionTime = reactionTime;
        } else {
            fastestReactionTime = Math.min(fastestReactionTime, reactionTime);
            averageReactionTime = (averageReactionTime * (hitCount - 1) + reactionTime) / hitCount;
        }
    }

    public int getRoundIndex() {
        return roundIndex;
    }

    public int getHitCount() {
        return hitCount;
    }

    public int getHeavyHitCount() {
        return heavyHitCount;
    }

    public int getMissCount() {
        return missCount;
    }

    public Long getFastestReactionTime() {
        return fastestReactionTime;
    }

    public Long getAverageReactionTime() {
        return averageReactionTime;
    }

    public int getTimeoutCount() {
        return timeoutCount;
    }

    public Object[] toArray() {
        return new Object[]{
                roundIndex,
                roundIndex,
                hitCount,
                heavyHitCount,
                missCount,
                timeoutCount,
                fastestReactionTime,
                averageReactionTime};
    }

    public Bundle toBundle() {
        Bundle args = new Bundle();
        args.putInt(RoundStatsTable.ROUND_INDEX_COLUMN, roundIndex);
        args.putInt(RoundStatsTable.HIT_COUNT_COLUMN, hitCount);
        args.putInt(RoundStatsTable.HEAVY_HIT_COUNT_COLUMN, heavyHitCount);
        args.putInt(RoundStatsTable.MISS_COUNT_COLUMN, missCount);
        args.putInt(RoundStatsTable.TIMEOUT_COUNT_COLUMN, timeoutCount);
        args.putLong(RoundStatsTable.FASTEST_REACTION_TIME_COLUMN, fastestReactionTime);
        args.putLong(RoundStatsTable.AVERAGE_REACTION_TIME_COLUMN, averageReactionTime);
        return args;
    }
}
