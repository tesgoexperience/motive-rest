package com.motive.rest.motive.attendance.dto;

import com.motive.rest.motive.attendance.Attendance;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ResponseDto {
    boolean accept;
    Long attendance;
}
