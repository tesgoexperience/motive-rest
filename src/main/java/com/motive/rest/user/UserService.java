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

import com.motive.rest.security.CustomUserDetailsService;

import java.util.Date;

@Service
public class UserService {

    @Value("${JWT_SIGNATURE}")
    String JWTSignature;

    @Autowired
    UserRepository repo;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    public static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    public String login(String email, String password) {

        User user = repo.findByEmail(email);
        if (user != null && PASSWORD_ENCODER.matches(password, user.getPassword())) {
            // generate a toke
            return getJWTToken(user);
        }

        throw new BadCredentialsException("Bad credentials");
    }

    public User getCurrentUser() {
        return userDetailsService.getCurrentUser();
    }

    public boolean validateUserInfo(User user) {

        // check password has Minimum eight characters, at least one letter, one number
        // and one special character:
        // regex source
        // https://stackoverflow.com/questions/19605150/regex-for-password-must-contain-at-least-eight-characters-at-least-one-number-a
        Pattern passwordPattern = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$");
        Boolean passwordSecure = passwordPattern.matcher(user.getPassword()).find();

        // email will also be verified. So no need for email
        // https://owasp.org/www-community/OWASP_Validation_Regex_Repository
        Pattern emailPattern = Pattern
                .compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
        Boolean emailValid = emailPattern.matcher(user.getEmail()).find();

        return passwordSecure && emailValid;
    }

    public String encodePassword(String password) {
        return PASSWORD_ENCODER.encode(password);
    }

    public String getJWTToken(User user) {

        List<GrantedAuthority> grantedAuthorities = AuthorityUtils.createAuthorityList(user.getRoles());

        String token = Jwts.builder().setId("mg").setSubject(user.getEmail())
                .claim("authorities",
                        grantedAuthorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 600000)) // 600000 is 10 minute expiration
                .signWith(SignatureAlgorithm.HS512, JWTSignature.getBytes()).compact();

        return "Bearer " + token;
    }

    public String getJWTSignature() {
        return JWTSignature;
    }

    public boolean userExists(User user) {
        return repo.findByEmail(user.getEmail()) != null;
    }

    public void registerNewUser(User user) {

        user.setPassword(this.encodePassword(user.getPassword()));
        // this.set new String[] { "USER" });

        // TODO remove the need for roles JWT token generation doesnt work without this roles array, not sure why
        user.setRoles(new String[] { "USER" } );

        repo.save(user);

    }
}
