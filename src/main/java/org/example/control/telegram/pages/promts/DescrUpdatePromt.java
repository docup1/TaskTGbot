package org.example.control.telegram.pages.promts;

import org.example.control.telegram.pages.helpers.DopTextTracker;
import org.example.control.telegram.pages.helpers.PageLogicInterface;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DescrUpdatePromt implements PageLogicInterface {
    @Override
    public SendMessage generate(SendMessage message, long chatId, String userName, String args) {
        message.setText("Напишите описание задачи:");

        DopTextTracker.SetArgsNeded(chatId, "SETDESCR-WAITING_" + args);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        InlineKeyboardButton button;

        button = new InlineKeyboardButton();
        button.setText("Отмена");
        button.setCallbackData("EXIT_" + "EDIT " + args);
        buttons.add(Collections.singletonList(button));

        markup.setKeyboard(buttons);

        message.setReplyMarkup(markup);
        return message;
    }
}
