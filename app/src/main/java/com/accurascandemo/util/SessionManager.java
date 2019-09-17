package com.accurascandemo.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.accurascandemo.model.SaveSyncDateTime;


/**
 * Created by richa on 27/4/17.
 */

public class SessionManager {
    private SharedPreferences mPrefs;

    public SessionManager(Context context) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean isLoggedIn() {
        return mPrefs.getBoolean(AppGeneral.PREFS_IS_LOGGEND_IN, false);
    }

    public void saveToken(String token, String user_Id) {
        SharedPreferences.Editor e = mPrefs.edit();
        e.putString(AppGeneral.PREFS_API_TOKEN, token + "");
        e.putString(AppGeneral.PREFS_USER_ID, user_Id + "");
        e.putBoolean(AppGeneral.PREFS_IS_LOGGEND_IN, true);
        e.commit();
    }

    public String getToken() {
        return mPrefs.getString(AppGeneral.PREFS_API_TOKEN, "");
    }

    public String getUserId() {
        return mPrefs.getString(AppGeneral.PREFS_USER_ID, "");
    }

    public void logout() {
        SharedPreferences.Editor e = mPrefs.edit();
        e.putBoolean(AppGeneral.PREFS_IS_LOGGEND_IN, false);
        e.putString(AppGeneral.PREFS_API_TOKEN, "");
    }

    public void saveLastSyncDatTime(SaveSyncDateTime saveSyncDateTime) {
        SharedPreferences.Editor e = mPrefs.edit();
        e.putString(AppGeneral.PREFS_SYNC_DATE, saveSyncDateTime.date + "");
        e.putString(AppGeneral.PREFS_SYNC_TIME, saveSyncDateTime.time + "");
        e.putBoolean(AppGeneral.PREFS_IS_LOGGEND_IN, true);
        e.commit();
    }

    public SaveSyncDateTime getLastSyncDateTime() {
        SaveSyncDateTime dateTime = new SaveSyncDateTime();
        dateTime.date = mPrefs.getString(AppGeneral.PREFS_SYNC_DATE, "");
        dateTime.time = mPrefs.getString(AppGeneral.PREFS_SYNC_TIME, "");
        return dateTime;
    }


    public void saveSDKToken(String sdkToken) {
        mPrefs.edit().putString(AppGeneral.PREFS_SDK_TOKEN, sdkToken).apply();
    }

    public String getSDKToken() {
        return mPrefs.getString(AppGeneral.PREFS_SDK_TOKEN, "9328f4d0-0a80-4ff9-9ca0-7d7a87ff7bb0");
    }
}
