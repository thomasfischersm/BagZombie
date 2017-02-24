package com.playposse.heavybagzombie.util;

import android.app.Application;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.playposse.heavybagzombie.BagZombieApplication;

/**
 * A helper class to deal with Google Analytics.
 */
public abstract class AnalyticsUtil {

    private AnalyticsUtil() {}

    public static void sendEvent(Application application, String category, String action) {
        BagZombieApplication lsystemApplication = (BagZombieApplication) application;
        Tracker tracker = lsystemApplication.getDefaultTracker();
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .build());
    }
}
