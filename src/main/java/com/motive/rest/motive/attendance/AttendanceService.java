package com.motive.rest.motive.attendance;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.motive.rest.dto.DTOFactory;
import com.motive.rest.dto.DTOFactory.DTO_TYPE;
import com.motive.rest.exceptions.EntityNotFound;
import com.motive.rest.exceptions.IllogicalRequest;
import com.motive.rest.exceptions.UnauthorizedRequest;
import com.motive.rest.motive.Motive;
import com.motive.rest.motive.MotiveService;
import com.motive.rest.motive.attendance.Attendance.ATTENDANCE_STATUS;
import com.motive.rest.motive.attendance.dto.AttendanceDTO;
import com.motive.rest.motive.attendance.dto.AttendanceResponseDto;
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
    FriendshipService friendshipService;

    public void requestAttendance(Long motiveId, boolean anonymous) {
        User user = userService.getCurrentUser();
        Motive motive = motiveService.getMotive(motiveId);

        // Ensure this user is friends with motive owner before attempting
        friendshipService.validateFriendship(motive.getOwner());

        if (hasAttendance(user, motive)) {
            throw new IllogicalRequest("Attendance already registered");
        }

        Attendance attendance = new Attendance(user, motive, anonymous);
        repo.save(attendance);
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
        if (!attendance.getMotive().getOwner().equals(userService.getCurrentUser())) {
            throw new UnauthorizedRequest("You cannot to attendance request as you not the motive owner");
        }

        if (attendance.getStatus().equals(ATTENDANCE_STATUS.CONFIRMED)) {
            throw new IllogicalRequest("Attendance already confirmed");
        }

        if (accept) {
            attendance.setStatus(ATTENDANCE_STATUS.CONFIRMED);
            repo.save(attendance);
        } else {
            repo.delete(attendance);
        }
    }

    public List<AttendanceDTO> getPendingAttendance(Long motive) {
        return getPendingAttendance(motiveService.getMotive(motive));
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

    public AttendanceDTO motiveAttendance(Long motiveId) {
        User user = userService.getCurrentUser();
        Optional<Attendance> att = repo.findByMotiveAndUser(motiveService.getMotive(motiveId), user);
        if (att.isPresent()) {
            return (AttendanceDTO) dtoFactory.getDto(
                    att.get(),
                    DTO_TYPE.ATTENDANCE);
        }
        return null;
    }

}
