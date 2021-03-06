package com.playposse.heavybagzombie.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * The contract for the content provider {@link BagZombieContentProvider}.
 */
public final class BagZombieContract {

    public static final String AUTHORITY = "com.playposse.heavybagzombie";

    public static final class FightTable implements BaseColumns {

        public static final String PATH = "fight";
        public static final Uri CONTENT_URI = createContentUri(PATH);

        public static final String HIT_COUNT_COLUMN = "hitCount";
        public static final String MISS_COUNT_COLUMN = "missCount";
        public static final String TIMEOUT_COUNT_COLUMN = "timeoutCount";
        public static final String FIGHT_STATE_COLUMN = "fightState";
        public static final String TIMER_COLUMN = "timer";
        public static final String CURRENT_ROUND_COLUMN = "currentRound";

        public static final String[] COLUMN_NAMES = new String[]{
                HIT_COUNT_COLUMN,
                MISS_COUNT_COLUMN,
                TIMEOUT_COUNT_COLUMN,
                FIGHT_STATE_COLUMN,
                TIMER_COLUMN,
                CURRENT_ROUND_COLUMN};

        public static final int ACTIVE_FIGHT_STATE = 1;
        public static final int REST_FIGHT_STATE = 2;
        public static final int NO_FIGHT_STATE = 3;
        public static final int STOPPED_FIGHT_STATE = 4;
    }

    public static final class RoundStatsTable implements BaseColumns {

        public static final String PATH = "roundStats";
        public static final Uri CONTENT_URI = createContentUri(PATH);

        public static final String ROUND_INDEX_COLUMN = "roundIndex";
        public static final String HIT_COUNT_COLUMN = "hitCount";
        public static final String HEAVY_HIT_COUNT_COLUMN = "heavyHitCount";
        public static final String MISS_COUNT_COLUMN = "missCount";
        public static final String TIMEOUT_COUNT_COLUMN = "timeoutCount";
        public static final String FASTEST_REACTION_TIME_COLUMN = "fastestReactionTime";
        public static final String AVERAGE_REACTION_TIME_COLUMN = "averageReactionTime";

        public static final String[] COLUMN_NAMES = new String[]{
                _ID,
                ROUND_INDEX_COLUMN,
                HIT_COUNT_COLUMN,
                HEAVY_HIT_COUNT_COLUMN,
                MISS_COUNT_COLUMN,
                TIMEOUT_COUNT_COLUMN,
                FASTEST_REACTION_TIME_COLUMN,
                AVERAGE_REACTION_TIME_COLUMN};
    }

    public static final class HitRecordTable implements  BaseColumns {

        public static final String PATH = "hitRecord";
        public static final Uri CONTENT_URI = createContentUri(PATH);

        public static final String COMMAND_COLUMN = "command";
        public static final String OVERALL_REACTION_TIME_COLUMN = "delay";
        public static final String REACTION_TIME_0 = "reactionTime0";
        public static final String REACTION_TIME_1 = "reactionTime1";
        public static final String REACTION_TIME_2 = "reactionTime2";
        public static final String REACTION_TIME_3 = "reactionTime3";

        public static final String[] COLUMN_NAMES = new String[]{
                _ID,
                COMMAND_COLUMN,
                OVERALL_REACTION_TIME_COLUMN,
                REACTION_TIME_0,
                REACTION_TIME_1,
                REACTION_TIME_2,
                REACTION_TIME_3};
    }

    public static final class SaveHitAction {

        public static final String PATH = "saveHit";
        public static final Uri CONTENT_URI = createContentUri(PATH);

        public static final String COMMAND_COLUMN = "command";
        public static final String OVERALL_REACTION_TIME_COLUMN = "overallReactionTime";
        public static final String REACTION_TIME_0 = "reactionTime0";
        public static final String REACTION_TIME_1 = "reactionTime1";
        public static final String REACTION_TIME_2 = "reactionTime2";
        public static final String REACTION_TIME_3 = "reactionTime3";
        public static final String IS_HEAVY_HIT_COLUMN = "isHeavyHit";
    }

    public static final class SaveMissAction {

        public static final String PATH = "saveMiss";
        public static final Uri CONTENT_URI = createContentUri(PATH);
    }

    public static final class SaveTimeoutAction {

        public static final String PATH = "saveTimeout";
        public static final Uri CONTENT_URI = createContentUri(PATH);

        public static final String COMMAND_COLUMN = "command";
    }

    public static final class ResetFightStatsAction {

        public static final String PATH = "resetFightStats";
        public static final Uri CONTENT_URI = createContentUri(PATH);
    }

    public static final class UpdateFightStateAction {

        public static final String PATH = "updateFightState";
        public static final Uri CONTENT_URI = createContentUri(PATH);

        public static final String FIGHT_STATE_COLUMN = "fightState";
        public static final String TIMER_COLUMN = "timer";
        public static final String CURRENT_ROUND_COLUMN = "currentRound";

    }

    public static final class StartRoundAction {

        public static final String PATH = "startRound";
        public static final Uri CONTENT_URI = createContentUri(PATH);

        public static final String ROUND_INDEX_COLUMN = "roundIndex";
    }

    public static final class StopFightAction {

        public static final String PATH = "stopFight";
        public static final Uri CONTENT_URI = createContentUri(PATH);
    }

    private static Uri createContentUri(String path) {
        return android.net.Uri.parse("content://" + AUTHORITY + "/" + path);

    }
}
