package com.motive.rest.chat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.hsqldb.lib.HashMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.motive.rest.util.ChatUtil;
import com.motive.rest.util.MotiveUtil;
import com.motive.rest.util.MvcUtil;
import com.motive.rest.util.SocialUtil;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ChatTest {
    @Autowired
    private MockMvc mvc;
    private MvcUtil mvcUtil;
    private SocialUtil socialUtil;
    private MotiveUtil motiveUtil;
    private JSONObject social;
    private ChatUtil chatUtil;

    @Before
    public void createContext() throws Exception {
        mvcUtil = new MvcUtil(mvc);
        motiveUtil = new MotiveUtil(mvc);
        socialUtil = new SocialUtil(mvc);
        social = socialUtil.createSocial();
        chatUtil = new ChatUtil(mvc);
    }

    @Test
    public void chat_created_for_friendship() throws Exception {
        String token = socialUtil.getToken(social);
        JSONObject friend = socialUtil.createFriend((JSONObject)social.get("user"));
        assertTrue(mvcUtil.getRequest("/chat/preview", token).getBody().contains(friend.getAsString("username")));
    }

    @Test
    public void chat_created_for_motive() throws Exception {
        String token = socialUtil.getToken(social);
        JSONObject motive = motiveUtil.anyMotive(token);
        // check if attendee can see motive
        assertTrue(mvcUtil.getRequest("/chat/preview", token).getBody().contains(motive.getAsString("title")));
    }

    @Test
    public void attendee_added_to_motive_chat() throws Exception {
        String token = socialUtil.getToken(social);
        JSONObject friend = socialUtil.createFriend((JSONObject)social.get("user"));
        JSONObject motive = motiveUtil.anyMotiveWithAttendee(token, friend);
        assertTrue(mvcUtil.getRequest("/chat/preview", token).getBody().contains(motive.getAsString("title")));
    }

    @Test
    public void exchange_message_with_friend() throws Exception {
        JSONObject user = ((JSONObject) social.get("user"));
        String token = user.getAsString("token");
        JSONObject friend = socialUtil.createFriend((JSONObject)social.get("user"));

        JSONObject message = chatUtil.sendMessageToFriend(friend.getAsString("username"), token);

        // assert friend has received the message
        JSONObject chatPreview = chatUtil.getFriendChatPreview(user.getAsString("username"),
                friend.getAsString("token"));

        assertTrue(chatPreview.getAsString("headMessage").contains(message.getAsString("message")));
        assertTrue(chatPreview.getAsString("unread").equals("true"));
    }

    @Test
    public void exchange_message_with_motive() throws Exception {
        String token = socialUtil.getToken(social);
        JSONObject friend = socialUtil.createFriend((JSONObject)social.get("user"));
        JSONObject motive = motiveUtil.anyMotiveWithAttendee(token, friend);

        // send message
        JSONObject message = chatUtil.sendMessageToMotive(motive.getAsString("id"), token);

        JSONObject chatWithMessage = chatUtil.getMotiveChatPreview(motive.getAsString("id"),
                friend.getAsString("token"));

        assertTrue(chatWithMessage.getAsString("headMessage").contains(message.getAsString("message")));
        assertTrue(chatWithMessage.getAsString("unread").equals("true"));

    }

    @Test
    public void get_messages() throws Exception {
        String token = socialUtil.getToken(social);
        JSONObject friend = socialUtil.createFriend((JSONObject)social.get("user"));
        JSONObject motive = motiveUtil.anyMotiveWithAttendee(token, friend);

        JSONObject chatPreview = chatUtil.getMotiveChatPreview(motive.getAsString("id"), friend.getAsString("token"));

        // send 51 messages
        JSONObject firstMessage = chatUtil.anyMessage(chatPreview.getAsString("chatId"), token);
        for (int i = 0; i < 49; i++) {
            chatUtil.anyMessage(chatPreview.getAsString("chatId"), token);
        }
        JSONObject fiftyFirstMessage = chatUtil.anyMessage(chatPreview.getAsString("chatId"), token);

        // As each page is 50 messages long, first in the first page should be
        // firstMessage and the first message in the last message should be
        // fiftyFirstMessage
        JSONArray messagesPage1 = chatUtil.getMessages(chatPreview.getAsString("chatId"), friend.getAsString("token"),
                0);
        JSONArray messagesPage2 = chatUtil.getMessages(chatPreview.getAsString("chatId"), friend.getAsString("token"),
                1);

        assertEquals(firstMessage.get("message"), ((JSONObject) messagesPage1.get(0)).get("content"));
        assertEquals(fiftyFirstMessage.get("message"), ((JSONObject) messagesPage2.get(0)).get("content"));
    }

    @Test
    public void chatPreviewUpdate() throws Exception {
        String token = socialUtil.getToken(social);
        JSONObject friend = socialUtil.createFriend((JSONObject)social.get("user"));
        JSONObject user = ((JSONObject) social.get("user"));

        JSONObject preview = chatUtil.getFriendChatPreview(user.getAsString("username"), friend.getAsString("token"));

        // check update returns empty as there are no new messages
        JSONObject messageUpdateDto = new JSONObject();
        messageUpdateDto.put("chat", preview.getAsString("chatId"));
        messageUpdateDto.put("headMessage", preview.getAsString("headMessageId"));
        assertEquals(mvcUtil.postRequest("/chat/message/update", messageUpdateDto.toString(), token)
                .getBody(), "false");

        // send message as friend then check if there is an update
        chatUtil.sendMessageToFriend(user.getAsString("username"), friend.getAsString("token"));

        // since a friend has now sent us a message, out chat preview update should
        // contain an update
        assertEquals(mvcUtil.postRequest("/chat/message/update", messageUpdateDto.toString(), token)
                .getBody(), "true");

    }

    @Test
    public void get_preview_for_chat_youre_not_in() {

    }

    @Test
    public void get_messages_for_chat_youre_not_in() {

    }
}
