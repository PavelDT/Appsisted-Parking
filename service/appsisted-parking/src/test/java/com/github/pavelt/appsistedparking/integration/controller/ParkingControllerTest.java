package com.github.pavelt.appsistedparking.integration.controller;

import com.github.pavelt.appsistedparking.database.CassandraClient;
import okhttp3.*;

import java.io.IOException;
import java.net.URLEncoder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ParkingControllerTest {

    /**
     * Runs all tests for this integration test
     */
    public static boolean runTests() {
        try {
            generateParkingCode();
            displayQRCodeTest();
            parkTest();
            exitParkinglotTest();
            cleanupParkingCode();
        } catch (Exception ex) {
            System.out.println("Parking Controller Test failed");
            return false;
        }
        System.out.println("Parking Controller Test passed!");
        return true;
    }

    /**
     * Used for generating a testable QR code
     */
    private static void generateParkingCode() {
        String cql = "INSERT INTO appsisted.parkingsite" +
                " (location, site, code) " +
                " VALUES ('TEST_X', 'TEST_Y', 'TEST_X+TEST_Y+TEST_CODE')";
        CassandraClient.getClient().execute(cql);
    }

    /**
     * Cleans up the QR code used for testing
     */
    private static void cleanupParkingCode() {
        String cql = "DELETE FROM appsisted.parkingsite WHERE location='TEST_X' AND site='TEST_Y'";
        CassandraClient.getClient().execute(cql);
    }

    /**
     * Tests if the user can park
     * @throws IOException
     */
    private static void parkTest() throws IOException {
        String qrCode = "TEST_X+TEST_Y+TEST_CODE";
        OkHttpClient client = new OkHttpClient();

        RequestBody rb = new FormBody.Builder()
                .add("qrCode", qrCode)
                .add("username", "test")
                .build();

        Request request = new Request.Builder()
                .url("http://localhost:8080/parking/park/")
                .post(rb)
                .build();

        try (Response response = client.newCall(request).execute()) {
            assertEquals("true", response.body().string());
            assertTrue(response.code() == 200);
        }
    }

    /**
     * Tests if user can exit a parking lot
     * @throws IOException
     */
    private static void exitParkinglotTest() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://localhost:8080/parking/exit?location=" + URLEncoder.encode("TEST_X") +
                     "&site=" + URLEncoder.encode("TEST_Y"))
                .build();

        try (Response response = client.newCall(request).execute()) {
            assertEquals("true", response.body().string());
            assertTrue(response.code() == 200);
        }
    }

    /**
     * Tests if the QR code for a parking site can be retrieved
     * Test relies on the fact that if the QRCode cant be retrieved a 404 will be returned as a
     * text based response rather than an image being sent back.
     * @throws Exception
     */
    private static void displayQRCodeTest() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://localhost:8080/parking/qrcode?location=stirling&site=South")
                .build();

        try (Response response = client.newCall(request).execute()) {
            assertEquals("image/png", response.body().contentType().toString());
            assertTrue(response.code() == 200);
        }
    }
}
