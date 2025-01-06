package org.example.control.telegram.pages;

import org.example.control.telegram.pages.helpers.DopTextTracker;
import org.example.control.telegram.pages.helpers.MessageManager;
import org.example.data.logger.LogType;
import org.example.data.logger.Logger;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class Menu {

    private MessageManager messageManager = new MessageManager();


    // Основной обработчик команд
    public SendMessage handleClickResult(String commandInput, long chatId, String userName) {

        messageManager = new MessageManager();


        SendMessage message = new SendMessage();
        message.setParseMode(ParseMode.HTML);
        message.setChatId(String.valueOf(chatId));

        try {
            message = messageManager.getMsg(commandInput, message, chatId, userName);
        }catch (NullPointerException e) {
            message.setText("Неизвестная команда. Пожалуйста, выберите действие из меню.");
        }

        return message;
    }

    // Основной обработчик команд
    public SendMessage handleCommand(String textInput, long chatId, String userName) {

        messageManager = new MessageManager();

        String args = new String();
        SendMessage message = new SendMessage();
        message.setParseMode(ParseMode.HTML);
        message.setChatId(String.valueOf(chatId));

        var task = DopTextTracker.GetArgsNeded(chatId);
        if (task != null) {
            args = textInput;
            textInput = task;
            Logger.put(textInput, LogType.DEBUG);
        }
        try {
            message = messageManager.getMsg(textInput, message, chatId, userName, args);

        }catch (NullPointerException e) {
            message.setText("Неизвестная команда. Пожалуйста, выберите действие из меню.");
        }

        return message;
    }
}
