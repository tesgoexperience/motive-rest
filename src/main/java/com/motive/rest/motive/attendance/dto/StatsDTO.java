package com.motive.rest.motive.attendance.dto;

import com.motive.rest.dto.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatsDTO implements DTO{
    int attending;
    int finished;
    int All;
}