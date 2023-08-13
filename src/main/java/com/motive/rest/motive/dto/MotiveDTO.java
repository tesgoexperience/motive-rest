package com.motive.rest.motive.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.motive.rest.dto.DTO;
import com.motive.rest.motive.Motive;
import com.motive.rest.motive.attendance.Attendance;
import com.motive.rest.motive.attendance.Attendance.ATTENDANCE_STATUS;
import com.motive.rest.user.User;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MotiveDTO implements DTO{
    @Getter private UUID id;
    @Getter private String title;
    @Getter private String description;
    @Getter private Date start;

    private User owner;
    private List<Attendance> attendance;
    @Getter private ManagementDetails managementDetails = null;
    
    public MotiveDTO(Motive motive, boolean isOwner) {
        if (isOwner) {
            this.managementDetails = new ManagementDetails(motive.getAttendance(), motive.getSpecificallyInvited());
        }
        this.id = motive.getId();
        this.start = motive.getStart();
        this.title = motive.getTitle();
        this.description = motive.getDescription();
        this.owner = motive.getOwner();
        this.attendance = motive.getAttendance();
    }

    public String getOwnerUsername(){
        return this.owner.getUsername();
    }

    public List<String> getAttendance(){
        List<String> attending = new ArrayList<>();
        for (Attendance att : attendance) {
            if (att.getStatus().equals(ATTENDANCE_STATUS.CONFIRMED) && !att.isAnonymous()) {
                attending.add(att.getUser().getUsername());
            }
        }
        return attending;
    }
}
