package org.example.control.telegram.pages.taskmanipulations;

import org.example.control.console.commands.helpers.CommandManager;
import org.example.control.console.commands.helpers.CommandsNames;
import org.example.control.telegram.TelegramMain;
import org.example.control.telegram.pages.helpers.MessageManager;
import org.example.control.telegram.pages.helpers.PageLogicInterface;
import org.example.data.logger.Logger;
import org.example.data.logger.LogType;
import org.example.data.response.Response;
import org.example.data.response.Status;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.ArrayList;
import java.util.Arrays;

public class Public implements PageLogicInterface {

    @Override
    public SendMessage generate(SendMessage message, long chatId, String userName, String args) {
        Logger.put("Starting Public.generate for user: " + userName + " with args: " + args, LogType.INFO);

        try {
            // Получение userId по userName
            Logger.put("Fetching user ID for username: " + userName, LogType.DEBUG);
            var userIdResponse = CommandManager.execute(CommandsNames.GET_USER, new ArrayList<>(Arrays.asList(userName)));
            Logger.put("GET_USER command result: " + userIdResponse, LogType.DEBUG);

            if (!isStatusOk(userIdResponse)) {
                return handleError(message, "Error: User not found.", userName);
            }
            var userId = userIdResponse.getData().toString();
            Logger.put("User ID retrieved: " + userId, LogType.INFO);

            // Извлечение аргументов для задачи
            String[] splitArgs = args.split(" ");
            if (splitArgs.length < 2) {
                return handleError(message, "Error: Not enough arguments provided. Expected: <param1> <param2>", userName);
            }

            // Получение taskId по userId и аргументам
            Logger.put("Fetching task ID for userId: " + userId + " with args: " + Arrays.toString(splitArgs), LogType.DEBUG);
            var taskIdResponse = CommandManager.execute(
                    CommandsNames.GET_TASK,
                    new ArrayList<>(Arrays.asList(userId, splitArgs[0], splitArgs[1]))
            );
            Logger.put("GET_TASK command result: " + taskIdResponse, LogType.DEBUG);

            if (!isStatusOk(taskIdResponse)) {
                return handleError(message, "Error: Task not found.", userName);
            }
            var taskId = taskIdResponse.getData().toString();
            Logger.put("Task ID retrieved: " + taskId, LogType.INFO);

            // Установка статуса задачи в "open"
            Logger.put("Setting task status to 'open' for taskId: " + taskId, LogType.DEBUG);
            CommandManager.execute(CommandsNames.SET_STATUS, new ArrayList<>(Arrays.asList(taskId, "open")));

            // Получение данных задачи по taskId
            Logger.put("Fetching task data for taskId: " + taskId, LogType.DEBUG);
            var taskResponse = CommandManager.execute(CommandsNames.GET_TASK_BY_ID, new ArrayList<>(Arrays.asList(taskId)));
            Logger.put("GET_TASK_BY_ID command result: " + taskResponse, LogType.DEBUG);

            if (!isStatusOk(taskResponse)) {
                return handleError(message, "Error: Task data could not be retrieved.", userName);
            }
            var task = taskResponse.getData().toString();
            Logger.put("Task data retrieved for taskId: " + taskId, LogType.INFO);

            // Публикация задачи в Telegram-канал
            Logger.put("Publishing task to Telegram channel for taskId: " + taskId, LogType.DEBUG);
            var messageId = TelegramMain.publishMessage(task);
            Logger.put("Task published. Telegram message ID: " + messageId, LogType.INFO);

            // Установка messageId для задачи
            Logger.put("Setting Telegram message ID for taskId: " + taskId, LogType.DEBUG);
            CommandManager.execute(CommandsNames.SET_MSG_ID, new ArrayList<>(Arrays.asList(taskId, messageId)));

            // Формирование текста сообщения для пользователя
            var result = CommandManager.execute(CommandsNames.GET_TASK_DETAILS_BY_ID, new ArrayList<>(Arrays.asList(taskId))).getData().toString();
            return new MessageManager().getMsg("TASK-TO-SHOW", message, chatId, userName, result);

        } catch (Exception e) {
            Logger.put("An error occurred: " + e.getMessage(), LogType.ERROR);
            return handleError(message, "An error occurred: " + e.getMessage(), userName);
        }
    }

    private boolean isStatusOk(Response response) {
        return response != null && response.getStatus() == Status.OK;
    }

    private SendMessage handleError(SendMessage message, String errorMessage, String userName) {
        Logger.put("Error for user: " + userName + " - " + errorMessage, LogType.WARN);
        message.setText(errorMessage);
        return message;
    }
}
