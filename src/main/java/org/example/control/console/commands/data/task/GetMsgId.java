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

public class GetMsgId extends Command {
    public void run(ArrayList<String> args) {


        int taskID = Integer.parseInt(args.get(0));

        try {
            String query = "SELECT msg_id FROM Tasks WHERE task_id = ?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, taskID);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String msgId = resultSet.getString("msg_id");
                setResponse(new Response(Status.OK, ContentType.TEXT_PLAIN, msgId));
            } else {
                setResponse(new Response(Status.NOT_FOUND, ContentType.TEXT_PLAIN, "Пользователь не найден"));
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            Logger.put("SQL Error: " + e.getMessage(), LogType.ERROR);
            setResponse(new Response(Status.INTERNAL_SERVER_ERROR, ContentType.TEXT_PLAIN, "Ошибка при получении ID пользователя"));
        }
    }
}
