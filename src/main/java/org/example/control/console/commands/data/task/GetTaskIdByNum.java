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

public class GetTaskIdByNum extends Command {
    private String output = "\n";

    @Override
    public void run(ArrayList<String> args) {
        // Проверка количества аргументов и их содержимого
        if (args.size() < 3 || args.get(0).isBlank() || args.get(1).isBlank() || args.get(2).isBlank()) {
            output = "Error: User ID, task number, and status cannot be empty.";
            Logger.put(output, LogType.WARN);
            setResponse(new Response(Status.BAD_REQUEST, ContentType.TEXT_PLAIN, "ID пользователя, номер задачи и статус не могут быть пустыми"));
            return;
        }

        try {
            // Проверка корректности формата аргументов
            int userId = Integer.parseInt(args.get(0));
            String taskStatus = args.get(1).toLowerCase();
            int taskNumber = Integer.parseInt(args.get(2));

            // Проверка на положительные значения userId и taskNumber
            if (userId <= 0 || taskNumber <= 0) {
                output = "Error: User ID and task number must be positive.";
                Logger.put(output, LogType.WARN);
                setResponse(new Response(Status.BAD_REQUEST, ContentType.TEXT_PLAIN, "ID пользователя и номер задачи должны быть положительными числами"));
                return;
            }

            // Проверка корректности статуса задачи
            if (!taskStatus.matches("open|completed|draft")) {
                output = "Error: Invalid task status.";
                Logger.put(output, LogType.WARN);
                setResponse(new Response(Status.BAD_REQUEST, ContentType.TEXT_PLAIN, "Недопустимый статус задачи"));
                return;
            }

            // SQL-запрос с учетом статуса задачи и сортировкой
            String query = "SELECT task_id FROM Tasks WHERE creator_id = ? AND status = ?::task_status ORDER BY created_at ASC, task_id ASC LIMIT 1 OFFSET ?;";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, userId);
                statement.setString(2, taskStatus);
                statement.setInt(3, taskNumber - 1); // Смещение для выборки задачи по порядковому номеру

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        int taskId = resultSet.getInt("task_id");
                        output = taskId + "";
                        Logger.put("Task ID retrieved successfully for task number " + taskNumber + " of user ID: " + userId + " with status: " + taskStatus, LogType.INFO);
                        setResponse(new Response(Status.OK, ContentType.TEXT_PLAIN, String.valueOf(taskId)));
                    } else {
                        output = "Error: Task not found at the specified position.";
                        Logger.put(output, LogType.WARN);
                        setResponse(new Response(Status.NOT_FOUND, ContentType.TEXT_PLAIN, "Задача не найдена на указанной позиции"));
                    }
                }
            }
        } catch (NumberFormatException e) {
            output = "Error: Invalid user ID, task number, or status format.";
            Logger.put(output, LogType.WARN);
            setResponse(new Response(Status.BAD_REQUEST, ContentType.TEXT_PLAIN, "Неверный формат ID пользователя, номера задачи или статуса"));
        } catch (SQLException e) {
            output = "SQL Error: " + e.getMessage();
            Logger.put(output, LogType.ERROR);
            Logger.put(e.toString(), LogType.DEBUG);  // Полное сообщение об ошибке для отладки
            setResponse(new Response(Status.INTERNAL_SERVER_ERROR, ContentType.TEXT_PLAIN, "Ошибка при получении ID задачи"));
        } catch (Exception e) {
            output = "Unexpected Error: " + e.getMessage();
            Logger.put(output, LogType.ERROR);
            Logger.put(e.toString(), LogType.DEBUG);  // Полное сообщение об ошибке для отладки
            setResponse(new Response(Status.INTERNAL_SERVER_ERROR, ContentType.TEXT_PLAIN, "Непредвиденная ошибка"));
        }
    }
}
