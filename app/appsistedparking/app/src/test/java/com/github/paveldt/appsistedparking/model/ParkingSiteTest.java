package com.github.paveldt.appsistedparking.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class ParkingSiteTest {

    /**
     * Tests default values when initializing a parking site.
     */
    @Test
    public void testExpectedDefaults() {
        ParkingSite ps = new ParkingSite("stirling", "Willow Court", 10, 10, 0.0f, 0.0f);

        assertEquals(ps.getAvailable(), 10);
        assertSame(ps.getSite(), "Willow Court");
    }
}
