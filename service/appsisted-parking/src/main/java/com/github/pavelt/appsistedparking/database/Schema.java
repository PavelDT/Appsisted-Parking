package com.github.pavelt.appsistedparking.database;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Schema {

    public List<String> createAllSchema() {
        List<String> createdTables = new ArrayList<>();
        if (createUserSchema()) {
            createdTables.add("user");
        }
        if (createParkingSite()) {
            createdTables.add("parkingsite");
        }

        return createdTables;
    }

    /**
     * creates cassandra keyspaces and tables AKA the schema.
     * @return boolean - whether the creation succeeded or not
     */
    private boolean createUserSchema(){
        String keyspace = "appsisted";
        String table = "user";

        // IF NOT EXISTS allows this statement to run multiple times without throwing an exception
        // this is essentially an idempotent query thanks to the 'IF NOT EXISTS'
        StringBuilder keyspaceQuery = new StringBuilder().append("CREATE KEYSPACE IF NOT EXISTS ")
                .append(keyspace)
                .append(" WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 3};");

        StringBuilder tableQuery = new StringBuilder().append("CREATE TABLE IF NOT EXISTS ")
                .append(keyspace)
                .append(".")
                .append(table)
                .append(" (username text, password text, salt text, PRIMARY KEY(username))");

        CassandraClient session = CassandraClient.getClient();
        session.execute(keyspaceQuery.toString());
        session.execute(tableQuery.toString());

        // function complete to this point without error
        // schema created successfully
        return true;
    }

    private boolean createParkingSite() {
        String keyspace = "appsisted";
        String table = "parkingsite";

        // IF NOT EXISTS allows this statement to run multiple times without throwing an exception
        // this is essentially an idempotent query thanks to the 'IF NOT EXISTS'
        StringBuilder keyspaceQuery = new StringBuilder().append("CREATE KEYSPACE IF NOT EXISTS ")
                .append(keyspace)
                .append(" WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 3};");

        StringBuilder tableQuery = new StringBuilder().append("CREATE TABLE IF NOT EXISTS ")
                .append(keyspace)
                .append(".")
                .append(table)
                .append(" (location text, site text, capacity int, available int, lat float, lon float, code text, ")
                .append("PRIMARY KEY(location, site))");

        CassandraClient session = CassandraClient.getClient();
        session.execute(keyspaceQuery.toString());
        session.execute(tableQuery.toString());

        // reaching this far means a successful table creation
        return true;
    }

    private boolean populateParkingSite() {
        String keyspace = "appsisted";
        String table = "parkingsite";

        String site1 = "INSERT INTO " + keyspace + "." + table +
                " (location, site, capacity, available, lat, lon, code) " +
                " VALUES ('stirling', 'ONE', 100, 100, 0.0, 0.0, '" + UUID.randomUUID().toString() + "')";

        String site2 = "INSERT INTO " + keyspace + "." + table +
                " (location, site, capacity, available, lat, lon, code) " +
                " VALUES ('stirling', 'TWO', 50, 50, 0.0, 0.0, '" + UUID.randomUUID().toString() + "')";

        String site3 = "INSERT INTO " + keyspace + "." + table +
                " (location, site, capacity, available, lat, lon, code) " +
                " VALUES ('stirling', 'THREE', 30, 30, 0.0, 0.0, '" + UUID.randomUUID().toString() + "')";

        CassandraClient session = CassandraClient.getClient();
        session.execute(site1);
        session.execute(site2);
        session.execute(site3);

        return true;
    }
}
