package com.github.paveldt.appsistedparking.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

// this class contains a ton of boilerplate code
public class ParkingLocation {

    List<ParkingSite> parkingSites;

    public ParkingLocation(String jsonLocationInfo) {
        parseLocationInfo(jsonLocationInfo);
    }

    private void parseLocationInfo(String json) {
        // verify that the result was turned into json correctly
        try {
            // turn the string representation of the data into a json array
            JSONArray data = new JSONArray(json);

            for (int i=0; i<data.length(); i++) {
                // declare json object for the parking site
                JSONObject parkingSite = data.getJSONObject(i);
                // each object has six fields
                String location = data.getString(0);
                String site = data.getString(1);
                int available= data.getInt(2);
                int capacity = data.getInt(3);
                float lon = (float)data.getDouble(4);
                float lat = (float)data.getDouble(5);

                // populate the parking site list
                ParkingSite parkingLotSite = new ParkingSite(location, site, available, capacity, lon, lat);
                parkingSites.add(parkingLotSite);
            }

        } catch (JSONException jex) {
            // failed to fetch data as json, log the exception and return an error.
            jex.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return parkingSites.toString();
    }
}
