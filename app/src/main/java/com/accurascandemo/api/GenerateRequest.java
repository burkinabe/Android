package com.accurascandemo.api;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.accurascandemo.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by latitude on 14/4/17.
 */

public class GenerateRequest {

    public static RequestBody requestContactUs(String company, String name, String email, String phone, String country, String message) {
        return new FormBody.Builder()
                .add("name", name)
                .add("company", company)
                .add("email", email)
                .add("phone", phone)
                .add("country", country)
                .add("message", message)
                .add("device_type", "2")
                .build();
    }

    public static RequestBody requestPanAadharDetail(File scan_image, int card_type){
        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
//                .addFormDataPart("api_key",context.getString(R.string.api_key))
                .addFormDataPart("card_type", String.valueOf(card_type))
                .addFormDataPart("scan_image",
                        scan_image.getName(),
                        RequestBody.create(MediaType.parse("image/*"), scan_image))
                .build();
    }

    public static RequestBody requestEnroll(String enrollmentIdentifier, String sessionId, byte[] zoomFacemap) {
        String zoomFacemapStr =  convertFacemapToBase64String(zoomFacemap);
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("enrollmentIdentifier", enrollmentIdentifier);
            parameters.put("sessionId", sessionId);
            parameters.put("facemap", zoomFacemapStr);

        } catch (Exception e) {
            // handle exception
        }

        Log.d("GenerateRequest", "requestEnroll param : " + parameters.toString());

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        return RequestBody.create(JSON, parameters.toString());
    }

    public static RequestBody requestFacemap(String enrollmentIdentifier, byte[] zoomFacemap) {
        String zoomFacemapStr =  convertFacemapToBase64String(zoomFacemap);
        return new FormBody.Builder()
                .add("sessionId", enrollmentIdentifier)
                .add("zoomSessionData", zoomFacemapStr)
                .build();
    }

    public static RequestBody requestLiveness(byte[] zoomFacemap, String enrollmentIdentifier) {
        String zoomFacemapStr =  convertFacemapToBase64String(zoomFacemap);
        return new FormBody.Builder()
                .add("sessionId", enrollmentIdentifier)
                .add("sessionData", zoomFacemapStr)
                .build();
    }

    private static String convertFacemapToBase64String(byte[] facemap) {
        return Base64.encodeToString(facemap, Base64.NO_WRAP).replace(" ", "+");
    }

    public static RequestBody requestAuthenticate(byte[] zoomFacemap, String sessionId, String enrollmentIdentifier) {
        JSONObject parameters = new JSONObject();

        // Note: This will be updated in the future, see comment above convertFacemapToBase64String function
        String zoomFacemapStr =  convertFacemapToBase64String(zoomFacemap);

        JSONObject sourceObject = new JSONObject();
        JSONArray jsonFacemapsArray = new JSONArray();
        JSONObject facemapObject = new JSONObject();

        try {
            parameters.put("performContinuousLearning", true);

            facemapObject.put("facemap", zoomFacemapStr);
            jsonFacemapsArray.put(facemapObject);
            parameters.put("targets", jsonFacemapsArray);

            sourceObject.put("enrollmentIdentifier", enrollmentIdentifier);
            parameters.put("source", sourceObject);

            parameters.put("sessionId", sessionId);

        } catch (Exception e) {
            // handle exception
        }
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        return RequestBody.create(JSON, parameters.toString());
    }

    public static RequestBody requestSaveData(String mrz, String document_type, String document_no, String document_check_number, String dni,
                                              String nationality,String country,String sex,String date_of_birth,String birth_check_number, String date_of_expiry,String expiration_check_number,
                                              String other_id,String other_id_check,String second_row_check_number,String status, String second_name,
                                              String pan_card_no,String aadhar_card_no, String father_name,String mother_name,String address,String passport_no,String passport_date, String place_of_issue) {
        return new FormBody.Builder()
                .add("mrz", mrz)
                .add("document_type", document_type)
                .add("document_no", document_no)
                .add("document_check_number", document_check_number)
                .add("dni", dni)
                .add("nationality", nationality)
                .add("country", country)
                .add("sex", sex)
                .add("date_of_birth", date_of_birth)
                .add("birth_check_number", birth_check_number)
                .add("date_of_expiry", date_of_expiry)
                .add("expiration_check_number", expiration_check_number)
                .add("other_id", other_id)
                .add("other_id_check", other_id_check)
                .add("second_row_check_number", second_row_check_number)
                .add("status", status)
                .add("second_name", second_name)
                .add("pan_card_no", pan_card_no)
                .add("aadhar_card_no", aadhar_card_no)
                .add("father_name", father_name)
                .add("mother_name", mother_name)
                .add("address", address)
                .add("passport_no", passport_no)
                .add("passport_date", passport_date)
                .add("place_of_issue", place_of_issue)
                .build();
    }

}
