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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static org.example.data.database.DataBase.connection;

public class ShowUserInf extends Command {
    private String output = "\n";

    public void run(ArrayList<String> args) {
        if (args.isEmpty() || args.get(0).isBlank()) {
            output = "Error: User ID cannot be empty.";
            Logger.put(output, LogType.WARN);
            setResponse(new Response(Status.BAD_REQUEST, ContentType.TEXT_PLAIN, "ID пользователя не может быть пустым"));
            return;
        }

        try {
            String query = "SELECT username, bio, created_at FROM Users WHERE username = ?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, args.get(0));

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String username = resultSet.getString("username");
                String bio = resultSet.getString("bio");
                Date createdAt = resultSet.getDate("created_at");

                SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
                String formattedDate = (createdAt != null) ? dateFormatter.format(createdAt) : "Неизвестно";

                StringBuilder responseText = new StringBuilder();
                responseText.append("<b>Информация о пользователе</b>\n\n");
                responseText.append("<b>Имя пользователя:</b> @").append(username).append("\n");
                responseText.append("<b>Биография:</b>\n").append(( bio != null ? bio : "Отсутствует")).append("\n");
                responseText.append("\n<b>Дата создания:</b> ").append(formattedDate).append("\n");

                output = responseText.toString();
                Logger.put("User information retrieved successfully.", LogType.INFO);
                setResponse(new Response(Status.OK, ContentType.TEXT_PLAIN, output));
            } else {
                output = "Error: User not found.";
                Logger.put(output, LogType.WARN);
                setResponse(new Response(Status.NOT_FOUND, ContentType.TEXT_PLAIN, "Пользователь не найден"));
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            output = "SQL Error: " + e.getMessage();
            Logger.put(output, LogType.ERROR);
            Logger.put(e.getClass().getSimpleName(), LogType.DEBUG);
            setResponse(new Response(Status.INTERNAL_SERVER_ERROR, ContentType.TEXT_PLAIN, "Ошибка при получении информации о пользователе"));
        } catch (Exception e) {
            output = "Unexpected Error: " + e.getMessage();
            Logger.put(output, LogType.ERROR);
            Logger.put(e.getClass().getSimpleName(), LogType.DEBUG);
            setResponse(new Response(Status.INTERNAL_SERVER_ERROR, ContentType.TEXT_PLAIN, "Непредвиденная ошибка"));
        }
    }
}
