package org.example.control.console.commands.data.task;

import org.example.control.console.commands.Command;
import org.example.data.logger.LogType;
import org.example.data.logger.Logger;
import org.example.data.response.ContentType;
import org.example.data.response.Response;
import org.example.data.response.Status;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.example.data.database.DataBase.connection;

public class CreateTask extends Command {

    public void run(ArrayList<String> args) {
        if (args == null || args.size() < 2 || args.get(0).isBlank() || args.get(1).isBlank()) {
            String errorMessage = "Error: Invalid arguments. Expected: creator_id and title.";
            Logger.put(errorMessage, LogType.WARN);
            setResponse(new Response(Status.BAD_REQUEST, ContentType.TEXT_PLAIN, "Недопустимые аргументы."));
            return;
        }

        String query = "INSERT INTO Tasks (creator_id, title) VALUES (?, ?);";
        try (PreparedStatement statement = connection.prepareStatement(query)) {

            int creatorId;
            try {
                creatorId = Integer.parseInt(args.get(0));
            } catch (NumberFormatException e) {
                String errorMessage = "Error: Invalid creator_id format: " + args.get(0);
                Logger.put(errorMessage, LogType.WARN);
                setResponse(new Response(Status.BAD_REQUEST, ContentType.TEXT_PLAIN, "Неверный формат creator_id: " + args.get(0)));
                return;
            }

            String title = args.get(1);
            statement.setInt(1, creatorId);
            statement.setString(2, title);

            int rowsAffected = statement.executeUpdate();
            String successMessage = "Task added successfully. Rows affected: " + rowsAffected;
            Logger.put(successMessage, LogType.INFO);
            setResponse(new Response(Status.ACCEPTED, ContentType.TEXT_PLAIN, "Задача успешно добавлена"));

        } catch (SQLException e) {
            String errorMessage = "SQL Error: " + e.getMessage();
            Logger.put(errorMessage, LogType.ERROR);
            Logger.put(e.getClass().getSimpleName(), LogType.DEBUG);
            setResponse(new Response(Status.INTERNAL_SERVER_ERROR, ContentType.TEXT_PLAIN, "Ошибка при добавлении задачи"));
        } catch (Exception e) {
            String errorMessage = "Unexpected Error: " + e.getMessage();
            Logger.put(errorMessage, LogType.ERROR);
            Logger.put(e.getClass().getSimpleName(), LogType.DEBUG);
            setResponse(new Response(Status.INTERNAL_SERVER_ERROR, ContentType.TEXT_PLAIN, "Непредвиденная ошибка"));
        }
    }
}
