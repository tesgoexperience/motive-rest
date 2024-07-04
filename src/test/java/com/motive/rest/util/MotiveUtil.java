package com.motive.rest.util;

import java.util.Map;

import org.springframework.test.web.servlet.MockMvc;

import net.minidev.json.JSONObject;

public class MotiveUtil {
    private JSONUtil json;
    private MvcUtil mvcUtil;

    public MotiveUtil(MockMvc mvc) {
        mvcUtil = new MvcUtil(mvc);
        json = new JSONUtil();
    }

    public JSONObject anyMotive(String ownerToken) throws Exception {
        // create motive
        return mvcUtil
                .postRequest("/motive/create", json.motiveObject("FRIENDS").toJSONString(), ownerToken)
                .getBodyAsJson();

    }

    public JSONObject anyMotiveWithAttendee(String ownerToken, JSONObject attendee) throws Exception {
        JSONObject motive = anyMotive(ownerToken);
        addAttendee(ownerToken, motive, attendee);
        return motive;
    }

    private boolean addAttendee(String ownerToken, JSONObject motive, JSONObject attendee) throws Exception {
        // add attendee
        mvcUtil.postRequest("/attendance/request",
                new JSONObject(Map.of("motive", motive.getAsString("id"), "anonymous", "false")).toJSONString(),
                attendee.getAsString("token"));

        // attendeeUsername: attendee, motiveId: this.motive.id
        mvcUtil.postRequest("/attendance/accept",
                new JSONObject(
                        Map.of("motiveId", motive.get("id"), "attendeeUsername", attendee.getAsString("username")))
                        .toJSONString(),
                ownerToken);
        return true;
    }

}
