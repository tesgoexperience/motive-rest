package com.motive.rest.motive;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import net.datafaker.Faker;
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
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import com.motive.rest.util.JSONUtil;
import com.motive.rest.util.MvcUtil;
import com.motive.rest.util.SimpleResponse;
import com.motive.rest.util.SocialUtil;

import net.minidev.json.JSONArray;

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
	private JSONObject social;
	private Faker faker = new Faker();

	@Before
	public void createContext() throws Exception {
		mvcUtil = new MvcUtil(mvc);
		json = new JSONUtil();
		socialUtil = new SocialUtil(mvc);
		social = socialUtil.createSocial();
	}

	// This test checks whether a user can create a motive and get the management
	// details
	@Test
	public void createManageMotive() throws UnsupportedEncodingException, Exception {
		String token = socialUtil.getToken(social);
		JSONObject friend = (JSONObject) ((JSONArray) social.get("friends")).get(0);

		// create a motive
		SimpleResponse res = mvcUtil.postRequest("/motive/create/",
				json.motiveObject("FRIENDS").toJSONString(), token);
 		String motiveId = res.getBodyAsJson().getAsString("id");

		// request motive as friend
		JSONObject request = new JSONObject();
		request.appendField("motive", motiveId);
		request.appendField("anonymous", "false");
		mvcUtil.postRequest("/attendance/request", request.toJSONString(),
				friend.getAsString("token"));

		// get managed motives
		JSONObject managedMotiveDTO = (JSONObject) mvcUtil.getRequest("/motive/managing", token)
				.getBodyAsJsonArray().get(0);
		assertEquals(motiveId, managedMotiveDTO.get("id"));
		assertEquals(friend.get("username"), json.toJsonArray(
				((JSONObject) managedMotiveDTO.get("managementDetails")).getAsString("requests")).get(0)
				.toString());

	}

	@Test
	public void MotiveDateSetToPast() throws UnsupportedEncodingException, Exception {
		String token = socialUtil.getToken(social);
		JSONObject motive = json.motiveObject("FRIENDS");
		motive.put("start",
				new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
						.format(faker.date().past(3, TimeUnit.DAYS)));
		SimpleResponse res = mvcUtil.postRequest("/motive/create/", motive.toJSONString(), token);
		assertEquals(res.getStatus(), HttpStatus.BAD_REQUEST);
		assertTrue(res.getBody().contains("Start date cannot be in the past"));
	}

	@Test
	public void MotiveEndDateBeforeStart() throws UnsupportedEncodingException, Exception {
		String token = socialUtil.getToken(social);
		JSONObject motive = json.motiveObject("FRIENDS");
		motive.put("start",
				new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
						.format(faker.date().future(4, 3, TimeUnit.DAYS)));
		motive.put("end",
				new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
						.format(faker.date().future(2, 1, TimeUnit.DAYS)));

		SimpleResponse res = mvcUtil.postRequest("/motive/create/", motive.toJSONString(), token);
		assertEquals(res.getStatus(), HttpStatus.BAD_REQUEST);
		assertTrue(res.getBody().contains("end date cannot before start"));
	}
	// This test check if a friend is able to view a motive posted by their friend
	@Test
	public void ViewMotiveOpenToOnlyFriends() throws UnsupportedEncodingException, Exception {
		String token = socialUtil.getToken(social);
		JSONObject friend = (JSONObject) ((JSONArray) social.get("friends")).get(0);
		JSONObject stranger = (JSONObject) ((JSONArray) social.get("outgoingRequests")).get(0);
		// create a motive
		SimpleResponse res = mvcUtil.postRequest("/motive/create/", json.motiveObject("FRIENDS").toJSONString(),
				token);
		JSONObject motive = json.toJsonObject(res.getBody());
		assertEquals(res.getStatus(), HttpStatus.OK);

		// Get motives as a friend
		SimpleResponse allMotives = mvcUtil.getRequest("/motive/all", (friend).getAsString("token"));

		// check the new motive from friend is in the list of available motives
		JSONObject motiveJSON = (JSONObject) allMotives.getBodyAsJsonArray().get(0);
		assertEquals(motiveJSON.get("id"), motive.get("id"));

		SimpleResponse allMotivesStranger = mvcUtil.getRequest("/motive/all", (stranger).getAsString("token"));
		assertEquals(0, allMotivesStranger.getBodyAsJsonArray().size());
	}

	// This test checks if a selected friend can view a motive and if a non selected
	// friend can view it
	@Test
	public void ViewMotiveOpenToOnlySpecificFriends() throws UnsupportedEncodingException, Exception {
		String token = socialUtil.getToken(social);

		JSONObject selectedFriend = (JSONObject) ((JSONArray) social.get("friends")).get(0);
		JSONObject nonSelectedFriend = (JSONObject) ((JSONArray) social.get("friends")).get(1);

		// create motive
		JSONArray jsonArray = new JSONArray();
		jsonArray.add(selectedFriend);
		SimpleResponse res = mvcUtil.postRequest("/motive/create/",
				json.motiveObjectSpecificFriends(jsonArray).toJSONString(), token);
		JSONObject motive = json.toJsonObject(res.getBody());

		// check the new motive from friend is in the list of available motives
		SimpleResponse allMotivesForSelectedFriend = mvcUtil.getRequest("/motive/all",
				selectedFriend.getAsString("token"));
		JSONObject motiveJSON = (JSONObject) allMotivesForSelectedFriend.getBodyAsJsonArray().get(0);
		assertEquals(motiveJSON.get("id"), motive.get("id"));

		SimpleResponse allMotivesForNonSelectedFriend = mvcUtil.getRequest("/motive/all",
				(nonSelectedFriend).getAsString("token"));
		assertEquals(0, allMotivesForNonSelectedFriend.getBodyAsJsonArray().size());
	}

	// checks whether this user can view their stats
	@Test
	public void getMotiveStats() throws UnsupportedEncodingException, Exception {
		String token = socialUtil.getToken(social);

		mvcUtil.postRequest("/motive/create/", json.motiveObject("FRIENDS").toJSONString(), token);

	 	assertEquals(1L, mvcUtil.getRequest("/motive/stats", token).getBodyAsJson().getAsNumber("attending"));
	}


}
