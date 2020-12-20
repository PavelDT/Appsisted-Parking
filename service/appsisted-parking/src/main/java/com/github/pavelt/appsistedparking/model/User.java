package com.github.pavelt.appsistedparking.model;


import com.datastax.oss.driver.api.core.ConsistencyLevel;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.github.pavelt.appsistedparking.database.CassandraClient;
import com.github.pavelt.appsistedparking.security.PasswordManager;

import java.security.InvalidParameterException;
import java.util.List;

public class User {

    String username;
    String password;
    String salt;

    public User (String username, String password, String salt) {
        this.username = username;
        this.password = password;
        this.salt = salt;
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

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    // Below is the database access layer

    public boolean register() {

        // check if user exists
        //     if not create them

        return true;
    }

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

            return new User(uname, passw, salt);
        }

        // no such user exists, or multiple users with same username
        // were identified (latter is impossible in Cassandra due to
        // schema of the table)
        throw new InvalidParameterException("Unknown user: " + username);
    }

    public static boolean register(String username, String password) {

        // invalid params or user already exists, fail to register
        if (username.equals("") || password.equals("") || userExists(username)) {
            return false;
        }

        CassandraClient client = CassandraClient.getClient();
        String query = "INSERT INTO appsisted.user (username, password, salt) VALUES (?, ?, ?);";

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
        return userExists(username);
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
}
