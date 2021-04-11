package com.github.paveldt.appsistedparking.model;

/**
 * Used for when the user logs in - holds user's details.
 */
public class User {

    private String username;
    // default is 'none'
    private String preferredLocation = "none";
    private String preferredSite = "none";
    private float balance = 0f;

    /**
     * Constructor for when the user hasn't set any settings yet.
     * @param username
     */
    public User(String username) {
        this.username = username;
    }

    /**
     * Constructoer for when the user has a preferred parking location
     * @param username - user's username
     * @param preferredLocation - preferred parking location
     * @param preferredSite - preferred parking site
     */
    public User(String username, String preferredLocation, String preferredSite, float balance) {
        this.username = username;
        this.preferredLocation = preferredLocation;
        this.preferredSite = preferredSite;
        this.balance = balance;
    }


    /**
     * Getter for username
     * @return - user's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Getter for preferred location
     * @return - String representing preferred location
     */
    public String getPreferredLocation() {
        return preferredLocation;
    }

    /**
     * Getter for preferred site
     * @return - String representing preferred site
     */
    public String getPreferredSite() {
        return preferredSite;
    }

    /**
     * Gets balance of user
     * @return - balance of user
     */
    public float getBalance() {
        return balance;
    }

    /**
     * Update user's balance
     * @param balance - new balance to update to
     */
    public void setBalance(Float balance) {
        this.balance = balance;
    }

    /**
     * Updates in-memory the location and site after a successful DB update.
     * @param preferredLocation - the new preffered location
     * @param preferredSite - the new preffered site
     */
    public void updataSettings(String preferredLocation, String preferredSite) {
        this.preferredLocation = preferredLocation;
        this.preferredSite = preferredSite;
    }
}
