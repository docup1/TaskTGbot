package org.example.commands.server;

import org.example.commands.Command;
import org.example.data.database.DataBase;
import org.example.data.logger.LogType;
import org.example.data.logger.Logger;
import org.example.data.Response;

import java.util.ArrayList;

public class Exit extends Command {
    @Override
    public Response run(ArrayList<String> args) {
        new DataBase().closeConnection();
        Logger.print("Server stopping...", LogType.INFO);
        System.exit(0);
        return ans;
    }
}
