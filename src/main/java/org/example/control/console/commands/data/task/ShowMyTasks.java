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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.example.data.database.DataBase.connection;

public class ShowMyTasks extends Command {
    private String output = "\n";

    public void run(ArrayList<String> args) {
        if (args.isEmpty() || args.get(0).isBlank()) {
            output = "Error: User ID cannot be empty.";
            Logger.put(output, LogType.WARN);
            setResponse(new Response(Status.BAD_REQUEST, ContentType.TEXT_PLAIN, "ID пользователя не может быть пустым"));
            return;
        }

        try {
            int userId = Integer.parseInt(args.get(0));
            String statusFilter = (args.size() > 1 && !args.get(1).isBlank()) ? args.get(1).toLowerCase() : null;
            Integer taskPosition = (args.size() > 2 && args.get(2).matches("\\d+")) ? Integer.parseInt(args.get(2)) : null;

            String query = "SELECT title, description, status, created_at, due_date, reward \n" +
                    "FROM Tasks \n" +
                    "WHERE creator_id = ? \n";
            if (statusFilter != null) {
                query += "AND LOWER(status::TEXT) = ? \n";
            }
            query += "ORDER BY task_id ASC;";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);

            if (statusFilter != null) {
                statement.setString(2, statusFilter);
            }

            ResultSet resultSet = statement.executeQuery();
            List<String> taskTitles = new ArrayList<>();
            StringBuilder responseText = new StringBuilder();
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            int taskCount = 0;
            boolean taskFound = false;

            while (resultSet.next()) {
                taskCount++;
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                String status = resultSet.getString("status");
                Date createdAt = resultSet.getTimestamp("created_at");
                Date dueDate = resultSet.getTimestamp("due_date");
                double reward = resultSet.getDouble("reward");

                String formattedCreatedAt = (createdAt != null) ? dateFormatter.format(createdAt) : "Неизвестно";
                String formattedDueDate = (dueDate != null) ? dateFormatter.format(dueDate) : "Неизвестно";

                if (taskPosition != null) {
                    if (taskCount == taskPosition) {
                        responseText.append("<b>").append(title).append("</b>\n\n")
                                .append(description != null ? description : "Нет описания").append("\n\n")
                                .append("<u>Дата создания:</u> <i>").append(formattedCreatedAt).append("</i>\n")
                                .append("<u>Дедлайн:</u> <i>").append(formattedDueDate).append("</i>\n")
                                .append("<u>Вознаграждение:</u> <i>").append(reward).append(" руб.</i>\n\n");
                        taskFound = true;
                        break;
                    }
                } else {
                    taskTitles.add(title);
                }
            }

            if (taskPosition != null && !taskFound) {
                output = "Error: Task not found at the specified position.";
                Logger.put(output, LogType.WARN);
                setResponse(new Response(Status.NOT_FOUND, ContentType.TEXT_PLAIN, "Задача не найдена на указанной позиции"));
            } else if (taskCount > 0) {
                if (taskPosition == null) {
                    output = "Tasks retrieved successfully for user ID: " + userId;
                    Logger.put(output, LogType.INFO);
                    setResponse(new Response(Status.OK, ContentType.APPLICATION_JSON, taskTitles));
                } else {
                    output = responseText.toString();
                    Logger.put(output, LogType.INFO);
                    setResponse(new Response(Status.OK, ContentType.TEXT_PLAIN, output));
                }
            } else {
                output = "No tasks found for the specified user.";
                Logger.put(output, LogType.WARN);
                setResponse(new Response(Status.NOT_FOUND, ContentType.TEXT_PLAIN, "Задачи для указанного пользователя не найдены"));
            }

            resultSet.close();
            statement.close();
        } catch (NumberFormatException e) {
            output = "Error: Invalid user ID or task number format.";
            Logger.put(output, LogType.WARN);
            setResponse(new Response(Status.BAD_REQUEST, ContentType.TEXT_PLAIN, "Неверный формат ID пользователя или номера задачи"));
        } catch (SQLException e) {
            output = "SQL Error: " + e.getMessage();
            Logger.put(output, LogType.ERROR);
            Logger.put(e.getClass().getSimpleName(), LogType.DEBUG);
            setResponse(new Response(Status.INTERNAL_SERVER_ERROR, ContentType.TEXT_PLAIN, "Ошибка при получении задач пользователя"));
        } catch (Exception e) {
            output = "Unexpected Error: " + e.getMessage();
            Logger.put(output, LogType.ERROR);
            Logger.put(e.getClass().getSimpleName(), LogType.DEBUG);
            setResponse(new Response(Status.INTERNAL_SERVER_ERROR, ContentType.TEXT_PLAIN, "Непредвиденная ошибка"));
        }
    }
}
