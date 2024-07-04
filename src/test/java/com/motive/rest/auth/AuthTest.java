package com.motive.rest.auth;

import com.motive.rest.util.JSONUtil;
import com.motive.rest.util.MvcUtil;
import com.motive.rest.util.SimpleResponse;
import net.minidev.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // used to run with "test" profile when executing outside gradle
public class AuthTest {

    @Autowired
    private MockMvc mvc;

    private JSONUtil json;
    private MvcUtil mvcUtil;

    @Before
    public void createContext() throws Exception {
        mvcUtil = new MvcUtil(mvc);
        json = new JSONUtil();
    }

    @Test
    public void register() throws Exception {
        assertEquals( HttpStatus.OK, mvcUtil.postRequest("/register", json.userObject().toJSONString()).getStatus());
    }

    @Test
    public void login() throws Exception {
        JSONObject user = json.userObject();
        assertEquals(HttpStatus.OK, mvcUtil.postRequest("/register", user.toJSONString()).getStatus());

        SimpleResponse response = mvcUtil.postRequest("/login", "", user.get("email").toString(),
                user.get("password").toString());
        assertEquals(response.getStatus(), HttpStatus.OK);
        assertTrue(response.getBody().contains("Bearer"));
        assertEquals(HttpStatus.OK, mvcUtil.getRequest("/user/", response.getBody()).getStatus());

        SimpleResponse username = mvcUtil.getRequest("/user/", response.getBody());
        assertEquals(user.get("username"), username.getBody());
    }

    @Test
    public void login_with_invalid_credentials() throws Exception {
        SimpleResponse response = mvcUtil.postRequest("/login", "", "test", "test");
        assertEquals(response.getStatus(), HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void register_with_used_email() throws Exception {

        JSONObject user = json.userObject();
        assertEquals(mvcUtil.postRequest("/register", user.toJSONString()).getStatus(), HttpStatus.OK);

        JSONObject userTwo = json.userObject();
        userTwo.put("email", user.get("email"));

        SimpleResponse response = mvcUtil.postRequest("/register", userTwo.toJSONString());
        assertEquals(response.getStatus(), HttpStatus.BAD_REQUEST);
        assertTrue(response.getBody().contains("email is already in use"));
    }

    @Test
    public void register_with_used_username() throws Exception {
        JSONObject user = json.userObject();
        assertEquals(mvcUtil.postRequest("/register", user.toJSONString()).getStatus(), HttpStatus.OK);

        JSONObject userTwo = json.userObject();
        userTwo.put("username", user.get("username"));

        SimpleResponse response = mvcUtil.postRequest("/register", userTwo.toJSONString());
        assertEquals(response.getStatus(), HttpStatus.BAD_REQUEST);
        assertTrue(response.getBody().contains("username is already in use"));
    }

    @Test
    public void register_with_unequal_passwords() throws Exception {
        JSONObject user = json.userObject();
        user.put("confirmPassword", "12123");

        SimpleResponse response = mvcUtil.postRequest("/register", user.toJSONString());
        assertEquals(response.getStatus(), HttpStatus.BAD_REQUEST);
        assertTrue(response.getBody().contains("confirm password must match password"));
    }

    @Test
    public void register_with_invalid_password() throws Exception {

        JSONObject user = json.userObject();
        user.put("password", "123");

        SimpleResponse response = mvcUtil.postRequest("/register", user.toJSONString());
        assertEquals(response.getStatus(), HttpStatus.BAD_REQUEST);
        assertTrue(response.getBodyAsJson().get("password").toString().contains("must match"));

    }

    @Test
    public void register_with_invalid_email() throws Exception {

        JSONObject user = json.userObject();
        user.put("email", "dave");

        SimpleResponse response = mvcUtil.postRequest("/register", user.toJSONString());
        assertEquals(response.getStatus(), HttpStatus.BAD_REQUEST);
        assertTrue(response.getBodyAsJson().get("email").toString()
                .contains("must be a well-formed email address"));
    }

    @Test
    public void register_with_invalid_username() throws Exception {
        JSONObject user = json.userObject();
        user.put("email", "dave");

        SimpleResponse response = mvcUtil.postRequest("/register", user.toJSONString());
        assertEquals(response.getStatus(), HttpStatus.BAD_REQUEST);
        assertTrue(response.getBodyAsJson().get("email").toString()
                .contains("must be a well-formed email address"));
    }

}
