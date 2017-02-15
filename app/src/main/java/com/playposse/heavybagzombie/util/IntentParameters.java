package com.playposse.heavybagzombie.util;

import android.content.Context;
import android.content.Intent;

import com.playposse.heavybagzombie.BagZombiePreferences;
import com.playposse.heavybagzombie.activity.ManualFightActivity;
import com.playposse.heavybagzombie.service.PunchCombinationSets;

import java.util.List;
import java.util.Set;

/**
 * A utility class that makes it easier to pass intents.
 */
public final class IntentParameters {

    public static final String ROUND_COUNT_EXTRA = "roundCount";
    public static final String ROUND_DURATION_EXTRA = "roundDuration";
    public static final String REST_DURATION_EXTRA = "restDuration";
    public static final String PUNCH_COMBINATIONS_EXTRA = "punchCombinations";

    private IntentParameters() {
    }

    public static Intent createManualFightIntent(Context context, int punchCombinationIndex) {
        int roundCount = BagZombiePreferences.getCustomRoundCount(context);
        int roundDuration = BagZombiePreferences.getCustomRoundDuration(context);
        int restDuration = BagZombiePreferences.getCustomRestDuration(context);
        String[] punchCombinationsArray =
                PunchCombinationSets.getPunchCombinations(context, punchCombinationIndex);


        Intent intent = new Intent(context, ManualFightActivity.class);
        intent.putExtra(ROUND_COUNT_EXTRA, roundCount);
        intent.putExtra(ROUND_DURATION_EXTRA, roundDuration);
        intent.putExtra(REST_DURATION_EXTRA, restDuration);
        intent.putExtra(PUNCH_COMBINATIONS_EXTRA, punchCombinationsArray);
        return intent;
    }
}
