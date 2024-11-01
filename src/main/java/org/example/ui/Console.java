package org.example.ui;

import org.example.data.logger.LogType;
import org.example.data.logger.Logger;
import org.example.data.Response;
import org.example.managers.CommandConverter;
import org.example.managers.CommandMannager;

import java.util.Scanner;

public class Console implements Runnable{
    private Scanner scanner;
    private CommandConverter converter;
    private CommandMannager mannager;
    private Response response;
    public Console() {
        response = new Response();
        converter = new CommandConverter();
        mannager = new CommandMannager();
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
                    response = mannager.execute(command, args);
                    output += "Executed command: " + command + " Command response: " + response.getResponse();
                    Logger.put(output, LogType.INFO, true);
                }
                else {
                    response = new Response("Command not found.");
                    Logger.put(response.getResponse(), LogType.WARN, true);
                }
            }catch (Exception e){
                Logger.put(e.getMessage(), LogType.ERROR, true);
            }
        }
    }
}
