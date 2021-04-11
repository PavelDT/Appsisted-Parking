package com.github.pavelt.appsistedparking.model;

import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.github.pavelt.appsistedparking.database.CassandraClient;
import org.apache.lucene.util.SloppyMath;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class ParkingSite {

    /**
     * Builds information about sites in a location.
     * @param location - user's preferred parking location
     * @param site - user's preferred site location
     * @param username - user's username
     * @return JSON in string format representing information about the location
     */
    public static String getLocationInfo(String location, String site, String username) {
        // Using "json" infront of the column specification allows cassandra to
        // return the result as a json formatted object.
        String query = "SELECT json location, site, available, capacity, lat, lon, price " +
                       "FROM appsisted.parkingsite WHERE location=?";
        PreparedStatement ps = CassandraClient.getClient().prepare(query);
        BoundStatement bs;
        // safeguard against users that haven't configured preferences
        // default to stirling as the recommendation site.
        if (location.equals("none")) {
            bs = ps.bind("stirling");
        } else {
            bs = ps.bind(location.toLowerCase());
        }

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
            return recommend(new JSONArray(result.toString()), location, site, username);
        } catch (JSONException jex) {
            // failed to fetch data as json, log the exception and return an error.
            jex.printStackTrace();
            return "ERROR";
        }
    }

    /**
     * High level overview: selects preferred parking location if there is more than 10 spots available, otherwise picks
     * nearest parking site with more than 10 spots available. If no parking site has more than 10 spots available, the
     * preffered site is recommended.
     * @param json - json of locations
     * @param location - preferred location
     * @param site - preferred site
     * @param username - username of the user
     * @return - JSON formatted as string representing recommended parking location.
     * @throws JSONException
     */
    private static String recommend(JSONArray json, String location, String site, String username) throws JSONException {

        // fetch user to check parking settings
        User user = User.getUser(username);
        // track most available spot and recommended site
        int mostSpotsAvailable = 0;
        String recommendedSite = "";

        // if the user has configured preferences
        if (!user.getSettingLocation().equals("none") && !user.getSettingSite().equals("none")) {
            // if specific lot is too full use nearest lot
            Deque<JSONObject> dq = new LinkedList<>();

            // pass in user's preferred location and site to the coordinate fetcher
            double[] coordinates = getSiteCoordinates(location, site);
            // simplify the results
            double latSetting = coordinates[0];
            double lonSetting = coordinates[1];

            Double bestDistance = null;

            // no specific user preference
            // recommend the most empty parking lot.
            for (int i = 0; i < json.length(); i++) {
                JSONObject jo = json.getJSONObject(i);

                // basic recommendation
                // this is a fallback in case there are 0 spots available
                int availableSpots = jo.getInt("available");
                if (availableSpots > mostSpotsAvailable) {
                    recommendedSite = jo.getString("site");
                    mostSpotsAvailable = availableSpots;
                }

                // skip this site, not enough spots
                if (jo.getInt("available") < 10) {
                    continue;
                }

                // calculate distance between preferred location and current location.
                // pass in lat1, lon1, lat2, lon2
                double distance = SloppyMath.haversinMeters(latSetting, lonSetting, jo.getDouble("lat"), jo.getDouble("lon"));

                // closer, add to front of queue
                // also check if there are more than 10 available spots.
                if ((bestDistance == null || bestDistance > distance) ) {
                    bestDistance = distance;
                    dq.addFirst(jo);
                } else {
                    dq.addLast(jo);
                }
            }

            // recommend the first available location as long as there is any available
            if (dq.size() > 0) {
                recommendedSite = dq.getFirst().getString("site");
                mostSpotsAvailable = dq.getFirst().getInt("available");
            }
        } else {
            // no specific user preference
            // recommend the most empty parking lot.
            for (int i = 0; i < json.length(); i++) {
                JSONObject jo = json.getJSONObject(i);
                int availableSpots = jo.getInt("available");

                if (availableSpots > mostSpotsAvailable) {
                    recommendedSite = jo.getString("site");
                    mostSpotsAvailable = availableSpots;
                }
            }
        }

        // replace first character which denotes the start of the array, aka "["
        // and put in the "[" again, the recommendation object and a comma
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

    /**
     * Retrieves all locations
     * @return - List of strings representing known locations
     */
    public static List<String> getLocations() {
        // a set is used to ensure that repeating locations are only used once
        // locations can repeat as they can have multiple sites
        Set<String> locations = new HashSet<>();

        // fetch all locations
        String query = "SELECT location FROM appsisted.parkingsite;";

        PreparedStatement ps = CassandraClient.getClient().prepare(query);
        BoundStatement bs = ps.bind();

        // execute the query
        ResultSet rs = CassandraClient.getClient().execute(bs);
        List<Row> all = rs.all();

        // append each location to the list
        for (Row r : all) {
            locations.add(r.getString("location"));
        }

        return new ArrayList<>(locations);
    }

    /**
     * Retrieves all sites for a given location
     * @param location - The specified location
     * @return - Returns list of strings representing all sites of a location.
     */
    public static List<String> getSites(String location) {
        List<String> sites = new ArrayList<>();

        // fetch all sites
        String query = "SELECT site FROM appsisted.parkingsite WHERE location=?;";

        PreparedStatement ps = CassandraClient.getClient().prepare(query);
        BoundStatement bs = ps.bind(location);

        // execute the query
        ResultSet rs = CassandraClient.getClient().execute(bs);
        List<Row> all = rs.all();

        // for every entry, add it to the map based on location as the key
        for (Row r : all) {
            // key is the location, value is the site
            sites.add(r.getString("site"));
        }

        return sites;
    }

    /**
     * Retrieves latitude and longitude of a parking site
     * @param location - specified location
     * @param site - specified site
     * @return - array of fixed size 2, index 0 = latitude, index 1 = longitude.
     */
    public static double[] getSiteCoordinates(String location, String site) {

        // fetch all sites
        String query = "SELECT lat, lon FROM appsisted.parkingsite WHERE location=? and site=?;";

        PreparedStatement ps = CassandraClient.getClient().prepare(query);
        BoundStatement bs = ps.bind(location, site);

        // execute the query
        ResultSet rs = CassandraClient.getClient().execute(bs);
        Row row = rs.one();

        return new double[]{row.getFloat("lat"), row.getFloat("lon")};
    }

    /**
     * Retrieves available spots for a specific location's site.
     * @param location - the picked location
     * @param site - the picked site
     * @return - integer representing available slots
     */
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
}
