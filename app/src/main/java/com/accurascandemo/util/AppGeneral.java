package com.accurascandemo.util;

import android.content.Context;

/**
 * Created by richa on 27/4/17.
 */

public class AppGeneral {

    public static Context appContext;
    public static final String PREFS_IS_LOGGEND_IN = "is_logged_in";
    public static final String PREFS_USER_ID = "user_id";
    public static final String PREFS_API_TOKEN = "api_token";
    public static final String PREFS_SDK_TOKEN = "sdk_token";
    public static final String PREFS_SYNC_DATE = "sync_date";
    public static final String PREFS_SYNC_TIME = "sync_time";

    public static final int APP_LINK = 0;
    public static final int LINKED_IN_LINK = 1;
    public static final int FB_LINK = 2;
    public static final int TWITTER_LINK = 3;

    public static final String LivenessData = "LivenessData";
    public static final String AuthenticationData = "AuthenticationData";
    public static final String LastSavedData = "LastSavedData";

    public interface SCAN_RESULT {
        String ACCURA_SCAN = "Android - Test ACCURA";
        String ACCURA_MRZ = "Android - Test MRZ";
        String ACCURA_FM = "Android - Test FM";
    }

}
