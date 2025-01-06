package org.example.control.telegram.pages.realizations;

import org.example.control.console.commands.helpers.CommandManager;
import org.example.control.console.commands.helpers.CommandsNames;
import org.example.control.telegram.pages.helpers.MessageManager;
import org.example.control.telegram.pages.helpers.PageLogicInterface;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.ArrayList;
import java.util.Arrays;

public class DescrUpdate implements PageLogicInterface {
    @Override
    public SendMessage generate(SendMessage message, long chatId, String userName, String args) {
        // Split the input arguments
        String[] splitArgs = args.split(" ");

        // Get the user ID
        String userId = CommandManager.execute(
                CommandsNames.GET_USER,
                new ArrayList<>(Arrays.asList(userName))
        ).getData().toString();

        // Get the task ID
        String taskId = CommandManager.execute(
                CommandsNames.GET_TASK,
                new ArrayList<>(Arrays.asList(userId, splitArgs[0], splitArgs[1]))
        ).getData().toString();

        // Construct the title string by excluding the first two elements
        String title = String.join(" ", Arrays.copyOfRange(splitArgs, 2, splitArgs.length));

        // Set the task title
        String setTitleResponse = CommandManager.execute(
                CommandsNames.SET_DESCRIPTION,
                new ArrayList<>(Arrays.asList(taskId, title))
        ).getData().toString();

        // Set the response message
        message = new MessageManager().getMsg("EDIT_" + splitArgs[0] + " " + splitArgs[1], message, chatId, userName );
        return message;
    }
}
