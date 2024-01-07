package com.motive.rest.motive.attendance;

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
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AttendanceTest {
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
    public void requestAttendance() throws UnsupportedEncodingException, Exception {
        String token = socialUtil.getToken(social);
        JSONObject friend = (JSONObject) ((JSONArray) social.get("friends")).get(0);

        // create a motive
        SimpleResponse res = mvcUtil.postRequest("/motive/create/",
                json.motiveObject("FRIENDS").toJSONString(), token);
        String motiveId = res.getBodyAsJson().getAsString("id");

        // request motive as friend
        JSONObject request = new JSONObject();
        request.appendField("motive", motiveId);
        request.appendField("anonymous", "false");
        mvcUtil.postRequest("/attendance/request", request.toJSONString(),
                friend.getAsString("token"));

        // get managed motives and assert that the requested user's username is in the "requests" array.
        JSONObject managedMotiveDTO = (JSONObject) mvcUtil.getRequest("/motive/managing", token)
                .getBodyAsJsonArray().get(0);
        assertEquals(motiveId, managedMotiveDTO.get("id"));
        assertEquals(friend.get("username"), json.toJsonArray(
                        ((JSONObject) managedMotiveDTO.get("managementDetails")).getAsString("requests")).get(0)
                .toString());
    }

    @Test
    public void respondToAttendanceRequestAccept() throws UnsupportedEncodingException, Exception {
        String token = socialUtil.getToken(social);
        JSONObject friend = (JSONObject) ((JSONArray) social.get("friends")).get(0);

        // create a motive
        SimpleResponse res = mvcUtil.postRequest("/motive/create/",
                json.motiveObject("FRIENDS").toJSONString(), token);
        String motiveId = res.getBodyAsJson().getAsString("id");

        // request motive as friend
        JSONObject request = new JSONObject();
        request.appendField("motive", motiveId);
        request.appendField("anonymous", "false");
        mvcUtil.postRequest("/attendance/request", request.toJSONString(),
                friend.getAsString("token"));

        // get managed motives and assert that the requested user's username is in the "requests" list.
        JSONObject managedMotiveDTO = (JSONObject) mvcUtil.getRequest("/motive/managing", token)
                .getBodyAsJsonArray().get(0);
        assertEquals(motiveId, managedMotiveDTO.get("id"));
        assertEquals(friend.get("username"), json.toJsonArray(
                        ((JSONObject) managedMotiveDTO.get("managementDetails")).getAsString("requests")).get(0)
                .toString());

        // At this pont, the attendance would be empty.
        List attendance = (List) managedMotiveDTO.get("attendance");
        assertThat(attendance.isEmpty());

        // Accept the request
        mvcUtil.postRequest("/attendance/accept", json.attendanceResponseObject(friend.get("username").toString(), motiveId).toJSONString(),
                token);

        // get managed motives again and assert the attendance array this stage.
        JSONObject secondManagedMotiveDTO = (JSONObject) mvcUtil.getRequest("/motive/managing", token)
                .getBodyAsJsonArray().get(0);
        assertEquals(motiveId, secondManagedMotiveDTO.get("id"));
        List updatedAttendance = (List) secondManagedMotiveDTO.get("attendance");
        assertThat(!updatedAttendance.isEmpty());
        assertEquals(friend.get("username"), updatedAttendance.get(0));
    }

    @Test
    public void respondToAttendanceRequestReject() throws UnsupportedEncodingException, Exception {
        String token = socialUtil.getToken(social);
        JSONObject friend = (JSONObject) ((JSONArray) social.get("friends")).get(0);

        // create a motive
        SimpleResponse res = mvcUtil.postRequest("/motive/create/",
                json.motiveObject("FRIENDS").toJSONString(), token);
        String motiveId = res.getBodyAsJson().getAsString("id");

        // request motive as friend
        JSONObject request = new JSONObject();
        request.appendField("motive", motiveId);
        request.appendField("anonymous", "false");
        mvcUtil.postRequest("/attendance/request", request.toJSONString(),
                friend.getAsString("token"));

        // get managed motives and assert that the requested user's username is in the "requests" list.
        JSONObject managedMotiveDTO = (JSONObject) mvcUtil.getRequest("/motive/managing", token)
                .getBodyAsJsonArray().get(0);
        assertEquals(motiveId, managedMotiveDTO.get("id"));
        assertEquals(friend.get("username"), json.toJsonArray(
                        ((JSONObject) managedMotiveDTO.get("managementDetails")).getAsString("requests")).get(0)
                .toString());

        // Reject the request
        mvcUtil.postRequest("/attendance/reject", json.attendanceResponseObject(friend.get("username").toString(), motiveId).toJSONString(),
                token);

        // get managed motives and assert that the "requests" list is now empty.
        JSONObject managedMotive2DTO = (JSONObject) mvcUtil.getRequest("/motive/managing", token)
                .getBodyAsJsonArray().get(0);
        assertEquals(motiveId, managedMotive2DTO.get("id"));
        List requests = (List) ((JSONObject) managedMotive2DTO.get("managementDetails")).get("requests");
        assertThat(requests.isEmpty());
    }

    @Test
    public void removeAttendee() throws UnsupportedEncodingException, Exception {
        String token = socialUtil.getToken(social);
        JSONObject friend = (JSONObject) ((JSONArray) social.get("friends")).get(0);

        // create a motive
        SimpleResponse res = mvcUtil.postRequest("/motive/create/",
                json.motiveObject("FRIENDS").toJSONString(), token);
        String motiveId = res.getBodyAsJson().getAsString("id");

        // request motive as friend
        JSONObject request = new JSONObject();
        request.appendField("motive", motiveId);
        request.appendField("anonymous", "false");
        mvcUtil.postRequest("/attendance/request", request.toJSONString(),
                friend.getAsString("token"));

        // get managed motives and assert that the requested user's username is in the "requests" list.
        JSONObject managedMotiveDTO = (JSONObject) mvcUtil.getRequest("/motive/managing", token)
                .getBodyAsJsonArray().get(0);
        assertEquals(motiveId, managedMotiveDTO.get("id"));
        assertEquals(friend.get("username"), json.toJsonArray(
                        ((JSONObject) managedMotiveDTO.get("managementDetails")).getAsString("requests")).get(0)
                .toString());

        // At this pont, the attendance would be empty.
        List attendance = (List) managedMotiveDTO.get("attendance");
        assertThat(attendance.isEmpty());

        // Accept the request
        mvcUtil.postRequest("/attendance/accept", json.attendanceResponseObject(friend.get("username").toString(), motiveId).toJSONString(),
                token);

        // get managed motives again and assert the attendance after the user has been accepted.
        JSONObject secondManagedMotiveDTO = (JSONObject) mvcUtil.getRequest("/motive/managing", token)
                .getBodyAsJsonArray().get(0);
        assertEquals(motiveId, secondManagedMotiveDTO.get("id"));
        List updatedAttendance = (List) secondManagedMotiveDTO.get("attendance");
        assertThat(!updatedAttendance.isEmpty());
        assertEquals(friend.get("username"), updatedAttendance.get(0));


        mvcUtil.postRequest("/attendance/remove", json.attendanceResponseObject(friend.get("username").toString(), motiveId).toJSONString(),
                token);


        // get managed motives again and assert the attendance array after the user has been removed from the attendance list.
        JSONObject thirdManagedMotiveDTO = (JSONObject) mvcUtil.getRequest("/motive/managing", token)
                .getBodyAsJsonArray().get(0);
        assertEquals(motiveId, thirdManagedMotiveDTO.get("id"));
        List afterRemovalAttendance = (List) thirdManagedMotiveDTO.get("attendance");
        assertThat(afterRemovalAttendance.isEmpty());

    }
    @Test
    public void getAllPendingRequests() throws UnsupportedEncodingException, Exception {
        String token = socialUtil.getToken(social);
        JSONObject friend = (JSONObject) ((JSONArray) social.get("friends")).get(0);

        // create a motive
        SimpleResponse res = mvcUtil.postRequest("/motive/create/",
                json.motiveObject("FRIENDS").toJSONString(), token);
        String motiveId = res.getBodyAsJson().getAsString("id");

        // request motive as friend
        JSONObject request = new JSONObject();
        request.appendField("motive", motiveId);
        request.appendField("anonymous", "false");
        mvcUtil.postRequest("/attendance/request", request.toJSONString(),
                friend.getAsString("token"));

        // Assert that the friend's username is in the pending list of requests.
        JSONObject pendingRequests = (JSONObject) mvcUtil.getRequest("/attendance/pending", motiveId, token)
                .getBodyAsJsonArray().get(0);

        assertEquals(friend.get("username"), pendingRequests.get("user"));

    }
}
