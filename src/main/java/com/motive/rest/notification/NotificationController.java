package com.motive.rest.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.motive.rest.Auth.AuthService;

import org.springframework.stereotype.Controller;
@Controller
@RequestMapping(path = "/notification")
@PreAuthorize("isAuthenticated()")
public class NotificationController {
    
    @Autowired
    AuthService authService;

    @PostMapping(value = "/add-token")
    @ResponseBody
    public boolean createMotive(@RequestBody String token) {
        return authService.saveToken(token);
    }

}
