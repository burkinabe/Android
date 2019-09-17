package com.accurascandemo.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.accurascandemo.model.ScanData;

import java.util.ArrayList;

/**
 * Created by latitude on 31/8/17.
 */

public class DBHelper {

    private static final String DATABASE_NAME = "ACCURA_SCAN";
    private static final int DB_VERSION = 3;
    private static final String TABLE_NAME = "TABLE_DATA";
    private static final String ID = "ID";
    private static final String KEY_LAST_NAME = "KEY_LAST_NAME";
    private static final String KEY_FIRST_NAME = "KEY_FIRST_NAME";
    private static final String KEY_PASSPORT_NO = "KEY_PASSPORT_NO";
    private static final String KEY_COUNTRY = "KEY_COUNTRY";
    private static final String KEY_GENDER = "KEY_GENDER";
    private static final String KEY_DATE_OF_BIRTH = "KEY_DATE_OF_BIRTH";
    private static final String KEY_DATE_OF_EXPIRY = "KEY_DATE_OF_EXPIRY";
    private static final String KEY_USER_PICTURE = "KEY_USER_PICTURE";
    private static final String KEY_DOCUMENT_TYPE = "KEY_DOCUMENT_TYPE";
    private static final String KEY_ADDRESS = "KEY_ADDRESS";
    private static final String KEY_AUTH = "KEY_AUTH";
    private static final String KEY_GLASSES_DECISION = "KEY_GLASSES_DECISION";
    private static final String KEY_GLASSES_SCORE = "KEY_GLASSES_SCORE";
    private static final String KEY_LIVENESS_AUTH_FACEMAP = "KEY_LIVENESS_AUTH_FACEMAP";
    private static final String KEY_LIVENESS_ENROLL_FACEMAP = "KEY_LIVENESS_ENROLL_FACEMAP";
    private static final String KEY_LIVENESS_SCORE = "KEY_LIVENESS_SCORE";
    private static final String KEY_LIVENESS_AUTH_RESULT_FACEMAP= "KEY_LIVENESS_AUTH_RESULT_FACEMAP";
    private static final String KEY_LIVENESS_ENROLL_RESULT_FACEMAP = "KEY_LIVENESS_ENROLL_RESULT_FACEMAP";
    private static final String KEY_MATCH_SCORE = "KEY_MATCH_SCORE";
    private static final String KEY_LIVENESS_RESULT = "KEY_LIVENESS_RESULT";
    private static final String KEY_RETRY_FEEDBACK_SUGGESTION = "KEY_RETRY_FEEDBACK_SUGGESTION";
    private SQLiteHelper sqLiteHelper;
    private SQLiteDatabase sqLiteDatabase;

    public DBHelper(Context context) {
        sqLiteHelper = new SQLiteHelper(context);
    }

