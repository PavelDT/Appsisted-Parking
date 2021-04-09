package com.github.pavelt.appsistedparking.integration.controller;

import com.github.pavelt.appsistedparking.database.CassandraClient;
import com.github.pavelt.appsistedparking.model.User;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LocationControllerTest {

    // user details used during test
    private static final String username = UUID.randomUUID().toString();
    private static final String password = UUID.randomUUID().toString();

    /**
     * Runs all tests for this integration test
     */
    public static boolean runTests() {
        try {
            getStatusTest();
            //getLocationsTest();
            //getSitesTest();
            cleanupUser();
        } catch (Exception ex) {
            System.out.println("Location Controller Test failed");
            return false;
        }
        System.out.println("Location Controller Test passed!");
        return true;

    }

    /**
     * Test getting status of a location
     * @throws IOException
     */
    private static void getStatusTest() throws IOException {

        // create a temporary user
        User.register(username, password);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://localhost:8080/location/status?location=stirling&site=South" +
                     "&username=" + URLEncoder.encode(username))
                .build();

        // ensure that a recommendation was made
        try (Response response = client.newCall(request).execute()) {
            String resp = response.body().string();
            assertTrue(resp.contains("recommended"));
            assertTrue(resp.contains("available-spots"));
            assertTrue(response.code() == 200);
        }
    }

    /**
     * Test getting location settings
     * @throws IOException
     */
    private static void getLocationsTest() throws IOException {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://localhost:8080/location/")
                .build();

        // ensure that a recommendation was made
        try (Response response = client.newCall(request).execute()) {
            assertEquals("[\"stirling\"]", response.body().string());
            assertTrue(response.code() == 200);
        }
    }

    /**
     * Test getting all sites
     * @throws IOException
     */
    private static void getSitesTest() throws IOException {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://localhost:8080/location/site?location=stirling")
                .build();

        // ensure that a recommendation was made
        try (Response response = client.newCall(request).execute()) {
            String expected = "[\"Cottrell\",\"Pathfoot\",\"South\",\"Willow Court\"]";
            assertEquals(expected, response.body().string());
            assertTrue(response.code() == 200);
        }
    }

    /**
     * utility function to clean up the created user.
     */
    private static void cleanupUser() {
        String query = "DELETE FROM appsisted.user WHERE username='" + username + "'";
        CassandraClient.getClient().execute(query);
    }
}
