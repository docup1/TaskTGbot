package org.example.control.console.commands.system;

import org.example.control.console.commands.Command;
import org.example.data.database.DataBase;
import org.example.data.logger.LogType;
import org.example.data.logger.Logger;
import org.example.data.response.Response;

import java.util.ArrayList;

public class Exit extends Command {
    @Override
    public void run(ArrayList<String> args) {
        new DataBase().closeConnection();
        Logger.put("Server stopping...", LogType.INFO);
        System.exit(0);
        setResponse(new Response());
    }
}
