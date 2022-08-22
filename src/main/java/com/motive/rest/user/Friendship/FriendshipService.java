package com.motive.rest.user.Friendship;

import org.springframework.beans.factory.annotation.Autowired;

import com.motive.rest.user.User;
import com.motive.rest.user.UserService;
import com.motive.rest.user.DTO.SearchResultDTO.USER_RELATIONSHIP;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import com.motive.rest.exceptions.BadInteraction;
import com.motive.rest.exceptions.EntityNotFound;
import com.motive.rest.user.DTO.SearchResultDTO;
import com.motive.rest.user.DTO.SocialSummaryDTO;

@Service
public class FriendshipService {

    @Autowired
    private UserService userService;

    @Autowired
    private FriendRepo repo;

    public USER_RELATIONSHIP getRelation(User otherUser) {
        User user = userService.getCurrentUser();

        for (Friendship req : user.getRequestsMade()) {
            if (req.getReceiver().equals(otherUser)) {
                if (req.approved) {
                    return USER_RELATIONSHIP.FRIEND;
                }
                return USER_RELATIONSHIP.REQUESTED_BY_YOU;
            }
        }

        for (Friendship req : user.getRequestsReceived()) {
            if (req.getRequester().equals(otherUser)) {
                if (req.approved) {
                    return USER_RELATIONSHIP.FRIEND;
                }
                return USER_RELATIONSHIP.REQUESTED_BY_THEM;
            }
        }

        return USER_RELATIONSHIP.NO_RELATION;

    }

    public List<Friendship> getAllFriendships() {

        User user = userService.getCurrentUser();
        List<Friendship> friendships = user.getRequestsMade();
        friendships.addAll(user.getRequestsReceived());

        return friendships;
    }

    public Friendship getFriendshipWithUser(User friend) throws EntityNotFound {
        List<Friendship> friendships = getAllFriendships();
        for (Friendship friendship : friendships) {
            if (friendship.getReceiver() == friend || friendship.getRequester() == friend) {
                return friendship;
            }
        }

        throw new EntityNotFound("Friendship with user was not found");
    }

    public List<Friendship> getPendingFriendships() {
        List<Friendship> friendships = getAllFriendships();
        friendships.removeIf(e -> e.approved);
        return friendships;
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
            results.add(new SearchResultDTO(user.getUsername(), getRelation(user)));
        }

        return results;
    }

    public void respondToRequest(String username, boolean accept) throws BadInteraction, EntityNotFound {

        User user = userService.getCurrentUser();
        User friend = userService.findByUsername(username);

        // get the request received from this friend
        Friendship request = getFriendshipWithUser(friend);

        if (!request.getReceiver().equals(user)) {
            throw new EntityNotFound("You have not received this request.");
        }

        if (request.approved) {
            throw new BadInteraction("Request is already approved.");
        }

        if (accept) {
            request.setApproved(true);
            repo.save(request);
        } else {
            repo.delete(request);
        }

    }

    public void createRequest(String username) throws EntityNotFound, BadInteraction {

        User friend = userService.findByUsername(username);
        User user = userService.getCurrentUser();

        if (friend == null) {
            throw new EntityNotFound("User not found.");
        }

        if (friend.equals(user)) {
            throw new BadInteraction("You cannot request yourself.");
        }

        USER_RELATIONSHIP relation = getRelation(friend);
        if (!relation.equals(USER_RELATIONSHIP.NO_RELATION)) {
            throw new BadInteraction(relation.getMessage());
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
            if (req.approved) {
                friends.add(username);
            } else {
                reqMade.add(username);

            }
        }

        for (Friendship req : user.getRequestsReceived()) {
            String username = req.getRequester().getUsername();
            if (req.approved) {
                friends.add(username);
            } else {
                reqReceived.add(username);

            }
        }

        return new SocialSummaryDTO(friends, reqReceived, reqMade);
    }

}
