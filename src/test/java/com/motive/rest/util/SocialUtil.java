package com.motive.rest.util;

import java.io.UnsupportedEncodingException;
import java.util.Random;

import org.springframework.test.web.servlet.MockMvc;

import net.minidev.json.JSONArray;

import net.minidev.json.JSONObject;

public class SocialUtil {
    private JSONUtil json;
    private MvcUtil mvcUtil;
    private AuthUtil authUtil;

    public SocialUtil(MockMvc mvc) {
        mvcUtil = new MvcUtil(mvc);
        json = new JSONUtil();
        authUtil = new AuthUtil(mvc);
    }

    /*
     * creates a json object containing
     * user: the name of the object user
     * friends: a list of friends
     * outgoing-requests: 3 requests from user
     * incoming-requests: 3 requests from other users
     * strangers: a list of users who are not friends
     */
    public JSONObject createSocial() throws UnsupportedEncodingException, Exception {
        JSONObject user = authUtil.registerUser();

        JSONArray friends = new JSONArray();
        for (int index = 0; index < 3; index++) {
            friends.add(createFriend(user));
        }

        JSONArray outgoingRequests = new JSONArray();
        for (int index = 0; index < 3; index++) {
            outgoingRequests.add(createOutgoingRequest(user));
        }

        JSONArray incomingRequests = new JSONArray();
        for (int index = 0; index < 3; index++) {
            incomingRequests.add(createIncomingRequest(user));
        }

        return new JSONObject().appendField("user", user)
                .appendField("friends", friends)
                .appendField("outgoingRequests", outgoingRequests)
                .appendField("incomingRequests", incomingRequests);
    }

    public JSONObject createIncomingRequest(JSONObject user) throws UnsupportedEncodingException, Exception {
        JSONObject friend = authUtil.registerUser();
        mvcUtil.postRequest("/friendship/request?username=" + user.get("username").toString(), "",
                friend.get("token").toString());

        return friend;
    }

    public JSONObject createOutgoingRequest(JSONObject user) throws UnsupportedEncodingException, Exception {
        JSONObject friend = authUtil.registerUser();
        mvcUtil.postRequest("/friendship/request?username=" + friend.get("username").toString(), "",
                user.get("token").toString());

        return friend;
    }

    public JSONObject createFriend(JSONObject user) throws UnsupportedEncodingException, Exception {
        JSONObject friend = authUtil.registerUser();
        mvcUtil.postRequest("/friendship/request?username=" + friend.get("username").toString(), "",
                user.get("token").toString());
        mvcUtil.postRequest("/friendship/accept?username=" + user.get("username").toString(), "",
                friend.get("token").toString());
        return friend;
    }

    public String getToken(JSONObject social) {
        return ((JSONObject) social.get("user")).getAsString("token");
    }
    public JSONObject getFriend(JSONObject social) {
        JSONArray friends = (JSONArray) social.get("friends");
        return (JSONObject)friends.get(new Random().nextInt(friends.size()));
    }
}
