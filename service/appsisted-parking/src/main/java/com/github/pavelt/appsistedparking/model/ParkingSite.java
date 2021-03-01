package com.github.pavelt.appsistedparking.model;

import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.github.pavelt.appsistedparking.database.CassandraClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ParkingSite {

    public static String getLocationInfo(String location) {
        // Using "json" infront of the column specification allows cassandra to
        // return the result as a json formatted object.
        String query = "SELECT json location, site, available, capacity, lat, lon " +
                       "FROM appsisted.parkingsite WHERE location=?";

        PreparedStatement ps = CassandraClient.getClient().prepare(query);
        BoundStatement bs = ps.bind(location.toLowerCase());

        // fetch all parking sites for that location
        ResultSet rs = CassandraClient.getClient().execute(bs);
        List<Row> all = rs.all();

        // parse each site and store it in a json obect that will then be toStringed
        // and return a JSON representation of all the parking sites.
        // string builder that will store a json array
        StringBuilder result = new StringBuilder().append("[");
        int commaTracker = 1;
        for (Row r : all) {

            // fetch the json formated query
            result.append(r.getString("[json]"));

            // append a comma for all results except the last one
            if (commaTracker != all.size()) {
                result.append(",");
            }
            commaTracker++;
        }
        // end the json array
        result.append("]");

        // verify that the result was turned into json correctly
        try {
            // turn the string representation of the data into a json array
            // and make a recommendation
            return recommend(new JSONArray(result.toString()));
        } catch (JSONException jex) {
            // failed to fetch data as json, log the exception and return an error.
            jex.printStackTrace();
            return "ERROR";
        }
    }

    private static String recommend(JSONArray json) throws JSONException {

        int mostSpotsAvailable = 0;
        String recommendedSite = "";

        // todo -- this needs to take account user preferences
        //         can be added in later on
        // recommend the most empty parking lot.
        for (int i = 0; i < json.length(); i++) {
            JSONObject jo = json.getJSONObject(i);
            int availableSpots = jo.getInt("available");
            int capacity = jo.getInt("capacity");

            if (availableSpots > mostSpotsAvailable) {
                recommendedSite = jo.getString("site");
                mostSpotsAvailable = availableSpots;
            }
        }

        // replace first character which denotes the start of the array, aka "["
        // and put in the "[" again, the recomendation object and a comma
        // the effect of this is that the recommendation is appended at the start
        String jsonStr = json.toString();
        // format: {"recommended":"ONE","available-spots":100}
        String recommendation = "{\"recommended\":\"" + recommendedSite + "\",\"available-spots\":" + mostSpotsAvailable + "}";
        jsonStr = jsonStr.replaceFirst("\\[", "[" + recommendation + ",");

        return jsonStr;
    }

    /**
     * Reduces or increases the parking spots available for a given location
     * @param location
     * @param site
     * @param increment - boolean stating whether to increment (when true) or decrement (false) the
     *                  number of available spots.
     * @return - boolean representing if the reduction was carried out successfully
     */
    public static boolean modifyAvailable(String location, String site, boolean increment) {

        // get available slots
        int available = getAvailableSlots(location, site);

        if (increment) {
            available = available + 1;
        } else {
            available = available - 1;
        }

        String query = "UPDATE appsisted.parkingsite SET available=? WHERE location=? AND site=?";

        // bind the query
        PreparedStatement ps = CassandraClient.getClient().prepare(query);
        BoundStatement bs = ps.bind((available),location, site);

        // run the query
        CassandraClient.getClient().execute(bs);

        return true;
    }

    private static int getAvailableSlots(String location, String site) {
        // WARNING - this is a read before write and is considered bad in cassandra.
        // this system can handle it as its very synchronous, it shouldn't be possible
        // for two users to enter the parking lot at the same time
        // there is some risk when a user enters as another is leaving.
        // A different safer implementation could be to use cassandra counters
        String queryCount = "SELECT available FROM appsisted.parkingsite WHERE location=? AND site=?;";
        // bind the location and site to the query
        PreparedStatement ps = CassandraClient.getClient().prepare(queryCount);
        BoundStatement bs = ps.bind(location, site);

        // fetch the result and verify that only 1 item was found.
        ResultSet rs = CassandraClient.getClient().execute(bs);
        List<Row> all = rs.all();
        if (all.size() != 1) {
            throw new RuntimeException("Unexpected number of results for code of parking location -- " +
                    "Should be exactly 1 but was " + all.size());
        }

        return all.get(0).getInt("available");
    }

    public static void main(String[] args) {
        modifyAvailable("stirling", "ONE", true);
    }
}
