package com.motive.rest.motive.attendance;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

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
import com.motive.rest.motive.attendance.dto.ResponseDto;
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
        if (!friendshipService.isFriends(motive.getOwner())) {
            throw new IllogicalRequest("User is not friends with motive owner");
        }

        if (hasAttendance(user, motive)) {
            throw new IllogicalRequest("Attendance already registered");
        }

        Attendance attendance = new Attendance(user, motive, anonymous);
        repo.save(attendance);
    }

    public void respondToAttendanceRequest(ResponseDto response) {
        Optional<Attendance> optionalAttendance = repo.findById(response.getAttendance());

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

        if (response.isAccept()) {
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
}
