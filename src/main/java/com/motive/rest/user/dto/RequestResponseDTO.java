package com.motive.rest.user.dto;

import com.motive.rest.user.UserService.REQUEST_RESPONSE;
import com.motive.rest.dto.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class RequestResponseDTO implements DTO{
    String username;
    REQUEST_RESPONSE response;
}
