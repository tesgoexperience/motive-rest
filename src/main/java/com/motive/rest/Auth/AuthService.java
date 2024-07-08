package com.motive.rest.Auth;


import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.motive.rest.dto.RegisterUser;
import com.motive.rest.exceptions.BadUserInput;
import com.motive.rest.image.ImageService;
import com.motive.rest.user.User;
import com.motive.rest.user.UserService;

@Service
public class AuthService  {

    @Autowired
    UserService userService;

    @Autowired
    AuthRepo repo;

    @Autowired
    ImageService imageService;

    @Autowired
    PasswordEncoder passwordEncoder;

    public void createUser(RegisterUser userDetails) throws IOException {

        if (!userDetails.getConfirmPassword().equals(userDetails.getPassword())) {
            throw new BadUserInput("confirm password must match password.");
        }

        if (repo.findByEmail(userDetails.getEmail()).isPresent()) {
            throw new BadUserInput("email is already in use.");
        }

        if (userService.usernameExists(userDetails.getUsername())) {
            throw new BadUserInput("username is already in use.");
        }

        User user = new User(userDetails.getUsername());
        AuthDetails authDetails = new AuthDetails(userDetails.getEmail(),
                passwordEncoder.encode(userDetails.getPassword()), user);

        user.setAuthDetails(authDetails);

        if(userDetails.getFile() != null) {
            user.setProfilePic(imageService.uploadImage(userDetails.getFile()));

        }

        userService.save(user);
    }

	public boolean saveToken(String token) {
		AuthDetails authDetails = getAuthUser().getAuthDetails();
        authDetails.setNotificationToken(token);
        repo.save(authDetails);
        return true;
	}

    public User getAuthUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return repo.findByEmail(authentication.getName()).orElseThrow().getOwner();
    }

}
