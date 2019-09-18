package com.accurascandemo.util;

import android.content.Context;
import android.graphics.Bitmap;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SendResultToServer {
    private File frontImageFile = null;
    private File backImageFile = null;
    private File faceImageFile = null;

    private static final SendResultToServer ourInstance = new SendResultToServer();

    public static SendResultToServer getInstance() {
        return ourInstance;
    }

    private SendResultToServer() {
    }

    public void send(final Context context, final Bitmap frontImage, final Bitmap backImage, final Bitmap faceImage, final String subject, final String body, final String type, final String liveness, final String facematch) {
        frontImageFile = null;
        backImageFile = null;
        faceImageFile = null;

        if (Utils.isNetworkAvailable(context)) {
            if (frontImage != null) {
                frontImageFile = Utils.fileFromBitmap(context, frontImage, "frontImage.jpg");
            }
            if (backImage != null) {
                backImageFile = Utils.fileFromBitmap(context, backImage, "backImage.jpg");
            }
            if (faceImage != null) {
                faceImageFile = Utils.fileFromBitmap(context, faceImage, "faceImage.jpg");
            }

            pushToServer(subject, body, type, liveness, facematch);  //push data to server
        }
    }

    //Pushing  data to server
    private void pushToServer(String subject, String body, String type, String liveness, String facematch) {
        Map<String, String> jsonObject = new HashMap<>();
        jsonObject.put("mailSubject", subject);
        jsonObject.put("mailBody", body);
        jsonObject.put("type",type);
        jsonObject.put("liveness",liveness);
        jsonObject.put("facematch",facematch);
        jsonObject.put("platform","Android");

        Map<String, File> multiPartFileMap = new HashMap<>();
        if (frontImageFile != null) {
            multiPartFileMap.put("imageFront", frontImageFile);
        }
        if (backImageFile != null) {
            multiPartFileMap.put("imageBack", backImageFile);
        }

        if (faceImageFile != null) {
            multiPartFileMap.put("imageFace", faceImageFile);
        }

        if (!multiPartFileMap.isEmpty()) {
            AndroidNetworking.upload("https://accurascan.com/sendEmailApi/sendEmail.php")
                    .addMultipartFile(multiPartFileMap)
                    .addMultipartParameter(jsonObject)
                    .setPriority(Priority.HIGH)
                    .build()
                    .setUploadProgressListener(new UploadProgressListener() {
                        @Override
                        public void onProgress(long bytesUploaded, long totalBytes) {
                            // do nothing
                        }
                    })
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // do nothing
                        }

                        @Override
                        public void onError(ANError error) {
                            // do nothing
                        }
                    });
        }
    }
}