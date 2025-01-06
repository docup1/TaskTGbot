package org.example.control.console.commands.data.user;

import org.example.control.console.commands.Command;
import org.example.data.logger.LogType;
import org.example.data.logger.Logger;
import org.example.data.response.ContentType;
import org.example.data.response.Response;
import org.example.data.response.Status;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.example.data.database.DataBase.connection;

public class AddUser extends Command {
    private String output = "\n";

    public void run(ArrayList<String> args) {
        if (args.isEmpty() || args.get(0).isBlank()) {
            output = "Error: Username cannot be empty.";
            Logger.put(output, LogType.WARN);
            setResponse(new Response(Status.BAD_REQUEST, ContentType.TEXT_PLAIN, "Имя пользователя не может быть пустым"));
            return;
        }

        try {
            // Подготавливаем запрос на добавление нового пользователя в таблицу
            String query = "INSERT INTO Users (username) VALUES (?);";
            PreparedStatement statement = connection.prepareStatement(query);

            // Устанавливаем имя пользователя
            statement.setString(1, args.get(0));

            // Выполняем запрос на добавление данных
            int rowsAffected = statement.executeUpdate();
            output = "User added successfully. Rows affected: " + rowsAffected;

            // Закрываем подготовленный запрос
            statement.close();
            Logger.put(output, LogType.INFO);
            setResponse(new Response(Status.ACCEPTED, ContentType.TEXT_PLAIN, "Пользователь успешно добавлен"));
        } catch (SQLException e) {
            if(e.getMessage().split(" ")[e.getMessage().split(" ").length - 1].equals("exists.")) {
                output = "Error: User already exists.";
                Logger.put(output, LogType.ERROR);
                Logger.put(e.getClass().getSimpleName(), LogType.DEBUG);
                setResponse(new Response(Status.FOUND, ContentType.TEXT_PLAIN, "Такой пользователь уже существует"));
            }
                else{
                output = "SQL Error: " + e.getMessage();
                Logger.put(output, LogType.ERROR);
                Logger.put(e.getClass().getSimpleName(), LogType.DEBUG);
                setResponse(new Response(Status.INTERNAL_SERVER_ERROR, ContentType.TEXT_PLAIN, "Ошибка при добавлении пользователя"));
            }
        } catch (Exception e) {
            output = "Unexpected Error: " + e.getMessage();
            Logger.put(output, LogType.ERROR);
            Logger.put(e.getClass().getSimpleName(), LogType.DEBUG);
            setResponse(new Response(Status.INTERNAL_SERVER_ERROR, ContentType.TEXT_PLAIN, "Непредвиденная ошибка"));
        }
    }
}