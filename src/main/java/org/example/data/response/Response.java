package org.example.data.response;

import java.time.Instant;

public class Response {
    private Instant timestamp;
    private Status status;
    private ContentType contentType;
    private Object data;


    // Конструктор по умолчанию
    public Response() {
        this.timestamp = Instant.now();
        this.status = Status.OK;
    }
    public Response(Status status) {
        this.status = status;
        this.timestamp = Instant.now();
    }
    // Конструктор с параметрами
    public Response(Status status, ContentType contentType, Object data ) {
        this.status = status;
        this.contentType = contentType;
        this.data = data;
        this.timestamp = Instant.now();
    }

    public Instant getTimestamp() {
        return timestamp;
    }
    public String getStringStatus() {
        return "Response status: " + status.getCode() + " - " + status.getMessage() + ".";
    }
    public Status getStatus() {
        return status;
    }
    public ContentType getContentType() {
        return contentType;
    }
    public Object getData(){
        if (data != null) return data;
        else return "null";
    }
}