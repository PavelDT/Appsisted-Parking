package com.github.pavelt.appsistedparking.integration.controller;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SchemaControllerTest {

    /**
     * Runs all tests for this integration test
     */
    public static boolean runTests() {
        try {
            testCreateAll();
        } catch (Exception ex) {
            System.out.println("Schema Controller Test failed");
            return false;
        }
        System.out.println("Schema Controller Test passed!");
        return true;
    }

    /**
     * Tests creating all the needed schema in Cassandra
     * @throws IOException
     */
    private static void testCreateAll() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://localhost:8080/schema/createall")
                .build();

        try (Response response = client.newCall(request).execute()) {
            assertEquals("Created tables: [user, parkingsite]", response.body().string());
            assertTrue(response.code() == 200);
        }
    }
}
