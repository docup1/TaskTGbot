package org.example.managers;

import org.example.commands.*;
import org.example.commands.data.user.AddUser;
import org.example.commands.data.task.CreateTask;
import org.example.commands.server.Exit;
import org.example.commands.server.PrintLogs;
import org.example.commands.server.Start;
import org.example.commands.server.StartConsole;
import org.example.data.Response;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class CommandMannager {
    private LinkedHashMap<CommandsNames, Command> CommandList;
    public CommandMannager() {
        CommandList = new LinkedHashMap<CommandsNames, Command>();
        CommandList.put(CommandsNames.ADD_USER, new AddUser());
        CommandList.put(CommandsNames.CREATE_TASK, new CreateTask());
        CommandList.put(CommandsNames.EXIT, new Exit());
        CommandList.put(CommandsNames.START, new Start());
        CommandList.put(CommandsNames.START_CONS, new StartConsole());
        CommandList.put(CommandsNames.PRINT_LOGS, new PrintLogs());
    }
    public Response execute(CommandsNames command) {
        return CommandList.get(command).run(new ArrayList<String>());
    }
    public Response execute(CommandsNames command, ArrayList<String> args) {
        return CommandList.get(command).run(args);
    }
}
