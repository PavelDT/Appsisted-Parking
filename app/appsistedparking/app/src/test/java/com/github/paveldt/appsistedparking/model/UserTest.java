package com.github.paveldt.appsistedparking.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserTest {

    /**
     * Tests creating a user without any preferences
     */
    @Test
    public void testUserNoSettings() {
        String username = "john";
        User u = new User(username);

        assertEquals(u.getUsername(), username);
    }

    /**
     * Tests creating a user with preferences
     */
    @Test
    public void testUserWithSettings() {
        String username = "jane";
        User u = new User(username, "location", "site");

        assertEquals(u.getUsername(), username);
        assertEquals(u.getPreferredLocation(), "location");
        assertEquals(u.getPreferredSite(), "site");
    }

    /**
     * Test updating a user's settings
     */
    @Test
    public void testUpdatingSettings() {
        // create user without any prefs
        String username = "john";
        User u = new User(username);

        assertEquals(u.getPreferredLocation(), "none");
        assertEquals(u.getPreferredSite(), "none");

        // now give them preferences
        u.updataSettings("stirling", "Cottrell");

        assertEquals(u.getPreferredLocation(), "stirling");
        assertEquals(u.getPreferredSite(), "Cottrell");
    }
}
