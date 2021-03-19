package com.github.paveldt.appsistedparking.model;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.assertEquals;

// required to ensure default-state runs before state-change
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ParkingStateTest {

    /**
     * Tests the default state is as expected
     */
    @Test
    public void testADefaultState() {
        // should be not parked by default
        assertEquals(ParkingState.NOT_PARKED, ParkingState.getInstance().getParkingState());
    }

    /**
     * Tests that state can be updated correctly in the singleton
     */
    @Test
    public void testBStateChange() {
        // update the state
        ParkingState.getInstance().setParkingState(ParkingState.PARKING);
        // validate
        assertEquals(ParkingState.PARKING, ParkingState.getInstance().getParkingState());
    }
}
