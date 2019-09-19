/*
 * Copyright (C) 2016 The Android Open Source Project
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

package com.accurascandemo;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.renderscript.RenderScript;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.docrecog.scan.BitmapUtil;
import com.google.android.cameraview.CameraView;
import com.google.android.cameraview.CameraViewImpl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class CameraPanAddhaarActivity extends BaseActivity {

    private static final int PERMISSION_CODE_STORAGE = 3001;
    private static final int PERMISSION_CODE_CAMERA = 3002;

    CameraView cameraView;

    View shutterEffect;
    ImageView captureButton;
    View turnButton;

    private RenderScript rs;

    private boolean frameIsProcessing = false;

    private ImageView ivFrame;
    private Bitmap frameBitmap;
    static String imagePath;
    private boolean isLandScape;
    Camera camera;
    Bitmap bm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_pan_addhaar);

        //fetch the current orientation
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            isLandScape = true;
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            isLandScape = false;
        }

        cameraView = findViewById(R.id.camera_view);
        shutterEffect = findViewById(R.id.shutter_effect);
        captureButton = findViewById(R.id.shutter);
        captureButton.setImageResource(R.drawable.ic_camera_black);
        turnButton = findViewById(R.id.turn);
        ivFrame = findViewById(R.id.ivFrame);
        findViewById(R.id.ivBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.takePicture();
            }
        });

        turnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.switchCamera();
            }
        });

        rs = RenderScript.create(this);

        ivFrame.invalidate();
        BitmapDrawable drawable = (BitmapDrawable) ivFrame.getDrawable();
        frameBitmap = drawable.getBitmap();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
        setupCameraCallbacks();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE_STORAGE:
            case PERMISSION_CODE_CAMERA:
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
        if (requestCode != PERMISSION_CODE_STORAGE && requestCode != PERMISSION_CODE_CAMERA) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onPause() {
        cameraView.stop();
        super.onPause();
    }

    //CameraView override
    private void setupCameraCallbacks() {
        cameraView.setOnPictureTakenListener(new CameraViewImpl.OnPictureTakenListener() {
            @Override
            public void onPictureTaken(Bitmap bitmap, int rotationDegrees) {
                startSavingPhoto(bitmap, rotationDegrees);
            }
        });
        cameraView.setOnFocusLockedListener(new CameraViewImpl.OnFocusLockedListener() {
            @Override
            public void onFocusLocked() {
                playShutterAnimation();
            }
        });
        cameraView.setOnTurnCameraFailListener(new CameraViewImpl.OnTurnCameraFailListener() {
            @Override
            public void onTurnCameraFail(Exception e) {
                Toast.makeText(CameraPanAddhaarActivity.this, "Switch Camera Failed. Does you device has a front camera?",
                        Toast.LENGTH_SHORT).show();
            }
        });
        cameraView.setOnCameraErrorListener(new CameraViewImpl.OnCameraErrorListener() {
            @Override
            public void onCameraError(Exception e) {
                Toast.makeText(CameraPanAddhaarActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        cameraView.setOnFrameListener(new CameraViewImpl.OnFrameListener() {
            @Override
            public void onFrame(final byte[] data, final int width, final int height, int rotationDegrees) {
                if (frameIsProcessing) return;
                frameIsProcessing = true;
                Observable.fromCallable(new Callable<Bitmap>() {
                    @Override
                    public Bitmap call() throws Exception {
                        YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21, width, height, null);
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        yuvImage.compressToJpeg(new Rect(0, 0, width, height), 100, os);
                        byte[] jpegByteArray = os.toByteArray();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(jpegByteArray, 0, jpegByteArray.length);
                        return bitmap;
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Bitmap>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(Bitmap frameBitmap) {
                                if (frameBitmap != null) {
//                                    Log.i("onFrame", frameBitmap.getWidth() + ", " + frameBitmap.getHeight());
                                }
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {
                                frameIsProcessing = false;
                            }
                        });
            }
        });
    }

    private void playShutterAnimation() {
        shutterEffect.setVisibility(View.VISIBLE);
        shutterEffect.animate().alpha(0f).setDuration(300).setListener(
                new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        shutterEffect.setVisibility(View.GONE);
                        shutterEffect.setAlpha(0.8f);
                    }
                });
    }

    private Bitmap rotateBitmap(Bitmap imageBitmap, int angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(imageBitmap, imageBitmap.getWidth(), imageBitmap.getHeight(), true);
        return Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    //used for conver bitmap to file
    //parameter to pass : bitmap
    //return string path
    private String bitmapToFile(Bitmap bitmap) {
        Bitmap croppedBmp;
        frameBitmap = bitmap;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //crop pan and Addhar image in Lanscape mode.
            croppedBmp = BitmapUtil.centerCrop(bitmap, bitmap.getWidth() / 3, bitmap.getHeight() / 2);
        } else {
            //crop pan and Addhar image in portrait mode.
            croppedBmp = BitmapUtil.centerCrop(bitmap, bitmap.getWidth(), bitmap.getHeight() / 3);
        }
        croppedBmp = rotateBitmap(croppedBmp, 90);
        //create a file to write bitmap data
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "IMG_" + System.currentTimeMillis() + ".jpg");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        if (bitmap.getByteCount() > 2 * 1000000) {
//            croppedBmp.compress(Bitmap.CompressFormat.JPEG, 75, bos);
//        } else {
        croppedBmp.compress(Bitmap.CompressFormat.JPEG, 90, bos);
//        }
        byte[] bitmapData = bos.toByteArray();

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(bitmapData);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            if (BuildConfig.DEBUG) e.printStackTrace();
            return "";
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            bitmap.recycle();
            croppedBmp.recycle();
        }
        rotateBitmap(file.getAbsolutePath());
        imagePath = file.getAbsolutePath();
        return file.getAbsolutePath();
    }

    // Used for rotate the image
    //parameter to pass : String path
    private void rotateBitmap(String path) {
        ExifInterface exif;
        Bitmap taken_image = null;
        try {
            exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            int angle = 0;

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    angle = 90;
//                    rotateImage(frameBitmap, angle);
                    break;
                case 0:
                    angle = -90;
//                    rotateImage(frameBitmap, angle);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    angle = 180;
//                    rotateImage(frameBitmap, angle);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    angle = 270;
//                    rotateImage(frameBitmap, angle);
                    break;

            }


//            int result;
//            //int currentapiVersion = android.os.Build.VERSION.SDK_INT;
//            // do something for phones running an SDK before lollipop
//            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
//                result = (info.orientation + angle) % 360;
//                result = (360 - result) % 360; // compensate the mirror
//            } else { // back-facing
//                result = (info.orientation - angle + 360) % 360;
//            }
//
//            camera.setDisplayOrientation(result);

            if (angle != 0) {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
                Matrix matrix = new Matrix();
                matrix.postRotate(angle);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                FileOutputStream out = new FileOutputStream(path);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.close();
                bitmap.recycle();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Intent intent = new Intent();
            intent.putExtra("fileUrl", imagePath);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @SuppressLint("CheckResult")
    private void startSavingPhoto(final Bitmap bitmap, final int rotationDegrees) {
        Observable.fromCallable(new Callable<Bitmap>() {
            @Override
            public Bitmap call() throws Exception {
                Matrix matrix = new Matrix();
                matrix.postRotate(-rotationDegrees);
                return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }
        }).map(new Function<Bitmap, String>() {
            @Override
            public String apply(Bitmap bitmap) throws Exception {
                return bitmapToFile(bitmap);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String filePath) throws Exception {
                        if (filePath.isEmpty()) {
                            Toast.makeText(CameraPanAddhaarActivity.this, "Save image file failed :(", Toast.LENGTH_SHORT).show();
                        } else {

                            Intent intent = new Intent(CameraPanAddhaarActivity.this, PreviewScreenActivity.class);
                            intent.putExtra("fileUrl", imagePath);
                            startActivityForResult(intent, 111);
//                            Intent intent = new Intent();
//                            intent.putExtra("fileUrl", imagePath);
//                            setResult(RESULT_OK, intent);
//                            finish();
//                            notifyGallery(filePath);
                        }
                    }
                });
    }

    private void notifyGallery(String filePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(filePath);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }
}
