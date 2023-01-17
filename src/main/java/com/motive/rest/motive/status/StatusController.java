package com.motive.rest.motive.status;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.motive.rest.motive.status.dto.StatusBrowseDTO;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping(path = "/status")
@PreAuthorize("isAuthenticated()")
public class StatusController {

    @Autowired
    StatusService service;

    @GetMapping(value = "/")
    @ResponseBody
    public List<StatusBrowseDTO> browseMotives() {
        return service.getAll();
    }

    @PostMapping(value = "/create")
    @ResponseBody
    public ResponseEntity<Boolean> createStatus(@RequestBody String status) {
        return service.createStatus(status);
    }

    @PostMapping(value = "/interest/add")
    @ResponseBody
    public ResponseEntity<Boolean> addInterest(@RequestBody Long status) {
        return new ResponseEntity<Boolean>(service.showInterest(status, true), HttpStatus.OK);
    }

    @PostMapping(value = "/interest/remove")
    @ResponseBody
    public ResponseEntity<Boolean> removeInterest(@RequestBody Long status) {
        return new ResponseEntity<Boolean>(service.showInterest(status, false), HttpStatus.OK);
    }

    @PostMapping(value = "/interest/") // returns a list of usernames of the people who are interested in this status
    @ResponseBody
    public List<String> createStatus(@RequestBody Long status) {
        return service.getInterests(status);
    }

}
