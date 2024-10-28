package org.example.ui;

import org.example.data.database.Connect;
import org.example.data.response.AnsType;
import org.example.data.response.Response;
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
        while (true){
            new Connect().run();
            try {
                scanner = new Scanner(System.in);
                converter.run(scanner.nextLine());
                if (converter.getCommandName() != null)
                    response = mannager.execute(converter.getCommandName(), converter.getArgs());
                else
                    response = new Response("Command not found.", AnsType.ERROR);
                response.print();
            }catch (Exception e){
                e.printStackTrace();
                System.exit(0);
            }
        }
    }
}
