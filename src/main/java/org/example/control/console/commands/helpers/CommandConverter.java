package org.example.control.console.commands.helpers;

import org.example.data.logger.LogType;
import org.example.data.logger.Logger;

import java.util.ArrayList;

public class CommandConverter {
    private CommandsNames commandName;
    private ArrayList<String> args;
    public void run(String input){
        try {
            String com[] = input.split(" ");
            args = new ArrayList<>();
            commandName = CommandsNames.fromString(com[0]);
            for(int i = 1; i < com.length; i++){
                args.add(com[i]);
            }
            Logger.put("Command converted.", LogType.TRACE);
            Logger.put("Command name: " + commandName, LogType.TRACE);
            Logger.put("Command arguments: " + args.toString(), LogType.TRACE);
        }
        catch (IllegalArgumentException e){
            Logger.put("Command convertion field.", LogType.TRACE);
            Logger.put(e.getMessage(), LogType.TRACE);
            commandName = null;
        }
        catch (Exception e){
            Logger.put(e.getMessage(), LogType.ERROR);
        }

    }
    public CommandsNames getCommandName(){
        return commandName;
    }
    public ArrayList<String> getArgs(){
        return args;
    }
}
