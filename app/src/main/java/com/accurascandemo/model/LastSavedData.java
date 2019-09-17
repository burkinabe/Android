package com.accurascandemo.model;

import android.os.Parcel;
import android.os.Parcelable;

public class LastSavedData implements Parcelable {

    public String id,
            email,
            mrz,
            document_type,
            surname,
            given_names,
            document_no,
            document_check_number,
            dni,
            nationality,
            country,
            sex,
            date_of_birth,
            birth_check_number,
            date_of_expiry,
            expiration_check_number,
            other_id,
            other_id_check,
            second_row_check_number,
            status,
            scan_image,
            card_type,
            name,
            second_name,
            pan_card_no,
            aadhar_card_no,
            father_name,
            mother_name,
            address,
            passport_no,
            passport_date,
            place_of_issue,
            file_no,
            created_at;

    protected LastSavedData(Parcel in) {
        id = in.readString();
        email = in.readString();
        mrz = in.readString();
        document_type = in.readString();
        surname = in.readString();
        given_names = in.readString();
        document_no = in.readString();
        document_check_number = in.readString();
        dni = in.readString();
        nationality = in.readString();
        country = in.readString();
        sex = in.readString();
        date_of_birth = in.readString();
        birth_check_number = in.readString();
        date_of_expiry = in.readString();
        expiration_check_number = in.readString();
        other_id = in.readString();
        other_id_check = in.readString();
        second_row_check_number = in.readString();
        status = in.readString();
        scan_image = in.readString();
        card_type = in.readString();
        name = in.readString();
        second_name = in.readString();
        pan_card_no = in.readString();
        aadhar_card_no = in.readString();
        father_name = in.readString();
        mother_name = in.readString();
        address = in.readString();
        passport_no = in.readString();
        passport_date = in.readString();
        place_of_issue = in.readString();
        file_no = in.readString();
        created_at = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(email);
        dest.writeString(mrz);
        dest.writeString(document_type);
        dest.writeString(surname);
        dest.writeString(given_names);
        dest.writeString(document_no);
        dest.writeString(document_check_number);
        dest.writeString(dni);
        dest.writeString(nationality);
        dest.writeString(country);
        dest.writeString(sex);
        dest.writeString(date_of_birth);
        dest.writeString(birth_check_number);
        dest.writeString(date_of_expiry);
        dest.writeString(expiration_check_number);
        dest.writeString(other_id);
        dest.writeString(other_id_check);
        dest.writeString(second_row_check_number);
        dest.writeString(status);
        dest.writeString(scan_image);
        dest.writeString(card_type);
        dest.writeString(name);
        dest.writeString(second_name);
        dest.writeString(pan_card_no);
        dest.writeString(aadhar_card_no);
        dest.writeString(father_name);
        dest.writeString(mother_name);
        dest.writeString(address);
        dest.writeString(passport_no);
        dest.writeString(passport_date);
        dest.writeString(place_of_issue);
        dest.writeString(file_no);
        dest.writeString(created_at);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LastSavedData> CREATOR = new Creator<LastSavedData>() {
        @Override
        public LastSavedData createFromParcel(Parcel in) {
            return new LastSavedData(in);
        }

        @Override
        public LastSavedData[] newArray(int size) {
            return new LastSavedData[size];
        }
    };
}
