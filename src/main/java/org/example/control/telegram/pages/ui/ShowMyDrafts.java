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

public class ShowMyDrafts implements PageLogicInterface {
        public SendMessage generate(SendMessage message, long chatId, String userName, String args) {
                // Получение ID пользователя
                var userID = CommandManager.execute(CommandsNames.GET_USER, new ArrayList<>(Arrays.asList(userName)))
                        .getData()
                        .toString();
                // Выполнение команды для получения задач пользователя
                var res = CommandManager.execute(CommandsNames.MY_TASKS, new ArrayList<>(Arrays.asList(userID, "draft")));

                // Проверка типа данных и преобразование результата в ArrayList<String>
                ArrayList<String> taskList;
                if (res.getData() instanceof List) {
                        taskList = new ArrayList<>((List<String>) res.getData());
                } else {
                        taskList = new ArrayList<>();
                }

                // Создаем клавиатуру для задач с Inline кнопками
                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
                InlineKeyboardButton taskButton;
                // Добавляем задачи в виде Inline кнопок
                for (int i = 0; i < taskList.size(); i++) {
                        taskButton = new InlineKeyboardButton();
                        taskButton.setText((i + 1) + ") " + taskList.get(i));
                        taskButton.setCallbackData("TASK-TO-SHOW_draft " + (i + 1));  // CallbackData для обработки выбранной задачи
                        buttons.add(Collections.singletonList(taskButton));
                }

                // Устанавливаем Inline клавиатуру
                markup.setKeyboard(buttons);
                message.setText("Список ваших черновиков:");

                // Устанавливаем Inline клавиатуру в сообщение
                message.setReplyMarkup(markup);
                return message;
        }
}
