package org.example.commands.data;

import org.example.commands.Command;
import org.example.data.Response;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.example.data.database.DataBase.connection;

public class GetAllUsers extends Command {
    private String output = "\n";
    public Response run(ArrayList<String> args) {
        try {// Пример запроса: Получаем всех пользователей
            String query = "SELECT * FROM Users";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            // Обработка результата запроса
            while (resultSet.next()) {
                int userId = resultSet.getInt("user_id");
                String username = resultSet.getString("username");
                String bio = resultSet.getString("bio");
                double rating = resultSet.getDouble("rating");
                output += userId + " - " + username + " - " + bio + " - " + rating + "\n";
            }

            resultSet.close();
            statement.close();
        }catch (SQLException e) {}
        ans = new Response(output);
        return ans;
    }
}
