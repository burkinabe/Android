package com.accurascandemo;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class PreviewScreenActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_priview_screen);

        ImageView ivPreview = findViewById(R.id.ivPreview);
        Glide.with(this).load(getIntent().getStringExtra("fileUrl")).into(ivPreview);

        findViewById(R.id.tvUsePhoto).setOnClickListener(this);
        findViewById(R.id.tvCancel).setOnClickListener(this);
    }

    //handle click of particular view
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.tvUsePhoto:
                setResult(RESULT_OK);
                finish();
                break;

            case R.id.tvCancel:
                finish();
                break;
        }
    }
}
