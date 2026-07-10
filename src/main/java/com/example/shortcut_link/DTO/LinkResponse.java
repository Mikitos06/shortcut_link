package com.example.shortcut_link.DTO;

public class LinkResponse {
    private int id;
    private String message;

    public LinkResponse() {}

    public LinkResponse(int id, String message) {
        this.id = id;
        this.message = message;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
