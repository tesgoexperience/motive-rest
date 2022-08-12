package com.motive.rest.user;

import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.GetMapping;

import com.motive.rest.user.DTO.RequestResponseDTO;
import com.motive.rest.user.DTO.SearchResultDTO;
import com.motive.rest.user.DTO.SocialSummaryDTO;
@Controller
@RequestMapping(path = "/user")
@PreAuthorize("isAuthenticated()")
public class UserController {

  @Autowired
  private UserService service;

  // returns user info
  @GetMapping(value = "/", produces = "application/json")
  @ResponseBody
  public User home() throws Exception {
    User dto = service.getCurrentUser(); // TODO return DTO
    return dto;
  }

  // Tokens have a 5 minute expiration. So we give the client the ability to
  // refresh using previous token
  @GetMapping(value = "/refresh")
  @ResponseBody
  public String generateNewToken() {

    // get authenticated
    String newToken = service.getJWTToken(service.getCurrentUser());

    return newToken;
  }

  @PostMapping(value = "/friend/respond")
  @ResponseBody
  public ResponseEntity<String> respondToFriendRequest(@RequestBody RequestResponseDTO response) {
       return service.respondToRequest(response.getId(), response.getResponse());
  }

  @PostMapping(value = "/friend/request")
  @ResponseBody
  public ResponseEntity<String> requestFriendRequest(@RequestBody String username) {
       return service.createRequest(username);
  }

  @GetMapping(value = "/friend")
  @ResponseBody
  public SocialSummaryDTO friendSummary() {
       return service.getSocialSummaryDTO();
  }

 
  @GetMapping(value = "/search-users", produces = "application/json")
  @ResponseBody
  public List<SearchResultDTO> searchUsers(@RequestParam String search) {
    return service.searchUsers(search);
  }

  @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
  @ResponseBody
  @PreAuthorize("isAnonymous()")
  public ResponseEntity<String> login(@RequestBody LinkedHashMap<String, String> user) {

    String token;
    try {
      token = service.login(user.get("email"), user.get("password"));
      return new ResponseEntity<String>(token, HttpStatus.OK);
    } catch (BadCredentialsException e) {
      return new ResponseEntity<String>("Invalid credentials", HttpStatus.NOT_ACCEPTABLE);
    }

  }

  @PreAuthorize("isAnonymous()")
  @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
  @ResponseBody
  public ResponseEntity<String> register(@RequestBody User user) {

    if (!service.validateUserInfo(user)) {
      return new ResponseEntity<String>("password or email doesn't meet requirements", HttpStatus.BAD_REQUEST);
    }

    // first check if user already exists
    if (service.userExists(user)) {
      return new ResponseEntity<String>("User already exists", HttpStatus.CONFLICT);
    }

    service.registerNewUser(user);

    return new ResponseEntity<String>("success", HttpStatus.OK);
  }

}
