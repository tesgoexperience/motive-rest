package com.motive.rest.user.friendship;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.motive.rest.exceptions.IllogicalRequest;
import com.motive.rest.user.dto.SearchResultDTO;
import com.motive.rest.user.dto.SocialSummaryDTO;
import com.motive.rest.exceptions.EntityNotFound;

import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequestMapping(path = "/friendship")
public class FriendshipController {

    @Autowired
    FriendshipService service;

    @GetMapping(value = "/")
    @ResponseBody
    public SocialSummaryDTO friendSummary() {
        return service.getSocialSummaryDTO();
    }

    @PostMapping(value = "/remove")
    @ResponseBody
    public void removeFriend(@RequestParam String username) throws EntityNotFound {
        service.removeFriendship(username);
    }

    @PostMapping(value = "/accept")
    @ResponseBody
    public void acceptFriendRequest(@RequestParam String username) throws IllogicalRequest, EntityNotFound {
        service.respondToRequest(username, true);
    }

    @GetMapping(value = "/search", produces = "application/json")
    @ResponseBody
    public List<SearchResultDTO> searchUsers(@RequestParam String search) {
        return service.searchUsers(search);
    }

    @PostMapping(value = "/reject")
    @ResponseBody
    public void refuseFriendRequest(@RequestParam String username) throws IllogicalRequest, EntityNotFound {
        service.respondToRequest(username, false);
    }

    @PostMapping(value = "/request")
    @ResponseBody
    public void requestFriend(@RequestParam String username) throws EntityNotFound, IllogicalRequest {
        service.createRequest(username);
    }

}
