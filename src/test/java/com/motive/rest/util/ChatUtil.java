package com.motive.rest.util;

import org.springframework.test.web.servlet.MockMvc;

import net.datafaker.Faker;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

public class ChatUtil {
    private MvcUtil mvcUtil;
    private AuthUtil authUtil;
    private Faker faker = new Faker();

    public ChatUtil(MockMvc mvc) {
        mvcUtil = new MvcUtil(mvc);
        authUtil = new AuthUtil(mvc);
    }

    public JSONObject anyMessage(String chat, String senderToken) throws Exception {
        JSONObject message = new JSONObject();
        message.appendField("chatId", chat);
        message.appendField("message", faker.simpsons().quote());
        mvcUtil.postRequest("/chat/send", message.toJSONString(), senderToken);
        return message;
    }

    public JSONArray getMessages(String chatId, String userToken, int page) throws Exception {
        return mvcUtil.getRequest("/chat/messages?chatId=" + chatId + "&page=" + page,
                userToken)
                .getBodyAsJsonArray();
    }

    public JSONObject sendMessageToFriend(String friendUsername, String token) throws Exception {
        // send message
        return anyMessage(getFriendChatPreview(friendUsername, token).getAsString("chatId"), token);
    }

    public JSONObject sendMessageToMotive(String motive, String token) throws Exception {
        // send message
        return anyMessage(getMotiveChatPreview(motive, token).getAsString("chatId"), token);
    }

    public JSONObject getMotiveChatPreview(String motiveId, String token) throws Exception {
        return mvcUtil.getRequest("/chat/motive?motiveId=" + motiveId,
                token)
                .getBodyAsJson();
    }

    public JSONObject getFriendChatPreview(String friendUsername, String token) throws Exception {
        return mvcUtil.getRequest("/chat/friend/?friendUsername=" + friendUsername,
                token)
                .getBodyAsJson();
    }
    
}
