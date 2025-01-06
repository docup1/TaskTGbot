package org.example.control.console.commands.system;

import org.example.control.console.commands.Command;
import org.example.data.response.Response;
import org.example.data.database.DataBase;
import org.example.data.logger.LogType;
import org.example.data.logger.Logger;
import org.example.control.telegram.TelegramMain;

import java.util.ArrayList;

public class Start extends Command {
    @Override
    public void run(ArrayList<String> args) {
        try {
            DataBase db = new DataBase();
            db.connect();

            // Получение экземпляра TelegramMain через getInstance()
            TelegramMain tgBot = TelegramMain.getInstance();
            tgBot.run();
        }
        catch (Exception e) {
            Logger.put(e.getMessage(), LogType.ERROR);
        }
        setResponse(new Response());
    }
}

