package com.github.pavelt.appsistedparking.database;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.metadata.Node;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.UUID;

public class CassandraClient {

    private static CassandraClient instance;
    private CqlSession session;
    private final String HOST_IP = "127.0.0.1";
    private final int HOST_PORT = 9042;
    private final String HOST_DC = "datacenter1";

    // Singleton will be used for the Cassandra client
    private CassandraClient() {
         session = CqlSession.builder()
                             .addContactPoint(new InetSocketAddress(HOST_IP, HOST_PORT))
                             .withLocalDatacenter(HOST_DC)
                             .build();
    }

    public static CassandraClient getClient() {
        if (instance == null) {
            instance = new CassandraClient();
        }

        return instance;
    }

    /**
     * Prepares a query into a prepared statement
     * @param query - CQL string query
     * @return prepared statement
     */
    public PreparedStatement prepare(String query) {
        return session.prepare(query);
    }

    /**
     * Executes a bound statement
     * @param bs BoundStatement with bound parameterized values
     * @return ResultSet (potentially empty) storing results
     */
    public ResultSet execute(BoundStatement bs) {
        return session.execute(bs);
    }

    /**
     * Executes a string as a cql statement - used for internal, safe statements
     * that receive 0 user input.
     * @param stringStatement - cql string
     * @return ResultSet holding results
     */
    public ResultSet execute(String stringStatement) {
        return session.execute(stringStatement);
    }

    /**
     * Used for shutting down the client. The client runs on a thread that will never stop, unless explicitly told to.
     */
    public void shutDownClient() {
        session.close();
    }
}
