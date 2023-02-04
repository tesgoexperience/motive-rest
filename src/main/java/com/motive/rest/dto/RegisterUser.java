package com.motive.rest.dto;



import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class RegisterUser {

    @NotBlank @Size(max = 20, min=5, message = "Username length must be between 5-20")
    String username;
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$")
    String password;
    @NotNull
    String confirmPassword;
    @NotNull
    String email;


}
