package com.chatter.classes;

import com.google.android.gms.maps.model.LatLng;

public class Location {
    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    private Double latitude;
    private Double longitude;

    public Location(LatLng latLng){
        this.latitude = latLng.latitude;
        this.longitude = latLng.longitude;
    }
    private Location() {}
}
