package org.example.data.response;

public class Response {
    private final String response;
    public Response() {
        this.response = "No Response";
    }
    public Response(String response) {
        this.response = response;
    }
    public String getResponse() {
        return response;
    }
}
