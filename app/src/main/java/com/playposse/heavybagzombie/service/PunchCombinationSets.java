package com.playposse.heavybagzombie.service;

import android.content.Context;

import com.playposse.heavybagzombie.BagZombiePreferences;
import com.playposse.heavybagzombie.service.fight.impl.PunchCombination;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Collection of default punch combinations for manual fights.
 */
public class PunchCombinationSets {

    public static String[] getSinglePunches() {
        return new String[]{"1", "2", "3", "4", "5", "6"};
    }

    public static String[] getTwoPunchCombos() {
        return new String[]{"1 2", "2 1", "3 4", "4 3", "5 6 ", "6 5"};
    }

    public static String[] getPopularFourPunchCombos() {
        return new String[]{"1", "2", "3", "4", "5", "6"};
    }

    public static String[] getEveryCombo() {
        int max = 6*6*6*6 + 1;
        String[] comboStrings = new String[max];
        for (int i = 0; i < max; i++) {
            int number = Integer.parseInt(Integer.toString(i, 6)) + 1;
            String numberString = Integer.toString(number);
            comboStrings[i] = numberString.replaceAll("([123456])(?=[123456])", "$1 ");
        }
        return comboStrings;
    }

    public static String[] getCustomComboSet(Context context) {
        Set<String> punchCombinationSet = BagZombiePreferences.getCustomComboSet(context);
        return punchCombinationSet.toArray(new String[punchCombinationSet.size()]);
    }

    public static String[] getPunchCombinations(Context context, int punchCombinationIndex) {
        switch (punchCombinationIndex) {
            case 0:
                return getSinglePunches();
            case 1:
                return getTwoPunchCombos();
            case 2:
                return getPopularFourPunchCombos();
            case 3:
                return getEveryCombo();
            case 4:
                return getCustomComboSet(context);
            default:
                throw new IllegalArgumentException("There is no punch combination type " + punchCombinationIndex);
        }
    }
}
