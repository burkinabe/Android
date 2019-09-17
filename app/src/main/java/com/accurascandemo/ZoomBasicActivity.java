//
// Main View Controller for the Basic App
//
// The Basic App serves to provide a bare minimum examples of the code required to verify liveness and retrieve ZoOm facemap.
//
// We have purposely left the commenting in this file bare in in the core ZoOm section so-as not to distract
// from the general low amount of complexity required to get an initial integration of ZoOm started.
//
// Please contact support@zoomlogin.com with any issues or questions!
//

package com.accurascandemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;

import com.accurascandemo.R;
import com.facetec.zoom.sdk.*;

public class ZoomBasicActivity extends Activity {
    private Button enrollButton;
    private Button authButton;
    private ContextThemeWrapper ctw = new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light);

    public void onEnrollClick(View v) {
        launchZoomToVerifyLivenessAndRetrieveFacemap();
    }

    public void onAuthClick(View v) {
        launchZoomToVerifyLivenessAndRetrieveFacemap();
    }

    // For server authentication (i.e. both enrolling and matching occurs on server, not on-device),
    // developers should use "Verification Mode" to invoke ZoOm.  Verification Mode performs the liveness check and
    // returns the ZoOm Facemap if configured like below.
    //
    // Enrollment and Authentication modes can also be used here as they can also be configured to return the ZoOm Facemap
    // in exactly the same fashion as Verification Mode.  The only different would be that the biometric would also be
    // stored on the users device if the Enrollment+Authentication mode flow is chosen
    //
    // Please note: this app has been pre-loaded with a public key to encrypt the biometric data.
    // This public key corresponds to the private key in the server-side example apps.
    // For production and deeper testing, creating your own key pair is strongly encouraged.
    public void launchZoomToVerifyLivenessAndRetrieveFacemap() {
        ZoomSDKStatus status = ZoomSDK.getStatus(this);
        if(status != ZoomSDKStatus.INITIALIZED) {
            alertWithMessage("Launch Error", "Unable to launch ZoOm.\nReason: " + status.toString());
            return;
        }

        Intent authenticationIntent = new Intent(this, ZoomVerificationActivity.class);
        startActivityForResult(authenticationIntent, ZoomSDK.REQUEST_CODE_VERIFICATION);
    }

    // This is the conceptual code where you would add your business logic.
    // Note that here we are only providing the code to retrieve the ZoOm facemap itself and not other elements of the full environment.
    //
    // Other elements you will/may need to consider:
    // - A webserver to receive facemap data and how to upload this securely
    // - Where to invoke Verification Mode in order to collect the user facemap
    // - How to associate the user facemap data to the user in your backend database
    // - Where to invoke Verification Mode for a user you already have facemap data for in order to authenticate her
    // - Waiting for your server response before branching the user to your own success/fail screens
    public void handleVerificationSuccessResult(ZoomVerificationResult successResult) {
        // retrieve the ZoOm facemap as byte[]
        if(successResult.getFaceMetrics() != null) {
            // this is the raw biometric data which can be uploaded, or may be
            // base64 encoded in order to handle easier at the cost of processing and network usage
            byte[] zoomFacemap = successResult.getFaceMetrics().getZoomFacemap();
            // handle facemap
        }
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        // make sure the result was returned correctly
        if(resultCode == RESULT_OK) {
            if(requestCode == ZoomSDK.REQUEST_CODE_VERIFICATION) {
                ZoomVerificationResult result = data.getParcelableExtra(ZoomSDK.EXTRA_VERIFY_RESULTS);

                // CASE: you did not set a public key before attempting to retrieve a facemap.
                // Retrieving facemaps requires that you generate a public/private key pair per the instructions at https://dev.zoomlogin.com/zoomsdk/#/zoom-server-guide
                if(result.getStatus() == ZoomVerificationStatus.ENCRYPTION_KEY_INVALID) {
                    AlertDialog alertDialog = new AlertDialog.Builder(ZoomBasicActivity.this).create();
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
                else if(result.getStatus() == ZoomVerificationStatus.USER_PROCESSED_SUCCESSFULLY) {
                    handleVerificationSuccessResult(result);
                }
                else {
                    // handle other error
                }
            }
        }

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // preload sdk resources so the UI is snappy (optional)
                ZoomSDK.preload(ZoomBasicActivity.this);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        enrollButton = findViewById(com.hybridbasicapp.R.id.enrollButton);
//        authButton = findViewById(R.id.authButton);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            enrollButton.setLetterSpacing(0.05f);
            authButton.setLetterSpacing(0.05f);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Initialize the SDK
        initializeZoom();
    }

    private void initializeZoom() {
        // Visit https://dev.zoomlogin.com/zoomsdk/#/account to retrieve your app token
        // Replace BuildConfig.ZOOM_APP_TOKEN below with your app token
        String zoomAppToken = "dUfNhktz2Tcl32pGgbPTZ57QujOQBluh";

        ZoomSDK.setFacemapEncryptionKey(ZoomConnectedConfig.PublicKey);

        ZoomSDK.initialize(
                this,
                zoomAppToken,
                mInitializeCallback
        );

        // preload sdk resources so the UI is snappy (optional)
        ZoomSDK.preload(ZoomBasicActivity.this);

        ZoomCustomization currentCustomization = new ZoomCustomization();
        ZoomSDK.setCustomization(currentCustomization);
    }

    private final ZoomSDK.InitializeCallback mInitializeCallback = new ZoomSDK.InitializeCallback() {
        @Override
        public void onCompletion(boolean successful) {
            if(successful) {
                ZoomBasicActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        enrollButton.setEnabled(true);
                        authButton.setEnabled(true);
                    }
                });
            }
            else {
                ZoomBasicActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        alertWithMessage("Initialization failed.", "Please check that you have set your ZoOm app token to the zoomAppToken variable in this file.  To retrieve your app token, visit https://dev.zoomlogin.com/zoomsdk/#/account.");
                    }
                });
            }
        }
    };

    private void alertWithMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setTitle(title);
        builder.setMessage(message);
        AlertDialog dialog = builder.create();

        dialog.show();
    }
}
