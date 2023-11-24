package com.motive.rest.chat;

import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonBooleanFormatVisitor;
import com.motive.rest.util.MotiveUtil;
import com.motive.rest.util.MvcUtil;
import com.motive.rest.util.SimpleResponse;
import com.motive.rest.util.SocialUtil;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import netscape.javascript.JSObject;
import net.datafaker.Faker;

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
    private Faker faker = new Faker();

    @Before
    public void createContext() throws Exception {
        mvcUtil = new MvcUtil(mvc);
        motiveUtil = new MotiveUtil(mvc);
        socialUtil = new SocialUtil(mvc);
        social = socialUtil.createSocial();
    }

    @Test
    public void chat_created_for_friendship() throws Exception {
        String token = socialUtil.getToken(social);
        JSONObject friend =socialUtil.getFriend(social);
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
        JSONObject friend = socialUtil.getFriend(social);
        JSONObject motive = motiveUtil.anyMotiveWithAttendee(token, friend);
        assertTrue(mvcUtil.getRequest("/chat/preview", token).getBody().contains(motive.getAsString("title")));
    }

    @Test
    public void exchange_message_with_friend() throws Exception {
        JSONObject user = ((JSONObject) social.get("user"));
        String token = user.getAsString("token");
        JSONObject friend = socialUtil.getFriend(social);

        // get chat with friend
        SimpleResponse chat = mvcUtil.getRequest("/chat/friend/?friend=" + friend.getAsString("username"), token);

        // send message
        JSONObject message = new JSONObject();
        message.appendField("chatId", chat.getBodyAsJson().getAsString("chatId"));
        message.appendField("message", faker.gameOfThrones().quote());
        mvcUtil.postRequest("/chat/send", message.toJSONString(), token);

        // assert friend has received the message
        assertTrue(mvcUtil.getRequest("/chat/friend?friend=" + user.getAsString("username"),
                        friend.getAsString("token"))
                        .getBody()
                        .contains(message.getAsString("message")));

    }
    @Test
    public void unread_convo_flag() {

    }
    @Test
    public void exchange_message_with_motive() {

    }

    @Test
    public void chat_preview() {

    }

    @Test
    public void get_second_page_of_messages() {

    }

}
