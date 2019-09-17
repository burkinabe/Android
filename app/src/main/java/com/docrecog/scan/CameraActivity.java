/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.docrecog.scan;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.AudioManager;
import android.media.CameraProfile;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accurascandemo.R;
import com.accurascandemo.ScanResultActivity;
import com.accurascandemo.util.DBHelper;
import com.accurascandemo.util.Utils;
import com.card.camera.CameraHolder;
import com.card.camera.FocusManager;
import com.card.camera.FocusManager.Listener;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
 *   This class is used to scan Passport & ID MRZ
 *   And result will be display using ScanResultActivity.java class.
 *
 * */

public class CameraActivity extends Activity implements
        SurfaceHolder.Callback, Camera.PreviewCallback, Camera.ShutterCallback,
        Camera.PictureCallback, Listener, OnTouchListener, View.OnClickListener {

    protected static final int IDLE = 0; // preview is active
    protected static final int SAVING_PICTURES = 5;
    private static final int PREVIEW_STOPPED = 0;
    // Focus is in progress. The exact focus state is in Focus.java.
    private static final int FOCUSING = 2;
    private static final int SNAPSHOT_IN_PROGRESS = 3;
    private static final int SELFTIMER_COUNTING = 4;
    private static final String TAG = "CameraActivity";
    private static final int FIRST_TIME_INIT = 0;
    private static final int CLEAR_SCREEN_DELAY = 1;
    private static final int SET_CAMERA_PARAMETERS_WHEN_IDLE = 4;
    // number clear
    private static final int TRIGER_RESTART_RECOG = 5;
    private static final int TRIGER_RESTART_RECOG_DELAY = 40; //30 ms
    // The subset of parameters we need to update in setCameraParameters().
    private static final int UPDATE_PARAM_INITIALIZE = 1;
    private static final int UPDATE_PARAM_PREFERENCE = 4;
    private static final int UPDATE_PARAM_ALL = -1;
    private static final long VIBRATE_DURATION = 200L;
    private static boolean LOGV = true;
    private static RecogEngine mCardScanner;
    private static int mRecCnt = 0; //counter for mrz detecting
    private Context mContext = null;
    private TextView mScanTitle = null;
    private ImageView mFlipImage = null;
    private final Lock _mutex = new ReentrantLock(true);

    /////////////////////
    //audio
    MediaPlayer mediaPlayer = null;
    AudioManager audioManager = null;

    final Handler mHandler = new Handler();
    private final CameraErrorCallback mErrorCallback = new CameraErrorCallback();
    private final AutoFocusCallback mAutoFocusCallback = new AutoFocusCallback();
    protected Camera mCameraDevice;
    // The first rear facing camera
    Parameters mParameters;
    SurfaceHolder mSurfaceHolder;
    // This handles everything about focus.
    FocusManager mFocusManager;
    int mPreviewWidth = 1280;//640;
    int mPreviewHeight = 720;//480;
    /*private CheckBox chkRecogType;*/
    private byte[] cameraData;
    private Parameters mInitialParams;
    private int mCameraState = PREVIEW_STOPPED;
    private int mCameraId;
    private boolean mOpenCameraFail = false;
    private boolean mCameraDisabled = false, isTouchCalled;
    Thread mCameraOpenThread = new Thread(new Runnable() {
        public void run() {
            try {
                mCameraDevice = Util.openCamera(CameraActivity.this, mCameraId);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                        requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
//                    }
//                }
            } catch (Exception e) {
                mOpenCameraFail = true;
                mCameraDisabled = true;
            }
        }
    });
    private boolean mOnResumePending;
    private boolean mPausing;
    private boolean mFirstTimeInitialized;
    // When setCameraParametersWhenIdle() is called, we accumulate the subsets
    // needed to be updated in mUpdateSet.
    private int mUpdateSet;
    private View mPreviewFrame;
    RelativeLayout rel_main;// Preview frame area for SurfaceView.
    /*private TextView mModeView, mPreviewSizeView, mPictureSizeView;*/
    private boolean mbVibrate;
    private Dialog dialog;
    private DBHelper dbHelper;
    // The display rotation in degrees. This is only valid when mCameraState is
    // not PREVIEW_STOPPED.
    private int mDisplayRotation;
    // The value for android.hardware.Camera.setDisplayOrientation.
    private int mDisplayOrientation;
    private boolean mIsAutoFocusCallback = false;
    Thread mCameraPreviewThread = new Thread(new Runnable() {
        public void run() {
            initializeCapabilities();
            startPreview();
        }
    });

    private static boolean isSupported(String value, List<String> supported) {
        return supported != null && supported.indexOf(value) >= 0;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (LOGV) Log.v(TAG, "onWindowFocusChanged.hasFocus=" + hasFocus
                + ".mOnResumePending=" + mOnResumePending);
        if (hasFocus && mOnResumePending) {
            doOnResume();
            mOnResumePending = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mbVibrate = true;
        if (LOGV) Log.v(TAG, "onResume. hasWindowFocus()=" + hasWindowFocus());
        if (mCameraDevice == null) {// && isKeyguardLocked()) {
            if (LOGV) Log.v(TAG, "onResume. mOnResumePending=true");
            mOnResumePending = true;
        } else {
            if (LOGV) Log.v(TAG, "onResume. mOnResumePending=false");
            int currentSDKVersion = Build.VERSION.SDK_INT;

            doOnResume();


            mOnResumePending = false;
        }
    }

    protected void doOnResume() {
        if (mOpenCameraFail || mCameraDisabled)
            return;

        // if (mRecogService != null && mRecogService.isProcessing())
        // showProgress(null);
        mRecCnt = 0;
        mPausing = false;

        // Start the preview if it is not started.
        if (mCameraState == PREVIEW_STOPPED) {
            try {
                mCameraDevice = Util.openCamera(this, mCameraId);
                initializeCapabilities();
                startPreview();
            } catch (Exception e) {
                Util.showErrorAndFinish(this, R.string.cannot_connect_camera);
                return;
            }
        }

        if (mSurfaceHolder != null) {
            // If first time initialization is not finished, put it in the
            // message queue.
            if (!mFirstTimeInitialized) {
                mHandler.sendEmptyMessage(FIRST_TIME_INIT);
            } else {
                initializeSecondTime();
            }
        }

        keepScreenOnAwhile();
        Log.i(TAG, "doOnresume end");
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.ivBack:
                onBackPressed();
                break;

            case R.id.tvExcel:
                dialog.dismiss();
                if (dbHelper.getScanData().size() > 0) {
                    Utils.exportDataToExcel(dbHelper, this);
                } else {
                    Toast.makeText(this, getString(R.string.text_no_data_export), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.tvPDF:
                dialog.dismiss();
                if (dbHelper.getScanData().size() > 0) {
                    Utils.exportDataToPDF(dbHelper, this);
                } else {
                    Toast.makeText(this, getString(R.string.text_no_data_export), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void showChooseOptionDialog() {
        if (dialog == null) {
            dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_export_chooser);
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int width = metrics.widthPixels - 50;
            dialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
            dialog.findViewById(R.id.tvExcel).setOnClickListener(this);
            dialog.findViewById(R.id.tvPDF).setOnClickListener(this);

            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog1) {
                    dialog = null;
                }
            });
            dialog.show();
        }
    }

    // Snapshots can only be taken after this is called. It should be called
    // once only. We could have done these things in onCreate() but we want to
    // make preview screen appear as soon as possible.
    private void initializeFirstTime() {
        if (mFirstTimeInitialized)
            return;

//		mOrientationListener = new MyOrientationEventListener(this);
//		mOrientationListener.enable();

        mCameraId = CameraHolder.instance().getBackCameraId();

        Util.initializeScreenBrightness(getWindow(), getContentResolver());
        mFirstTimeInitialized = true;
    }

    // If the activity is paused and resumed, this method will be called in
    // onResume.
    private void initializeSecondTime() {
        //mOrientationListener.enable();
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        keepScreenOnAwhile();
    }

    private void resetScreenOn() {
        mHandler.removeMessages(CLEAR_SCREEN_DELAY);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void keepScreenOnAwhile() {
        mHandler.removeMessages(CLEAR_SCREEN_DELAY);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DBHelper(this);
        mCameraId = CameraHolder.instance().getBackCameraId();
        //String str = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE;
        String[] defaultFocusModes = {"continuous-video", "auto", "continuous-picture"};
        mFocusManager = new FocusManager(defaultFocusModes);
        /*
         * To reduce startup time, we start the camera open and preview threads.
         * We make sure the preview is started at the end of onCreate.
         */
        mCameraOpenThread.start();

        // create and initialize the scan engine.
        if (mCardScanner == null) {
            mCardScanner = new RecogEngine();
            mCardScanner.initEngine(this);
        }
        mContext = this;
        //initialize the result value
        //{{{
        mRecCnt = 0;
        RecogEngine.g_recogResult.recType = RecType.INIT;
        RecogEngine.g_recogResult.bRecDone = false;
        RecogEngine.g_recogResult.bFaceReplaced = false;
        RecogEngine.g_recogResult.faceBitmap = null;
        RecogEngine.g_recogResult.docBackBitmap = null;
        RecogEngine.g_recogResult.docFrontBitmap = null;
        //}}}

        // Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_camera);
        findViewById(R.id.ivBack).setOnClickListener(this);

        mPreviewFrame = findViewById(R.id.camera_preview);
        rel_main = findViewById(R.id.rel_main);

        mPreviewFrame.setOnTouchListener(this);
        SurfaceView preview = findViewById(R.id.camera_preview);
        SurfaceHolder holder = preview.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mScanTitle = (TextView) findViewById(R.id.scantitle);
        mFlipImage = (ImageView) findViewById(R.id.ivFlipImage);
        mFlipImage.setVisibility(View.INVISIBLE);

        ///audio init
        mediaPlayer = MediaPlayer.create(CameraActivity.this, R.raw.beep);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // Make sure camera device is opened.
        try {
            mCameraOpenThread.join();
            mCameraOpenThread = null;
            if (mOpenCameraFail) {
                Util.showErrorAndFinish(this, R.string.cannot_connect_camera);
                return;
            } else if (mCameraDisabled) {
                Util.showErrorAndFinish(this, R.string.camera_disabled);
                return;
            }
        } catch (InterruptedException ex) {
            // ignore
        }

        mCameraPreviewThread.start();

        // do init
        // initializeZoomMax(mInitialParams);

        // Make sure preview is started.
        try {
            mCameraPreviewThread.join();
        } catch (InterruptedException ex) {
            // ignore
        }
        mCameraPreviewThread = null;
        requestCameraPermission();
    }

    @Override
    public void onDestroy() {

        // finalize the scan engine.
        if (mediaPlayer != null)
            mediaPlayer.release();

        super.onDestroy();
        // unregister receiver.
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void initializeCapabilities() {

        if (mCameraDevice != null)
            mInitialParams = mCameraDevice.getParameters();
//        mCameraDevice.autoFocus(new AutoFocusCallback());
        if (mInitialParams != null) {
            mInitialParams.getFocusMode();
            mFocusManager.initializeParameters(mInitialParams);
        }

        if (mCameraDevice != null)
            mParameters = mCameraDevice.getParameters();
    }

    private void startPreview() {

        if (mCameraDevice != null) {
            if (mPausing || isFinishing())
                return;

            mCameraDevice.setErrorCallback(mErrorCallback);

            // If we're previewing already, stop the preview first (this will blank
            // the screen).
            if (mCameraState != PREVIEW_STOPPED)
                stopPreview();

            setPreviewDisplay(mSurfaceHolder);
            setDisplayOrientation();

            mCameraDevice.setOneShotPreviewCallback(CameraActivity.this);
            setCameraParameters(UPDATE_PARAM_ALL);

            // Inform the mainthread to go on the UI initialization.
            if (mCameraPreviewThread != null) {
                synchronized (mCameraPreviewThread) {
                    mCameraPreviewThread.notify();
                }
            }

            try {
                Log.v(TAG, "startPreview");
                mCameraDevice.startPreview();
//                autoFocus();
            } catch (Throwable ex) {
                closeCamera();
//                throw new RuntimeException("startPreview failed", ex);
            }

            setCameraState(IDLE);

            // notify again to make sure main thread is wake-up.
            if (mCameraPreviewThread != null) {
                synchronized (mCameraPreviewThread) {
                    mCameraPreviewThread.notify();
                }
            }
        }
    }

    private void setPreviewDisplay(SurfaceHolder holder) {
        try {
            if (mCameraDevice != null) {
                mCameraDevice.setPreviewDisplay(holder);
            }
        } catch (Throwable ex) {
            closeCamera();
            throw new RuntimeException("setPreviewDisplay failed", ex);
        }
    }

    private void setDisplayOrientation() {
        mDisplayRotation = Util.getDisplayRotation(this);
        mDisplayOrientation = Util.getDisplayOrientation(mDisplayRotation,
                mCameraId);
        mCameraDevice.setDisplayOrientation(mDisplayOrientation);
    }

    private void stopPreview() {
        if (mCameraDevice == null)
            return;
        mCameraDevice.stopPreview();
        // mCameraDevice.setPreviewCallback(null);
        setCameraState(PREVIEW_STOPPED);
    }

    private void setCameraState(int state) {
        mCameraState = state;
    }

    private void closeCamera() {
        if (mCameraDevice != null) {
            CameraHolder.instance().release();
            mCameraDevice.setErrorCallback(null);
            mCameraDevice = null;
            setCameraState(PREVIEW_STOPPED);
            mFocusManager.onCameraReleased();
        }
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause");

        mOnResumePending = false;
        mPausing = true;

        mIsAutoFocusCallback = false;

        stopPreview();

        // Close the camera now because other activities may need to use it.
        closeCamera();
        resetScreenOn();

        // Remove the messages in the event queue.
        mHandler.removeMessages(FIRST_TIME_INIT);
        mHandler.removeMessages(TRIGER_RESTART_RECOG);

//		if (mFirstTimeInitialized)
//			mOrientationListener.disable();

        super.onPause();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // Make sure we have a surface in the holder before proceeding.
        if (holder.getSurface() == null) {
            Log.d(TAG, "holder.getSurface() == null");
            return;
        }

        // We need to save the holder for later use, even when the mCameraDevice
        // is null. This could happen if onResume() is invoked after this
        // function.
        mSurfaceHolder = holder;

        // The mCameraDevice will be null if it fails to connect to the camera
        // hardware. In this case we will show a dialog and then finish the
        // activity, so it's OK to ignore it.
        if (mCameraDevice == null)
            return;

        // Sometimes surfaceChanged is called after onPause or before onResume.
        // Ignore it.
        if (mPausing || isFinishing())
            return;

        // Set preview display if the surface is being created. Preview was
        // already started. Also restart the preview if display rotation has
        // changed. Sometimes this happens when the device is held in portrait
        // and camera app is opened. Rotation animation takes some time and
        // display rotation in onCreate may not be what we want.
        if (mCameraState == PREVIEW_STOPPED) {
            startPreview();
        } else {
            if (Util.getDisplayRotation(this) != mDisplayRotation) {
                setDisplayOrientation();
            }
            if (holder.isCreating()) {
                // Set preview display if the surface is being created and
                // preview
                // was already started. That means preview display was set to
                // null
                // and we need to set it now.
                setPreviewDisplay(holder);
            }
        }

        // If first time initialization is not finished, send a message to do
        // it later. We want to finish surfaceChanged as soon as possible to let
        // user see preview first.
        if (!mFirstTimeInitialized) {
            mHandler.sendEmptyMessage(FIRST_TIME_INIT);
        } else {
            initializeSecondTime();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (mCameraDevice != null) {
                Camera.Parameters p = mCameraDevice.getParameters();
                List<String> focusModes = p.getSupportedFocusModes();

//                if (focusModes != null && focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
//                    mCameraDevice.autoFocus(new AutoFocusCallback());
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        stopPreview();
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onShutter() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onPreviewFrame(final byte[] data, Camera camera) {
        // TODO Auto-generated method stub
//		Log.e(TAG, "onPreviewFrame mPausing=" + mPausing + ", mCameraState=" + mCameraState);

        if (mPausing) {
//			mCardScanner.isRecognizing = false;
            return;
        }

        if (mCameraState != IDLE) {
            mCameraDevice.setOneShotPreviewCallback(CameraActivity.this);
            return;
        } else {
//			mCardScanner.isRecognizing = true;
        }
        // generate jpeg image.
        final int width = camera.getParameters().getPreviewSize().width;
        final int height = camera.getParameters().getPreviewSize().height;
        final int format = camera.getParameters().getPreviewFormat();
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        final int deviceWidth = size.x;
        final int deviceHeight = size.y;

//		if(safeToTakePicture) {
//			safeToTakePicture = false;
        Thread recogThread = new Thread(new Runnable() {
            int ret;
            int faceret = 0; // detecting face return value
            Bitmap bmCard;

            //
            @Override
            public void run() {

                YuvImage temp = new YuvImage(data, format, width, height, null);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                temp.compressToJpeg(new Rect(0, 0, temp.getWidth(), temp.getHeight()), 100, os);
                Bitmap bmp_org = BitmapFactory.decodeByteArray(os.toByteArray(), 0, os.toByteArray().length);
                Matrix matrix = new Matrix();
                matrix.postRotate(mDisplayOrientation);


                Bitmap bmp1 = Bitmap.createBitmap(bmp_org, 0, 0, bmp_org.getWidth(), bmp_org.getHeight(), matrix, true);

                //Crop image before scan , send crop image to OCR SDK
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    //Crop image as per Frame size in landscape mode
                    bmCard = Bitmap.createScaledBitmap(bmp1, 757, 428, false);
                } else {
                    //center crop in portrait mode
                    bmCard = BitmapUtil.centerCrop(bmp1, bmp1.getWidth(), bmp1.getHeight() / 3);
                }

                bmp_org.recycle();
                bmp1.recycle();

                /*//{{{debug
                if (RecogEngine.g_recogResult.recType == RecType.INIT)
                    bmCard = BitmapFactory.decodeResource(getResources(), R.drawable.a);
                else if (RecogEngine.g_recogResult.recType == RecType.FACE)
                    bmCard = BitmapFactory.decodeResource(getResources(), R.drawable.b);
                //}}}*/
                _mutex.lock();
                if (RecogEngine.g_recogResult.recType == RecType.INIT) {
                    ret = mCardScanner.doRunData(bmCard, RecogEngine.facepick, mDisplayRotation, RecogEngine.g_recogResult);
                    if (ret <= 0 && mRecCnt > 2) {
                        // Bitmap docBmp = null;

                        if (mRecCnt % 4 == 1)
                            faceret = mCardScanner.doRunFaceDetect(bmCard, RecogEngine.g_recogResult);
                    }
                    mRecCnt++; //counter increases
                } else if (RecogEngine.g_recogResult.recType == RecType.FACE) { //have to do mrz
                    ret = mCardScanner.doRunData(bmCard, RecogEngine.facepick, mDisplayRotation, RecogEngine.g_recogResult);
                } else if (RecogEngine.g_recogResult.recType == RecType.MRZ) { //have to do face
                    if (mRecCnt > 2) {
                        Bitmap docBmp = bmCard;

                        if (mRecCnt % 5 == 1)
                            ret = mCardScanner.doRunFaceDetect(docBmp, RecogEngine.g_recogResult);
                    }
                    mRecCnt++;
                }
                _mutex.unlock();

                CameraActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (ret > 0) {
                            mRecCnt = 0; //counter sets 0
                            Bitmap docBmp = bmCard;

                            if ((RecogEngine.g_recogResult.recType == RecType.MRZ && !RecogEngine.g_recogResult.bRecDone) ||
                                    (RecogEngine.g_recogResult.recType == RecType.FACE && RecogEngine.g_recogResult.bRecDone)) {
                                if (RecogEngine.g_recogResult.bFaceReplaced) {
                                    RecogEngine.g_recogResult.docFrontBitmap = docBmp.copy(Bitmap.Config.ARGB_8888, false);
                                    RecogEngine.g_recogResult.docBackBitmap = null;
                                } else
                                    RecogEngine.g_recogResult.docBackBitmap = docBmp.copy(Bitmap.Config.ARGB_8888, false);
                            }

                            if (RecogEngine.g_recogResult.recType == RecType.BOTH ||
                                    RecogEngine.g_recogResult.recType == RecType.MRZ && RecogEngine.g_recogResult.bRecDone)
                                RecogEngine.g_recogResult.docFrontBitmap = docBmp.copy(Bitmap.Config.ARGB_8888, false);

                            docBmp.recycle();

                            if (RecogEngine.g_recogResult.bRecDone) {
                                showResultActivity();
                            } else {
                                mScanTitle.setText("Scan Front Side of Document");
                                flipImage();

                                if (!mPausing && mCameraDevice != null) {
                                    mCameraDevice.setOneShotPreviewCallback(CameraActivity.this);
                                }

                                mHandler.sendMessageDelayed(
                                        mHandler.obtainMessage(TRIGER_RESTART_RECOG),
                                        TRIGER_RESTART_RECOG_DELAY);
                            }
                        } else {
                            Log.d(TAG, "failed to ocr card image");
                            if (mRecCnt > 3 && faceret > 0) //detected only face, so need to detect mrz
                            {
                                mRecCnt = 0; //counter sets 0
                                faceret = 0;
                                RecogEngine.g_recogResult.recType = RecType.FACE;

                                mScanTitle.setText("Scan Back Side of Document");
                                flipImage();

                                Bitmap docBmp = bmCard;
                               /* if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                                    //Do some stuff
                                    docBmp = BitmapUtil.centerCrop(bmCard, bmCard.getWidth() / 3, bmCard.getHeight() / 2);
                                    //  RecogEngine.g_recogResult.docBitmap = BitmapUtil.centerCrop(bmCard, rel_main.getWidth()/5, rel_main.getHeight()/3);
                                } else {
                                    // RecogEngine.g_recogResult.docBitmap = BitmapUtil.centerCrop(bmCard, rel_main.getWidth()/2, rel_main.getHeight()/3);
                                    docBmp = BitmapUtil.centerCrop(bmCard, bmCard.getWidth(), bmCard.getHeight() / 3);
                                }*/
                                RecogEngine.g_recogResult.docFrontBitmap = docBmp.copy(Bitmap.Config.ARGB_8888, false);
                                docBmp.recycle();
                            }

                            if (!mPausing && mCameraDevice != null) {
                                mCameraDevice.setOneShotPreviewCallback(CameraActivity.this);
                            }

                            mHandler.sendMessageDelayed(
                                    mHandler.obtainMessage(TRIGER_RESTART_RECOG),
                                    TRIGER_RESTART_RECOG_DELAY);
                        }
                    }

                });
            }
        });
        recogThread.start();
//		}
    }

    ObjectAnimator anim = null;

    private void flipImage() {
        try {
            mFlipImage.setVisibility(View.VISIBLE);
            anim = (ObjectAnimator) AnimatorInflater.loadAnimator(mContext, R.animator.flipping);
            anim.setTarget(mFlipImage);
            anim.setDuration(1000);


            Animator.AnimatorListener animatorListener
                    = new Animator.AnimatorListener() {

                public void onAnimationStart(Animator animation) {
                    playEffect();
                }

                public void onAnimationRepeat(Animator animation) {

                }

                public void onAnimationEnd(Animator animation) {
                    mFlipImage.setVisibility(View.INVISIBLE);
                }

                public void onAnimationCancel(Animator animation) {

                }
            };

            anim.addListener(animatorListener);
            anim.start();
        } catch (Exception e) {

        }

    }

    private void updateCameraParametersInitialize() {
        // Reset preview frame rate to the maximum because it may be lowered by
        // video camera application.
        List<Integer> frameRates = mParameters.getSupportedPreviewFrameRates();
        if (frameRates != null) {
            Integer max = Collections.max(frameRates);
            mParameters.setPreviewFrameRate(max);
        }

        //mParameters.setRecordingHint(false);

        // Disable video stabilization. Convenience methods not available in API
        // level <= 14
        String vstabSupported = mParameters
                .get("video-stabilization-supported");
        if ("true".equals(vstabSupported)) {
            mParameters.set("video-stabilization", "false");
        }
    }

    private void updateCameraParametersPreference() {

        // Since change scene mode may change supported values,

        //mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
//        mModeView.setText(R.string.preview_mode);

        int camOri = CameraHolder.instance().getCameraInfo()[mCameraId].orientation;
        // Set the preview frame aspect ratio according to the picture size.
        Camera.Size size = mParameters.getPictureSize();
        double aspectWtoH = 0.0;
        if ((camOri == 0 || camOri == 180) && size.height > size.width) {
            aspectWtoH = (double) size.height / size.width;
        } else {
            aspectWtoH = (double) size.width / size.height;
        }

        if (LOGV)
            Log.e(TAG, "picture width=" + size.width + ", height=" + size.height);

        // Set a preview size that is closest to the viewfinder height and has the right aspect ratio.
        List<Camera.Size> sizes = mParameters.getSupportedPreviewSizes();
        Camera.Size optimalSize;
        //if (mode == SettingsActivity.CAPTURE_MODE)
        //	optimalSize = Util.getOptimalPreviewSize(this, sizes, aspectWtoH);
        //else
        {
            int requiredArea = mPreviewWidth * mPreviewHeight;

            //optimalSize = Util.getOptimalPreviewSize(this, sizes, aspectWtoH);
            optimalSize = Util.getOptimalPreviewSizeByArea(this, sizes, requiredArea);
        }

        // Camera.Size optimalSize = Util.getMaxPreviewSize(sizes, camOri);
        Camera.Size original = mParameters.getPreviewSize();

        Log.i(TAG, " Sensor[" + mCameraId + "]'s orientation is " + camOri);
        if (!original.equals(optimalSize)) {
            if (camOri == 0 || camOri == 180) {
                mParameters.setPreviewSize(optimalSize.height, optimalSize.width);
            } else {
                mParameters.setPreviewSize(optimalSize.width, optimalSize.height);
            }

            // Zoom related settings will be changed for different preview
            // sizes, so set and read the parameters to get lastest values

            if (mCameraDevice != null) {
                mCameraDevice.setParameters(mParameters);
                mParameters = mCameraDevice.getParameters();
            }
        }
        if (LOGV)
            Log.e(TAG, "Preview size is " + optimalSize.width + "x"
                    + optimalSize.height);

        String previewSize = "";
        previewSize = "[" + optimalSize.width + "x" + optimalSize.height + "]";
//        mPreviewSizeView.setText(previewSize);

        // Set JPEG quality.
        int jpegQuality = CameraProfile.getJpegEncodingQualityParameter(
                mCameraId, CameraProfile.QUALITY_HIGH);
        mParameters.setJpegQuality(jpegQuality);

        // For the following settings, we need to check if the settings are
        // still supported by latest driver, if not, ignore the settings.

        //if (Parameters.SCENE_MODE_AUTO.equals(mSceneMode))
        {
            if (mParameters != null) {
                // Set white balance parameter.
                String whiteBalance = "auto";
                if (isSupported(whiteBalance,
                        mParameters.getSupportedWhiteBalance())) {
                    mParameters.setWhiteBalance(whiteBalance);
                }

                String focusMode = mFocusManager.getFocusMode();
                mParameters.setFocusMode(focusMode);

                // Set exposure compensation
                int value = 0;
                int max = mParameters.getMaxExposureCompensation();
                int min = mParameters.getMinExposureCompensation();
                if (value >= min && value <= max) {
                    mParameters.setExposureCompensation(value);
                } else {
                    Log.w(TAG, "invalid exposure range: " + value);
                }
            }
        }


        if (mParameters != null) {
            // Set flash mode.
            String flashMode = "off";
            List<String> supportedFlash = mParameters.getSupportedFlashModes();
            if (isSupported(flashMode, supportedFlash)) {
                mParameters.setFlashMode(flashMode);
            }

            Log.e(TAG, "focusMode=" + mParameters.getFocusMode());
        }

    }

    // We separate the parameters into several subsets, so we can update only
    // the subsets actually need updating. The PREFERENCE set needs extra
    // locking because the preference can be changed from GLThread as well.
    private void setCameraParameters(int updateSet) {
        if (mCameraDevice != null) {
            mParameters = mCameraDevice.getParameters();

            if ((updateSet & UPDATE_PARAM_INITIALIZE) != 0) {
                updateCameraParametersInitialize();
            }


            if ((updateSet & UPDATE_PARAM_PREFERENCE) != 0) {
                updateCameraParametersPreference();
                mIsAutoFocusCallback = false;
            }

            if (mParameters != null)
                mCameraDevice.setParameters(mParameters);
        }
    }

    public void autoFocus() {

        if (mCameraDevice != null) {
            Camera.Parameters p = mCameraDevice.getParameters();
            List<String> focusModes = p.getSupportedFocusModes();

            if (focusModes != null && focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                if (FocusManager.isSupported(Parameters.FOCUS_MODE_AUTO, mParameters.getSupportedFocusModes())) {
                    mParameters.setFocusMode(Parameters.FOCUS_MODE_AUTO);
                    if (mCameraDevice != null) {
                        mCameraDevice.setParameters(mParameters);
                        mCameraDevice.autoFocus(mAutoFocusCallback);
                        setCameraState(FOCUSING);
                        isTouchCalled = false;
                    }
                }
            } else {
                isTouchCalled = false;
            }
        }

    }

    @Override
    public void cancelAutoFocus() {
        // TODO Auto-generated method stub
        mCameraDevice.cancelAutoFocus();
        if (mCameraState != SELFTIMER_COUNTING
                && mCameraState != SNAPSHOT_IN_PROGRESS) {
            setCameraState(IDLE);
        }
        setCameraParameters(UPDATE_PARAM_PREFERENCE);
        isTouchCalled = false;
    }

    @Override
    public boolean capture() {
        // If we are already in the middle of taking a snapshot then ignore.
        if (mCameraState == SNAPSHOT_IN_PROGRESS || mCameraDevice == null) {
            return false;
        }
        setCameraState(SNAPSHOT_IN_PROGRESS);

        return true;
    }

    @Override
    public void setFocusParameters() {
        // TODO Auto-generated method stub
        setCameraParameters(UPDATE_PARAM_PREFERENCE);
    }

    @Override
    public void playSound(int soundId) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onTouch(View v, MotionEvent e) {
//        if (mPausing || mCameraDevice == null || !mFirstTimeInitialized
//                || mCameraState == SNAPSHOT_IN_PROGRESS
//                || mCameraState == PREVIEW_STOPPED
//                || mCameraState == SAVING_PICTURES) {
//            return false;
//        }
        if (!isTouchCalled) {
            String focusMode = mParameters.getFocusMode();
            if (focusMode == null || Parameters.FOCUS_MODE_INFINITY.equals(focusMode)) {
                return false;
            }

            if (e.getAction() == MotionEvent.ACTION_UP) {
                isTouchCalled = true;
                autoFocus();
            }

        }

        //
        //return mFocusManager.onTouch(e);

        return true;
    }

    // If the Camera is idle, update the parameters immediately, otherwise
    // accumulate them in mUpdateSet and update later.
    private void setCameraParametersWhenIdle(int additionalUpdateSet) {
        mUpdateSet |= additionalUpdateSet;
        if (mCameraDevice == null) {
            // We will update all the parameters when we open the device, so
            // we don't need to do anything now.
            mUpdateSet = 0;
            return;
        } else if (isCameraIdle()) {
            setCameraParameters(mUpdateSet);
            mUpdateSet = 0;
        } else {
            if (!mHandler.hasMessages(SET_CAMERA_PARAMETERS_WHEN_IDLE)) {
                mHandler.sendEmptyMessageDelayed(
                        SET_CAMERA_PARAMETERS_WHEN_IDLE, 1000);
            }
        }
    }

    private boolean isCameraIdle() {
        return (mCameraState == IDLE || mFocusManager.isFocusCompleted());
    }

    /*When data scan successfully this method called and display result*/
    void showResultActivity() {

        Intent intent = new Intent();
        intent.setClass(this, ScanResultActivity.class);
        startActivity(intent);
        finish();

        playEffect();
    }

    void playEffect() {
        if (audioManager != null)
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer1) {
                //mediaPlayer.stop();
                //mediaPlayer.release();
            }
        });
    }

    public void requestCameraPermission() {
        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) &&
                        ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            1);

                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            1);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
        }
        switch (requestCode) {
            case 1:
                // If request is cancelled, the result arrays are empty.
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Start your camera handling here
                    try {
                        mCameraDevice = Util.openCamera(CameraActivity.this, mCameraId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(this, "You declined to allow the app to access your camera", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private class MainHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CLEAR_SCREEN_DELAY: {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    break;
                }
                case FIRST_TIME_INIT: {
                    initializeFirstTime();
                    break;
                }

                case SET_CAMERA_PARAMETERS_WHEN_IDLE: {
                    setCameraParametersWhenIdle(0);
                    break;
                }

                case TRIGER_RESTART_RECOG:
                    if (!mPausing)
                        mCameraDevice.setOneShotPreviewCallback(CameraActivity.this);
                    // clearNumberAreaAndResult();
                    break;
            }
        }
    }

    private final class AutoFocusCallback implements
            Camera.AutoFocusCallback {
        public void onAutoFocus(boolean focused, Camera camera) {
            if (mPausing)
                return;

            if (mCameraState == FOCUSING) {
                setCameraState(IDLE);
            }
            mFocusManager.onAutoFocus(focused);
            mIsAutoFocusCallback = true;

            String focusMode = mFocusManager.getFocusMode();
            mParameters.setFocusMode(focusMode);
            mCameraDevice.setParameters(mParameters);
            isTouchCalled = false;
//            autoFocus();
        }
    }

    public class CameraErrorCallback implements Camera.ErrorCallback {
        private static final String TAG = "CameraErrorCallback";

        public void onError(int error, Camera camera) {
            Log.e(TAG, "Got camera error callback. error=" + error);
            if (error == Camera.CAMERA_ERROR_SERVER_DIED) {
                // We are not sure about the current state of the app (in preview or
                // snapshot or recording). Closing the app is better than creating a
                // new Camera object.
                throw new RuntimeException("Media server died.");
            }
        }
    }


}
