package com.motive.rest.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.motive.rest.Auth.AuthService;
import com.motive.rest.dto.DTOFactory;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequestMapping(path = "/user")
public class UserController {

  @Autowired
  DTOFactory dtoFactory;

  @Autowired
  AuthService auth;
  
  // return information about the authenticated user
  @GetMapping(value = "/")
  @ResponseBody
  public String home() throws Exception {
    return "hello " + auth.getAuthUser().getUsername();
  }

}
