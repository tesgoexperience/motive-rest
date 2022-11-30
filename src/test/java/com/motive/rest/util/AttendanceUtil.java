package com.motive.rest.util;

import org.springframework.test.web.servlet.ResultMatcher;

import net.minidev.json.JSONObject;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class AttendanceUtil {
    
    private MockMvc mvc;
    private MvcUtil mvcUtil;
    public AttendanceUtil(MockMvc mvc) {
        this.mvc = mvc;
        mvcUtil = new MvcUtil(mvc);
    }

    public void requestAttendance(JSONObject user, JSONObject motive) throws Exception{
        JSONObject attendanceRequest = new JSONObject();
        attendanceRequest.put("motive", motive.get("id"));
        attendanceRequest.put("anonymous", true);
        mvcUtil.postAndExpectOk("/attendance/request", user, attendanceRequest);
    }
}
