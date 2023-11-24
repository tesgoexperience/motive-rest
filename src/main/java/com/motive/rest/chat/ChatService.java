package com.motive.rest.chat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.motive.rest.chat.Message;
import com.motive.rest.chat.dto.ChatPreviewDTO;
import com.motive.rest.exceptions.BadUserInput;
import com.motive.rest.exceptions.UnauthorizedRequest;
import com.motive.rest.Auth.AuthService;
import com.motive.rest.user.User;
import com.motive.rest.user.UserRepo;
import com.motive.rest.user.UserService;
import com.motive.rest.user.friendship.FriendRepo;
import com.motive.rest.user.friendship.Friendship;
import com.motive.rest.user.friendship.FriendshipService;

@Component
public class ChatService {

    @Autowired
    ChatRepo repo;
    @Autowired
    AuthService authService;
    @Autowired
    FriendshipService service;

    public List<ChatPreviewDTO> getChatPreview() {
        User user = authService.getAuthUser();

        List<Chat> getAllChats = repo.getChats(user.getId().toString());
        List<ChatPreviewDTO> chatsPreviews = new ArrayList<>();
        for (Chat chat : getAllChats) {
            boolean unread = chat.getNotUpToDate().contains(user);
            
            chatsPreviews.add(new ChatPreviewDTO(chat, getChatTitle(chat) ,unread));
        }
        return chatsPreviews;
    }
// Expression #6 of SELECT list is not in GROUP BY clause and contains nonaggregated column 'motivedb.chat_members.chat_id' which is not functionally dependent on columns in GROUP BY clause; this is incompatible with sql_mode=only_full_group_by

    private String getChatTitle(Chat chat) {
        if(chat.getType().equals(Chat.TYPE.FRIENDSHIP))
        {
            Friendship friendship = chat.getBelongsToFriendship();
            return service.extractFriend(friendship).getUsername();
        }

        return chat.getBelongsToMotive().getTitle();
    }

    public void sendMessage(UUID chatId, String message) {
        Optional<Chat> opChat = repo.findById(chatId);
        if (!opChat.isPresent())
            throw new BadUserInput("This chat could be found");

        Chat chat = opChat.get();

        User user = authService.getAuthUser();
        validateIsMember(chat, user);

        chat.getMessages().add(new Message(chat, message, user));

        chat.getNotUpToDate().addAll(chat.getMembers());
        // remove the message sender from the not upto date list
        chat.getNotUpToDate().remove(user);
        
        repo.save(chat);

        //TODO notify everyone in the chat about the new message
    }

    private void validateIsMember(Chat chat, User user) {
        if (!chat.getMembers().contains(user))
            throw new UnauthorizedRequest("user is not a member of the chat");
    }

    // Friendship friendship =
    // service.getFriendshipWithUser(userRepo.findByUsername("chris").get());
    // Chat chat =
    // repo.findById(UUID.fromString("d416ab74-9c48-439e-9741-c051a90d0d28")).get();
    // chat.getMessages().add(new Message(chat,"wassaaaap"));
    // repo.save(chat);
    // friendship.setChat(chat);
    // friendshipRepo.save(friendship);
    // repo.save(new
    // Chat(Arrays.asList(friendship.getReceiver(),friendship.getSender()),friendship));
}
