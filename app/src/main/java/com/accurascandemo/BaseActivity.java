package com.accurascandemo;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.accurascandemo.util.AlertDialogAbstract;
import com.accurascandemo.util.AppGeneral;
import com.accurascandemo.util.DBHelper;

import java.util.regex.Pattern;


/**
 * Created by richa on 27/4/17.
 */

public class BaseActivity extends AppCompatActivity {

    protected DBHelper dbHelper;
    private ProgressDialog mProgressDialog;
    protected static final String TYPE = "type";
    private Dialog dialog;
    private EditText etEmail;
    protected String identifier;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setStatusBarColor(getResources().getColor(R.color.red));
//        }
        dbHelper = new DBHelper(this); //initialize the DBhelper class
        AppGeneral.appContext = getApplicationContext();
        initializeProgressDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Hide the status bar.
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        } else {
//            View decorView = getWindow().getDecorView();
//            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
//            decorView.setSystemUiVisibility(uiOptions);
//        }
    }

    private void initializeProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Loading...");
    }

    public void showProgressDialog() {
        try {
            mProgressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismissProgressDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing())
                mProgressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        if (doubleBackToExitPressedOnce) {
//            super.onBackPressed();
//            return;
//        }
//
//        this.doubleBackToExitPressedOnce = true;
//        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
//
//        new Handler().postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                doubleBackToExitPressedOnce = false;
//            }
//        }, 2000);
    }


    protected void showEmailDialog(View.OnClickListener onClickListener) {
        if (dialog == null) {
            dialog = new Dialog(this, R.style.AppTheme);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_email);
            dialog.setCancelable(false);
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            }
            etEmail = dialog.findViewById(R.id.etEmail);
            dialog.findViewById(R.id.tvOk).setOnClickListener(onClickListener);
            dialog.findViewById(R.id.tvCancelDia).setOnClickListener(onClickListener);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    dialog = null;
                }
            });
            if (!this.isFinishing()) {
                dialog.show();
            }
        }
    }

    protected boolean isValidate(){
        String msg = "";
        if(TextUtils.isEmpty(etEmail.getText().toString())){
            msg = getString(R.string.empty_email);
        }else if(!Pattern.matches(Patterns.EMAIL_ADDRESS.pattern(), etEmail.getText().toString())){
            msg = getString(R.string.valid_email);
        }

        if(!TextUtils.isEmpty(msg)){
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

    protected void dismissEmailDialog(){
        if(dialog!= null){
            dialog.dismiss();
            dialog = null;
        }
    }
}
