package com.accurascandemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accurascandemo.model.AuthenticationData;
import com.accurascandemo.model.LastSavedData;
import com.accurascandemo.model.LivenessData;
import com.accurascandemo.util.AppGeneral;
import com.docrecog.scan.CameraActivity;
import com.facetec.zoom.sdk.ZoomSDK;

import java.io.ByteArrayOutputStream;


/**
 * This class used to display data after scanning Passport & ID MRZ document
 */

public class DisplayAllDataActivity extends BaseActivity implements View.OnClickListener {

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
    private ImageView ivUserProfile;
    private TextView tvEmail, tvLastName, tvFirstName, tvPassportNo, tvCountry, tvNationality,
            tvSex, tvDOB, tvDateOfExpiry, tvDocumentType, tvMrz, tvOtherId, tvDocumentNoCheck, tvDOBCheck, tvDateOfExpiryCheck, tvOtherIdCheck, tvSecondRowCheckNumber,
            tvAddress, tvDocNoTitle, tvRet, tvDocNo,
            tvAuth, tvGlassesDecision, tvGlassesScore, tvLivenessAuthFacemap, tvLivenessEnrollFacemap, tvLivenessScore, tvLivenessAuthResultFacemap,
            tvLivenessEnrollResultFacemap, tvMatchScore, tvLivenessResult, tvRetryFeedbackSuggestion;
    private LinearLayout llGender, llLastName, llFirstName, llAddress, llDOB, llDocumentNo;
    private LivenessData livenessData;
    private AuthenticationData authenticationData;
    private LastSavedData lastSavedData;

    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    public static void startActivity(Activity activity, LivenessData livenessData, AuthenticationData authenticationData, LastSavedData lastSavedData) {
        Intent intent = new Intent(activity, DisplayAllDataActivity.class);
        intent.putExtra(AppGeneral.LivenessData, livenessData);
        intent.putExtra(AppGeneral.AuthenticationData, authenticationData);
        intent.putExtra(AppGeneral.LastSavedData, lastSavedData);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_all_data);
        initUi();
    }

    private void initUi() {
        //initialize the UI
        findViewById(R.id.ivBack).setOnClickListener(this);
        livenessData = getIntent().getParcelableExtra(AppGeneral.LivenessData);
        authenticationData = getIntent().getParcelableExtra(AppGeneral.AuthenticationData);
        lastSavedData = getIntent().getParcelableExtra(AppGeneral.LastSavedData);

        tvRet = findViewById(R.id.tvRet);
        tvEmail = findViewById(R.id.tvEmail);
        tvDocumentType = findViewById(R.id.tvDocumentType);
        tvMrz = findViewById(R.id.tvMrz);
        tvPassportNo = findViewById(R.id.tvPassportNo);
        tvCountry = findViewById(R.id.tvCountry);
        tvNationality = findViewById(R.id.tvNationality);
        tvSex = findViewById(R.id.tvSex);
        tvDOB = findViewById(R.id.tvDOB);
        tvDocNo = findViewById(R.id.tvDocNo);
        tvDateOfExpiry = findViewById(R.id.tvDateOfExpiry);
        tvDocumentNoCheck = findViewById(R.id.tvDocumentNoCheck);
        tvDOBCheck = findViewById(R.id.tvDOBCheck);
        tvOtherId = findViewById(R.id.tvOtherId);
        tvDateOfExpiryCheck = findViewById(R.id.tvDateOfExpiryCheck);
        tvOtherIdCheck = findViewById(R.id.tvOtherIdCheck);
        tvSecondRowCheckNumber = findViewById(R.id.tvSecondRowCheckNumber);
        tvLastName = findViewById(R.id.tvLastName);
        tvFirstName = findViewById(R.id.tvFirstName);
        llGender = findViewById(R.id.llGender);
        llLastName = findViewById(R.id.llLastName);
        llFirstName = findViewById(R.id.llFirstName);
        llAddress = findViewById(R.id.llAddressLine1);
        tvAddress = findViewById(R.id.tvAddressLine1);
        llDOB = findViewById(R.id.llDOB);
        llDocumentNo = findViewById(R.id.llDocumentNo);
        tvDocNoTitle = findViewById(R.id.tvDocNoTitle);
        tvLivenessResult = findViewById(R.id.tvLivenessResult);
        tvRetryFeedbackSuggestion = findViewById(R.id.tvRetryFeedbackSuggestion);
        tvAuth = findViewById(R.id.tvAuth);
        tvGlassesDecision = findViewById(R.id.tvGlassesDecision);
        tvGlassesScore = findViewById(R.id.tvGlassesScore);
        tvLivenessAuthFacemap = findViewById(R.id.tvLivenessAuthFacemap);
        tvLivenessEnrollFacemap = findViewById(R.id.tvLivenessEnrollFacemap);
        tvLivenessScore = findViewById(R.id.tvLivenessScore);
        tvLivenessAuthResultFacemap = findViewById(R.id.tvLivenessAuthResultFacemap);
        tvLivenessEnrollResultFacemap = findViewById(R.id.tvLivenessEnrollResultFacemap);
        tvMatchScore = findViewById(R.id.tvMatchScore);
        findViewById(R.id.tvOk).setOnClickListener(this);

        setData();

    }

    @Override
    public void onBackPressed() {
        startActivityForResult(new Intent(DisplayAllDataActivity.this, CameraActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION), RETURN_RESULT);
        overridePendingTransition(0, 0);
        DisplayAllDataActivity.this.finish();
    }

    private void setData() {
        //set data in view
        tvLivenessResult.setText(livenessData.livenessResult);
        tvRetryFeedbackSuggestion.setText(livenessData.retryFeedbackSuggestion);
        tvAuth.setText(String.valueOf(authenticationData.authenticated));
        tvGlassesDecision.setText(String.valueOf(authenticationData.glassesDecision));
        tvGlassesScore.setText(authenticationData.glassesScore);
        tvLivenessAuthFacemap.setText(authenticationData.livenessScoreForAuthenticationFacemap);
        tvLivenessEnrollFacemap.setText(authenticationData.livenessScoreForEnrollmentFacemap);
        tvLivenessScore.setText(authenticationData.livenessScore);
        tvLivenessAuthResultFacemap.setText(String.valueOf(authenticationData.livenessResultForAuthenticationFacemap));
        tvLivenessEnrollResultFacemap.setText(String.valueOf(authenticationData.livenessResultForEnrollmentFacemap));
        tvMatchScore.setText(authenticationData.matchScore);

        tvEmail.setText(lastSavedData.email);
        tvDocumentType.setText(lastSavedData.document_type);
        tvMrz.setText(lastSavedData.mrz);
        tvPassportNo.setText(lastSavedData.passport_no);
        tvCountry.setText(lastSavedData.country);
        tvNationality.setText(lastSavedData.nationality);
        tvSex.setText(lastSavedData.sex);
        tvDOB.setText(lastSavedData.date_of_birth);
        tvDateOfExpiry.setText(lastSavedData.date_of_expiry);
        tvDateOfExpiryCheck.setText(lastSavedData.expiration_check_number);
        tvDocumentNoCheck.setText(lastSavedData.document_check_number);
        tvDOBCheck.setText(lastSavedData.birth_check_number);
        tvOtherId.setText(lastSavedData.other_id);
        tvOtherIdCheck.setText(lastSavedData.other_id_check);
        tvSecondRowCheckNumber.setText(lastSavedData.second_row_check_number);
        tvLastName.setText(lastSavedData.surname);
        tvFirstName.setText(lastSavedData.name);
        tvAddress.setText(lastSavedData.address);
        tvRet.setText(lastSavedData.status);
        if (!TextUtils.isEmpty(lastSavedData.pan_card_no)) {
            tvDocNoTitle.setText(getString(R.string.pan_card_no));
            tvDocNo.setText(lastSavedData.pan_card_no);
        } else {
            tvDocNoTitle.setText(getString(R.string.aadhaar_card_no));
            tvDocNo.setText(lastSavedData.aadhar_card_no);
        }
    }

    //handle click of view
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;

            case R.id.tvOk:
                MainActivity.startActivity(this);
                break;
        }
    }
}
