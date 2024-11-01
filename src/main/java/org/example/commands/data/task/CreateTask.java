package org.example.commands.data.task;

import org.example.commands.Command;
import org.example.data.Response;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.example.data.database.DataBase.connection;

public class CreateTask extends Command {
    private String output = "\n";

    public Response run(ArrayList<String> args) {
        try {
            // Подготавливаем запрос на добавление нового пользователя в таблицу
            String query = "INSERT INTO Tasks (creator_id, title)\n" +
                    "VALUES (?, ?);";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, Integer.parseInt(args.get(0)));
            statement.setString(2, args.subList(1, args.size()).toString());


            // Выполняем запрос на добавление данных
            int rowsAffected = statement.executeUpdate();
            output = "Task added successfully. Rows affected: " + rowsAffected;

            // Закрываем подготовленный запрос
            statement.close();
        } catch (SQLException e) {
            output = "Error: " + e.getMessage();
        }

        // Возвращаем ответ с результатом выполнения команды
        Response ans = new Response(output);
        return ans;
    }}
