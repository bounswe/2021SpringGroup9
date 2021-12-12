package com.example.postory.models;

public class LocationModel {
    private String locName;
    private double lat;
    private double lon;



    public LocationModel(String locName, double lat, double lon) {
        this.locName = locName;
        this.lat = lat;
        this.lon = lon;
    }

    public String getLocName() {
        return locName;
    }

    public void setLocName(String locName) {
        this.locName = locName;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
