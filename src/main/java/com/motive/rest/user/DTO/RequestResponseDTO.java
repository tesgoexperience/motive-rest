package com.motive.rest.user.DTO;

import com.motive.rest.user.UserService.REQUEST_RESPONSE;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class RequestResponseDTO {
    String username;
    REQUEST_RESPONSE response;
}
