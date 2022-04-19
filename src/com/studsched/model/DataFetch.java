package com.studsched.model;

import java.sql.SQLException;

public class DataFetch {
    // cache
    private static Client lastLoadedClient;

    public static Client getLastLoadedClient() {
        return lastLoadedClient;
    }

    public static Client fetchClient(String name, String password) throws SQLException, NullPointerException {
        checkForNull(name, password);
        Client fetchedClient = DatabaseManager.getClient(name, password);
        if (fetchedClient != null) {
            lastLoadedClient = fetchedClient;
        }
        return fetchedClient;
    }

    public static boolean clientExists(String studentId, String password) throws SQLException {
        checkForNull(studentId, password);
        return DatabaseManager.authenticate(studentId, password); // does not load schedule, so not stored as lastClient
    }

    public static boolean clientExists(String studentId) throws SQLException {
        checkForNull(studentId);
        return DatabaseManager.clientExists(studentId);
    }

    private static void checkForNull(Object... objects) throws NullPointerException {
        for (Object object : objects) {
            if (object == null) {
                throw new NullPointerException();
            }
        }
    }
}
