package org.example.commands;

import org.example.data.response.Response;
import java.util.ArrayList;

public abstract class Command {
    public Response ans = new Response();
    public Response run(ArrayList<String> args){
        return ans;
    }
}
