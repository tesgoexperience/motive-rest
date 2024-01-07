package com.motive.rest.motive.attendance;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.motive.rest.Auth.AuthService;
import com.motive.rest.chat.Chat;
import com.motive.rest.chat.ChatRepo;
import com.motive.rest.dto.DTOFactory;
import com.motive.rest.dto.DTOFactory.DTO_TYPE;
import com.motive.rest.exceptions.EntityNotFound;
import com.motive.rest.exceptions.IllogicalRequest;
import com.motive.rest.exceptions.UnauthorizedRequest;
import com.motive.rest.motive.Motive;
import com.motive.rest.motive.MotiveService;
import com.motive.rest.motive.Invite.Invite;
import com.motive.rest.motive.attendance.Attendance.ATTENDANCE_STATUS;
import com.motive.rest.motive.attendance.dto.AttendanceDTO;
import com.motive.rest.motive.attendance.dto.AttendanceResponseDto;
import com.motive.rest.notification.NotificationService;
import com.motive.rest.user.User;
import com.motive.rest.user.UserService;
import com.motive.rest.user.friendship.FriendshipService;

@Service
public class AttendanceService {

    @Autowired
    DTOFactory dtoFactory;
    @Autowired
    AttendanceRepo repo;
    @Autowired
    MotiveService motiveService;
    @Autowired
    UserService userService;
    @Autowired
    AuthService authService;
    @Autowired
    FriendshipService friendshipService;
    @Autowired
    ChatRepo chatRepo;
    @Autowired
    private NotificationService notificationService;

    public void cancelMyAttendance(UUID motiveId) {
        User user = authService.getAuthUser();
        Motive motive = motiveService.getMotive(motiveId);

        if (!hasAttendance(user, motive)) {
            throw new IllogicalRequest("You are not attending this motive.");
        }

        repo.delete(findByMotiveAndUser(motiveId).get());
        notificationService.notify(motive.getTitle(), user.getUsername()+" is no longer attending", motive.getOwner().getAuthDetails().getNotificationToken());
    }

    public void removeAttendee(AttendanceResponseDto response) {
        User user = userService.findByUsername(response.getAttendeeUsername());
        Motive motive = motiveService.getMotive(response.getMotiveId());

        // throws error if this user is not the owner
        motiveService.validateOwner(motive);
        
        if (!hasAttendance(user, motive)) {
            throw new IllogicalRequest("User is not attending this motive.");
        }

        repo.delete(findByMotiveAndUser(response.getMotiveId(),user).get());
        notificationService.notify(motive.getTitle(), "You have been removed from the motive", user.getAuthDetails().getNotificationToken());
    }

    public void requestAttendance(UUID motiveId, boolean anonymous) {
        User user = authService.getAuthUser();
        Motive motive = motiveService.getMotive(motiveId);

        if (user.equals(motive.getOwner())) {
            throw new IllogicalRequest("You cannot request your own event.");
        }

        if (!motive.getAttendanceType().equals(Motive.ATTENDANCE_TYPE.EVERYONE)) {
            // Ensure this user is friends with motive owner before attempting
            friendshipService.validateFriendship(motive.getOwner());

            if (motive.getAttendanceType().equals(Motive.ATTENDANCE_TYPE.SPECIFIC_FRIENDS)) {
                boolean isInvited = false;
                for (Invite invite : motive.getSpecificallyInvited()) {
                    if (invite.getUser().equals(user)) {
                        isInvited = true;
                        break;
                    }
                }

                if (!isInvited) {
                    throw new UnauthorizedRequest("You must be invited to this event.");
                }
            }
        }

        if (hasAttendance(user, motive)) {
            throw new IllogicalRequest("Attendance already registered");
        }

        Attendance attendance = new Attendance(user, motive, anonymous);
        repo.save(attendance);
        notificationService.notify(motive.getTitle(), "New attendance request", motive.getOwner().getAuthDetails().getNotificationToken());
    }

    public void respondToAttendanceRequest(AttendanceResponseDto response, boolean accept) {
        User friend = userService.findByUsername(response.getAttendeeUsername());

        friendshipService.validateFriendship(friend);

        Optional<Attendance> optionalAttendance = repo
                .findByMotiveAndUser(motiveService.getMotive(response.getMotiveId()), friend);

        if (!optionalAttendance.isPresent()) {
            throw new EntityNotFound("Attendance not found");
        }

        Attendance attendance = optionalAttendance.get();

        // validate user is authorized to make this request
        if (!attendance.getMotive().getOwner().equals(authService.getAuthUser())) {
            throw new UnauthorizedRequest("You cannot to attendance request as you not the motive owner");
        }

        if (attendance.getStatus().equals(ATTENDANCE_STATUS.CONFIRMED)) {
            throw new IllogicalRequest("Attendance already confirmed");
        }

        if (accept) {
            attendance.setStatus(ATTENDANCE_STATUS.CONFIRMED);
            repo.save(attendance);
            // add new member to chat 
            Chat chat = attendance.getMotive().getChat();
            chat.getMembers().add(attendance.getUser());
            chatRepo.save(chat);
            notificationService.notify(attendance.getMotive().getTitle(), "Request accepted!", attendance.getUser().getAuthDetails().getNotificationToken());
        } else {
            repo.delete(attendance);
        }
    }

    public List<AttendanceDTO> getPendingAttendance(UUID motiveId) {
        Motive motive = motiveService.getMotive(motiveId);
        motiveService.validateOwner(motive);
        return getPendingAttendance(motive);
    }

    @SuppressWarnings("unchecked")
    public List<AttendanceDTO> getPendingAttendance(Motive motive) {
        return (List<AttendanceDTO>) dtoFactory.getDto(
                repo.findByMotiveAndStatus(motive, ATTENDANCE_STATUS.REQUESTED),
                DTO_TYPE.ATTENDANCE);
    }

    private boolean hasAttendance(User user, Motive motive) {
        return repo.findByMotiveAndUser(motive, user).isPresent();
    }

    public Optional<Attendance> findByMotiveAndUser(UUID motiveId) {
        return findByMotiveAndUser(motiveId, authService.getAuthUser());
    }
    public Optional<Attendance> findByMotiveAndUser(UUID motiveId, User user) {
        return repo.findByMotiveAndUser(motiveService.getMotive(motiveId), user);
    }
    public AttendanceDTO motiveAttendance(UUID motiveId) {
        Optional<Attendance> att = findByMotiveAndUser(motiveId);
        if (att.isPresent()) {
            return (AttendanceDTO) dtoFactory.getDto(
                    att.get(),
                    DTO_TYPE.ATTENDANCE);
        }
        return null;
    }

}
