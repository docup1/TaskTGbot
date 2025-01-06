package org.example.control.telegram.pages.realizations;

import org.example.control.console.commands.helpers.CommandManager;
import org.example.control.console.commands.helpers.CommandsNames;
import org.example.control.telegram.pages.helpers.MessageManager;
import org.example.control.telegram.pages.helpers.PageLogicInterface;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.ArrayList;
import java.util.Arrays;

public class BioUpdate implements PageLogicInterface {
    public SendMessage generate(SendMessage message, long chatId, String userName, String newBio) {
        CommandManager.execute(CommandsNames.UPDATE_BIO, new ArrayList<>(Arrays.asList(userName, newBio)));
        message = new MessageManager().getMsg("Меню", message, chatId, userName, "");
        return message;
    }
}