    public void saveScanData(ScanData scanData) {
        try {
            sqLiteDatabase = sqLiteHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_LAST_NAME, scanData.getLastName());
            values.put(KEY_FIRST_NAME, scanData.getFirstName());
            values.put(KEY_PASSPORT_NO, scanData.getPassportNo());
            values.put(KEY_COUNTRY, scanData.getCountry());
            values.put(KEY_GENDER, scanData.getGender());
            values.put(KEY_DATE_OF_BIRTH, scanData.getDateOfBirth());
            values.put(KEY_DATE_OF_EXPIRY, scanData.getDateOfExpiry());
            values.put(KEY_USER_PICTURE, scanData.getUserPicture());
            values.put(KEY_DOCUMENT_TYPE, scanData.getDocumentType());
            values.put(KEY_ADDRESS, scanData.getAddress());
            values.put(KEY_AUTH, scanData.getAuth());
            values.put(KEY_GLASSES_DECISION, scanData.getGlassesDecision());
            values.put(KEY_GLASSES_SCORE, scanData.getGlassesScore());
            values.put(KEY_LIVENESS_AUTH_FACEMAP, scanData.getLivenessAuthFacemap());
            values.put(KEY_LIVENESS_ENROLL_FACEMAP, scanData.getLivenessEnrollFacemap());
            values.put(KEY_LIVENESS_SCORE, scanData.getLivenessScore());
            values.put(KEY_LIVENESS_AUTH_RESULT_FACEMAP, scanData.getLivenessAuthResultFacemap());
            values.put(KEY_LIVENESS_ENROLL_RESULT_FACEMAP, scanData.getLivenessEnrollResultFacemap());
            values.put(KEY_MATCH_SCORE, scanData.getMatchScore());
            values.put(KEY_LIVENESS_RESULT, scanData.getLivenessResult());
            values.put(KEY_RETRY_FEEDBACK_SUGGESTION, scanData.getRetryFeedbackSuggestion());

            if (sqLiteDatabase != null) {
                sqLiteDatabase.insert(TABLE_NAME, null, values);
            }

        } finally {
            if (sqLiteDatabase != null)
                sqLiteDatabase.close();
        }
    }

    public ArrayList<ScanData> getScanData() {
        Cursor cursor = null;
        ArrayList<ScanData> scanDataList = new ArrayList<>();
        try {
            sqLiteDatabase = sqLiteHelper.getWritableDatabase();
            String selectQuery = "SELECT * FROM " + TABLE_NAME;

            if (sqLiteDatabase != null) {
                cursor = sqLiteDatabase.rawQuery(selectQuery, null);
            }
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    ScanData scanData = new ScanData();
                    scanData.setId(cursor.getInt(0));
                    scanData.setLastName(cursor.getString(1));
                    scanData.setFirstName(cursor.getString(2));
                    scanData.setPassportNo(cursor.getString(3));
                    scanData.setCountry(cursor.getString(4));
                    scanData.setGender(cursor.getString(5));
                    scanData.setDateOfBirth(cursor.getString(6));
                    scanData.setDateOfExpiry(cursor.getString(7));
                    scanData.setUserPicture(cursor.getBlob(8));
                    scanData.setDocumentType(cursor.getString(9));
                    scanData.setAddress(cursor.getString(10));
                    scanData.setAuth(cursor.getString(11));
                    scanData.setGlassesDecision(cursor.getString(12));
                    scanData.setGlassesScore(cursor.getString(13));
                    scanData.setLivenessAuthFacemap(cursor.getString(14));
                    scanData.setLivenessEnrollFacemap(cursor.getString(15));
                    scanData.setLivenessScore(cursor.getString(16));
                    scanData.setLivenessAuthResultFacemap(cursor.getString(17));
                    scanData.setLivenessEnrollResultFacemap(cursor.getString(18));
                    scanData.setMatchScore(cursor.getString(19));
                    scanData.setLivenessResult(cursor.getString(20));
                    scanData.setRetryFeedbackSuggestion(cursor.getString(21));
                    scanDataList.add(scanData);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (sqLiteDatabase != null)
                sqLiteDatabase.close();
        }

        return scanDataList;
    }

    private class SQLiteHelper extends SQLiteOpenHelper {

        String CREATE_TABLE = "create table " + TABLE_NAME + "( " + ID + " INTEGER PRIMARY KEY autoincrement, "
                + KEY_LAST_NAME + " TEXT, "
                + KEY_FIRST_NAME + " TEXT, "
                + KEY_PASSPORT_NO + " TEXT, "
                + KEY_COUNTRY + " TEXT, "
                + KEY_GENDER + " TEXT, "
                + KEY_DATE_OF_BIRTH + " TEXT, "
                + KEY_DATE_OF_EXPIRY + " TEXT, "
                + KEY_USER_PICTURE + " BLOB, "
                + KEY_DOCUMENT_TYPE + " TEXT, "
                + KEY_ADDRESS + " TEXT, "
                + KEY_AUTH + " TEXT, "
                + KEY_GLASSES_DECISION + " TEXT, "
                + KEY_GLASSES_SCORE + " TEXT, "
                + KEY_LIVENESS_AUTH_FACEMAP + " TEXT, "
                + KEY_LIVENESS_ENROLL_FACEMAP + " TEXT, "
                + KEY_LIVENESS_SCORE + " TEXT, "
                + KEY_LIVENESS_AUTH_RESULT_FACEMAP + " TEXT, "
                + KEY_LIVENESS_ENROLL_RESULT_FACEMAP + " TEXT, "
                + KEY_MATCH_SCORE + " TEXT, "
                + KEY_LIVENESS_RESULT + " TEXT, "
                + KEY_RETRY_FEEDBACK_SUGGESTION + " TEXT);";

        SQLiteHelper(Context context) {
            super(context, DATABASE_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}
