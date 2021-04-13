package com.github.pavelt.appsistedparking.simulation;

import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.github.pavelt.appsistedparking.database.CassandraClient;
import com.github.pavelt.appsistedparking.database.Schema;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;

import static org.junit.Assert.assertTrue;

public class CapacitySimulation {

    private static OkHttpClient client = new OkHttpClient();

    public static void main(String[] args) throws Exception {
        // make all parking spots available
        setUpParkingLocation();

        // set up users
        registerUser("cottrell", "Cottrell");
        registerUser("south", "South");
        registerUser("willow_court", "Willow Court");
        registerUser("pathfoot", "Pathfoot");
        registerUser("none", "none");

        String dataSet = "";
        // start filling up the parking spot
        // do 200 requests of users that don't have preferences
        for (int i = 0; i < 200; i++) {
            fillParkingLot("none", "none");
            dataSet += collectMetrics() + "\n";
        }

        // verify users get expected parking locations
        fillParkingLot("Cottrell", "cottrell");
        dataSet += collectMetrics() + "\n";
        fillParkingLot("South", "south");
        dataSet += collectMetrics() + "\n";
        fillParkingLot("Willow Court", "willow_court");
        dataSet += collectMetrics() + "\n";
        fillParkingLot("Pathfoot", "pathfoot");
        dataSet += collectMetrics() + "\n";

        // fill remainder of parking sites
        for (int i = 0; i < 461; i++) {
            fillParkingLot("none", "none");
            dataSet += collectMetrics() + "\n";
        }

        // terminate cassandra's client threads so the simulation can stop running
        CassandraClient.getClient().shutDownClient();

        System.out.println(dataSet);
    }

    private static void fillParkingLot(String site, String username) throws Exception {
        // get parking recommendation
        String recommendedSite = getParkingRecommendation(site);

        // fetch the current QR code for the site
        String cqlQR = "SELECT code FROM appsisted.parkingsite WHERE location='stirling' AND site='" + recommendedSite + "';";
        Row r = CassandraClient.getClient().execute(cqlQR).one();

        // simulate parking by a user
        String qrCode = r.getString("code");
        OkHttpClient client = new OkHttpClient();
        RequestBody rb = new FormBody.Builder()
                .add("qrCode", qrCode)
                .add("username", username)
                .build();
        // build the request
        Request request = new Request.Builder()
                .url("http://localhost:8080/parking/park/")
                .post(rb)
                .build();

        try (Response response = client.newCall(request).execute()) {
            assertTrue(response.code() == 200);
        }
    }

    private static String getParkingRecommendation(String site) throws Exception {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://localhost:8080/location/status?location=stirling&site=" + site +
                        "&username=" + URLEncoder.encode("none"))
                .build();

        // ensure that a recommendation was made
        try (Response response = client.newCall(request).execute()) {
            assertTrue(response.code() == 200);
            String resp = response.body().string();
            JSONArray data = new JSONArray(resp);
            JSONObject recommendation = data.getJSONObject(0);
            return recommendation.getString("recommended");
        }
    }

    private static void setUpParkingLocation() throws Exception {
        // drop the table that stores details about the parking location
        String cql = "DROP TABLE IF EXISTS appsisted.parkingsite";
        CassandraClient.getClient().execute(cql);

        // create all schema that would have all parking sites completely empty
        // and will not destroy currently registered users.
        Schema schema = new Schema();
        schema.createAllSchema();
        // ensure cassandra creates schema
        Thread.sleep(3000);
    }

    private static void registerUser(String username, String site) throws IOException {
        // creates a web request to the endpoint
        Request request = new Request.Builder()
                .url("http://localhost:8080//user/register?username=" + URLEncoder.encode(username) +
                        "&password=" + URLEncoder.encode("password"))
                .build();

        try (Response response = client.newCall(request).execute()) {
            assertTrue(response.code() == 200);
        }

        // set the location preference for the user.
        // put request requires a request body for its parameters
        RequestBody rb = new FormBody.Builder()
                .add("username", username)
                .add("location", "stirling")
                .add("site", site)
                .build();

        Request request2 = new Request.Builder()
                .put(rb)
                .url("http://localhost:8080/user/settings/update")
                .build();

        try (Response response = client.newCall(request2).execute()) {
            assertTrue(response.code() == 200);
        }
    }
    
    private static String collectMetrics() {
        String cql = "SELECT site, available FROM appsisted.parkingsite";
        ResultSet rs = CassandraClient.getClient().execute(cql);
        String data = "";
        for (Row r : rs) {
            String site = r.getString("site");
            int available = r.getInt("available");

            data += site + " " + available + ",";
        }
        return data;
    }
}
