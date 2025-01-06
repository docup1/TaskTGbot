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

public class UpdateBio extends Command {
    private String output = "\n";

    public void run(ArrayList<String> args) {
        if (args.size() < 2 || args.get(0).isBlank() || args.get(1).isBlank()) {
            output = "Error: User ID and bio cannot be empty.";
            Logger.put(output, LogType.WARN);
            setResponse(new Response(Status.BAD_REQUEST, ContentType.TEXT_PLAIN, "ID пользователя и биография не могут быть пустыми"));
            return;
        }

        try {
            String query = "UPDATE Users SET bio = ? WHERE username = ?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, args.get(1));
            statement.setString(2, args.get(0));

            int rowsAffected = statement.executeUpdate();
            output = "Bio updated successfully. Rows affected: " + rowsAffected;

            statement.close();
            Logger.put(output, LogType.INFO);
            setResponse(new Response(Status.ACCEPTED, ContentType.TEXT_PLAIN, "Биография успешно обновлена"));
        } catch (SQLException e) {
            output = "SQL Error: " + e.getMessage();
            Logger.put(output, LogType.ERROR);
            Logger.put(e.getClass().getSimpleName(), LogType.DEBUG);
            setResponse(new Response(Status.INTERNAL_SERVER_ERROR, ContentType.TEXT_PLAIN, "Ошибка при обновлении биографии"));
        } catch (Exception e) {
            output = "Unexpected Error: " + e.getMessage();
            Logger.put(output, LogType.ERROR);
            Logger.put(e.getClass().getSimpleName(), LogType.DEBUG);
            setResponse(new Response(Status.INTERNAL_SERVER_ERROR, ContentType.TEXT_PLAIN, "Непредвиденная ошибка"));
        }
    }
}