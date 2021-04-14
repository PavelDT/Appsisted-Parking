package com.github.paveldt.appsistedparking.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// this class contains a ton of boilerplate code
public class ParkingLocation {

    private List<ParkingSite> parkingSites;
    private String recommendedLocation;
    private int availableSpots;

    public ParkingLocation(String jsonLocationInfo) {
        parkingSites = new ArrayList();
        parseLocationInfo(jsonLocationInfo);
    }

    /**
     * Parses location info by processing the json string passed in and processing the JSON into a
     * list of ParkingSite objects.
     * @param json
     */
    private void parseLocationInfo(String json) {
        // verify that the result was turned into json correctly
        try {
            // turn the string representation of the data into a json array
            JSONArray data = new JSONArray(json);

            JSONObject recommendationInfo = data.getJSONObject(0);
            recommendedLocation = recommendationInfo.getString("recommended");
            availableSpots = recommendationInfo.getInt("available-spots");

            for (int i=1; i<data.length(); i++) {
                // declare json object for the parking site
                JSONObject parkingSite = data.getJSONObject(i);

                // each object has six fields
                String location = parkingSite.getString("location");
                String site = parkingSite.getString("site");
                int available= parkingSite.getInt("available");
                int capacity = parkingSite.getInt("capacity");
                float lon = (float)parkingSite.getDouble("lon");
                float lat = (float)parkingSite.getDouble("lat");
                float price = (float)parkingSite.getDouble("price");

                // populate the parking site list
                ParkingSite parkingLotSite = new ParkingSite(location, site, available, capacity, lon, lat, price);
                parkingSites.add(parkingLotSite);
            }

        } catch (JSONException jex) {
            // failed to fetch data as json, log the exception and return an error.
            jex.printStackTrace();
        }
    }

    /**
     * Gets the recommended site
     * @return - String representing the recommended site
     */
    public String getRecommendedLocation() {
        return recommendedLocation;
    }

    /**
     * Gets the number of available spots
     * @return int representing number of available spots.
     */
    public int getAvailableSpots() {
        return availableSpots;
    }

    /**
     * Returns all th eparking site info that was processed from the json earlier.
     * @return - List of ParkingSite objects.
     */
    public List<ParkingSite> getParkingSites() {
        return parkingSites;
    }

    @Override
    public String toString() {
        return recommendedLocation + " " + availableSpots + " " + parkingSites.toString();
    }
}
