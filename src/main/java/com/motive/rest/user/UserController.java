package com.motive.rest.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.motive.rest.Auth.AuthService;
import com.motive.rest.dto.DTOFactory;
import com.motive.rest.exceptions.EntityNotFound;
import com.motive.rest.image.ImageService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.MediaType;

@Controller
@RequestMapping(path = "/user")
public class UserController {

  @Autowired
  DTOFactory dtoFactory;

  @Autowired
  AuthService auth;

  @Autowired
  UserRepo repo;
  
  @Autowired
  ImageService imageService;

  // return information about the authenticated user
  @GetMapping(value = "/")
  @ResponseBody
  public String home() throws Exception {
    return auth.getAuthUser().getUsername();
  }

  @GetMapping(value = "/picture")
  @ResponseBody
  public ResponseEntity<?> getProfilePicture(@RequestParam String username) throws Exception {

     User user = repo.findByUsername(username).orElseThrow(()->  new EntityNotFound("User not found"));
    
     if(user.getProfilePic() == null){
        
     }
 
     return ResponseEntity.status(HttpStatus.OK)
     .contentType(MediaType.valueOf(user.profilePic.getType()))
     .body(imageService.getImage(user.getProfilePic().getId()));  
  }
  
}
