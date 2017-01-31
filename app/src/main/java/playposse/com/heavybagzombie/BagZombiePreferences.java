package playposse.com.heavybagzombie;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

import be.tarsos.dsp.onsets.PercussionOnsetDetector;

/**
 * Helper class that makes application preferences accessible.
 */
public final class BagZombiePreferences {

    private static final String LOG_CAT = BagZombiePreferences.class.getSimpleName();

    public static final String PREFS_NAME = "BagZombiePreferences";

    private static final String SENSITIVITY_KEY = "sensitivity";
    private static final String THRESHOLD_KEY = "threshold";

    private static final String NULL_STRING = "-1";

    public static Integer getSensitivity(Context context) {
        Integer sensitivity = getInt(context, SENSITIVITY_KEY);
        int defaultSensitivity = (int) PercussionOnsetDetector.DEFAULT_SENSITIVITY;
        return (sensitivity != null) ? sensitivity : defaultSensitivity;
    }

    public static void setSensitivity(Context context, Integer sensitivity) {
        setInt(context, SENSITIVITY_KEY, sensitivity);
    }

    public static Integer getThreshold(Context context) {
        Integer threshold = getInt(context, THRESHOLD_KEY);
        int defaultThreshold = (int) PercussionOnsetDetector.DEFAULT_THRESHOLD;
        return (threshold != null) ? threshold : defaultThreshold;
    }

    public static void setThreshold(Context context, Integer threshold) {
        setInt(context, THRESHOLD_KEY, threshold);
    }

    private static String getString(Context context, String key) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String str = sharedPreferences.getString(key, NULL_STRING);
        return (!NULL_STRING.equals(str)) ? str : null;
    }

    private static void setString(Context context, String key, String value) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (value != null) {
            sharedPreferences.edit().putString(key, value).commit();
        } else {
            sharedPreferences.edit().remove(key).commit();
        }
    }

    private static Integer getInt(Context context, String key) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Integer value = sharedPreferences.getInt(key, -1);
        return (value != -1) ? value : null;
    }

    private static void setInt(Context context, String key, Integer value) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (value != null) {
            sharedPreferences.edit().putInt(key, value).commit();
        } else {
            sharedPreferences.edit().remove(key).commit();
        }
    }

    private static boolean getBoolean(Context context, String key, boolean defaultValue) {
        try {
            SharedPreferences sharedPreferences =
                    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getBoolean(key, defaultValue);
        } catch (ClassCastException ex) {
            setBoolean(context, key, defaultValue);
            return false;
        }
    }

    private static void setBoolean(Context context, String key, boolean value) {
        Log.i(LOG_CAT, "Setting preference boolean for key " + key + " to " + value);
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences
                .edit()
                .putBoolean(key, value)
                .commit();
    }

    private static Set<Long> getLongSet(Context context, String key) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> set = sharedPreferences.getStringSet(key, null);

        if ((set == null) || (set.size() == 0)) {
            return new HashSet<>();
        }

        Set<Long> result = new HashSet<>(set.size());
        for (String value : set) {
            result.add(Long.valueOf(value));
        }
        return result;
    }

    private static void setLongSet(Context context, String key, Set<Long> set) {
        Set<String> stringSet = new HashSet<>(set.size());
        for (Long value : set) {
            stringSet.add(value.toString());
        }

        SharedPreferences sharedPreferences =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putStringSet(key, stringSet).commit();
    }

    private static void addValueToLongSet(Context context, String key, Long value) {
        Set<Long> set = getLongSet(context, key);
        set.add(value);
        setLongSet(context, key, set);
    }
}

