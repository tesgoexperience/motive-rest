package com.motive.rest.motive.attendance;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.motive.rest.dto.DTOFactory.DTO_TYPE;
import com.motive.rest.motive.attendance.dto.AttendanceDTO;
import com.motive.rest.motive.attendance.dto.ResponseDto;

import org.springframework.stereotype.Controller;

@Controller
@RequestMapping(path = "/attendance")
@PreAuthorize("isAuthenticated()")
public class AttendanceController {

    @Autowired
    AttendanceService service;

    @PostMapping(value = "/request")
    @ResponseBody
    public void requestAttendance(@RequestBody Map<String,String> req){
        service.requestAttendance(Long.valueOf(req.get("motive")), req.get("anonymous").equals("true"));
    }

    @GetMapping(value = "/pending")
    @ResponseBody
    public List<AttendanceDTO> getPendingRequests(@RequestParam String motiveId){
        return service.getPendingAttendance(Long.valueOf(motiveId));
    }


    @PostMapping(value = "/respond")
    @ResponseBody
    public void respondToAttendanceRequest(@RequestBody ResponseDto req){
        service.respondToAttendanceRequest(req);
    }

}
