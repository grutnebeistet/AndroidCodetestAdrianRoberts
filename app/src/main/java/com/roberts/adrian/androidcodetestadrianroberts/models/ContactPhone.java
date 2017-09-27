package com.roberts.adrian.androidcodetestadrianroberts.models;

import android.os.Parcel;
import android.os.Parcelable;


public class ContactPhone implements Parcelable {
    private String number;
    private String numberIdd;
    private int type;

    public ContactPhone(String number, String id, int type) {
        this.number = number;
        this.numberIdd = id;
        this.type = type;
    }

    public ContactPhone(Parcel in) {
        this.number = in.readString();
        this.numberIdd = in.readString();
        this.type = in.readInt();
    }

    public static final Parcelable.Creator<ContactPhone> CREATOR
            = new Parcelable.Creator<ContactPhone>() {
        public ContactPhone createFromParcel(Parcel in) {
            return new ContactPhone(in);
        }

        public ContactPhone[] newArray(int size) {
            return new ContactPhone[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.number);
        parcel.writeString(this.numberIdd);
        parcel.writeInt(this.type);
    }

    public void setType(int type) {
        this.type = type;
    }


    public void setNumber(String number) {
        this.number = number;
    }

    public void setNumberIdd(String numberIdd) {
        this.numberIdd = numberIdd;
    }

    public int getType() {
        return type;
    }

    public String getNumberIdd() {
        return numberIdd;
    }

    public String getNumber() {
        return number;
    }

}
