package com.github.paveldt.appsistedparking.model;

public class ParkingState {

    // default to not being parked
    private int parkingState = 0;

    public final static int NOT_PARKED = 0;
    public final static int PARKED = 1;
    public final static int EXITING_PARKING_LOT = 2;

    public int getParkingState() {
        return parkingState;
    }

    public void setParkingState(int newState) {
        parkingState = newState;
    }
}
