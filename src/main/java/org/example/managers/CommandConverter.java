package org.example.managers;

import java.util.ArrayList;

public class CommandConverter {
    private CommandsNames commandName;
    private ArrayList<String> args;
    public CommandConverter(){
        args = new ArrayList<>();
    }
    public void run(String input){
        String com[] = input.split(" ");
        try {
            commandName = CommandsNames.fromString(com[0]);
        }
        catch (IllegalArgumentException e){
            commandName = null;
        }
        for(int i = 1; i < com.length; i++){
            args.add(com[i]);
        }
    }
    public CommandsNames getCommandName(){
        return commandName;
    }
    public ArrayList<String> getArgs(){
        return args;
    }
}
