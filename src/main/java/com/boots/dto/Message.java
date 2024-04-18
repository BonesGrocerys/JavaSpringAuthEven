package com.boots.dto;

public class Message {
    private String message;
    private String description;

    public Message(String message){
        this.message = message;
    }

    public Message(String message, String stackTrace){
        this.message = message;
        this.description = stackTrace;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}