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
        // Получение пути к файлу конфигурации из переменной окружения
        String configPath = System.getenv("CONFIG_PATH");
        if (configPath == null || configPath.isEmpty()) {
            configPath = "configs/configs.ini"; // Значение по умолчанию
        }

        Runnable console = new ConsoleMain();

        // Если в аргументах указано "autostart", выполняем автозапуск
        if (args.length != 0 && args[0].equals("autostart")) {
            System.out.println("Starting auto-startup...");
            Logger.init(configPath);                    // Инициализация логгера
            DataBase.init(configPath);                  // Инициализация базы данных
            TelegramMain.init(configPath);              // Инициализация Telegram API
            Logger.put(new CommandManager().execute(CommandsNames.START).getStringStatus(), LogType.INFO, true);
            console.run();
        } else {
            // Обычный запуск приложения
            Logger.init(configPath);
            DataBase.init(configPath);
            TelegramMain.init(configPath);
            console.run();
        }
    }
}
