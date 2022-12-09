package com.motive.rest.motive.attendance.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AttendanceResponseDto {
    String attendeeUsername;
    Long motiveId; 
}
