package com.motive.rest.motive;

import com.motive.rest.Auth.AuthService;
import com.motive.rest.chat.Chat;
import com.motive.rest.chat.ChatRepo;
import com.motive.rest.dto.DTOFactory;
import com.motive.rest.exceptions.BadUserInput;
import com.motive.rest.exceptions.EntityNotFound;
import com.motive.rest.exceptions.UnauthorizedRequest;
import com.motive.rest.motive.Invite.Invite;
import com.motive.rest.motive.attendance.Attendance;
import com.motive.rest.motive.attendance.AttendanceRepo;
import com.motive.rest.motive.attendance.dto.StatsDTO;
import com.motive.rest.motive.dto.MotiveDTO;
import com.motive.rest.notification.NotificationService;
import com.motive.rest.user.User;
import com.motive.rest.user.UserService;
import com.motive.rest.user.friendship.FriendshipService;

import io.github.jav.exposerversdk.PushClientException;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

@Service
public class MotiveService {

    @Autowired
    DTOFactory dtoFactory;

    @Autowired
    MotiveRepo repo;

    @Autowired
    private AttendanceRepo attendanceRepo;

    @Autowired
    private UserService userService;

    @Autowired
    private FriendshipService friendshipService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    AuthService authService;

    @Autowired
    ChatRepo chatRepo;
    /**
     * Creates a motive and notifies this users friends
     * 
     * @param title
     * @param description
     * @param start
     * @param hiddenFrom  is the list of friends that shouldn't see this motive
     * @return a manageDTO generated from the created motive
     */
    public MotiveDTO createMotive(String title, String description, Date start, Date end, Motive.ATTENDANCE_TYPE type,
            String[] specificallyInvited) {

        // check motive date in the future
        if (start.before(new Date())) {
            throw new BadUserInput("Start date cannot be in the past.");
        }
        if (end.before(start)) {
            throw new BadUserInput("end date cannot before start.");
        }

        User user = authService.getAuthUser();

        Motive motive = new Motive(
                user,
                title,
                description,
                start,
                end,
                type);
        
        // motive.setChat(new Chat(new ArrayList<>(), motive));
        
        if (type.equals(Motive.ATTENDANCE_TYPE.SPECIFIC_FRIENDS)) {
            for (String username : specificallyInvited) {
                friendshipService.validateFriendship(username);
                motive.getSpecificallyInvited().add(new Invite(motive, userService.findByUsername(username)));
            }
        }

        repo.save(motive);
        chatRepo.save(new Chat(user,motive));

        // notify all potential attendees
        List<User> invitedUsers = getPotentialAttendees(motive);
        for (User invited : invitedUsers) {
            notificationService.notify("New motive from " + user.getUsername(), motive.getTitle(),
                    invited.getAuthDetails().getNotificationToken());
        }

        return convertMotiveToDTO(motive);
    }

    /**
     * Get the list of friends who are not in the hidden from list, have no
     * rejected, pending or confirmed attendances
     * 
     * @param motive
     * @return the remaining friends after subtraction
     */
    public List<User> getPotentialAttendees(Motive motive) {
        List<User> allFriends = new ArrayList<>();

        if (motive.getAttendanceType().equals(Motive.ATTENDANCE_TYPE.SPECIFIC_FRIENDS)) {
            allFriends.addAll(
                    motive.getSpecificallyInvited().stream().map(e -> e.getUser()).collect(Collectors.toList()));
        } else if (motive.getAttendanceType().equals(Motive.ATTENDANCE_TYPE.FRIENDS)) {
            for (User friend : friendshipService.getFriends()) {
                allFriends.add(friend);
            }
        }

        // remove all the friends that already have an attendance status
        for (Attendance attendance : motive.getAttendance()) {
            allFriends.remove(attendance.getUser());
        }

        return allFriends;
    }

    public Motive getMotive(UUID id) {
        Optional<Motive> motive = repo.findById(id);
        if (!motive.isPresent()) {
            throw new EntityNotFound("Could not find motive");
        }
        return motive.get();
    }

