package com.github.paveldt.appsistedparking.model;

public class ParkingState {

    private static ParkingState instance;

    // default to not being parked
    private int parkingState = 0;

    public final static int NOT_PARKED = 0;
    public final static int PARKING = 10;
    public final static int PARKED = 11;
    public final static int EXITING_PARKING_LOT = 20;

    // private constructor to enforce singleton
    private ParkingState() {}

    public static ParkingState getInstance() {

        if (instance == null) {
            instance = new ParkingState();
        }

        return instance;
    }

    public int getParkingState() {
        return instance.parkingState;
    }

    public void setParkingState(int newState) {
        instance.parkingState = newState;
    }


}
