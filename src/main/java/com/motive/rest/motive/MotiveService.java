package com.motive.rest.motive;

import com.motive.rest.dto.DTOFactory;
import com.motive.rest.dto.DTOFactory.DTO_TYPE;
import com.motive.rest.exceptions.EntityNotFound;
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

    /**
     * Creates a motive and notifies this users friends
     * 
     * @param title
     * @param description
     * @param start
     * @param hiddenFrom  is the list of friends that shouldn't see this motive
     * @return a manageDTO generated from the created motive
     */
    public MotiveManageDTO createMotive(String title, String description, Date start, String[] hiddenFrom) {
        User user = userService.getCurrentUser();

        Motive motive = new Motive(
                user,
                title,
                description,
                start);

        for (String username : hiddenFrom) {
            friendshipService.validateFriendship(username);
            motive.getHiddenFrom().add(userService.findByUsername(username));
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
        // returns friends - (hiddenFrom + pending Attendance + confirmed attendance)
        List<User> allFriends = new ArrayList<>();
        for (User friend : friendshipService.getApprovedFriendshipsUserObjects()) {
            allFriends.add(friend);
        }

        // remove all the friends that already have an attendance status
        for (Attendance attendance : motive.getAttendance()) {
            allFriends.remove(attendance.getUser());
        }

        // remove all the users who the motive is hidden from
        allFriends.removeAll(motive.getHiddenFrom());

        return allFriends;
    }

    public Motive getMotive(Long id) {
        Optional<Motive> motive = repo.findById(id);
        if (!motive.isPresent()) {
            throw new EntityNotFound("Could not find motive using id");
        }

        return motive.get();
    }

    @SuppressWarnings("unchecked")
    public List<MotiveBrowseDTO> browseMotives() {
        return (List<MotiveBrowseDTO>) dtoFactory.getDto(getActiveMotives(), DTO_TYPE.MOTIVE_BROWSE);
    }

    @SuppressWarnings("unchecked")
    public List<MotiveBrowseDTO> getAttending() {
        User user = userService.getCurrentUser();

        List<Motive> motives = new ArrayList<Motive>();
        for (Motive motive : getActiveMotives()) {
            if (motive.getOwner().equals(user)) {
                motives.add(motive);
                continue;
            }

            if (attendanceRepo.findByMotiveAndUser(motive, user).isPresent()) {
                motives.add(motive);
                continue;
            }
        }
        return (List<MotiveBrowseDTO>) dtoFactory.getDto(motives, DTO_TYPE.MOTIVE_BROWSE);
    }

    @SuppressWarnings("unchecked")
    public List<MotiveManageDTO> manageMotives() {
        return (List<MotiveManageDTO>) dtoFactory.getDto(repo.findByOwner(userService.getCurrentUser()),
                DTO_TYPE.MOTIVE_MANAGE);
    }

    private List<Motive> getActiveMotives() {
        List<Motive> motives = new ArrayList<>();
        for (User friend : friendshipService.getApprovedFriendshipsUserObjects()) {
            motives.addAll(repo.findByOwnerAndFinished(friend, false));
        }
        return motives;
    }

    public StatsDTO getStats() {
        return new StatsDTO(getAttending().size(),0,getActiveMotives().size());
    }

}
