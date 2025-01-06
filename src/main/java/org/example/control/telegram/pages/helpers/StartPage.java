package org.example.control.telegram.pages.helpers;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StartPage {
    public ReplyKeyboardMarkup draw() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(new KeyboardRow(Arrays.asList(new KeyboardButton("Меню"), new KeyboardButton("Новая задача"))));
        keyboard.add(new KeyboardRow(Arrays.asList(new KeyboardButton("Мои задачи"), new KeyboardButton("История"), new KeyboardButton("Черновики"))));
        //keyboard.add(new KeyboardRow(Arrays.asList(new KeyboardButton("Посмотреть профиль пользователя"))));
        keyboard.add(new KeyboardRow(Arrays.asList(new KeyboardButton("Информация"))));
        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }
}
