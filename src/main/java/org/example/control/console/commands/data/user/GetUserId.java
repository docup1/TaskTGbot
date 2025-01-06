package org.example.control.console.commands.data.user;

import org.example.control.console.commands.Command;
import org.example.data.logger.LogType;
import org.example.data.logger.Logger;
import org.example.data.response.ContentType;
import org.example.data.response.Response;
import org.example.data.response.Status;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.example.data.database.DataBase.connection;

public class GetUserId extends Command {
    public void run(ArrayList<String> args) {
        if (args.isEmpty() || args.get(0).isBlank()) {
            Logger.put("Error: Username cannot be empty.", LogType.WARN);
            setResponse(new Response(Status.BAD_REQUEST, ContentType.TEXT_PLAIN, "Имя пользователя не может быть пустым"));
            return;
        }

        String username = args.get(0);

        try {
            String query = "SELECT user_id FROM Users WHERE username = ?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int userId = resultSet.getInt("user_id");
                setResponse(new Response(Status.OK, ContentType.TEXT_PLAIN, "" + userId));
            } else {
                setResponse(new Response(Status.NOT_FOUND, ContentType.TEXT_PLAIN, "Пользователь не найден"));
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            Logger.put("SQL Error: " + e.getMessage(), LogType.ERROR);
            setResponse(new Response(Status.INTERNAL_SERVER_ERROR, ContentType.TEXT_PLAIN, "Ошибка при получении ID пользователя"));
        }
    }
}