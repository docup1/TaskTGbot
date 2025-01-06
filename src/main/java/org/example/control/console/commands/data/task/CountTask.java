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

public class CountTask extends Command {
    private String output = "\n";

    public void run(ArrayList<String> args) {
        if (args.size() < 2 || args.get(0).isBlank() || args.get(1).isBlank()) {
            output = "Error: User ID and task status cannot be empty.";
            Logger.put(output, LogType.WARN);
            setResponse(new Response(Status.BAD_REQUEST, ContentType.TEXT_PLAIN, "ID пользователя и статус задачи не могут быть пустыми"));
            return;
        }

        try {
            int userId = Integer.parseInt(args.get(0));
            String status = args.get(1).toLowerCase();

            // Проверяем, что переданный статус валиден
            if (!status.equals("open") && !status.equals("completed") && !status.equals("draft")) {
                output = "Error: Invalid task status.";
                Logger.put(output, LogType.WARN);
                setResponse(new Response(Status.BAD_REQUEST, ContentType.TEXT_PLAIN, "Неверный статус задачи"));
                return;
            }

            String query = "SELECT COUNT(*) AS task_count FROM Tasks WHERE creator_id = ? AND status = ?::task_status";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            statement.setString(2, status);  // Передаем строковое значение статуса


            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int taskCount = resultSet.getInt("task_count");
                output = taskCount + "";
                Logger.put(output, LogType.INFO);
                setResponse(new Response(Status.OK, ContentType.TEXT_PLAIN, "" + taskCount));
            } else {
                output = "Error: No tasks found for the specified user and status.";
                Logger.put(output, LogType.WARN);
                setResponse(new Response(Status.NOT_FOUND, ContentType.TEXT_PLAIN, "Задачи для указанного пользователя и статуса не найдены"));
            }

            resultSet.close();
            statement.close();
        } catch (NumberFormatException e) {
            output = "Error: Invalid user ID format.";
            Logger.put(output, LogType.WARN);
            setResponse(new Response(Status.BAD_REQUEST, ContentType.TEXT_PLAIN, "Неверный формат ID пользователя"));
        } catch (SQLException e) {
            output = "SQL Error: " + e.getMessage();
            Logger.put(output, LogType.ERROR);
            Logger.put(e.getClass().getSimpleName(), LogType.DEBUG);
            setResponse(new Response(Status.INTERNAL_SERVER_ERROR, ContentType.TEXT_PLAIN, "Ошибка при подсчете задач пользователя"));
        } catch (Exception e) {
            output = "Unexpected Error: " + e.getMessage();
            Logger.put(output, LogType.ERROR);
            Logger.put(e.getClass().getSimpleName(), LogType.DEBUG);
            setResponse(new Response(Status.INTERNAL_SERVER_ERROR, ContentType.TEXT_PLAIN, "Непредвиденная ошибка"));
        }
    }
}
