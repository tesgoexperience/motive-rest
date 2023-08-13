package com.motive.rest.util;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.io.UnsupportedEncodingException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

public class MvcUtil {

    private MockMvc mvc;

    public MvcUtil(MockMvc mvc) {
        this.mvc = mvc;
    }

    public SimpleResponse postRequest(String url, String body, String username, String password)
            throws UnsupportedEncodingException, Exception {
        return resultToSimpleResponse(mvc.perform(post(url).with(httpBasic(username, password))).andReturn());
    }

    
    public SimpleResponse postRequest(String url, String body, String token)
            throws UnsupportedEncodingException, Exception {
        return resultToSimpleResponse(mvc.perform(post(url)
        .contentType(MediaType.APPLICATION_JSON)
        .content(body)
        .header("authorization", token)).andReturn());
    }

    public SimpleResponse postRequest(String url, String body) throws UnsupportedEncodingException, Exception {
        return resultToSimpleResponse(mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andReturn());
    }

    public SimpleResponse getRequest(String url, String token) throws UnsupportedEncodingException, Exception{
        MvcResult browseMotiveResult = mvc.perform(get(url).header("authorization", token))
        .andReturn();
        return resultToSimpleResponse(browseMotiveResult);
    }

    private SimpleResponse resultToSimpleResponse(MvcResult mvcResult) throws UnsupportedEncodingException {
        return new SimpleResponse(mvcResult.getResponse().getContentAsString(),
                HttpStatus.valueOf(mvcResult.getResponse().getStatus()));
    }

}
