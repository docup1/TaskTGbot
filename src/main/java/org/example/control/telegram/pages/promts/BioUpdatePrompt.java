package org.example.control.telegram.pages.promts;


import org.example.control.telegram.pages.helpers.DopTextTracker;
import org.example.control.telegram.pages.helpers.PageLogicInterface;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BioUpdatePrompt implements PageLogicInterface {
    public SendMessage generate(SendMessage message, long chatId, String userName, String args) {
        message.setText("Напишите новое описание для вашего профиля:");

        DopTextTracker.SetArgsNeded(chatId, "SETBIO-WAITING");
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        InlineKeyboardButton button;

        button = new InlineKeyboardButton();
        button.setText("Отмена");
        button.setCallbackData("EXIT_" + "Меню");
        buttons.add(Collections.singletonList(button));

        markup.setKeyboard(buttons);

        message.setReplyMarkup(markup);
        return message;
    }
}