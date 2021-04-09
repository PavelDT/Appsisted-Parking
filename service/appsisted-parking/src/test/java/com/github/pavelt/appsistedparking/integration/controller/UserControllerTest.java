package com.github.pavelt.appsistedparking.integration.controller;

import com.github.pavelt.appsistedparking.database.CassandraClient;
import okhttp3.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.UUID;

import static org.junit.Assert.*;

public class UserControllerTest {

    // create a user that will be used throughout this scenario
    private static final String username = UUID.randomUUID().toString();
    private static final String password = UUID.randomUUID().toString();
    private static OkHttpClient client = new OkHttpClient();

    /**
     * Runs all tests for this integration test
     */
    public static boolean runTests() {
        try {
            registerUserTest();
            loginUserTest();
            logoutUserTest();
            userExistsTest();
            updateSettingsTest();
            // remove created user
            cleanupUser();
        } catch (Exception ex) {
            System.out.println("User Controller Test failed");
            return false;
        }
        System.out.println("User Controller Test passed!");
        return true;
    }

    /**
     * Test regustering a user
     * @throws IOException
     */
    private static void registerUserTest() throws IOException {

        // creates a web request to the endpoint
        Request request = new Request.Builder()
                .url("http://localhost:8080//user/register?username=" + URLEncoder.encode(username) +
                     "&password=" + URLEncoder.encode(password))
                .build();

        try (Response response = client.newCall(request).execute()) {
            assertEquals("true", response.body().string());
            assertTrue(response.code() == 200);
        }
    }

    /**
     * Test if the user can log in
     * @throws IOException
     */
    private static void loginUserTest() throws IOException {

        // creates a web request to the endpoint
        Request request = new Request.Builder()
                .url("http://localhost:8080//user/login?username=" + URLEncoder.encode(username) +
                        "&password=" + URLEncoder.encode(password))
                .build();

        try (Response response = client.newCall(request).execute()) {
            // check the response contains the username
            // that would mean the user was found and their password matched the username
            String resp = response.body().string();
            // username shouldn't be empty
            assertFalse(resp.contains("\"username\":\"\""));
            assertTrue(resp.contains("\"username\":\"" + username + "\""));
            assertTrue(response.code() == 200);
        }
    }

    /**
     * Test if the user can be logged out
     * @throws IOException
     */
    private static void logoutUserTest() throws IOException {

        // creates a web request to the endpoint
        Request request = new Request.Builder()
                .url("http://localhost:8080//user/logout?username=" + URLEncoder.encode(username))
                .build();

        try (Response response = client.newCall(request).execute()) {
            // check the user can be logged out
            assertEquals("logout", response.body().string());
            assertTrue(response.code() == 200);
        }
    }

    /**
     * Test if the user that was created exists and is retrievable
     * @throws IOException
     */
    private static void userExistsTest() throws IOException {

        // creates a web request to the endpoint
        Request request = new Request.Builder()
                .url("http://localhost:8080//user/exists?username=" + URLEncoder.encode(username))
                .build();

        try (Response response = client.newCall(request).execute()) {
            // check the user can be logged out
            assertEquals("true", response.body().string());
            assertTrue(response.code() == 200);
        }
    }

    /**
     * Test updating user settings
     * @throws IOException
     */
    private static void updateSettingsTest() throws IOException {

        // put request requires a request body for its parameters
        RequestBody rb = new FormBody.Builder()
                .add("username", username)
                .add("location", "stirling")
                .add("site", "Cotrell")
                .build();

        Request request = new Request.Builder()
                .put(rb)
                .url("http://localhost:8080/user/settings/update")
                .build();

        try (Response response = client.newCall(request).execute()) {
            // check the user can be logged out
            assertEquals("true", response.body().string());
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
