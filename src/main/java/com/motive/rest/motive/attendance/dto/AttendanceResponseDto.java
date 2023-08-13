package com.motive.rest.motive.attendance.dto;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AttendanceResponseDto {
    String attendeeUsername;
    UUID motiveId; 
}
