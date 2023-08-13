package com.motive.rest.util;

import org.springframework.test.web.servlet.MockMvc;
import net.minidev.json.JSONObject;

public class MotiveUtil {
    private JSONUtil json;
    private MvcUtil mvcUtil;

    public MotiveUtil(MockMvc mvc) {
        mvcUtil = new MvcUtil(mvc);
        json = new JSONUtil();
    }
    
}