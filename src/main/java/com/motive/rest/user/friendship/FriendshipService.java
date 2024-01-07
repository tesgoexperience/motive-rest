package com.motive.rest.user.friendship;

import org.springframework.beans.factory.annotation.Autowired;

import com.motive.rest.user.User;
import com.motive.rest.user.UserService;
import com.motive.rest.user.dto.SearchResultDTO;
import com.motive.rest.user.dto.SocialSummaryDTO;
import com.motive.rest.user.dto.SearchResultDTO.USER_RELATIONSHIP;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.motive.rest.exceptions.IllogicalRequest;
import com.motive.rest.notification.NotificationService;
import com.motive.rest.Auth.AuthService;
import com.motive.rest.exceptions.BadUserInput;
import com.motive.rest.exceptions.EntityNotFound;

@Service
public class FriendshipService {

    public static final String USER_NOT_FRIEND_ERROR = "USER IS NOT YOUR FRIEND";

    @Autowired
    private UserService userService;

    @Autowired
    private FriendRepo repo;

    @Autowired
    AuthService authService;

    @Autowired
    private NotificationService notificationService;

    /**
     * Will find all users this user has an approved friendship with.
     * 
     * @return the list of users this user has resoved frienships requests with
     * 
     */
    public List<User> getFriends() {
        return extractFriends(repo.findApprovedRequests(authService.getAuthUser().getId().toString()));
    }

    /**
     * Get the friendship requests this user has recieved
     * 
     * @param includeApproved if false, will return only pending requests recieved.
     *                        If true, it will returns pending and approved
     *                        requests.
     * @return the users this user has recieved requests from
     */
    public List<User> getRequestsRecieved(boolean includeApproved) {
        return extractFriends(repo.findRequestsRecieved(authService.getAuthUser().getId().toString(), includeApproved));
    }

    /**
     * Get the friendship requests this user has sent
     * 
     * @param includeApproved if false, will return only pending requests sent. If
     *                        true, it will returns pending and approved requests.
     * @return the users this user has sent requests
     */
    public List<User> getRequestsSent(boolean includeApproved) {
        return extractFriends(repo.findRequestsSent(authService.getAuthUser().getId().toString(), includeApproved));
    }

    /**
     * Friendships consist of a receiver and sender. Either one could be this user
     * and this method will extract the other friend
     * 
     * @params friendships the list of freindships which need the friend to
     *         extracted from
     * @return the list of friends extracted from the friendship objects
     */
    private List<User> extractFriends(List<Friendship> friendships) {
        List<User> friends = new ArrayList<>();
        for (Friendship friendship : friendships) {
            if (friendship.getSender().equals(authService.getAuthUser())) {
                friends.add(friendship.getReceiver());
            } else {
                friends.add(friendship.getSender());
            }
        }

        return friends;
    }

    /**
     * if the context user is not friends with this user, throw an error
     * 
     * @param username of the friend we are checking against
     * @throws BadUserInput
     */
    public void validateFriendship(String username) {
        validateFriendship(userService.findByUsername(username));
    }

    /**
     * if the context user is not friends with this user, throw an error
     * 
     * @param otherUser the friend we are checking against
     * @throws BadUserInput
     */
    public void validateFriendship(User otherUser) {
        if (!isFriends(otherUser)) {
            throw new BadUserInput(USER_NOT_FRIEND_ERROR);
        }
    }

    public boolean isFriends(User otherUser) {
        return getFriends().contains(otherUser);
    }

    public Friendship getFriendshipWithUser(User friend) throws EntityNotFound {
        Optional<Friendship> friendship = repo.findFriendship(authService.getAuthUser().getId().toString(),
                friend.getId().toString());

        if (friendship.isPresent()) {
            return friendship.get();
        }

        throw new EntityNotFound(USER_NOT_FRIEND_ERROR);
    }

    public void removeFriendship(String username) throws EntityNotFound {

        User friend = userService.findByUsername(username);

        Friendship friendship = getFriendshipWithUser(friend);
        repo.delete(friendship);
    }

    public void respondToRequest(String username, boolean accept) throws IllogicalRequest, EntityNotFound {

        User user = authService.getAuthUser();
        User friend = userService.findByUsername(username);

        // get the request received from this friend
        Friendship request = getFriendshipWithUser(friend);

        if (!request.getReceiver().equals(user)) {
            throw new EntityNotFound("You have not received this request.");
        }

        if (request.isApproved()) {
            throw new IllogicalRequest("Request is already approved.");
        }

        if (accept) {
            request.setApproved(true);
            repo.save(request);
            notificationService.notify("New friend", user.getUsername() + " accepted your friend request",
                    friend.getAuthDetails().getNotificationToken());
        } else {
            repo.delete(request);
        }

    }

    public void createRequest(String username) throws EntityNotFound, IllogicalRequest {
        // TODO push to friend's notification stack
        User friend = userService.findByUsername(username);
        User user = authService.getAuthUser();

        if (friend == null) {
            throw new EntityNotFound("User not found.");
        }

        if (friend.equals(user)) {
            throw new BadUserInput("You cannot request yourself.");
        }

        if (getFriends().contains(user) || getRequestsRecieved(false).contains(user)
                || getRequestsSent(false).contains(user)) {
            throw new IllogicalRequest("friendship already exists or is pending.");
        }

        repo.save(new Friendship(user, friend));
        
        notificationService.notify("New friend Request", user.getUsername() + " sent you a friend request",
                friend.getAuthDetails().getNotificationToken());
    }

    private USER_RELATIONSHIP getSpecificRelationship(User otherUser) {

        if (getFriends().contains(otherUser)) {
            return USER_RELATIONSHIP.FRIEND;
        }

        if (getRequestsRecieved(false).contains(otherUser)) {
            return USER_RELATIONSHIP.REQUESTED_BY_THEM;
        }

        if (getRequestsSent(false).contains(otherUser)) {
            return USER_RELATIONSHIP.REQUESTED_BY_YOU;
        }

        return USER_RELATIONSHIP.NO_RELATION;

    }

    public List<SearchResultDTO> searchUsers(String search) {
        List<User> users = userService.findByUsernameContaining(search);

        ArrayList<SearchResultDTO> results = new ArrayList<SearchResultDTO>();
        for (User user : users) {
            results.add(new SearchResultDTO(user.getUsername(), getSpecificRelationship(user)));
        }

        return results;
    }

    public SocialSummaryDTO getSocialSummaryDTO() {
        List<String> reqMade = getRequestsSent(false).stream().map(e -> e.getUsername()).collect(Collectors.toList());
        List<String> reqReceived = getRequestsRecieved(false).stream().map(e -> e.getUsername())
                .collect(Collectors.toList());
        List<String> friends = getFriends().stream().map(e -> e.getUsername()).collect(Collectors.toList());

        return new SocialSummaryDTO(friends, reqMade, reqReceived);
    }

}
