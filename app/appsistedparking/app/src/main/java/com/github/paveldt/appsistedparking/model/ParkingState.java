package com.github.paveldt.appsistedparking.model;

public class ParkingState {

    // default to not being parked
    private int parkingState = 0;

    public final static int NOT_PARKED = 0;
    public final static int PARKING = 10;
    public final static int PARKED = 11;
    public final static int EXITING_PARKING_LOT = 20;

    public int getParkingState() {
        return parkingState;
    }

    public void setParkingState(int newState) {
        parkingState = newState;
    }
}
