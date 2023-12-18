package com.motive.rest.chat.message.dto;

import java.util.Date;

import com.motive.rest.chat.message.Message;

import lombok.Getter;

@Getter
public class MessageDTO {
    private String sender;
    private Date sentOn;
    private String message;

    public MessageDTO(Message m) {
        sender = m.getSender().getUsername();
        sentOn = m.getCreateDate();
        message = m.getContent();
    }
}
