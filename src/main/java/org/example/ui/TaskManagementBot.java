package org.example.ui;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TaskManagementBot extends TelegramLongPollingBot {
    private final String dbUrl = "jdbc:postgresql://localhost:5432/postgres";
    private final String dbUser = "docup";
    private final String dbPassword = "docup2005";

    @Override
    public String getBotUsername() {
        return "@GeeksKingdomBot";
    }

    @Override
    public String getBotToken() {
        return "1133203115:AAFVAIMojgNIllfNiTnTphTydQdZMm63R1g";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String userMessage = message.getText();
            String response;

            try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
                int userId = message.getFrom().getId().intValue(); // Преобразование userId в Integer

                if (userMessage.startsWith("/newtask")) {
                    response = handleNewTaskCommand(userMessage, userId, conn); // Передаем userId в handleNewTaskCommand
                } else if (userMessage.startsWith("/mytasks")) {
                    response = handleMyTasksCommand(userId, conn);
                } else if (userMessage.startsWith("/applytask")) {
                    response = handleApplyTaskCommand(userMessage, userId, conn);
                } else {
                    response = "Неизвестная команда. Попробуйте /newtask, /mytasks или /applytask.";
                }
            } catch (Exception e) {
                response = "Произошла ошибка при выполнении команды: " + e.getMessage();
            }

            sendResponse(message.getChatId(), response);
        }
    }

    private void sendResponse(Long chatId, String response) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(response);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private String handleNewTaskCommand(String userMessage, int userId, Connection conn) throws Exception {
        String[] parts = userMessage.split(" ", 3); // /newtask <title> <description>
        if (parts.length < 3) {
            return "Неправильный формат. Используйте: /newtask <название> <описание>";
        }
        String title = parts[1];
        String description = parts[2];

        String sql = "INSERT INTO Tasks (creator_id, title, description) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId); // Используем userId в качестве creator_id
            stmt.setString(2, title);
            stmt.setString(3, description);
            stmt.executeUpdate();
        }
        return "Задача создана!";
    }

    private String handleMyTasksCommand(int userId, Connection conn) throws Exception {
        StringBuilder response = new StringBuilder("Ваши задачи:\n");
        String sql = "SELECT task_id, title, status FROM Tasks WHERE creator_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                response.append("ID: ").append(rs.getInt("task_id"))
                        .append(", Название: ").append(rs.getString("title"))
                        .append(", Статус: ").append(rs.getString("status"))
                        .append("\n");
            }
        }
        return response.length() > 0 ? response.toString() : "У вас пока нет задач.";
    }

    private String handleApplyTaskCommand(String userMessage, int userId, Connection conn) throws Exception {
        String[] parts = userMessage.split(" ");
        if (parts.length != 2) {
            return "Неправильный формат. Используйте: /applytask <task_id>";
        }
        int taskId = Integer.parseInt(parts[1]);

        String sql = "INSERT INTO TaskApplications (task_id, applicant_id) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, taskId);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
        return "Заявка на выполнение задачи отправлена!";
    }
}
