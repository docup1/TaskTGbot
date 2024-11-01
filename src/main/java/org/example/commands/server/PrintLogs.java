package org.example.commands.server;

import org.example.commands.Command;
import org.example.data.Response;
import org.example.data.logger.Logger;

import java.util.ArrayList;

public class PrintLogs extends Command {
    @Override
    public Response run(ArrayList<String> args) {
        Logger.printLogs();
        return ans;
    }
}
