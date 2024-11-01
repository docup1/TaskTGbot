package org.example.ui;

import org.example.data.logger.LogType;
import org.example.data.logger.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Telegram extends TelegramLongPollingBot implements Runnable{

    @Override
    public String getBotUsername() {
        return "@GeeksKingdomBot"; // Укажите имя пользователя бота
    }

    @Override
    public String getBotToken() {
        return "1133203115:AAFVAIMojgNIllfNiTnTphTydQdZMm63R1g"; // Вставьте ваш токен
    }

    @Override
    public void onUpdateReceived(Update update) {
        // Проверка, пришло ли сообщение
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            Logger.put("ChatID: " + chatId, LogType.INFO);
            // Ответное сообщение
            String responseText = "Msg: " + messageText;
            Logger.put(responseText, LogType.INFO);

            // Создание сообщения
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));
            message.setText(responseText);

            // Отправка сообщения
            try {
                execute(message); // Отправка сообщения пользователю
            } catch (TelegramApiException e) {
                Logger.put(e.getMessage(), LogType.ERROR);
            }
        }
    }

    public void run() {
        // Инициализация API Telegram
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new Telegram());
        } catch (TelegramApiException e) {
            Logger.put(e.getMessage(), LogType.ERROR);
        }
    }
}
