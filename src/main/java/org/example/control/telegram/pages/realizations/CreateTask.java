package org.example.control.telegram.pages.realizations;

import org.example.control.console.commands.helpers.CommandManager;
import org.example.control.console.commands.helpers.CommandsNames;
import org.example.control.telegram.pages.helpers.MessageManager;
import org.example.control.telegram.pages.helpers.PageLogicInterface;
import org.example.data.logger.LogType;
import org.example.data.logger.Logger;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.ArrayList;
import java.util.Arrays;

public class CreateTask implements PageLogicInterface {
    public SendMessage generate(SendMessage message, long chatId, String userName, String args) {
        var arg = new ArrayList<String>();
        var userID = CommandManager.execute(CommandsNames.GET_USER, new ArrayList<>(Arrays.asList(userName))).getData().toString();
        arg.add(userID);
        arg.add(args);
        var res = CommandManager.execute(CommandsNames.CREATE_TASK, arg);
        Logger.put("Executed command: " + CommandsNames.CREATE_TASK + " " + res.getStringStatus(), LogType.INFO);

        var LastTask = CommandManager.execute(CommandsNames.TASK_COUNT,new ArrayList<>(Arrays.asList(userID, "draft"))).getData().toString();

        Logger.put(LastTask, LogType.DEBUG);
        return new MessageManager().getMsg("EDIT", message, chatId, userName, "draft " + LastTask);
    }
}
