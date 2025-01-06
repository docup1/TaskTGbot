package org.example.control.telegram.chat;

import org.example.data.logger.LogType;
import org.example.data.logger.Logger;

import java.util.ArrayList;
import java.util.List;

public class TelegramCommandConverter {
    private String commandName;
    private String args;
    private String firstArg;
    private boolean addFirst = false;

    public void run(String input) {
        run(input, false);
    }

    public void run(String input, boolean addFirst) {
        this.addFirst = addFirst;
        if (input == null || input.isBlank()) {
            Logger.put("Input command is empty or null.", LogType.WARN);
            commandName = null;
            args = "";
            firstArg = "";
            return;
        }

        try {
            String[] com = input.trim().split("\\s+");
            if (com.length == 0) {
                Logger.put("Command not found in input: " + input, LogType.WARN);
                commandName = null;
                return;
            }

            commandName = com[0];
            StringBuilder argsBuilder = new StringBuilder();

            if (addFirst && com.length > 1) {
                firstArg = com[1];
                for (int i = 2; i < com.length; i++) {
                    argsBuilder.append(com[i]).append(" ");
                }
            } else {
                for (int i = 1; i < com.length; i++) {
                    argsBuilder.append(com[i]).append(" ");
                }
            }

            args = argsBuilder.toString().trim();
            Logger.put("Command converted successfully.", LogType.TRACE);
            Logger.put("Command name: " + commandName, LogType.TRACE);
            Logger.put("Command arguments: " + args, LogType.TRACE);

        } catch (IllegalArgumentException e) {
            Logger.put("Command conversion failed: Unknown command name.", LogType.WARN);
            Logger.put(e.getMessage(), LogType.DEBUG);
            commandName = null;
        } catch (Exception e) {
            Logger.put("Unexpected error during command conversion: " + e.getMessage(), LogType.ERROR);
            Logger.put(e.toString(), LogType.DEBUG);
            commandName = null;
        }
    }

    public String getCommandName() {
        return commandName;
    }

    public List<String> getArgs() {
        List<String> list = new ArrayList<>();
        if (addFirst && firstArg != null) {
            list.add(firstArg);
        }
        if (args != null && !args.isBlank()) {
            list.add(args);
        }
        Logger.put("Final command arguments: " + list, LogType.TRACE);
        return list;
    }
}
