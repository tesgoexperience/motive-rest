package com.motive.rest.motive.attendance;

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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.github.javafaker.Faker;
import com.motive.rest.util.AttendanceUtil;
import com.motive.rest.util.MvcUtil;
import com.motive.rest.util.MotiveUtil;
import com.motive.rest.util.UserUtil;
import com.mysql.cj.xdevapi.JsonArray;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AttendanceTest {

    private UserUtil userUtil;
    @Autowired
    private MockMvc mvc;
    private MotiveUtil motiveUtil;
    private MvcUtil mvcUtil;
    private AttendanceUtil attendanceUtil;
    private JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);

    @Before
    public void createContext() throws Exception {
        userUtil = new UserUtil(mvc);
        motiveUtil = new MotiveUtil(mvc);
        mvcUtil = new MvcUtil(mvc);
        attendanceUtil = new AttendanceUtil(mvc);
    }

    @Test
    public void request_attendance_to_invalid_motive() throws Exception {

        JSONObject user = userUtil.generateUser(true);

        JSONObject attendanceRequest = new JSONObject();
        attendanceRequest.put("motive", "234234234234");
        attendanceRequest.put("anonymous", true);

        mvcUtil.postAndExpectError("/attendance/request", user, attendanceRequest, "Could not find motive using id", status().isNotFound());
    }

    @Test
    public void request_attendance_to_valid_motive() throws Exception {

        JSONObject owner = userUtil.generateUser(true);
        JSONObject friend = userUtil.createFriend(owner);

        motiveUtil.generateSimpleMotiveAndSave(owner);

        // browse motives and get the one that was just created
        JSONArray availableMotives = motiveUtil.browseMotives(friend);

        JSONObject attendanceRequest = new JSONObject();
        attendanceRequest.put("motive", ((JSONObject)availableMotives.get(0)).get("id"));
        attendanceRequest.put("anonymous", true);

        mvcUtil.postAndExpectOk("/attendance/request", friend, attendanceRequest);

    }

    @Test
    public void request_attendance_to_strangers_motive() throws Exception {
        JSONObject owner = userUtil.generateUser(true);
        JSONObject stranger = userUtil.generateUser(true);

        JSONObject  motive = motiveUtil.generateSimpleMotiveAndSave(owner);

        JSONObject attendanceRequest = new JSONObject();
        attendanceRequest.put("motive", motive.get("id"));
        attendanceRequest.put("anonymous", true);

        mvcUtil.postAndExpectError("/attendance/request", stranger, attendanceRequest, "User is not friends with motive owner", status().isConflict());
    }

    @Test
    public void view_requests_for_motive() throws Exception {
        JSONObject owner = userUtil.generateUser(true);
        JSONObject friend = userUtil.createFriend(owner);

        JSONObject motive = motiveUtil.generateSimpleMotiveAndSave(owner);
        attendanceUtil.requestAttendance(friend, motive);

        motive = (JSONObject)mvcUtil.getArrayAndExpectOk("/motive/managing", owner).get(0);

        assertTrue(!((JSONArray)motive.get("requests")).isEmpty());

        String request = (String)((JSONArray)motive.get("requests")).get(0);
        assertEquals(request, friend.get("username"));
    }

    @Test
    public void accept_request_for_motive() throws Exception {
        JSONObject owner = userUtil.generateUser(true);
        JSONObject friend = userUtil.createFriend(owner);

        JSONObject motive = motiveUtil.generateSimpleMotiveAndSave(owner);
        attendanceUtil.requestAttendance(friend, motive);

        // accept request
        motive = (JSONObject)mvcUtil.getArrayAndExpectOk("/motive/managing", owner).get(0);

        MvcResult result = mvc.perform(get("/attendance/pending").param("motiveId",String.valueOf(motive.get("id")))
        .header("authorization", owner.get("token")))
        .andExpect(status().isOk()).andReturn();

        JSONObject firstRequest = (JSONObject)((JSONArray) parser.parse(result.getResponse().getContentAsString())).get(0);

        //TODO fininsh test
    }
    public void get_pending_requests() {
        // check if friends can see motive attendance when anonymous
    }
    public void request_anonymously() {
        // check if friends can see motive attendance when anonymous
    }
}
