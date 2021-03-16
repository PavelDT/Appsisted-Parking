package com.github.pavelt.appsistedparking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AppsistedParkingApplication {

	/**
	 * Main application entry point. Starts the web service and leaves it running until its manually terminated.
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(AppsistedParkingApplication.class, args);
	}

}
