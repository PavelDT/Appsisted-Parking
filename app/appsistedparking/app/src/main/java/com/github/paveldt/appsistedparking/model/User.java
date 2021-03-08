package com.github.paveldt.appsistedparking.model;

/**
 * Used for when the user logs in - holds user's details.
 */
public class User {

    private String username;
    // default is 'none'
    private String preferredLocation = "none";
    private String preferredSite = "none";

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
    public User(String username, String preferredLocation, String preferredSite) {
        this.username = username;
        this.preferredLocation = preferredLocation;
        this.preferredSite = preferredSite;
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
}
