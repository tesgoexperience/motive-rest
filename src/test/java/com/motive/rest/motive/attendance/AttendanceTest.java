package com.motive.rest.motive.attendance;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.motive.rest.util.AttendanceUtil;
import com.motive.rest.util.MvcUtil;
import com.motive.rest.util.MotiveUtil;
import com.motive.rest.util.UserUtil;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
        attendanceUtil.addPendingAttendee(friend, motive);

        motive = (JSONObject)mvcUtil.getArrayAndExpectOk("/motive/managing", owner).get(0);

        assertTrue(!((JSONArray)motive.get("requests")).isEmpty());

        String request = (String)((JSONArray)motive.get("requests")).get(0);
        assertEquals(request, friend.get("username"));
    }

    @Test
    public void get_pending_requests() throws Exception {
        JSONObject owner = userUtil.generateUser(true);
        JSONObject friend = userUtil.createFriend(owner);

        JSONObject motive = motiveUtil.generateSimpleMotiveAndSave(owner);
        attendanceUtil.addPendingAttendee(friend, motive);

        motive = (JSONObject)mvcUtil.getArrayAndExpectOk("/motive/managing", owner).get(0);
        JSONArray requests = attendanceUtil.getPendingRequests(friend, motive);
        assertEquals(requests.size(),1);
    }
    
    @Test
    public void request_motive_twice() throws Exception {
        // check if friends can see motive attendance when anonymous

        JSONObject owner = userUtil.generateUser(true);
        JSONObject friend = userUtil.createFriend(owner);

        JSONObject motive = motiveUtil.generateSimpleMotiveAndSave(owner);
        attendanceUtil.addPendingAttendee(friend, motive);

        // request the motive again and expect the error
        JSONObject attendanceRequest = new JSONObject();
        attendanceRequest.put("motive", motive.get("id"));
        attendanceRequest.put("anonymous", true);

        mvcUtil.postAndExpectError("/attendance/request", friend, attendanceRequest, "Attendance already registered",status().isConflict());
        
    }
    
    @Test
    public void accept_request_for_motive() throws Exception {
        JSONObject owner = userUtil.generateUser(true);
        JSONObject friend = userUtil.createFriend(owner);

        JSONObject motive = motiveUtil.generateSimpleMotiveAndSave(owner);
        attendanceUtil.addPendingAttendee(friend, motive);

        // get the motive
        motive = (JSONObject)mvcUtil.getArrayAndExpectOk("/motive/managing", owner).get(0);

        JSONArray requests = attendanceUtil.getPendingRequests(friend, motive);

        JSONObject request = (JSONObject)requests.get(0);
        JSONObject attendanceResponse = new JSONObject();

        attendanceResponse.put("attendance", request.get("id"));
        attendanceResponse.put("accept", "true");

        mvcUtil.postAndExpectOk("/attendance/respond", owner, attendanceResponse);    

        motive = (JSONObject)mvcUtil.getArrayAndExpectOk("/motive/managing", owner).get(0);

        assertEquals(friend.get("username"), ((JSONArray)motive.get("confirmedAttendance")).get(0));

        // ensure there is none in the requests
        assertTrue(((JSONArray)motive.get("requests")).isEmpty());

    }
   
    @Test
    public void request_anonymously() throws Exception {
        // check if friends can see motive attendance when anonymous
        JSONObject owner = userUtil.generateUser(true);
        JSONObject friend = userUtil.createFriend(owner);
        JSONObject motive = motiveUtil.generateSimpleMotiveAndSave(owner);
        attendanceUtil.addConfirmedAttendee(owner, friend, motive, true); 

        // browse a motive and check who is attending it
        JSONObject friend2 = userUtil.createFriend(owner);

        motive = (JSONObject)mvcUtil.getArrayAndExpectOk("/motive/", friend2).get(0);

        assertEquals(1L,motive.get("confirmedAttendanceAnonymous"));
        assertTrue(((JSONArray)motive.get("confirmedAttendance")).isEmpty());
    }
}
