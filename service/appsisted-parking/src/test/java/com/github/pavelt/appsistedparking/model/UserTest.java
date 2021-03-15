package com.github.pavelt.appsistedparking.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;


import static org.junit.Assert.assertEquals;

/**
 * Tests everything in the user class that doesn't touch the database.
 * Embedding databases in unit tests
 */
@RunWith(SpringRunner.class)
public class UserTest {

    private final User user = new User("jack", "pa$$W0rD", "salt", "stirling", "Cottrell");

    /**
     * Tests all getters validate and encrypt details as expected.
     */
    @Test
    public void testGetters() {
        assertEquals("jack", user.getUsername());
        assertEquals("pa$$W0rD", user.getPassword());
        assertEquals("salt", user.getSalt());
        assertEquals("stirling", user.getSettingLocation());
        assertEquals("Cottrell", user.getSettingSite());
    }
}
