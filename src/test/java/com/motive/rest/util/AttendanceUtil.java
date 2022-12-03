package com.motive.rest.util;


import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class AttendanceUtil {
    
    private MockMvc mvc;
    private MvcUtil mvcUtil;
    public AttendanceUtil(MockMvc mvc) {
        this.mvc = mvc;
        mvcUtil = new MvcUtil(mvc);
    }

    public void addPendingAttendee(JSONObject user, JSONObject motive, boolean anonymous) throws Exception{
        JSONObject attendanceRequest = new JSONObject();
        attendanceRequest.put("motive", motive.get("id"));
        attendanceRequest.put("anonymous", anonymous);
        mvcUtil.postAndExpectOk("/attendance/request", user, attendanceRequest);
    }


    public JSONObject addConfirmedAttendee(JSONObject owner, JSONObject friend,JSONObject motive, boolean anonymous) throws Exception{
     
        JSONObject attendanceRequest = new JSONObject();
        attendanceRequest.put("motive", motive.get("id"));
        attendanceRequest.put("anonymous", anonymous);
        mvcUtil.postAndExpectOk("/attendance/request", friend, attendanceRequest);

        JSONArray requests = getPendingRequests(friend, motive);

        JSONObject request = (JSONObject)requests.get(0);
        JSONObject attendanceResponse = new JSONObject();

        attendanceResponse.put("attendance", request.get("id"));
        attendanceResponse.put("accept", "true");

        mvcUtil.postAndExpectOk("/attendance/respond", owner, attendanceResponse);    

        return attendanceRequest;
    }

    public JSONArray getPendingRequests(JSONObject user, JSONObject motive) throws Exception{
        
        MvcResult result = mvc.perform(get("/attendance/pending").param("motiveId",String.valueOf(motive.get("id")))
        .header("authorization", user.get("token")))
        .andExpect(status().isOk()).andReturn();

        return mvcUtil.getArrayResponse(result);
    }
    public void respondToReponse(JSONObject user, JSONObject motive) throws Exception{
    
    }
}
