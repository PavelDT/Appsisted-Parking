package com.github.paveldt.appsistedparking;

import com.github.paveldt.appsistedparking.model.ParkingLocationTest;
import com.github.paveldt.appsistedparking.model.ParkingSiteTest;
import com.github.paveldt.appsistedparking.model.ParkingState;
import com.github.paveldt.appsistedparking.model.ParkingStateTest;
import com.github.paveldt.appsistedparking.model.UserTest;
import com.github.paveldt.appsistedparking.util.JSONUtilTest;
import com.github.paveldt.appsistedparking.util.WebRequestQueue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import static org.junit.Assert.*;

/**
 * Test suite that runs all unit tests.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        JSONUtilTest.class,
        UserTest.class,
        ParkingStateTest.class,
        ParkingSiteTest.class,
        ParkingLocationTest.class
})
public class TestSuite {

    /**
     * Prepare all resources used throughout the tests
     */
    @BeforeClass
    public static void setup() {
        // initialize the singleton of parking state
        ParkingState.getInstance();
    }

    /**
     * Clean-up and destroy all resources after the tests.
     */
    @AfterClass
    public static void stop() {
    }
}