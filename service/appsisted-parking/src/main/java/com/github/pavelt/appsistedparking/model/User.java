package com.github.pavelt.appsistedparking.model;


import com.datastax.oss.driver.api.core.ConsistencyLevel;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.github.pavelt.appsistedparking.database.CassandraClient;
import com.github.pavelt.appsistedparking.security.PasswordManager;

import java.security.InvalidParameterException;
import java.util.List;

public class User {

    private String username;
    private String password;
    private String salt;
    private String settingLocation;
    private String settingSite;

    public User (String username, String password, String salt, String settingLocation, String settingSite) {
        this.username = username;
        this.password = password;
        this.salt = salt;
        this.settingLocation = settingLocation;
        this.settingSite = settingSite;
    }

    /**
     * Getter for user password
     * @return - pw hash
     */
    public String getPassword() {
        return password;
    }

    /**
     * Getter for user salt
     * @return - salt hash
     */
    public String getSalt() {
        return salt;
    }

    /**
     * Gets username of user
     * @return - String username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets location setting of user
     * @return - String representation of location
     */
    public String getSettingLocation() {
        return settingLocation;
    }

    /**
     * Gets user site setting
     * @return - String user site setting
     */
    public String getSettingSite() {
        return settingSite;
    }

    // Below is the database access layer

    public static User getUser(String username) {
        String query = "SELECT * FROM appsisted.user WHERE username=?";

        PreparedStatement ps = CassandraClient.getClient().prepare(query);
        BoundStatement bs = ps.bind(username);

        // it's worth noting that due to Cassandra's overwrite
        // nature of insertion, it's impossible that the same
        // username comes up twice.
        //
        // Thus the assumption that there is only one row is safe
        // to make, but in order to keep the code viable for a different
        // database, the check here also handles multiple rows.
        ResultSet rs = CassandraClient.getClient().execute(bs);
        List<Row> all = rs.all();

        if (all.size() == 1) {
            String uname = all.get(0).getString("username");
            String passw = all.get(0).getString("password");
            String salt = all.get(0).getString("salt");
            String location = all.get(0).getString("setting_location");
            String site = all.get(0).getString("setting_site");

            return new User(uname, passw, salt, location, site);
        }

        // no such user exists, or multiple users with same username
        // were identified (latter is impossible in Cassandra due to
        // schema of the table)
        throw new InvalidParameterException("Unknown user: " + username);
    }

    /**
     * Registeres the user
     * @param username - username chosen
     * @param password - user's pasword
     * @return - String representing status of registration
     */
    public static String register(String username, String password) {

        // invalid params or user already exists, fail to register
        if (username.equals("") || password.equals("")) {
            return "Username / Password cannot be empty";
        }
        if (userExists(username)) {
            return "Username already taken";
        }

        CassandraClient client = CassandraClient.getClient();
        String query = "INSERT INTO appsisted.user (username, password, salt, setting_location, setting_site) " +
                       "VALUES (?, ?, ?, 'none', 'none');";

        String salt = PasswordManager.getInstance().generateSalt();
        String hash = PasswordManager.getInstance().hashPassword(salt, password);

        PreparedStatement ps = client.prepare(query);
        BoundStatement bs = ps.bind(username, hash, salt);
        // set the Cassandra consistency level to the safest possible
        // this ensures that the registration is safe and wont compete
        // with any other individual trying to register the same username
        // serial ensures the highest level of consistency but Quarum
        // would be a safe standard as it is the default
        bs.setConsistencyLevel(ConsistencyLevel.SERIAL);
        // register the user
        client.execute(bs);

        // This is dangerous in Cassandra due to the eventual consistency.
        // for that reason a high consistency level was used when registering
        Boolean status = userExists(username);

        return status.toString();
    }

    /**
     * Checks if a user exists
     * @param username
     * @return boolean - whether user exists or not
     */
    public static boolean userExists(String username) {
        // query is limited to only one item as a result
        String query = "SELECT * FROM appsisted.user WHERE username=? LIMIT 1";
        PreparedStatement ps = CassandraClient.getClient().prepare(query);
        BoundStatement bs = ps.bind(username);

        // fetches only one row from the result set
        Row result = CassandraClient.getClient().execute(bs).one();
        // check if the row is null
        if (result != null) {
            // user exists
            return true;
        }

        return false;
    }

    /**
     * Updates user's prefered parking location and site
     * @param username - User's username to find them
     * @param location - preferred location
     * @param site - preferred site
     * @return - boolean representing success of the update
     */
    public static boolean updateSettings(String username, String location, String site) {
        String query = "UPDATE appsisted.user SET setting_location=?, setting_site=? WHERE username=?";
        PreparedStatement ps = CassandraClient.getClient().prepare(query);
        BoundStatement bs = ps.bind(location, site, username);

        // execute the update
        CassandraClient.getClient().execute(bs).one();

        // no exception, update succeeded
        return true;
    }
}
