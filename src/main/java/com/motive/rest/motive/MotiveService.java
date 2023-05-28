package com.motive.rest.motive;

import com.motive.rest.Auth.AuthService;
import com.motive.rest.dto.DTO;
import com.motive.rest.dto.DTOFactory;
import com.motive.rest.dto.DTOFactory.DTO_TYPE;
import com.motive.rest.exceptions.EntityNotFound;
import com.motive.rest.exceptions.UnauthorizedRequest;
import com.motive.rest.motive.Invite.Invite;
import com.motive.rest.motive.attendance.Attendance;
import com.motive.rest.motive.attendance.AttendanceRepo;
import com.motive.rest.motive.attendance.dto.StatsDTO;
import com.motive.rest.motive.dto.MotiveBrowseDTO;
import com.motive.rest.motive.dto.MotiveManageDTO;
import com.motive.rest.notification.NotificationService;
import com.motive.rest.user.User;
import com.motive.rest.user.UserService;
import com.motive.rest.user.friendship.FriendshipService;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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

    /**
     * Creates a motive and notifies this users friends
     * 
     * @param title
     * @param description
     * @param start
     * @param hiddenFrom  is the list of friends that shouldn't see this motive
     * @return a manageDTO generated from the created motive
     */
    public MotiveManageDTO createMotive(String title, String description, Date start, Motive.ATTENDANCE_TYPE type,
            String[] specificallyInvited) {
        User user = authService.getAuthUser();

        Motive motive = new Motive(
                user,
                title,
                description,
                start,
                type);

        if (type.equals(Motive.ATTENDANCE_TYPE.SPECIFIC_FRIENDS)) {
            for (String username : specificallyInvited) {
                friendshipService.validateFriendship(username);
                motive.getSpecificallyInvited().add(new Invite(motive, userService.findByUsername(username)));
            }
        }

        repo.save(motive);

        List<User> invitedUsers = getPotentialAttendees(motive);
        for (User invited : invitedUsers) {
            notificationService.notify(invited, user.getUsername() + " has started a new motive", true);
        }

        MotiveManageDTO motiveDto = (MotiveManageDTO) dtoFactory.getDto(motive, DTO_TYPE.MOTIVE_MANAGE);

        return motiveDto;
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

    public Motive getMotive(Long id) {
        Optional<Motive> motive = repo.findById(id);
        if (!motive.isPresent()) {
            throw new EntityNotFound("Could not find motive");
        }
        return motive.get();
    }

    public DTO getMotiveDto(Long id) {
        Optional<Motive> motive = repo.findById(id);
        if (!motive.isPresent()) {
            throw new EntityNotFound("Could not find motive");
        }

        if (motive.get().getOwner().equals(authService.getAuthUser())) {
            return dtoFactory.getDto(motive.get(), DTOFactory.DTO_TYPE.MOTIVE_MANAGE);
        }
        
        if (!canAttend(motive.get())) {
            return dtoFactory.getDto(repo.findById(id), DTOFactory.DTO_TYPE.MOTIVE_BROWSE);
        } else {
            throw new UnauthorizedRequest("Forbidden from this action.");
        }

    }

    @SuppressWarnings("unchecked")
    public List<MotiveBrowseDTO> browseMotives() {
        User user = authService.getAuthUser();
        // don't include the motives the user owns in the list
        List<Motive> motives = getActiveMotives().stream().filter(m -> !m.getOwner().equals(user))
                .collect(Collectors.toList());

        return (List<MotiveBrowseDTO>) dtoFactory.getDto(motives, DTO_TYPE.MOTIVE_BROWSE);
    }

    @SuppressWarnings("unchecked")
    public List<MotiveBrowseDTO> getAttending() {
        User user = authService.getAuthUser();

        List<Motive> motives = getActiveMotives().stream()
                .filter(m -> attendanceRepo.findByMotiveAndUser(m, user).isPresent())
                .collect(Collectors.toList());

        return (List<MotiveBrowseDTO>) dtoFactory.getDto(motives, DTO_TYPE.MOTIVE_BROWSE);
    }

    @SuppressWarnings("unchecked")
    public List<MotiveManageDTO> manageMotives() {
        return (List<MotiveManageDTO>) dtoFactory.getDto(repo.findByOwner(authService.getAuthUser()),
                DTO_TYPE.MOTIVE_MANAGE);
    }

    /**
     * 1. Gets all motives this user can potentially attend
     * 2. First is all the motives the user owns
     * 3. Sorts remaining motives based of friends attending
     * TODO 3. order motives with no friends attending based distance, uni...
     */
    private List<Motive> getActiveMotives() {

        List<Motive> potentialMotive = new ArrayList<>();
        List<Motive> AllMotives = repo.findByFinished(false);
        for (Motive motive : AllMotives) {
            if (canAttend(motive)) {
                potentialMotive.add(motive);
            }
        }

        return potentialMotive;
    }

    /**
     * 
     * @param motive
     * @return whether or not this user can attend this motive
     */
    private boolean canAttend(Motive motive) {
        if (motive.getOwner().equals(authService.getAuthUser())
                || motive.getAttendanceType().equals(Motive.ATTENDANCE_TYPE.EVERYONE)) {
            return true;
        }

        if (motive.getAttendanceType().equals(Motive.ATTENDANCE_TYPE.SPECIFIC_FRIENDS)) {
            return motive.getSpecificallyInvited().stream().map(e -> e.getUser()).collect(Collectors.toList())
                    .contains(authService.getAuthUser());
        }

        if (motive.getAttendanceType().equals(Motive.ATTENDANCE_TYPE.FRIENDS)) {
            return friendshipService.isFriends(motive.getOwner());
        }

        return false;
    }

    public StatsDTO getStats() {
        return new StatsDTO(getAttending().size() + manageMotives().size(), 0, getActiveMotives().size());
    }

    public void validateOwner(Motive motive) {
        if (!motive.getOwner().equals(authService.getAuthUser())) {
            throw new UnauthorizedRequest("Forbidden from this action.");
        }
        ;
    }

}
