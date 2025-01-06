package org.example.control.console.commands.data.task;

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

public class GetTaskDetailsById extends Command {
    @Override
    public void run(ArrayList<String> args) {
        if (args.isEmpty() || args.get(0).isBlank()) {
            Logger.put("Error: Task ID cannot be empty.", LogType.WARN);
            setResponse(new Response(Status.BAD_REQUEST, ContentType.TEXT_PLAIN, "ID задачи не может быть пустым"));
            return;
        }

        try {
            int taskId = Integer.parseInt(args.get(0));
            if (taskId <= 0) {
                Logger.put("Error: Task ID must be positive.", LogType.WARN);
                setResponse(new Response(Status.BAD_REQUEST, ContentType.TEXT_PLAIN, "ID задачи должен быть положительным числом"));
                return;
            }

            // SQL-запрос для получения деталей задачи
            String query = "SELECT t.creator_id, u.username, t.status, " +
                    "(SELECT COUNT(*) + 1 FROM Tasks t2 " +
                    "WHERE t2.creator_id = t.creator_id AND t2.status = t.status AND t2.created_at < t.created_at) AS task_number " +
                    "FROM Tasks t " +
                    "JOIN Users u ON t.creator_id = u.user_id " +
                    "WHERE t.task_id = ?;";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, taskId);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        int creatorId = resultSet.getInt("creator_id");
                        String username = resultSet.getString("username");
                        String status = resultSet.getString("status");
                        int taskNumber = resultSet.getInt("task_number");

                        String output =  status + " " + taskNumber;
                        Logger.put("Task details retrieved successfully for task ID: " + taskId, LogType.INFO);
                        setResponse(new Response(Status.OK, ContentType.TEXT_PLAIN, output));
                    } else {
                        Logger.put("Error: Task not found with ID: " + taskId, LogType.WARN);
                        setResponse(new Response(Status.NOT_FOUND, ContentType.TEXT_PLAIN, "Задача с указанным ID не найдена"));
                    }
                }
            }
        } catch (NumberFormatException e) {
            Logger.put("Error: Invalid task ID format.", LogType.WARN);
            setResponse(new Response(Status.BAD_REQUEST, ContentType.TEXT_PLAIN, "Неверный формат ID задачи"));
        } catch (SQLException e) {
            Logger.put("SQL Error: " + e.getMessage(), LogType.ERROR);
            setResponse(new Response(Status.INTERNAL_SERVER_ERROR, ContentType.TEXT_PLAIN, "Ошибка при получении деталей задачи"));
        } catch (Exception e) {
            Logger.put("Unexpected Error: " + e.getMessage(), LogType.ERROR);
            setResponse(new Response(Status.INTERNAL_SERVER_ERROR, ContentType.TEXT_PLAIN, "Непредвиденная ошибка"));
        }
    }
}
