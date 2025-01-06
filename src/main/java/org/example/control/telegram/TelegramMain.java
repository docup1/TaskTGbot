package org.example.control.telegram;

import org.example.control.telegram.pages.Menu;
import org.example.control.telegram.pages.helpers.StartPage;
import org.example.data.logger.LogType;
import org.example.data.logger.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.groupadministration.RestrictChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.ChatPermissions;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TelegramMain extends TelegramLongPollingBot implements Runnable {

    private static TelegramMain instance;

    private static String botUsername;
    private static String botToken;
    private final Menu menu;
    private HashMap<String, Integer> cleaner = new HashMap<>();
    // ExecutorService for multithreading
    private static final int THREAD_POOL_SIZE = 10; // Adjust based on expected load
    private final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    // Restricted chat ID
    private static String TASK_OUTPUT_CHAT_ID = "";

    private TelegramMain() {
        this.menu = new Menu();
    }

    public static TelegramMain getInstance() {
        if (instance == null) {
            instance = new TelegramMain();
        }
        return instance;
    }

    public static void init(String configPath) {
        Properties props = new Properties();
        try (FileInputStream fileInput = new FileInputStream(new File(configPath))) {
            props.load(fileInput);

            botUsername = getRequiredProperty(props, "BOT_USER_NAME");
            botToken = getRequiredProperty(props, "BOT_API_TOKEN");
            TASK_OUTPUT_CHAT_ID = getRequiredProperty(props, "TASK_OUTPUT_CHAT_ID");
            Logger.put("Configuration loaded successfully", LogType.DEBUG);
        } catch (IOException e) {
            Logger.put("Error loading config file: " + e.getMessage(), LogType.ERROR);
            throw new IllegalStateException("Configuration failed to load.", e);
        }
    }

    private static String getRequiredProperty(Properties props, String key) {
        String value = props.getProperty(key);
        if (value == null) {
            throw new IllegalStateException("Missing required configuration: " + key);
        }
        return value;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        // Submit each update to the thread pool for processing
        executorService.submit(() -> {
            try {
                // Check if the update contains a message and is from a restricted chat
                if (update.hasMessage() && update.getMessage().hasText()) {

                    long chatId = update.getMessage().getChatId();
                    Logger.put("ChatId: " + chatId, LogType.DEBUG);
                    deleteMessage(String.valueOf(chatId), String.valueOf(update.getMessage().getMessageId()));

                    if ((chatId) == Long.parseLong(TASK_OUTPUT_CHAT_ID.replaceAll("[^\\d-]", "")) && !update.getMessage().getFrom().getIsBot()) {
                        return; // Stop further processing
                    }
                    //
                    handleMessage(update); // Process normal messages
                } else if (update.hasCallbackQuery()) {
                    long chatId = update.getCallbackQuery().getMessage().getChatId();
                    deleteMessage(String.valueOf(chatId), String.valueOf(cleaner.get(String.valueOf(chatId))));
                    handleCallbackQuery(update.getCallbackQuery());
                }
            } catch (Exception e) {
                Logger.put("Error processing update: " + e.getMessage(), LogType.ERROR);
            }
        });
    }


    private void handleMessage(Update update) {
        Message message = update.getMessage();
        long chatId = message.getChatId();
        String userName = message.getFrom().getUserName();

        Logger.put(String.format("[Message] ChatID: %d, User: %s, Text: %s", chatId, userName, message.getText()), LogType.DEBUG);


        if (userName == null) {
            sendMessage(chatId, "Для использования бота необходимо задать имя пользователя в настройках телеграм.");
            return;
        }

        SendMessage responseMessage = menu.handleCommand(message.getText(), chatId, userName);
        sendMessage(responseMessage);
    }

    private void handleCallbackQuery(CallbackQuery callbackQuery) {
        String callbackData = callbackQuery.getData();
        long chatId = callbackQuery.getMessage().getChatId();
        String userName = callbackQuery.getFrom().getUserName();

        Logger.put(String.format("[Callback] ChatID: %d, User: %s, Data: %s", chatId, userName, callbackData), LogType.DEBUG);

        answerCallbackQuery(callbackQuery, "Ваш запрос обрабатывается...");
        SendMessage responseMessage = menu.handleClickResult(callbackData, chatId, userName);
        sendMessage(responseMessage);
    }


    private static String publishMessage(String chatId, String content) {
        TelegramMain bot = getInstance();
        SendMessage message = new SendMessage(chatId, content);
        message.enableHtml(true);
        try {
            Message sentMessage = bot.execute(message);
            return String.valueOf(sentMessage.getMessageId());
        } catch (TelegramApiException e) {
            Logger.put("Failed to publish message: " + e.getMessage(), LogType.ERROR);
            return null;
        }
    }
    public static String publishMessage(String text){
        return publishMessage(TASK_OUTPUT_CHAT_ID, text);
    }
    private static void deleteMessage(String chatId, String messageId) {
        TelegramMain bot = getInstance();
        try {
            int msgId = Integer.parseInt(messageId);
            bot.execute(new DeleteMessage(chatId, msgId));
        } catch (NumberFormatException | TelegramApiException e) {
            Logger.put("Failed to delete message: " + e.getMessage(), LogType.ERROR);
        }
    }
    public static void deleteMessage(String messageId) {
        deleteMessage(TASK_OUTPUT_CHAT_ID, messageId);
    }
    private static void editMessage(String chatId, String messageId, String newText) {
        TelegramMain bot = getInstance();
        try {
            int msgId = Integer.parseInt(messageId);
            EditMessageText editMessage = new EditMessageText();
            editMessage.enableHtml(true);
            editMessage.setChatId(chatId);
            editMessage.setMessageId(msgId);
            editMessage.setText(newText);
            bot.execute(editMessage);
        } catch (NumberFormatException | TelegramApiException e) {
            Logger.put("Failed to edit message: " + e.getMessage(), LogType.ERROR);
        }
    }
    public static void editMessage( String messageId, String newText) {
        editMessage(TASK_OUTPUT_CHAT_ID, messageId, newText);
    }
    private void sendMessage(long chatId, String text) {
        sendMessage(new SendMessage(String.valueOf(chatId), text));
    }

    private Message sendMessage(SendMessage message) {
        try {
            var msg = execute(message);
            var msgId = msg.getMessageId();
            var chatId = message.getChatId();
            cleaner.put(chatId, msgId);
            return msg;
        } catch (TelegramApiException e) {
            Logger.put("Failed to send message: " + e.getMessage(), LogType.ERROR);
            return null;
        }
    }

    private void answerCallbackQuery(CallbackQuery callbackQuery, String text) {
        AnswerCallbackQuery answer = new AnswerCallbackQuery();
        answer.setCallbackQueryId(callbackQuery.getId());
        answer.setText(text);
        try {
            execute(answer);
        } catch (TelegramApiException e) {
            Logger.put("Failed to answer callback: " + e.getMessage(), LogType.ERROR);
        }
    }




    @Override
    public void run() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
        } catch (TelegramApiException e) {
            Logger.put("Failed to register bot: " + e.getMessage(), LogType.ERROR);
        }
    }

    // Shutdown method for graceful termination
    public void shutdown() {
        executorService.shutdown();
        Logger.put("Bot is shutting down...", LogType.INFO);
    }
}