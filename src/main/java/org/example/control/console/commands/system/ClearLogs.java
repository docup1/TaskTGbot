package org.example.control.console.commands.system;

import org.example.control.console.commands.Command;
import org.example.data.logger.Logger;

import java.util.ArrayList;

public class ClearLogs extends Command {
    @Override
    public void run(ArrayList<String> args) {
        Logger.clear();

    }
}
