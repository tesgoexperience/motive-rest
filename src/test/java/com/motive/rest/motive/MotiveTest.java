package com.motive.rest.motive;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.UnsupportedEncodingException;

import com.motive.rest.user.dto.SocialSummaryDTO;
import com.motive.rest.util.JSONUtil;
import com.motive.rest.util.MvcUtil;
import com.motive.rest.util.SimpleResponse;
import com.motive.rest.util.SocialUtil;

import org.junit.Before;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class MotiveTest {
    @Autowired
    private MockMvc mvc;

    private JSONUtil json;
    private MvcUtil mvcUtil;
    private SocialUtil socialUtil;

    @Before
    public void  createContext() throws Exception {
        mvcUtil = new MvcUtil(mvc);
        json = new JSONUtil();
        socialUtil = new SocialUtil(mvc);
    }

    /**
     * Create an event
     * @throws Exception
     * @throws UnsupportedEncodingException
     */
    @Test
    public void createEvent() throws UnsupportedEncodingException, Exception {
        JSONObject user = socialUtil.createSocial();
        
    }
}
