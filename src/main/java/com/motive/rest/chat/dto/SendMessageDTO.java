package com.motive.rest.chat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SendMessageDTO {
    String chatId;
    String message;
}
