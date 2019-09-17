package com.accurascandemo.api;

import android.content.Context;

import com.accurascandemo.R;
import com.accurascandemo.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by richa on 28/4/17.
 */

public class Soap {

    private static String getConnectionErrorResponse(Context context)
            throws JSONException {
        JSONObject objRes = new JSONObject();
        objRes.put(context.getString(R.string.key_status_code), context.getString(R.string.status_code_error));
        objRes.put(context.getString(R.string.key_message), context.getString(R.string.err_network));
        return objRes.toString();
    }

    public static String getSoapResponse(Context context, String postFixUrl, String header)
            throws IOException, JSONException {

        if (Utils.isNetworkAvailable(context)) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();
            Request request = new Request.Builder()
                    .addHeader(postFixUrl.contains("biometrics") ? context.getString(R.string.header_key_zoom) : context.getString(R.string.header_key), header)
                    .url(postFixUrl)
                    .build();
            Response response = client.newCall(request).execute();
            String res = response.body().string();
            Utils.Log_e("Response : ", res);
            return res;
        } else {
            return getConnectionErrorResponse(context);
        }
    }

    public static String getSoapResponseDelete(Context context, String postFixUrl, String header)
            throws IOException, JSONException {

        if (Utils.isNetworkAvailable(context)) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();
            Request request = new Request.Builder()
                    .addHeader(postFixUrl.contains("biometrics") ? context.getString(R.string.header_key_zoom) : context.getString(R.string.header_key), header)
                    .url(postFixUrl)
                    .delete()
                    .build();
            Response response = client.newCall(request).execute();
            String res = response.body().string();
            Utils.Log_e("Response : ", res);
            return res;
        } else {
            return getConnectionErrorResponse(context);
        }
    }

    public static String getSoapResponsePost(Context context, String postFixUrl, String header, RequestBody requestBody)
            throws IOException, JSONException {
        if (Utils.isNetworkAvailable(context)) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();
            Request.Builder builder = new Request.Builder();
            builder.url(postFixUrl);
            if (postFixUrl.contains("biometrics")) {
                builder.addHeader("Content-Type", "application/json");
            }
            builder.addHeader(postFixUrl.contains("biometrics") ? context.getString(R.string.header_key_zoom) : postFixUrl.equals(context.getString(R.string.api)) ? context.getString(R.string.header_key_new) : context.getString(R.string.header_key), header);
            builder.post(requestBody);
            Request request = builder.build();
            Response response = client.newCall(request).execute();
            String res = response.body().string();
            Utils.Log_e("Response : ", res);
            return res;
        } else {
            return getConnectionErrorResponse(context);
        }
    }
}
