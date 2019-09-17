package com.accurascandemo.api;

import android.content.Context;
import android.os.AsyncTask;

import com.accurascandemo.R;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.RequestBody;

/**
 * Created by latitude on 14/4/17.
 */

public class RequestTask extends AsyncTask<Void, String, String> {

    private Context context;
    private RequestBody requestBody;
    private String header, postFixUrl;
    private boolean isZoomUrl, isDelete;

    public RequestTask(Context context, String header, RequestBody requestBody, String postFixUrl, boolean isZoomUrl) {
        this.context = context;
        this.requestBody = requestBody;
        this.header = header;
        this.postFixUrl = postFixUrl;
        this.isZoomUrl = isZoomUrl;
    }

    public RequestTask(Context context, String header, RequestBody requestBody, String postFixUrl, boolean isZoomUrl, boolean isDelete) {
        this.context = context;
        this.requestBody = requestBody;
        this.header = header;
        this.postFixUrl = postFixUrl;
        this.isZoomUrl = isZoomUrl;
        this.isDelete = isDelete;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... voids) {
        String response = null;
        try {
            if (requestBody == null) {
                if (isDelete) {
                    response = Soap.getSoapResponseDelete(context, isZoomUrl ? context.getString(R.string.base_zoom) + postFixUrl : context.getString(R.string.base) + postFixUrl, header);
                } else {
                    response = Soap.getSoapResponse(context, isZoomUrl ? context.getString(R.string.base_zoom) + postFixUrl : context.getString(R.string.base) + postFixUrl, header);
                }
            } else {
                response = Soap.getSoapResponsePost(context, isZoomUrl ? context.getString(R.string.base_zoom) + postFixUrl : postFixUrl.equals(context.getString(R.string.api)) ? context.getString(R.string.api) : context.getString(R.string.base) + postFixUrl, header, requestBody);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return response;
    }
}
