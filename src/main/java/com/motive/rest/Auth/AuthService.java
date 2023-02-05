package com.motive.rest.Auth;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.motive.rest.Util.ServiceInterface;
import com.motive.rest.dto.RegisterUser;
import com.motive.rest.user.User;
import com.motive.rest.user.UserService;

@Service
public class AuthService implements ServiceInterface<AuthDetails> {

    @Autowired
    UserService userService;
    @Autowired
    AuthRepo repo;

    @Autowired
    PasswordEncoder passwordEncoder;

    public void createUser(RegisterUser userDetails) {
        User user = new User(userDetails.getUsername());
        AuthDetails authDetails = new AuthDetails(userDetails.getEmail(),
                passwordEncoder.encode(userDetails.getPassword()), user);
        user.setAuthDetails(authDetails);
        userService.save(user);
    }

    @Override
    public AuthDetails save(AuthDetails object) {
        return repo.save(object);
    }

    @Override
    public void delete(AuthDetails object) {
        repo.delete(object);
    }

    @Override
    public AuthDetails findById(UUID id) {
        return repo.findById(id).orElseThrow();
    }

    public User getAuthUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return repo.findByEmail(authentication.getName()).orElseThrow().getOwner();
    }

}
