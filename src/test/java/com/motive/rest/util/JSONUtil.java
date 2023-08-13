package com.motive.rest.util;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import com.nimbusds.jose.shaded.json.JSONArray;

import net.datafaker.Faker;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

public class JSONUtil {
    private Faker faker = new Faker();
    private JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);

    public JSONObject userObject() {
        JSONObject user = new JSONObject();

        user.put("username", faker.name().username());
        user.put("email", faker.name().username() + "@gmail.com");
        user.put("password", "Test123!");
        user.put("confirmPassword", "Test123!");

        return user;
    }

    public JSONObject motiveObject(String attendanceType) {
        JSONObject motive = new JSONObject();
        motive.appendField("title", faker.eldenRing().skill());
        motive.appendField("description", faker.bojackHorseman().quotes());
        motive.appendField("attendanceType", attendanceType);
        motive.appendField("specificallyInvited", new JSONArray());
        motive.put("start",
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(faker.date().future(1, TimeUnit.DAYS)));

        return motive;
    }

    public JSONObject toJsonObject(String jsonString) throws ParseException {
        return (JSONObject) parser.parse(jsonString);
    }
}
