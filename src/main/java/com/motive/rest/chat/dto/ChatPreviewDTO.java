package com.motive.rest.chat.dto;

import com.motive.rest.chat.Chat;
import com.motive.rest.chat.message.Message;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatPreviewDTO {
    private String chatId;
    private String title;
    private String headMessage;
    private String headMessageSender;
    private String headMessageId;
    private boolean unread;

    public ChatPreviewDTO(Chat chat, String title, boolean unread) {
        this.title = title;
        this.chatId = chat.getId().toString();
        if (chat.getHeadMessage() == null) {
            this.headMessage = "";
            this.headMessageId = "";
            this.headMessageSender = "";
            this.unread = unread;
            return;
        }

        Message head = chat.getHeadMessage();
        this.headMessage = head.getContent();
        this.headMessageId = head.getId().toString();
        this.headMessageSender = head.getSender().getUsername();
        this.unread = unread;
    }
}
