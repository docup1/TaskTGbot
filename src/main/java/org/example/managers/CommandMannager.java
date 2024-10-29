package org.example.managers;

import org.example.commands.*;
import org.example.commands.data.AddUser;
import org.example.commands.data.GetAllUsers;
import org.example.commands.server.Exit;
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
        CommandList.put(CommandsNames.EXIT, new Exit());
        CommandList.put(CommandsNames.START, new Start());
        CommandList.put(CommandsNames.SHOW_USERS, new GetAllUsers());
        CommandList.put(CommandsNames.START_CONS, new StartConsole());
    }
    public Response execute(CommandsNames command) {
        return CommandList.get(command).run(new ArrayList<String>());
    }
    public Response execute(CommandsNames command, ArrayList<String> args) {
        return CommandList.get(command).run(args);
    }
}
