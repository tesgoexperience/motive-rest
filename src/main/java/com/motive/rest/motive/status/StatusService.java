package com.motive.rest.motive.status;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.motive.rest.Auth.AuthService;
import com.motive.rest.exceptions.EntityNotFound;
import com.motive.rest.exceptions.IllogicalRequest;
import com.motive.rest.exceptions.UnauthorizedRequest;
import com.motive.rest.motive.status.dto.StatusBrowseDTO;
import com.motive.rest.notification.NotificationService;
import com.motive.rest.user.User;
import com.motive.rest.user.UserService;
import com.motive.rest.user.friendship.FriendshipService;

@Service
public class StatusService {

    @Autowired
    StatusRepo repo;

    @Autowired
    UserService userService;

    @Autowired
    FriendshipService friendshipService;

    @Autowired
    AuthService authService;

    @Autowired
    private NotificationService notificationService;

    public List<StatusBrowseDTO> getAll() {
        User currentUser = authService.getAuthUser();
        // friends who can see the status
        List<User> friends = friendshipService.getFriends();
        for (User friend : friends) {
            if (friend.getHideStatusFrom().contains(currentUser)) {
                friends.remove(friend);
            }
        }
        List<StatusBrowseDTO> statusList = repo.findByOwnerAndNotExpired(currentUser.getId()).stream()
                .map(s -> toStatusBrowseDTO(s)).collect(Collectors.toList());
        // add the statuses by the current users
        for (User friend : friends) {
            for (Status status : repo.findByOwnerAndNotExpired(friend.getId())) {
                statusList.add(toStatusBrowseDTO(status));
            }
        }

        return statusList;
    }

    private Interest getInterest(Status status, User user) {
        for (Interest interest : status.getInterest()) {
            if (interest.getUser().equals(user)) {
                return interest;
            }
        }
        return null;
    }

    private StatusBrowseDTO toStatusBrowseDTO(Status status) {
        User currentUser = authService.getAuthUser();

        return new StatusBrowseDTO(status.getId().toString(), status.getTitle(), status.getOwner().getUsername(),
                status.getCreateDate(), currentUser.equals(status.getOwner()), getInterest(status, currentUser)!=null);
    }

    public boolean showInterest(UUID statusID, boolean add) {
        // check they are friends
        User currentUser = authService.getAuthUser();
        Status status = getById(statusID);
        
        friendshipService.validateFriendship(status.getOwner());

        if (!add) {
            Interest interest =  getInterest(status, currentUser);
            if (interest==null) {
                throw new IllogicalRequest("You have no in this motive");
            }

            repo.deleteInterest(interest.getId());
            return true;
        }

        if (status.getOwner().getHideStatusFrom().contains(currentUser)) {
            throw new UnauthorizedRequest("status not found.");
        }

        status.getInterest().add(new Interest(status, currentUser));
        repo.save(status);
        notificationService.
        notify("Status", currentUser.getUsername()+ " is interested", status.getOwner().getAuthDetails().getNotificationToken());
        return true;
    }

    public Status getById(UUID statusID) {
        Optional<Status> status = repo.findById(statusID);
        if (!status.isPresent()) {
            throw new EntityNotFound("status not found.");
        }
        return status.get();
    }

    public ResponseEntity<Boolean> createStatus(String status) {
        
        User user = authService.getAuthUser();
        repo.save(new Status(status,user));
        
        List<User> friends = friendshipService.getFriends();
        for (User friend : friends) {
            if (user.getHideStatusFrom().contains(friend)) {
                friends.remove(friend);
            }
        }

        String statusSubstring = status;
        if (status.length() > 50) {
            statusSubstring = status.substring(0, 49)+"...";
        }
        for (User friend : friends) {
            notificationService.notify("New status from " +user.getUsername(), statusSubstring, friend.getAuthDetails().getNotificationToken());
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    public void ValidateOwnership(Status status){
        if (!status.getOwner().equals( authService.getAuthUser())) {
            throw new UnauthorizedRequest("Permission denied.");
        }
    }

    public List<String> getInterests(UUID statusId) {
        Status status = getById(statusId);
        ValidateOwnership(status);

        List<String> interestedUsers = status.getInterest().stream().map(s -> s.getUser().getUsername()).collect(Collectors.toList());
        
        return interestedUsers;
    }
}
