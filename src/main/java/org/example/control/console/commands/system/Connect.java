package org.example.control.console.commands.system;

import org.example.control.console.commands.Command;
import org.example.data.database.DataBase;
import org.example.data.logger.LogType;
import org.example.data.logger.Logger;
import org.example.data.response.Response;
import java.util.ArrayList;

public class Connect extends Command {
    @Override
    public void run(ArrayList<String> args) {
        try {
            DataBase db = new DataBase();
            db.connect();
        }
        catch (Exception e) {
            Logger.put(e.getMessage(), LogType.ERROR);
        }
        setResponse(new Response());
    }
}
