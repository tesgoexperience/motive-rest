package com.motive.rest.motive.dto;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.motive.rest.dto.DTO;

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
    private String[] hiddenFrom;

}