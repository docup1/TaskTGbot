package org.example;

import org.example.data.Response;
import org.example.data.logger.LogType;
import org.example.data.logger.Logger;
import org.example.managers.CommandConverter;
import org.example.managers.CommandMannager;

import java.io.BufferedReader;


public class Main {

    public static void main(String[] args) {

        try {
            Logger.init("config/logger.ini");
            String input = "";
            CommandMannager mannager = new CommandMannager();
            CommandConverter converter = new CommandConverter();
            Response response;
            for (int i = 0; i < args.length; i++) {
                input += args[i] + " ";
            }
            converter.run(input);
            if(converter.getCommandName() != null) {
                var command = converter.getCommandName();
                var arguments = converter.getArgs();
                var output = "";
                response = mannager.execute(command, arguments);
                output += "Executed command: " + command + " Command response: " + response.getResponse();
                Logger.put(output, LogType.INFO);
            }

            else {
                response = new Response("Command not found.");
                Logger.put(response.getResponse(), LogType.WARN);
            }
        }catch (Exception e){
            Logger.put(e.getMessage(), LogType.ERROR);
        }

    }

}
