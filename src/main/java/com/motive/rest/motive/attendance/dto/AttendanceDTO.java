package com.motive.rest.motive.attendance.dto;

import com.motive.rest.dto.DTO;
import com.motive.rest.motive.Motive;
import com.motive.rest.motive.attendance.Attendance.ATTENDANCE_STATUS;
import com.motive.rest.user.User;

import lombok.Getter;
import lombok.Setter;


public class AttendanceDTO implements DTO{
    @Getter @Setter private ATTENDANCE_STATUS status;
    @Getter @Setter private boolean anonymous;
    @Getter @Setter private Long id;

    @Setter private User user;
    @Setter private Motive motive;

    public String getUser(){
        return user.getUsername();
    }
}
