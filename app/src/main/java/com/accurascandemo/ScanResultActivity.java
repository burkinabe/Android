package com.accurascandemo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
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
import com.accurascandemo.model.LivenessData;
import com.accurascandemo.model.ScanData;
import com.accurascandemo.util.AlertDialogAbstract;
import com.accurascandemo.util.AppGeneral;
import com.accurascandemo.util.ParsedResponse;

import com.docrecog.scan.CameraActivity;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * This class used to display data after scanning Passport & ID MRZ document
 */

public class ScanResultActivity extends BaseActivity implements View.OnClickListener, FaceCallback {

    FaceDetectionResult leftResult = null;
    FaceDetectionResult rightResult = null;
    float match_score = 0.0f;
    Bitmap face2 = null;

    final private int CAPTURE_IMAGE = 2;

    private static final int RETURN_RESULT = 1;
    private final ZoomSDK.InitializeCallback mInitializeCallback = new ZoomSDK.InitializeCallback() {
        @Override
        public void onCompletion(boolean successful) {
            if (successful) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.w("ScanResult", "Initialization success");
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.w("ScanResult", "Initialization failed.");
                    }
                });
            }
        }
    };
    private ScrollView scrollView;
    private ImageView ivBack, ivUserProfile, ivScanImage, ivScanBackImage;
    private ImageView ivUserProfile2;
    private TextView tvRetry, tvLastName, tvFirstName, tvPassportNo, tvCountry, tvNationality,
            tvSex, tvDOB, tvDateOfExpiry, tvDocumentType, tvMrz, tvOtherId, tvRet, tvDocumentNoCheck, tvDOBCheck, tvDateOfExpiryCheck, tvOtherIdCheck, tvSecondRowCheckNumber, tvSave, tvFM,
            tvAuth, tvLivenessAuthFacemap, tvLivenessEnrollFacemap, tvLivenessAuthResultFacemap,
            tvLivenessEnrollResultFacemap, tvMatchScore, tvFaceMatchScore1, tvLivenessScore1, tvRetryFeedbackSuggestion;
    private TextView tvTxtFront, tvTxtBack;
    private LinearLayout llFacemap, llLiveness, llFaceMatchScore1;
    private ScanData scanData;
    private byte[] bytes;
    private String sessionId;
    private ZoomConnectedAPI zoomConnectedAPI;
    private boolean isLiveness = false;
    private boolean isEmailSent = false;
    private boolean isFaceMatch = false;

    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);

        initEngine(); //initialize the engine
        //call zoom connect API
        zoomConnectedAPI = new ZoomConnectedAPI(ZoomConnectedConfig.AppToken, getApplicationContext().getPackageName(), this);
        initUi();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initializeZoom();
    }

    private void initializeZoom() {
        // Visit https://dev.zoomlogin.com/zoomsdk/#/account to retrieve your app token
        // Replace BuildConfig.ZOOM_APP_TOKEN below with your app token
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
        ZoomConnectedConfig.currentCustomization.setFeedbackCustomization(ZoomConnectedConfig.zoomFeedbackCustomization);
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
            ///////////////////////////////////////////////////////////
            bytes = successResult.getFaceMetrics().getZoomFacemap();
            if (!successResult.getFaceMetrics().getAuditTrail().isEmpty()) {
                face2 = successResult.getFaceMetrics().getAuditTrail().get(0).copy(Bitmap.Config.ARGB_8888, true);
            }

            if (RecogEngine.g_recogResult.faceBitmap != null) {
                Bitmap nBmp = RecogEngine.g_recogResult.faceBitmap.copy(Bitmap.Config.ARGB_8888, true);
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
            liveness(successResult);  //check liveness
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
                }
                // CASE: user performed a ZoOm and passed liveness check
                else if (result.getStatus() == ZoomVerificationStatus.USER_PROCESSED_SUCCESSFULLY) {
                    handleVerificationSuccessResult(result);
                }
            } else if (requestCode == RETURN_RESULT) {
                setUserPassportProfile();
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

                if (RecogEngine.g_recogResult.faceBitmap != null) {
                    Bitmap nBmp = RecogEngine.g_recogResult.faceBitmap.copy(Bitmap.Config.ARGB_8888, true);
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
                ZoomSDK.preload(ScanResultActivity.this);
            }
        });
    }

    private void initUi() {

        scrollView = findViewById(R.id.scrollView);
        ivScanBackImage = findViewById(R.id.ivScanBackImage);
        tvTxtFront = findViewById(R.id.tvTxtFront);
        tvTxtBack = findViewById(R.id.tvTxtBack);

        llFacemap = findViewById(R.id.llFacemap);
        llLiveness = findViewById(R.id.llLiveness);
        llFaceMatchScore1 = findViewById(R.id.llFaceMatchScore1);
        ivUserProfile = findViewById(R.id.ivUserProfile);
        ivUserProfile2 = findViewById(R.id.ivUserProfile2);
        ivScanImage = findViewById(R.id.ivScanImage);
        tvRetry = findViewById(R.id.tvRetry);
        tvLivenessScore1 = findViewById(R.id.tvLivenessScore1);
        tvRetryFeedbackSuggestion = findViewById(R.id.tvRetryFeedbackSuggestion);
        tvAuth = findViewById(R.id.tvAuth);
        tvLivenessAuthFacemap = findViewById(R.id.tvLivenessAuthFacemap);
        tvLivenessEnrollFacemap = findViewById(R.id.tvLivenessEnrollFacemap);
        tvLivenessAuthResultFacemap = findViewById(R.id.tvLivenessAuthResultFacemap);
        tvLivenessEnrollResultFacemap = findViewById(R.id.tvLivenessEnrollResultFacemap);
        tvMatchScore = findViewById(R.id.tvMatchScore);
        tvFaceMatchScore1 = findViewById(R.id.tvFaceMatchScore1);
        tvRet = findViewById(R.id.tvRet);
        tvDocumentType = findViewById(R.id.tvDocumentType);
        tvMrz = findViewById(R.id.tvMrz);
        tvLastName = findViewById(R.id.tvLastName);
        tvFirstName = findViewById(R.id.tvFirstName);
        tvPassportNo = findViewById(R.id.tvPassportNo);
        tvCountry = findViewById(R.id.tvCountry);
        tvNationality = findViewById(R.id.tvNationality);
        tvSex = findViewById(R.id.tvSex);
        tvDOB = findViewById(R.id.tvDOB);
        tvDateOfExpiry = findViewById(R.id.tvDateOfExpiry);
        tvDocumentNoCheck = findViewById(R.id.tvDocumentNoCheck);
        tvDOBCheck = findViewById(R.id.tvDOBCheck);
        tvOtherId = findViewById(R.id.tvOtherId);
        tvDateOfExpiryCheck = findViewById(R.id.tvDateOfExpiryCheck);
        tvOtherIdCheck = findViewById(R.id.tvOtherIdCheck);
        tvSecondRowCheckNumber = findViewById(R.id.tvSecondRowCheckNumber);

        tvSave = findViewById(R.id.tvSave);
        tvSave.setOnClickListener(this);
        findViewById(R.id.tvCancel).setOnClickListener(this);

        tvFM = findViewById(R.id.tvFM);
        tvFM.setOnClickListener(this);

        if (AccuraDemoApplication.mMenuMode == AccuraDemoApplication.MENU_MODE_OCR) {
            tvSave.setVisibility(View.GONE);
            tvFM.setVisibility(View.GONE);
            llFaceMatchScore1.setVisibility(View.GONE);
        }

        setUserPassportProfile();
    }

    //Set scanned passport data to view
    private void setUserPassportProfile() {

        Log.d("Result", RecogEngine.g_recogResult.GetResultString());
        scanData = new ScanData();

        if (RecogEngine.g_recogResult.docFrontBitmap != null) {
            ivScanImage.setImageBitmap(RecogEngine.g_recogResult.docFrontBitmap);
        } else {
            tvTxtFront.setVisibility(View.GONE);
            ivScanImage.setVisibility(View.GONE);
        }

        if (RecogEngine.g_recogResult.docBackBitmap != null) {
            ivScanBackImage.setImageBitmap(RecogEngine.g_recogResult.docBackBitmap);
        } else {
            tvTxtBack.setVisibility(View.GONE);
            ivScanBackImage.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(RecogEngine.g_recogResult.surname)) {
            tvLastName.setText(RecogEngine.g_recogResult.surname);
            scanData.setLastName(RecogEngine.g_recogResult.surname);
        } else {
            tvLastName.setText("");
        }

        if (!TextUtils.isEmpty(RecogEngine.g_recogResult.givenname)) {
            tvFirstName.setText(RecogEngine.g_recogResult.givenname);
            scanData.setFirstName(RecogEngine.g_recogResult.givenname);
        } else {
            tvFirstName.setText("");
        }

        if (!TextUtils.isEmpty(RecogEngine.g_recogResult.docnumber)) {
            tvPassportNo.setText(RecogEngine.g_recogResult.docnumber);
            scanData.setPassportNo(RecogEngine.g_recogResult.docnumber);
        } else {
            tvPassportNo.setText("");
        }

        if (!TextUtils.isEmpty(RecogEngine.g_recogResult.country)) {
            tvCountry.setText(RecogEngine.g_recogResult.country);
            scanData.setCountry(RecogEngine.g_recogResult.country);
        } else {
            tvCountry.setText("");
        }

        if (!TextUtils.isEmpty(RecogEngine.g_recogResult.nationality)) {
            tvNationality.setText(RecogEngine.g_recogResult.nationality);
        } else {
            tvNationality.setText("");
        }

        if (!TextUtils.isEmpty(RecogEngine.g_recogResult.sex)) {
            if (RecogEngine.g_recogResult.sex.equalsIgnoreCase("F")) {
                tvSex.setText(getString(R.string.text_female));
                scanData.setGender(getString(R.string.text_female));
            } else {
                tvSex.setText(getString(R.string.text_male));
                scanData.setGender(getString(R.string.text_male));
            }
        } else {
            tvSex.setText("");
        }

        scanData.setMrz(RecogEngine.g_recogResult.ret);
        tvMrz.setText(RecogEngine.g_recogResult.lines);

        if (RecogEngine.g_recogResult.ret == 0) {
            tvRet.setText(getString(R.string.failed));
            scanData.setStatus(getString(R.string.failed));
        } else if (RecogEngine.g_recogResult.ret == 1) {
            tvRet.setText(getString(R.string.correct_mrz));
            scanData.setStatus(getString(R.string.correct_mrz));
        } else if (RecogEngine.g_recogResult.ret == 0) {
            tvRet.setText(getString(R.string.incorrect_mrz));
            scanData.setStatus(getString(R.string.incorrect_mrz));
        }

        if (!TextUtils.isEmpty(RecogEngine.g_recogResult.docchecksum)) {
            tvDocumentNoCheck.setText(RecogEngine.g_recogResult.docchecksum);
        }

        if (!TextUtils.isEmpty(RecogEngine.g_recogResult.birthchecksum)) {
            tvDOBCheck.setText(RecogEngine.g_recogResult.birthchecksum);
        }

        if (!TextUtils.isEmpty(RecogEngine.g_recogResult.expirationchecksum)) {
            tvDateOfExpiryCheck.setText(RecogEngine.g_recogResult.expirationchecksum);
        }

        if (!TextUtils.isEmpty(RecogEngine.g_recogResult.otheridchecksum)) {
            tvOtherIdCheck.setText(RecogEngine.g_recogResult.otheridchecksum);
        }

        if (!TextUtils.isEmpty(RecogEngine.g_recogResult.secondrowchecksum)) {
            tvSecondRowCheckNumber.setText(RecogEngine.g_recogResult.secondrowchecksum);
        }

        if (!TextUtils.isEmpty(RecogEngine.g_recogResult.docType)) {
            if (RecogEngine.g_recogResult.docType.substring(0, 1).equalsIgnoreCase("P")) {
                scanData.setDocumentType("PASSPORT");
            } else if (RecogEngine.g_recogResult.docType.substring(0, 1).equalsIgnoreCase("V")) {
                scanData.setDocumentType("VISA");
            } else if (RecogEngine.g_recogResult.docType.substring(0, 1).equalsIgnoreCase("I")) {
                scanData.setDocumentType("ID");
            } else if (RecogEngine.g_recogResult.docType.substring(0, 1).equalsIgnoreCase("D")) {
                scanData.setDocumentType("DL");
            } else {
                scanData.setDocumentType("ID");
            }
        } else if (!TextUtils.isEmpty(RecogEngine.g_recogResult.lines) && RecogEngine.g_recogResult.lines.length() > 0) {
            if (RecogEngine.g_recogResult.lines.substring(0, 1).equalsIgnoreCase("D")) {
                scanData.setDocumentType("DL");
            } else {
                scanData.setDocumentType("ID");
            }
        }

        tvDocumentType.setText(scanData.getDocumentType());

        tvOtherId.setText(RecogEngine.g_recogResult.otherid);

        if (RecogEngine.facepick == 1) {
            if (RecogEngine.g_recogResult.faceBitmap != null) {
                Bitmap bitmap = RecogEngine.g_recogResult.faceBitmap;
                Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

                BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                Paint paint = new Paint();
                paint.setShader(shader);
                paint.setAntiAlias(true);
                Canvas c = new Canvas(circleBitmap);
                c.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);
                if (RecogEngine.g_recogResult.faceBitmap != null) {
                    scanData.setUserPicture(getBytes(bitmap));
                    ivUserProfile.setImageBitmap(bitmap);
                } else {
                    ivUserProfile.setVisibility(View.GONE);
                }
            }
        }

        DateFormat date = new SimpleDateFormat("yymmdd", Locale.getDefault());
        SimpleDateFormat newDateFormat = new SimpleDateFormat("dd-mm-yy", Locale.getDefault());

        if (!TextUtils.isEmpty(RecogEngine.g_recogResult.expirationdate)) {
            try {
                Date expiryDate = date.parse(RecogEngine.g_recogResult.expirationdate);
                tvDateOfExpiry.setText(newDateFormat.format(expiryDate));
                scanData.setDateOfExpiry(newDateFormat.format(expiryDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            tvDateOfExpiry.setText("");
        }

        if (!TextUtils.isEmpty(RecogEngine.g_recogResult.birth)) {
            try {
                Date birthDate = date.parse(RecogEngine.g_recogResult.birth.replace("<", ""));
                tvDOB.setText(newDateFormat.format(birthDate));
                scanData.setDateOfBirth(newDateFormat.format(birthDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            tvDOB.setText("");
        }

    }

    @Override
    public void onBackPressed() {
        startActivityForResult(new Intent(ScanResultActivity.this, CameraActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION), RETURN_RESULT);
        overridePendingTransition(0, 0);
        ScanResultActivity.this.finish();
    }

    // handle click of particular view
    @Override
    public void onClick(View view) {
        face2 = null;
        isEmailSent = false;
        isLiveness = false;
        isFaceMatch = false;
        switch (view.getId()) {

            case R.id.ivBack:
                finish();
                break;

            case R.id.tvSave:
                //start liveness
                isLiveness = true;
                launchZoomScanScreen();
                break;
            case R.id.tvFM:
                //start FaceMatch
                isFaceMatch = true;
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File f = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
                Uri uriForFile = FileProvider.getUriForFile(
                        ScanResultActivity.this,
                        "com.accurascan.demoapp.provider",
                        f
                );
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile);
                startActivityForResult(intent, CAPTURE_IMAGE);
                break;
            case R.id.tvRetry://start liveness
                isLiveness = true;
                launchZoomScanScreen();
                break;
            case R.id.tvCancel:
                startActivityForResult(new Intent(ScanResultActivity.this, CameraActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION), RETURN_RESULT);
                overridePendingTransition(0, 0);
                finish();
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

            default:
                break;
        }
    }

    private void displayRetryAlert(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialogAbstract(ScanResultActivity.this, msg, getString(R.string.ok), "") {
                    @Override
                    public void positive_negativeButtonClick(int pos_neg_id) {

                    }
                };
            }
        });
    }

    private void enroll(final String identifier, final String sessionId) {
        zoomConnectedAPI.enrollUser(identifier, bytes, sessionId, new ZoomConnectedAPI.Callback() {
            @Override
            public void completion(final boolean completed, final String message, final JSONObject data) {
                if (dbHelper != null) {
                    dbHelper.saveScanData(scanData);
                }
                ParsedResponse p = HandleResponse.responseEnroll(ScanResultActivity.this, data.toString());
                if (p.error) {
                    displayRetryAlert("Something went wrong with Liveness Check. Please try again");

                }
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private void deleteEnroll(final String identifier, final String sessionId) {
        showProgressDialog();
        new RequestTask(this, getString(R.string.zoom_key), null, String.format(getString(R.string.enrollment_delete), identifier), true, true) {

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
                ParsedResponse p = HandleResponse.responseDeleteEnroll(ScanResultActivity.this, response);
                if (!p.error) {
                    enroll(identifier, sessionId);
                } else {
                    displayRetryAlert("Something went wrong with Liveness Check. Please try again");
                }
            }
        }.execute();
    }

    // method for set livenessdata
    //parameter to pass : livenessdata
    private void setLivenessData(LivenessData livenessData) {
        tvLivenessScore1.setText(livenessData.livenessScore);
        tvRetryFeedbackSuggestion.setText(livenessData.retryFeedbackSuggestion);
        llLiveness.setVisibility(View.VISIBLE);
        tvSave.setVisibility(View.GONE);


    }

    private void liveness(final ZoomVerificationResult zoomVerificationResult) {
        showProgressDialog();
        byte[] zoomFacemap = zoomVerificationResult.getFaceMetrics().getZoomFacemap();
        zoomConnectedAPI.checkLiveness(zoomFacemap, zoomVerificationResult.getSessionId(), new ZoomConnectedAPI.Callback() {
            @Override
            public void completion(final boolean completed, final String message, final JSONObject data) {
                dismissProgressDialog();
                final ParsedResponse p = HandleResponse.responseLiveness(ScanResultActivity.this, data.toString());
                if (!p.error) {
                    final LivenessData livenessData = (LivenessData) p.o;
                    if (!livenessData.livenessResult.equalsIgnoreCase("undetermined")) {
                        //liveness complete successfully.
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setLivenessData(livenessData); //set liveness data to view
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

    //////////////////////////////////////
    //facematch
    /////////////////////////////////////

    //init Face Match Engine
    private void initEngine() {

        //call Sdk  method InitEngine
        // parameter to pass : FaceCallback callback, int fmin, int fmax, float resizeRate, String modelpath, String weightpath, AssetManager assets
        // this method will return the integer value
        //  the return value by initEngine used the identify the particular error
        // -1 - No key found
        // -2 - Invalid Key
        // -3 - Invalid Platform
        // -4 - Invalid License

        writeFileToPrivateStorage(R.raw.model, "model.prototxt");  // write file to private storage
        File modelFile = getApplicationContext().getFileStreamPath("model.prototxt");
        String pathModel = modelFile.getPath();
        writeFileToPrivateStorage(R.raw.weight, "weight.dat");
        File weightFile = getApplicationContext().getFileStreamPath("weight.dat");
        String pathWeight = weightFile.getPath();

        int nRet = FaceLockHelper.InitEngine(this, 30, 800, 1.18f, pathModel, pathWeight, this.getAssets());
        Log.i("facematch", "InitEngine: " + nRet);
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

    //Calculate Face Match Score
    public void calcMatch() {
        if (leftResult == null || rightResult == null) {
            match_score = 0.0f;
        } else {
            match_score = FaceLockHelper.Similarity(leftResult.getFeature(), rightResult.getFeature(), rightResult.getFeature().length);
            if (match_score > 0) {
                llFaceMatchScore1.setVisibility(View.VISIBLE);
                match_score *= 100.0f;
                tvFaceMatchScore1.setText(Float.toString(match_score));

            }
        }
    }

    //used for writing file to private storage
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

    //Rotate Image as per current orientation
    //parameter to pass : string path
    //return bitmap
    private Bitmap rotateImage(final String path) {
        Bitmap b = decodeFileFromPath(path);

        try {
            ExifInterface ei = new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90: //rotate 90 to right it
                    matrix.postRotate(90);
                    b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);

                    break;
                case ExifInterface.ORIENTATION_ROTATE_180: //rotate 180 to right it
                    matrix.postRotate(180);
                    b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270: //rotate 270 to right it
                    matrix.postRotate(270);
                    b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
                    break;
                default:
                    b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
                    break;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return b;
    }

    //Get bitmap image form path
    //parameter to pass : String path
    //return bitmap
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
            e.printStackTrace(); // handle file not found exception
        } catch (IOException e) {
            e.printStackTrace(); // handle IO exception
        }
        return null;
    }

    //get Uri from given string path
    //parameter to pass : String path
    // return uri
    private Uri getImageUri(String path) {
        return Uri.fromFile(new File(path));
    }

    //used for getting resized image bitmap
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


}