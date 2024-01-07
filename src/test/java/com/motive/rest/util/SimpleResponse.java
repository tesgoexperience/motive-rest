package com.motive.rest.util;

import org.springframework.http.HttpStatus;

import net.minidev.json.JSONArray;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.ParseException;

@Data
@AllArgsConstructor
public class SimpleResponse {
    String body;
    HttpStatus status;

    public JSONObject getBodyAsJson() throws ParseException {
        return new JSONUtil().toJsonObject(body);
    }

    public JSONArray getBodyAsJsonArray() throws ParseException {
        return new JSONUtil().toJsonArray(body);
    }
}
