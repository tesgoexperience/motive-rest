package com.motive.rest.user.dto;

import com.motive.rest.dto.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
@AllArgsConstructor
public class UserDto implements DTO{
    private String email;
    private String username;
}
