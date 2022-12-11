package com.motive.rest.motive;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.motive.rest.motive.attendance.dto.StatsDTO;
import com.motive.rest.motive.dto.CreateMotiveDTO;
import com.motive.rest.motive.dto.MotiveBrowseDTO;
import com.motive.rest.motive.dto.MotiveManageDTO;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequestMapping(path = "/motive")
@PreAuthorize("isAuthenticated()")
public class MotiveController {

    @Autowired
    MotiveService service;

    @GetMapping(value = "/all")
    @ResponseBody
    public List<MotiveBrowseDTO> browseMotives() {
        return service.browseMotives();
    }

    @GetMapping(value = "/attending")
    @ResponseBody
    public List<MotiveBrowseDTO> attending() {
        return service.getAttending();
    }

    @GetMapping(value = "/stats")
    @ResponseBody
    public StatsDTO stats() {
        return service.getStats();
    }

    @GetMapping(value = "/managing")
    @ResponseBody
    public List<MotiveManageDTO> managingMotives() {
        return service.manageMotives();
    }

    @PostMapping(value = "/create")
    @ResponseBody
    public ResponseEntity<MotiveManageDTO> createMotive(@RequestBody CreateMotiveDTO motive) {
        MotiveManageDTO dto = service.createMotive(motive.getTitle(), motive.getDescription(), motive.getStart(),
                motive.getHiddenFrom());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}
