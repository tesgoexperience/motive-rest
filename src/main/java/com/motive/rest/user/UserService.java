package com.motive.rest.user;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import javax.security.auth.message.callback.PrivateKeyCallback.Request;

import com.motive.rest.security.CustomUserDetailsService;
import com.motive.rest.user.DTO.SearchResultDTO;
import com.motive.rest.user.DTO.SocialSummaryDTO;
import com.motive.rest.user.Friend.FriendRequest;
import com.motive.rest.user.Friend.FriendRequestRepo;
import com.motive.rest.user.Friend.FriendRequest.REQUEST_STATUS;

import java.util.ArrayList;
import java.util.Date;

/* TODO methods should throw errors instead of returning a response entity
 * Instead the controller should catch errors from the service and throw errors
 * This the detail of teh response is not relevant to the service
 * Also, if another service uses this one, the response entity would not be as useful compared to errors thrown
*/

@Service
public class UserService {

    public enum REQUEST_RESPONSE {
        ACCEPT, REJECT, CANCEL
    }

    @Value("${JWT_SIGNATURE}")
    String JWTSignature;

    @Autowired
    UserRepo repo;

    @Autowired
    FriendRequestRepo friendRequestRepo;

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

        // TODO remove the need for roles JWT token generation doesnt work without this
        // roles array, not sure why
        user.setRoles(new String[] { "USER" });

        repo.save(user);

    }

    public List<SearchResultDTO> searchUsers(String search) {
        List<User> users = repo.findByUsernameContaining(search);

        ArrayList<SearchResultDTO> results = new ArrayList<SearchResultDTO>();
        for (User user : users) {
            // results.add(new SearchResultDTO(user.getUsername(), )));
        }
        return results;
    }

    public ResponseEntity<String> respondToRequest(Long id, REQUEST_RESPONSE response) {
        // not involved in
        User user = getCurrentUser();
        FriendRequest request = friendRequestRepo.findById(id).get();

        if (response.equals(REQUEST_RESPONSE.CANCEL)) {
            // check if the user has made this request
            List<FriendRequest> requests = user.getRequestsMade();
            if (requests.contains(request)) {
                friendRequestRepo.deleteById(request.getId());
                return new ResponseEntity<String>("Request was cancelled.", HttpStatus.OK);
            }
        }

        List<FriendRequest> requests = user.getRequestsReceived();
        if (!requests.contains(request)) {// TODO report to security if a user tries to respond to friend request they
                                          // are not involved in
            return new ResponseEntity<String>("You are not authorized to reject this request. ", HttpStatus.UNAUTHORIZED);
        }

        if (response.equals(REQUEST_RESPONSE.ACCEPT)) {
            List<User> responderFriendList = user.getFriends();
            responderFriendList.add(request.getRequester());
            user.setFriends(responderFriendList);

            User requester = request.getRequester();
            List<User> requesterFriendList = requester.getFriends();
            requesterFriendList.add(user);

            repo.save(user);
            repo.save(requester);
        }

        friendRequestRepo.delete(request);

        return new ResponseEntity<String>("You have " + response + "ED the friend request ", HttpStatus.OK);
    }

    public ResponseEntity<String> createRequest(String username) {
        User friend = repo.findByUsername(username);
        if (friend == null) {
            return new ResponseEntity<String>("Username not found. ", HttpStatus.BAD_REQUEST);
        }

        User user = getCurrentUser();
        if (friend.equals(user)) {
            return new ResponseEntity<String>("You cannot request yourself ", HttpStatus.BAD_REQUEST);
        }

        List<FriendRequest> friendRequests = user.getRequestsMade();
        friendRequests.addAll(user.getRequestsReceived());

        for (FriendRequest req : friendRequests) {
            if (req.getStatus() == REQUEST_STATUS.PENDING) {
                if (req.getFriend().equals(friend)) {
                    return new ResponseEntity<String>("You have already requested this user.", HttpStatus.CONFLICT);
                } else if (req.getRequester().equals(friend)) {
                    return new ResponseEntity<String>("This user has already requested you.", HttpStatus.CONFLICT);
                }
            }
        }

        for (User f : user.getFriends()) {
            if (f.equals(friend)) {
                return new ResponseEntity<String>("You are already friends with this user.", HttpStatus.CONFLICT);
            }
        }

        friendRequestRepo.save(new FriendRequest(user, friend));

        return new ResponseEntity<String>("Friend requested", HttpStatus.OK);
    }

    public SocialSummaryDTO getSocialSummaryDTO() {
        User user = getCurrentUser();

        List<Pair<String, Long>> reqMade = new ArrayList<>();
        for (FriendRequest req : user.getRequestsMade()) {
            reqMade.add(Pair.of(req.getFriend().getUsername(), req.getId()));
        }

        List<Pair<String, Long>> reqReceived = new ArrayList<>();
        for (FriendRequest req : user.getRequestsReceived()) {
            reqReceived.add(Pair.of(req.getRequester().getUsername(), req.getId()));
        }

        List<String> friends = new ArrayList<>();
        for (User friend : user.getFriends()) {
            friends.add(friend.getUsername());
        }

        return new SocialSummaryDTO(friends, reqReceived, reqMade);
    }
}
