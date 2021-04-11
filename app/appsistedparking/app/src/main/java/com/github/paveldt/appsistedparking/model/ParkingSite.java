package com.github.paveldt.appsistedparking.model;

public class ParkingSite {

    private String location;
    private String site;
    private int available;
    private int capacity;
    private float lon;
    private float lat;
    private float price;

    public ParkingSite(String location, String site, int available, int capacity, float lon, float lat, float price) {
        this.location = location;
        this.site = site;
        this.available = available;
        this.capacity = capacity;
        this.lon = lon;
        this.lat = lat;
        this.price = price;
    }

    /**
     * The name of the site
     * @return - String representing the site
     */
    public String getSite() {
        return site;
    }

    /**
     * Number of available spots
     * @return - int representing available spots
     */
    public int getAvailable() {
        return available;
    }

    /**
     * Get the price of the site
     * @return - float representing price of site
     */
    public float getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return location + " " +  site + " " +  available + " " + capacity + " " +  lon + " " + lat + " " + price;
    }
}
