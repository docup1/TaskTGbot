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
import java.util.Date;
import java.util.ArrayList;

import static org.example.data.database.DataBase.connection;

public class GetTaskById extends Command {
    private String output = "\n";

    @Override
    public void run(ArrayList<String> args) {
        // Проверяем наличие ID задачи в аргументах
        if (args.isEmpty() || args.get(0).isBlank()) {
            output = "Error: Task ID cannot be empty.";
            Logger.put(output, LogType.WARN);
            setResponse(new Response(Status.BAD_REQUEST, ContentType.TEXT_PLAIN, "ID задачи не может быть пустым"));
            return;
        }

        try {
            // Преобразуем аргумент в число
            int taskId = Integer.parseInt(args.get(0));

            // SQL-запрос для получения данных задачи и имени автора
            String query = "SELECT t.title, t.description, t.status, t.created_at, t.due_date, t.reward, u.username " +
                    "FROM Tasks t " +
                    "JOIN Users u ON t.creator_id = u.user_id " +
                    "WHERE t.task_id = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, taskId);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        // Извлекаем данные из результата
                        String title = resultSet.getString("title");
                        String description = resultSet.getString("description");
                        Date createdAt = resultSet.getTimestamp("created_at");
                        Date dueDate = resultSet.getTimestamp("due_date");
                        double reward = resultSet.getDouble("reward");
                        String author = resultSet.getString("username");

                        // Форматируем даты
                        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                        String formattedCreatedAt = (createdAt != null) ? dateFormatter.format(createdAt) : "Неизвестно";
                        String formattedDueDate = (dueDate != null) ? dateFormatter.format(dueDate) : "Неизвестно";

                        // Формируем текст ответа
                        StringBuilder responseText = new StringBuilder();
                        responseText.append("<b>").append(title).append("</b>\n\n")
                                .append(description != null ? description : "Нет описания").append("\n\n")
                                .append("<u>Дата создания:</u> <i>").append(formattedCreatedAt).append("</i>\n")
                                .append("<u>Дедлайн:</u> <i>").append(formattedDueDate).append("</i>\n")
                                .append("<u>Вознаграждение:</u> <i>").append(reward).append(" руб.</i>\n\n")
                                .append("<u>Автор:</u> @").append(author).append("\n");

                        // Устанавливаем ответ
                        output = responseText.toString();
                        Logger.put("Task retrieved successfully for task ID: " + taskId, LogType.INFO);
                        setResponse(new Response(Status.OK, ContentType.TEXT_PLAIN, output));
                    } else {
                        // Если задача не найдена
                        output = "Error: Task not found for the specified ID.";
                        Logger.put(output, LogType.WARN);
                        setResponse(new Response(Status.NOT_FOUND, ContentType.TEXT_PLAIN, "Задача с указанным ID не найдена"));
                    }
                }
            }
        } catch (NumberFormatException e) {
            output = "Error: Invalid task ID format.";
            Logger.put(output, LogType.WARN);
            setResponse(new Response(Status.BAD_REQUEST, ContentType.TEXT_PLAIN, "Неверный формат ID задачи"));
        } catch (SQLException e) {
            output = "SQL Error: " + e.getMessage();
            Logger.put(output, LogType.ERROR);
            Logger.put(e.toString(), LogType.DEBUG);
            setResponse(new Response(Status.INTERNAL_SERVER_ERROR, ContentType.TEXT_PLAIN, "Ошибка при получении задачи"));
        } catch (Exception e) {
            output = "Unexpected Error: " + e.getMessage();
            Logger.put(output, LogType.ERROR);
            Logger.put(e.toString(), LogType.DEBUG);
            setResponse(new Response(Status.INTERNAL_SERVER_ERROR, ContentType.TEXT_PLAIN, "Непредвиденная ошибка"));
        }
    }
}
