package com.motive.rest.chat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Component;

import com.motive.rest.chat.dto.ChatPreviewDTO;
import com.motive.rest.chat.message.Message;
import com.motive.rest.chat.message.MessageRepo;
import com.motive.rest.chat.message.dto.MessageDTO;
import com.motive.rest.exceptions.BadUserInput;
import com.motive.rest.exceptions.EntityNotFound;
import com.motive.rest.exceptions.UnauthorizedRequest;
import com.motive.rest.motive.Motive;
import com.motive.rest.motive.MotiveService;
import com.motive.rest.Auth.AuthService;
import com.motive.rest.user.User;
import com.motive.rest.user.UserService;
import com.motive.rest.user.friendship.Friendship;
import com.motive.rest.user.friendship.FriendshipService;
import org.springframework.data.domain.Sort.Direction;

import java.util.stream.Collectors;

@Component
public class ChatService {

    @Autowired
    ChatRepo repo;
    @Autowired
    AuthService authService;
    @Autowired
    FriendshipService friendshipService;
    @Autowired
    UserService userService;
    @Autowired
    MotiveService motiveService;
    @Autowired
    MessageRepo messageRepo;

    public List<ChatPreviewDTO> getChatPreview() {
        User user = authService.getAuthUser();

        List<Chat> getAllChats = repo.getChats(user.getId().toString());

        // put the chats this user is not upto date with first
        List<Chat> sortedChat = getAllChats.stream()
                .filter(chat -> chat.getNotUpToDate().contains(user)).collect(Collectors.toList());
        sortedChat.addAll(getAllChats.stream()
                .filter(chat -> !chat.getNotUpToDate().contains(user)).collect(Collectors.toList()));

        List<ChatPreviewDTO> chatsPreviews = new ArrayList<>();
        for (Chat chat : sortedChat) {
            chatsPreviews.add(chatToDto(chat));
        }
        return chatsPreviews;
    }

    public ChatPreviewDTO getChatWithFriend(String friendUsername) {
        Friendship friendship = friendshipService.getFriendshipWithUser(userService.findByUsername(friendUsername));
        return chatToDto(friendship.getChat());
    }

    public ChatPreviewDTO getChatForMotive(String motiveId) {
        Motive motive = motiveService.getMotive(UUID.fromString(motiveId));
        // check user is in the list of participants
        Chat chat = motive.getChat();
        validateIsMember(chat, authService.getAuthUser());
        return chatToDto(chat);
    }

    private ChatPreviewDTO chatToDto(Chat chat) {
        User user = authService.getAuthUser();
        boolean unread = chat.getNotUpToDate().contains(user);
        return new ChatPreviewDTO(chat, getChatTitle(chat), unread);
    }

    private String getChatTitle(Chat chat) {
        if (chat.getType().equals(Chat.TYPE.FRIENDSHIP)) {
            Friendship friendship = chat.getBelongsToFriendship();
            return friendshipService.extractFriend(friendship).getUsername();
        }

        return chat.getBelongsToMotive().getTitle();
    }

    public void sendMessage(UUID chatId, String messageContent) {
        
        if(messageContent.isEmpty())
        {
            throw new BadUserInput("Cannot send empty message");
        }

        Chat chat = findById(chatId);

        User user = authService.getAuthUser();
        validateIsMember(chat, user);

        Message message = new Message(chat, messageContent, user);
        messageRepo.save(message);

        // add users to the not upto date list
        for (User member : chat.getMembers()) {
            if (!member.equals(user) && !chat.getNotUpToDate().contains(member)) {
                chat.getNotUpToDate().add(member);
            }
        }

        chat.setHeadMessage(message);
        repo.save(chat);

        // TODO notify everyone in the chat about the new message
    }

    private void validateIsMember(Chat chat, User user) {
        if (!chat.getMembers().contains(user))
            throw new UnauthorizedRequest("user is not a member of the chat");
    }

    public Chat findById(UUID chatId) {
        Optional<Chat> chat = repo.findById(chatId);
        if (!chat.isPresent())
            throw new EntityNotFound("Could not find chat");

        return chat.get();
    }

    private void markAsRead(Chat chat) {
        User user = authService.getAuthUser();
        if (chat.getNotUpToDate().contains(user)) {
            chat.getNotUpToDate().remove(user);
            repo.save(chat);
        }
    }

    public List<MessageDTO> getMessages(String chatId, int page) {
        Chat chat = findById(UUID.fromString(chatId));
        User user = authService.getAuthUser();
        validateIsMember(chat, user);
        markAsRead(chat);

        List<Message> messages = messageRepo.findByChatIdOrderByCreateDateDesc(UUID.fromString(chatId), PageRequest.of(page, 50,Sort.by("id")));
        Collections.reverse(messages);
        List<MessageDTO> messageDtos = new ArrayList<>();
        for (Message message : messages) {
            messageDtos.add(new MessageDTO(message,message.getSender().equals(user)));
        }
        return messageDtos;
    }

    /*
     * This method will take in a list of chatPreviews from the client
     * It will then check if any of the chats have a new head message,
     * if so it will return true notifying the client they need to refresh they're chats
     * essentially having a set of previews for chats the user is not upto date with
     */
    public boolean getChatPreviewUpdate(List<ChatPreviewDTO> chats) {
        for (ChatPreviewDTO preview : chats) {

            Chat chat;
            Message headMessage = null;
            try {
                chat = findById(UUID.fromString(preview.getChatId()));
                // if the head message is null, skip as it means this chat has no messages
                if (!preview.getHeadMessage().isEmpty()) {
                    headMessage = findMessageById(Long.valueOf(preview.getHeadMessageId()));
                }
            } catch (IllegalArgumentException e) {
                throw new BadUserInput("Bad format for update. Invalid chatId/MessageId");
            }

            // validate this user is apart of this chat
            validateIsMember(chat, authService.getAuthUser());

            if ((preview.getHeadMessage().isEmpty() && chat.getHeadMessage() != null)) {
                return true;
            } else if (chat.getHeadMessage() != null && !chat.getHeadMessage().equals(headMessage)) {
                return true;
            }
        }

        return repo.getChats(authService.getAuthUser().getId().toString()).size() != chats.size();
    }

    public boolean checkMessagesUpdate(String headMessageId, String chatId ){
        Chat chat = findById(UUID.fromString(chatId));
        // if the client head message is empty but the chat has a had message it means the client is behind
        if(headMessageId == "" && chat.getHeadMessage() != null){
            return true;
        }
        
        if (headMessageId == ""){
            return false;
        }


        Message message = findMessageById(Long.valueOf(headMessageId));

        return !message.getChat().getHeadMessage().equals(message);
    }
    
    private Message findMessageById(Long id) {
        Optional<Message> message = messageRepo.findById(id);
        if (!message.isPresent())
            throw new EntityNotFound("Could not find message");

        return message.get();
    }
}
