package com.chris.scrim;

/**
 * Created by chris on 3/5/2016.
 */
public class Chat {

    private String message;
    private String author;

    Chat(String message, String author) {
        this.message = message;
        this.author = author;
    }

    public String getMessage() {
        return message;
    }

    public String getAuthor() {
        return author;
    }
}