package com.motive.rest.motive.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.motive.rest.dto.DTO;
import com.motive.rest.motive.attendance.Attendance;
import com.motive.rest.motive.attendance.Attendance.ATTENDANCE_STATUS;
import com.motive.rest.user.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.AllArgsConstructor;

@NoArgsConstructor
public class MotiveManageDTO implements DTO {


    @Getter @Setter private Long id;
    @Getter @Setter private String title;
    @Getter @Setter private String description;
    @Getter @Setter private Date start;
    @Getter @Setter private boolean finished;

    @Setter  private User owner;
    @Setter  private List<User> hiddenFrom;
    @Setter  private List<Attendance> attendance;

    public String getOwnerUsername(){
        return this.owner.getUsername();
    }

    public List<String> getRequests(){
        List<String> requesterUsernames = new ArrayList<>();
        for (Attendance att : attendance) {
            if (att.getStatus().equals(ATTENDANCE_STATUS.REQUESTED)) {
                requesterUsernames.add(att.getUser().getUsername());
            }
        }
        return requesterUsernames;
    }


    public List<String> getConfirmedAttendanceAnonymous(){
        List<String> attending = new ArrayList<>();
        for (Attendance att : attendance) {
            if (att.getStatus().equals(ATTENDANCE_STATUS.CONFIRMED) && att.isAnonymous()) {
                attending.add(att.getUser().getUsername());
            }
        }
        return attending;
    }


    public List<String> getConfirmedAttendance(){
        List<String> attending = new ArrayList<>();
        for (Attendance att : attendance) {
            if (att.getStatus().equals(ATTENDANCE_STATUS.CONFIRMED) && !att.isAnonymous()) {
                attending.add(att.getUser().getUsername());
            }
        }
        return attending;
    }


    public List<String> getHiddenFrom(){
        List<String> users = new ArrayList<>();

        for (User user : hiddenFrom) {
            users.add(user.getUsername());
        }

        return users;

    }
}
