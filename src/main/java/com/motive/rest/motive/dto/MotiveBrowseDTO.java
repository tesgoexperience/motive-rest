package com.motive.rest.motive.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.motive.rest.dto.DTO;
import com.motive.rest.motive.attendance.Attendance;
import com.motive.rest.motive.attendance.Attendance.ATTENDANCE_STATUS;
import com.motive.rest.user.User;

import lombok.Getter;
import lombok.Setter;

public class MotiveBrowseDTO implements DTO{
    @Getter @Setter private Long id;
    @Getter @Setter private String title;
    @Getter @Setter private String description;
    @Getter @Setter private Date start;


    @Setter  private User owner;
    @Setter  private List<Attendance> attendance;

    public String getOwnerUsername(){
        return this.owner.getUsername();
    }

    public List<String> getConfirmedAttendance(){
        List<String> attending = new ArrayList<>();
        for (Attendance att : attendance) {
            if (att.getStatus().equals(ATTENDANCE_STATUS.CONFIRMED)) {
                attending.add(att.getUser().getUsername());
            }
        }
        return attending;
    }
}
