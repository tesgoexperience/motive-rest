package com.motive.rest.util;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.github.javafaker.Faker;

import net.minidev.json.JSONObject;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

public class UserUtil {
    private Faker faker = new Faker();
    private MockMvc mvc;
    private MvcUtil mvcUtil;
    public UserUtil(MockMvc mvc) {
        this.mvc = mvc;
        mvcUtil = new MvcUtil(mvc);
    }

    /**
     * Generates a single user object
     * If persist is set true, this user will be saved and logged in
     * @param user has to be authenticated
     * @return List<friend>
     * @throws Exception
     */
    public JSONObject generateUser(boolean persist) throws Exception {

        JSONObject user = new JSONObject();

        user.put("username", faker.name().username());
        user.put("email", faker.name().username() + "@gmail.com");
        user.put("password", "Test123!");

        if (!persist) {
            return user;
        }

        mvc.perform(post("/user/register").contentType(MediaType.APPLICATION_JSON).content(user.toJSONString()))
                .andExpect(status().isOk());

        MvcResult mvcResult = mvc
                .perform(post("/user/login").contentType(MediaType.APPLICATION_JSON).content(user.toJSONString()))
                .andExpect(status().isOk()).andReturn();

        user.put("token", mvcResult.getResponse().getContentAsString());

        return user;
    }

    /**
     * Generates users and sets them as a friend of this user. Friends are saved to
     * database
     * 
     * @param user has to be authenticated
     * @return List<friend>
     * @throws Exception
     */
    public List<JSONObject> generateFriend(JSONObject user, int count) throws Exception {
        List<JSONObject> friends = new ArrayList<JSONObject>();

        for (int index = 0; index < count; index++) {
            friends.add(createFriend(user));
        }

        return friends;
    }

    /**
     * Generates a user and sets them as a friend of this user. Friend is saved to
     * database
     * @param user has to be authenticated
     * @return friend
     * @throws Exception
     */
    public JSONObject createFriend(JSONObject user) throws Exception {

        JSONObject friend = generateUser(true);

        mvc.perform(post("/friendship/request").param("username", user.get("username").toString())
                .header("authorization", friend.get("token")))
                .andExpect(status().isOk());

        mvc.perform(post("/friendship/accept").param("username", friend.get("username").toString())
                .header("authorization", user.get("token")))
                .andExpect(status().isOk());

        return friend;
    }

    public List<JSONObject> generateUsers(int count) throws Exception {

        List<JSONObject> users = new ArrayList<JSONObject>();

        for (int index = 0; index < count; index++) {
            users.add(generateUser(true));
        }

        return users;
    }
}
