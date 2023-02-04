package com.motive.rest.user;

import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.motive.rest.dto.DTOFactory;
import com.motive.rest.dto.DTOFactory.DTO_TYPE;
import com.motive.rest.user.dto.UserDto;

import org.springframework.web.bind.annotation.GetMapping;

//TODO refactor according to https://github.com/joseph-redmond/lLamaWork/blob/abf66589b9720a4f5c330e982b0789d6de9572e9/src/main/java/tech/jrdev/llamawork/web/rest/UserJWTController.java
@Controller
@RequestMapping(path = "/user")
@PreAuthorize("isAuthenticated()")
public class UserController {

  @Autowired
  DTOFactory dtoFactory;

  // return information about the authenticated user
  @GetMapping(value = "/")
  @ResponseBody
  public String home(Authentication auth) throws Exception {
    return auth.getName();
  }

}
