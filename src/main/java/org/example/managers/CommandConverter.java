package org.example.managers;

import org.example.data.logger.LogType;
import org.example.data.logger.Logger;

import java.util.ArrayList;

public class CommandConverter {
    private CommandsNames commandName;
    private ArrayList<String> args;
    public CommandConverter(){
        args = new ArrayList<>();
    }
    public void run(String input){
        try {
            String com[] = input.split(" ");
            commandName = CommandsNames.fromString(com[0]);
            for(int i = 1; i < com.length; i++){
                args.add(com[i]);
            }
            Logger.print("Command converted.", LogType.TRACE);
            Logger.print("Command name: " + commandName, LogType.TRACE);
            Logger.print("Command arguments: " + args.toString(), LogType.TRACE);
        }
        catch (IllegalArgumentException e){
            Logger.print("Command convertion field.", LogType.TRACE);
            Logger.print(e.getMessage(), LogType.TRACE);
            commandName = null;
        }
        catch (Exception e){
            Logger.print(e.getMessage(), LogType.DEBUG_FATAL);
        }

    }
    public CommandsNames getCommandName(){
        return commandName;
    }
    public ArrayList<String> getArgs(){
        return args;
    }
}
