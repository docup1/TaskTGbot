package org.example.ui;

import org.example.data.database.Connect;
import org.example.data.logger.LogType;
import org.example.data.logger.Logger;
import org.example.data.response.Response;
import org.example.managers.CommandConverter;
import org.example.managers.CommandMannager;

import java.util.Scanner;

public class Console implements Runnable{
    private Scanner scanner;
    private CommandConverter converter;
    private CommandMannager mannager;
    private Response response;
    private Logger logger;
    public Console() {
        response = new Response();
        logger = new Logger();
        converter = new CommandConverter();
        mannager = new CommandMannager();

        logger.setDebug(true);
    }
    @Override
    public void run() {
        while (true){
            try {
                scanner = new Scanner(System.in);
                converter.run(scanner.nextLine());
                if (converter.getCommandName() != null) {
                    response = mannager.execute(converter.getCommandName(), converter.getArgs());
                    logger.setLog(response.getResponse(), LogType.INFO);
                }
                else {
                    response = new Response("Command not found.");
                    logger.setLog(response.getResponse(), LogType.WARN);
                }

            }catch (Exception e){
                logger.setLog(e.getMessage(), LogType.ERROR);
                logger.print();
                System.exit(0);
            }
            logger.print();
        }
    }
}
