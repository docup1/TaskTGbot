package org.example.control.telegram.pages.other;

import org.example.control.telegram.pages.helpers.DopTextTracker;
import org.example.control.telegram.pages.helpers.MessageManager;
import org.example.control.telegram.pages.helpers.PageLogicInterface;
import org.example.control.telegram.pages.helpers.StartPage;
import org.example.data.logger.LogType;
import org.example.data.logger.Logger;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class Exit implements PageLogicInterface {
    @Override
    public SendMessage generate(SendMessage message, long chatId, String userName, String args) {
        // Убедимся, что трекер данных вызывается
        DopTextTracker.GetArgsNeded(chatId);

        if (!args.isEmpty()) {
            // Разделяем строку на команду и оставшиеся аргументы
            var split = args.split(" ", 2); // Ограничиваем split на максимум 2 части
            var command = split[0]; // Первая часть — команда
            var remainingArgs = (split.length > 1) ? split[1] : ""; // Вторая часть — оставшиеся аргументы, если есть;
            Logger.put(command + " " + remainingArgs, LogType.DEBUG); // Логируем команду и аргументы
            return new MessageManager().getMsg(command, message, chatId, userName, remainingArgs); // Передаем обработанные аргументы
        } else {
            // Если аргументы отсутствуют, возвращаем сообщение об отмене
            message.setText("Действие отменено");
            return message;
        }
    }
}
