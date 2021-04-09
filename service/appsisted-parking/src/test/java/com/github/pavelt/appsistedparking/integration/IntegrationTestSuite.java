package com.github.pavelt.appsistedparking.integration;

import com.github.pavelt.appsistedparking.database.CassandraClient;
import com.github.pavelt.appsistedparking.integration.controller.LocationControllerTest;
import com.github.pavelt.appsistedparking.integration.controller.ParkingControllerTest;
import com.github.pavelt.appsistedparking.integration.controller.SchemaControllerTest;
import com.github.pavelt.appsistedparking.integration.controller.UserControllerTest;

import java.util.ArrayList;
import java.util.List;

public class IntegrationTestSuite {

    /**
     * Runs the test suite.
     * @param args
     */
    public static void main(String[] args) {

        List<Boolean> testSuccess = new ArrayList<>(4);

        testSuccess.add(SchemaControllerTest.runTests());
        testSuccess.add(UserControllerTest.runTests());
        testSuccess.add(ParkingControllerTest.runTests());
        testSuccess.add(LocationControllerTest.runTests());

        // terminate the cassandra client thread-pool to end the test
        CassandraClient.getClient().shutDownClient();

        // if anything failed, let the user know the tests failed.
        for (boolean b : testSuccess) {
            if (!b) {
                System.out.println("Tests Failed");
                return;
            }
        }
        System.out.println("All Tests Passed");
    }
}
