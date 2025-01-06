package org.example;

import org.example.data.database.DataBase;
import org.example.data.logger.LogType;
import org.example.data.logger.Logger;
import org.example.control.console.commands.helpers.CommandManager;
import org.example.control.console.commands.helpers.CommandsNames;
import org.example.control.console.ConsoleMain;
import org.example.control.telegram.TelegramMain;

public class Main {
    public static void main(String[] args) {
        String INIT_PASS = "/configs.ini";
        Runnable console = new ConsoleMain();

        if (args.length != 0) {INIT_PASS = args[0];}

        if(args.length != 0 && args[0].equals("autostart")){
            System.out.println("Starting auto-startup...");
            String PASS = "configs/configs.ini";
            Logger.init(PASS);
            DataBase.init(PASS);
            TelegramMain.init(PASS);
            Logger.put(new CommandManager().execute(CommandsNames.START).getStringStatus(), LogType.INFO, true);
            console.run();

        }
        else{
            Logger.init(INIT_PASS);
            DataBase.init(INIT_PASS);
            TelegramMain.init(INIT_PASS);
            console.run();
        }
    }
}
