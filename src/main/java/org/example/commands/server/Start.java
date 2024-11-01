package org.example.commands.server;

import org.example.commands.Command;
import org.example.data.Response;
import org.example.data.database.DataBase;
import org.example.data.logger.LogType;
import org.example.data.logger.Logger;
import org.example.ui.Telegram;

import java.util.ArrayList;

public class Start extends Command {
    @Override
    public Response run(ArrayList<String> args) {
        try {
            DataBase db = new DataBase();
            db.init("config/database.ini");
            db.connect();
            new Telegram().run();
        }
        catch (Exception e) {
            Logger.put(e.getMessage(), LogType.ERROR);
        }
        return ans;
    }
}
