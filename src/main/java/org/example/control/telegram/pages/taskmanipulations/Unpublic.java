package org.example.control.telegram.pages.taskmanipulations;

import org.example.control.console.commands.helpers.CommandManager;
import org.example.control.console.commands.helpers.CommandsNames;
import org.example.control.telegram.TelegramMain;
import org.example.control.telegram.pages.helpers.MessageManager;
import org.example.control.telegram.pages.helpers.PageLogicInterface;
import org.example.data.logger.Logger;
import org.example.data.logger.LogType;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.ArrayList;
import java.util.Arrays;

public class Unpublic implements PageLogicInterface {

    @Override
    public SendMessage generate(SendMessage message, long chatId, String userName, String args) {
        Logger.put("Starting Unpublic.generate for user: " + userName + " with args: " + args, LogType.INFO);

        try {
            // Получение userId по userName
            Logger.put("Fetching user ID for username: " + userName, LogType.DEBUG);
            var userIdResponse = CommandManager.execute(CommandsNames.GET_USER, new ArrayList<>(Arrays.asList(userName)));
            Logger.put("GET_USER command result: " + userIdResponse, LogType.DEBUG);

            if (userIdResponse.getData() == null) {
                Logger.put("User not found: " + userName, LogType.WARN);
                message.setText("Error: User not found.");
                return message;
            }
            var userId = userIdResponse.getData().toString();
            Logger.put("User ID retrieved: " + userId, LogType.INFO);

            // Извлечение аргументов для задачи
            String[] splitArgs = args.split(" ");
            if (splitArgs.length < 2) {
                Logger.put("Insufficient arguments provided: " + args, LogType.WARN);
                message.setText("Error: Not enough arguments provided. Expected: <param1> <param2>");
                return message;
            }

            // Получение taskId по userId и аргументам
            Logger.put("Fetching task ID for userId: " + userId + " with args: " + Arrays.toString(splitArgs), LogType.DEBUG);
            var taskIdResponse = CommandManager.execute(
                    CommandsNames.GET_TASK,
                    new ArrayList<>(Arrays.asList(userId, splitArgs[0], splitArgs[1]))
            );
            Logger.put("GET_TASK command result: " + taskIdResponse, LogType.DEBUG);

            if (taskIdResponse.getData() == null) {
                Logger.put("Task not found for userId: " + userId + ", args: " + Arrays.toString(splitArgs), LogType.WARN);
                message.setText("Error: Task not found.");
                return message;
            }
            var taskId = taskIdResponse.getData().toString();
            Logger.put("Task ID retrieved: " + taskId, LogType.INFO);

            // Установка статуса задачи в "draft"
            Logger.put("Setting task status to 'draft' for taskId: " + taskId, LogType.DEBUG);
            var setStatusResponse = CommandManager.execute(CommandsNames.SET_STATUS, new ArrayList<>(Arrays.asList(taskId, "draft"))).getData().toString();
            Logger.put("SET_STATUS command result: " + setStatusResponse, LogType.DEBUG);

            // Удаление задачи из Telegram-канала
            Logger.put("Fetching message ID for taskId: " + taskId, LogType.DEBUG);
            var messageIdResponse = CommandManager.execute(CommandsNames.GET_MSG_ID, new ArrayList<>(Arrays.asList(taskId)));
            Logger.put("GET_MSG_ID command result: " + messageIdResponse, LogType.DEBUG);

            if (messageIdResponse.getData() != null) {
                String messageId = messageIdResponse.getData().toString();
                Logger.put("Deleting message from Telegram channel. Message ID: " + messageId, LogType.DEBUG);
                TelegramMain.deleteMessage(messageId);
            }

            // Формирование текста ответа
            var result = CommandManager.execute(CommandsNames.GET_TASK_DETAILS_BY_ID, new ArrayList<>(Arrays.asList(taskId))).getData().toString();
            Logger.put(result, LogType.DEBUG);
            message = new MessageManager().getMsg("TASK-TO-SHOW", message, chatId, userName, result);
            Logger.put("Task unpublished successfully for taskId: " + taskId, LogType.INFO);

        } catch (Exception e) {
            Logger.put("An error occurred: " + e.getMessage(), LogType.ERROR);
            message.setText("An error occurred: " + e.getMessage());
        }

        Logger.put("Unpublic.generate execution completed for user: " + userName, LogType.INFO);
        return message;
    }
}
