package com.roberts.adrian.androidcodetestadrianroberts.models;

import android.os.Parcel;
import android.os.Parcelable;


public class ContactEmail implements Parcelable {
    private String email;
    private String emailId;
    private int type;

    public ContactEmail(String email, String id, int type) {
        this.email = email;
        this.emailId = id;
        this.type = type;
    }

    public ContactEmail(Parcel in) {
        this.email = in.readString();
        this.emailId = in.readString();
        this.type = in.readInt();
    }

    public static final Parcelable.Creator<ContactEmail> CREATOR
            = new Parcelable.Creator<ContactEmail>() {
        public ContactEmail createFromParcel(Parcel in) {
            return new ContactEmail(in);
        }

        public ContactEmail[] newArray(int size) {
            return new ContactEmail[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.email);
        parcel.writeString(this.emailId);
        parcel.writeInt(this.type);

    }


    public void setType(int type) {
        this.type = type;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getType() {
        return type;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getEmailId() {
        return emailId;
    }

    public String getEmail() {
        return email;
    }

}
