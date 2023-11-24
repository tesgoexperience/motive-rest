package com.motive.rest.chat.dto;

import com.motive.rest.chat.Chat;
import com.motive.rest.chat.Message;

import lombok.Getter;

@Getter
public class ChatPreviewDTO {
    private String chatId;
    private String title;
    private String headMessage;
    private String headMessageSender;
    private boolean unread;
    public ChatPreviewDTO(Chat chat,String title , boolean unread) {
        this.title = title;
        this.chatId = chat.getId().toString();
        if(chat.getMessages().size() == 0)
            return;

        Message head = chat.getMessages().get(chat.getMessages().size()-1);
        this.headMessage = head.getContent();
        this.headMessageSender = head.getSender().getUsername();
        this.unread = unread;
    }
}
