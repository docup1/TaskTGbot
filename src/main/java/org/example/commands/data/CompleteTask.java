package org.example.commands.data;

import org.example.commands.Command;
import org.example.data.Response;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.example.data.database.DataBase.connection;

public class CompleteTask extends Command {
    private String output = "\n";

    public Response run(ArrayList<String> args) {
        try {
            // Подготавливаем запрос на добавление нового пользователя в таблицу
            String query = "INSERT INTO Tasks (creator_id, title, description, due_date, reward)\n" +
                    "VALUES (?, ?, ?, ?, ?);";
            PreparedStatement statement = connection.prepareStatement(query);

            // Передаем значения для creator_id и bio
            statement.setString(1, args.get(0)); // Преобразуем creator_id в int
            statement.setString(2, args.get(1)); // bio как строку

            // Выполняем запрос на добавление данных
            int rowsAffected = statement.executeUpdate();
            output = "User added successfully. Rows affected: " + rowsAffected;

            // Закрываем подготовленный запрос
            statement.close();
        } catch (SQLException e) {
            output = "Error: " + e.getMessage();
        }

        // Возвращаем ответ с результатом выполнения команды
        Response ans = new Response(output);
        return ans;
    }
}
