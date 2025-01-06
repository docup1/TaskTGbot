package org.example.control.telegram.pages.other;

import org.example.control.console.commands.helpers.CommandManager;
import org.example.control.console.commands.helpers.CommandsNames;
import org.example.control.telegram.pages.helpers.PageLogicInterface;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.example.control.telegram.pages.helpers.StartPage;
import java.util.ArrayList;
import java.util.Arrays;

public class StartCommand implements PageLogicInterface {
    public SendMessage generate(SendMessage message, long chatId, String userName, String args) {
        message.setReplyMarkup(new StartPage().draw());
        String response = CommandManager.execute(CommandsNames.ADD_USER, new ArrayList<>(Arrays.asList(userName))).getData().toString();
        message.setText(response);
        return message;
    }
}
