package com.accurascandemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.accurascandemo.api.GenerateRequest;
import com.accurascandemo.api.HandleResponse;
import com.accurascandemo.api.RequestTask;
import com.accurascandemo.api.ZoomConnectedAPI;
import com.accurascandemo.R;
import com.accurascandemo.model.AuthenticationData;
import com.accurascandemo.model.LastSavedData;
import com.accurascandemo.model.LivenessData;
import com.accurascandemo.util.AlertDialogAbstract;
import com.accurascandemo.util.ParsedResponse;
import com.facetec.zoom.sdk.ZoomCustomization;
import com.facetec.zoom.sdk.ZoomSDK;
import com.facetec.zoom.sdk.ZoomSDKStatus;
import com.facetec.zoom.sdk.ZoomVerificationActivity;
import com.facetec.zoom.sdk.ZoomVerificationResult;
import com.facetec.zoom.sdk.ZoomVerificationStatus;

import org.json.JSONObject;

import java.util.regex.Pattern;

public class LoginEmailActivity extends BaseActivity implements View.OnClickListener {

    private EditText etEmail;
    private byte[] bytes;
    private AuthenticationData authenticationData;
    private LivenessData livenessData;
    private LastSavedData lastSavedData;
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

    private ZoomConnectedAPI zoomConnectedAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_email);

        //calling the zoom connect API
        zoomConnectedAPI = new ZoomConnectedAPI(ZoomConnectedConfig.AppToken, getApplicationContext().getPackageName(), this);

        etEmail = findViewById(R.id.etEmail);

        ((TextView) findViewById(R.id.tvToolbarTitle)).setText(getString(R.string.loginViaFace));

        findViewById(R.id.ivBack).setOnClickListener(this);
        findViewById(R.id.btnSubmit).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initializeZoom();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AccuraDemoApplication.getInstance().reportToGoogleAnalytics(getString(R.string.about_screen));
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    //hanlde click of view
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;

            case R.id.btnSubmit:
                if (isValidate()) {
                    isUserEnroll(etEmail.getText().toString());
                }
                break;
        }
    }

    protected boolean isValidate() {
        String msg = "";
        if (TextUtils.isEmpty(etEmail.getText().toString())) {
            msg = getString(R.string.empty_email);
        } else if (!Pattern.matches(Patterns.EMAIL_ADDRESS.pattern(), etEmail.getText().toString())) {
            msg = getString(R.string.valid_email);
        }

        if (!TextUtils.isEmpty(msg)) {
            new AlertDialogAbstract(this, msg, getString(R.string.ok), "") {
                @Override
                public void positive_negativeButtonClick(int pos_neg_id) {

                }
            };

            return false;
        }
        identifier = etEmail.getText().toString();
        return true;
    }

    private void isUserEnroll(final String identifier) {
        showProgressDialog();
        new RequestTask(this, getString(R.string.zoom_key), null, String.format(getString(R.string.enrollment_delete), identifier), true) {

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
                dismissProgressDialog();
                ParsedResponse p = HandleResponse.responseDeleteEnroll(LoginEmailActivity.this, response);
                if (!p.error) {
                    launchZoomScanScreen();
                } else {
                    dismissProgressDialog();
                    new AlertDialogAbstract(LoginEmailActivity.this, (String) p.o, getString(R.string.ok), "") {
                        @Override
                        public void positive_negativeButtonClick(int pos_neg_id) {
                        }
                    };
                }
            }
        }.execute();
    }

    private void initializeZoom() {
        // Visit https://dev.zoomlogin.com/zoomsdk/#/account to retrieve your app token
        // Replace BuildConfig.ZOOM_APP_TOKEN below with your app token
        String zoomAppToken = getString(R.string.zoom_key);
        ZoomSDK.setFacemapEncryptionKey(getString(R.string.encrypted_key));
        ZoomSDK.initialize(
                this,
                zoomAppToken,
                mInitializeCallback
        );

        // preload sdk resources so the UI is snappy (optional)
        ZoomSDK.preload(this);

        ZoomCustomization currentCustomization = new ZoomCustomization();
        ZoomSDK.setCustomization(currentCustomization);
    }

    public void launchZoomScanScreen() {
        ZoomSDKStatus status = ZoomSDK.getStatus(this);
        if (status != ZoomSDKStatus.INITIALIZED) {
            Log.w("ScanResult", "Launch Error Unable to launch ZoOm.\nReason: " + status.toString());
            return;
        }

        Intent authenticationIntent = new Intent(this, ZoomVerificationActivity.class);
        startActivityForResult(authenticationIntent, ZoomSDK.REQUEST_CODE_VERIFICATION);
    }

    public void handleVerificationSuccessResult(ZoomVerificationResult successResult) {
        // retrieve the ZoOm facemap as byte[]
        if (successResult.getFaceMetrics() != null) {
            // this is the raw biometric data which can be uploaded, or may be
            // base64 encoded in order to handle easier at the cost of processing and network usage
            bytes = successResult.getFaceMetrics().getZoomFacemap();
            authentication(successResult);
//            authenticate(bytes, successResult.getSessionId(), new SessionManager(this).getSDKToken());
            // handle facemap
        }
    }

    private void authenticate(final byte[] zoomFacemap, final String sessionId, String enrollmentIdentifier) {
        showProgressDialog();
        new RequestTask(this,
                getString(R.string.zoom_key),
                GenerateRequest.requestAuthenticate(zoomFacemap, sessionId, enrollmentIdentifier),
                getString(R.string.authenticate),
                true) {

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
                ParsedResponse p = HandleResponse.responseAuthenticate(LoginEmailActivity.this, response);
                if (!p.error) {
                    authenticationData = (AuthenticationData) p.o;
                    liveness(zoomFacemap, sessionId);
                } else {
                    dismissProgressDialog();
                    new AlertDialogAbstract(LoginEmailActivity.this, (String) p.o, getString(R.string.ok), "") {
                        @Override
                        public void positive_negativeButtonClick(int pos_neg_id) {
                        }
                    };
                }
            }
        }.execute();
    }

    private void liveness(byte[] zoomFacemap, String sessionId) {
        new RequestTask(this,
                getString(R.string.zoom_key),
                GenerateRequest.requestFacemap(sessionId, zoomFacemap),
                getString(R.string.liveness_api),
                true) {

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
                dismissProgressDialog();
                ParsedResponse p = HandleResponse.responseLiveness(LoginEmailActivity.this, response);
                if (!p.error) {
                    livenessData = (LivenessData) p.o;
                    if (!livenessData.livenessResult.equalsIgnoreCase("undetermined")) {
                        getLastData();
                    }
                } else {
                    showProgressDialog();
                    new AlertDialogAbstract(LoginEmailActivity.this, (String) p.o, getString(R.string.ok), "") {
                        @Override
                        public void positive_negativeButtonClick(int pos_neg_id) {
                        }
                    };
                }
            }
        }.execute();
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
                    alertDialog.setTitle("Public Key Not Set");
                    alertDialog.setMessage("Retrieving facemaps requires that you generate a public/private key pair per the instructions at https://dev.zoomlogin.com/zoomsdk/#/zoom-server-guide");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
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
                } else {
                    // handle other error
                }
            }
        }

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // preload sdk resources so the UI is snappy (optional)
                ZoomSDK.preload(LoginEmailActivity.this);
            }
        });
    }

    private void getLastData() {

        new RequestTask(this, getString(R.string.zoom_key), null, String.format(getString(R.string.enrollment_delete), identifier), false) {

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
                ParsedResponse p = HandleResponse.responseGetLastSaveData(LoginEmailActivity.this, response);
                if (!p.error) {
                    showProgressDialog();
                    lastSavedData = (LastSavedData) p.o;
                } else {
                    dismissProgressDialog();
                    new AlertDialogAbstract(LoginEmailActivity.this, (String) p.o, getString(R.string.ok), "") {
                        @Override
                        public void positive_negativeButtonClick(int pos_neg_id) {
                        }
                    };
                }
            }
        }.execute();
    }

    private void authentication(final ZoomVerificationResult zoomVerificationResult) {
        byte[] zoomFacemap = zoomVerificationResult.getFaceMetrics().getZoomFacemap();
        zoomConnectedAPI.authenticateUser(identifier, zoomFacemap, zoomVerificationResult.getSessionId(), new ZoomConnectedAPI.Callback() {
            @Override
            public void completion(final boolean completed, final String message, final JSONObject data) {
                Log.d("data", data.toString());
            }
        });
    }
}
