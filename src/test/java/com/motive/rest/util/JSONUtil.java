package com.motive.rest.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.minidev.json.JSONArray;

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

    public JSONObject motiveObjectSpecificFriends(JSONArray invited) {
        List<String> friendUsernames = new ArrayList<String>();

        for (int i = 0; i < invited.size(); i++) {
            friendUsernames.add(((JSONObject) invited.get(i)).get("username").toString());
        }

        JSONObject motive = baseMotiveObject();
        motive.appendField("attendanceType", "SPECIFIC_FRIENDS");
        motive.appendField("specificallyInvited", friendUsernames);

        return motive;
    }

    private JSONObject baseMotiveObject() {
        JSONObject motive = new JSONObject();
        motive.put("start",
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(faker.date().future(1, TimeUnit.DAYS)));
        motive.put("end",
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(faker.date().future(5,2, TimeUnit.DAYS)));                
        motive.appendField("title", faker.eldenRing().skill());
        motive.appendField("description", faker.bojackHorseman().quotes());
        return motive;
    }

    public JSONObject motiveObject(String attendanceType) {
        JSONObject motive = baseMotiveObject();
        motive.appendField("attendanceType", attendanceType);
        motive.appendField("specificallyInvited", new JSONArray());
        return motive;
    }

    public JSONObject toJsonObject(String jsonString) throws ParseException {
        return (JSONObject) parser.parse(jsonString);
    }

    public JSONArray toJsonArray(String jsonString) throws ParseException {
        return (JSONArray) parser.parse(jsonString);
    }


    public JSONObject attendanceResponseObject( String username, String uuid) {
        JSONObject attendanceResponse = new JSONObject();
        attendanceResponse.put("attendeeUsername",username);
        attendanceResponse.put("motiveId",uuid);

        return attendanceResponse;
    }








}
