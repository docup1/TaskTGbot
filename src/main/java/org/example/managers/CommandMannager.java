package org.example.managers;

import org.example.commands.AddUser;
import org.example.commands.Command;
import org.example.data.response.Response;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class CommandMannager {
    private LinkedHashMap<CommandsNames, Command> CommandList;
    public CommandMannager() {
        CommandList = new LinkedHashMap<CommandsNames, Command>();
        CommandList.put(CommandsNames.ADD_USER, new AddUser());
    }
    public Response execute(CommandsNames command) {
        return CommandList.get(command).run(new ArrayList<String>());
    }
    public Response execute(CommandsNames command, ArrayList<String> args) {
        return CommandList.get(command).run(args);
    }
}
