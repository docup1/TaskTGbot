package org.example.control.telegram.pages.taskmanipulations;

import org.example.control.console.commands.helpers.CommandManager;
import org.example.control.console.commands.helpers.CommandsNames;
import org.example.control.telegram.pages.helpers.PageLogicInterface;
import org.example.data.logger.LogType;
import org.example.data.logger.Logger;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Edit implements PageLogicInterface {
    public SendMessage generate(SendMessage message, long chatId, String userName, String args) {

        var a = args.split(" ");
        Logger.put(args.toString(), LogType.DEBUG);
        try {
            Integer.parseInt(a[1]);
        }
        catch (NumberFormatException e) {
            message.setText("Неоюходимо выбрать задачу из списка.");
            return message;
        }

        // Получение ID пользователя
        var userID = CommandManager.execute(CommandsNames.GET_USER, new ArrayList<>(Arrays.asList(userName)))
                .getData()
                .toString();
        // Выполнение команды для получения задач пользователя
        var res = CommandManager.execute(CommandsNames.MY_TASKS, new ArrayList<>(Arrays.asList(userID, a[0], a[1])));

        message.setText(res.getData().toString());
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        InlineKeyboardButton button1;
        InlineKeyboardButton button2;

        button1 = new InlineKeyboardButton();
        button1.setText("Название");
        button1.setCallbackData("SETTITLE_" + args);

        button2 = new InlineKeyboardButton();
        button2.setText("Описание");
        button2.setCallbackData("SETDESCR_" + args);

        buttons.add(Arrays.asList(button1, button2));

        button1 = new InlineKeyboardButton();
        button1.setText("Дедлайн");
        button1.setCallbackData("SETDEADLINE_" + args);

        button2 = new InlineKeyboardButton();
        button2.setText("Вознаграждение");
        button2.setCallbackData("SETREW_" + args);

        buttons.add(Arrays.asList(button1, button2));

        button1 = new InlineKeyboardButton();
        button1.setText("Опубликовать");
        button1.setCallbackData("PUBLIC_" + args);

        button2 = new InlineKeyboardButton();
        button2.setText("Удалить");
        button2.setCallbackData("REMOVE_" + args);

        //buttons.add(Collections.singletonList(button2));
        buttons.add(Collections.singletonList(button1));

        markup.setKeyboard(buttons);

        message.setReplyMarkup(markup);
        return message;
    }
}
