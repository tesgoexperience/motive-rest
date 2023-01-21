package com.motive.rest.motive.dto;

import java.util.Date;
import com.motive.rest.dto.DTO;
import com.motive.rest.motive.Motive.ATTENDANCE_TYPE;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
@Getter
@Setter
@NoArgsConstructor
public class CreateMotiveDTO implements DTO {

    private String title;
    private String description;
    private Date start;
    private ATTENDANCE_TYPE attendanceType;
    private String[] specificallyInvited;

}