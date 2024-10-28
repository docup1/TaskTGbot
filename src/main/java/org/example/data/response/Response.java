package org.example.data.response;

public class Response {
    private final String response;
    private final AnsType type;
    public Response() {
        this.response = "No Response";
        this.type = AnsType.NOT_DEFINED;
    }
    public Response(String response, AnsType type) {
        this.response = response;
        this.type = type;
    }
    public String getResponse() {
        return response;
    }
    public AnsType getType() {
        return type;
    }
    public void print(){
        switch (type){
            case ERROR:
                System.err.printf(response + "\n");
                break;
            case OUT:
                System.out.printf(response + "\n");
                break;
            default:
                System.err.printf(response + "\n");
                System.err.println("Response type is NOT DEFINED");
                break;
        }
    }
}
