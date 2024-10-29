package org.example.data.database;

import org.example.data.logger.LogType;
import org.example.data.logger.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBase {

    // URL для подключения к базе данных
    private String URL;
    private String USER;
    private String PASSWORD;
    public static Connection connection = null;

    public void init(){
        try {

            URL = System.getenv("DB_URL");
            USER = System.getenv("DB_USER");
            PASSWORD = System.getenv("DB_PASSWORD");

            Logger.print("DB_URL: " + URL, LogType.TRACE);
            Logger.print("DB_USER: " + USER, LogType.TRACE);
            Logger.print("DB_PASSWORD: " + PASSWORD, LogType.TRACE);

            if (URL == null || USER == null || PASSWORD == null) {
                throw new IllegalStateException("Database environment variables are not set properly.");
            }
        } catch (Exception e) {
            Logger.print("Error retrieving environment variables: " + e.getMessage(), LogType.DEBUG_FATAL);
        }
    }
    public void connect() {
        try {
            // Устанавливаем соединение
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            Logger.print("Connected to the database!", LogType.INFO);

        } catch (SQLException e) {
            Logger.print("Connection failure.", LogType.ERROR);
            Logger.print(e.getMessage(), LogType.DEBUG_FATAL);
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                Logger.print("Closed connection.", LogType.INFO);
            }
        } catch (SQLException ex) {
            Logger.print("Failed to close connection: " + ex.getMessage(), LogType.ERROR);
        } finally {
            connection = null; // Очищаем ссылку на соединение
        }
    }
}