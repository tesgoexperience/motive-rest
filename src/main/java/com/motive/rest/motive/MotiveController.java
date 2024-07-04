package com.motive.rest.motive;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.motive.rest.dto.DTO;
import com.motive.rest.motive.attendance.dto.StatsDTO;
import com.motive.rest.motive.dto.CreateMotiveDTO;
import com.motive.rest.motive.dto.MotiveDTO;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequestMapping(path = "/motive")
public class MotiveController {

    @Autowired
    MotiveService service;

    @GetMapping(value = "/all")
    @ResponseBody
    public List<MotiveDTO> browseMotives() {
        return service.browseMotives();
    }

    @GetMapping(value = "/attending")
    @ResponseBody
    public List<MotiveDTO> attending() {
        return service.getAttending();
    }

    @GetMapping(value = "/stats")
    @ResponseBody
    public StatsDTO stats() {
        return service.getStats();
    }

    @GetMapping(value = "/managing")
    @ResponseBody
    public List<MotiveDTO> managingMotives() {
        return service.manageMotives();
    }

    @GetMapping(value = "/past")
    @ResponseBody
    public List<MotiveDTO> getPastMotives() {
        return service.getPastMotives();
    }

    @PostMapping(value = "/create")
    @ResponseBody //todo use manageDto rather than createMotiveDto here to make reduce repition for updates
    public ResponseEntity<MotiveDTO> createMotive(@RequestBody CreateMotiveDTO motive) {
        MotiveDTO dto = service.createMotive(motive.getTitle(), motive.getDescription(), motive.getStart(), motive.getEnd(), motive.getAttendanceType(),
                motive.getSpecificallyInvited());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping(value = "/get")
    @ResponseBody //todo use manageDto rather than createMotiveDto here to make reduce repition for updates
    public ResponseEntity<DTO> getMotive(@RequestParam UUID motive) {
        return new ResponseEntity<>(service.getMotiveDto(motive), HttpStatus.OK);
    }
}
