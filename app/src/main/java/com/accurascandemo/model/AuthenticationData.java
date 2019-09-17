package com.accurascandemo.model;

import android.os.Parcel;
import android.os.Parcelable;

public class AuthenticationData implements Parcelable {

    public String glassesScore, livenessResult,
    livenessScore, livenessScoreForAuthenticationFacemap,
    livenessScoreForEnrollmentFacemap, matchScore;
    public boolean authenticated,livenessResultForEnrollmentFacemap, livenessResultForAuthenticationFacemap, glassesDecision;
    public int retryFeedbackSuggestion;

    protected AuthenticationData(Parcel in) {
        glassesScore = in.readString();
        livenessResult = in.readString();
        livenessScore = in.readString();
        livenessScoreForAuthenticationFacemap = in.readString();
        livenessScoreForEnrollmentFacemap = in.readString();
        matchScore = in.readString();
        authenticated = in.readByte() != 0;
        livenessResultForEnrollmentFacemap = in.readByte() != 0;
        livenessResultForAuthenticationFacemap = in.readByte() != 0;
        glassesDecision = in.readByte() != 0;
        retryFeedbackSuggestion = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(glassesScore);
        dest.writeString(livenessResult);
        dest.writeString(livenessScore);
        dest.writeString(livenessScoreForAuthenticationFacemap);
        dest.writeString(livenessScoreForEnrollmentFacemap);
        dest.writeString(matchScore);
        dest.writeByte((byte) (authenticated ? 1 : 0));
        dest.writeByte((byte) (livenessResultForEnrollmentFacemap ? 1 : 0));
        dest.writeByte((byte) (livenessResultForAuthenticationFacemap ? 1 : 0));
        dest.writeByte((byte) (glassesDecision ? 1 : 0));
        dest.writeInt(retryFeedbackSuggestion);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AuthenticationData> CREATOR = new Creator<AuthenticationData>() {
        @Override
        public AuthenticationData createFromParcel(Parcel in) {
            return new AuthenticationData(in);
        }

        @Override
        public AuthenticationData[] newArray(int size) {
            return new AuthenticationData[size];
        }
    };
}
