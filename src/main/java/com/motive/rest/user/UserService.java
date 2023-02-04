package com.motive.rest.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.motive.rest.exceptions.EntityNotFound;
// import com.motive.rest.security.CustomUserDetailsService;

import java.util.Date;

@Service
public class UserService {

    public enum REQUEST_RESPONSE {
        ACCEPT, REJECT, CANCEL, REMOVE_FRIEND
    }

    @Value("${JWT_SIGNATURE}")
    String JWTSignature;

    @Autowired
    UserRepo repo;



    public User getCurrentUser() {
        return null;
    }


    public boolean userExists(User user) {
        return repo.findByEmail(user.getEmail()) != null || repo.findByUsername(user.getUsername()) != null;
    }

    public User findByUsername(String username) throws EntityNotFound{
        User user = repo.findByUsername(username);

        if (user==null) {
            throw new EntityNotFound("User not found");
        }

        return user;
    }


    public  List<User>  findByUsernameContaining(String username) throws EntityNotFound{
        List<User> user = repo.findByUsernameContaining(username);

        if (user==null) {
            throw new EntityNotFound("User not found");
        }

        return user;
    }
   
}
