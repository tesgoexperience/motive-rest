package com.motive.rest.user.Friendship.Circle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.motive.rest.exceptions.BadUserInput;
import com.motive.rest.exceptions.EntityNotFound;
import com.motive.rest.exceptions.IllogicalRequest;
import com.motive.rest.user.User;
import com.motive.rest.user.UserService;
import com.motive.rest.user.Friendship.FriendshipService;
import com.motive.rest.user.Friendship.Circle.DTO.CircleDTO;

@Service
public class CircleService {
    public static final String DUPLICATE_ENTRY = "NO_DUPLICATE_CIRCLE_NAME";
    public static final String INVALID_COLOR_ERROR = "INVALID COLOR";
    public static final String CIRCLE_NOT_FOUND_ERROR = "CIRCLE NOT FOUND";
    public static final String FRIEND_ALREADY_A_MEMBER_ERROR = "FRIEND IS ALREADY A MEMBER";
    private static final String FRIEND_NOT_A_MEMBER_ERROR = "FRIEND IS NOT A MEMBER";

    @Autowired
    CircleRepo repo;

    // TODO autowire a currentUser object so that I don't have to constant get it
    // via UserService userService
    @Autowired
    private UserService userService;

    @Autowired
    private FriendshipService friendshipService;

    public void createCircle(String name, String color) {
        User user = userService.getCurrentUser();

        boolean validHexColor = Pattern.compile("^#(?:[0-9a-fA-F]{3}){1,2}$").matcher(color).find();
        if (!validHexColor) {
            throw new BadUserInput(INVALID_COLOR_ERROR);
        }

        try {
            repo.save(new Circle(user, name, color));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals(DUPLICATE_ENTRY)) {
                throw new IllogicalRequest("Duplicate circle name.");
            }
        }
    }

    public void deleteCircle(String circleName) {
        Optional<Circle> circle = repo.findByOwnerAndName(userService.getCurrentUser(), circleName);

        if (circle.isPresent()) {
            repo.delete(circle.get());
        } else {
            throw new EntityNotFound(CIRCLE_NOT_FOUND_ERROR);
        }
    }

    public void editCircle(String friendUsername, String circleName, boolean add) {

        User user = userService.getCurrentUser();
        Optional<Circle> optionalCircle = repo.findByOwnerAndName(user, circleName);

        if (!optionalCircle.isPresent()) {
            throw new BadUserInput(CIRCLE_NOT_FOUND_ERROR);
        }

        User friend = userService.findByUsername(friendUsername);

        // users can only add/remove their friends
        friendshipService.validateFriendship(friend);

        Circle circle = optionalCircle.get();
        if (add) {
            if (!circle.members.add(friend))
                throw new IllogicalRequest(FRIEND_ALREADY_A_MEMBER_ERROR);
        } else {
            if (!circle.members.remove(friend))
                throw new IllogicalRequest(FRIEND_NOT_A_MEMBER_ERROR);
        }

        repo.save(circle);

    }

    public List<CircleDTO> getAllCircles() {
        List<Circle> circles = repo.findByOwner(userService.getCurrentUser());
        List<CircleDTO> circleDTOs = new ArrayList<>();
        for (Circle circle : circles) {
            Set<String> members = new HashSet<String>();
            for (User member : circle.getMembers()) {
                members.add(member.getUsername());
            }
            circleDTOs.add(new CircleDTO(circle.getName(), circle.getColor(), members));
        }

        return circleDTOs;
    }

}
