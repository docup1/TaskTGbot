package org.example.control.console.commands.system;

import org.example.control.console.commands.Command;
import org.example.data.response.Response;
import org.example.data.logger.Logger;

import java.util.ArrayList;

public class PrintLogs extends Command {
    @Override
    public void run(ArrayList<String> args) {
        Logger.printLogs();
        setResponse(new Response());
    }
}
