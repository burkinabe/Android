package com.accurascandemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.accurascandemo.api.HandleResponse;
import com.accurascandemo.api.RequestTask;
import com.accurascandemo.api.ZoomConnectedAPI;
import com.accurascandemo.model.AuthenticationData;
import com.accurascandemo.model.LivenessData;
import com.accurascandemo.model.PanAadharDetail;
import com.accurascandemo.model.ScanData;
import com.accurascandemo.util.AlertDialogAbstract;
import com.accurascandemo.util.AppGeneral;
import com.accurascandemo.util.ParsedResponse;
import com.accurascandemo.util.SendResultToServer;
import com.accurascandemo.util.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.docrecog.scan.RecogEngine;
import com.facetec.zoom.sdk.ZoomAuditTrailType;
import com.facetec.zoom.sdk.ZoomCustomization;
import com.facetec.zoom.sdk.ZoomSDK;
import com.facetec.zoom.sdk.ZoomSDKStatus;
import com.facetec.zoom.sdk.ZoomVerificationActivity;
import com.facetec.zoom.sdk.ZoomVerificationResult;
import com.facetec.zoom.sdk.ZoomVerificationStatus;
import com.inet.facelock.callback.FaceCallback;
import com.inet.facelock.callback.FaceDetectionResult;
import com.inet.facelock.callback.FaceLockHelper;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class PanAadharResultActivity extends BaseActivity implements View.OnClickListener, FaceCallback {

    FaceDetectionResult leftResult = null;
    FaceDetectionResult rightResult = null;
    float match_score = 0.0f;
    Bitmap face2 = null;
    private boolean isLiveness = false;
    private boolean isFaceMatch = false;
    private boolean isEmailSent = false;

    final private int CAPTURE_IMAGE = 2;
    private final ZoomSDK.InitializeCallback mInitializeCallback = new ZoomSDK.InitializeCallback() {
        @Override
        public void onCompletion(boolean successful) {
            if (successful) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        }
    };

    private ScrollView scrollView;
    private PanAadharDetail panAadharCardDetail;
    private ImageView ivUserProfile, ivScanImage;
    private ImageView ivUserProfile2;
    private TextView tvDocumentType, tvLastName, tvFirstName, tvDocNo, tvDOB, tvSex, tvAddress, tvSave, tvFM, tvCancel, tvDocNoTitle, tvCountry,
            tvAuth, tvLivenessAuthFacemap, tvLivenessEnrollFacemap, tvLivenessScore, tvLivenessAuthResultFacemap,
            tvLivenessEnrollResultFacemap, tvMatchScore, tvFaceMatchScore1, tvRetryFeedbackSuggestion, tvRetry;
    private LinearLayout llGender, llLastName, llFirstName, llAddress, llDOB, llDocumentNo, llFacemap, llLiveness, llFaceMatchScore;
    private int card_type;
    private ScanData scanData;
    private String imageFileUri;
    private File imageFile;
    private byte[] bytes;
    private String sessionId;
    private ZoomConnectedAPI zoomConnectedAPI;
    private String path = "";

    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pan_aadhar_result);

        initEngine();
        zoomConnectedAPI = new ZoomConnectedAPI(ZoomConnectedConfig.AppToken, getApplicationContext().getPackageName(), this);

        getDataFromIntent(getIntent());
        path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/IMG_0060.jpeg";
        initUI();
    }

    private void getDataFromIntent(Intent intent) {
        panAadharCardDetail = intent.getParcelableExtra("panAadharDetail");
        card_type = intent.getIntExtra("card_type", 0);
        imageFileUri = intent.getStringExtra("imageFile");
    }

    private void initUI() {
        scrollView = findViewById(R.id.scrollView);
        ivUserProfile = findViewById(R.id.ivUserProfile);
        ivUserProfile2 = findViewById(R.id.ivUserProfile2);
        tvDocumentType = findViewById(R.id.tvDocumentType);
        tvAuth = findViewById(R.id.tvAuth);
        tvRetry = findViewById(R.id.tvRetry);
        tvFM = findViewById(R.id.tvFM);
        tvFaceMatchScore1 = findViewById(R.id.tvFaceMatchScore1);
        tvRetryFeedbackSuggestion = findViewById(R.id.tvRetryFeedbackSuggestion);
        tvLivenessAuthFacemap = findViewById(R.id.tvLivenessAuthFacemap);
        tvLivenessEnrollFacemap = findViewById(R.id.tvLivenessEnrollFacemap);
        tvLivenessScore = findViewById(R.id.tvLivenessScore);
        tvLivenessAuthResultFacemap = findViewById(R.id.tvLivenessAuthResultFacemap);
        tvLivenessEnrollResultFacemap = findViewById(R.id.tvLivenessEnrollResultFacemap);
        tvMatchScore = findViewById(R.id.tvMatchScore);
        tvLastName = findViewById(R.id.tvLastName);
        tvFirstName = findViewById(R.id.tvFirstName);
        tvDocNo = findViewById(R.id.tvDocNo);
        tvDOB = findViewById(R.id.tvDOB);
        tvSave = findViewById(R.id.tvSave);
        tvCancel = findViewById(R.id.tvCancel);
        tvSex = findViewById(R.id.tvSex);
        llLiveness = findViewById(R.id.llLiveness);
        llFaceMatchScore = findViewById(R.id.llFaceMatchScore);
        llFacemap = findViewById(R.id.llFacemap);
        llGender = findViewById(R.id.llGender);
        llLastName = findViewById(R.id.llLastName);
        llFirstName = findViewById(R.id.llFirstName);
        llAddress = findViewById(R.id.llAddressLine1);
        tvAddress = findViewById(R.id.tvAddressLine1);
        llDOB = findViewById(R.id.llDOB);
        llDocumentNo = findViewById(R.id.llDocumentNo);
        ivScanImage = findViewById(R.id.ivScanImage);
        tvDocNoTitle = findViewById(R.id.tvDocNoTitle);
        tvCountry = findViewById(R.id.tvCountry);

        tvSave.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        tvFM.setOnClickListener(this);

        if (AccuraDemoApplication.mMenuMode == AccuraDemoApplication.MENU_MODE_OCR) {
            tvSave.setVisibility(View.GONE);
            tvFM.setVisibility(View.GONE);
        }
        setData();
    }

    private void setData() {
        scanData = new ScanData();
        imageFile = new File(imageFileUri);

        if (!TextUtils.isEmpty(panAadharCardDetail.scan_image)) {
            Glide.with(this)
                    .asBitmap()
                    .load(panAadharCardDetail.scan_image)
                    .into(getGlideTarget());
            ivUserProfile.setVisibility(View.VISIBLE);
        } else {
            ivUserProfile.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(panAadharCardDetail.card)) {
            tvDocumentType.setText(panAadharCardDetail.card);
            scanData.setDocumentType(panAadharCardDetail.card);
        }
        if (!TextUtils.isEmpty(panAadharCardDetail.name)) {
            tvFirstName.setText(panAadharCardDetail.name);
            scanData.setFirstName(panAadharCardDetail.name);
        }
        if (!TextUtils.isEmpty(panAadharCardDetail.date_of_birth)) {
            tvDOB.setText(panAadharCardDetail.date_of_birth);
            scanData.setDateOfBirth(panAadharCardDetail.date_of_birth);
        }
        if (!TextUtils.isEmpty(panAadharCardDetail.sex)) {
            llGender.setVisibility(View.VISIBLE);
            tvSex.setText(panAadharCardDetail.sex);
            scanData.setGender(panAadharCardDetail.sex);
        } else {
            llGender.setVisibility(View.GONE);
        }
        tvCountry.setText(getString(R.string.india));

        llAddress.setVisibility(View.GONE);

        if (card_type == 2) {
            llLastName.setVisibility(View.VISIBLE);
            Glide.with(this).load(imageFile).into(ivScanImage);
            tvDocNoTitle.setText(getString(R.string.pan_card_no));
            if (!TextUtils.isEmpty(panAadharCardDetail.pan_card_no)) {
                tvDocNo.setText(panAadharCardDetail.pan_card_no);
                scanData.setPassportNo(panAadharCardDetail.pan_card_no);
            }
            if (!TextUtils.isEmpty(panAadharCardDetail.second_name)) {
                tvLastName.setText(panAadharCardDetail.second_name);
                scanData.setLastName(panAadharCardDetail.second_name);
            }
        } else if (card_type == 3) {
            llLastName.setVisibility(View.GONE);
            Glide.with(this).load(imageFile).into(ivScanImage);
            tvDocNoTitle.setText(getString(R.string.aadhaar_card_no));
            if (!TextUtils.isEmpty(panAadharCardDetail.aadhar_card_no)) {
                tvDocNo.setText(panAadharCardDetail.aadhar_card_no);
                scanData.setPassportNo(panAadharCardDetail.aadhar_card_no);
            }
            if (!TextUtils.isEmpty(panAadharCardDetail.address) && panAadharCardDetail.card.contains("back")) {
                llAddress.setVisibility(View.VISIBLE);
                llLastName.setVisibility(View.GONE);
                llFirstName.setVisibility(View.GONE);
                llDOB.setVisibility(View.GONE);
                llDocumentNo.setVisibility(View.GONE);
                llGender.setVisibility(View.GONE);
                ivUserProfile.setVisibility(View.GONE);
                tvAddress.setText(panAadharCardDetail.address.replace("Address: ", ""));
                scanData.setAddress(panAadharCardDetail.address.replace("Address: ", ""));
            }
        }

        sendResultToServer(AppGeneral.SCAN_RESULT.ACCURA_MRZ);
    }

    //target to save
    private SimpleTarget<Bitmap> getGlideTarget() {
        SimpleTarget<Bitmap> target = new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                ivUserProfile.setImageBitmap(bitmap);
                Log.d("Result", "height" + bitmap.getHeight());
                Log.d("Result", "width" + bitmap.getWidth());
                scanData.setUserPicture(getBytes(bitmap));
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
            }
        };

        return target;
    }

    @Override
    public void onClick(View view) {
        isEmailSent = false;
        isLiveness = false;
        isFaceMatch = false;
        switch (view.getId()) {
            case R.id.tvSave:
                isLiveness = true;
                launchZoomScanScreen();
                break;
            case R.id.tvFM:
                isFaceMatch = true;
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File f = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
                Uri uriForFile = FileProvider.getUriForFile(
                        PanAadharResultActivity.this,
                        "com.accurascan.demoapp.provider",
                        f
                );
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile);
                startActivityForResult(intent, CAPTURE_IMAGE);
                break;
            case R.id.tvRetry:
                isLiveness = true;
                launchZoomScanScreen();
                break;
            case R.id.tvCancel:
                overridePendingTransition(0, 0);
                setResult(RESULT_OK);
                PanAadharResultActivity.this.finish();
                break;
            case R.id.tvOk:
                if (isValidate()) {
                    dismissEmailDialog();
                    deleteEnroll(identifier, sessionId);
                }
                break;
            case R.id.tvCancelDia:
                dismissEmailDialog();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (imageFile.exists()) {
            imageFile.delete();
        }
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initializeZoom();
    }

    private void initializeZoom() {
        // Visit https://dev.zoomlogin.com/zoomsdk/#/account to retrieve your app token
        // Replace BuildConfig.ZOOM_APP_TOKEN below with your app token
        String zoomAppToken = getString(R.string.zoom_key);
        ZoomSDK.setFacemapEncryptionKey(ZoomConnectedConfig.PublicKey);
        ZoomSDK.initialize(
                this,
                ZoomConnectedConfig.AppToken,
                mInitializeCallback
        );

        // preload sdk resources so the UI is snappy (optional)
        ZoomSDK.preload(this);

        // Signal to the ZoOm SDK that audit trail should be captured
        ZoomSDK.setAuditTrailType(ZoomAuditTrailType.HEIGHT_640);

        // Signal to ZoOm to also capture time-based session images which can be used in addition to ZoOm Audit Trail per our documentation.
        ZoomSDK.setTimeBasedSessionImagesEnabled(true);

        ZoomCustomization currentCustomization = new ZoomCustomization();
        ZoomConnectedConfig.currentCustomization = ZoomConnectedConfig.ZoomConnectedCustomization();
        ZoomSDK.setCustomization(currentCustomization);
    }

    public void launchZoomScanScreen() {
        ZoomSDKStatus status = ZoomSDK.getStatus(this);
        if (status != ZoomSDKStatus.INITIALIZED) {
            Log.w("ScanResult", "Launch Error Unable to launch ZoOm.\nReason: " + status.toString());
            return;
        }

        // only set this if settings have not been changed
        if (ZoomConnectedConfig.shouldSetIdealFrameSizeRatio) {
            ZoomConnectedConfig.setIdealFrameSizeRatio(this, getWindow().getDecorView().getWidth());
        }

        if (ZoomConnectedConfig.shouldCenterZoomFrame && !ZoomConnectedConfig.isTablet(this)) {
            ZoomConnectedConfig.centerZoomFrame(getWindow().getDecorView().getWidth(), findViewById(R.id.llMain).getHeight());
        }

        // set customization
        ZoomConnectedConfig.currentCustomization.setCancelButtonCustomization(ZoomConnectedConfig.zoomCancelButtonCustomization);
        ZoomConnectedConfig.currentCustomization.setOvalCustomization(ZoomConnectedConfig.zoomOvalCustomization);
        ZoomConnectedConfig.currentCustomization.setFrameCustomization(ZoomConnectedConfig.zoomFrameCustomization);
        ZoomConnectedConfig.currentCustomization.setFrameCustomization(ZoomConnectedConfig.zoomFrameCustomization);
        ZoomConnectedConfig.currentCustomization.showPreEnrollmentScreen = false;
        ZoomSDK.setCustomization(ZoomConnectedConfig.currentCustomization);

        // Developer Note:
        // This code hides all the app content and only show the branding logo right before launching ZoOm.
        // However, developers may choose a number of strategies instead of this behavior.
        // For instance, a developer may choose to put a fullscreen semi-transparent view over the screen
        // before launching ZoOm so their full app is visible in the background but ZoOm is exposed on top of it.
        // The options are endless and full control is given to the developer for how ZoOm looks on top of their app.

        Intent authenticationIntent = new Intent(this, ZoomVerificationActivity.class);
        startActivityForResult(authenticationIntent, ZoomSDK.REQUEST_CODE_VERIFICATION);
    }

    public void handleVerificationSuccessResult(ZoomVerificationResult successResult) {
        // retrieve the ZoOm facemap as byte[]
        if (successResult.getFaceMetrics() != null) {
            // this is the raw biometric data which can be uploaded, or may be
            // base64 encoded in order to handle easier at the cost of processing and network usage
            bytes = successResult.getFaceMetrics().getZoomFacemap();
            if (!successResult.getFaceMetrics().getAuditTrail().isEmpty()) {
                face2 = successResult.getFaceMetrics().getAuditTrail().get(0).copy(Bitmap.Config.ARGB_8888, true);
            }

            //if (RecogEngine.g_recogResult.faceBitmap != null) {
            if (imageFile != null) {
                //Bitmap nBmp = RecogEngine.g_recogResult.faceBitmap.copy(Bitmap.Config.ARGB_8888, true);
                Bitmap nBmp = decodeFileFromPath(imageFile.getAbsolutePath()).copy(Bitmap.Config.ARGB_8888, true);
                int w = nBmp.getWidth();
                int h = nBmp.getHeight();
                int s = (w * 32 + 31) / 32 * 4;
                ByteBuffer buff = ByteBuffer.allocate(s * h);
                nBmp.copyPixelsToBuffer(buff);
                FaceLockHelper.DetectLeftFace(buff.array(), w, h);
            }

            if (face2 != null) {
                Bitmap nBmp = face2;
                ivUserProfile2.setImageBitmap(nBmp);
                ivUserProfile2.setVisibility(View.VISIBLE);
                int w = nBmp.getWidth();
                int h = nBmp.getHeight();
                int s = (w * 32 + 31) / 32 * 4;
                ByteBuffer buff = ByteBuffer.allocate(s * h);
                nBmp.copyPixelsToBuffer(buff);
                FaceLockHelper.DetectRightFace(buff.array(), w, h, null);
            }

            sessionId = successResult.getSessionId();
            liveness(successResult);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // make sure the result was returned correctly
        if (resultCode == RESULT_OK) {
            if (requestCode == ZoomSDK.REQUEST_CODE_VERIFICATION) {
                ZoomVerificationResult result = data.getParcelableExtra(ZoomSDK.EXTRA_VERIFY_RESULTS);

                // CASE: you did not set a public key before attempting to retrieve a facemap.
                // Retrieving facemaps requires that you generate a public/private key pair per the instructions at https://dev.zoomlogin.com/zoomsdk/#/zoom-server-guide
                if (result.getStatus() == ZoomVerificationStatus.ENCRYPTION_KEY_INVALID) {
                    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    alertDialog.setTitle(getString(R.string.public_key_not_set));
                    alertDialog.setMessage(getString(R.string.key_not_set));
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(android.R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                } else if (result.getStatus() == ZoomVerificationStatus.USER_PROCESSED_SUCCESSFULLY) {
                    handleVerificationSuccessResult(result);
                }
            } else if (requestCode == CAPTURE_IMAGE) {
                File f = new File(Environment.getExternalStorageDirectory().toString());
                File ttt = null;
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        ttt = temp;
                        break;
                    }
                }
                if (ttt == null)
                    return;

                Bitmap bmp = rotateImage(ttt.getAbsolutePath());
                ttt.delete();
                face2 = bmp.copy(Bitmap.Config.ARGB_8888, true);
                ivUserProfile2.setImageBitmap(face2);
                ivUserProfile2.setVisibility(View.VISIBLE);

                //if (RecogEngine.g_recogResult.faceBitmap != null) {
                if (imageFile != null) {
                    //Bitmap nBmp = RecogEngine.g_recogResult.faceBitmap.copy(Bitmap.Config.ARGB_8888, true);
                    Bitmap nBmp = decodeFileFromPath(imageFile.getAbsolutePath()).copy(Bitmap.Config.ARGB_8888, true);
                    int w = nBmp.getWidth();
                    int h = nBmp.getHeight();
                    int s = (w * 32 + 31) / 32 * 4;
                    ByteBuffer buff = ByteBuffer.allocate(s * h);
                    nBmp.copyPixelsToBuffer(buff);
                    FaceLockHelper.DetectLeftFace(buff.array(), w, h);
                }

                if (face2 != null) {
                    Bitmap nBmp = face2;
                    int w = nBmp.getWidth();
                    int h = nBmp.getHeight();
                    int s = (w * 32 + 31) / 32 * 4;
                    ByteBuffer buff = ByteBuffer.allocate(s * h);
                    nBmp.copyPixelsToBuffer(buff);
                    FaceLockHelper.DetectRightFace(buff.array(), w, h, null);
                }
            }
        }

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // preload sdk resources so the UI is snappy (optional)
                ZoomSDK.preload(PanAadharResultActivity.this);
            }
        });
    }

    private Bitmap rotateImage(final String path) {

        Bitmap b = decodeFileFromPath(path);

        try {
            ExifInterface ei = new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);

                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
                    break;
                default:
                    b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
                    //b.copyPixelsFromBuffer(ByteBuffer.)
                    break;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return b;
    }

    private Bitmap decodeFileFromPath(String path) {
        Uri uri = getImageUri(path);
        InputStream in = null;
        try {
            in = getContentResolver().openInputStream(uri);

            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            BitmapFactory.decodeStream(in, null, o);
            in.close();

            int scale = 1;
            int inSampleSize = 1024;
            if (o.outHeight > inSampleSize || o.outWidth > inSampleSize) {
                scale = (int) Math.pow(2, (int) Math.round(Math.log(inSampleSize / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            in = getContentResolver().openInputStream(uri);
            // Bitmap b = BitmapFactory.decodeStream(in, null, o2);
            int MAXCAP_SIZE = 512;
            Bitmap b = getResizedBitmap(BitmapFactory.decodeStream(in, null, o2), MAXCAP_SIZE);
            in.close();

            return b;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Uri getImageUri(String path) {
        return Uri.fromFile(new File(path));
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private void setLivenessData(LivenessData livenessData) {
        tvLivenessScore.setText(livenessData.livenessScore);
        tvRetryFeedbackSuggestion.setText(livenessData.retryFeedbackSuggestion);
        scanData.setLivenessResult(livenessData.livenessResult);
        scanData.setLivenessScore(livenessData.livenessScore);
        scanData.setRetryFeedbackSuggestion(livenessData.retryFeedbackSuggestion);
        llLiveness.setVisibility(View.VISIBLE);
        tvSave.setVisibility(View.GONE);
        sendResultToServer(AppGeneral.SCAN_RESULT.ACCURA_SCAN);
    }

    private void setAuthData(AuthenticationData authenticationData) {
        tvAuth.setText(String.valueOf(authenticationData.authenticated));
        tvLivenessAuthFacemap.setText(authenticationData.livenessScoreForAuthenticationFacemap);
        tvLivenessEnrollFacemap.setText(authenticationData.livenessScoreForEnrollmentFacemap);
        tvLivenessScore.setText(authenticationData.livenessScore);
        tvLivenessAuthResultFacemap.setText(String.valueOf(authenticationData.livenessResultForAuthenticationFacemap));
        tvLivenessEnrollResultFacemap.setText(String.valueOf(authenticationData.livenessResultForEnrollmentFacemap));
        tvMatchScore.setText(authenticationData.matchScore);
        scanData.setAuth(String.valueOf(authenticationData.authenticated));
        scanData.setLivenessAuthFacemap(authenticationData.livenessScoreForAuthenticationFacemap);
        scanData.setLivenessEnrollFacemap(authenticationData.livenessScoreForEnrollmentFacemap);
        scanData.setLivenessScore(authenticationData.livenessScore);
        scanData.setLivenessAuthResultFacemap(String.valueOf(authenticationData.livenessResultForAuthenticationFacemap));
        scanData.setLivenessEnrollResultFacemap(String.valueOf(authenticationData.livenessResultForEnrollmentFacemap));
        scanData.setMatchScore(String.valueOf(authenticationData.matchScore));
        llFacemap.setVisibility(View.VISIBLE);
        tvRetry.setVisibility(View.GONE);
    }

    @SuppressLint("StaticFieldLeak")
    private void deleteEnroll(final String identifier, final String sessionId) {
        showProgressDialog();
        new RequestTask(this, ZoomConnectedConfig.AppToken, null, String.format(getString(R.string.enrollment_delete), identifier), true, true) {

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
                ParsedResponse p = HandleResponse.responseDeleteEnroll(PanAadharResultActivity.this, response);
                if (!p.error) {
                    enroll(identifier, sessionId);
                } else {
                    displayAlert((String) p.o);
                }
            }
        }.execute();
    }

    private boolean checkReadWritePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(PanAadharResultActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 111);
                return false;
            }
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 111) {
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                //resume tasks needing this permission
                callEnroll();
            } else if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                //resume tasks needing this permission
                checkReadWritePermission();
            }
        }
    }

    private void callEnroll() {
        showProgressDialog();
        bytes = scanData.getUserPicture();
        final String sessionId = Utils.randomToken();

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                File file = new File(path);
                bytes = new byte[(int) file.length()];
                try {
                    BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
                    buf.read(bytes, 0, bytes.length);
                    buf.close();
                    zoomConnectedAPI.enrollUser(sessionId, bytes, sessionId, new ZoomConnectedAPI.Callback() {
                        @Override
                        public void completion(final boolean completed, final String message, final JSONObject data) {
                            dismissProgressDialog();
                            final ParsedResponse p = HandleResponse.responseEnroll(PanAadharResultActivity.this, data.toString());
                            if (!p.error) {
                                authenticate(bytes, sessionId, Utils.randomToken());
                            } else {
                                displayAlert((String) p.o);
                            }
                        }
                    });
                } catch (FileNotFoundException e) {
                    dismissProgressDialog();
                    e.printStackTrace();
                } catch (IOException e) {
                    dismissProgressDialog();
                    e.printStackTrace();
                }

            }
        });
    }

    private void enroll(final String identifier, final String sessionId) {
        zoomConnectedAPI.enrollUser(identifier, bytes, sessionId, new ZoomConnectedAPI.Callback() {
            @Override
            public void completion(final boolean completed, final String message, final JSONObject data) {
                ParsedResponse p = HandleResponse.responseEnroll(PanAadharResultActivity.this, data.toString());
                if (p.error) {
//                    displayAlert((String) p.o);
                    displayRetryAlert("Something went wrong with Liveness Check. Please try again");

                }
            }
        });
    }

    private void displayAlert(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialogAbstract(PanAadharResultActivity.this, msg, getString(R.string.ok), "") {
                    @Override
                    public void positive_negativeButtonClick(int pos_neg_id) {
                        finish();
                    }
                };
            }
        });
    }

    private void displayRetryAlert(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialogAbstract(PanAadharResultActivity.this, msg, getString(R.string.ok), "") {
                    @Override
                    public void positive_negativeButtonClick(int pos_neg_id) {

                    }
                };
            }
        });
    }


    private void authenticate(final byte[] zoomFacemap, final String sessionId, String enrollmentIdentifier) {
        showProgressDialog();
        zoomConnectedAPI.authenticateUser(enrollmentIdentifier, zoomFacemap, sessionId, new ZoomConnectedAPI.Callback() {
            @Override
            public void completion(final boolean completed, final String message, final JSONObject data) {
                dismissProgressDialog();
                final ParsedResponse p = HandleResponse.responseAuthenticate(PanAadharResultActivity.this, data.toString());
                if (!p.error) {
                    ArrayList<AuthenticationData> authenticationData = (ArrayList<AuthenticationData>) p.o;
                    for (final AuthenticationData mAuthenticationData : authenticationData) {
                        if (mAuthenticationData.authenticated) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setAuthData(mAuthenticationData);
                                }
                            });
                            break;
                        } else {
                            tvRetry.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
//                    displayAlert((String) p.o);
                    displayRetryAlert("Something went wrong with Liveness Check. Please try again");
                }
            }
        });
    }

    private void liveness(final ZoomVerificationResult zoomVerificationResult) {
        showProgressDialog();
        byte[] zoomFacemap = zoomVerificationResult.getFaceMetrics().getZoomFacemap();
        zoomConnectedAPI.checkLiveness(zoomFacemap, zoomVerificationResult.getSessionId(), new ZoomConnectedAPI.Callback() {
            @Override
            public void completion(final boolean completed, final String message, final JSONObject data) {
                dismissProgressDialog();
                final ParsedResponse p = HandleResponse.responseLiveness(PanAadharResultActivity.this, data.toString());
                if (!p.error) {
                    final LivenessData livenessData = (LivenessData) p.o;
                    if (!livenessData.livenessResult.equalsIgnoreCase("undetermined")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setLivenessData(livenessData);
                            }
                        });
                    } else {
                        displayRetryAlert("Something went wrong with Liveness Check. Please try again");
                    }
                } else {
                    displayRetryAlert("Something went wrong with Liveness Check. Please try again");
                }
            }
        });
    }

    private void initEngine() {
        writeFileToPrivateStorage(R.raw.model, "model.prototxt");
        File modelFile = getApplicationContext().getFileStreamPath("model.prototxt");
        String pathModel = modelFile.getPath();
        writeFileToPrivateStorage(R.raw.weight, "weight.dat");
        File weightFile = getApplicationContext().getFileStreamPath("weight.dat");
        String pathWeight = weightFile.getPath();

        int nRet = FaceLockHelper.InitEngine(this, 30, 800, 1.18f, pathModel, pathWeight, this.getAssets());
        Log.i("PanAadharResultActivity", "InitEngine: " + nRet);
        if (nRet < 0) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            if (nRet == -1) {
                builder1.setMessage("No Key Found");
            } else if (nRet == -2) {
                builder1.setMessage("Invalid Key");
            } else if (nRet == -3) {
                builder1.setMessage("Invalid Platform");
            } else if (nRet == -4) {
                builder1.setMessage("Invalid License");
            }

            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();

                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
    }

    @Override
    public void onInitEngine(int ret) {
    }

    @Override
    public void onLeftDetect(FaceDetectionResult faceResult) {
        leftResult = null;
        if (faceResult != null) {
            leftResult = faceResult;

            if (face2 != null) {
                Bitmap nBmp = face2.copy(Bitmap.Config.ARGB_8888, true);
                if (nBmp != null) {
                    int w = nBmp.getWidth();
                    int h = nBmp.getHeight();
                    int s = (w * 32 + 31) / 32 * 4;
                    ByteBuffer buff = ByteBuffer.allocate(s * h);
                    nBmp.copyPixelsToBuffer(buff);
                    if (leftResult != null) {
                        FaceLockHelper.DetectRightFace(buff.array(), w, h, leftResult.getFeature());
                    } else {
                        FaceLockHelper.DetectRightFace(buff.array(), w, h, null);
                    }
                }
            }
        } else {
            if (face2 != null) {
                Bitmap nBmp = face2.copy(Bitmap.Config.ARGB_8888, true);
                if (nBmp != null) {
                    int w = nBmp.getWidth();
                    int h = nBmp.getHeight();
                    int s = (w * 32 + 31) / 32 * 4;
                    ByteBuffer buff = ByteBuffer.allocate(s * h);
                    nBmp.copyPixelsToBuffer(buff);
                    if (leftResult != null) {
                        FaceLockHelper.DetectRightFace(buff.array(), w, h, leftResult.getFeature());
                    } else {
                        FaceLockHelper.DetectRightFace(buff.array(), w, h, null);
                    }
                }
            }
        }
        calcMatch();
    }

    @Override
    public void onRightDetect(FaceDetectionResult faceResult) {
        if (faceResult != null) {
            rightResult = faceResult;
        } else {
            rightResult = null;
        }
        calcMatch();
    }

    @Override
    public void onExtractInit(int ret) {
    }

    public void calcMatch() {
        if (leftResult == null || rightResult == null) {
            match_score = 0.0f;
        } else {
            match_score = FaceLockHelper.Similarity(leftResult.getFeature(), rightResult.getFeature(), rightResult.getFeature().length);
            match_score *= 100.0f;
            tvFaceMatchScore1.setText(Float.toString(match_score));
            llFaceMatchScore.setVisibility(View.VISIBLE);
            if (!isLiveness && !isEmailSent) {
                isEmailSent = true;
                // sendResultToServer(AppGeneral.SCAN_RESULT.ACCURA_SCAN);
                sendResultToServer(AppGeneral.SCAN_RESULT.ACCURA_FM);
            }
        }
    }

    public void writeFileToPrivateStorage(int fromFile, String toFile) {
        InputStream is = getApplicationContext().getResources().openRawResource(fromFile);
        int bytes_read;
        byte[] buffer = new byte[4096];
        try {
            FileOutputStream fos = getApplicationContext().openFileOutput(toFile, Context.MODE_PRIVATE);

            while ((bytes_read = is.read(buffer)) != -1)
                fos.write(buffer, 0, bytes_read); // write

            fos.close();
            is.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendResultToServer(String subject) {
        boolean isPanCard = false;
        if (isFaceMatch) {
            tvFM.setVisibility(View.GONE);
        }
        scrollView.smoothScrollTo(0, 0);

        String mrz = "";
        if (tvDocumentType.getText().toString().contains("PAN CARD")) {
            isPanCard = true;
            mrz = "Document: " + tvDocumentType.getText() + " <br/>"
                    + "Last Name: " + tvLastName.getText() + " <br/>"
                    + "First Name: " + tvFirstName.getText() + " <br/>"
                    + "Pan Card No: " + tvDocNo.getText() + " <br/>"
                    + "Country: " + tvCountry.getText() + " <br/>"
                    + "Date of Birth: " + tvDOB.getText() + " <br/>";
        } else {
            mrz = "Document: " + tvDocumentType.getText() + " <br/>"
                    + "Last Name: " + tvLastName.getText() + " <br/>"
                    + "First Name: " + tvFirstName.getText() + " <br/>"
                    + "Aadhar Card No: " + tvDocNo.getText() + " <br/>"
                    + "Country: " + tvCountry.getText() + " <br/>"
                    + "Date of Birth: " + tvDOB.getText() + " <br/>"
                    + "Sex: " + tvSex.getText() + " <br/>"
                    + "Address: " + tvAddress.getText() + " <br/>"
                    + "Authenticated: " + tvAuth.getText() + " <br/>";
            // + "Match Score: " + tvMatchScore.getText() + " <br/>"
        }

        String title = isPanCard ? "Android - Test PAN CARD" : "Android - Test AADHAAR CARD";
        String body = "";
        String liveness = "False";
        String facematch = "False";
        String type = isPanCard ? "Pan Card" : "Aadhar Card";

        if (subject.equals(AppGeneral.SCAN_RESULT.ACCURA_FM)) {
            subject = title + " " + tvFaceMatchScore1.getText().toString() + "%";
            body = "FaceMatch Score: " + tvFaceMatchScore1.getText() + " <br/>"
                    + mrz;
            facematch = "True";
            liveness = "False";
        } else if (subject.equals(AppGeneral.SCAN_RESULT.ACCURA_MRZ)) {
            subject = title + " " + tvFirstName.getText().toString();
            body = mrz;
        } else {
            subject = title + " " + tvFirstName.getText().toString();
            body = "FaceMatch Score: " + tvFaceMatchScore1.getText() + " <br/>"
                    + "Liveness Score: " + tvLivenessScore.getText() + " <br/>"
                    + mrz;
            facematch = "True";
            liveness = "True";
        }

        Bitmap frontImage = null;
        if (imageFile.exists()) {
            String filePath = imageFile.getPath();
            frontImage = BitmapFactory.decodeFile(filePath);
        }

        SendResultToServer.getInstance().send(this,
                frontImage,
                RecogEngine.g_recogResult.docBackBitmap,
                face2,
                subject,
                body,
                type,
                liveness,
                facematch
        );
    }
}