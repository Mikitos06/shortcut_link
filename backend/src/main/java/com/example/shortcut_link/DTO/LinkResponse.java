package com.example.shortcut_link.DTO;

import com.example.shortcut_link.entity.Link;

public class LinkResponse {
    private int id;
    private String message;
    private String linkURL;

    public LinkResponse() {}

    public LinkResponse(Link link, String message,String linkURL) {
        this.id = link.getId();
        this.message = message;
        this.linkURL = linkURL;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getLinkURL() { return linkURL; }
    public void setLinkURL(String linkURL) { this.linkURL = linkURL; }
}
