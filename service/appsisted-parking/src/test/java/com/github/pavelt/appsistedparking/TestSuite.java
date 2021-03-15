package com.github.pavelt.appsistedparking;


import com.github.pavelt.appsistedparking.model.QRCodeTest;
import com.github.pavelt.appsistedparking.model.UserTest;
import com.github.pavelt.appsistedparking.security.PasswordManagerTest;
import com.github.pavelt.appsistedparking.security.SanitizerTest;
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
