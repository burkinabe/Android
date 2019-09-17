package com.accurascandemo.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class PanAadharDetail implements Parcelable {
    public String card;
    public String name;
    public String second_name;
    @SerializedName("date of birth")
    public String date_of_birth;
    public String pan_card_no;
    public String scan_image;
    public String aadhar_card_no;
    public String sex;
    public String address;

    protected PanAadharDetail(Parcel in) {
        card = in.readString();
        name = in.readString();
        second_name = in.readString();
        date_of_birth = in.readString();
        pan_card_no = in.readString();
        scan_image = in.readString();
        aadhar_card_no = in.readString();
        sex = in.readString();
        address = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(card);
        dest.writeString(name);
        dest.writeString(second_name);
        dest.writeString(date_of_birth);
        dest.writeString(pan_card_no);
        dest.writeString(scan_image);
        dest.writeString(aadhar_card_no);
        dest.writeString(sex);
        dest.writeString(address);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PanAadharDetail> CREATOR = new Creator<PanAadharDetail>() {
        @Override
        public PanAadharDetail createFromParcel(Parcel in) {
            return new PanAadharDetail(in);
        }

        @Override
        public PanAadharDetail[] newArray(int size) {
            return new PanAadharDetail[size];
        }
    };
}
