package org.example.control.console.commands.system;

import org.example.control.console.commands.Command;
import org.example.data.response.Response;
import org.example.control.console.ConsoleMain;

import java.util.ArrayList;

public class StartConsole extends Command {
    @Override
    public void run(ArrayList<String> args) {
        Runnable AdminConsole = new ConsoleMain();
        AdminConsole.run();
        setResponse(new Response());
    }
}
