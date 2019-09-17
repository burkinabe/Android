package com.accurascandemo;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.accurascandemo.R;
import com.accurascandemo.util.AppGeneral;

public class AboutActivity extends BaseActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView tvAppLink = (TextView) findViewById(R.id.tvAppLink);
        tvAppLink.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        findViewById(R.id.ivBack).setOnClickListener(this);
        tvAppLink.setOnClickListener(this);
        findViewById(R.id.ibLinkedIn).setOnClickListener(this);
        findViewById(R.id.ibFacebook).setOnClickListener(this);
        findViewById(R.id.ibTwitter).setOnClickListener(this);

        ((TextView) findViewById(R.id.tvToolbarTitle)).setText(getString(R.string.text_about));
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

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.ivBack:
                onBackPressed();
                break;

            case R.id.tvAppLink:
                openLink(AppGeneral.APP_LINK);
                break;

            case R.id.ibLinkedIn:
                openLink(AppGeneral.LINKED_IN_LINK);
                break;

            case R.id.ibFacebook:
                openLink(AppGeneral.FB_LINK);
                break;

            case R.id.ibTwitter:
                openLink(AppGeneral.TWITTER_LINK);
                break;
        }
    }

    private void openLink(int flag){

        String link = "";
        switch (flag){

            case AppGeneral.APP_LINK:
                link = getString(R.string.text_website);
                break;

            case AppGeneral.LINKED_IN_LINK:
                link = getString(R.string.text_linkedin_link);
                break;

            case AppGeneral.FB_LINK:
                link = getString(R.string.text_fb_link);
                break;

            case AppGeneral.TWITTER_LINK:
                link = getString(R.string.text_twitter_link);
                break;
        }

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        startActivity(browserIntent);
    }
}
