package playposse.com.heavybagzombie.provider;

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

        public static final String[] COLUMN_NAMES = new String[]{
                HIT_COUNT_COLUMN,
                MISS_COUNT_COLUMN,
                TIMEOUT_COUNT_COLUMN};
    }

    public static final class HitRecordTable implements  BaseColumns {

        public static final String PATH = "hitRecord";
        public static final Uri CONTENT_URI = createContentUri(PATH);

        public static final String COMMAND_COLUMN = "command";
        public static final String DELAY_COLUMN = "delay";

        public static final String[] COLUMN_NAMES = new String[]{
                _ID,
                COMMAND_COLUMN,
                DELAY_COLUMN};
    }

    public static final class SaveHitAction {

        public static final String PATH = "saveHit";
        public static final Uri CONTENT_URI = createContentUri(PATH);

        public static final String COMMAND_COLUMN = "command";
        public static final String DELAY_COLUMN = "delay";
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

    private static Uri createContentUri(String path) {
        return android.net.Uri.parse("content://" + AUTHORITY + "/" + path);

    }
}
