package org.example.control.console.commands.data.task.set;

import org.example.control.console.commands.Command;
import org.example.data.response.ContentType;
import org.example.data.response.Response;
import org.example.data.response.Status;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.example.data.database.DataBase.connection;

public class SetDescription extends Command {
    @Override
    public void run(ArrayList<String> args) {
        if (args.size() < 2) {
            setResponse(new Response(Status.BAD_REQUEST, ContentType.TEXT_PLAIN, "Insufficient arguments. Expected description and task_id."));
            return;
        }

        String query = "UPDATE Tasks SET description = ? WHERE task_id = ?;";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, args.get(1)); // description
            statement.setInt(2, Integer.parseInt(args.get(0))); // task_id

            int rowsAffected = statement.executeUpdate();
            String message = rowsAffected > 0
                    ? "Task description updated successfully. Rows affected: " + rowsAffected
                    : "No rows affected. Please check if the task ID exists.";
            setResponse(new Response(Status.OK, ContentType.TEXT_PLAIN, message));
        } catch (SQLException e) {
            setResponse(new Response(Status.INTERNAL_SERVER_ERROR, ContentType.TEXT_PLAIN, "Database error: " + e.getMessage()));
        } catch (NumberFormatException e) {
            setResponse(new Response(Status.BAD_REQUEST, ContentType.TEXT_PLAIN, "Error: Task ID must be a valid integer."));
        }
    }
}
