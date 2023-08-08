package com.motive.rest.util;

import java.io.UnsupportedEncodingException;
 
import org.springframework.test.web.servlet.MockMvc;
import net.minidev.json.JSONObject;

public class AuthUtil {
    private JSONUtil json;
    private MvcUtil mvcUtil;

    public AuthUtil(MockMvc mvc) {
        mvcUtil = new MvcUtil(mvc);
        json = new JSONUtil();
    }
    
    public JSONObject registerUser() throws UnsupportedEncodingException, Exception {
        JSONObject user = json.userObject();
        mvcUtil.postRequest("/register", user.toJSONString());
        SimpleResponse response = mvcUtil.postRequest("/login", "", user.get("email").toString(),
                user.get("password").toString());
        user.appendField("token", response.getBody());
        return user;
    }
}
