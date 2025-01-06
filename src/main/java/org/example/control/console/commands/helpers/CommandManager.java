package org.example.control.console.commands.helpers;

import org.example.control.console.commands.Command;
import org.example.control.console.commands.data.task.*;
import org.example.control.console.commands.data.task.set.*;
import org.example.control.console.commands.data.user.AddUser;
import org.example.control.console.commands.data.user.GetUserId;
import org.example.control.console.commands.data.user.ShowUserInf;
import org.example.control.console.commands.data.user.UpdateBio;
import org.example.control.console.commands.system.*;
import org.example.data.response.Response;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class CommandManager {
    private static LinkedHashMap<CommandsNames, Command> CommandList;

    public CommandManager() {
        CommandList = new LinkedHashMap<>();
        // Команды для работы с пользователями
        CommandList.put(CommandsNames.ADD_USER, new AddUser());
        CommandList.put(CommandsNames.UPDATE_BIO, new UpdateBio());
        CommandList.put(CommandsNames.GET_USER, new GetUserId());
        CommandList.put(CommandsNames.USER_INF, new ShowUserInf());

        // Команды для работы с задачами
        CommandList.put(CommandsNames.CREATE_TASK, new CreateTask());
        CommandList.put(CommandsNames.MY_TASKS, new ShowMyTasks());
        CommandList.put(CommandsNames.GET_TASK, new GetTaskIdByNum());
        CommandList.put(CommandsNames.GET_TASK_BY_ID, new GetTaskById());
        CommandList.put(CommandsNames.GET_MSG_ID, new GetMsgId());
        CommandList.put(CommandsNames.SET_REWARD, new SetReward());
        CommandList.put(CommandsNames.SET_DESCRIPTION, new SetDescription());
        CommandList.put(CommandsNames.SET_DEADLINE, new SetDeadline());
        CommandList.put(CommandsNames.SET_MSG_ID, new SetMsgID());
        CommandList.put(CommandsNames.SET_STATUS, new SetStatus());
        CommandList.put(CommandsNames.SET_TITLE, new SetTitle());
        CommandList.put(CommandsNames.TASK_COUNT, new CountTask());
        CommandList.put(CommandsNames.GET_TASK_DETAILS_BY_ID, new GetTaskDetailsById());

        // Системные команды
        CommandList.put(CommandsNames.START, new Start());
        CommandList.put(CommandsNames.START_CONS, new StartConsole());
        CommandList.put(CommandsNames.EXIT, new Exit());
        CommandList.put(CommandsNames.PRINT_LOGS, new PrintLogs());
        CommandList.put(CommandsNames.SEARCH_LOGS, new SearchLogs());
        CommandList.put(CommandsNames.CLEAR_LOGS, new ClearLogs());
        CommandList.put(CommandsNames.CONNECT, new Connect());
    }

    public static Response execute(CommandsNames command) {
        return execute(command, new ArrayList<>());
    }

    public static Response execute(CommandsNames command, ArrayList<String> args) {
        Command commandToExecute = CommandList.get(command);
        if (commandToExecute == null) {
            return new Response();
        }
        commandToExecute.run(args);
        return commandToExecute.getResponse();
    }
}
