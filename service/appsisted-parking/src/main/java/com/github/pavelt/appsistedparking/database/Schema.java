package com.github.pavelt.appsistedparking.database;

import java.util.ArrayList;
import java.util.List;

public class Schema {

    public List<String> createAllSchema() {
        List<String> createdTables = new ArrayList<>();
        if (createUserSchema()) {
            createdTables.add("user");
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
}
