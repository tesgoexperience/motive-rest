package com.motive.rest.user.friendship;

import com.google.gson.Gson;
import com.motive.rest.util.JSONUtil;
import com.motive.rest.util.MvcUtil;
import com.motive.rest.util.SimpleResponse;
import com.motive.rest.util.SocialUtil;
import net.datafaker.Faker;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class FriendshipTest {
    @Autowired
    private MockMvc mvc;

    private JSONUtil json;
    private MvcUtil mvcUtil;
    private SocialUtil socialUtil;
    private JSONObject social;
    private Faker faker = new Faker();


    @Before
    public void createContext() throws Exception {
        mvcUtil = new MvcUtil(mvc);
        json = new JSONUtil();
        socialUtil = new SocialUtil(mvc);
        social = socialUtil.createSocial();
    }

    @Test
    public void acceptFriendRequest() throws UnsupportedEncodingException, Exception {
        String token = socialUtil.getToken(social);

        // Get a summary
        SimpleResponse summary =  mvcUtil.getRequest("/friendship/", token);

        System.out.println("Summary before is: " + summary);

        JSONObject incomingRequests = (JSONObject) ((JSONArray) social.get("incomingRequests")).get(0);
        HashMap<Object, Object> yourHashMap = new Gson().fromJson(String.valueOf(incomingRequests), HashMap.class);
        System.out.println("username 1 is" + yourHashMap.get("username"));


        // Accept one of the incoming requests.
       String result =  mvcUtil.postRequest("/friendship/accept?username=" + (String) yourHashMap.get("username"),"", token).toString();

       System.out.println("result is:" + result);


        // Get a summary
        summary =  mvcUtil.getRequest("/friendship/", token);

        System.out.println("Summary is: " + summary);
    }

    @Test
    public void refuseFriendRequest() throws UnsupportedEncodingException, Exception {
        String token = socialUtil.getToken(social);
        JSONArray totalIncomingRequests =  (JSONArray) social.get("incomingRequests");
//        System.out.println("Totals incoming requests are" + totalIncomingRequests.size());

        // Get a summary
        SimpleResponse summary =  mvcUtil.getRequest("/friendship/", token);

        System.out.println("Summary before is: " + summary);

        JSONObject incomingRequests = (JSONObject) ((JSONArray) social.get("incomingRequests")).get(0);
        HashMap<Object, Object> yourHashMap = new Gson().fromJson(String.valueOf(incomingRequests), HashMap.class);
        System.out.println("username 1 is" + yourHashMap.get("username"));

        // Accept one of the incoming requests.
        String result =  mvcUtil.postRequest("/friendship/reject?username=" + (String) yourHashMap.get("username"), token).toString();

        System.out.println("result is:" + result);


        // Get a summary
        summary =  mvcUtil.getRequest("/friendship/", token);

        System.out.println("Summary is: " + summary);
    }

    @Test
    public void searchUsers() throws Exception {
        String token = socialUtil.getToken(social);
        JSONArray totalIncomingRequests =  (JSONArray) social.get("incomingRequests");
        JSONObject incomingRequests = (JSONObject) ((JSONArray) social.get("incomingRequests")).get(0);
        HashMap<Object, Object> yourHashMap = new Gson().fromJson(String.valueOf(incomingRequests), HashMap.class);
        System.out.println("username 1 is" + yourHashMap.get("username"));

        JSONObject searchResultDTO =  (JSONObject) mvcUtil.getRequest("/friendship/search?search=" + yourHashMap.get("username"), token).getBodyAsJsonArray().get(0);

        System.out.println("Response is " + searchResultDTO);
        assertEquals(yourHashMap.get("username"), searchResultDTO.get("username"));
    }
}
