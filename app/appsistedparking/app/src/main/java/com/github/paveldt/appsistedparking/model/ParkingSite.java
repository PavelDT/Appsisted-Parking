package com.github.paveldt.appsistedparking.model;

public class ParkingSite {

    String location;
    String site;
    int available;
    int capacity;
    float lon;
    float lat;

    public ParkingSite(String location, String site, int available, int capacity, float lon, float lat) {
        this.location = location;
        this.site = site;
        this.available = available;
        this.capacity = capacity;
        this.lon = lon;
        this.lat = lat;
    }

}
