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

import com.motive.rest.exceptions.IllogicalRequest;
import com.motive.rest.exceptions.BadUserInput;
import com.motive.rest.exceptions.EntityNotFound;

@Service
public class FriendshipService {

    public static final String USER_NOT_FRIEND_ERROR = "USER IS NOT YOUR FRIEND";

    @Autowired
    private UserService userService;

    @Autowired
    private FriendRepo repo;

    // Only use get the specific friendship status. Do not use to find out if users
    // are friends
    public USER_RELATIONSHIP getSpecificRelationship(User otherUser) {
        User user = userService.getCurrentUser();

        for (Friendship req : user.getRequestsMade()) {
            if (req.getReceiver().equals(otherUser)) {
                if (req.isApproved()) {
                    return USER_RELATIONSHIP.FRIEND;
                }
                return USER_RELATIONSHIP.REQUESTED_BY_YOU;
            }
        }

        for (Friendship req : user.getRequestsReceived()) {
            if (req.getRequester().equals(otherUser)) {
                if (req.isApproved()) {
                    return USER_RELATIONSHIP.FRIEND;
                }
                return USER_RELATIONSHIP.REQUESTED_BY_THEM;
            }
        }

        return USER_RELATIONSHIP.NO_RELATION;

    }

    public void validateFriendship(User otherUser) {
        if (!isFriends(otherUser)) {
            throw new BadUserInput(USER_NOT_FRIEND_ERROR);
        }
    }

    public List<User> getApprovedFriendshipsUserObjects() {
       return extractFriendUserObjects(getApprovedFriendships());
    }

    // returns all the other users who in the friendship with the context user
    private List<User> extractFriendUserObjects(List<Friendship> friendships){
        List<User> friends = new ArrayList<>();
        for (Friendship friendship : friendships) {
            if (friendship.getRequester().equals(userService.getCurrentUser())) {
                friends.add(friendship.getReceiver());
            } else {
                friends.add(friendship.getRequester());
            }
        }
        return friends;
    }
    public boolean isFriends(User otherUser) {
        return getApprovedFriendshipsUserObjects().contains(otherUser);
    }

    public List<Friendship> getApprovedFriendships() {
        return getFriendships(false, true);
    }

    public Friendship getFriendshipWithUser(String username) throws EntityNotFound {
        return getFriendshipWithUser(userService.findByUsername(username));
    }

    private List<Friendship> getFriendships(boolean includePending, boolean includeApproved) {

        User user = userService.getCurrentUser();
        List<Friendship> friendships = user.getRequestsMade();
        friendships.addAll(user.getRequestsReceived());

        if (!includePending) {
            friendships.removeIf(e -> !e.isApproved());
        }

        if (!includeApproved) {
            friendships.removeIf(Friendship::isApproved);
        }

        return friendships;
    }

    // NOTE includes pending friendships
    public List<Friendship> getAllFriendshipsIncludingPending() {
        return getFriendships(true, true);
    }

    public List<Friendship> getPendingFriendships() {
        return getFriendships(true, false);
    }

    public Friendship getFriendshipWithUser(User friend) throws EntityNotFound {
        List<Friendship> friendships = getAllFriendshipsIncludingPending();
        for (Friendship friendship : friendships) {
            if (friendship.getReceiver() == friend || friendship.getRequester() == friend) {
                return friendship;
            }
        }

        throw new EntityNotFound(USER_NOT_FRIEND_ERROR);
    }

    public void removeFriendship(String username) throws EntityNotFound {

        User friend = userService.findByUsername(username);

        Friendship friendship = getFriendshipWithUser(friend);
        repo.delete(friendship);
    }

    public List<SearchResultDTO> searchUsers(String search) {
        List<User> users = userService.findByUsernameContaining(search);

        ArrayList<SearchResultDTO> results = new ArrayList<SearchResultDTO>();
        for (User user : users) {
            results.add(new SearchResultDTO(user.getUsername(), getSpecificRelationship(user)));
        }

        return results;
    }

    public void respondToRequest(String username, boolean accept) throws IllogicalRequest, EntityNotFound {

        User user = userService.getCurrentUser();
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
        } else {
            repo.delete(request);
        }

    }

    public void createRequest(String username) throws EntityNotFound, IllogicalRequest {
        // TODO push to friend's notification stack
        User friend = userService.findByUsername(username);
        User user = userService.getCurrentUser();

        if (friend == null) {
            throw new EntityNotFound("User not found.");
        }

        if (friend.equals(user)) {
            throw new IllogicalRequest("You cannot request yourself.");
        }

        if (extractFriendUserObjects(getAllFriendshipsIncludingPending()).contains(friend)) {
            throw new IllogicalRequest("friendship already exists or is pending.");
        }

        repo.save(new Friendship(user, friend));
    }

    public SocialSummaryDTO getSocialSummaryDTO() {
        User user = userService.getCurrentUser();

        List<String> reqMade = new ArrayList<>();
        List<String> reqReceived = new ArrayList<>();
        List<String> friends = new ArrayList<>();

        for (Friendship req : user.getRequestsMade()) {
            String username = req.getReceiver().getUsername();
            if (req.isApproved()) {
                friends.add(username);
            } else {
                reqMade.add(username);

            }
        }

        for (Friendship req : user.getRequestsReceived()) {
            String username = req.getRequester().getUsername();
            if (req.isApproved()) {
                friends.add(username);
            } else {
                reqReceived.add(username);

            }
        }

        return new SocialSummaryDTO(friends, reqReceived, reqMade);
    }

}