    public MotiveDTO getMotiveDto(UUID id) {
        Optional<Motive> motive = repo.findById(id);
        if (!motive.isPresent()) {
            throw new EntityNotFound("Could not find motive");
        }

        if (motive.get().getOwner().equals(authService.getAuthUser())) {
            return convertMotiveToDTO(motive.get());
        }

        if (canAttend(motive.get())) {
            return convertMotiveToDTO(motive.get());
        } else {
            throw new UnauthorizedRequest("Forbidden from this action.");
        }

    }

    public List<MotiveDTO> browseMotives() {
        User user = authService.getAuthUser();
        // don't include the motives the user owns in the list
        List<Motive> motives = getActiveMotives().stream().filter(m -> !m.getOwner().equals(user))
                .collect(Collectors.toList());
        return convertMotiveToDTO(motives);
    }

    public List<MotiveDTO> getAttending() {
        User user = authService.getAuthUser();

        List<Motive> motives = getActiveMotives().stream()
                .filter(m -> attendanceRepo.findByMotiveAndUser(m, user).isPresent())
                .collect(Collectors.toList());

        return convertMotiveToDTO(motives);
    }

    public List<MotiveDTO> manageMotives() {
        List<Motive> motives = repo.findByOngoingWithOwner(authService.getAuthUser().getId());
        return convertMotiveToDTO(motives);
    }

    /**
     * 1. Gets all motives this user can potentially attend
     * 2. First is all the motives the user owns
     * 3. Sorts remaining motives based of friends attending
     * TODO 3. order motives with no friends attending based distance, uni...
     */
    private List<Motive> getActiveMotives() {

        List<Motive> AllMotives = repo.findByOngoing();
        AllMotives.removeIf(motive -> !canAttend(motive));
        return AllMotives;
    }

    /**
     * get all finished motives this user has finished or managed
     * 
     */
    public List<MotiveDTO> getPastMotives() {
        User user = authService.getAuthUser();
        List<Motive> pastMotives = repo.findByFinishedOrCancelled();
        List<Motive> usersPastMotives = new ArrayList<>();

        for (Motive motive : pastMotives) { // todo move this to an sql query to make scalable
            if (motive.getOwner().equals(user) || attendanceRepo.findByMotiveAndUser(motive, user).isPresent()) {
                usersPastMotives.add(motive);
            }
        }

        return convertMotiveToDTO(usersPastMotives);
    }

    /**
     * 
     * @param motive
     * @return whether or not this user can attend this motive
     */
    private boolean canAttend(Motive motive) {
        if (motive.getOwner().equals(authService.getAuthUser())) {
            return true;
        }

        if (motive.getAttendanceType().equals(Motive.ATTENDANCE_TYPE.SPECIFIC_FRIENDS)) {
            return motive.getSpecificallyInvited().stream().map(e -> e.getUser()).collect(Collectors.toList())
                    .contains(authService.getAuthUser());
        }

        if (motive.getAttendanceType().equals(Motive.ATTENDANCE_TYPE.FRIENDS) || motive.getAttendanceType().equals(Motive.ATTENDANCE_TYPE.EVERYONE)) {
            return friendshipService.isFriends(motive.getOwner());
        }

        return false;
    }

    public StatsDTO getStats() {
        return new StatsDTO(getAttending().size() + manageMotives().size(), getPastMotives().size(),
                getActiveMotives().size());
    }

    public void validateOwner(Motive motive) {
        if (!motive.getOwner().equals(authService.getAuthUser())) {
            throw new UnauthorizedRequest("Forbidden from this action.");
        }
        ;
    }

    public List<MotiveDTO> convertMotiveToDTO(List<Motive> motives) {
        List<MotiveDTO> dtos = new ArrayList<>();
        for (Motive motive : motives) {
            dtos.add(convertMotiveToDTO(motive));
        }
        return dtos;
    }

    public boolean isOngoing(Motive motive) {
        return !(motive.isCancelled() || motive.getEnd().before(new Date()));
    }

     public MotiveDTO convertMotiveToDTO(Motive motive) {
        return new MotiveDTO(motive, isOngoing(motive) && motive.getOwner().equals(authService.getAuthUser()));
    }
}
