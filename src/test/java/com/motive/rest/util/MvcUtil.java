package com.motive.rest.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

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

    public SimpleResponse postRequestWithParam(String url, String paramName, String paramValue, String token)
            throws UnsupportedEncodingException, Exception {
        return resultToSimpleResponse(mvc.perform(post(url)
                .param(paramName, paramValue)
                .header("authorization", token)).andReturn());
    }

    public SimpleResponse postRequestWithoutBody(String url, String token) throws Exception {
        return resultToSimpleResponse(mvc.perform(post(url)
                .header("authorization", token)).andReturn());
    }

    public SimpleResponse postRequest(String url, String body) throws UnsupportedEncodingException, Exception {
        return resultToSimpleResponse(mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andReturn());
    }

    public SimpleResponse getRequest(String url, String token) throws UnsupportedEncodingException, Exception {
        MvcResult browseMotiveResult = mvc.perform(get(url).header("authorization", token))
                .andReturn();
        return resultToSimpleResponse(browseMotiveResult);
    }

    public SimpleResponse getRequest(String url, String param, String token) throws UnsupportedEncodingException, Exception {
        MvcResult browseMotiveResult = mvc.perform(get(url).header("authorization", token).param("motiveId", param))
                .andReturn();
        return resultToSimpleResponse(browseMotiveResult);
    }

    private SimpleResponse resultToSimpleResponse(MvcResult mvcResult) throws UnsupportedEncodingException {
        return new SimpleResponse(mvcResult.getResponse().getContentAsString(),
                HttpStatus.valueOf(mvcResult.getResponse().getStatus()));
    }

}
