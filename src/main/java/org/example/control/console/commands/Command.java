package org.example.control.console.commands;

import org.example.data.response.Response;
import org.example.data.response.Status;

import java.util.ArrayList;

public abstract class Command {
    private Response ans;
    {
        ans = new Response(Status.NO_CONTENT);
    }
    public void run(ArrayList<String> args){}
    public Response getResponse(){
        return ans;
    }
    public void setResponse(Response ans){
        this.ans = ans;
    }
}
