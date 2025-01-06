package org.example.control.telegram.pages.ui;

import org.example.control.telegram.pages.helpers.PageLogicInterface;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShowInfoPage implements PageLogicInterface {
    public SendMessage generate(SendMessage message, long chatId, String userName, String args) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        InlineKeyboardButton infoButton = new InlineKeyboardButton();
        infoButton.setText("Подробнее об информации");
        infoButton.setUrl("https://google.com"); // URL для страницы

        buttons.add(Collections.singletonList(infoButton));
        markup.setKeyboard(buttons);

        message.setText("Нажмите кнопку ниже для получения дополнительной информации:");
        message.setReplyMarkup(markup);
        return message;
    }
}
