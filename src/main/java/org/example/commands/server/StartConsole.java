package org.example.commands.server;

import org.example.commands.Command;
import org.example.data.Response;
import org.example.ui.Console;

import java.util.ArrayList;

public class StartConsole extends Command {
    @Override
    public Response run(ArrayList<String> args) {
        Runnable AdminConsole = new Console();
        AdminConsole.run();
        return ans;
    }
}
