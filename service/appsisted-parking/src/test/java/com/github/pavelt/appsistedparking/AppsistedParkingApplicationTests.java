package com.github.pavelt.appsistedparking;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.ServerSocket;

@RunWith(SpringRunner.class)
public class AppsistedParkingApplicationTests {

	@Before
	@Test
	public void contextLoads() {
	}

	@Test
	public void testServiceUp() throws IOException {
		try (ServerSocket ignored = new ServerSocket(8080)) {
			// success, test can pass.
			throw new RuntimeException("Port not in use - service isn't running.");
		} catch (IOException e) {
			// all good, port is in use, service should be running.
		}
	}

}
