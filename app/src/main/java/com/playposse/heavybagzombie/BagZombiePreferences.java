package com.playposse.heavybagzombie;

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
    private static final String CUSTOM_ROUND_COUNT_KEY = "customRoundCount";
    private static final String CUSTOM_ROUND_DURATION_KEY = "customRoundDuration";
    private static final String CUSTOM_REST_DURATION_KEY = "customRestDuration";
    private static final String CUSTOM_COMBO_CHOICE_KEY = "customComboChoice";
    private static final String CUSTOM_COMBO_SET_KEY = "customComboSet";

    private static final int DEFAULT_ROUND_COUNT = 3;
    private static final int DEFAULT_ROUND_DURATION = 60;
    private static final int DEFAULT_REST_DURATION = 30;
    private static final int DEFAULT_CUSTOM_COMBO_CHOICE = 0;

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

    public static Integer getCustomRoundCount(Context context) {
        Integer customRoundCount = getInt(context, CUSTOM_ROUND_COUNT_KEY);
        return (customRoundCount != null) ? customRoundCount : DEFAULT_ROUND_COUNT;
    }

    public static void setCustomRoundCount(Context context, Integer customRoundCount) {
        setInt(context, CUSTOM_ROUND_COUNT_KEY, customRoundCount);
    }

    public static Integer getCustomRoundDuration(Context context) {
        Integer customRoundDuration = getInt(context, CUSTOM_ROUND_DURATION_KEY);
        return (customRoundDuration != null) ? customRoundDuration : DEFAULT_ROUND_DURATION;
    }

    public static void setCustomRoundDuration(Context context, Integer customRoundDuration) {
        setInt(context, CUSTOM_ROUND_DURATION_KEY, customRoundDuration);
    }

    public static Integer getCustomRestDuration(Context context) {
        Integer customRestDuration = getInt(context, CUSTOM_REST_DURATION_KEY);
        return (customRestDuration != null) ? customRestDuration : DEFAULT_REST_DURATION;
    }

    public static void setCustomRestDuration(Context context, Integer customRestDuration) {
        setInt(context, CUSTOM_REST_DURATION_KEY, customRestDuration);
    }

    public static Integer getCustomComboChoice(Context context) {
        Integer customComboChoice = getInt(context, CUSTOM_COMBO_CHOICE_KEY);
        return (customComboChoice != null) ? customComboChoice : DEFAULT_CUSTOM_COMBO_CHOICE;
    }

    public static void setCustomComboChoice(Context context, Integer customComboChoice) {
        setInt(context, CUSTOM_COMBO_CHOICE_KEY, customComboChoice);
    }

    public static Set<String> getCustomComboSet(Context context) {
        return getStringSet(context, CUSTOM_COMBO_SET_KEY);
    }

    public static void setCustomComboSet(Context context, Set<String> customCombos) {
        setStringSet(context, CUSTOM_COMBO_SET_KEY, customCombos);
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

    private static Set<String> getStringSet(Context context, String key) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> stringSet = sharedPreferences.getStringSet(key, null);
        if (stringSet != null) {
            return stringSet;
        } else {
            return new HashSet<>();
        }
    }

    private static void setStringSet(Context context, String key, Set<String> set) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putStringSet(key, set).commit();
    }

    private static void addValueToStringSet(Context context, String key, String value) {
        Set<String> set = getStringSet(context, key);
        set.add(value);
        setStringSet(context, key, set);
    }
}

