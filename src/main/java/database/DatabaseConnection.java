package database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class DatabaseConnection {

    private final String DATABASE_DIRECTORY = "src/main/resources/db";
    private final String DATABASE = DATABASE_DIRECTORY + "/user_db.db";
    private final String URL = String.format("jdbc:sqlite:%s", DATABASE);
    private static DatabaseConnection instance;
    private static Connection connection;

    public DatabaseConnection() {
        connect();
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public void connect() {
        try {
            log.info("Attempting to connect with data base");
            connection = DriverManager.getConnection(URL);
            if(connection != null){
                log.info("Connection with database {} established", DATABASE);
            } else {
                log.info("Failed to connect with database {}", DATABASE);
            }
        } catch (SQLException ex) {
            log.error("Error during database connection: {}", ex.getMessage());

        }
    }

    public Connection getConnection(){
        return connection;
    }

    public void disconnect() {
        try {
            if (connection != null) {
                connection.close();
                log.info("Database disconnected");
            }
        } catch(SQLException ex){
            log.error("Error during database disconnection: {}", ex.getMessage());
        }
    }




    /*private final String USER = "user_manager";
    private final String PASSWORD = "user123";
    private final String DATABASE = "user_db";
    private final int PORT_NUMBER = 5432;
    private final String URL = String.format("jdbc:postgresql://localhost:%d/%s", PORT_NUMBER, DATABASE);
    private static DatabaseConnection instance;
    private static Connection connection;

    public DatabaseConnection() {
        startConnection();
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public void startConnection() {
        try {
            log.info("Attempting to connect with data base");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            if(connection != null){
                log.info("Connection with {} database established on port {}", USER, PORT_NUMBER);
            } else {
                log.info("Failed to connect with {} database established on port {}", USER, PORT_NUMBER);
            }
        } catch (SQLException ex) {
            log.error("Error during database connection: {}", ex.getMessage());

        }
    }

    public Connection getConnection(){
        return connection;
    }

    public void disconnect() {
        try {
            if (connection != null) {
                connection.close();
                log.info("Database disconnected");
            }
        } catch(SQLException ex){
            log.error("Error during database disconnection: {}", ex.getMessage());
        }
    }*/
}