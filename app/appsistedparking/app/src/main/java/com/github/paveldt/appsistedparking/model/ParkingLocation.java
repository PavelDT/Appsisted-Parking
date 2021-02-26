package com.github.paveldt.appsistedparking.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// this class contains a ton of boilerplate code
public class ParkingLocation {

    List<ParkingSite> parkingSites;
    String recommendedLocation;
    int availableSpots;

    public ParkingLocation(String jsonLocationInfo) {
        parkingSites = new ArrayList();
        parseLocationInfo(jsonLocationInfo);
    }

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

                // populate the parking site list
                ParkingSite parkingLotSite = new ParkingSite(location, site, available, capacity, lon, lat);
                parkingSites.add(parkingLotSite);
            }

        } catch (JSONException jex) {
            // failed to fetch data as json, log the exception and return an error.
            jex.printStackTrace();
        }
    }

    public String getRecommendedLocation() {
        return recommendedLocation;
    }

    public int getAvailableSpots() {
        return availableSpots;
    }

    public List<ParkingSite> getParkingSites() {
        return parkingSites;
    }

    @Override
    public String toString() {
        return recommendedLocation + " " + availableSpots + " " + parkingSites.toString();
    }
}
