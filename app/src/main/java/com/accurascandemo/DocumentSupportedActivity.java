package com.accurascandemo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;

import com.accurascandemo.R;

public class DocumentSupportedActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_supported);

        findViewById(R.id.ivBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        ((TextView) findViewById(R.id.tvToolbarTitle)).setText(getString(R.string.text_document_supported));

        SpannableString styledString = new SpannableString(getString(R.string.text_connect));
        ((TextView) findViewById(R.id.tvToolbarTitle)).setText(getString(R.string.text_document_supported));
        TextView tvConnectString = (TextView) findViewById(R.id.tvConnectString);
        ClickableSpan clickableSpan = new ClickableSpan() {

            @Override
            public void onClick(View widget) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("plain/text");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {getString(R.string.text_email_id)});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                startActivity(Intent.createChooser(emailIntent, "Send mail"));
            }
        };

        styledString.setSpan(clickableSpan, 23, 45, 0);
        styledString.setSpan(new URLSpan(getString(R.string.text_connect)), 23, 45, 0);
        styledString.setSpan(new ForegroundColorSpan(Color.BLUE), 23, 45, 0);
        tvConnectString.setMovementMethod(LinkMovementMethod.getInstance());
        tvConnectString.setText(styledString);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AccuraDemoApplication.getInstance().reportToGoogleAnalytics(getString(R.string.doc_supported_screen));
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
