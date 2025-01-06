package org.example.control.telegram.pages.helpers;

import org.example.control.telegram.pages.other.Exit;
import org.example.control.telegram.pages.other.StartCommand;
import org.example.control.telegram.pages.promts.*;
import org.example.control.telegram.pages.realizations.*;
import org.example.control.telegram.pages.taskmanipulations.*;
import org.example.control.telegram.pages.ui.*;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import java.util.HashMap;
import java.util.Map;

public class MessageManager {
    private final Map<String, PageLogicInterface> msgs = new HashMap<>();

    {
        msgs.put(PageName.START.getCommand(), new StartCommand());
        msgs.put(PageName.EXIT.getCommand(), new Exit());

        msgs.put(PageName.USER_MENU.getCommand(), new UserMenu());
        msgs.put(PageName.SHOW_INFO.getCommand(), new ShowInfoPage());
        msgs.put(PageName.SHOW_MY_TASKS.getCommand(), new ShowMyTasks());
        msgs.put(PageName.SHOW_MY_DRAFTS.getCommand(), new ShowMyDrafts());
        msgs.put(PageName.SHOW_FINISHED_TASKS.getCommand(), new ShowFinishedTasks());

        msgs.put(PageName.SELECT_TASK.getCommand(), new SelectTask());
        msgs.put(PageName.PUBLIC.getCommand(), new Public());
        msgs.put(PageName.UNPUBLIC.getCommand(), new Unpublic());
        msgs.put(PageName.FINISH.getCommand(), new Finish());
        msgs.put(PageName.EDIT.getCommand(), new Edit());


        msgs.put(PageName.CREATE_TASK.getCommand(), new CreateTaskPrompt());
        msgs.put(PageName.CHANGE_BIO.getCommand(), new BioUpdatePrompt());
        msgs.put(PageName.SET_TITLE.getCommand(), new TitleUpdatePromt());
        msgs.put(PageName.SET_DESCRIPTION.getCommand(), new DescrUpdatePromt());
        msgs.put(PageName.SET_DEADLINE.getCommand(), new DeadlineUpdatePromt());
        msgs.put(PageName.SET_REW.getCommand(), new RewUpdatePromt());

        msgs.put(PageName.SET_BIO_WAITING.getCommand(), new BioUpdate());
        msgs.put(PageName.SET_TITLE_WAITING.getCommand(), new TitleUpdate());
        msgs.put(PageName.SET_TASK_TITLE_WAITING.getCommand(), new CreateTask());
        msgs.put(PageName.SET_DESCRIPTION_WAITING.getCommand(), new DescrUpdate());
        msgs.put(PageName.SET_REW_WAITING.getCommand(), new RewUpdate());
        msgs.put(PageName.SET_DEADLINE_WAITING.getCommand(), new DeadlineUpdate());
    }

    public SendMessage getMsg(String textInput, SendMessage message, long chatId, String userName) {
        String [] msg = textInput.split("_");
        String args = "";
        String command = textInput;
        if (msg.length > 1) {
            command = msg[0];
            args = msg[1];
        }
        return getMsg(command, message, chatId, userName, args);
    }

    public SendMessage getMsg(String textInput, SendMessage message, long chatId, String userName, String args) {
        var result = textInput.split("_");
        PageLogicInterface logic = msgs.get(result[0]);
        if (result.length > 1){
            args = result[1] + " " + args;
        }
        if (logic == null) {
            throw new IllegalArgumentException("Unknown command: " + textInput);
        }
        return logic.generate(message, chatId, userName, args);
    }
}
