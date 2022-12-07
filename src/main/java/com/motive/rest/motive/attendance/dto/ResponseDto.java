package com.motive.rest.motive.attendance.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ResponseDto {
    String username;
    Long motiveId; // TODO based on these two details we should be able to get the id. Update the tests. Rather than having to pass around and find the attendance id
    Long attendance; //TODO should be changed to the motive id. We should be able to get the atte
}
