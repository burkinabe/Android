package com.accurascandemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.accurascandemo.Adapter.DrawerListAdapter;
import com.accurascandemo.R;

import java.util.ArrayList;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String TYPE = "type";
    public static final String TITLE = "title";
    /*private DrawerLayout drawerLayout;
    private ListView lvDrawer;
    private ActionBarDrawerToggle mDrawerToggle;*/

    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_main);

        TextView txtTitle = findViewById(R.id.txtTitle);
        txtTitle.setText(getIntent().getStringExtra(TITLE));

        initUI();
    }

    private void initUI() {
        findViewById(R.id.btnPassport).setOnClickListener(this);
        findViewById(R.id.ivBack).setOnClickListener(this);
        findViewById(R.id.btnDrivingLicense).setOnClickListener(this);
        findViewById(R.id.btnBarcode).setOnClickListener(this);
        findViewById(R.id.btnPanCard).setOnClickListener(this);
        findViewById(R.id.btnAdhaarCard).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int type = 0;

        switch (v.getId()) {

            case R.id.ivMenu:
                onBackPressed();
                break;

            case R.id.tvLoginViaFace:
                startActivity(new Intent(this, LoginEmailActivity.class));
                break;

            case R.id.btnPassport:
                type = 1;
                openScanActivity(type);
                break;

            case R.id.btnDrivingLicense:
                type = 2;
                openScanActivity(type);
                break;

            case R.id.btnBarcode:
                type = 3;
                openScanActivity(type);
                break;

            case R.id.btnPanCard:
                type = 4;
                openScanActivity(type);
                break;

            case R.id.btnAdhaarCard:
                type = 5;
                openScanActivity(type);
                break;

            case R.id.ivBack:
                finish();
                break;
        }
    }

    private void openScanActivity(int type) {
        Intent intent = new Intent(this, ScanNowPassportImageActivity.class);
        intent.putExtra(TYPE, type);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}
