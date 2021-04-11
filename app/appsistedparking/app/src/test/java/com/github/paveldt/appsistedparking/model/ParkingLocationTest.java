package com.github.paveldt.appsistedparking.model;

import org.json.JSONException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ParkingLocationTest {

    /**
     * Tests if malformed json can crash the parsing of a parking location
     * note: This method will print a stack trace showing a JSONException.
     */
    @Test
    public void testBadJsonDoesntCrash() {
        System.out.println("The below JSONException is expected.");
        ParkingLocation pl = new ParkingLocation("");

        assertEquals("null 0 []", pl.toString());
    }

    /**
     * Tests parsing of json into a parking location.
     */
    @Test
    public void testParingLocation() {
        // a purposefully complex json string
        String json = "[{\"recommended\":\"South\",\"available-spots\":185}," +
                "{\"site\":\"Cottrell\",\"price\":1,\"available\":130,\"location\":\"stirling\",\"lon\":-3.919445,\"lat\":56.143047,\"capacity\":130}," +
                "{\"site\":\"Pathfoot\",\"price\":1,\"available\":150,\"location\":\"stirling\",\"lon\":-3.928216,\"lat\":56.148575,\"capacity\":150}," +
                "{\"site\":\"South\",\"price\":1,\"available\":185,\"location\":\"stirling\",\"lon\":-3.922227,\"lat\":56.14238,\"capacity\":185}," +
                "{\"site\":\"Willow Court\",\"price\":1,\"available\":3,\"location\":\"stirling\",\"lon\":-3.922113,\"lat\":56.149452,\"capacity\":200}]";

        ParkingLocation pl = new ParkingLocation(json);

        String expected = "South 185 " +
                "[stirling Cottrell 130 130 -3.919445 56.143047 1.0, " +
                "stirling Pathfoot 150 150 -3.928216 56.148575 1.0, " +
                "stirling South 185 185 -3.922227 56.14238 1.0, " +
                "stirling Willow Court 3 200 -3.922113 56.149452 1.0]";

        assertEquals(expected, pl.toString());
    }

    /**
     * Tests recommendation from parsed json.
     */
    @Test
    public void testRecommendation() {
        // a purposefully complex json string
        String json = "[{\"recommended\":\"Cottrell\",\"available-spots\":130}," +
                "{\"site\":\"Cottrell\",\"price\":1,\"available\":130,\"location\":\"stirling\",\"lon\":-3.919445,\"lat\":56.143047,\"capacity\":130}," +
                "{\"site\":\"Pathfoot\",\"price\":1,\"available\":150,\"location\":\"stirling\",\"lon\":-3.928216,\"lat\":56.148575,\"capacity\":150}," +
                "{\"site\":\"South\",\"price\":1,\"available\":185,\"location\":\"stirling\",\"lon\":-3.922227,\"lat\":56.14238,\"capacity\":185}," +
                "{\"site\":\"Willow Court\",\"price\":1,\"available\":200,\"location\":\"stirling\",\"lon\":-3.922113,\"lat\":56.149452,\"capacity\":200}]";

        ParkingLocation pl = new ParkingLocation(json);

        // check the recommendation and available spots
        assertEquals("Cottrell", pl.getRecommendedLocation());
        assertEquals(130, pl.getAvailableSpots());
        // verify the parsed json contains the expected parking locations in the correct order
        assertEquals("stirling Cottrell 130 130 -3.919445 56.143047 1.0", pl.getParkingSites().get(0).toString());
    }
}
