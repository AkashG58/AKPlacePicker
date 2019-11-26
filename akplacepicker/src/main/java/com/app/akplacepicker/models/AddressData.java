package com.app.akplacepicker.models;

import android.location.Address;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class AddressData implements Parcelable {

    private double latitude;
    private double longitude;
    private String placeName;
    private List<Address> addressList = new ArrayList<>();

    public AddressData(double latitude, double longitude, String placeName, List<Address> addressList) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.addressList = addressList;
        this.placeName = placeName;
    }

    protected AddressData(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
        placeName = in.readString();
        addressList = in.createTypedArrayList(Address.CREATOR);
    }

    public static final Creator<AddressData> CREATOR = new Creator<AddressData>() {
        @Override
        public AddressData createFromParcel(Parcel in) {
            return new AddressData(in);
        }

        @Override
        public AddressData[] newArray(int size) {
            return new AddressData[size];
        }
    };

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public List<Address> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<Address> addressList) {
        this.addressList = addressList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(placeName);
        dest.writeTypedList(addressList);
    }
}
