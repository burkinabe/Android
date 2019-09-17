package com.accurascandemo.api;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.accurascandemo.R;
import com.accurascandemo.model.AuthenticationData;
import com.accurascandemo.model.LastSavedData;
import com.accurascandemo.model.LivenessData;
import com.accurascandemo.model.PanAadharDetail;
import com.accurascandemo.util.ParsedResponse;
import com.accurascandemo.util.SessionManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by latitude on 14/4/17.
 */

public class HandleResponse {

    public static ParsedResponse responseContactUs(Activity context, String response) {

        ParsedResponse p = new ParsedResponse();
        try {
            if (!TextUtils.isEmpty(response)) {
                JSONObject objRes = new JSONObject(response);
                if (objRes.getBoolean("status") && objRes.getString(context.getString(R.string.key_status_code))
                        .equals(context.getString(R.string.status_code_success))) {
                    p.error = false;
                    p.o = objRes.getString(context.getString(R.string.key_message));
                } else {
                    p.error = true;
                    p.o = objRes.getString(context.getString(R.string.key_message));
                }
            } else {
                p.error = true;
                p.o = context.getString(R.string.err_something_wrong);
            }
        } catch (JSONException e) {
            p.error = true;
            p.o = e.getMessage();
            e.printStackTrace();
        }
        return p;

    }

    public static ParsedResponse responsePanAadharDetail(Activity context, String response) {

        ParsedResponse p = new ParsedResponse();
        try {
            if (!TextUtils.isEmpty(response)) {
                JSONObject objRes = new JSONObject(response);
                if (objRes.has("status") && objRes.getBoolean("status")) {
                    p.error = false;
                    p.o = new Gson().fromJson(objRes.getJSONArray("data").get(0).toString(), PanAadharDetail.class);
                } else {
                    p.error = true;
                    p.o = objRes.getString("message");
                }
            } else {
                p.error = true;
                p.o = context.getString(R.string.err_something_wrong);
            }
        } catch (JSONException e) {
            p.error = true;
            p.o = e.getMessage();
            e.printStackTrace();
        }
        return p;

    }

    public static ParsedResponse responseEnroll(Activity context, String response) {

        ParsedResponse p = new ParsedResponse();
        try {
            if (!TextUtils.isEmpty(response)) {
                JSONObject objRes = new JSONObject(response);
                if (objRes.getJSONObject("meta").getBoolean("ok")) {
                    p.error = false;
                    JSONObject jsonObject = objRes.getJSONObject("data");
                    new SessionManager(context).saveSDKToken(jsonObject.getString("enrollmentIdentifier"));
                } else {
                    p.error = true;
                    p.o = objRes.getJSONObject("meta").getString("message");
                }
            } else {
                p.error = true;
                p.o = context.getString(R.string.err_something_wrong);
            }
        } catch (JSONException e) {
            p.error = true;
            p.o = e.getMessage();
            e.printStackTrace();
        }
        return p;

    }

    public static ParsedResponse responseDeleteEnroll(Activity context, String response) {

        ParsedResponse p = new ParsedResponse();
        try {
            if (!TextUtils.isEmpty(response)) {
                JSONObject objRes = new JSONObject(response);
                Log.w("Reposne", response);
                if(objRes.has("error")){
                    p.error = true;
                    p.o = objRes.getString("message");
                }
                else if (objRes.getJSONObject("meta").getBoolean("ok")) {
                    p.error = false;
                } else {
                    p.error = true;
                    p.o = objRes.getJSONObject("meta").getString("message");
                }
            } else {
                p.error = true;
                p.o = context.getString(R.string.err_something_wrong);
            }
        } catch (JSONException e) {
            p.error = true;
            p.o = e.getMessage();
            e.printStackTrace();
        }
        return p;

    }

    public static ParsedResponse responseFaceMap(Activity context, String response) {

        ParsedResponse p = new ParsedResponse();
        try {
            if (!TextUtils.isEmpty(response)) {
                JSONObject objRes = new JSONObject(response);
                if (objRes.getJSONObject("meta").getBoolean("ok")) {
                    p.error = false;
                    p.o = "";
                } else {
                    p.error = true;
                    p.o = objRes.getJSONObject("meta").getString("message");
                }
            } else {
                p.error = true;
                p.o = context.getString(R.string.err_something_wrong);
            }
        } catch (JSONException e) {
            p.error = true;
            p.o = e.getMessage();
            e.printStackTrace();
        }
        return p;

    }

    public static ParsedResponse responseAuthenticate(Activity context, String response) {

        ParsedResponse p = new ParsedResponse();
        try {
            if (!TextUtils.isEmpty(response)) {
                JSONObject objRes = new JSONObject(response);
                if (objRes.getJSONObject("meta").getBoolean("ok")) {
                    p.error = false;
                    JSONObject jsonObject = objRes.getJSONObject("data");
                    p.o = new Gson().fromJson(jsonObject.getJSONArray("results").toString(), new TypeToken<List<AuthenticationData>>() {}.getType());
                } else {
                    p.error = true;
                    p.o = objRes.getJSONObject("meta").getString("message");
                }
            } else {
                p.error = true;
                p.o = context.getString(R.string.err_something_wrong);
            }
        } catch (JSONException e) {
            p.error = true;
            p.o = e.getMessage();
            e.printStackTrace();
        }
        return p;
    }

    public static ParsedResponse responseLiveness(Activity context, String response) {

        ParsedResponse p = new ParsedResponse();
        try {
            if (!TextUtils.isEmpty(response)) {
                JSONObject objRes = new JSONObject(response);
                if (objRes.getJSONObject("meta").getBoolean("ok")) {
                    p.error = false;
                    JSONObject jsonObject = objRes.getJSONObject("data");
                    p.o = new Gson().fromJson(jsonObject.toString(), LivenessData.class);
                } else {
                    p.error = true;
                    p.o = objRes.getJSONObject("meta").getString("message");
                }
            } else {
                p.error = true;
                p.o = context.getString(R.string.err_something_wrong);
            }
        } catch (JSONException e) {
            p.error = true;
            p.o = e.getMessage();
            e.printStackTrace();
        }
        return p;
    }

    public static ParsedResponse responseSaveData(Activity context, String response) {

        ParsedResponse p = new ParsedResponse();
        try {
            if (!TextUtils.isEmpty(response)) {
                JSONObject objRes = new JSONObject(response);
                if (objRes.getBoolean("status")) {
                    p.error = false;
                    p.o = objRes.getString("message");
                } else {
                    p.error = true;
                    p.o = objRes.getString("message");
                }
            } else {
                p.error = true;
                p.o = context.getString(R.string.err_something_wrong);
            }
        } catch (JSONException e) {
            p.error = true;
            p.o = e.getMessage();
            e.printStackTrace();
        }
        return p;

    }

    public static ParsedResponse responseGetLastSaveData(Activity context, String response) {

        ParsedResponse p = new ParsedResponse();
        try {
            if (!TextUtils.isEmpty(response)) {
                JSONObject objRes = new JSONObject(response);
                if (objRes.getBoolean("status")) {
                    p.error = false;
                    JSONObject jsonObject = objRes.getJSONObject("data");
                    p.o = new Gson().fromJson(jsonObject.toString(), LastSavedData.class);
                } else {
                    p.error = true;
                    p.o = objRes.getJSONObject("meta").getString("message");
                }
            } else {
                p.error = true;
                p.o = context.getString(R.string.err_something_wrong);
            }
        } catch (JSONException e) {
            p.error = true;
            p.o = e.getMessage();
            e.printStackTrace();
        }
        return p;

    }

}
