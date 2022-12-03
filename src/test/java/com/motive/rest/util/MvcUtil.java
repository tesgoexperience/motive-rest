package com.motive.rest.util;

import org.springframework.test.web.servlet.ResultMatcher;

import ch.qos.logback.core.status.Status;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.io.UnsupportedEncodingException;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class MvcUtil {

    private MockMvc mvc;
    private JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);

    public MvcUtil(MockMvc mvc) {
        this.mvc = mvc;
    }

    public void postAndExpectError(String url, JSONObject user, JSONObject body, String expectedErrorMessage,
            ResultMatcher expectedStatus) throws Exception {
        // The motives should be returned in the same order they were created in
        MvcResult mvcResult = mvc.perform(post(url).header("authorization", user.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body.toJSONString()))
                .andExpect(expectedStatus)
                .andReturn();

        assertTrue(mvcResult.getResolvedException().toString().contains(expectedErrorMessage));
    }

    public MvcResult postAndExpectOk(String url, JSONObject user, JSONObject body)
            throws Exception {
        return mvc.perform(post(url).header("authorization", user.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body.toJSONString()))
                .andExpect(status().isOk())
                .andReturn();
    }

    public MvcResult postAndExpect(String url, JSONObject user, JSONObject body, ResultMatcher expectedStatus)
            throws Exception {
        return mvc.perform(post(url).header("authorization", user.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body.toJSONString()))
                .andExpect(expectedStatus)
                .andReturn();
    }

    public JSONArray getArrayAndExpectOk(String url, JSONObject user) throws Exception {

        MvcResult browseMotiveResult = mvc.perform(get(url).header("authorization", user.get("token")))
                .andExpect(status().isOk())
                .andReturn();

        return getArrayResponse(browseMotiveResult);
    }

    public JSONArray getArrayAndExpectOk(String url, JSONObject user, String param) throws Exception {

        MvcResult browseMotiveResult = mvc.perform(get(url).header("authorization", user.get("token")))
                .andExpect(status().isOk())
                .andReturn();

        return getArrayResponse(browseMotiveResult);
    }

    public JSONArray getArrayResponse( MvcResult result) throws UnsupportedEncodingException, ParseException{
        return (JSONArray) parser.parse(result.getResponse().getContentAsString());

    }
}
