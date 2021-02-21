package com.github.pavelt.appsistedparking.model;

import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.github.pavelt.appsistedparking.database.CassandraClient;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class ParkingSite {

    public static String getLocationInfo(String location) {
        // Using "json" infront of the column specification allows cassandra to
        // return the result as a json formatted object.
        String query = "SELECT json * FROM appsisted.parkingsite WHERE location=?";

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
            JSONArray test = new JSONArray(result.toString());
            return result.toString();
        } catch (JSONException jex) {
            // failed to fetch data as json, log the exception and return an error.
            jex.printStackTrace();
            return "ERROR";
        }
    }
}
