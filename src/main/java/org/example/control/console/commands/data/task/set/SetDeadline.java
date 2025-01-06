package org.example.control.console.commands.data.task.set;

import org.example.control.console.commands.Command;
import org.example.data.response.ContentType;
import org.example.data.response.Response;
import org.example.data.response.Status;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import static org.example.data.database.DataBase.connection;

public class SetDeadline extends Command {

    @Override
    public void run(ArrayList<String> args) {
        // Проверка наличия аргументов
        if (args.size() < 2) {
            setResponse(new Response(Status.BAD_REQUEST, ContentType.TEXT_PLAIN,
                    "Insufficient arguments. Expected task_id and deadline."));
            return;
        }

        // Проверка соединения с базой данных
        if (connection == null) {
            setResponse(new Response(Status.INTERNAL_SERVER_ERROR, ContentType.TEXT_PLAIN,
                    "Database connection is not available."));
            return;
        }

        String query = "UPDATE Tasks SET due_date = ? WHERE task_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            // Парсинг task_id
            int taskId = Integer.parseInt(args.get(0));

            // Парсинг дедлайна
            LocalDateTime deadline = parseDateTime(args.get(1));
            if (deadline == null) {
                setResponse(new Response(Status.BAD_REQUEST, ContentType.TEXT_PLAIN,
                        "Error: Unable to parse deadline. Supported formats: YYYY-MM-DD, DD/MM/YYYY, MM-DD-YYYY, optionally with time."));
                return;
            }

            // Логирование для отладки
            System.out.println("Parsed LocalDateTime: " + deadline);

            // Преобразование в Timestamp с учетом временной зоны
            Timestamp timestamp = Timestamp.valueOf(deadline);
            System.out.println("Converted Timestamp: " + timestamp);

            // Установка параметров запроса
            statement.setTimestamp(1, timestamp);
            statement.setInt(2, taskId);

            // Выполнение запроса
            int rowsAffected = statement.executeUpdate();
            String message = rowsAffected > 0
                    ? "Task deadline updated successfully. Rows affected: " + rowsAffected
                    : "No rows affected. Please check if the task ID exists.";
            setResponse(new Response(Status.OK, ContentType.TEXT_PLAIN, message));
        } catch (SQLException e) {
            setResponse(new Response(Status.INTERNAL_SERVER_ERROR, ContentType.TEXT_PLAIN,
                    "Database error: " + e.getMessage()));
        } catch (NumberFormatException e) {
            setResponse(new Response(Status.BAD_REQUEST, ContentType.TEXT_PLAIN,
                    "Error: Task ID must be a valid integer."));
        }
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        List<DateTimeFormatter> formatters = List.of(
                DateTimeFormatter.ISO_LOCAL_DATE_TIME, // YYYY-MM-DDTHH:MM:SS
                DateTimeFormatter.ISO_LOCAL_DATE, // YYYY-MM-DD (только дата)
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"), // DD/MM/YYYY HH:MM
                DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm"), // MM-DD-YYYY HH:MM
                DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"), // YYYY.MM.DD HH:MM
                DateTimeFormatter.ofPattern("dd/MM/yyyy"), // DD/MM/YYYY
                DateTimeFormatter.ofPattern("MM-dd-yyyy"), // MM-DD-YYYY
                DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"), // DD.MM.YYYY HH:MM
                DateTimeFormatter.ofPattern("dd.MM.yyyy"), // DD.MM.YYYY
                DateTimeFormatter.ofPattern("dd.MM.yy HH.mm"), // DD.MM.YY HH.MM
                DateTimeFormatter.ofPattern("HH:mm:ss | dd.MM.yy"), // "23:41:58 | 27.11.24"
                DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy"), // 12:30:23 20.12.2025
                DateTimeFormatter.ofPattern("HH:mm | dd.MM.yy"), // "23:41:58 | 27.11.24"
                DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy") // 12:30:23 20.12.2025
        );

        for (DateTimeFormatter formatter : formatters) {
            try {
                if (dateTimeStr.contains(":")) {
                    // Если строка содержит время
                    return LocalDateTime.parse(dateTimeStr, formatter);
                } else {
                    // Если строка только с датой, устанавливаем время в начале суток
                    return LocalDate.parse(dateTimeStr, formatter).atStartOfDay();
                }
            } catch (DateTimeParseException ignored) {
                // Игнорируем ошибку и пробуем следующий формат
            }
        }
        return null; // Если ни один формат не подошел
    }

}
