package com.ad.weather;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by anastasiia on 23.12.17.
 */

public class SharedPreferencesHelper {

    private static final String APP_KEY = "weather.appdata";
    private static SharedPreferences preferences;

    private static SharedPreferences getDefault() {
        if (preferences == null) {
            preferences = Weather.getInstance()
                    .getSharedPreferences(APP_KEY, Context.MODE_PRIVATE);
        }
        return preferences;
    }

    public static SharedPreferences.Editor edit() {
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = getDefault().edit();
        return editor;
    }

    public static String getString(String key) {
        return getDefault().getString(key, "");
    }

    public static int getInt(String key, int defValue) {
        return getDefault().getInt(key, defValue);
    }

    public static float getFloat(String key, int defValue) {
        return getDefault().getFloat(key, defValue);
    }

    public static long getLong(String key, int defValue) {
        return getDefault().getLong(key, defValue);
    }

}
