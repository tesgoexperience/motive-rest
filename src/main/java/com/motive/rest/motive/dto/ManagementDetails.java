package com.motive.rest.motive.dto;

import java.util.ArrayList;
import java.util.List;

import com.motive.rest.dto.DTO;
import com.motive.rest.motive.Invite.Invite;
import com.motive.rest.motive.attendance.Attendance;
import com.motive.rest.motive.attendance.Attendance.ATTENDANCE_STATUS;


public class ManagementDetails implements DTO {

    private List<Attendance> attendance;
    private List<Invite>  specificallyInvited;
    public ManagementDetails(List<Attendance> attendance, List<Invite> specificallyInvited) {
        this.attendance = attendance;
        this.specificallyInvited = specificallyInvited;
    }

    public ManagementDetails() {
        this.attendance = new ArrayList<Attendance>();
        this.specificallyInvited = new ArrayList<>();
    }

    public List<String> getRequests() {
        List<String> requesterUsernames = new ArrayList<>();
        for (Attendance att : attendance) {
            if (att.getStatus().equals(ATTENDANCE_STATUS.REQUESTED)) {
                requesterUsernames.add(att.getUser().getUsername());
            }
        }
        return requesterUsernames;
    }

    public List<String> getAnonymousAttendees() {
        List<String> attending = new ArrayList<>();
        for (Attendance att :  attendance) {
            if (att.getStatus().equals(ATTENDANCE_STATUS.CONFIRMED) && att.isAnonymous()) {
                attending.add(att.getUser().getUsername());
            }
        }
        return attending;
    }

    public List<String> getSpecificallyInvited() {
        List<String> users = new ArrayList<>();

        for (Invite invite : specificallyInvited) {
            users.add(invite.getUser().getUsername());
        }

        return users;

    }
}
