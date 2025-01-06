package org.example.control.telegram.pages.taskmanipulations;

import org.example.control.console.commands.helpers.CommandManager;
import org.example.control.console.commands.helpers.CommandsNames;
import org.example.control.telegram.TelegramMain;
import org.example.control.telegram.pages.helpers.PageLogicInterface;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.ArrayList;
import java.util.Arrays;

public class Finish implements PageLogicInterface {
    public SendMessage generate(SendMessage message, long chatId, String userName, String args) {

        var userId = CommandManager.execute(CommandsNames.GET_USER, new ArrayList<>(Arrays.asList(userName))).getData().toString();
        var taskId = CommandManager.execute(CommandsNames.GET_TASK, new ArrayList<>(Arrays.asList(userId, args.split(" ")[0], args.split(" ")[1]))).getData().toString();
        var msgId = CommandManager.execute(CommandsNames.GET_MSG_ID, new ArrayList<>(Arrays.asList(taskId))).getData().toString();
        CommandManager.execute(CommandsNames.SET_STATUS, new ArrayList<>(Arrays.asList(taskId, "completed"))).getData().toString();
        var task = CommandManager.execute(CommandsNames.GET_TASK_BY_ID, new ArrayList<>(Arrays.asList(taskId))).getData().toString();
        message.setText("Finished:\n\n" + task);
        TelegramMain.editMessage(msgId, "<s>" + task + "</s>");
        return message;
    }
}