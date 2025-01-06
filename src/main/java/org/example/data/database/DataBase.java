package org.example.data.database;

import org.example.data.logger.LogType;
import org.example.data.logger.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DataBase {

    // URL для подключения к базе данных
    private static String URL;
    private static String USER;
    private static String PASSWORD;
    public static Connection connection = null;

    public static void init(String CONFIG) {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream(new File(CONFIG)));

            URL = props.getProperty("DB_URL");
            USER = props.getProperty("DB_USER");
            PASSWORD = props.getProperty("DB_PASSWORD");

            Logger.put("DB_URL: " + URL, LogType.DEBUG);
            Logger.put("DB_USER: " + USER, LogType.DEBUG);
            Logger.put("DB_PASSWORD: " + PASSWORD, LogType.DEBUG);

            if (URL == null || USER == null || PASSWORD == null) {
                throw new IllegalStateException("Database environment variables are not set properly.");
            }
        } catch (Exception e) {
            Logger.put("Error retrieving environment variables: " + e.getMessage(), LogType.ERROR);
        }
    }
    public void connect() {
        try {
            // Устанавливаем соединение
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            Logger.put("Connected to the database!", LogType.INFO);

        } catch (SQLException e) {
            Logger.put("Connection failure.", LogType.ERROR);
            Logger.put(e.getMessage(), LogType.ERROR);
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                Logger.put("Closed connection.", LogType.INFO);
            }
        } catch (SQLException ex) {
            Logger.put("Failed to close connection: " + ex.getMessage(), LogType.ERROR);
        } finally {
            connection = null; // Очищаем ссылку на соединение
        }
    }
}