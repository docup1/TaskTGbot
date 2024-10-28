package org.example.data.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Connect {

    // URL для подключения к базе данных
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "docup";
    private static final String PASSWORD = "docup2005";

    public void run() {
        Connection connection = null;

        try {
            // Устанавливаем соединение
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to the PostgreSQL database!");

            // Пример запроса: Получаем всех пользователей
            String query = "SELECT * FROM Users";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            // Обработка результата запроса
            while (resultSet.next()) {
                int userId = resultSet.getInt("user_id");
                String username = resultSet.getString("username");
                String bio = resultSet.getString("bio");
                double rating = resultSet.getDouble("rating");

                System.out.println("User ID: " + userId);
                System.out.println("Username: " + username);
                System.out.println("Bio: " + bio);
                System.out.println("Rating: " + rating);
                System.out.println("---------------");
            }

            resultSet.close();
            statement.close();

        } catch (SQLException e) {
            System.out.println("Connection failure.");
            e.printStackTrace();
        } finally {
            // Закрываем соединение
            try {
                if (connection != null) {
                    connection.close();
                    System.out.println("Connection closed.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
