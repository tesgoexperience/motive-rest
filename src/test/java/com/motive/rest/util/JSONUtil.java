package com.motive.rest.util;

import com.github.javafaker.Faker;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

public class JSONUtil {
    private Faker faker = new Faker();
    private JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);

    public JSONObject userObject(){
        JSONObject user = new JSONObject();

        user.put("username", faker.name().username());
        user.put("email", faker.name().username() + "@gmail.com");
        user.put("password", "Test123!");
        user.put("confirmPassword", "Test123!");

        return user;
    }

    public JSONObject toJsonObject(String jsonString) throws ParseException{
        return (JSONObject)parser.parse(jsonString);
    }
}
