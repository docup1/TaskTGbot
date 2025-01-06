package org.example.control.telegram.pages.taskmanipulations;

import org.example.control.console.commands.helpers.CommandManager;
import org.example.control.console.commands.helpers.CommandsNames;
import org.example.control.telegram.pages.helpers.PageLogicInterface;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SelectTask implements PageLogicInterface {
    public SendMessage generate(SendMessage message, long chatId, String userName, String args) {
        var a = args.split(" ");
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
        InlineKeyboardButton button;

        switch (a[0]){
            case "open":
                button = new InlineKeyboardButton();
                button.setText("Завершить");
                button.setCallbackData("FINISH_" + args);
                buttons.add(Collections.singletonList(button));

                button = new InlineKeyboardButton();
                button.setText("Снять");
                button.setCallbackData("UNPUBLIC_" + args);
                buttons.add(Collections.singletonList(button));
                break;
            case "draft":
                button = new InlineKeyboardButton();
                button.setText("Удалить");
                button.setCallbackData("REMOVE_" + args);
                //buttons.add(Collections.singletonList(button));

                button = new InlineKeyboardButton();
                button.setText("Изменить");
                button.setCallbackData("EDIT_" + args);
                buttons.add(Collections.singletonList(button));

                button = new InlineKeyboardButton();
                button.setText("Опубликовать");
                button.setCallbackData("PUBLIC_" + args);
                buttons.add(Collections.singletonList(button));
                break;
            case "completed":
                button = new InlineKeyboardButton();
                button.setText("Вернуть");
                button.setCallbackData("CALLBACK_" + args);
                //buttons.add(Collections.singletonList(button));
                break;
        }


        markup.setKeyboard(buttons);

        message.setReplyMarkup(markup);

        return message;
    }

}
