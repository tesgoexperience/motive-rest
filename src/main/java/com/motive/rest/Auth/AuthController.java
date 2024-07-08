package com.motive.rest.Auth;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.motive.rest.dto.RegisterUser;
import com.motive.rest.security.TokenService;

@RestController
public class AuthController {

    @Autowired
    TokenService tokenService;

    @Autowired
    AuthService service;

    @Autowired
    PasswordEncoder encoder;

    @PostMapping("login")
    public String login(Authentication authentication) {
        return tokenService.generateToken(authentication);
    }

    @PostMapping("register")
    public void register(  @Valid @ModelAttribute RegisterUser createUser) throws Exception{
        service.createUser(createUser);
    }
    
    @PostMapping(value = "/notification/add-token")
    @ResponseBody
    public boolean registerToken(@RequestBody String token) {
        return service.saveToken(token);
    }
}
