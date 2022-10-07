package com.motive.rest.user.Friendship.Circle;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.motive.rest.exceptions.IllogicalRequest;
import com.motive.rest.user.Friendship.Circle.DTO.CircleDTO;
import com.motive.rest.exceptions.BadUserInput;
import com.motive.rest.exceptions.EntityNotFound;


@Controller
@RequestMapping(path = "friendship/circle")
@PreAuthorize("isAuthenticated()")
public class CircleController {
    @Autowired
    CircleService service;

    @PostMapping(value = "/create")
    @ResponseBody
    public void createCircle(@RequestBody Map<String,String> request) throws IllogicalRequest, EntityNotFound {
        service.createCircle(request.get("name"), request.get("color"));
    }

    @PostMapping(value = "/delete")
    @ResponseBody
    public void deleteCircle(@RequestBody String name) throws EntityNotFound {
        service.deleteCircle(name);
    }

    @GetMapping(value = "/")
    @ResponseBody
    public List<CircleDTO> getAllCircles() throws EntityNotFound {
       return service.getAllCircles();
    }

    @PostMapping(value = "/add")
    @ResponseBody
    public void addFriend(@RequestBody Map<String,String> request) throws EntityNotFound {
       service.editCircle(request.get("friend"), request.get("circle"), true);
    }

    @PostMapping(value = "/remove")
    @ResponseBody
    public void removeFriend(@RequestBody Map<String,String> request) throws EntityNotFound {
       service.editCircle(request.get("friend"), request.get("circle"), false);
    }

}
