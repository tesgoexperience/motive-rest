package com.motive.rest.chat;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.stereotype.Controller;

import com.motive.rest.chat.dto.ChatPreviewDTO;
import com.motive.rest.chat.dto.SendMessageDTO;
import com.motive.rest.chat.message.dto.MessageDTO;
import com.motive.rest.exceptions.BadUserInput;
import com.motive.rest.exceptions.EntityNotFound;


@Controller
@RequestMapping(path = "/chat")
@PreAuthorize("isAuthenticated()")
public class ChatController {
    @Autowired
    ChatService service;

    @GetMapping(value = "/preview")
    @ResponseBody
    public List<ChatPreviewDTO> getChatPreview() {
        return service.getChatPreview();
    }

    @PostMapping(value = "/send")
    @ResponseBody
    public void sendMessage(@RequestBody SendMessageDTO message) {
        service.sendMessage(UUID.fromString(message.getChatId()), message.getMessage());
    }

    @GetMapping(value = "/friend")
    @ResponseBody
    public ChatPreviewDTO getChatWithFriendPreview(@RequestParam String friendUsername) {
        try {
            return service.getChatWithFriend(friendUsername);
        } catch (EntityNotFound e) {
            throw new BadUserInput("There is no friendship with this user");
        }
    }

    @GetMapping(value = "/motive")
    @ResponseBody
    public ChatPreviewDTO getChatPreview(@RequestParam String motiveId) {
        try {
            return service.getChatForMotive(motiveId);
        } catch (EntityNotFound e) {
            throw new BadUserInput("Motive not found");
        }
    }

    @PostMapping(value = "/update")
    @ResponseBody
    public boolean getChatPreviewUpdate(@RequestBody List<ChatPreviewDTO> headMessages) {
        return service.getUpdate(headMessages);
    }
    
    @GetMapping(value = "/messages")
    @ResponseBody
    public List<MessageDTO> getMessages(@RequestParam String chatId, @RequestParam Integer page) {
        try {
            return service.getMessages(chatId, page);
        } catch (EntityNotFound e) {
            throw new BadUserInput("Chat not found");
        }
    }
}
