package com.playposse.heavybagzombie.util;

import android.database.Cursor;

/**
 * A utility for dealing with {@link android.database.Cursor}s.
 */
public class CursorUtil {

    public static int getInt(Cursor cursor, String columnName) {
        return cursor.getInt(cursor.getColumnIndex(columnName));
    }

    public static long getLong(Cursor cursor, String columnName) {
        return cursor.getLong(cursor.getColumnIndex(columnName));
    }
}
