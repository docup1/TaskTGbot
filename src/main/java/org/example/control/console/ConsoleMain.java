package org.example.control.console;

import org.example.control.console.commands.helpers.CommandConverter;
import org.example.data.logger.LogType;
import org.example.data.logger.Logger;
import org.example.data.response.Response;
import org.example.control.console.commands.helpers.CommandManager;

import java.util.Scanner;

public class ConsoleMain implements Runnable{
    private Scanner scanner;
    private CommandConverter converter;
    private Response response;
    public ConsoleMain() {
        response = new Response();
        converter = new CommandConverter();
    }
    @Override
    public void run() {
        Logger.put("Console started successfully", LogType.INFO, true);
        while (true){
            try {
                scanner = new Scanner(System.in);
                converter.run(scanner.nextLine());
                if (converter.getCommandName() != null) {
                    var command = converter.getCommandName();
                    var args = converter.getArgs();
                    var output = "";
                    response = CommandManager.execute(command, args);
                    output += "Executed command: " + command + " " + response.getStatus();
                    Logger.put(output, LogType.INFO, true);
                    Logger.put("Command data: " + response.getData().toString(), LogType.INFO, true);
                }
                else {
                    Logger.put("Command not found.", LogType.WARN, true);
                }
            }catch (Exception e){
                Logger.put(e.getMessage(), LogType.ERROR, true);
            }
        }
    }
}
