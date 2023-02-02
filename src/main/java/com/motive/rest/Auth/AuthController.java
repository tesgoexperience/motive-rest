package com.motive.rest.Auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.motive.rest.security.TokenService;

@RestController
public class AuthController {
    
    @Autowired
    TokenService tokenService;

    @PostMapping("login")
    public String login(Authentication authentication){
        return tokenService.generateToken(authentication);
    }

    @GetMapping("register")
    public String register(){
        return "nice";
    }
}
