package com.motive.rest.motive;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.github.javafaker.Faker;
import com.motive.rest.util.MvcUtil;
import com.motive.rest.util.MotiveUtil;
import com.motive.rest.util.UserUtil;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MotiveTest {

    private Faker faker = new Faker();

    @Autowired
    private MockMvc mvc;
    private JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
    private JSONObject authenticatedUser;

    private UserUtil userUtil;
    private MotiveUtil motiveUtil;
    private MvcUtil mvcUtil;
    @Before
    public void createContext() throws Exception {
        userUtil = new UserUtil(mvc);
        authenticatedUser = userUtil.generateUser(true);
        motiveUtil = new MotiveUtil(mvc);
        mvcUtil = new MvcUtil(mvc);
    }

    @Test
    public void create_motive() throws Exception {
        JSONObject motive = new JSONObject();
        motive.put("title", faker.job().title());
        motive.put("description", faker.elderScrolls().quote());

        String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        motive.put("start", simpleDateFormat.format(faker.date().future(1, TimeUnit.DAYS)));
        motive.put("hiddenFrom", new JSONArray());

       MvcResult mvcResult = mvcUtil.postAndExpect("/motive/create", authenticatedUser, motive, status().isOk());

        JSONObject responseObject = (JSONObject) parser.parse(mvcResult.getResponse().getContentAsString());
        motiveUtil.compareCreateDTOWithManageDTO(motive, responseObject);

    }

    @Test
    public void get_multiple_managing_motives() throws Exception {

        JSONObject[] motives = new JSONObject[] { motiveUtil.generateSimpleMotiveAndSave(authenticatedUser),
                motiveUtil.generateSimpleMotiveAndSave(authenticatedUser),
                motiveUtil.generateSimpleMotiveAndSave(authenticatedUser) };

        // The motives should be returned in the same order they were created in
        MvcResult mvcResult = mvc
                .perform(get("/motive/managing").header("authorization", authenticatedUser.get("token")))
                .andExpect(status().isOk())
                .andReturn();

        // convert to result to json array
        JSONArray result = (JSONArray) parser.parse(mvcResult.getResponse().getContentAsString());
        for (int i = 0; i < motives.length; i++) {
            JSONObject responseMotive = (JSONObject) result.get(i);
            JSONObject expectedMotive = motives[i];
            motiveUtil.compareCreateDTOWithManageDTO(expectedMotive, responseMotive);
        }
    }

    @Test
    public void browse_active_motives() throws Exception {
        JSONObject[] motives = new JSONObject[] { motiveUtil.generateSimpleMotiveAndSave(authenticatedUser),
                motiveUtil.generateSimpleMotiveAndSave(authenticatedUser),
                motiveUtil.generateSimpleMotiveAndSave(authenticatedUser) };
        JSONObject friend = userUtil.createFriend(authenticatedUser);

        // so now the auth user has created a motive, their friend should see when they
        // call the /motive/browse endpoint
        MvcResult browseMotiveResult = mvc.perform(get("/motive/").header("authorization", friend.get("token")))
                .andExpect(status().isOk())
                .andReturn();

        JSONArray result = (JSONArray) parser.parse(browseMotiveResult.getResponse().getContentAsString());

        for (int i = 0; i < motives.length; i++) {
            motiveUtil.compareBrowseDTOWithManageDTO((JSONObject) result.get(i), motives[i]);
        }
    }

    @Test
    public void get_motives_by_different_friends() throws Exception {
        JSONObject[] friends = new JSONObject[] { userUtil.createFriend(authenticatedUser),
                userUtil.createFriend(authenticatedUser), userUtil.createFriend(authenticatedUser) };
        JSONObject[] friendsMotives = new JSONObject[] { motiveUtil.generateSimpleMotiveAndSave(friends[0]),
                motiveUtil.generateSimpleMotiveAndSave(friends[1]),
                motiveUtil.generateSimpleMotiveAndSave(friends[2]) };

        MvcResult browseMotiveResult = mvc
                .perform(get("/motive/").header("authorization", authenticatedUser.get("token")))
                .andExpect(status().isOk())
                .andReturn();

        JSONArray result = (JSONArray) parser.parse(browseMotiveResult.getResponse().getContentAsString());

        for (int i = 0; i < friendsMotives.length; i++) {
            motiveUtil.compareBrowseDTOWithManageDTO((JSONObject) result.get(i), friendsMotives[i]);
        }
    }

    @Test
    public void do_not_show_motives_from_non_friends() throws Exception {
        // create a random user and then use them to create a random motive
        motiveUtil.generateSimpleMotiveAndSave(userUtil.generateUser(true));

        JSONArray availableMotives = motiveUtil.browseMotives(authenticatedUser);
        assertTrue(availableMotives.isEmpty());

    }

    @Test
    public void hidden_from_user_cannot_see_motive() throws Exception {
        JSONObject friend = userUtil.createFriend(authenticatedUser);
        JSONObject createdMotive = motiveUtil.generateSimpleMotive();
        JSONArray hiddenFrom = new JSONArray();

        hiddenFrom.add(friend.get("username"));
        createdMotive.put("hiddenFrom", hiddenFrom);
        motiveUtil.save(createdMotive, authenticatedUser);

        JSONArray availableMotives = motiveUtil.browseMotives(authenticatedUser);

        assertTrue(availableMotives.isEmpty());

    }

    @Test
    public void hidden_from_user_who_is_not_a_friend() throws Exception {
        JSONObject randomUser = userUtil.generateUser(true);
        JSONObject createdMotive = motiveUtil.generateSimpleMotive();
        JSONArray hiddenFrom = new JSONArray();

        hiddenFrom.add(randomUser.get("username"));
        createdMotive.put("hiddenFrom", hiddenFrom);

        mvcUtil.postAndExpectError("/motive/create", authenticatedUser, createdMotive, "USER IS NOT YOUR FRIEND", status().isNotFound());

    }

    //TODO test relation with motive
    //TODO if a user tries to request their own motive it returns "User is not friends with motive owner"

}
