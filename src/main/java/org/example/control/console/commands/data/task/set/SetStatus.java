package org.example.control.console.commands.data.task.set;

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
import java.util.Arrays;

import static org.example.data.database.DataBase.connection;

public class SetStatus extends Command {

    // Возможные статусы из ENUM 'task_status'
    private static final String[] VALID_STATUSES = {"open", "completed", "draft"};

    @Override
    public void run(ArrayList<String> args) {
        Response response;

        // Проверка аргументов
        if (args.size() < 2) {
            response = new Response(
                    Status.BAD_REQUEST,
                    ContentType.TEXT_PLAIN,
                    "Insufficient arguments. Expected status and task_id."
            );
            setResponse(response);
            return;
        }

        // Получаем статус из аргументов
        String status = args.get(1);

        // Проверяем, что статус валидный
        if (!Arrays.asList(VALID_STATUSES).contains(status)) {
            response = new Response(
                    Status.BAD_REQUEST,
                    ContentType.TEXT_PLAIN,
                    "Invalid status. Valid statuses are: " + String.join(", ", VALID_STATUSES)
            );
            setResponse(response);
            return;
        }

        // SQL запрос с явным приведением типа
        String query = "UPDATE Tasks SET status = ?::task_status WHERE task_id = ?;";
        try (PreparedStatement statement = connection.prepareStatement(query)) {

            // Устанавливаем статус (сначала строка, которая будет преобразована в task_status в SQL)
            statement.setString(1, status);
            Logger.put(status, LogType.DEBUG);

            // Устанавливаем task_id
            statement.setInt(2, Integer.parseInt(args.get(0))); // task_id

            // Выполняем обновление
            int rowsAffected = statement.executeUpdate();

            // Формируем успешный ответ
            response = new Response(
                    Status.OK,
                    ContentType.TEXT_PLAIN,
                    rowsAffected > 0
                            ? "Task status updated successfully. Rows affected: " + rowsAffected
                            : "No rows affected. Please check if the task ID exists."
            );
        } catch (SQLException e) {
            // Ошибка базы данных
            response = new Response(
                    Status.INTERNAL_SERVER_ERROR,
                    ContentType.TEXT_PLAIN,
                    "Database error: " + e.getMessage()
            );
        } catch (NumberFormatException e) {
            // Ошибка преобразования task_id
            response = new Response(
                    Status.BAD_REQUEST,
                    ContentType.TEXT_PLAIN,
                    "Error: Task ID must be a valid integer."
            );
        } catch (Exception e) {
            // Неожиданная ошибка
            response = new Response(
                    Status.INTERNAL_SERVER_ERROR,
                    ContentType.TEXT_PLAIN,
                    "Unexpected error: " + e.getMessage()
            );
        }

        // Устанавливаем ответ
        setResponse(response);
    }
}
