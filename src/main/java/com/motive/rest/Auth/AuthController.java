package com.motive.rest.Auth;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.motive.rest.dto.RegisterUser;
import com.motive.rest.security.TokenService;
import com.motive.rest.user.User;
import com.motive.rest.user.UserRepo;

@RestController
public class AuthController {

    @Autowired
    TokenService tokenService;

    @Autowired
    UserRepo repo;

    @Autowired
    PasswordEncoder encoder;

    @PostMapping("login")
    public String login(Authentication authentication) {
        return tokenService.generateToken(authentication);
    }

    @PostMapping("register")
    public void register( @RequestBody @Valid RegisterUser createUser) throws Exception{
        User user = new User(createUser.getEmail(), encoder.encode(createUser.getPassword()), createUser.getUsername());
        user.setRoles(new String[] { "role" });
        repo.save(user);
    }
    
}
