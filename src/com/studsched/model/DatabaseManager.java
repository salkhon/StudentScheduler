package com.studsched.model;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class DatabaseManager {
    private static final String CONNECTION_STRING = "jdbc:sqlite:D:\\Griffin_\\Java\\Student Scheduler\\data\\studsched.db";
    private static Connection connection;

    // tables
    private static final String TEXT_TYPE = "TEXT";
    private static final String TABLE_CLIENT_INFO = "ClientInfo";
    private static final String COL_CI_STUDENT_ID = "studentID";
    private static final String COL_CI_FULL_NAME = "fullName";
    private static final String COL_CI_EMAIL = "email";
    private static final String COL_CI_PASSWORD = "password";

    private static final String TABLE_SCHEDULES_PREFIX = "Schedules"; // Schedules1805050
    private static final String COL_CS_DATE = "date";
    private static final String COL_CS_TIME = "time";
    private static final String COL_CS_TITLE = "title";
    private static final String COL_CS_NOTE = "note";

    // sql
    private static final String QUERY_CLIENT_INFO_FROM_CI =
            "SELECT * FROM " + TABLE_CLIENT_INFO +
            " WHERE " + TABLE_CLIENT_INFO + "." + COL_CI_STUDENT_ID + " = ? AND " +
                    TABLE_CLIENT_INFO + "." + COL_CI_PASSWORD + " = ?";
    private static PreparedStatement queryClientInfo;

    private static final String QUERY_CLIENT_BY_ID_FROM_CI =
            "SELECT " + TABLE_CLIENT_INFO + "." + COL_CI_STUDENT_ID +
                    " FROM " + TABLE_CLIENT_INFO +
                    " WHERE " + TABLE_CLIENT_INFO + "." + COL_CI_STUDENT_ID +
                    " = ?";
    private static PreparedStatement queryClientById;

    private static final String QUERY_CLIENT_SCHEDULES_FROM_CS =
            "SELECT * FROM ";

    private static final String INSERT_CLIENT_CRED_INTO_CI =
            "INSERT INTO " + TABLE_CLIENT_INFO +
                    " VALUES (?, ?, ?, ?)";
    private static PreparedStatement insertClientInfo;

    private static final String CREATE_CLIENT_SCHEDULE_TABLE_BEFORE_ROLL =
            "CREATE TABLE IF NOT EXISTS " + TABLE_SCHEDULES_PREFIX;
    private static final String CREATE_CLIENT_SCHEDULE_TABLE_AFTER_ROLL =
            " (" + COL_CS_DATE + " " + TEXT_TYPE + ", " +
                    COL_CS_TIME + " " + TEXT_TYPE + ", " +
                    COL_CS_TITLE + " " + TEXT_TYPE +  ", " +
                    COL_CS_NOTE + " " + TEXT_TYPE + ")";

    private static final String DELETE_ALL_RECORDS = "DELETE FROM ";

    private static final String STORE_NEW_SCHEDULE_CS_BEFORE_TABLE_NAME = "INSERT INTO ";
    public static final String STORE_NEW_SCHEDULE_CS_AFTER_TABLE_NAME = " VALUES (?, ?, ?, ?)";

    private DatabaseManager() {}

    public static void openConnection() throws SQLException {
        connection = DriverManager.getConnection(CONNECTION_STRING);
        queryClientInfo = connection.prepareStatement(QUERY_CLIENT_INFO_FROM_CI);
        queryClientById = connection.prepareStatement(QUERY_CLIENT_BY_ID_FROM_CI);
        insertClientInfo = connection.prepareStatement(INSERT_CLIENT_CRED_INTO_CI);
    }

    public static void closeConnection() throws SQLException{
        if (connection != null) {
            connection.close();
        }
        if (queryClientInfo != null) {
            queryClientInfo.close();
        }
        if (queryClientById != null) {
            queryClientById.close();
        }
        if (insertClientInfo != null) {
            insertClientInfo.close();
        }
    }

    public static boolean authenticate(String studentId, String password) throws SQLException {
        return getClientCredentials(studentId, password) != null;
    }

    public static boolean clientExists(String studentId) throws SQLException {
        boolean clientExists = false;
        if (studentId != null) {
            queryClientById.clearParameters();
            queryClientById.setString(1, studentId);
            try (ResultSet resultSet = queryClientById.executeQuery()) {
                if (resultSet.next()) {
                    clientExists = true;
                }
            }
        }
        return clientExists;
    }

    public static Client getClient(String studentId, String password) throws SQLException {
        Client client = getClientCredentials(studentId, password);
        if (client != null) {
            loadClientSchedules(client);
        }
        return client;
    }

    public static Client getClientCredentials(String studentId, String password) throws SQLException {
        Client client = null;
        if (studentId != null && password != null) {
            queryClientInfo.clearParameters();
            queryClientInfo.setString(1, studentId);
            queryClientInfo.setString(2, password);
            try (ResultSet resultSet = queryClientInfo.executeQuery()) {
                if (resultSet.next()) {
                    client = new Client(resultSet.getString(COL_CI_STUDENT_ID),
                            resultSet.getString(COL_CI_FULL_NAME), resultSet.getString(COL_CI_EMAIL),
                            resultSet.getString(COL_CI_PASSWORD));
                }
            }
        }
        return client;
    }

    private static void loadClientSchedules(Client client) throws SQLException {
        if (client != null) {
            Statement querySchedule = connection.createStatement();
            try (ResultSet resultSet = querySchedule.executeQuery(QUERY_CLIENT_SCHEDULES_FROM_CS + TABLE_SCHEDULES_PREFIX + client.getStudentId())) {
                while (resultSet.next()) {
                    client.addSchedule(new ClientSchedule(client.getStudentId(), LocalDate.parse(resultSet.getString(COL_CS_DATE)),
                            LocalTime.parse(resultSet.getString(COL_CS_TIME)), resultSet.getString(COL_CS_TITLE), resultSet.getString(COL_CS_NOTE)));
                }
            }
        }
    }

    public static void storeNewClient(Client client) throws SQLException {
        storeNewClientCred(client);
        createNewClientScheduleTable(client);
    }

    public static void storeNewClientCred(Client client) throws SQLException {
        if (client != null) {
            insertClientInfo.clearParameters();
            insertClientInfo.setString(1, client.getStudentId());
            insertClientInfo.setString(2, client.getFullName());
            insertClientInfo.setString(3, client.getEmail());
            insertClientInfo.setString(4, client.getPassword());

            insertClientInfo.execute();
        }
    }

    public static void createNewClientScheduleTable(Client client) throws SQLException {
        if (client != null) {
            Statement createTable = connection.createStatement();
            createTable.execute(CREATE_CLIENT_SCHEDULE_TABLE_BEFORE_ROLL + client.getStudentId() +
                    CREATE_CLIENT_SCHEDULE_TABLE_AFTER_ROLL);
        }
    }

    public static void overwriteClientScheduleTable(Client client) throws SQLException {
        deleteAllSchedulesOfClient(client);
        storeNewClientSchedules(client);
    }

    private static void storeNewClientSchedules(Client client) throws SQLException {
        List<ClientSchedule> clientSchedules = client.getAllClientSchedules();
        try (PreparedStatement storeClientPrepStatement = connection.prepareStatement(STORE_NEW_SCHEDULE_CS_BEFORE_TABLE_NAME +
                TABLE_SCHEDULES_PREFIX + client.getStudentId() + STORE_NEW_SCHEDULE_CS_AFTER_TABLE_NAME)) {
            for (ClientSchedule clientSchedule : clientSchedules) {
                storeClientPrepStatement.clearParameters();
                storeClientPrepStatement.setString(1, clientSchedule.getLocalDate().toString());
                storeClientPrepStatement.setString(2, clientSchedule.getLocalTime().toString());
                storeClientPrepStatement.setString(3, clientSchedule.getTitle());
                storeClientPrepStatement.setString(4, clientSchedule.getNote());

                storeClientPrepStatement.executeUpdate();
            }
        }
    }

    private static void deleteAllSchedulesOfClient(Client client) throws SQLException {
        if (client != null) {
            try (Statement statement = connection.createStatement()) {
                String sql = DELETE_ALL_RECORDS + TABLE_SCHEDULES_PREFIX + client.getStudentId();
                statement.execute(sql);
            }
        }
    }

    // test client
    public static void main(String[] args) {
        try {
            DatabaseManager.openConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + TABLE_CLIENT_INFO);
            while (resultSet.next()) {
                System.out.println("Client: " + resultSet.getString(COL_CI_STUDENT_ID) + " " + resultSet.getString(COL_CI_PASSWORD));
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
}
