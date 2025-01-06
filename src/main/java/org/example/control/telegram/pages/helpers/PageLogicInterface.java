package org.example.control.telegram.pages.helpers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface PageLogicInterface {
    SendMessage generate(SendMessage message, long chatId, String userName, String args);
}
