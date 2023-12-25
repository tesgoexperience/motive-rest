package com.motive.rest.chat.message.dto;

import java.util.Date;

import com.motive.rest.chat.message.Message;

import lombok.Getter;

@Getter
public class MessageDTO {
    private String id;
    private String sender;
    private Date sentOn;
    private String content;
    private boolean sentByMe;

    public MessageDTO(Message m, boolean sentByMe) {
        id = m.getId().toString();
        sender = m.getSender().getUsername();
        sentOn = m.getCreateDate();
        content = m.getContent();
        this.sentByMe = sentByMe;
    }
}
