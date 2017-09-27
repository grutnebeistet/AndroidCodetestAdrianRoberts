package com.roberts.adrian.androidcodetestadrianroberts.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ContactAddress implements Parcelable {
    private String address;
    private String addressId;
    private int type;

    public ContactAddress(String address, String id, int type) {
        this.address = address;
        this.addressId = id;
        this.type = type;
    }

    public ContactAddress(Parcel in) {
        this.address = in.readString();
        this.addressId = in.readString();
        this.type = in.readInt();
    }

    public static final Parcelable.Creator<ContactAddress> CREATOR
            = new Parcelable.Creator<ContactAddress>() {
        public ContactAddress createFromParcel(Parcel in) {
            return new ContactAddress(in);
        }

        public ContactAddress[] newArray(int size) {
            return new ContactAddress[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.address);
        parcel.writeString(this.addressId);
        parcel.writeInt(this.type);
    }


    public void setType(int type) {
        this.type = type;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getType() {
        return type;
    }

    public String getAddressId() {
        return addressId;
    }

    public String getAddress() {
        return address;
    }

}
