package com.github.paveldt.appsistedparking.model;

import com.github.paveldt.appsistedparking.util.WebRequestManager;

public class User {
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Checks if a username is already taken
     * @param username - string representation of username to check
     * @return boolean - representing username availability, false = unavailable
     */
    public boolean usernameAvailable(String username) {


        return true;
    }

    public String register() {
        WebRequestManager client = new WebRequestManager();
        // if the client returns "error", return false.
        Thread thread = new Thread(client);
        try {
            thread.start();
            thread.join();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        return client.getResult();
    }

    public void login() {

    }

    @Override
    public String toString() {
        return username + " " + password;
    }
}
