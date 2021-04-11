package com.github.pavelt.appsistedparking.database;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Schema {

    /**
     * Creates all needed schema for the appsisted-parking application.
     * @return - List of Strings represented tables created.
     */
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
     * creates cassandra keyspaces and tables for the user.
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
                .append(" (username text, password text, salt text, setting_location text, setting_site text, balance float, PRIMARY KEY(username))");

        CassandraClient session = CassandraClient.getClient();
        session.execute(keyspaceQuery.toString());
        session.execute(tableQuery.toString());

        // function complete to this point without error
        // schema created successfully
        return true;
    }

    /**
     * creates cassandra keyspaces and tables for the parking infrastructure.
     * @return boolean - whether the creation succeeded or not
     */
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
                .append(" (location text, site text, capacity int, available int, lat float, lon float, price float, code text, ")
                .append("PRIMARY KEY(location, site))");

        CassandraClient session = CassandraClient.getClient();
        session.execute(keyspaceQuery.toString());
        session.execute(tableQuery.toString());

        // add predetermined parking locations
        populateParkingSite();

        // reaching this far means a successful table creation
        return true;
    }

    /**
     * Populates pre-selected parking locations and their sites.
     * @return
     */
    private void populateParkingSite() {
        String keyspace = "appsisted";
        String table = "parkingsite";

        String code1 = "stirling+Willow Court+14129a27-c38e-4f0b-a40b-220e944062d3";
        String site2 = "INSERT INTO " + keyspace + "." + table +
                " (location, site, capacity, available, lat, lon, price, code) " +
                " VALUES ('stirling', 'Willow Court', 200, 200, 56.149451, -3.922113, 1.00, '" + code1 + "')";


        String code2 = "stirling+Cottrell+" + UUID.randomUUID().toString();
        String site1 = "INSERT INTO " + keyspace + "." + table +
                " (location, site, capacity, available, lat, lon, price, code) " +
                " VALUES ('stirling', 'Cottrell', 130, 130, 56.143046, -3.919445, 1.00, '" + code2 + "')";

        String code3 = "stirling+Pathfoot+" + UUID.randomUUID().toString();
        String site3 = "INSERT INTO " + keyspace + "." + table +
                " (location, site, capacity, available, lat, lon, price, code) " +
                " VALUES ('stirling', 'Pathfoot', 150, 150, 56.148576, -3.928216, 1.00, '" + code3 + "')";

        String code4 = "stirling+South+" + UUID.randomUUID().toString();
        String site4 = "INSERT INTO " + keyspace + "." + table +
                " (location, site, capacity, available, lat, lon, price, code) " +
                " VALUES ('stirling', 'South', 185, 185, 56.142379, -3.922227, 1.00, '" + code4 + "')";

        CassandraClient session = CassandraClient.getClient();
        session.execute(site1);
        session.execute(site2);
        session.execute(site3);
        session.execute(site4);
    }
}
