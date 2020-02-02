package com.beerdelivery.driver.model;

public class ChatMessageModel {
    public String message;
    public String sender;

    public ChatMessageModel(String message, String sender) {
        this.message = message;
        this.sender = sender;
    }
}
