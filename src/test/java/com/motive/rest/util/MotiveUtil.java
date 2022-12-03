package com.motive.rest.util;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.github.javafaker.Faker;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class MotiveUtil {
    protected Faker faker;
    protected MockMvc mvc;
    private JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    private MvcUtil mvcUtil;
    public MotiveUtil(MockMvc mvc) {
        this.mvc = mvc;
        mvcUtil = new MvcUtil(mvc);
        faker = new Faker();

    }

    /**
     * Create and save a simple motive with a random TITLE,DESCRIPTION,START and an
     * empty HIDDENFROM
     * It will set the OWNER to the provided creator
     * 
     * @param creator
     * @return returns the resulting MotiveManageDTO from the creation
     * @throws Exception
     */
    public JSONObject generateSimpleMotiveAndSave(JSONObject creator) throws Exception {
        return (JSONObject) parser.parse(save(generateSimpleMotive(), creator).getResponse().getContentAsString());
    }

    /**
     * Create a simple motive with a random TITLE,DESCRIPTION,START and an empty
     * HIDDENFROM
     *
     * @param creator
     * @return simple motive object
     * @throws Exception
     */
    public JSONObject generateSimpleMotive() throws Exception {
        JSONObject motive = new JSONObject();
        motive.put("title", faker.job().title());
        motive.put("description", faker.elderScrolls().quote());
        motive.put("hiddenFrom", new JSONArray());
        String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        motive.put("start", simpleDateFormat.format(faker.date().future(1, TimeUnit.DAYS)));

        return motive;
    }

    /**
     * Saves the motive to the database. Result is expected to be ok
     */
    public MvcResult save(JSONObject motive, JSONObject user) throws Exception {
        return mvcUtil.postAndExpectOk("/motive/create", user, motive);
    }

    /**
     * Tests if a creationDTO was successfully created by comparing it to the
     * resulting manageDTO returned by the API
     * 
     * @param createMotiveDTO is the object that is created by the client
     * @param motiveManageDTO is the result of getting the created motive from the
     *                        API so will have additional fields which will also be
     *                        tested
     */
    public void compareCreateDTOWithManageDTO(JSONObject createMotiveDTO, JSONObject motiveManageDTO) {
        assertEquals(motiveManageDTO.get("title"), createMotiveDTO.get("title"));
        assertEquals(motiveManageDTO.get("description"), createMotiveDTO.get("description"));
        assertFalse((boolean) motiveManageDTO.get("finished"));
        assertTrue(((JSONArray) motiveManageDTO.get("hiddenFrom")).isEmpty());
        assertTrue(((JSONArray) motiveManageDTO.get("confirmedAttendance")).isEmpty());
        assertTrue(((JSONArray) motiveManageDTO.get("confirmedAttendance")).isEmpty());
        assertTrue(((JSONArray) motiveManageDTO.get("requests")).isEmpty());
        assertFalse((motiveManageDTO.get("id")).toString().isEmpty());
        assertEquals(simpleDateFormat.format((Long) motiveManageDTO.get("start")),
                simpleDateFormat.format((Long) motiveManageDTO.get("start")));
    }

    /**
     * Tests if a browseMotiveDTO is derived from the same object as a
     * motiveManageDTO
     * 
     * @param browseMotiveDTO is the object that is created by the client
     * @param motiveManageDTO is the result of getting the created motive from the
     *                        API so will have additional fields which will also be
     *                        tested
     */
    public void compareBrowseDTOWithManageDTO(JSONObject browseMotiveDTO, JSONObject motiveManageDTO) {
        assertEquals(browseMotiveDTO.get("title"), motiveManageDTO.get("title"));
        assertEquals(browseMotiveDTO.get("description"), motiveManageDTO.get("description"));
        assertEquals(simpleDateFormat.format((Long) browseMotiveDTO.get("start")),
                simpleDateFormat.format((Long) motiveManageDTO.get("start")));
        assertEquals(browseMotiveDTO.get("confirmedAttendance"), motiveManageDTO.get("confirmedAttendance"));
        assertEquals(browseMotiveDTO.get("ownerUsername"), motiveManageDTO.get("ownerUsername"));
    }

    public JSONArray browseMotives(JSONObject user) throws Exception {
        return mvcUtil.getArrayAndExpectOk("/motive/", user);
    }
}
