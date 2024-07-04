package com.motive.rest.motive.attendance;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.motive.rest.motive.attendance.dto.AttendanceDTO;
import com.motive.rest.motive.attendance.dto.AttendanceResponseDto;

import org.springframework.stereotype.Controller;

@Controller
@RequestMapping(path = "/attendance")
public class AttendanceController {

    @Autowired
    AttendanceService service;

    @PostMapping(value = "/request")
    @ResponseBody
    public void requestAttendance(@RequestBody Map<String, String> req) {
        service.requestAttendance(UUID.fromString(req.get("motive")), req.get("anonymous").equals("true"));
    }

    @GetMapping(value = "/pending")
    @ResponseBody
    public List<AttendanceDTO> getAllPendingRequests(@RequestParam UUID motiveId) { // todo move to body
        return service.getPendingAttendance(motiveId);
    }

    @GetMapping(value = "/")
    @ResponseBody
    public AttendanceDTO getAttendanceForMotive(@RequestParam UUID motiveId) {
        return service.motiveAttendance(motiveId);
    }

    @PostMapping(value = "/accept")
    @ResponseBody
    public void respondToAttendanceRequestAccept(@RequestBody AttendanceResponseDto req) {
        service.respondToAttendanceRequest(req, true);
    }

    @PostMapping(value = "/reject")
    @ResponseBody
    public void respondToAttendanceRequest(@RequestBody AttendanceResponseDto req) {
        service.respondToAttendanceRequest(req, false);
    }

    @PostMapping(value = "/remove")
    @ResponseBody
    public void removeAttendee(@RequestBody AttendanceResponseDto req) {
        service.removeAttendee(req);
    }

    @PostMapping(value = "/cancel")
    @ResponseBody
    public void cancelAttendance(@RequestBody UUID motiveId) {
        service.cancelMyAttendance(motiveId);
    }

}
