package com.motive.rest.motive.status.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class StatusBrowseDTO {
    Long id;
    String title;
    String owner;
    Date timePosted;
    boolean belongsToMe;
    boolean interested;
}
