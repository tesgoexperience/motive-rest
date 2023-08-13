package com.motive.rest.motive;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import net.datafaker.Faker;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.motive.rest.user.dto.SocialSummaryDTO;
import com.motive.rest.util.AuthUtil;
import com.motive.rest.util.JSONUtil;
import com.motive.rest.util.MvcUtil;
import com.motive.rest.util.SimpleResponse;
import com.motive.rest.util.SocialUtil;
import com.nimbusds.jose.shaded.json.JSONArray;
import net.minidev.json.parser.JSONParser;

import org.springframework.http.HttpStatus;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class MotiveTest {
    @Autowired
    private MockMvc mvc;

    private JSONUtil json;
    private MvcUtil mvcUtil;
    private SocialUtil socialUtil;
    private AuthUtil authUtil;
    private JSONObject social;
    private JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
    private Faker faker = new Faker();

    @Before
    public void createContext() throws Exception {
        mvcUtil = new MvcUtil(mvc);
        json = new JSONUtil();
        authUtil = new AuthUtil(mvc);
        socialUtil = new SocialUtil(mvc);
        social = socialUtil.createSocial();
    }

    @Test
    public void MotiveOpenToEveryone() throws UnsupportedEncodingException, Exception {
        String token = socialUtil.getToken(social);
        // Create event
        JSONObject motive = json.motiveObject("EVERYONE");
        SimpleResponse res = mvcUtil.postRequest("/motive/create/", motive.toJSONString(), token);

        assertEquals(res.getStatus(), HttpStatus.OK);

        // get event by ID
        JSONObject motiveResponseJSON = (JSONObject) parser.parse(res.getBody());
        assertEquals(mvcUtil.getRequest("/motive/get?motive=" + motiveResponseJSON.get("id"), token).getStatus(),
                HttpStatus.OK);
    }

    @Test
    public void MotiveDateSetToPast() throws UnsupportedEncodingException, Exception {
        String token = socialUtil.getToken(social);
        JSONObject motive = json.motiveObject("EVERYONE");
        motive.put("start",
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(faker.date().past(3, TimeUnit.DAYS)));
        SimpleResponse res = mvcUtil.postRequest("/motive/create/", motive.toJSONString(), token);
        assertEquals(res.getStatus(), HttpStatus.BAD_REQUEST);
        assertTrue(res.getBody().contains("Start date cannot be in the past"));
    }

    @Test
    public void MotiveOpenToOnlyFriends() throws UnsupportedEncodingException, Exception {
        String token = socialUtil.getToken(social);
        JSONObject motive = json.motiveObject("FRIENDS");
        SimpleResponse res = mvcUtil.postRequest("/motive/create/", motive.toJSONString(), token);
        assertEquals(res.getStatus(), HttpStatus.OK);

        // Get motives as a friend
        SimpleResponse allMotives = mvcUtil.getRequest("/motive/all", ((JSONObject)((JSONArray)social.get("friends")).get(0)).getAsString("token"));
        // String motiveID 
        // Get motive as stranger
    }
}
