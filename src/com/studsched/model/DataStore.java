package com.studsched.model;

import java.sql.SQLException;

public class DataStore {
    public static void storeNewClient(Client client) throws SQLException, NullPointerException {
        checkForNull(client);
        DatabaseManager.storeNewClient(client);
    }

    public static void saveClientSchedules(Client client) throws SQLException {
        if (client != null) {
            DatabaseManager.overwriteClientScheduleTable(client);
        }
    }

    private static void checkForNull(Object... objects) throws NullPointerException {
        for (Object object : objects) {
            if (object == null) {
                throw new NullPointerException();
            }
        }
    }
}
