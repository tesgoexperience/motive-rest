package com.motive.rest.user.Friendship;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.motive.rest.exceptions.BadInteraction;
import com.motive.rest.exceptions.EntityNotFound;
import com.motive.rest.user.DTO.SearchResultDTO;
import com.motive.rest.user.DTO.SocialSummaryDTO;

import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequestMapping(path = "/friendship")
@PreAuthorize("isAuthenticated()")
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
    public void acceptFriendRequest(@RequestParam String username) throws BadInteraction, EntityNotFound {
        service.respondToRequest(username, true);

    }

    @GetMapping(value = "/search", produces = "application/json")
    @ResponseBody
    public List<SearchResultDTO> searchUsers(@RequestParam String search) {
        return service.searchUsers(search);
    }

    @PostMapping(value = "/reject")
    @ResponseBody
    public void refuseFriendRequest(@RequestParam String username) throws BadInteraction, EntityNotFound {
        service.respondToRequest(username, false);
    }

    @PostMapping(value = "/request")
    @ResponseBody
    public void requestFriend(@RequestParam String username) throws EntityNotFound, BadInteraction {
        service.createRequest(username);
    }

}
