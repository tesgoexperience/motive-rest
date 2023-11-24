package com.motive.rest.chat;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.motive.rest.chat.dto.ChatPreviewDTO;
import com.motive.rest.chat.dto.SendMessageDTO;

import org.springframework.stereotype.Controller;

@Controller
@RequestMapping(path = "/chat")
@PreAuthorize("isAuthenticated()")
public class ChatController {
    @Autowired 
    ChatService service;

    @GetMapping(value = "/preview")
    @ResponseBody
    public List<ChatPreviewDTO> getChatPreview(){
        return service.getChatPreview();
    }


    @PostMapping(value = "/send")
    @ResponseBody
    public void sendMessage(@RequestBody SendMessageDTO message){
       service.sendMessage(UUID.fromString( message.getChatId()), message.getMessage());
    }

}
///
