package org.example;

import org.example.data.Response;
import org.example.data.logger.LogType;
import org.example.data.logger.Logger;
import org.example.managers.CommandConverter;
import org.example.managers.CommandMannager;


public class Main {
    public static void main(String[] args) {
        Logger logger = new Logger();
        logger.setDebug(true);
        try {
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
                Logger.print(output, LogType.INFO);
            }

            else {
                response = new Response("Command not found.");
                Logger.print(response.getResponse(), LogType.WARN);
            }
        }catch (Exception e){
            Logger.print(e.getMessage(), LogType.ERROR);
        }

    }
}
