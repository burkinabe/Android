package com.accurascandemo;

import android.app.Application;
import android.graphics.Bitmap;

import com.accurascandemo.R;
import com.androidnetworking.AndroidNetworking;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.inet.facelock.callback.FaceDetectionResult;

import io.fabric.sdk.android.Fabric;
import okhttp3.OkHttpClient;

public class AccuraDemoApplication extends Application {
    static { //for facematch
        try {
            System.loadLibrary("accurafacem");
//			System.loadLibrary("ExtractEngine");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static GoogleAnalytics sAnalytics;
    private static Tracker tracker;
    private static AccuraDemoApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        mInstance = this;
        sAnalytics = GoogleAnalytics.getInstance(this);

        initFastNetwork();
    }

    private void initFastNetwork() {
        AndroidNetworking.initialize(getApplicationContext());
    }

    public static synchronized AccuraDemoApplication getInstance() {
        return mInstance;
    }

    synchronized public void getDefaultTracker() {
        if (tracker == null) {
            tracker = sAnalytics.newTracker(R.xml.global_tracker);
        }
    }

    public void reportToGoogleAnalytics(String screenName) {
        getDefaultTracker();
        tracker.setScreenName(screenName);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }


    public static int mMenuMode;
    public static int MENU_MODE_OCR = 1;
    public static int MENU_MODE_FACE = 2;
    public static int MENU_MODE_SCAN = 3;
    ////////////////////////
    //face match
    ////////////////////////////
    Bitmap bitmap;

    FaceDetectionResult detectionResult = null;

    int faceCount;

    public int getFaceCount() {
        return this.faceCount;
    }

    public void setFaceCount(int c) {
        this.faceCount = c;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void setFaceDetectionResult(FaceDetectionResult result) {
        this.detectionResult = result;
    }

    public FaceDetectionResult getFaceDetectionResult() {
        return this.detectionResult;
    }

}
