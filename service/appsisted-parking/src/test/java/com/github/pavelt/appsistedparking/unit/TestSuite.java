package com.github.pavelt.appsistedparking.unit;


import com.github.pavelt.appsistedparking.AppsistedParkingApplication;
import com.github.pavelt.appsistedparking.unit.model.QRCodeTest;
import com.github.pavelt.appsistedparking.unit.model.UserTest;
import com.github.pavelt.appsistedparking.unit.security.PasswordManagerTest;
import com.github.pavelt.appsistedparking.unit.security.SanitizerTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        AppsistedParkingApplicationTests.class,
        PasswordManagerTest.class,
        SanitizerTest.class,
        UserTest.class,
        QRCodeTest.class
})
public class TestSuite {

    // context used to stop service once it's finished being tested.
    private static ConfigurableApplicationContext cap;

    /**
     * Start database and web-service used for testing.
     */
    @BeforeClass
    public static void setup() {
        // start web service
        cap = SpringApplication.run(AppsistedParkingApplication.class, "");
    }

    /**
     * Stop database and web-service
     */
    @AfterClass
    public static void stop() {
        cap.stop();
    }

}
