package org.example.control.telegram.pages.ui;

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

public class UserMenu implements PageLogicInterface {
    public SendMessage generate(SendMessage message, long chatId, String userName, String args) {

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        InlineKeyboardButton button;

        button = new InlineKeyboardButton();
        button.setText("Изменить описание профиля");
        button.setCallbackData("CHANGE-BIO");
        buttons.add(Collections.singletonList(button));
        markup.setKeyboard(buttons);
        message.setReplyMarkup(markup);
        String userInfo = CommandManager.execute(CommandsNames.USER_INF, new ArrayList<>(Arrays.asList(userName))).getData().toString();
        message.setText(userInfo);
        return message;
    }
}
